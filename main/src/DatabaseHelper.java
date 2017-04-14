import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DatabaseHelper {

    // general database info
    private static final String DATABASE_NAME = "beerbaron.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_DRIVER = "jdbc:sqlite:";

    // schema details for Product table
    private static final String TABLE_PRODUCT = "Product";
    private static final String PRODUCT_COLUMN_ID = "p_id";
    private static final String PRODUCT_COLUMN_NAME = "p_name";
    private static final String PRODUCT_COLUMN_CURRENTPRICE = "p_currentprice";
    private static final String PRODUCT_COLUMN_AVGPRICE = "p_avgprice";

    // schema details for PriceCheck table
    private static final String TABLE_PRICECHECK = "PriceCheck";
    private static final String PRICECHECK_COLUMN_DATETIME = "pc_datetime";
    private static final String PRICECHECK_COLUMN_PRICE = "pc_price";

    // queries for table creation
    private static final String DATABASE_CREATE_TABLE_PRODUCT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "(" +
                PRODUCT_COLUMN_ID             + " TEXT NOT NULL, " +
                PRODUCT_COLUMN_NAME           + " TEXT, " +
                PRODUCT_COLUMN_CURRENTPRICE   + " REAL, " +
                PRODUCT_COLUMN_AVGPRICE       + " REAL, " +
                "CONSTRAINT Product_p_id_pk PRIMARY KEY (" + PRODUCT_COLUMN_ID + ")" +
            ");";

    private static final String DATABASE_CREATE_TABLE_PRICECHECK =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PRICECHECK + "(" +
                PRODUCT_COLUMN_ID             + " TEXT NOT NULL, " +
                PRICECHECK_COLUMN_DATETIME    + " INTEGER NOT NULL, " +
                PRICECHECK_COLUMN_PRICE       + " REAL, " +
                "CONSTRAINT PriceCheck_p_pc_id_pk PRIMARY KEY (" +
                    PRODUCT_COLUMN_ID + ", " + PRICECHECK_COLUMN_DATETIME +
                "), CONSTRAINT PriceCheck_p_id FOREIGN KEY (" +
                    PRODUCT_COLUMN_ID + ") REFERENCES " + TABLE_PRODUCT + "(" + PRODUCT_COLUMN_ID + ")" +
            ");";

    // queries for trigger creation
    private static final String DATABASE_CREATE_TRIGGER_AVGPRICE =
            "CREATE TRIGGER 'update_product_avgprice' " +
                "AFTER INSERT ON '" + TABLE_PRICECHECK + "' " +
                "FOR EACH ROW " +
            "BEGIN " +
                "UPDATE '" + TABLE_PRODUCT + "' " +
                "SET " + PRODUCT_COLUMN_AVGPRICE + " = (" +
                    "SELECT AVG(" + PRICECHECK_COLUMN_PRICE + ") " +
                    "FROM (" +
                        "SELECT DISTINCT " + TABLE_PRICECHECK + "." + PRICECHECK_COLUMN_PRICE + " " +
                        "FROM " + TABLE_PRICECHECK + " " +
                        "WHERE " + TABLE_PRICECHECK + "." + PRODUCT_COLUMN_ID + " = " + TABLE_PRODUCT + "." + PRODUCT_COLUMN_ID + " " +
                    ")" +
                ");" +
            "END;";

    // returns a Connection object to interact with the database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_DRIVER + DATABASE_NAME);
    }

    // checks if there is an existing database and if not sets one up
    public void setUpDatabase() {
        if(!Files.exists(Paths.get(DATABASE_NAME))) {
            // there isn't an existing database
            createDatabaseStructure();  // create the tables and triggers
            addDefaultProducts();       // add default products to the database
            addPriceChecks();           // perform an initial price check on all products
        }
    }

    // creates the tables and triggers in the database
    private void createDatabaseStructure() {
        try(Connection connection = this.getConnection();
            Statement statement = connection.createStatement()) {

            statement.execute(DATABASE_CREATE_TABLE_PRODUCT);
            statement.execute(DATABASE_CREATE_TABLE_PRICECHECK);
            statement.execute(DATABASE_CREATE_TRIGGER_AVGPRICE);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // adds a new product to the Product table
    public void addProduct(String productId, String productName) {
        try(Connection connection = this.getConnection();
            Statement statement = connection.createStatement()) {

            String sql = "INSERT INTO Product (" + PRODUCT_COLUMN_ID + ", " + PRODUCT_COLUMN_NAME + ") " +
                         "VALUES ('" + productId + "', '" + productName + "');";
            statement.execute(sql);
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // adds the list of default products to the database from file
    private void addDefaultProducts() {
        Path filePath = Paths.get("products.txt");

        try(BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
            Connection connection = this.getConnection();
            Statement statement = connection.createStatement()) {

            String line = null;

            while((line = br.readLine()) != null) {
                // the file is formatted with the product id on the first line and the product name on the next
                String productId = line;
                String productName = br.readLine();

                // add a new product to the database using the data we read in
                addProduct(productId, productName);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // adds a new price check for the specified product
    public void addPriceCheck(String productId, String productPrice) {
        try(Connection connection = this.getConnection();
            Statement statement = connection.createStatement()) {

            // add the parsed data to the PriceCheck table
            statement.execute(getSQLPriceCheckInsert(productId, productPrice));

            // update p_currentprice in the Product table with this price now whilst we have most of the data
            // we need instead of as a trigger which would require more queries to the database
            statement.execute(getSQLProductUpdateCurrentPrice(productId, productPrice));
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // adds a new price check for each product in the database
    public void addPriceChecks() {
        String productId = "";
        String productPrice = "";
        Statement innerStatement = null;

        try(Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT " + PRODUCT_COLUMN_ID + " FROM " + TABLE_PRODUCT)) {

            // cycle through the all the product ids in the ResultSet
            while (rs.next()) {
                productId = rs.getString(PRODUCT_COLUMN_ID);
                innerStatement = connection.createStatement();

                // parse the product price using JSoup
                ProductParser parser = new ProductParser();
                productPrice = parser.parseProductPrice(parser.parseProductPage(productId));

                // add the parsed data to the PriceCheck table
                innerStatement.execute(getSQLPriceCheckInsert(productId, productPrice));

                // update p_currentprice in the Product table with this price now whilst we have most of the data
                // we need instead of as a trigger which would require more queries to the database
                innerStatement.execute(getSQLProductUpdateCurrentPrice(productId, productPrice));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // returns all the records in the Product table as an ObservableList of Product objects
    public ObservableList<Product> getAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try(Connection connection = this.getConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_PRODUCT)) {

            // the query returned results so cycle through the ResultSet, create new Product
            // objects and add them to the list to return
            while(rs.next()) {
                String productId = rs.getString(PRODUCT_COLUMN_ID);
                String productName = rs.getString(PRODUCT_COLUMN_NAME);
                BigDecimal productCurrentPrice = rs.getBigDecimal((PRODUCT_COLUMN_CURRENTPRICE));
                BigDecimal productAvgPrice = rs.getBigDecimal(PRODUCT_COLUMN_AVGPRICE);

                products.add(new Product(productId, productName, productCurrentPrice, productAvgPrice));
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    // returns an sql query String to perform an insert on the PriceCheck table for the given data
    public String getSQLPriceCheckInsert(String productId, String productPrice) {
        return "INSERT INTO " + TABLE_PRICECHECK + " (" +
                   PRODUCT_COLUMN_ID + ", " + PRICECHECK_COLUMN_DATETIME + ", " + PRICECHECK_COLUMN_PRICE +
               ") " + "VALUES ('" +
                    productId + "', strftime('%s', 'now'), '" + productPrice + "'" +
               ");";
    }

    // returns an sql query String to perform an update on the p_currentprice column in the Product
    // table for the given data
    public String getSQLProductUpdateCurrentPrice(String productId, String productPrice) {
        return "UPDATE " + TABLE_PRODUCT +
               " SET " + PRODUCT_COLUMN_CURRENTPRICE + " = '" + productPrice + "' " +
               " WHERE " + PRODUCT_COLUMN_ID + " = " + "'" + productId + "'";
    }
}

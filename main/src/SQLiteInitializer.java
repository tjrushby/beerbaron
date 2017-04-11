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

public class SQLiteInitializer {
    Connection connection;
    ResultSet resultSet;
    Statement statement;

    private static final String DATABASE_NAME = "beerbaron.db";
    private static final int DATABASE_VERSION = 1;

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
    private static final String DATABASE_CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PRODUCT + "("
                + PRODUCT_COLUMN_ID             + " TEXT NOT NULL, "
                + PRODUCT_COLUMN_NAME           + " TEXT, "
                + PRODUCT_COLUMN_CURRENTPRICE   + " REAL, "
                + PRODUCT_COLUMN_AVGPRICE       + " REAL, "
                + "CONSTRAINT Product_p_id_pk PRIMARY KEY (" + PRODUCT_COLUMN_ID + ")"
            + ");";

    private static final String DATABASE_CREATE_TABLE_PRICECHECK = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PRICECHECK + "("
                + PRODUCT_COLUMN_ID             + " TEXT NOT NULL, "
                + PRICECHECK_COLUMN_DATETIME    + " INTEGER NOT NULL, "
                + PRICECHECK_COLUMN_PRICE       + " REAL, "
                + "CONSTRAINT PriceCheck_p_pc_id_pk PRIMARY KEY ("
                    + PRODUCT_COLUMN_ID + ", " + PRICECHECK_COLUMN_DATETIME
                + "), CONSTRAINT PriceCheck_p_id FOREIGN KEY ("
                    + PRODUCT_COLUMN_ID + ") REFERENCES " + TABLE_PRODUCT + "(" + PRODUCT_COLUMN_ID + ")"
            + ");";

    // queries for trigger creation
    private static final String DATABASE_CREATE_TRIGGER_AVGPRICE =
            "CREATE TRIGGER 'update_product_avgprice' "
                + "AFTER INSERT ON '" + TABLE_PRICECHECK + "' "
                + "FOR EACH ROW "
            + "BEGIN "
                + "UPDATE '" + TABLE_PRODUCT + "' "
                + "SET " + PRODUCT_COLUMN_AVGPRICE + " = ("
                    + "SELECT AVG(" + TABLE_PRICECHECK + "." + PRICECHECK_COLUMN_PRICE + ") "
                    + "FROM '" + TABLE_PRICECHECK + "' "
                    + "WHERE " + TABLE_PRICECHECK + "." + PRODUCT_COLUMN_ID + " = " + TABLE_PRODUCT + "." + PRODUCT_COLUMN_ID + " "
                + ");"
            + "END;";

    // method to close connections
    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch(Exception e) {
            // do nothing because if connection is null then it has been closed
        }
    }

    // method to close result sets
    private void closeResultSet(ResultSet resultSet) {
        try {
            resultSet.close();
        } catch(Exception e) {
            // do nothing because if resultset is null then it has been closed
        }
    }

    //  method to close statements
    private void closeStatement(Statement statement) {
        try {
            statement.close();
        } catch(Exception e) {
            // do nothing because if statement is null then it has been closed
        }
    }

    // checks if there is an existing database
    public void checkForExistingDatabase() {
        if(!Files.exists(Paths.get(DATABASE_NAME))) {
            // there isn't an existing database
            createDatabaseStructure();  // create the tables and triggers
            addDefaultProducts();       // add default products to the database
            addPriceCheck();            // perform an initial price check
        }
    }

    // creates the tables and triggers in the database
    private void createDatabaseStructure() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            statement = connection.createStatement();

            statement.execute(DATABASE_CREATE_TABLE_PRODUCT);
            statement.execute(DATABASE_CREATE_TABLE_PRICECHECK);
            statement.execute(DATABASE_CREATE_TRIGGER_AVGPRICE);
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    // adds the list of default products to the database from file
    private void addDefaultProducts() {
        Path filePath = Paths.get("products.txt");

        try(BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            statement = connection.createStatement();
            String line = null;

            while((line = br.readLine()) != null) {
                String p_id = line;
                String p_name = br.readLine();

                String sql = "INSERT INTO Product (" + PRODUCT_COLUMN_ID + ", " + PRODUCT_COLUMN_NAME + ") " +
                             "VALUES ('" + p_id + "', '" + p_name + "');";
                statement.execute(sql);
            }

        } catch(IOException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    // adds a new row to the PriceCheck table using data parsed with JSoup
    public void addPriceCheck() {
        String productId = "";
        String productPrice = "";
        Statement innerStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            statement = connection.createStatement();

            // get all the product id's from the Product table
            resultSet = statement.executeQuery("SELECT " + PRODUCT_COLUMN_ID + " FROM " + TABLE_PRODUCT);

            // cycle through the ResultSet of product id's and connect to them using JSoup
            while (resultSet.next()) {
                productId = resultSet.getString(PRODUCT_COLUMN_ID);
                String url = "https://www.danmurphys.com.au/product/" + productId;

                // attempt to connect to the url for this product
                Document doc = Jsoup.connect(url).get();

                // select the product price
                Elements prices = doc.select(".prdtlewgt-priceval");

                if (prices.size() > 0) {
                    // the carton price will be in the first selection unless there is a four
                    // or six-pack special on the page
                    productPrice = prices.get(0).text();

                    // remove the $ character so we can check if we parsed the carton price
                    String[] priceSplit = productPrice.split("\\$");
                    productPrice = priceSplit[1];

                    // a carton is incredibly unlikely to be under the 'if value', and a four or six-pack is unlikely
                    // to be over it so we can tell what we've parsed
                    if (Double.parseDouble(productPrice) < 31) {
                        // we didn't parse the carton price from the first selection so it will be in
                        // the second selection
                        priceSplit = prices.get(1).text().split("\\$|\\s");
                        productPrice = priceSplit[1];
                    }

                    innerStatement = connection.createStatement();

                    // add the parsed product price in to the PriceCheck table
                    String sql = "INSERT INTO " + TABLE_PRICECHECK + " ("
                            + PRODUCT_COLUMN_ID + ", " + PRICECHECK_COLUMN_DATETIME + ", " + PRICECHECK_COLUMN_PRICE
                            + ") " + "VALUES ('"
                            + productId + "', strftime('%s', 'now'), '" + productPrice + "'"
                            + ");";

                    innerStatement.execute(sql);

                    // update p_currentprice in the Product table with this price now whilst  we have most of the data
                    // we need instead of as a trigger which would require more queries to the database
                    sql = "UPDATE " + TABLE_PRODUCT
                            + " SET " + PRODUCT_COLUMN_CURRENTPRICE + " = '" + productPrice + "' "
                            + " WHERE " + PRODUCT_COLUMN_ID + " = " + "'" + productId + "'";

                    innerStatement.execute(sql);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            closeResultSet(resultSet);
            closeStatement(innerStatement);
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    // returns all the records in the Product table as an ObservableList of Product objects
    public ObservableList<Product> getAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM " + TABLE_PRODUCT);

            // the query returned results so cycle through the ResultSet and create new Products
            // adding them to the list to return
            while(resultSet.next()) {
                String productId = resultSet.getString(PRODUCT_COLUMN_ID);
                String productName = resultSet.getString(PRODUCT_COLUMN_NAME);
                BigDecimal productCurrentPrice = resultSet.getBigDecimal((PRODUCT_COLUMN_CURRENTPRICE));
                BigDecimal productAvgPrice = resultSet.getBigDecimal(PRODUCT_COLUMN_AVGPRICE);

                products.add(new Product(productId, productName, productCurrentPrice, productAvgPrice));
            }

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }

        return products;
    }
}

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteInitializer {
    Connection connection;

    private static final String DATABASE_NAME = "beerbaron.db";
    private static final int DATABASE_VERSION = 1;

    // schema details for Product table
    private static final String TABLE_PRODUCT = "Product";
    private static final String PRODUCT_COLUMN_ID = "p_id";
    private static final String PRODUCT_COLUMN_NAME = "p_name";
    private static final String PRODUCT_COLUMN_AVGPRICE = "p_avgprice";

    // schema details for PriceCheck table
    private static final String TABLE_PRICECHECK = "PriceCheck";
    private static final String PRICECHECK_COLUMN_DATETIME = "pc_datetime";
    private static final String PRICECHECK_COLUMN_PRICE = "pc_price";

    // queries for table creation
    private static final String DATABASE_CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PRODUCT + "(" +
                PRODUCT_COLUMN_ID + " TEXT NOT NULL, " +
                PRODUCT_COLUMN_NAME + " TEXT, " +
                PRODUCT_COLUMN_AVGPRICE + " REAL, " +
            "CONSTRAINT Product_p_id_pk PRIMARY KEY (" + PRODUCT_COLUMN_ID + "));";

    private static final String DATABASE_CREATE_TABLE_PRICECHECK = "CREATE TABLE IF NOT EXISTS " +
            TABLE_PRICECHECK + "(" +
                PRODUCT_COLUMN_ID + " TEXT NOT NULL, " +
                PRICECHECK_COLUMN_DATETIME + " INTEGER NOT NULL, " +
                PRICECHECK_COLUMN_PRICE  + " REAL, " +
            "CONSTRAINT PriceCheck_p_pc_id_pk PRIMARY KEY (" + PRODUCT_COLUMN_ID + ", " + PRICECHECK_COLUMN_DATETIME + "), " +
            "CONSTRAINT PriceCheck_p_id FOREIGN KEY (" + PRODUCT_COLUMN_ID +
            ") REFERENCES " + TABLE_PRODUCT + "(" + PRODUCT_COLUMN_ID + "));";

    // queries for trigger creation
    private static final String DATABASE_CREATE_TRIGGER_AVGPRICE =
            "CREATE TRIGGER 'update_product_avgprice' " +
                "AFTER INSERT ON '" + TABLE_PRICECHECK + "' " +
                "FOR EACH ROW " +
            "BEGIN " +
                "UPDATE '" + TABLE_PRODUCT + "' " +
                "SET " + PRODUCT_COLUMN_AVGPRICE + " = (" +
                    "SELECT AVG(" + TABLE_PRICECHECK + "." + PRICECHECK_COLUMN_PRICE + ") " +
                    "FROM '" + TABLE_PRICECHECK + "' " +
                    "WHERE " + TABLE_PRICECHECK + "." + PRODUCT_COLUMN_ID + " = " + TABLE_PRODUCT + "." + PRODUCT_COLUMN_ID + " " +
                ");" +
            "END;";

    public SQLiteInitializer() {
        // check if there is an existing database
        if(!Files.exists(Paths.get(DATABASE_NAME))) {
            // there isn't an existing database so we need to create the tables, triggers and add default products
            createDatabaseStructure();
            addDefaultProducts();
        }
    }

    // creates the tables and triggers in the database
    public void createDatabaseStructure() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);

            Statement statement = connection.createStatement();
            statement.execute(DATABASE_CREATE_TABLE_PRODUCT);
            statement.execute(DATABASE_CREATE_TABLE_PRICECHECK);
            statement.execute(DATABASE_CREATE_TRIGGER_AVGPRICE);
            statement.close();

            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDefaultProducts() {
        Path filePath = Paths.get("products.txt");

        try(BufferedReader br = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            Statement statement = connection.createStatement();
            String line = null;

            while((line = br.readLine()) != null) {
                String p_id = line;
                String p_name = br.readLine();

                String sql = "INSERT INTO Product (" + PRODUCT_COLUMN_ID + ", " + PRODUCT_COLUMN_NAME + ") " +
                             "VALUES ('" + p_id + "', '" + p_name + "');";
                statement.execute(sql);
            }

            statement.close();
            connection.close();

        } catch(IOException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPriceCheck(String productID, String productPrice) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
            Statement statement = connection.createStatement();

            String sql = "INSERT INTO " + TABLE_PRICECHECK + " (" + PRODUCT_COLUMN_ID + ", " +
                         PRICECHECK_COLUMN_DATETIME + ", " + PRICECHECK_COLUMN_PRICE + ") " +
                         "VALUES ('" + productID + "', strftime('%s', 'now'), '" + productPrice + "');";

            statement.execute(sql);
            statement.close();
            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ViewPriceCheckGraphController implements Initializable {
    @FXML private Button buttonBack;
    @FXML private LineChart<Date, Double> lineChart;

    private Product product;
    private Stage prevStage;
    private Scene prevScene;

    public ViewPriceCheckGraphController(Product product, Stage prevStage, Scene prevScene) {
        this.product = product;
        this.prevStage = prevStage;
        this.prevScene = prevScene;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set the chart title and label the axis
        lineChart.setTitle("Prices Over Time\n" + product.getProductName());
        lineChart.getXAxis().setLabel("Date");
        lineChart.getYAxis().setLabel("Price ($)");

        // hide the legend at the bottom of the LineChart
        lineChart.setLegendVisible(false);

        // get all the PriceCheck data associated with this product id
        DatabaseHelper dbHelper = new DatabaseHelper();
        ObservableList<PriceCheck> priceChecks = dbHelper.getAllPriceChecksForProductId(product.getProductId());

        XYChart.Series series = new XYChart.Series();

        // cycle through all the PriceCheck objects and add the date and price to the Series for the LineChart
        for(PriceCheck priceCheck : priceChecks) {
            String priceCheckDateTime = priceCheck.getPriceCheckDateTime().toString();
            BigDecimal priceCheckPrice = priceCheck.getPriceCheckPrice();

            XYChart.Data<String, BigDecimal> data = new XYChart.Data(priceCheckDateTime, priceCheckPrice);

            // set the node class for the data as our custom class
            data.setNode(new LineChartHoverNode(priceCheckPrice));

            series.getData().add(data);
        }

        // add the series to the LineChart
        lineChart.getData().add(series);

        // set the back button to render the previous scene
        buttonBack.setOnAction(e -> {
            prevStage.setWidth(prevStage.getWidth());
            prevStage.setHeight(prevStage.getHeight());
            prevStage.setScene(prevScene);
        });
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // a LineChart Node that when hovered over with the mouse displays the PriceCheck price
    private class LineChartHoverNode extends StackPane {
        private Label labelPriceCheckPrice;

        public LineChartHoverNode(BigDecimal priceCheckPrice) {
            // set the label text to the PriceCheck price
            labelPriceCheckPrice = new Label("$" + priceCheckPrice.toString());

            // make the label big enough to see the data
            labelPriceCheckPrice.setMinSize(45, 45);

            // offset the StackPane on the y-axis so that it is visible if the node is near the
            // top of the LineChart
            labelPriceCheckPrice.setTranslateY(20);

            labelPriceCheckPrice.getStyleClass().add("chart-line-symbol-hover-node");

            // display the node data when the cursor enters the node
            setOnMouseEntered(e -> {
                getChildren().setAll(labelPriceCheckPrice);
                setCursor(Cursor.NONE);
                toFront();
            });

            // hide the node data when the cursor leaves the node
            setOnMouseExited(e -> {
                getChildren().clear();
                setCursor(Cursor.DEFAULT);
            });
        }
    }
}

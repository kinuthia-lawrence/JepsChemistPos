package com.larrykin.jepschemistpos.CONTROLLERS;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.larrykin.jepschemistpos.MODELS.Sales;
import com.larrykin.jepschemistpos.UTILITIES.DatabaseConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportsController {

    @FXML
    private AnchorPane combinedChartAnchorPane;


    private DatabaseConn databaseConn = new DatabaseConn();
//    private File saveLocation;

    @FXML
    public void initialize() {
        initializeCombinedChart();
//        setDefaultSaveLocation();
    }

    private void initializeCombinedChart() {
        ObservableList<XYChart.Series<String, Number>> barChartData = getBarChartDataFromDatabase();
        ObservableList<XYChart.Series<String, Number>> lineChartData = getLineChartDataFromDatabase();

        Group combinedChart = createCombinedChart(barChartData, lineChartData);
        setChartInAnchorPane(combinedChart, combinedChartAnchorPane);
    }

    private List<Sales> getSalesFromDatabase() {
        List<Sales> sales = new ArrayList<>();
        String query = "SELECT strftime('%Y-%m-%d', date) as date, SUM(total_amount) as total_amount FROM sales GROUP BY strftime('%Y-%m-%d', date)";
        try (
                Connection connection = databaseConn.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
        ) {
            while (resultSet.next()) {
                Sales sale = new Sales();
                sale.setDate(resultSet.getString("date"));
                sale.setTotalAmount(resultSet.getInt("total_amount"));
                sales.add(sale);
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error fetching sales");
            alert.setContentText("Error fetching sales from the database");
            alert.showAndWait();
            System.out.println("Error fetching sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    private ObservableList<XYChart.Series<String, Number>> getBarChartDataFromDatabase() {
        ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<Sales> salesData = getSalesFromDatabase();
        for (Sales sale : salesData) {
            series.getData().add(new XYChart.Data<>(sale.getDate(), sale.getTotalAmount()));
        }

        barChartData.add(series);
        return barChartData;
    }

    private ObservableList<XYChart.Series<String, Number>> getLineChartDataFromDatabase() {
        ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        List<Sales> salesData = getSalesFromDatabase();
        for (Sales sale : salesData) {
            series.getData().add(new XYChart.Data<>(sale.getDate(), sale.getTotalAmount()));
        }

        lineChartData.add(series);
        return lineChartData;
    }

    private Group createCombinedChart(ObservableList<XYChart.Series<String, Number>> barChartData, ObservableList<XYChart.Series<String, Number>> lineChartData) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Total Amount");

        StackedBarChart<String, Number> stackedBarChart = new StackedBarChart<>(xAxis, yAxis) {
            @Override
            protected void layoutPlotChildren() {
                super.layoutPlotChildren();
                for (XYChart.Series<String, Number> series : getData()) {
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        if (data.getNode() instanceof StackPane) {
                            StackPane stackPane = (StackPane) data.getNode();
                            stackPane.setStyle("-fx-background-color: green;");
                        }
                    }
                }
            }
        };

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        stackedBarChart.setTitle("Sales Trend");
        stackedBarChart.setData(barChartData);
        lineChart.setData(lineChartData);

        return new Group(stackedBarChart, lineChart);
    }

    private void setChartInAnchorPane(Group chart, AnchorPane anchorPane) {
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(chart);
        AnchorPane.setTopAnchor(chart, 0.0);
        AnchorPane.setBottomAnchor(chart, 0.0);
        AnchorPane.setLeftAnchor(chart, 0.0);
        AnchorPane.setRightAnchor(chart, 0.0);
    }

    /*//! Sales Report SECTION
    private void setDefaultSaveLocation() {
        String userHome = System.getProperty("user.home");
        Path defaultPath = Paths.get(userHome, "Desktop", "Sales Report");
        if (!Files.exists(defaultPath)) {
            try {
                Files.createDirectories(defaultPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveLocation = defaultPath.toFile();
    }

    @FXML
    private void chooseSaveLocation() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Location");
        directoryChooser.setInitialDirectory(saveLocation);
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory != null) {
            saveLocation = selectedDirectory;
        }
    }

    public static void createPdfReport(String fileName, List<Sales> sales) {
        try {
            File file = new File(saveLocation, fileName + ".pdf");
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Sales Report"));
            document.add(new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));

            Table table = new Table(new float[]{1, 3, 2, 2, 2, 2, 2, 2});
            table.addHeaderCell("ID");
            table.addHeaderCell("Goods");
            table.addHeaderCell("Expected Amount");
            table.addHeaderCell("Paid Amount");
            table.addHeaderCell("Discount");
            table.addHeaderCell("Cash");
            table.addHeaderCell("Mpesa");
            table.addHeaderCell("Credit");

            for (Sales sale : sales) {
                table.addCell(sale.getSalesID().toString());
                table.addCell(sale.getGoods());
                table.addCell(String.valueOf(sale.getExpectedAmount()));
                table.addCell(String.valueOf(sale.getTotalAmount()));
                table.addCell(String.valueOf(sale.getDiscountAmount()));
                table.addCell(String.valueOf(sale.getCash()));
                table.addCell(String.valueOf(sale.getMpesa()));
                table.addCell(String.valueOf(sale.getCredit()));
            }

            document.add(table);
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
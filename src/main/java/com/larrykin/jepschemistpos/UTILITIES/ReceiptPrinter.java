package com.larrykin.jepschemistpos.UTILITIES;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.print.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiptPrinter implements Printable {
    private static final Logger log = LoggerFactory.getLogger(ReceiptPrinter.class);
    private final String receiptText;
    private static final BlockingQueue<ReceiptPrinter> printQueue = new LinkedBlockingQueue<>();//? this is a blocking queue that will hold the receipt printer objects sequentially

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    DatabaseConn databaseConn = new DatabaseConn();

    public ReceiptPrinter(String receiptText) {
        this.receiptText = receiptText;
    }

    //? get the current date in the date format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(new Date());
    }

    //? get the current receipt number for the date
    private int getCurrentReceiptNumber(String date) {
        int receiptNumber = 1;
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT receipt_number FROM receipt_counter WHERE date = ?")) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                receiptNumber = rs.getInt("receipt_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receiptNumber;
    }

    private void incrementReceiptNumber(String date) {
        try (Connection connection = databaseConn.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO receipt_counter (date, receipt_number) VALUES (?, 1) " +
                             "ON CONFLICT(date) DO UPDATE SET " +
                             "date = CASE WHEN receipt_counter.date = ? THEN receipt_counter.date ELSE ? END, " +
                             "receipt_number = CASE WHEN receipt_counter.date = ? THEN receipt_counter.receipt_number + 1 ELSE 1 END"
             )) {
            ps.setString(1, date);
            ps.setString(2, date);
            ps.setString(3, date);
            ps.setString(4, date);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override //? this method is responsible for rendering the receipt text on the printer
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException { //? this method is called by the printer job to render the receipt text on the printer --taking the configurations done
        if (page > 0) { //? if the page is greater than 0, then return no such page(in case print is called multiple times for different pages) -- because the receipt is designed to be printed on a single page(if page is greator that 0, then it means that the print is called for a different or subsequent page)
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g; //Graphics2D  is used to draw shapes and text on the printer
        g2d.translate(pf.getImageableX(), pf.getImageableY());  //? translate the graphics object to the imageable area of the page format

        g.setFont(new Font("Monospaced", Font.PLAIN, 8));
        int y = 10; //? start drawing text from y position 10

        //Calculate the max width for the text
        int maxWidth = (int) pf.getImageableWidth() - 20; //20 pixels padding

        // Split the receipt text into lines and render each line -- iterate through the receipt text and render each line(draw each line on the receipt)
        for (String line : receiptText.split("\n")) { //split on line break
            //Wrap the text if it exceeds the max width
            while (line.length() > 0) {
                int breakIndex = g.getFontMetrics().stringWidth(line) > maxWidth ? line.lastIndexOf(' ', maxWidth / g.getFontMetrics().charWidth(' ')) : line.length(); //? calculate the break index for the line
                if (breakIndex == -1) breakIndex = line.length(); //? if the break index is -1, then set it to the length of the line
                g.drawString(line.substring(0, breakIndex), 10, y); //? draw the line on the graphics object
                y += g.getFontMetrics().getHeight();//? increment the y position by the height of the font
                line = line.substring(breakIndex).trim(); //? set the line to the remaining text
            }
        }

        return PAGE_EXISTS; //? return page exists to indicate that the page has been rendered
    }

    public void printReceipt() {
        String currentDate = getCurrentDate();
        int receiptNumber = getCurrentReceiptNumber(currentDate);
        String receiptName = receiptNumber + "-of-" + currentDate;

        incrementReceiptNumber(currentDate);
        printQueue.add(this); //? add the receipt printer object to the print queue
        new Thread(() -> {
            try {
                ReceiptPrinter printer = printQueue.take(); //take the receipt printer object from the print queue
                PrinterJob job = PrinterJob.getPrinterJob(); // get the printer job
                job.setPrintable(printer); // set the printer job to the printer object

                //? Set a specific printer
           /*
                PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
                for (PrintService printService : printServices) {
                    if (printService.getName().equals("Your Printer Name")) {
                        job.setPrintService(printService);
                        break;
                    }
                }*/

                //? Set Job Name
                job.setJobName(receiptName);
                //? Create a custom paper size
                Paper paper = new Paper();
                double paperWidth = 2.28 * 72.0; // 58mm in inches
                double paperHeight = Double.MAX_VALUE; // Set to a very large value to let the height be determined by the content
                paper.setSize(paperWidth, paperHeight);
                paper.setImageableArea(0, 0, paperWidth, paperHeight);

                //?Set the custom paper size to the page format
                PageFormat pageFormat = job.defaultPage();
                pageFormat.setPaper(paper);

                job.setPrintable(printer, pageFormat);


                boolean doPrint;
                if (PrintingManager.loadAutoConfirmState()) {
                    doPrint = true;
                } else {
                    doPrint = job.printDialog(); //printer dialog to confirm
                }
                if (doPrint) {
                    try {
                        job.print(); //print the job
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Print Success");
                            alert.setHeaderText("Receipt printed successfully");
                            alert.setContentText("The receipt has been printed successfully.");
                            alert.show();

                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> alert.close()));
                            timeline.setCycleCount(1);
                            timeline.play();
                        });
                    } catch (Exception e) {
                        log.error("Failed to print receipt{}", e.getMessage());
                        e.printStackTrace();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Print Error");
                            alert.setHeaderText("Failed to print receipt");
                            alert.setContentText("An error occurred while printing the receipt: " + e.getMessage());
                            alert.showAndWait();
                        });
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Print queue interrupted", e);
            }
        }).start();
    }
}
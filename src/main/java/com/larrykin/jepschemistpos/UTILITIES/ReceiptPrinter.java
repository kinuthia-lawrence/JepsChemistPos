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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReceiptPrinter implements Printable {
    private static final Logger log = LoggerFactory.getLogger(ReceiptPrinter.class);
    private final String receiptText;
    private static final BlockingQueue<ReceiptPrinter> printQueue = new LinkedBlockingQueue<>();

    public ReceiptPrinter(String receiptText) {
        this.receiptText = receiptText;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        int y = 100;
        for (String line : receiptText.split("\n")) {
            g.drawString(line, 100, y);
            y += g.getFontMetrics().getHeight();
        }

        return PAGE_EXISTS;
    }

    public void printReceipt() {
        printQueue.add(this);
        new Thread(() -> {
            try {
                ReceiptPrinter printer = printQueue.take();
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(printer);

                boolean doPrint = job.printDialog();
                if (doPrint) {
                    try {
                        job.print();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Print Success");
                            alert.setHeaderText("Receipt printed successfully");
                            alert.setContentText("The receipt has been printed successfully.");
                            alert.show();

                            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), ev -> alert.close()));
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
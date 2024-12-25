package com.larrykin.jepschemistpos.UTILITIES;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class ReceiptPrinter implements Printable {
    private static final Logger log = LoggerFactory.getLogger(ReceiptPrinter.class);
    private final String receiptText;

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
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);

        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Print Success");
                    alert.setHeaderText("Receipt printed successfully");
                    alert.setContentText("The receipt has been printed successfully.");
                    alert.showAndWait();
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
    }
}
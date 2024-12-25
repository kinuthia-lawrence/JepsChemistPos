package com.larrykin.jepschemistpos.CONTROLLERS;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelpController implements Initializable {

    @FXML
    private AnchorPane helpAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try (InputStream inputStream = getClass().getResourceAsStream("/HELP.md")) {
            if (inputStream == null) {
                throw new IOException("HELP.md file not found");
            }
            String markdownContent = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            Parser parser = Parser.builder().build();
            Document document = parser.parse(markdownContent);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String htmlContent = renderer.render(document);

            WebView webView = new WebView();
            webView.getEngine().loadContent(htmlContent);
            webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/STYLES/dark-mode.css").toString());

            helpAnchorPane.getChildren().add(webView);
            // Set the AnchorPane constraints to the WebView
            AnchorPane.setTopAnchor(webView, 0.0);
            AnchorPane.setBottomAnchor(webView, 0.0);
            AnchorPane.setLeftAnchor(webView, 0.0);
            AnchorPane.setRightAnchor(webView, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
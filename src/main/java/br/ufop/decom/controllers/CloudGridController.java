package br.ufop.decom.controllers;

import br.ufop.decom.client.Subscriber;
import br.ufop.decom.util.Commons;
import br.ufop.decom.util.Pixel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CloudGridController implements Initializable {
    @FXML
    private BorderPane borderPane;
    @FXML
    private Canvas canvas;
    @FXML
    private MenuItem connectMenuItem;
    @FXML
    private MenuItem disconnectMenuItem;
    @FXML
    private StatusBar statusBar;

    private Subscriber subscriber;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.statusBar = new StatusBar();
        statusBar.setText("Disconnected!");
        borderPane.setBottom(statusBar);
        changeStatusBarImage("disconnected_128x128.png");
        clearCanvas();
        subscriber = new Subscriber(this);
    }

    private void changeStatusBarImage(String imageName) {
        try {
            String imageURL = new File(Commons.IMG_PATH + imageName).toURI().toURL().toString();
            Image connectionImage = new Image(imageURL, 32, 32, true, true);
            ImageView imageView = new ImageView(connectionImage);
            statusBar.getRightItems().clear();
            statusBar.getRightItems().add(imageView);
        } catch (MalformedURLException e) {
            showExceptionDialog(e);
        }
    }

    private void connect(String address, int port) {
        statusBar.setText("Connecting...");

        try {
            subscriber.connect(address, port);
        } catch (Exception e) {
            showExceptionDialog(e);
            e.printStackTrace();
        }

        connectMenuItem.setDisable(true);
        disconnectMenuItem.setDisable(false);
        statusBar.setText("Connected!");
        changeStatusBarImage("connected_128x128.png");
    }

    private void disconnect() {
        try { subscriber.disconnect(); } catch (Exception e) { showExceptionDialog(e); }

        statusBar.setText("Disconnected!");
        connectMenuItem.setDisable(false);
        disconnectMenuItem.setDisable(true);
        changeStatusBarImage( "disconnected_128x128.png");
        clearCanvas();
    }

    private void showExceptionDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Window");
        alert.setHeaderText("An exception occurs");
        alert.setContentText(ex.getLocalizedMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
        System.exit(1);
    }

    private void clearCanvas() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.WHITE);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawPixel(Pixel pixel) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(Color.rgb(pixel.r, pixel.g, pixel.b));
        context.fillOval(pixel.x,pixel.y,1,1);
    }

    private static Optional<Pair<String, String>> showInputDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Connection Window");
        dialog.setHeaderText("Insert the cloud connection info");
        try {
            dialog.setGraphic(new ImageView(new File(Commons.IMG_PATH + "connect_63x64.png").toURI().toURL().toString()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the address and port labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField address = new TextField("localhost");
        address.setPromptText("Address");
        TextField port = new TextField("5000");
        port.setPromptText("Port");

        grid.add(new Label("Address:"), 0, 0);
        grid.add(address, 1, 0);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);

        // Enable/Disable login button depending on whether a address was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);

        // Do some validation (using the Java 8 lambda syntax).
        String IPPattern = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        address.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = true;
            if(!newValue.isEmpty() && !newValue.matches(String.format("localhost|%s", IPPattern))) {
                address.setStyle("-fx-text-fill: red;");
                valid = false;
            }
            else address.setStyle("-fx-text-fill: black;");
            loginButton.setDisable(!valid || port.getText().trim().isEmpty() || address.getText().trim().isEmpty());
        });

        port.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean valid = false;
            try {
                int i = Integer.parseInt(newValue);
                valid = i >= 1024 && i <= 65535;
            } catch (NumberFormatException ignored) {}
            if (valid)
                port.setStyle("-fx-text-fill: black;");
            else port.setStyle("-fx-text-fill: red;");
            loginButton.setDisable(!valid || port.getText().trim().isEmpty() || address.getText().trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the address field by default.
        Platform.runLater(address::requestFocus);

        // Convert the result to a address-port-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(address.getText(), port.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public void connectMenuItemOnAction(@SuppressWarnings("unused") ActionEvent ignored) {
        Optional<Pair<String, String>> result = showInputDialog();
        result.ifPresent(info -> connect(info.getKey(), Integer.parseInt(info.getValue())));

    }

    public void exitMenuItemOnAction(@SuppressWarnings("unused") ActionEvent ignored) {
        disconnect();
        Platform.exit();
    }

    public void disconnectMenuItemOnAction(@SuppressWarnings("unused") ActionEvent ignored) {
        disconnect();
    }
}

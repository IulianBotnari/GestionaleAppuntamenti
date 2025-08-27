package com.gestionale;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ControllerInserisciCosti implements Initializable {
    @FXML
    private TextField descrizione;
    @FXML
    private TextField costo;

    @FXML
    private StackPane warningMessage;
    @FXML
    private StackPane infoMessage;

    @FXML
    public void inserisciCosto() {

        if (descrizione.getText().length() != 0 && costo.getText().length() != 0) {

            TabellaCosti newTabellaCosti = new TabellaCosti();
            newTabellaCosti.setDescrizione(descrizione.getText());
            newTabellaCosti.setImporto(Double.parseDouble(costo.getText()));
            newTabellaCosti.setData(LocalDate.now());
            DatabaseManager.inserisciTabellaCosti(newTabellaCosti);


            Text infoMessageOk = new Text("Articolo inserito con successo!");
            VBox verticalBox = new VBox();
            Platform.runLater(() -> {
                infoMessage.setOpacity(1);
                infoMessage.setVisible(true);
                infoMessage.setManaged(true);
                infoMessage.getChildren().add(verticalBox);

                verticalBox.setAlignment(Pos.CENTER);
                verticalBox.getChildren().add(infoMessageOk);
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                        infoMessage.setOpacity(0);
                        infoMessage.setVisible(false);
                        infoMessage.setManaged(false);

                        if (verticalBox.getChildren().contains(infoMessageOk)) {
                            verticalBox.getChildren().remove(infoMessageOk);
                        }
                        if (infoMessage.getChildren().contains(verticalBox)) {
                            infoMessage.getChildren().remove(verticalBox);
                        }
                    });
                }
            }, 5000);
        } else {
            Text errorMessageCampiVuoti = new Text("Devi compliare entrambi i campi");
            VBox verticalBox = new VBox();
            Platform.runLater(() -> {
                warningMessage.setOpacity(1);
                warningMessage.setVisible(true);
                warningMessage.setManaged(true);
                warningMessage.getChildren().add(verticalBox);

                verticalBox.setAlignment(Pos.CENTER);
                verticalBox.getChildren().add(errorMessageCampiVuoti);
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                        warningMessage.setOpacity(0);
                        warningMessage.setVisible(false);
                        warningMessage.setManaged(false);

                        if (verticalBox.getChildren().contains(errorMessageCampiVuoti)) {
                            verticalBox.getChildren().remove(errorMessageCampiVuoti);
                        }
                        if (warningMessage.getChildren().contains(verticalBox)) {
                            warningMessage.getChildren().remove(verticalBox);
                        }
                    });
                }
            }, 5000);
        }

    }

    @FXML
    public void vaiAHome(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
        App.setRoot("home");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}

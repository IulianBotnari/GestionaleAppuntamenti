package com.gestionale;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MessaggiPerUtente {

    public static void inserisciAppuntamentoInContabilita(StackPane infoMessage, StackPane warningMessage,
            Text message) {
    

        if (warningMessage == null) {

            VBox verticalBox = new VBox();
            Platform.runLater(() -> {
                infoMessage.setOpacity(1);
                infoMessage.setVisible(true);
                infoMessage.setManaged(true);
                infoMessage.getChildren().add(verticalBox);

                verticalBox.setAlignment(Pos.CENTER);
                verticalBox.getChildren().add(message);
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                        infoMessage.setOpacity(0);
                        infoMessage.setVisible(false);
                        infoMessage.setManaged(false);

                        if (verticalBox.getChildren().contains(message)) {
                            verticalBox.getChildren().remove(message);
                        }
                        if (infoMessage.getChildren().contains(verticalBox)) {
                            infoMessage.getChildren().remove(verticalBox);
                        }
                    });
                }
            }, 5000);
        } else if (infoMessage == null) {
            VBox verticalBox = new VBox();
            Platform.runLater(() -> {
                warningMessage.setOpacity(1);
                warningMessage.setVisible(true);
                warningMessage.setManaged(true);
                warningMessage.getChildren().add(verticalBox);

                verticalBox.setAlignment(Pos.CENTER);
                verticalBox.getChildren().add(message);
            });

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {

                    Platform.runLater(() -> {
                        warningMessage.setOpacity(0);
                        warningMessage.setVisible(false);
                        warningMessage.setManaged(false);

                        if (verticalBox.getChildren().contains(message)) {
                            verticalBox.getChildren().remove(message);
                        }
                        if (warningMessage.getChildren().contains(verticalBox)) {
                            warningMessage.getChildren().remove(verticalBox);
                        }
                    });
                }
            }, 5000);
        }
    }
}

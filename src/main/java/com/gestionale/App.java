package com.gestionale;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static Scene sceneAggiona;
    private static Scene sceneReport;
    private static Scene sceneAcquista;

    // private static ObservableList<Appuntamento> appuntamentiList =
    // FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            scene = new Scene(loadFXML("home"));
            stage.setScene(scene);
            stage.setTitle("Gestione Appuntamenti");
            stage.setMaximized(true);
            stage.setY(10);
            stage.show();

            DatabaseManager.createNewDatabase();
            DatabaseManager.createNewTable();
            DatabaseManager.creaTabellaFatturato();
            DatabaseManager.creaTabellaDisponibilità();
            DatabaseManager.creaTabellaNero();
            DatabaseManager.creaTabellaCosti();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void loadAggiornamento(String fxml) throws IOException {
        try {

            Stage stageAggiorna = new Stage();
            // stageAggiorna.initStyle(StageStyle.UNDECORATED);
            sceneAggiona = new Scene(loadFXML(fxml));
            stageAggiorna.initModality(Modality.APPLICATION_MODAL);
            stageAggiorna.setScene(sceneAggiona);
            stageAggiorna.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCreaReport(String fxml) throws IOException {
        try {
            Stage stageReport = new Stage();
            sceneReport = new Scene(loadFXML(fxml));
            stageReport.initModality(Modality.APPLICATION_MODAL);
            stageReport.setScene(sceneReport);
            stageReport.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void loadInserisciAcquisto(String fxml) throws IOException {
        try {
            Stage stageAcquisto = new Stage();
            sceneAcquista = new Scene(loadFXML(fxml));
            stageAcquisto.initModality(Modality.APPLICATION_MODAL);
            stageAcquisto.setScene(sceneReport);
            stageAcquisto.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
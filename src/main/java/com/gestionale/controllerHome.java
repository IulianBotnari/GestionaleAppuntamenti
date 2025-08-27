package com.gestionale;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class controllerHome implements Initializable {

    @FXML
    private Button vaiInserisciAppuntamento;

    // @FXML
    // private Button vaiAReport;

    @FXML
    private ListView<Appuntamento> appuntamentiListView;

    @FXML
    private DatePicker filtraPerData;
    @FXML
    private StackPane warningMessage;

    @FXML
    private StackPane infoMessage;

    @FXML
    public void vaiAInserisci(ActionEvent event) throws IOException {
        App.setRoot("inserisci");
    }

    public void apriModaleAggiorna(ActionEvent event, String fxml) throws IOException {
        App.setRoot(fxml);
    }

    @FXML
    public void creaReport(ActionEvent event) throws IOException {
        App.loadCreaReport("generaReport");
    }

    @FXML
    public void inserisciAcquisto(ActionEvent event) throws IOException {
        App.loadCreaReport("inserisciCosti");
    }

    @FXML
    public void getAppuntamentiByDate(ActionEvent e) throws Exception {
        LocalDate data = filtraPerData.getValue();
        appuntamentiListView.setItems(DatabaseManager.getDbAppuntamentiList(data));
        appuntamentiListView.setCellFactory((ListView<Appuntamento> param) -> {
            return new ListCell<Appuntamento>() {
                @Override
                protected void updateItem(Appuntamento appuntamento, boolean empty) {
                    super.updateItem(appuntamento, empty);

                    if (empty || appuntamento == null) {
                        setText(null);
                        setGraphic(null);
                    } else {

                          HBox hbox = new HBox(10);
                        hbox.setPadding(new Insets(5, 5, 5, 5));
                        hbox.setAlignment(Pos.CENTER);

                        HBox.setHgrow(appuntamentiListView, Priority.ALWAYS);

                        Label nomeLabel = new Label("Nome: ");
                        nomeLabel.setStyle("-fx-font-weight: bold");
                        Label cognomeLabel = new Label("Cognome: ");
                        cognomeLabel.setStyle("-fx-font-weight: bold");
                        Label trattamentoLabel = new Label("Trattamento: ");
                        trattamentoLabel.setStyle("-fx-font-weight: bold");
                        Label oraInizioLabel = new Label("Inizio: ");
                        oraInizioLabel.setStyle("-fx-font-weight: bold");
                        Label durata = new Label("Durata: ");
                        durata.setStyle("-fx-font-weight: bold");
                        Label dataLabel = new Label("Data: ");
                        dataLabel.setStyle("-fx-font-weight: bold");
                        Label lablePrezzo = new Label("Prezzo: ");
                        lablePrezzo.setStyle("-fx-font-weight: bold;");
                        Label dataRecuperata = new Label(appuntamento.getData()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        dataRecuperata.setStyle("fx-text-fill: red;");

                        Label recuperoOraInizioLabel = new Label(String.valueOf(appuntamento.getOraInizio()));
                        recuperoOraInizioLabel.setStyle("fx-text-fill: red;");
                        Button aggiorna = new Button("Aggiorna");
                        aggiorna.setOnAction(event -> {
                            try {
                                ControllerModificaAppuntamento.datiAppuntamento(appuntamento);
                            } catch (IOException ex) {
                            }

                        });

                        CheckBox completatoCheckBox = new CheckBox("Completato");
                        Button elimina = new Button("Elimina");
                        elimina.setOnAction(
                                event -> DatabaseManager.eliminaAppuntamento(appuntamento.getId(),
                                        appuntamento.getData(),
                                        appuntamentiListView, appuntamento.getOraInizio(), appuntamento.getDurata()));
                        ToggleGroup selezionaTipoIncasso = new ToggleGroup();

                        ToggleButton fatturato = new ToggleButton();
                        fatturato.setToggleGroup(selezionaTipoIncasso);
                        fatturato.setText("Fatturato");
                        fatturato.setSelected(appuntamento.isFatturato() == 0 ? false : true);
                        ToggleButton nero = new ToggleButton();
                        nero.setToggleGroup(selezionaTipoIncasso);
                        nero.setText("Nero");
                        nero.setSelected(appuntamento.isNero() == 0 ? false : true);

                        fatturato.setOnAction(event -> {
                            if (fatturato.isSelected()) {
                                DatabaseManager.setStatoFatturatoInAppuntamento(1, appuntamento.getId());

                            } else if (!fatturato.isSelected()) {
                                DatabaseManager.setStatoFatturatoInAppuntamento(0, appuntamento.getId());

                            }
                        });

                        nero.setOnAction(event -> {
                            if (nero.isSelected()) {
                                DatabaseManager.setStatoNeroInAppuntamento(1, appuntamento.getId());

                            } else if (!nero.isSelected()) {
                                DatabaseManager.setStatoNeroInAppuntamento(0, appuntamento.getId());

                            }

                        });

                        completatoCheckBox.setSelected((appuntamento.isCompletato() == 0) ? false : true);
                        completatoCheckBox
                                .setOnAction(event -> {
                                    if (fatturato.isSelected() && completatoCheckBox.isSelected()) {
                                        DatabaseManager.inserisciTabellaFaturato(appuntamento);
                                        DatabaseManager.setStatoCompletatoInAppuntamento(1, appuntamento.getId());
                                        fatturato.setDisable(true);
                                        nero.setDisable(true);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento inserito in registro contabile!"));
                                        // completatoCheckBox.setSelected(true);
                                    } else if (nero.isSelected() && completatoCheckBox.isSelected()) {
                                        DatabaseManager.inserisciTabellaNero(appuntamento);
                                        DatabaseManager.setStatoCompletatoInAppuntamento(1, appuntamento.getId());
                                        fatturato.setDisable(true);
                                        nero.setDisable(true);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento inserito in registro contabile!"));

                                    } else if (fatturato.isSelected() && !completatoCheckBox.isSelected()) {
                                        DatabaseManager.remuoviElementoDaTabellaFatturato(appuntamento.getId());
                                        DatabaseManager.setStatoCompletatoInAppuntamento(0, appuntamento.getId());
                                        fatturato.setDisable(false);
                                        nero.setDisable(false);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento disinserito dal registro contabile!"));

                                    } else if (nero.isSelected() && !completatoCheckBox.isSelected()) {
                                        DatabaseManager.remuoviElementoDaTabellaFatturato(appuntamento.getId());
                                        DatabaseManager.setStatoCompletatoInAppuntamento(0, appuntamento.getId());
                                        fatturato.setDisable(false);
                                        nero.setDisable(false);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento disinserito dal registro contabile!"));

                                    } else {
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(null, warningMessage,
                                                new Text("Devi selezionare fatturato oppure nero!"));

                                        if (completatoCheckBox.isSelected() == true) {
                                            completatoCheckBox.setSelected(false);

                                        } else {
                                            completatoCheckBox.setSelected(true);
                                        }

                                    }
                                });
                        if (completatoCheckBox.isSelected()) {

                            fatturato.setDisable(true);
                            nero.setDisable(true);
                        } else {
                            fatturato.setDisable(false);
                            nero.setDisable(false);
                        }
                        ;

                        // Spaziatore per spingere i bottoni a destra
                        Region spacer = new Region();

                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Label valoreNomeLabel = new Label(appuntamento.getNome());
                        valoreNomeLabel.setPrefWidth(130);

                        Label valoreCognomeLabel = new Label(appuntamento.getCognome());
                        valoreCognomeLabel.setPrefWidth(130);
                        Label valoreTrattamentoLabel = new Label(appuntamento.getTrattamento());
                        valoreTrattamentoLabel.setPrefWidth(130);
                        if (completatoCheckBox.isSelected()) {

                            dataRecuperata.setStyle("fx-text-fill: green;");
                        }

                        DecimalFormat df = new DecimalFormat("#.00");

                        hbox.getChildren().addAll(
                                dataLabel,
                                dataRecuperata,
                                oraInizioLabel,
                                recuperoOraInizioLabel,
                                trattamentoLabel,
                                valoreTrattamentoLabel,
                                nomeLabel,
                                valoreNomeLabel,
                                cognomeLabel,
                                valoreCognomeLabel,
                                durata,
                                new Label(String.valueOf(appuntamento.getDurata())),
                                lablePrezzo,
                                new Label(String.valueOf(df.format(appuntamento.getPrezzo()))),
                                spacer,
                                fatturato,
                                nero,
                                aggiorna,
                                elimina,
                                completatoCheckBox);
                        setGraphic(hbox);

                    }
                }
            };
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate data = filtraPerData.getValue();

        appuntamentiListView.setItems(DatabaseManager.getDbAppuntamentiList(data));
        appuntamentiListView.setCellFactory((ListView<Appuntamento> param) -> {
            return new ListCell<Appuntamento>() {
                @Override
                protected void updateItem(Appuntamento appuntamento, boolean empty) {
                    super.updateItem(appuntamento, empty);

                    if (empty || appuntamento == null) {
                        setText(null);
                        setGraphic(null);
                    } else {

                        HBox hbox = new HBox(10);
                        hbox.setPadding(new Insets(5, 5, 5, 5));
                        hbox.setAlignment(Pos.CENTER);

                        HBox.setHgrow(appuntamentiListView, Priority.ALWAYS);

                        Label nomeLabel = new Label("Nome: ");
                        nomeLabel.setStyle("-fx-font-weight: bold");
                        Label cognomeLabel = new Label("Cognome: ");
                        cognomeLabel.setStyle("-fx-font-weight: bold");
                        Label trattamentoLabel = new Label("Trattamento: ");
                        trattamentoLabel.setStyle("-fx-font-weight: bold");
                        Label oraInizioLabel = new Label("Inizio: ");
                        oraInizioLabel.setStyle("-fx-font-weight: bold");
                        Label durata = new Label("Durata: ");
                        durata.setStyle("-fx-font-weight: bold");
                        Label dataLabel = new Label("Data: ");
                        dataLabel.setStyle("-fx-font-weight: bold");
                        Label lablePrezzo = new Label("Prezzo: ");
                        lablePrezzo.setStyle("-fx-font-weight: bold;");
                        Label dataRecuperata = new Label(appuntamento.getData()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        dataRecuperata.setStyle("fx-text-fill: red;");

                        Label recuperoOraInizioLabel = new Label(String.valueOf(appuntamento.getOraInizio()));
                        recuperoOraInizioLabel.setStyle("fx-text-fill: red;");
                        Button aggiorna = new Button("Aggiorna");
                        aggiorna.setOnAction(event -> {
                            try {
                                ControllerModificaAppuntamento.datiAppuntamento(appuntamento);
                            } catch (IOException ex) {
                            }

                        });

                        CheckBox completatoCheckBox = new CheckBox("Completato");
                        Button elimina = new Button("Elimina");
                        elimina.setOnAction(
                                event -> DatabaseManager.eliminaAppuntamento(appuntamento.getId(),
                                        appuntamento.getData(),
                                        appuntamentiListView, appuntamento.getOraInizio(), appuntamento.getDurata()));
                        ToggleGroup selezionaTipoIncasso = new ToggleGroup();

                        ToggleButton fatturato = new ToggleButton();
                        fatturato.setToggleGroup(selezionaTipoIncasso);
                        fatturato.setText("Fatturato");
                        fatturato.setSelected(appuntamento.isFatturato() == 0 ? false : true);
                        ToggleButton nero = new ToggleButton();
                        nero.setToggleGroup(selezionaTipoIncasso);
                        nero.setText("Nero");
                        nero.setSelected(appuntamento.isNero() == 0 ? false : true);

                        fatturato.setOnAction(event -> {
                            if (fatturato.isSelected()) {
                                DatabaseManager.setStatoFatturatoInAppuntamento(1, appuntamento.getId());

                            } else if (!fatturato.isSelected()) {
                                DatabaseManager.setStatoFatturatoInAppuntamento(0, appuntamento.getId());

                            }
                        });

                        nero.setOnAction(event -> {
                            if (nero.isSelected()) {
                                DatabaseManager.setStatoNeroInAppuntamento(1, appuntamento.getId());

                            } else if (!nero.isSelected()) {
                                DatabaseManager.setStatoNeroInAppuntamento(0, appuntamento.getId());

                            }

                        });

                        completatoCheckBox.setSelected((appuntamento.isCompletato() == 0) ? false : true);
                        completatoCheckBox
                                .setOnAction(event -> {
                                    if (fatturato.isSelected() && completatoCheckBox.isSelected()) {
                                        DatabaseManager.inserisciTabellaFaturato(appuntamento);
                                        DatabaseManager.setStatoCompletatoInAppuntamento(1, appuntamento.getId());
                                        fatturato.setDisable(true);
                                        nero.setDisable(true);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento inserito in registro contabile!"));
                                        // completatoCheckBox.setSelected(true);
                                    } else if (nero.isSelected() && completatoCheckBox.isSelected()) {
                                        DatabaseManager.inserisciTabellaNero(appuntamento);
                                        DatabaseManager.setStatoCompletatoInAppuntamento(1, appuntamento.getId());
                                        fatturato.setDisable(true);
                                        nero.setDisable(true);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento inserito in registro contabile!"));

                                    } else if (fatturato.isSelected() && !completatoCheckBox.isSelected()) {
                                        DatabaseManager.remuoviElementoDaTabellaFatturato(appuntamento.getId());
                                        DatabaseManager.setStatoCompletatoInAppuntamento(0, appuntamento.getId());
                                        fatturato.setDisable(false);
                                        nero.setDisable(false);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento disinserito dal registro contabile!"));

                                    } else if (nero.isSelected() && !completatoCheckBox.isSelected()) {
                                        DatabaseManager.remuoviElementoDaTabellaFatturato(appuntamento.getId());
                                        DatabaseManager.setStatoCompletatoInAppuntamento(0, appuntamento.getId());
                                        fatturato.setDisable(false);
                                        nero.setDisable(false);
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(infoMessage, null,
                                                new Text("Appuntamento disinserito dal registro contabile!"));

                                    } else {
                                        MessaggiPerUtente.inserisciAppuntamentoInContabilita(null, warningMessage,
                                                new Text("Devi selezionare fatturato oppure nero!"));

                                        if (completatoCheckBox.isSelected() == true) {
                                            completatoCheckBox.setSelected(false);

                                        } else {
                                            completatoCheckBox.setSelected(true);
                                        }

                                    }
                                });
                        if (completatoCheckBox.isSelected()) {

                            fatturato.setDisable(true);
                            nero.setDisable(true);
                        } else {
                            fatturato.setDisable(false);
                            nero.setDisable(false);
                        }
                        ;

                        // Spaziatore per spingere i bottoni a destra
                        Region spacer = new Region();

                        HBox.setHgrow(spacer, Priority.ALWAYS);

                        Label valoreNomeLabel = new Label(appuntamento.getNome());
                        valoreNomeLabel.setPrefWidth(130);

                        Label valoreCognomeLabel = new Label(appuntamento.getCognome());
                        valoreCognomeLabel.setPrefWidth(130);
                        Label valoreTrattamentoLabel = new Label(appuntamento.getTrattamento());
                        valoreTrattamentoLabel.setPrefWidth(130);
                        if (completatoCheckBox.isSelected()) {

                            dataRecuperata.setStyle("fx-text-fill: green;");
                        }

                        DecimalFormat df = new DecimalFormat("#.00");

                        hbox.getChildren().addAll(
                                dataLabel,
                                dataRecuperata,
                                oraInizioLabel,
                                recuperoOraInizioLabel,
                                trattamentoLabel,
                                valoreTrattamentoLabel,
                                nomeLabel,
                                valoreNomeLabel,
                                cognomeLabel,
                                valoreCognomeLabel,
                                durata,
                                new Label(String.valueOf(appuntamento.getDurata())),
                                lablePrezzo,
                                new Label(String.valueOf(df.format(appuntamento.getPrezzo()))),
                                spacer,
                                fatturato,
                                nero,
                                aggiorna,
                                elimina,
                                completatoCheckBox);
                        setGraphic(hbox);

                    }
                }
            };
        });
    }
}

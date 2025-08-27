package com.gestionale;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ControllerInserisci implements Initializable {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private SplitMenuButton trattamentoField;
    @FXML
    private Spinner<Integer> oraInizioFieldOre;
    @FXML
    private Spinner<Integer> oraInizioFieldMinuti;
    @FXML
    private Label durata;
    @FXML
    private Label prezzo;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private StackPane warningMessage;
    @FXML
    private ListView<Disponibilita> visualizzaDisponibilita;
    @FXML
    private Button cercaDisponibilita;
    @FXML
    private DatePicker dataDisponibilita;

    private VBox verticalBox = new VBox();
    private Text errorMessageCampiVuoti = new Text(
            "Impossibile registrare l'appuntamento, i campi \"Nome\" \"Cognome\" \"Trattamento\" \"Data\" devono essere compilati");
    private Text errorMessageOrarioNonValido = new Text(
            "Errore: l'appuntamento e fuori dall'orario di lavoro.");
    private Text errorMessageMinutiNonValidi = new Text(
            "Errore: il campo minuti accetta solo valori multipli di 5, oppure il valore 0.");
    private Text errorMessageNonDisponibile = new Text("Impossibile aggiungere appuntamento, posto già occupato");

    @FXML
    private void registraAppuntamento(ActionEvent event)
            throws IOException, StringIndexOutOfBoundsException, InvocationTargetException, IllegalArgumentException {

        try {
            int contatore = 0;
            String nome = nomeField.getText();
            String cognome = cognomeField.getText();
            String trattamento = trattamentoField.getText();
            Integer oraInizio = oraInizioFieldOre.getValue();
            Integer minutoInizio = oraInizioFieldMinuti.getValue();
            String oraInFormatoCorretto = (oraInizio < 10) ? "0" + String.valueOf(oraInizio)
                    : String.valueOf(oraInizio);
            String minutoInFormatoCorretto = (minutoInizio < 10) ? "0" + String.valueOf(minutoInizio)
                    : String.valueOf(minutoInizio);
            String oraInFormatoString = oraInFormatoCorretto + ":" + minutoInFormatoCorretto;

            LocalDate data = dataPicker.getValue();
            String stringToNumber = durata.getText();
            double durataTrattamento = 0;
            double prezzoConvertito = 0;
            if (stringToNumber.length() > 0) {

                durataTrattamento = Double.parseDouble(stringToNumber.substring(0, 3).trim());
            }
            if (prezzo.getText().length() > 0) {

                prezzoConvertito = Double.parseDouble(prezzo.getText().replace(",", "."));
            }

            if (nome.isEmpty() || cognome.isEmpty() || trattamento.isEmpty()
                    || data == null || durata.getText() == null) {
                System.out.println(errorMessageCampiVuoti);

                Platform.runLater(() -> {
                    warningMessage.setOpacity(1);
                    warningMessage.setVisible(true);
                    warningMessage.setManaged(true);
                    warningMessage.getChildren().add(verticalBox);

                    verticalBox.setAlignment(Pos.CENTER);
                    errorMessageNonDisponibile.setStyle("-fx-text-fill: black;");
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
                return;
            }

            if (durataTrattamento > 21 - durataTrattamento || oraInizio < 7) {
                System.out.println(errorMessageOrarioNonValido);

                Platform.runLater(() -> {
                    warningMessage.setOpacity(1);
                    warningMessage.setVisible(true);
                    warningMessage.setManaged(true);
                    warningMessage.getChildren().add(verticalBox);

                    verticalBox.setAlignment(Pos.CENTER);
                    errorMessageNonDisponibile.setStyle("-fx-text-fill: black;");
                    verticalBox.getChildren().add(errorMessageOrarioNonValido);
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            warningMessage.setOpacity(0);
                            warningMessage.setVisible(false);
                            warningMessage.setManaged(false);

                            if (verticalBox.getChildren().contains(errorMessageOrarioNonValido)) {
                                verticalBox.getChildren().remove(errorMessageOrarioNonValido);
                            }
                            if (warningMessage.getChildren().contains(verticalBox)) {
                                warningMessage.getChildren().remove(verticalBox);
                            }
                        });
                    }
                }, 5000);
                return;
            }

            if (minutoInizio % 5 != 0) {
                System.out.println(errorMessageMinutiNonValidi);

                Platform.runLater(() -> {
                    warningMessage.setOpacity(1);
                    warningMessage.setVisible(true);
                    warningMessage.setManaged(true);
                    warningMessage.getChildren().add(verticalBox);
                    verticalBox.setAlignment(Pos.CENTER);
                    errorMessageNonDisponibile.setStyle("-fx-text-fill: black;");
                    verticalBox.getChildren().add(errorMessageMinutiNonValidi);
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        Platform.runLater(() -> {
                            warningMessage.setOpacity(0);
                            warningMessage.setVisible(false);
                            warningMessage.setManaged(false);

                            if (verticalBox.getChildren().contains(errorMessageMinutiNonValidi)) {
                                verticalBox.getChildren().remove(errorMessageMinutiNonValidi);
                            }
                            if (warningMessage.getChildren().contains(verticalBox)) {
                                warningMessage.getChildren().remove(verticalBox);
                            }
                        });
                    }
                }, 5000);
                return;
            }

            if (DatabaseManager.insertNewAppuntamento(nome, cognome, trattamento, oraInFormatoString, durataTrattamento,
                    data, prezzoConvertito, 0, 0, 0, contatore) == true) {

                Platform.runLater(() -> {
                    warningMessage.setOpacity(1);
                    warningMessage.setVisible(true);
                    warningMessage.setManaged(true);
                    warningMessage.getChildren().add(verticalBox);
                    verticalBox.setAlignment(Pos.CENTER);
                    errorMessageNonDisponibile.setStyle("-fx-text-fill: black;");
                    verticalBox.getChildren().add(errorMessageNonDisponibile);
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {

                        Platform.runLater(() -> {
                            warningMessage.setOpacity(0);
                            warningMessage.setVisible(false);
                            warningMessage.setManaged(false);

                            if (verticalBox.getChildren().contains(errorMessageNonDisponibile)) {
                                verticalBox.getChildren().remove(errorMessageNonDisponibile);
                            }
                            if (warningMessage.getChildren().contains(verticalBox)) {
                                warningMessage.getChildren().remove(verticalBox);
                            }
                        });
                    }
                }, 5000);
                return;

            }
            DatabaseManager.insertNewAppuntamento(nome, cognome, trattamento, oraInFormatoString, durataTrattamento,
                    data, prezzoConvertito, 0, 0, 0, contatore);

            nomeField.clear();
            cognomeField.clear();
            trattamentoField.setText("Seleziona Trattamento");
            oraInizioFieldOre.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
            oraInizioFieldMinuti.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 00));
            dataPicker.setValue(null);

        } catch (StringIndexOutOfBoundsException e) {
            System.err.println(
                    "Campo durata ha una lunghezza non valida, deve avere la lunghezza di 3 caratteri, compreso il punto: "
                            + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Errore riga 260 conInserisci: " + e.getMessage());
        }
    }

    @FXML
    private void vaiAHome(ActionEvent event) throws IOException {
        App.setRoot("home");
    }

    @FXML
    private void selezionaTrattamento(ActionEvent e) {
        Trattamento laminazione = new Trattamento("Laminazione", 1, 28);
        Trattamento semiPermanente = new Trattamento("Semi", 1.5, 25);
        Trattamento[] listaTrattamenti = { laminazione, semiPermanente };
        MenuItem selectedItem = (MenuItem) e.getSource();

        String selectedText = selectedItem.getText();

        trattamentoField.setText(selectedText);
        for (int i = 0; i < listaTrattamenti.length; i++) {
            if (selectedText.equals(listaTrattamenti[i].getNome())) {
                DecimalFormat df = new DecimalFormat("#.00");
                durata.setText(String.valueOf(listaTrattamenti[i].getDurata()) + " Ore");
                prezzo.setText(df.format(listaTrattamenti[i].getPrezzo()));

                break;
            }
        }

    }

    @FXML
    public void visualizzaDisponibilita(ActionEvent e) throws Exception {
        visualizzaDisponibilita.setItems(DatabaseManager.getDisponibilita(dataDisponibilita));




        visualizzaDisponibilita.setCellFactory(param -> new ListCell<Disponibilita>() {

            @Override
            protected void updateItem(Disponibilita disponibilita, boolean empty) {
                super.updateItem(disponibilita, empty);

                if (visualizzaDisponibilita.getItems().isEmpty() || empty || disponibilita == null) {
                    setGraphic(null);
                    setText(null);

                } else {

                    HBox hbox = new HBox();
                    hbox.setPadding(new Insets(0, 5, 0, 5));
                    Label oraLabel = new Label("Ora: " + disponibilita.getOra());
                    oraLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

                    if (disponibilita.getOccupato() == 1) {
                        hbox.setStyle("-fx-background-color: #b30000ff;");
                        setStyle("-fx-background-color: #b30000ff;");
                        setText(null);
                        setGraphic(hbox);
                    } else {
                        hbox.setStyle("-fx-background-color: #049104ff;");
                        setStyle("-fx-background-color: #049104ff;");
                        oraLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                        setText(null);
                        setGraphic(hbox);
                    }
                    int indiceCorrente = getIndex();
                    if (indiceCorrente != 0) {
                        Disponibilita disponibilitaPrecedente = visualizzaDisponibilita.getItems()
                                .get(indiceCorrente - 1);

                        if (disponibilitaPrecedente.getOccupato() == disponibilita.getOccupato()) {
                            String oraAttuale = disponibilita.getOra();
                            int oraAttualeInt = Integer.parseInt(oraAttuale.substring(0, 2));
                            int minutoAttualeInt = Integer.parseInt(oraAttuale.substring(3, 5));
                            if (minutoAttualeInt == 55) {
                                minutoAttualeInt = 0;
                            } else {
                                minutoAttualeInt += 5;
                            }

                            if (minutoAttualeInt == 0) {
                                oraAttualeInt += 1;
                            }

                            String parseOraStringa = (oraAttualeInt < 10) ? "0" + String.valueOf(oraAttualeInt)
                                    : String.valueOf(oraAttualeInt);
                            String parseMinutoStringa = (minutoAttualeInt < 10) ? "0" + String.valueOf(minutoAttualeInt)
                                    : String.valueOf(minutoAttualeInt);

                            String conversioneCompleta = parseOraStringa + ":" + parseMinutoStringa;
                            oraLabel.setText("Ora: " + conversioneCompleta);
                            hbox.getChildren().add(oraLabel);
                        } else {
                            hbox.getChildren().add(oraLabel);
                        }
                    } else {
                        hbox.getChildren().add(oraLabel);
                    }

                }
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // vengono creati 2 nuovi oggetti di tipo trattamento
        Trattamento laminazione = new Trattamento("Laminazione", 1, 28);
        Trattamento semiPermanente = new Trattamento("Semi", 1.5, 25);

        // vengono creati due oggetti di tipo MenuItem con lo scopo di riempirli con il
        // nome dei trattamenti precedenti creati e poi aggiungerli a Trattamento field
        // oggetto di tipo SplitMenuButton
        MenuItem itemLaminazione = new MenuItem(laminazione.getNome());
        itemLaminazione.setOnAction(this::selezionaTrattamento);

        MenuItem itemSemiPermanente = new MenuItem(semiPermanente.getNome());
        // implementiamo il metodo seleziona trattamento al click del menu
        itemSemiPermanente.setOnAction(this::selezionaTrattamento);

        // getItems() viene usato perchè trattamentoField e un istanza di un oggeto
        // SplitMenuButton che ha di default un figlio Items .addAll() lo usiamo per
        // aggiungere i menu
        trattamentoField.getItems().addAll(itemLaminazione, itemSemiPermanente);

        trattamentoField.setText("Seleziona Trattamento");

        oraInizioFieldOre.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        oraInizioFieldOre.setEditable(true);

        oraInizioFieldMinuti.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        oraInizioFieldMinuti.setEditable(true);
    }

}
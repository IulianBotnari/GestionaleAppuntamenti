package com.gestionale;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ControllerModificaAppuntamento implements Initializable {
    @FXML
    private TextField nomeField;
    @FXML
    private TextField cognomeField;
    @FXML
    private SplitMenuButton trattamentoField;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private Spinner<Integer> oraField;
    @FXML
    private Spinner<Integer> minutoField;
    @FXML
    private Label durataField;
    @FXML
    private Label prezzo;
    @FXML
    private CheckBox completatoCheckBox;
    @FXML
    private StackPane warningMessage;

    private static  VBox verticalBox = new VBox();
    private Text errorMessageCampiVuoti = new Text(
            "Impossibile registrare l'appuntamento,\ni campi \"Nome\" \"Cognome\" \"Trattamento\" \"Data\" \ndevono essere compilati");
    private Text errorMessageOrarioNonValido = new Text(
            "Errore: l'appuntamento e fuori dall'orario di lavoro.");
    private Text errorMessageMinutiNonValidi = new Text(
            "Errore: il campo minuti accetta solo valori multipli di 5, oppure il valore 0.");
    private Text errorMessageNonDisponibile = new Text("Impossibile aggiungere appuntamento, posto già occupato");

    private static Appuntamento appuntamentoCorrente = new Appuntamento();

    public static void datiAppuntamento(Appuntamento appuntamento) throws IOException {
        ControllerModificaAppuntamento.appuntamentoCorrente = appuntamento;
        App.loadAggiornamento("modificaAppuntamento");

    }

    public void aggiornaAppuntamento(ActionEvent event) {
        Appuntamento oldAppuntamento = ControllerModificaAppuntamento.appuntamentoCorrente;
        Appuntamento newAppuntamento = new Appuntamento();

        newAppuntamento.setId(oldAppuntamento.getId());
        newAppuntamento.setNome(nomeField.getText());
        newAppuntamento.setCognome(cognomeField.getText());
        newAppuntamento.setTrattamento(trattamentoField.getText());
        newAppuntamento.setData(dataPicker.getValue());
        String oreParseString = (oraField.getValue() < 10) ? "0" + String.valueOf(oraField.getValue())
                : String.valueOf(oraField.getValue());
        String minutiParseString = (minutoField.getValue() < 10) ? "0" + String.valueOf(minutoField.getValue())
                : String.valueOf(minutoField.getValue());
        String inizioOraParsato = oreParseString + ":" + minutiParseString;
        newAppuntamento.setOraInizio(inizioOraParsato);
        newAppuntamento.setDurata(Double.parseDouble(durataField.getText().substring(0, 3)));
        newAppuntamento.setPrezzo(Double.parseDouble(prezzo.getText().replace(",", ".")));

 
        if (newAppuntamento.getNome().isEmpty() || newAppuntamento.getCognome().isEmpty()
                || newAppuntamento.getTrattamento().isEmpty()
                || newAppuntamento.getData() == null) {
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

        if (newAppuntamento.getDurata() > 21 - newAppuntamento.getDurata() || oraField.getValue() < 7) {
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

        if (minutoField.getValue() % 5 != 0) {
            System.out.println("Errore: il campo minuti accetta solo valori multipli di 5, oppure il valore 0.");
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
        int contatore = 0;

        boolean notDisponibile = DatabaseManager.aggiornaAppuntamento(newAppuntamento, oldAppuntamento, contatore);

        if (notDisponibile == true) {

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
        try {
            App.setRoot("home");
        } catch (IOException ex) {
        }

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void tornaHome(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void selezionaTrattamento(ActionEvent e) {
        Trattamento laminazione = new Trattamento("Laminazione", 1, 28);
        Trattamento semiPermanente = new Trattamento("Semi", 1.5, 25);
        Trattamento[] listaTrattamenti = { laminazione, semiPermanente };
        MenuItem selectedItem = (MenuItem) e.getSource();

        String selectedText = selectedItem.getText();
        DecimalFormat df = new DecimalFormat("#.00");

        trattamentoField.setText(selectedText);
        for (int i = 0; i < listaTrattamenti.length; i++) {
            if (selectedText.equals(listaTrattamenti[i].getNome())) {
                durataField.setText(String.valueOf(listaTrattamenti[i].getDurata()) + " Ore");
                prezzo.setText(df.format(String.valueOf(listaTrattamenti[i].getPrezzo())));

                break;
            }
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ControllerModificaAppuntamento.appuntamentoCorrente != null) {
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

            DecimalFormat df = new DecimalFormat("#.00");

            Integer oraConvertita = Integer
                    .parseInt(ControllerModificaAppuntamento.appuntamentoCorrente.getOraInizio().substring(0, 2));
            Integer minutoConvertito = Integer
                    .parseInt(ControllerModificaAppuntamento.appuntamentoCorrente.getOraInizio().substring(3, 5));

            nomeField.setText(ControllerModificaAppuntamento.appuntamentoCorrente.getNome());
            cognomeField.setText(ControllerModificaAppuntamento.appuntamentoCorrente.getCognome());
            trattamentoField.setText(ControllerModificaAppuntamento.appuntamentoCorrente.getTrattamento());
            dataPicker.setValue(ControllerModificaAppuntamento.appuntamentoCorrente.getData());
            oraField.setEditable(true);
            oraField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, oraConvertita));
            minutoField.setEditable(true);
            minutoField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, minutoConvertito));
            durataField.setText(String.valueOf(ControllerModificaAppuntamento.appuntamentoCorrente.getDurata() + " Ore"));
            prezzo.setText(String.valueOf(df.format(appuntamentoCorrente.getPrezzo())));
            completatoCheckBox.isSelected();

        } else {
            System.err.println("Errore nullpointer");
        }

    }

}

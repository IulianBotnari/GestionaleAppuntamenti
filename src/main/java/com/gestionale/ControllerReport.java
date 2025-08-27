package com.gestionale;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerReport implements Initializable {

    @FXML
    private MenuButton selezionaAnnoFatturato;
    @FXML
    private MenuButton selezionaMeseFatturato;

    @FXML
    private MenuButton selezionaAnnoNero;
    @FXML
    private MenuButton selezionaMeseNero;

    @FXML
    private Button salva;

    @FXML
    public void salvaFileFatturato(ActionEvent event) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva il tuo file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File Excel (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {

            return;
        }

        List<TabellaFatturato> datiFatturato = DatabaseManager.getTabellaFatturato(selezionaAnnoFatturato.getText(),
                selezionaMeseFatturato.getText());
        List<TabellaCosti> datiCosti = DatabaseManager.recuperaTabellaCosti(selezionaAnnoFatturato.getText(),
                selezionaMeseFatturato.getText());

        try (InputStream fis = getClass().getResourceAsStream("/com/gestionale/ModelloFatturato.xlsx");
                Workbook modello = new XSSFWorkbook(fis);
                FileOutputStream outputStream = new FileOutputStream(file)) {

            Sheet foglio = modello.getSheetAt(0);

            int numeroRigaCorrente = 1;

            for (TabellaFatturato dato : datiFatturato) {
                Row riga = foglio.getRow(numeroRigaCorrente);

                Cell cellConteggio = riga.getCell(0);
                cellConteggio.setCellValue(1);
                Cell cellData = riga.getCell(1);
                cellData.setCellValue(
                        dato.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                Cell cellNome = riga.getCell(2);
                cellNome.setCellValue(dato.getNome());
                Cell cellCognome = riga.getCell(3);
                cellCognome.setCellValue(dato.getCognome());
                Cell cellLaminazione = riga.getCell(4);
                cellLaminazione.setCellValue(dato.getLaminazione());
                Cell cellUnghie = riga.getCell(5);
                cellUnghie.setCellValue(dato.getUnghie());
                Cell cellDurata = riga.getCell(6);
                cellDurata.setCellValue(Double.parseDouble(dato.getDurata()));
                Cell cellIncasso = riga.getCell(7);
                cellIncasso.setCellValue(dato.getPrezzo());

                numeroRigaCorrente++;
            }

            numeroRigaCorrente = 1;

            for (TabellaCosti dato : datiCosti) {
                Row riga = foglio.getRow(numeroRigaCorrente);

                Cell cellDescrizione = riga.getCell(9);
                cellDescrizione.setCellValue(dato.getDescrizione());
                Cell cellImporto = riga.getCell(10);
                cellImporto.setCellValue(dato.getImporto());

                numeroRigaCorrente++;
            }

            FormulaEvaluator evaluator = modello.getCreationHelper().createFormulaEvaluator();
            for (Row riga : foglio) {
                for (Cell cella : riga) {
                    if (cella.getCellType() == CellType.FORMULA) {
                        evaluator.evaluateFormulaCell(cella);
                    }
                }
            }

            modello.write(outputStream);

        } catch (Exception e) {
            System.err.println("Errore durante la creazione del file Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void salvaFileNero(ActionEvent event) throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva il tuo file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("File Excel (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file == null) {

            return;
        }

        List<TabellaNero> datiFatturato = DatabaseManager.getTabellaNero(selezionaAnnoNero.getText(),
                selezionaMeseNero.getText());

        try (InputStream fis = getClass().getResourceAsStream("/com/gestionale/ModelloFatturatoNero.xlsx");
                Workbook modello = new XSSFWorkbook(fis);
                FileOutputStream outputStream = new FileOutputStream(file)) {

            Sheet foglio = modello.getSheetAt(0);

            int numeroRigaCorrente = 1;

            for (TabellaNero dato : datiFatturato) {
                Row riga = foglio.getRow(numeroRigaCorrente);

                Cell cellConteggio = riga.getCell(0);
                cellConteggio.setCellValue(1);
                Cell cellData = riga.getCell(1);
                cellData.setCellValue(
                        dato.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                Cell cellNome = riga.getCell(2);
                cellNome.setCellValue(dato.getNome());
                Cell cellCognome = riga.getCell(3);
                cellCognome.setCellValue(dato.getCognome());
                Cell cellLaminazione = riga.getCell(4);
                cellLaminazione.setCellValue(dato.getLaminazione());
                Cell cellUnghie = riga.getCell(5);
                cellUnghie.setCellValue(dato.getUnghie());
                Cell cellDurata = riga.getCell(6);
                cellDurata.setCellValue(Double.parseDouble(dato.getDurata()));
                Cell cellIncasso = riga.getCell(7);
                cellIncasso.setCellValue(dato.getPrezzo());

                numeroRigaCorrente++;
            }

            numeroRigaCorrente = 1;

            FormulaEvaluator evaluator = modello.getCreationHelper().createFormulaEvaluator();
            for (Row riga : foglio) {
                for (Cell cella : riga) {
                    if (cella.getCellType() == CellType.FORMULA) {
                        evaluator.evaluateFormulaCell(cella);
                    }
                }
            }

            modello.write(outputStream);

        } catch (Exception e) {
            System.err.println("Errore durante la creazione del file Excel: " + e.getMessage());
            e.printStackTrace();
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

        int annoCorrenteFatturato = LocalDate.now().getYear();

        for (int i = annoCorrenteFatturato; i < annoCorrenteFatturato + 5; i++) {
            String annoString = String.valueOf(i);
            MenuItem newItem = new MenuItem();
            newItem.setText(annoString);

            newItem.setOnAction(event -> {

                selezionaAnnoFatturato.setText(annoString);
            });

            selezionaAnnoFatturato.getItems().add(newItem);
        }
        String[] mesiFatturato = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
                "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre" };

        Platform.runLater(() -> {
            for (String nomeMese : mesiFatturato) {

                MenuItem newItem = new MenuItem(nomeMese);

                newItem.setOnAction(event -> {

                    selezionaMeseFatturato.setText(newItem.getText());
                });

                selezionaMeseFatturato.getItems().add(newItem);
            }
        });

        int annoCorrenteNero = LocalDate.now().getYear();

        for (int i = annoCorrenteNero; i < annoCorrenteNero + 5; i++) {
            String annoString = String.valueOf(i);
            MenuItem newItem = new MenuItem();
            newItem.setText(annoString);

            newItem.setOnAction(event -> {

                selezionaAnnoNero.setText(annoString);
            });

            selezionaAnnoNero.getItems().add(newItem);
        }
        String[] mesiNero = { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
                "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre" };

        Platform.runLater(() -> {
            for (String nomeMese : mesiNero) {

                MenuItem newItem = new MenuItem(nomeMese);

                newItem.setOnAction(event -> {

                    selezionaMeseNero.setText(newItem.getText());
                });

                selezionaMeseNero.getItems().add(newItem);
            }
        });
    }
}

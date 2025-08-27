package com.gestionale;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;

public class DatabaseManager {

    private static final String APP_FOLDER = "AppGestionale";
    private static final String DB_NAME = "database.db";

    //metodo per recuperare una path utile dove poter creare il database senza incorrere a problemi legati ai permessi di scrittura su disco
    public static String getConnection() throws SQLException {
        String userHome = System.getProperty("user.home");
        File appDir = new File(userHome, APP_FOLDER);

        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String dbPath = appDir.getAbsolutePath() + File.separator + DB_NAME;
        String url = "jdbc:sqlite:" + dbPath;

        return url;
    }

    public DatabaseManager() {

    }

    /**
     * {@code createNewDatabase} metodo che viene chiamato quando si inserisce un
     * nuovo appuntamento
     * Il database viene creato una sola volta al primo inserimento di un
     * appuntamento
     */
    public static void createNewDatabase() {
        try (Connection connect = DriverManager.getConnection(getConnection())) {
            if (connect != null) {
                System.out.println("Database già esistente");
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione del database: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
    }

    /**
     * {@code createNewTable} metodo che viene invocato per creare una tabella
     * appuntamenti,
     * viene evocato ogni volta che si inserisce un appuntamento
     */
    public static void createNewTable() {

        // istruzione sql per il database, crea la tabella appuntamenti se non esiste
        String sql = "CREATE TABLE IF NOT EXISTS appuntamenti (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " nome TEXT NOT NULL,\n"
                + " cognome TEXT NOT NULL,\n"
                + " trattamento TEXT NOT NULL,\n"
                + " orarioinizio TEXT NOT NULL,\n"
                + " durata TEXT NOT NULL,\n"
                + " data TEXT NOT NULL,\n"
                + " prezzo TEXT NOT NULL,\n"
                + " fatturato INTEGER,\n"
                + " nero INTEGER,\n"
                + " completato INTEGER);";

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect.prepareStatement(sql)) {
            cmd.execute();
        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
    }

    public static void creaTabellaDisponibilità() {

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS disponibilita (\n" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                                "data TEXT, \n" +
                                "idbloccoora INTEGER, \n" +
                                "ora TEXT, \n" +
                                "occupato INTEGER DEFAULT 0\n" +
                                ");")) {

            cmd.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella disponibilità: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
    }

    /**
     * {@code insertNewAppuntamento} viene chiamato ogni volta che viene inserito un
     * appuntamento
     * 
     * @param nome            fa parte della classe appuntamento
     * @param cognome         fa parte della classe appuntamento
     * @param trattamento     fa parte della classe appuntamento
     * @param oraInizio       fa parte della classe appuntamento
     * @param durata          fa parte della classe appuntamento
     * @param data            fa parte della classe appuntamento
     * @param contatore       fa parte della classe appuntamento
     * @param dataParseString dato di tipo string utile per dare il nome alla
     *                        tabella disponibilita , contiene la data
     *                        dell'appuntamento formattata YYYYMMDD, viene aggiunta
     *                        una D all'inizio della data
     *                        perchè sqlite non accetta tabelle il cui nome inizia
     *                        con una cifra, quindi il formato finale sara DYYYYMMDD
     */
    public static boolean insertNewAppuntamento(String nome, String cognome, String trattamento, String oraInizio,
            double durata, LocalDate data, double prezzo, int fatturato, int nero, int completato, int contatore) {

        Appuntamento newAppuntamento = new Appuntamento();
        newAppuntamento.setNome(nome);
        newAppuntamento.setCognome(cognome);
        newAppuntamento.setTrattamento(trattamento);
        newAppuntamento.setOraInizio(oraInizio);
        newAppuntamento.setDurata(durata);
        newAppuntamento.setData(data);
        newAppuntamento.setPrezzo(prezzo);
        newAppuntamento.setFatturato(fatturato);
        newAppuntamento.setNero(nero);
        newAppuntamento.setCompletato(completato);

        boolean notDisponibile = DatabaseManager.verificaDisponibilita(newAppuntamento);

        if (notDisponibile == true) {
            System.out.println("Impossibile aggiungere appuntamento, posto già occupato");

            return true;

        }

        // primo blocco per inserire i dati dell'appuntamento
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect.prepareStatement(
                        "insert into appuntamenti (nome, cognome, trattamento, orarioinizio, durata, data, prezzo, fatturato, nero, completato) values (?,?,?,?,?,?,?,?,?,?)")) {
            cmd.setString(1, nome);
            cmd.setString(2, cognome);
            cmd.setString(3, trattamento);
            cmd.setString(4, oraInizio);
            cmd.setDouble(5, durata);
            cmd.setObject(6, data);
            cmd.setDouble(7, prezzo);
            cmd.setDouble(8, fatturato);
            cmd.setDouble(9, nero);
            cmd.setDouble(10, completato);
            cmd.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento dell'appuntamento: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        String dataParseString = "D" + data.format(DateTimeFormatter.BASIC_ISO_DATE);

        /**
         * blocco try/catch che crea la tabella disponibilita se non esiste
         */
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect.prepareStatement(
                        "CREATE TABLE IF NOT EXISTS disponibilita (\n" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
                                "data TEXT, \n" +
                                "idbloccoora INTEGER, \n" +
                                "ora TEXT, \n" +
                                "occupato INTEGER DEFAULT 0\n" +
                                ");")) {

            cmd.executeUpdate();

            /**
             * blocco try/catch che verifica se la tabella siponibilita esiste, nel caso si
             * utilizza un doppio ciclo for, uno per le ore,
             * un altro per i minuti che creera righe o blocchi di 5 minuti tanti quanti ne
             * stanno dentro una giornata lavorativa, ogni blocco avra lo stato
             * occupato = 0 di default, che significa "libero".
             */
            try (PreparedStatement veificaTabella = connect
                    .prepareStatement("select * from disponibilita where data = '" + dataParseString + "'")) {

                ResultSet result = veificaTabella.executeQuery();

                if (!result.next()) {

                    /**
                     * ciclo che si occupera di far visualizzare una lista che rappresentera la
                     * disponibilita dalle ore 7 (variabile i) alle ore 21 (condizione booleana
                     * ciclo)
                     */
                    for (int i = 7; i <= 21; i++) {
                        /**
                         * secondo ciclo che andra a creare una lista delle disponibilita ogni ora la
                         * variabile j parte da 0 che rappresente i minuti 00, la condizione booleana
                         * raappresenta i minuti in un ora, il ciclo si ripete 12 volte per
                         * rapprensetare una lista con un elemento avente il valore di 5 minuti
                         */

                        for (int j = 0; j < 60; j += 5) {

                            try (
                                    PreparedStatement newConnection = connect.prepareStatement(
                                            "INSERT INTO disponibilita (data, idbloccoora, ora) VALUES (?,?,?)")) {

                                // le seguenti 3 stringhe rappresentano un modello di conversione dell'ora da
                                // formato int:int a string hh:mm
                                String ora = (i < 10) ? "0" + String.valueOf(i) : String.valueOf(i);
                                String minuti = (j < 10) ? "0" + String.valueOf(j) : String.valueOf(j);
                                String stringaFormatatta = ora + ":" + minuti;
                                newConnection.setString(1, dataParseString);
                                newConnection.setInt(2, contatore);
                                newConnection.setString(3, stringaFormatatta);

                                newConnection.executeUpdate();

                            } catch (SQLException e) {
                                System.err.println(
                                        "Errore durante la creazione della tabella delle disponibilita: "
                                                + e.getMessage());
                            } catch (Exception e) {
                                System.err.println("Errore generico: " + e.getMessage());
                            }

                            contatore++;

                        }
                    }

                    contatore = 1;
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Errore generico: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella delle disponibilita: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        String recuperoOra = oraInizio.substring(0, 2); // prende il dato oraInizio in formato hh:mm, elimina :mm e
                                                        // trattiene solo hh
        String recuperoMinuto = oraInizio.substring(3, 5); // prende il dato oraInizio in formato hh:mm, elimina hh: e
                                                           // trattiene solo mm
        int parsaOra = Integer.parseInt(recuperoOra); // trasforma @param recuperoOra in formato intero
        int parsaMinuto = Integer.parseInt(recuperoMinuto); // trasforma @param recuperoMinuto in formato intero
        int convertiOraInId = (parsaOra - 7) * 12; /**
                                                    * prende @param parsaOra fa una sottrazione di 7(che rappresenta
                                                    * l'orario di inizio in ore) e si moltiplica
                                                    * per 12 questo perchè in riferimento ai blocchi da 5 minuti ogni
                                                    * ora va rappresentata con dodici blocchi da 5 minuti ciascuno,
                                                    * servira per far sapere quali blocchi
                                                    * nella tabella disponibilita devono avere lo stato OCCUPATO = 1
                                                    */

        int convertiMinutoInId = 12 - ((60 - parsaMinuto) / 5); // stesso discorso delle ore ma applicato ai minuti
        int oraInizioConvertita = convertiMinutoInId + convertiOraInId; /**
                                                                         * somma tra @param convertiMinutoInId @param
                                                                         * convertiOraInId che dara come risultato l'id
                                                                         * corretto
                                                                         * dal quale partire per assegnare alla colonna
                                                                         * OCCUPATO nella tabella disponibilità lo stato
                                                                         * "1"
                                                                         */
        double durataConvertita = durata * 12; /**
                                                * conversione in numero intero della durata di ogni trattamento es: 1.5
                                                * ore moltiplicato * 12 = 18, 18 sono i blocchi successivi
                                                * al @param oraInizioConvertita che dovranno avere lo stato OCCUPATO = 1
                                                */
        int oraFineConvertita = oraInizioConvertita + (int) durataConvertita;

        /**
         * Blocco try/catch che specifica quali blocchi dovranno avere valore 1 nella
         * tabella disponibilita, colonna occupato il valore di 1
         */
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect
                        .prepareStatement("update disponibilita set occupato = 1 where idbloccoora >='"
                                + oraInizioConvertita + "' and idbloccoora <'" + oraFineConvertita + "' and data = '"
                                + dataParseString + "'")) {
            cmd.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Errore durante il settaggio occupoato nella tabella delle disponibilità: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
        return false;
    }

    /**
     * {@code getDisponibilita} metodo per visualizzare nella pagina inserisci le
     * disponibilita in base al giorno selezionato
     * 
     * @param datePicker      elemento che rappresenta la data selezionata che per
     *                        la quale l'utente vorra vedere i posti disponibili per
     *                        prendere un appuntamento
     * @param dataParseString dato di tipo string utile selezionare la
     *                        tabella disponibilita , contiene la data
     *                        dell'appuntamento formattata YYYYMMDD, viene aggiunta
     *                        una D all'inizio della data
     *                        perchè sqlite non accetta tabelle il cui nome inizia
     *                        con una cifra, quindi il formato finale sara DYYYYMMDD
     * @param sql             query sql per selezionare le righe dentro la tabella
     *                        disponibilita secondo questa logica: verifica lo stato
     *                        OCCUPATO, ogni volta che c'è un
     *                        cambiamento dello stato crea un gruppo di righe prima
     *                        e dopo l'ultimo cambiamento di stato, di questo gruppo
     *                        viene selezionata solo la prima e l'ultima
     *                        rig successivamente le si ordina in base all id in
     *                        maniera crescenete, cosi da visualizzare gli
     *                        intervalli tra le ore disponibili ed eventualmente
     *                        occupate
     *                        nella tabella disponibilita
     * 
     * @return ritorna un ObservableList {@link javafx.collections.ObservableList}
     *         del tutto simile a una List di {@link java.util.List} con la
     *         differenza che
     *         l'interfaccia in questione offre la possibilita, quando una
     *         ObservableList e collegata a una ListView
     *         {@link javafx.scene.control.ListView} in questo caso
     *         la list view verra aggiornata all'istatnte ogni qualvolta ci sara un
     *         cambiamento nella ObservableList in questione.
     */
    public static ObservableList<Disponibilita> getDisponibilita(DatePicker datePicker) {
        LocalDate data = datePicker.getValue();
        String dataParseString = "D" + data.format(DateTimeFormatter.BASIC_ISO_DATE);
        String sql = "WITH Blocks AS (SELECT id, data, idbloccoora, ora, occupato, ROW_NUMBER() OVER (ORDER BY ora) - ROW_NUMBER() OVER (PARTITION BY occupato ORDER BY ora) AS grp FROM disponibilita WHERE data = '"
                + dataParseString + "'), "
                + "RankedBlocks AS (SELECT id, data, idbloccoora, ora, occupato, ROW_NUMBER() OVER (PARTITION BY occupato, grp ORDER BY ora) AS rn_asc,ROW_NUMBER() OVER (PARTITION BY occupato, grp ORDER BY ora DESC) AS rn_desc FROM Blocks) SELECT id, data, idbloccoora, ora, occupato FROM RankedBlocks where rn_asc = 1 OR rn_desc = 1 ORDER BY idbloccoora;";

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect.prepareStatement(sql)) {

            ResultSet result = cmd.executeQuery();

            ObservableList<Disponibilita> listaDisponibilita = FXCollections.observableArrayList();

            while (result.next()) {
                Disponibilita nuovaDisponibilita = new Disponibilita();
                nuovaDisponibilita.setId(result.getInt("id"));
                nuovaDisponibilita.setData((result.getString("data")));
                nuovaDisponibilita.setIdBloccoOra(result.getInt("idbloccoora"));
                nuovaDisponibilita.setOra(result.getString("ora"));
                nuovaDisponibilita.setOccupato(result.getInt("occupato"));
                listaDisponibilita.add(nuovaDisponibilita);

            }

            if (listaDisponibilita.isEmpty()) {
                Disponibilita newDisponibilita1 = new Disponibilita(1, null, 0, "07:00", 0);
                Disponibilita newDisponibilita2 = new Disponibilita(1, null, 0, "21:55", 0);
                listaDisponibilita.add(newDisponibilita1);
                listaDisponibilita.add(newDisponibilita2);

            }

            return listaDisponibilita;

        } catch (SQLException e) { // Cattura specificamente SQLException per problemi DB
            System.err.println("ERRORE DATABASE nel recupero delle disponibilità: " + e.getMessage());
            e.printStackTrace(); // Stampa lo stack trace completo per debugging
            return FXCollections.observableArrayList(); // Ritorna una lista vuota in caso di errore
        } catch (Exception e) { // Cattura altri tipi di eccezioni
            System.err.println("ERRORE GENERICO nel recupero delle disponibilità: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }

    }

    /**
     * 
     * metodo per recuperae la lista degli appuntamenti che verrano visualizzati
     * nella Home, il @param date serve eventualmente a filtrare gli appuntamenti
     * per una data specifica all'interno del metdo c'è un espressione condizionale
     * che verifica se @param date e nullo o meno cosi da visualizzare in pagina
     * la lista degli appuntamenti desiderata, in entrambi i casi gli appuntamenti
     * vengono ordinati prima per data e poi in base all'orario.
     * 
     * @return ritorna un ObservableList<Appuntamento> con la lista degli
     *         appuntament
     */
    public static ObservableList<Appuntamento> getDbAppuntamentiList(LocalDate date) {
        if (date == null) {
            try (Connection connect = DriverManager.getConnection(getConnection());
                    PreparedStatement cmd = connect
                            .prepareStatement("select * from appuntamenti order by data asc, orarioinizio asc")) {
                cmd.execute();

                ResultSet data = cmd.getResultSet();

                ObservableList<Appuntamento> appuntamentiList = FXCollections.observableArrayList();

                while (data.next()) {
                    Appuntamento newAppuntamento = new Appuntamento();
                    newAppuntamento.setId(data.getInt("id"));
                    newAppuntamento.setNome(data.getString("nome"));
                    newAppuntamento.setCognome(data.getString("cognome"));
                    newAppuntamento.setTrattamento(data.getString("trattamento"));
                    newAppuntamento.setOraInizio(data.getString("orarioinizio"));
                    newAppuntamento.setDurata(Double.parseDouble(data.getString("durata")));
                    newAppuntamento.setData(LocalDate.parse(data.getString("data")));
                    newAppuntamento.setPrezzo(Double.parseDouble(data.getString("prezzo")));
                    newAppuntamento.setFatturato(data.getInt("fatturato"));
                    newAppuntamento.setNero(data.getInt("nero"));
                    newAppuntamento.setCompletato(data.getInt("completato"));

                    appuntamentiList.add(newAppuntamento);

                }
                return appuntamentiList;

            } catch (SQLException e) {

                System.err.println("Erore durante il recupero dei dati nel data base: riga 405" + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erore generico il recupero dei dati nel data base: riga 407 " + e.getMessage());
            }
        } else {

            String day = String.valueOf((date.getDayOfMonth() < 10) ? "0" + String.valueOf(date.getDayOfMonth())
                    : String.valueOf(date.getDayOfMonth()));
            String mounth = String.valueOf((date.getMonthValue() < 10) ? "0" + String.valueOf(date.getMonthValue())
                    : String.valueOf(date.getMonthValue()));
            String year = String.valueOf(date.getYear());

            String foramttedDateForSql = year + "-" + mounth + "-" + day;

            try (Connection connect = DriverManager.getConnection(getConnection());
                    PreparedStatement cmd = connect
                            .prepareStatement("select * from appuntamenti where data='" + foramttedDateForSql
                                    + "' order by data asc, orarioinizio asc")) {
                cmd.execute();

                ResultSet data = cmd.getResultSet();

                ObservableList<Appuntamento> appuntamentiList = FXCollections.observableArrayList();

                while (data.next()) {
                    Appuntamento newAppuntamento = new Appuntamento();
                    newAppuntamento.setId(data.getInt("id"));
                    newAppuntamento.setNome(data.getString("nome"));
                    newAppuntamento.setCognome(data.getString("cognome"));
                    newAppuntamento.setTrattamento(data.getString("trattamento"));
                    newAppuntamento.setOraInizio(data.getString("orarioinizio"));
                    newAppuntamento.setDurata(Double.parseDouble(data.getString("durata")));
                    newAppuntamento.setData(LocalDate.parse(data.getString("data")));
                    newAppuntamento.setPrezzo(Double.parseDouble(data.getString("prezzo")));
                    newAppuntamento.setFatturato(data.getInt("fatturato"));
                    newAppuntamento.setNero(data.getInt("nero"));
                    newAppuntamento.setCompletato(data.getInt("completato"));

                    appuntamentiList.add(newAppuntamento);

                }
                return appuntamentiList;

            } catch (SQLException e) {

                System.err.println("Erore durante il recupero dei dati nel data base: riga 450" + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erore generico il recupero dei dati nel data base: riga 452" + e.getMessage());
            }
        }
        return null;
    }

    /**
     * metodo per eliminare un appuntamento, il primo try/catch contiene il codice
     * per eliminare l'appuntamento dalla tabella appuntamenti
     * il secondo per per modificare nella tabella disponibilita lo stato della
     * colonna OCCUPATO portandolo a 0(Libero)
     */
    public static void eliminaAppuntamento(int id, LocalDate data, ListView<Appuntamento> listAppuntamenti,
            String oraInizio, double durata) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("DELETE from appuntamenti where id = " + "'" + id + "'")) {

            int affectedRows = prst.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Appuntamento eliminato");

            }

        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione del appuntamento " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
        String dataParseString = "D" + data.format(DateTimeFormatter.BASIC_ISO_DATE);
        String recuperoOra = oraInizio.substring(0, 2);
        String recuperoMinuto = oraInizio.substring(3, 5);
        int parsaOra = Integer.parseInt(recuperoOra);
        int parsaMinuto = Integer.parseInt(recuperoMinuto);
        int convertiOraInId = (parsaOra - 7) * 12;
        int convertiMinutoInId = 12 - ((60 - parsaMinuto) / 5);
        int oraInizioConvertita = convertiMinutoInId + convertiOraInId;
        double durataConvertita = durata * 12;
        int oraFineConvertita = oraInizioConvertita + (int) durataConvertita;

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect
                        .prepareStatement("update disponibilita set occupato = 0 where data = '" + dataParseString
                                + "' and idbloccoora >='"
                                + oraInizioConvertita + "' and idbloccoora <'" + oraFineConvertita + "'")) {
            cmd.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Errore durante il settaggio occupoato nella tabella delle disponibilità: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        Platform.runLater(() -> {
            listAppuntamenti.setItems(DatabaseManager.getDbAppuntamentiList(null));

        });

    }

    public static boolean verificaDisponibilita(Appuntamento appuntamento) {

        String dataParseString = "D" + appuntamento.getData().format(DateTimeFormatter.BASIC_ISO_DATE);
        String recuperoOra = appuntamento.getOraInizio().substring(0, 2);
        String recuperoMinuto = appuntamento.getOraInizio().substring(3, 5);
        int parsaOra = Integer.parseInt(recuperoOra);
        int parsaMinuto = Integer.parseInt(recuperoMinuto);
        int convertiOraInId = (parsaOra - 7) * 12;
        int convertiMinutoInId = 12 - ((60 - parsaMinuto) / 5);
        int oraInizioConvertita = convertiMinutoInId + convertiOraInId;
        double durataConvertita = appuntamento.getDurata() * 12;
        int oraFineConvertita = oraInizioConvertita + (int) durataConvertita;
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("select * from disponibilita where idbloccoora >= '" + oraInizioConvertita
                                + "' and idbloccoora <'" + oraFineConvertita + "' and data = '" + dataParseString
                                + "' and occupato = 1")) {

            ResultSet result = prst.executeQuery();
            if (result.next()) {
                System.out.println("Righe in verifica disponibilita" + result.next());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Errore nella verifica delle disponibilità: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Errore generico delle disponibilità: " + e.getMessage());
        }

        return false;

    }

    public static boolean aggiornaAppuntamento(Appuntamento newAppuntamento, Appuntamento oldAppuntamento,
            int contatore) {

        String dataParseString = "D" + oldAppuntamento.getData().format(DateTimeFormatter.BASIC_ISO_DATE);
        String recuperoOra = oldAppuntamento.getOraInizio().substring(0, 2);
        String recuperoMinuto = oldAppuntamento.getOraInizio().substring(3, 5);
        int parsaOra = Integer.parseInt(recuperoOra);
        int parsaMinuto = Integer.parseInt(recuperoMinuto);
        int convertiOraInId = (parsaOra - 7) * 12;
        int convertiMinutoInId = 12 - ((60 - parsaMinuto) / 5);
        int oraInizioConvertita = convertiMinutoInId + convertiOraInId;
        double durataConvertita = oldAppuntamento.getDurata() * 12;
        int oraFineConvertita = oraInizioConvertita + (int) durataConvertita;

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect
                        .prepareStatement("update disponibilita set occupato = 0 where data = '" + dataParseString
                                + "' and idbloccoora >= "
                                + oraInizioConvertita + " and idbloccoora < " + oraFineConvertita)) {
            System.out.println("Numero righe settate su 0: " + cmd.executeUpdate());

            boolean notDisponibile = DatabaseManager.verificaDisponibilita(newAppuntamento);

            if (notDisponibile == true) {
                connect.rollback();
                return true;

            }

        } catch (SQLException e) {
            System.err.println(
                    "Errore durante il settaggio occupoato nella tabella delle disponibilità: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        boolean notDisponibile = DatabaseManager.verificaDisponibilita(newAppuntamento);

        if (notDisponibile == true) {
            return true;
        }

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement(
                        "update appuntamenti set nome = ?, cognome = ?, trattamento = ?, orarioinizio = ?, durata = ?, data = ?, prezzo = ? where id = ? ")) {

            prst.setString(1, newAppuntamento.getNome());
            prst.setString(2, newAppuntamento.getCognome());
            prst.setString(3, newAppuntamento.getTrattamento());
            prst.setString(4, newAppuntamento.getOraInizio());
            prst.setDouble(5, newAppuntamento.getDurata());
            prst.setObject(6, newAppuntamento.getData());
            prst.setDouble(7, newAppuntamento.getPrezzo());
            prst.setInt(8, newAppuntamento.getId());

            prst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiornamento dell'appuntamento: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        dataParseString = "D" + newAppuntamento.getData().format(DateTimeFormatter.BASIC_ISO_DATE);
        recuperoOra = newAppuntamento.getOraInizio().substring(0, 2);
        recuperoMinuto = newAppuntamento.getOraInizio().substring(3, 5);
        parsaOra = Integer.parseInt(recuperoOra);
        parsaMinuto = Integer.parseInt(recuperoMinuto);
        convertiOraInId = (parsaOra - 7) * 12;
        convertiMinutoInId = 12 - ((60 - parsaMinuto) / 5);
        oraInizioConvertita = convertiMinutoInId + convertiOraInId;
        durataConvertita = newAppuntamento.getDurata() * 12;
        oraFineConvertita = oraInizioConvertita + (int) durataConvertita;

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement veificaTabella = connect
                        .prepareStatement("select * from disponibilita where data = '" + dataParseString + "'")) {

            ResultSet result = veificaTabella.executeQuery();

            if (!result.next()) {

                /**
                 * ciclo che si occupera di far visualizzare una lista che rappresentera la
                 * disponibilita dalle ore 7 (variabile i) alle ore 21 (condizione booleana
                 * ciclo)
                 */
                for (int i = 7; i <= 21; i++) {
                    /**
                     * secondo ciclo che andra a creare una lista delle disponibilita ogni ora la
                     * variabile j parte da 0 che rappresente i minuti 00, la condizione booleana
                     * raappresenta i minuti in un ora, il ciclo si ripete 12 volte per
                     * rapprensetare una lista con un elemento avente il valore di 5 minuti
                     */

                    for (int j = 0; j < 60; j += 5) {

                        try (
                                PreparedStatement newConnection = connect.prepareStatement(
                                        "INSERT INTO disponibilita (data, idbloccoora, ora) VALUES (?,?,?)")) {

                            // le seguenti 3 stringhe rappresentano un modello di conversione dell'ora da
                            // formato int:int a string hh:mm
                            String ora = (i < 10) ? "0" + String.valueOf(i) : String.valueOf(i);
                            String minuti = (j < 10) ? "0" + String.valueOf(j) : String.valueOf(j);
                            String stringaFormatatta = ora + ":" + minuti;
                            newConnection.setString(1, dataParseString);
                            newConnection.setInt(2, contatore);
                            newConnection.setString(3, stringaFormatatta);

                            newConnection.executeUpdate();

                        } catch (SQLException e) {
                            System.err.println(
                                    "Errore durante la creazione della tabella delle disponibilita: "
                                            + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Errore generico: " + e.getMessage());
                        }

                        contatore++;
                    }
                }
                contatore = 0;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement cmd = connect
                        .prepareStatement("update disponibilita set occupato = 1 where data = '" + dataParseString
                                + "' and idbloccoora >='"
                                + oraInizioConvertita + "' and idbloccoora <'" + oraFineConvertita + "'")) {
            cmd.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Errore durante il settaggio occupoato nella tabella delle disponibilità: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico: " + e.getMessage());
        }
        return false;
    }

    public static void creaTabellaFatturato() {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("create table if not exists tabellafatturato (" +
                        "id integer primary key autoincrement," +
                        "idappuntamento integer," +
                        "data text," +
                        "nome text," +
                        "cognome text," +
                        "laminazione integer," +
                        "unghie integer," +
                        "durata double," +
                        "prezzo double);")) {

            prst.execute();

        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella 'tabellafatturato'" + e.getMessage());
        } catch (Exception e) {
            System.err
                    .println("Errore generico durante la creazione della tabella 'tabellafatturato'" + e.getMessage());
        }
    }

    public static void creaTabellaNero() {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("create table if not exists tabellanero (" +
                        "id integer primary key autoincrement," +
                        "idappuntamento integer," +
                        "data text," +
                        "nome text," +
                        "cognome text," +
                        "laminazione integer," +
                        "unghie integer," +
                        "durata double," +
                        "prezzo double);")) {

            prst.execute();

        } catch (SQLException e) {
            System.err.println("Errore durante la creazione della tabella 'tabellanero'" + e.getMessage());
        } catch (Exception e) {
            System.err
                    .println("Errore generico durante la creazione della tabella 'tabellanero'" + e.getMessage());
        }
    }

    public static void inserisciTabellaFaturato(Appuntamento appuntamento) {

        int laminazione = 0;
        int unghie = 0;

        if (appuntamento.getTrattamento().equals("Laminazione")) {
            laminazione = 1;
        } else {
            unghie = 1;
        }

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement(
                        "insert into tabellafatturato (data, nome, cognome, laminazione, unghie, durata, prezzo, idappuntamento) values (?,?,?,?,?,?,?,?);")) {

            prst.setObject(1, appuntamento.getData());
            prst.setString(2, appuntamento.getNome());
            prst.setString(3, appuntamento.getCognome());
            prst.setInt(4, laminazione);
            prst.setInt(5, unghie);
            prst.setDouble(6, appuntamento.getDurata());
            prst.setDouble(7, appuntamento.getPrezzo());
            prst.setInt(8, appuntamento.getId());

            prst.executeUpdate();

            System.out.println("Inserimento tabella fatturato eseguito con successo");
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento dati nella tabella 'tabellafatturato'" + e.getMessage());
        } catch (Exception e) {
            System.err.println(
                    "Errore generico durante l'inserimento dati nella tabella 'tabellafatturato'" + e.getMessage());
        }

    }

    public static void inserisciTabellaNero(Appuntamento appuntamento) {

        int laminazione = 0;
        int unghie = 0;

        if (appuntamento.getTrattamento().equals("Laminazione")) {
            laminazione = 1;
        } else {
            unghie = 1;
        }

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement(
                        "insert into tabellanero (data, nome, cognome, laminazione, unghie, durata, prezzo, idappuntamento) values (?,?,?,?,?,?,?,?);")) {

            prst.setObject(1, appuntamento.getData());
            prst.setString(2, appuntamento.getNome());
            prst.setString(3, appuntamento.getCognome());
            prst.setInt(4, laminazione);
            prst.setInt(5, unghie);
            prst.setDouble(6, appuntamento.getDurata());
            prst.setDouble(7, appuntamento.getPrezzo());
            prst.setInt(8, appuntamento.getId());

            prst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento dati nella tabella 'tabellanero'" + e.getMessage());
        } catch (Exception e) {
            System.err.println(
                    "Errore generico durante l'inserimento dati nella tabella 'tabellanero'" + e.getMessage());
        }

    }

    public static List<TabellaFatturato> getTabellaFatturato(String anno, String mese) {

        String meseConvertioInStringaNumerica = "";

        switch (mese) {
            case "Gennaio":
                meseConvertioInStringaNumerica = "01";

                break;

            case "Febbraio":
                meseConvertioInStringaNumerica = "02";
                break;

            case "Marzo":
                meseConvertioInStringaNumerica = "03";
                break;

            case "Aprile":
                meseConvertioInStringaNumerica = "04";
                break;
            case "Maggio":
                meseConvertioInStringaNumerica = "05";
                break;

            case "Giugno":
                meseConvertioInStringaNumerica = "06";
                break;

            case "Luglio":
                meseConvertioInStringaNumerica = "07";
                break;

            case "Agosto":
                meseConvertioInStringaNumerica = "08";
                break;

            case "Settembre":
                meseConvertioInStringaNumerica = "09";
                break;

            case "Ottobre":
                meseConvertioInStringaNumerica = "10";
                break;
            case "Novembre":
                meseConvertioInStringaNumerica = "11";
                break;

            case "Dicembre":
                meseConvertioInStringaNumerica = "12";
                break;

            default:
                break;
        }

        String stringaQuery = anno + "-" + meseConvertioInStringaNumerica;

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("select * from tabellafatturato where strftime('%Y-%m', data) = ?")) {
            prst.setString(1, stringaQuery);

            ResultSet result = prst.executeQuery();
            List<TabellaFatturato> listaTabellaFatturato = new ArrayList<>();

            while (result.next()) {
                TabellaFatturato newTabellaFatturato = new TabellaFatturato();
                newTabellaFatturato.setData(LocalDate.parse(result.getString("data")));
                newTabellaFatturato.setNome(result.getString("nome"));
                newTabellaFatturato.setCognome(result.getString("cognome"));
                newTabellaFatturato.setLaminazione(result.getInt("laminazione"));
                newTabellaFatturato.setUnghie(result.getInt("unghie"));
                newTabellaFatturato.setDurata(result.getString("durata"));
                newTabellaFatturato.setPrezzo(result.getDouble("prezzo"));

                listaTabellaFatturato.add(newTabellaFatturato);
            }

            return listaTabellaFatturato;

        } catch (SQLException e) {
            System.err.println("Errore durante la crezione della lista tabellafatturato: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Errore generico durante la crezionene della lista tabellafatturato: " + e.getMessage());
        }
        return null;
    }

    public static List<TabellaNero> getTabellaNero(String anno, String mese) {

        // String [] listaMesi =
        // {"Gennaio","Febbraio","Marzo","Aprile","Maggio","Giugno","Luglio","Agosto","Settembre","Ottobre","Novembre","Dicembre"};

        String meseConvertioInStringaNumerica = "";

        switch (mese) {
            case "Gennaio":
                meseConvertioInStringaNumerica = "01";

                break;

            case "Febbraio":
                meseConvertioInStringaNumerica = "02";
                break;

            case "Marzo":
                meseConvertioInStringaNumerica = "03";
                break;

            case "Aprile":
                meseConvertioInStringaNumerica = "04";
                break;
            case "Maggio":
                meseConvertioInStringaNumerica = "05";
                break;

            case "Giugno":
                meseConvertioInStringaNumerica = "06";
                break;

            case "Luglio":
                meseConvertioInStringaNumerica = "07";
                break;

            case "Agosto":
                meseConvertioInStringaNumerica = "08";
                break;

            case "Settembre":
                meseConvertioInStringaNumerica = "09";
                break;

            case "Ottobre":
                meseConvertioInStringaNumerica = "10";
                break;
            case "Novembre":
                meseConvertioInStringaNumerica = "11";
                break;

            case "Dicembre":
                meseConvertioInStringaNumerica = "12";
                break;

            default:
                break;
        }

        String stringaQuery = anno + "-" + meseConvertioInStringaNumerica;

        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("select * from tabellanero where strftime('%Y-%m', data) = ?")) {
            prst.setString(1, stringaQuery);

            ResultSet result = prst.executeQuery();
            List<TabellaNero> listaTabellaNero = new ArrayList<>();

            while (result.next()) {
                TabellaNero newTabellaFatturato = new TabellaNero();
                newTabellaFatturato.setData(LocalDate.parse(result.getString("data")));
                newTabellaFatturato.setNome(result.getString("nome"));
                newTabellaFatturato.setCognome(result.getString("cognome"));
                newTabellaFatturato.setLaminazione(result.getInt("laminazione"));
                newTabellaFatturato.setUnghie(result.getInt("unghie"));
                newTabellaFatturato.setDurata(result.getString("durata"));
                newTabellaFatturato.setPrezzo(result.getDouble("prezzo"));

                listaTabellaNero.add(newTabellaFatturato);
            }

            return listaTabellaNero;

        } catch (SQLException e) {
            System.err.println("Errore durante la crezione della lista tabellanero: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Errore generico durante la crezionene della lista tabellanero: " + e.getMessage());
        }
        return null;
    }

    public static void remuoviElementoDaTabellaFatturato(int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("delete from tabellafatturato where idappuntamento = ?")) {
            prst.setInt(1, id);

            prst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'elimanzione nella tabella fatturato: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Errore generico durante l'eliminazione nalla tabella fatturato");
        }
    }

    public static void remuoviElementoDaTabellaNero(int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("delete from tabellanero where idappuntamento = ?")) {
            prst.setInt(1, id);

            prst.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Errore durante l'elimanzione nella tabella nero: " + e.getMessage());

        } catch (Exception e) {
            System.err.println("Errore generico durante l'eliminazione nalla tabella nero");
        }
    }

    public static void setStatoFatturatoInAppuntamento(int stato, int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("update appuntamenti set fatturato = ? where id = ?")) {
            prst.setInt(1, stato);
            prst.setInt(2, id);
            prst.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Errore sql durante l'aggiornamento dello stato fatturato in appuntamenti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(
                    "Errore generico durante l'aggiornamento dello stato fatturato in appuntamenti: " + e.getMessage());
        }
    }

    public static void setStatoNeroInAppuntamento(int stato, int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("update appuntamenti set nero = ? where id = ?")) {
            prst.setInt(1, stato);
            prst.setInt(2, id);
            prst.executeUpdate();
        } catch (SQLException e) {
            System.err
                    .println("Errore sql durante l'aggiornamento dello stato nero in appuntamenti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(
                    "Errore generico durante l'aggiornamento dello stato nero in appuntamenti: " + e.getMessage());
        }
    }

    public static void setStatoCompletatoInAppuntamento(int stato, int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("update appuntamenti set completato = ? where id = ?")) {
            prst.setInt(1, stato);
            prst.setInt(2, id);
            prst.executeUpdate();
        } catch (SQLException e) {
            System.err.println(
                    "Errore sql durante l'aggiornamento dello stato completato in appuntamenti: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico durante l'aggiornamento dello stato completato in appuntamenti: "
                    + e.getMessage());
        }
    }

    public static void creaTabellaCosti() {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("create table if not exists tabellacosti(\n" +
                        "id integer  primary key AUTOINCREMENT," +
                        "descrizione text," +
                        "importo double," +
                        "data text);")) {
            prst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore sql durante la creazione tabella costi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico durante la creazione della tabella costi: " + e.getMessage());
        }
    }

    public static void inserisciTabellaCosti(TabellaCosti tabellaCosti) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("insert into tabellacosti (descrizione, importo, data) values (?,?,?)")) {

            prst.setString(1, tabellaCosti.getDescrizione());
            prst.setDouble(2, tabellaCosti.getImporto());
            prst.setObject(3, tabellaCosti.getData());

            prst.executeUpdate();

        } catch (SQLException e) {

            System.err.println("Errore sql durante l'inserimento dei dati nella tabella costi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico duratne l'inserimento dei dati nella tabella costi: " + e.getMessage());
        }
    }

    public static List<TabellaCosti> recuperaTabellaCosti(String anno, String mese) {

        String meseConvertioInStringaNumerica = "";

        switch (mese) {
            case "Gennaio":
                meseConvertioInStringaNumerica = "01";

                break;

            case "Febbraio":
                meseConvertioInStringaNumerica = "02";
                break;

            case "Marzo":
                meseConvertioInStringaNumerica = "03";
                break;

            case "Aprile":
                meseConvertioInStringaNumerica = "04";
                break;
            case "Maggio":
                meseConvertioInStringaNumerica = "05";
                break;

            case "Giugno":
                meseConvertioInStringaNumerica = "06";
                break;

            case "Luglio":
                meseConvertioInStringaNumerica = "07";
                break;

            case "Agosto":
                meseConvertioInStringaNumerica = "08";
                break;

            case "Settembre":
                meseConvertioInStringaNumerica = "09";
                break;

            case "Ottobre":
                meseConvertioInStringaNumerica = "10";
                break;
            case "Novembre":
                meseConvertioInStringaNumerica = "11";
                break;

            case "Dicembre":
                meseConvertioInStringaNumerica = "12";
                break;

            default:
                break;
        }

        String stringaQuery = anno + "-" + meseConvertioInStringaNumerica;
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect
                        .prepareStatement("select * from tabellacosti where strftime('%Y-%m', data) = ?")) {
            prst.setString(1, stringaQuery);
            ResultSet resultSet = prst.executeQuery();
            List<TabellaCosti> listaTabellaCosti = new ArrayList<>();

            while (resultSet.next()) {
                TabellaCosti tabellaCosti = new TabellaCosti();
                tabellaCosti.setId(resultSet.getInt("id"));
                tabellaCosti.setDescrizione(resultSet.getString("descrizione"));
                tabellaCosti.setImporto(resultSet.getDouble("importo"));
                listaTabellaCosti.add(tabellaCosti);
            }

            return listaTabellaCosti;

        } catch (SQLException e) {

            System.err.println("Errore sql durante il recupero dei dati nella tabella costi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico duratne il recupero dei dati nella tabella costi: " + e.getMessage());
        }
        return null;
    }

    public static void eliminaDatiInTabellaCosti(int id) {
        try (Connection connect = DriverManager.getConnection(getConnection());
                PreparedStatement prst = connect.prepareStatement("delete from tabellacosti where id = ?")) {

            prst.setInt(1, id);

            prst.executeUpdate();

        } catch (SQLException e) {

            System.err.println("Errore sql durante l'elimanzione dei dati nella tabella costi: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Errore generico duratne l'elimanzione dei dati nella tabella costi: " + e.getMessage());
        }
    }

}

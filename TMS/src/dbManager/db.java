package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.sqlite.JDBC; // Importiamo esplicitamente la classe del driver

public class db {

    private static final String URL = "jdbc:sqlite:tms.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            DriverManager.registerDriver(new JDBC());

            conn = DriverManager.getConnection(URL);
            System.out.println("Connessione a SQLite stabilita.");

        } catch (SQLException e) {
            System.err.println("ERRORE CRITICO DB: " + e.getMessage());
            throw new RuntimeException("Impossibile connettersi al database.", e);
        }
        return conn;
    }

    public static void initializeDb() {
        String sqlReparto = "CREATE TABLE IF NOT EXISTS reparto (idReparto INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, codice TEXT UNIQUE, sale INTEGER);";
        String sqlUtente = "CREATE TABLE IF NOT EXISTS utente (idUtente INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT NOT NULL, cognome TEXT NOT NULL, dataNascita TEXT NOT NULL, tipoUtente TEXT NOT NULL);";
        String sqlPaziente = "CREATE TABLE IF NOT EXISTS paziente (idUtente INTEGER PRIMARY KEY, codiceFiscale TEXT UNIQUE NOT NULL, indirizzo TEXT, FOREIGN KEY (idUtente) REFERENCES utente(idUtente));";
        String sqlDottore = "CREATE TABLE IF NOT EXISTS dottore (idUtente INTEGER PRIMARY KEY, matricola TEXT UNIQUE NOT NULL, turni TEXT, idReparto INTEGER, FOREIGN KEY (idUtente) REFERENCES utente(idUtente), FOREIGN KEY (idReparto) REFERENCES reparto(idReparto));";
        String sqlSegretario = "CREATE TABLE IF NOT EXISTS segretario (idUtente INTEGER PRIMARY KEY, livelloPermessi INTEGER, FOREIGN KEY (idUtente) REFERENCES utente(idUtente));";
        String sqlTicket = "CREATE TABLE IF NOT EXISTS ticket (idTicket INTEGER PRIMARY KEY AUTOINCREMENT, colore TEXT, priorita INTEGER, sintomi TEXT, stato TEXT, timestamp TEXT, idPaziente INTEGER NOT NULL, FOREIGN KEY (idPaziente) REFERENCES paziente(idUtente));";
        String sqlVisita = "CREATE TABLE IF NOT EXISTS visita (idVisita INTEGER PRIMARY KEY AUTOINCREMENT, dataOraInizio TEXT, dataOraFine TEXT, sala TEXT, idTicket INTEGER, idDottore INTEGER, idPaziente INTEGER, idReparto INTEGER, FOREIGN KEY (idTicket) REFERENCES ticket(idTicket), FOREIGN KEY (idDottore) REFERENCES dottore(idUtente), FOREIGN KEY (idPaziente) REFERENCES paziente(idUtente), FOREIGN KEY (idReparto) REFERENCES reparto(idReparto));";
        String sqlReferto = "CREATE TABLE IF NOT EXISTS referto (idReferto INTEGER PRIMARY KEY AUTOINCREMENT, diagnosi TEXT, prognosi TEXT, prescrizioni TEXT, dataCreazione TEXT, idVisita INTEGER UNIQUE, FOREIGN KEY (idVisita) REFERENCES visita(idVisita));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlReparto);
            stmt.execute(sqlUtente);
            stmt.execute(sqlPaziente);
            stmt.execute(sqlDottore);
            stmt.execute(sqlSegretario);
            stmt.execute(sqlTicket);
            stmt.execute(sqlVisita);
            stmt.execute(sqlReferto);
            System.out.println("Tabelle del database inizializzate con successo");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
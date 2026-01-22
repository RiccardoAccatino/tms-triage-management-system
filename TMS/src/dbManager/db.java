package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class db {

    // Nome del file del database
    // Verrà creato nella cartella principale del progetto
    private static final String URL = "jdbc:sqlite:tms.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            // 1. FORZIAMO IL CARICAMENTO DEL DRIVER
            // Questo passaggio è spesso necessario quando si eseguono i test da IDE
            Class.forName("org.sqlite.JDBC");

            // 2. Creiamo la connessione
            conn = DriverManager.getConnection(URL);
            System.out.println("Connessione a SQLite stabilita.");

        } catch (ClassNotFoundException e) {
            System.out.println("Errore: Driver SQLite non trovato nel classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Errore di connessione: " + e.getMessage());
        }
        return conn;
    }


    /**
     * Crea le tabelle se non esistono
     */
    public static void initializeDb() {
        // 1. REPARTO (Entità indipendente)
        String sqlReparto = "CREATE TABLE IF NOT EXISTS reparto ("
                + "idReparto INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "codice TEXT UNIQUE, "
                + "sale INTEGER"
                + ");";

        // 2. UTENTE (Superclasse - Strategia Joined Table)
        // Contiene i dati comuni a Paziente, Dottore, Segretario
        String sqlUtente = "CREATE TABLE IF NOT EXISTS utente ("
                + "idUtente INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL, "
                + "cognome TEXT NOT NULL, "
                + "dataNascita TEXT NOT NULL, " // SQLite usa TEXT per le date (ISO8601 strings)
                + "tipoUtente TEXT NOT NULL"     // Per distinguere Paziente/Dottore/Segretario
                + ");";

        // 3. PAZIENTE (Estende Utente)
        String sqlPaziente = "CREATE TABLE IF NOT EXISTS paziente ("
                + "idUtente INTEGER PRIMARY KEY, "
                + "codiceFiscale TEXT UNIQUE NOT NULL, "
                + "indirizzo TEXT, "
                + "FOREIGN KEY (idUtente) REFERENCES utente(idUtente)"
                + ");";

        // 4. DOTTORE (Estende Utente e Appartiene a Reparto)
        String sqlDottore = "CREATE TABLE IF NOT EXISTS dottore ("
                + "idUtente INTEGER PRIMARY KEY, "
                + "matricola TEXT UNIQUE NOT NULL, "
                + "turni TEXT, "
                + "idReparto INTEGER, "
                + "FOREIGN KEY (idUtente) REFERENCES utente(idUtente), "
                + "FOREIGN KEY (idReparto) REFERENCES reparto(idReparto)"
                + ");";

        // 5. SEGRETARIO (Estende Utente)
        String sqlSegretario = "CREATE TABLE IF NOT EXISTS segretario ("
                + "idUtente INTEGER PRIMARY KEY, "
                + "livelloPermessi INTEGER, "
                + "FOREIGN KEY (idUtente) REFERENCES utente(idUtente)"
                + ");";

        // 6. TICKET (Associato a Paziente)
        String sqlTicket = "CREATE TABLE IF NOT EXISTS ticket ("
                + "idTicket INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "colore TEXT, "    // Bianco, Verde, Giallo, Rosso
                + "priorita INTEGER, "
                + "sintomi TEXT, "
                + "stato TEXT, "     // In Attesa, Accettato, Rifiutato
                + "timestamp TEXT, "
                + "idPaziente INTEGER NOT NULL, "
                + "FOREIGN KEY (idPaziente) REFERENCES paziente(idUtente)"
                + ");";

        // 7. VISITA (Associata a Ticket, Dottore, Paziente, Reparto)
        String sqlVisita = "CREATE TABLE IF NOT EXISTS visita ("
                + "idVisita INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "dataOraInizio TEXT, "
                + "dataOraFine TEXT, "
                + "sala TEXT, "
                + "idTicket INTEGER, "
                + "idDottore INTEGER, "
                + "idPaziente INTEGER, "
                + "idReparto INTEGER, "
                + "FOREIGN KEY (idTicket) REFERENCES ticket(idTicket), "
                + "FOREIGN KEY (idDottore) REFERENCES dottore(idUtente), "
                + "FOREIGN KEY (idPaziente) REFERENCES paziente(idUtente), "
                + "FOREIGN KEY (idReparto) REFERENCES reparto(idReparto)"
                + ");";

        // 8. REFERTO (Composizione di Visita)
        String sqlReferto = "CREATE TABLE IF NOT EXISTS referto ("
                + "idReferto INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "diagnosi TEXT, "
                + "prognosi TEXT, "
                + "prescrizioni TEXT, "
                + "dataCreazione TEXT, "
                + "idVisita INTEGER UNIQUE, " // 1 visita -> 0..1 referto
                + "FOREIGN KEY (idVisita) REFERENCES visita(idVisita)"
                + ");";

        // Esecuzione delle query
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

            System.out.println("Tabelle del database inizializzate con successo.");

        } catch (SQLException e) {
            System.out.println("Errore durante l'inizializzazione del DB: " + e.getMessage());
        }
    }
}
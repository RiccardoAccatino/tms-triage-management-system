package dbManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class dbPopolate {

    public static void popolaDatabase() {
        String[] sqlStatements = {
                // 1. PULIZIA (Opzionale: rimuove i dati vecchi per ripartire da zero
                "DELETE FROM referto;",
                "DELETE FROM visita;",
                "DELETE FROM ticket;",
                "DELETE FROM segretario;",
                "DELETE FROM dottore;",
                "DELETE FROM paziente;",
                "DELETE FROM utente;",
                "DELETE FROM reparto;",

                // 2. INSERIMENTO REPARTI
                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (1, 'Cardiologia', 'CARD', 10);",
                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (2, 'Ortopedia', 'ORT', 8);",
                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (3, 'Chirurgia Generale', 'CHIR', 5);",
                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (4, 'Medicina Interna', 'MED', 12);",
                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (5, 'Neurologia', 'NEURO', 6);",

                // 3. INSERIMENTO SEGRETARIO (Login ID: 100)
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (100, 'Giulia', 'Admin', '1990-05-20', 'SEGRETARIO');",
                "INSERT INTO segretario (idUtente, livelloPermessi) VALUES (100, 5);",

                // 4. INSERIMENTO DOTTORI (Login ID: 1, 2, 3, 4, 5)
                // Dottore 1 - Cardiologia
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (1, 'Mario', 'Rossi', '1975-03-10', 'DOTTORE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (1, 'DOC001', 'Mattina', 1);",

                // Dottore 2 - Ortopedia
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (2, 'Luca', 'Bianchi', '1980-11-05', 'DOTTORE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (2, 'DOC002', 'Pomeriggio', 2);",

                // Dottore 3 - Chirurgia
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (3, 'Elena', 'Verdi', '1982-07-22', 'DOTTORE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (3, 'DOC003', 'Notte', 3);",

                // Dottore 4 - Medicina Interna
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (4, 'Gregory', 'House', '1965-01-01', 'DOTTORE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (4, 'DOC004', 'H24', 4);",

                // Dottore 5 - Neurologia
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (5, 'Stephen', 'Strange', '1976-10-10', 'DOTTORE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (5, 'DOC005', 'Reperibile', 5);",

                // 5. INSERIMENTO PAZIENTI
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (10, 'Luigi', 'Paziente', '1995-02-15', 'PAZIENTE');",
                "INSERT INTO paziente (idUtente, codiceFiscale, indirizzo) VALUES (10, 'LGUPAZ95B15F205X', 'Via Roma 10');",

                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (11, 'Anna', 'Neri', '1950-08-30', 'PAZIENTE');",
                "INSERT INTO paziente (idUtente, codiceFiscale, indirizzo) VALUES (11, 'NNANRI50M70L219K', 'Via Milano 20');",

                // 6. INSERIMENTO TICKET (Alcuni per testare la lista accettazione)
                // Ticket In Attesa (per testare Segreteria)
                "INSERT INTO ticket (colore, priorita, sintomi, stato, timestamp, idPaziente) VALUES ('Giallo', 3, 'Dolore toracico forte', 'IN_ATTESA', '2023-11-01 10:00', 10);",
                "INSERT INTO ticket (colore, priorita, sintomi, stato, timestamp, idPaziente) VALUES ('Verde', 2, 'Distorsione caviglia', 'IN_ATTESA', '2023-11-01 10:15', 11);",

                // Ticket Accettato (per testare Visite)
                "INSERT INTO ticket (idTicket, colore, priorita, sintomi, stato, timestamp, idPaziente) VALUES (99, 'Rosso', 4, 'Trauma cranico', 'ACCETTATO', '2023-11-01 09:00', 10);",

                // 7. INSERIMENTO VISITE (Per il calendario del Dottore 1)
                "INSERT INTO visita (dataOraInizio, dataOraFine, sala, idTicket, idDottore, idPaziente, idReparto) VALUES ('2023-11-02 09:00', '2023-11-02 09:30', 'Sala 1', 99, 1, 10, 1);",
        };

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    System.out.println("Errore esecuzione SQL (" + sql + "): " + e.getMessage());
                }
            }
            System.out.println("Database popolato con successo");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
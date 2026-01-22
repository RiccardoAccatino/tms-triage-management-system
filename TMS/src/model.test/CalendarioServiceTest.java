package model.test;

import dbManager.db;
import model.CalendarioService;
import model.pojo.Visita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarioServiceTest {

    private CalendarioService calendarioService;

    @BeforeEach
    void setUp() {
        // 1. IMPORTANTE: Creiamo le tabelle se non esistono!
        // Senza questa riga, il database è vuoto e dà errore "no such table"
        db.initializeDb();

        // 2. Inizializziamo il Service
        calendarioService = new CalendarioService();

        // 3. Prepariamo i dati di prova
        popolaDatabaseDiProva();
    }

    // ... lascia invariati gli altri test (testGetEventiGlobali, testGetEventiMedico) ...
    // Se vuoi copiare tutto per sicurezza, ecco il resto della classe:

    @Test
    void testGetEventiGlobali() {
        System.out.println("Esecuzione testGetEventiGlobali...");
        List<Visita> visite = calendarioService.getEventiGlobali();

        assertNotNull(visite, "La lista visite non dovrebbe essere null");
        assertFalse(visite.isEmpty(), "La lista visite non dovrebbe essere vuota");

        boolean trovato = false;
        for (Visita v : visite) {
            if (v.getIdVisita() == 500) {
                trovato = true;
                assertEquals("Sala 1", v.getSala());
                assertEquals(1, v.getIdDottore());
                break;
            }
        }
        assertTrue(trovato, "Dovremmo aver trovato la visita con ID 500");
    }

    @Test
    void testGetEventiMedico() {
        System.out.println("Esecuzione testGetEventiMedico...");
        int idDottoreEsistente = 1;
        int idDottoreInesistente = 999;

        List<Visita> visiteDottore = calendarioService.getEventiMedico(idDottoreEsistente);
        assertFalse(visiteDottore.isEmpty(), "Il dottore 1 dovrebbe avere visite");

        List<Visita> visiteVuote = calendarioService.getEventiMedico(idDottoreInesistente);
        assertTrue(visiteVuote.isEmpty(), "Il dottore 999 non dovrebbe avere visite");
    }

    private void popolaDatabaseDiProva() {
        String[] sqlStatements = {
                "DELETE FROM visita WHERE idVisita = 500;",
                "DELETE FROM ticket WHERE idTicket = 100;",
                "DELETE FROM dottore WHERE idUtente = 1;",
                "DELETE FROM paziente WHERE idUtente = 2;",
                "DELETE FROM utente WHERE idUtente IN (1, 2);",
                "DELETE FROM reparto WHERE idReparto = 1;",

                "INSERT INTO reparto (idReparto, nome, codice, sale) VALUES (1, 'Cardiologia Test', 'TEST_CARD', 5);",
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (1, 'Mario', 'Test', '1980-01-01', 'DOTTORE');",
                "INSERT INTO utente (idUtente, nome, cognome, dataNascita, tipoUtente) VALUES (2, 'Luigi', 'Test', '1990-01-01', 'PAZIENTE');",
                "INSERT INTO dottore (idUtente, matricola, turni, idReparto) VALUES (1, 'TEST01', 'Mattina', 1);",
                "INSERT INTO paziente (idUtente, codiceFiscale, indirizzo) VALUES (2, 'TESTCF01', 'Via Test');",
                "INSERT INTO ticket (idTicket, colore, priorita, sintomi, stato, timestamp, idPaziente) VALUES (100, 'Verde', 1, 'Test', 'InCorso', '2023-01-01', 2);",
                "INSERT INTO visita (idVisita, dataOraInizio, dataOraFine, sala, idTicket, idDottore, idPaziente, idReparto) VALUES (500, '2023-11-01 09:00', '2023-11-01 09:30', 'Sala 1', 100, 1, 2, 1);"
        };

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    // Stampa l'errore se non è solo un problema di dati già esistenti
                    System.out.println("Info SQL: " + e.getMessage());
                }
            }
            System.out.println("Database popolato con dati di test.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Impossibile popolare il DB per il test", e);
        }
    }
}
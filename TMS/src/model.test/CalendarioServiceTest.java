package model.test;

import dbManager.db;
import model.CalendarioService;
import model.Sessione;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;
import model.pojo.Visita;
import org.junit.jupiter.api.AfterEach;
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
        db.initializeDb();
        Sessione.getInstance().logout(); // Reset sessione
        calendarioService = new CalendarioService();
        popolaDatabaseDiProva();
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().logout();
    }

    @Test
    void testGetEventiGlobali_ComeSegretario_Successo() {
        // 1. Login come Segretario
        Segretario s = new Segretario(10, "Anna", "Segretaria", "1980-01-01", 1);
        Sessione.getInstance().setUtenteLoggato(s);

        // 2. Richiesta eventi globali
        List<Visita> visite = calendarioService.getEventiGlobali();

        assertNotNull(visite);
        assertFalse(visite.isEmpty(), "Il segretario dovrebbe vedere le visite nel DB");

        // Verifica contenuto
        boolean trovato = visite.stream().anyMatch(v -> v.getIdVisita() == 500);
        assertTrue(trovato, "La visita 500 dovrebbe essere presente");
    }

    @Test
    void testGetEventiGlobali_AccessoNegatoDottore() {
        // 1. Login come Dottore
        Dottore d = new Dottore(1, "Mario", "Doc", "1980-01-01", "M1", "H24", 1);
        Sessione.getInstance().setUtenteLoggato(d);

        // 2. Il dottore non può vedere il calendario GLOBALE (vede solo il suo)
        assertThrows(SecurityException.class, () -> {
            calendarioService.getEventiGlobali();
        });
    }

    @Test
    void testGetEventiPersonali_ComeDottore_Successo() {
        // 1. Login come Dottore (ID = 1, che corrisponde ai dati popolati nel DB)
        Dottore d = new Dottore(1, "Mario", "Test", "1980-01-01", "TEST01", "Mattina", 1);
        Sessione.getInstance().setUtenteLoggato(d);

        // 2. Richiesta eventi personali
        List<Visita> visite = calendarioService.getEventiPersonali();

        assertFalse(visite.isEmpty(), "Il dottore ID 1 dovrebbe avere visite assegnate");
        assertEquals(1, visite.get(0).getIdDottore(), "L'ID dottore della visita deve corrispondere all'utente loggato");
    }

    @Test
    void testGetEventiPersonali_AccessoNegatoPaziente() {
        // 1. Login come Paziente
        Paziente p = new Paziente(2, "Luigi", "Paz", "1990-01-01", "CF123", "Via A");
        Sessione.getInstance().setUtenteLoggato(p);

        // 2. Il paziente non ha un calendario lavorativo
        Exception e = assertThrows(SecurityException.class, () -> {
            calendarioService.getEventiPersonali();
        });

        assertEquals("Accesso Negato: Solo i dottori possono accedere al calendario personale.", e.getMessage());
    }

    // --- Metodo Helper per popolare il DB (Invariato) ---
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
                    System.out.println("Info SQL: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Impossibile popolare il DB per il test", e);
        }
    }
}
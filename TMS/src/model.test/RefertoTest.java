package model.test;

import dbManager.db;
import model.dao.*;
import model.pojo.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class RefertoTest {

    private RefertoDao refertoDao;
    private static int idVisitaValida = 10;

    @BeforeAll
    static void setupGlobal() {
        db.initializeDb(); // Crea le tabelle se non esistono
        popolaDatiNecessari(); // Crea le FK necessarie
    }

    @BeforeEach
    void setUp() {
        refertoDao = new RefertoDao();
        pulisciRefertiTest();
    }

    // ===================================================================================
    // SEZIONE 1: TEST POJO
    // ===================================================================================

    @Test
    void testPojoReferto() {
        Referto r = new Referto(0, "Diagnosi Test", "Prognosi 7gg", "Riposo", "2023-10-25", idVisitaValida);

        assertAll("Verifica integrità dati oggetto Referto",
                () -> assertEquals("Diagnosi Test", r.getDiagnosi()),
                () -> assertEquals("Prognosi 7gg", r.getPrognosi()),
                () -> assertEquals(idVisitaValida, r.getIdVisita())
        );
    }

    // ===================================================================================
    // SEZIONE 2: TEST DI INTEGRAZIONE DB
    // ===================================================================================

    @Test
    void testSalvataggioERecupero() {
        Referto nuovoReferto = new Referto(0, "Influenza", "3 giorni", "Tachipirina", "2023-11-01", idVisitaValida);

        refertoDao.save(nuovoReferto);
        int idGenerato = nuovoReferto.getIdReferto();

        assertTrue(idGenerato > 0, "L'ID referto deve essere generato");

        Referto recuperato = refertoDao.get(idGenerato);
        assertNotNull(recuperato);
        assertEquals("Influenza", recuperato.getDiagnosi());
    }

    @Test
    void testAggiornamentoReferto() {
        // Prepariamo il dato
        Referto r = new Referto(0, "Originale", "1gg", "Nulla", "2023-10-25", idVisitaValida);
        refertoDao.save(r);

        // Modifica
        r.setDiagnosi("Aggiornata");
        refertoDao.update(r);

        // Verifica
        Referto aggiornato = refertoDao.get(r.getIdReferto());
        assertEquals("Aggiornata", aggiornato.getDiagnosi());
    }

    @Test
    void testEliminazioneReferto() {
        Referto r = new Referto(0, "Da Eliminare", "3gg", "Nessuna", "2023-10-25", idVisitaValida);
        refertoDao.save(r);
        int id = r.getIdReferto();

        refertoDao.delete(r);

        assertNull(refertoDao.get(id), "Il referto deve essere nullo dopo l'eliminazione");
    }

    /**
     * Metodo helper per svuotare la tabella referti prima di ogni test.
     * Risolve il problema SQLITE_CONSTRAINT_UNIQUE: referto.idVisita
     */
    private void pulisciRefertiTest() {
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM referto WHERE idVisita = " + idVisitaValida);
        } catch (Exception e) {
            System.err.println("Errore pulizia: " + e.getMessage());
        }
    }

    private static void popolaDatiNecessari() {
        try (Connection conn = db.connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT OR IGNORE INTO reparto VALUES (1, 'Generale', 'GEN', 10)");
            stmt.execute("INSERT OR IGNORE INTO utente VALUES (1, 'Dott', 'Test', '1980-01-01', 'DOTTORE')");
            stmt.execute("INSERT OR IGNORE INTO utente VALUES (2, 'Paz', 'Test', '1990-01-01', 'PAZIENTE')");
            stmt.execute("INSERT OR IGNORE INTO dottore VALUES (1, 'MAT-REF', 'Full', 1)");
            stmt.execute("INSERT OR IGNORE INTO paziente VALUES (2, 'CF-REF-TEST', 'Via Test')");
            stmt.execute("INSERT OR IGNORE INTO ticket VALUES (1, 'Verde', 1, 'Sintomi', 'CHIUSO', '2023-01-01', 2)");

            // Creiamo la visita 10 se non esiste
            stmt.execute("INSERT OR IGNORE INTO visita (idVisita, dataOraInizio, idTicket, idDottore, idPaziente, idReparto) " +
                    "VALUES (" + idVisitaValida + ", '2023-10-25 10:00', 1, 1, 2, 1)");
        } catch (Exception e) {
            System.err.println("Errore setup RefertoTest: " + e.getMessage());
        }
    }
}
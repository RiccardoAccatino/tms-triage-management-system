package model.test;

import dbManager.db;
import model.dao.DottoreDao;
import model.pojo.Dottore;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
class DottoreTest {

    private DottoreDao dottoreDao;

    @BeforeEach
    void setUp() {
        // Inizializza le tabelle del database prima di ogni test
        db.initializeDb();
        dottoreDao = new DottoreDao();
    }

    // ===================================================================================
    // SEZIONE 1: TEST POJO (Getter e Setter)
    // ===================================================================================

    @Test
    void testPojoDottore() {
        Dottore d = new Dottore(0, "Mario", "Rossi", "1980-01-01", "MAT001", "Mattina", 1);

        assertAll("Verifica integrità dati oggetto",
                () -> assertEquals("Mario", d.getNome()),
                () -> assertEquals("MAT001", d.getMatricola()),
                () -> assertEquals("DOTTORE", d.getTipoUtente())
        );
    }

    // ===================================================================================
    // SEZIONE 2: TEST DI INTEGRAZIONE DB
    // ===================================================================================

    @Test
    void testSalvataggioERecupero() {
        // Creazione di un dottore di prova (Assicurati che l'idReparto 1 esista nel tuo DB di test)
        Dottore nuovoDottore = new Dottore(0, "Giuseppe", "Verdi", "1975-05-15", "DOC-TEST-01", "Pomeriggio", 1);

        // Salvataggio su DB tramite DAO
        dottoreDao.save(nuovoDottore);
        int idGenerato = nuovoDottore.getIdUtente();

        assertTrue(idGenerato > 0, "L'ID dovrebbe essere generato automaticamente dal DB");

        // Recupero dal DB e verifica dei dati tramite i Getter
        Dottore recuperato = dottoreDao.get(idGenerato);

        assertNotNull(recuperato, "Il dottore dovrebbe essere presente nel database");
        assertEquals("Giuseppe", recuperato.getNome(), "Il nome recuperato deve coincidere");
        assertEquals("DOC-TEST-01", recuperato.getMatricola(), "La matricola recuperata deve coincidere");
        assertEquals(1, recuperato.getIdReparto(), "L'ID reparto deve coincidere");
    }

    @Test
    void testAggiornamentoDati() {
        Dottore d = new Dottore(0, "Elena", "Neri", "1985-12-12", "MAT-UPDATE", "Mattina", 1);
        dottoreDao.save(d);

        // Modifica tramite Setter
        d.setTurni("Notte");
        d.setCognome("Neri-Bianchi");

        // Aggiornamento su DB
        dottoreDao.update(d);

        // Verifica
        Dottore aggiornato = dottoreDao.get(d.getIdUtente());
        assertEquals("Notte", aggiornato.getTurni(), "Il turno deve essere aggiornato nel DB");
        assertEquals("Neri-Bianchi", aggiornato.getCognome(), "Il cognome deve essere aggiornato nel DB");
    }

    @Test
    void testEliminazione() {
        Dottore d = new Dottore(0, "Da", "Eliminare", "2000-01-01", "MAT-DEL", "Mattina", 1);
        dottoreDao.save(d);
        int id = d.getIdUtente();

        // Eliminazione
        dottoreDao.delete(d);

        // Verifica che non esista più
        Dottore eliminato = dottoreDao.get(id);
        assertNull(eliminato, "Il dottore non dovrebbe più esistere nel database");
    }
}
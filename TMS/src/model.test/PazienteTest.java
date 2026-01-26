package model.test;

import dbManager.db;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class PazienteTest {

    private PazienteDao pazienteDao;

    @BeforeEach
    void setUp() {
        // Inizializza le tabelle del database prima di ogni test
        db.initializeDb();
        pazienteDao = new PazienteDao();
    }

    // ===================================================================================
    // SEZIONE 1: TEST POJO (Getter, Setter e Ereditarietà)
    // ===================================================================================

    @Test
    void testPojoPaziente() {
        // Test del costruttore pieno
        Paziente p = new Paziente(0, "Mario", "Rossi", "1990-01-01", "RSSMRA90A01H501W", "Via Roma 1");

        assertAll("Verifica integrità dati oggetto Paziente",
                () -> assertEquals("Mario", p.getNome(), "Il nome deve coincidere"),
                () -> assertEquals("RSSMRA90A01H501W", p.getCodiceFiscale(), "Il CF deve coincidere"),
                () -> assertEquals("PAZIENTE", p.getTipoUtente(), "Il tipo utente deve essere PAZIENTE"),
                () -> assertTrue(p instanceof model.pojo.Utente, "Paziente deve estendere Utente")
        );
    }

    // ===================================================================================
    // SEZIONE 2: TEST DI INTEGRAZIONE DB (Persistenza e DAO)
    // ===================================================================================

    @Test
    void testSalvataggioERecupero() {
        Paziente nuovoPaziente = new Paziente(0, "Anna", "Verdi", "1985-05-15", "VRDNNA85E55L219Z", "Corso Italia 10");

        // Salvataggio su DB tramite DAO
        pazienteDao.save(nuovoPaziente);
        int idGenerato = nuovoPaziente.getIdUtente();

        assertTrue(idGenerato > 0, "L'ID utente dovrebbe essere generato dal database");

        // Recupero dal DB e verifica tramite Getter
        Paziente recuperato = pazienteDao.get(idGenerato);

        assertNotNull(recuperato, "Il paziente dovrebbe essere presente nel database");
        assertEquals("Anna", recuperato.getNome());
        assertEquals("VRDNNA85E55L219Z", recuperato.getCodiceFiscale());
        assertEquals("Corso Italia 10", recuperato.getIndirizzo());
    }

    @Test
    void testAggiornamentoPaziente() {
        Paziente p = new Paziente(0, "Luca", "Neri", "1970-10-10", "NRELUC70R10F205T", "Via Milano 5");
        pazienteDao.save(p);

        // Modifica tramite Setter
        p.setIndirizzo("Nuovo Indirizzo 100");
        p.setCognome("Neri Bianchi");

        // Aggiornamento su DB
        pazienteDao.update(p);

        // Verifica ricaricando il dato
        Paziente aggiornato = pazienteDao.get(p.getIdUtente());
        assertEquals("Nuovo Indirizzo 100", aggiornato.getIndirizzo(), "L'indirizzo deve essere aggiornato nel DB");
        assertEquals("Neri Bianchi", aggiornato.getCognome(), "Il cognome deve essere aggiornato nel DB");
    }

    @Test
    void testEliminazionePaziente() {
        Paziente p = new Paziente(0, "Test", "Elimina", "2000-01-01", "TSTEML00A01H501X", "Via Test");
        pazienteDao.save(p);
        int id = p.getIdUtente();

        // Eliminazione
        pazienteDao.delete(p);

        // Verifica
        Paziente eliminato = pazienteDao.get(id);
        assertNull(eliminato, "Il paziente non deve più esistere nel database dopo la delete");
    }
}
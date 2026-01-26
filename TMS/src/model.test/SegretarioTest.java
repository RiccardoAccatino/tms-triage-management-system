package model.test;

import dbManager.db;
import model.dao.SegretarioDao;
import model.pojo.Segretario;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite per la gestione dei Segretari.
 * Verifica il funzionamento del POJO e l'integrazione con il Database tramite DAO.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SegretarioTest {

    private SegretarioDao segretarioDao;

    @BeforeAll
    static void initDb() {
        // Inizializza il database e le tabelle prima di eseguire i test
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        segretarioDao = new SegretarioDao();
    }

    @Test
    @Order(1)
    @DisplayName("Unit Test: Verifica Getter e Setter del POJO")
    void testSegretarioPojo() {
        Segretario s = new Segretario();
        s.setNome("Giulia");
        s.setCognome("Bianchi");
        s.setDataNascita("1985-04-20");
        s.setLivelloPermessi(2);

        assertEquals("Giulia", s.getNome());
        assertEquals("Bianchi", s.getCognome());
        assertEquals("1985-04-20", s.getDataNascita());
        assertEquals(2, s.getLivelloPermessi());
        assertEquals("SEGRETARIO", s.getTipoUtente(), "Il tipo utente deve essere 'SEGRETARIO' di default");
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test: Salvataggio e Recupero dal DB")
    void testSalvataggioERecupero() {
        // 1. Creazione (ID 0 perché è nuovo)
        Segretario nuovoSegretario = new Segretario(0, "Maria", "Verdi", "1992-06-15", 1);

        // 2. Salvataggio
        segretarioDao.save(nuovoSegretario);

        // 3. Verifica generazione ID
        // Nota: SegretarioDao aggiorna l'oggetto con l'ID generato, quindi possiamo usarlo direttamente
        assertTrue(nuovoSegretario.getIdUtente() > 0, "L'ID del segretario dovrebbe essere stato generato e assegnato all'oggetto");

        // 4. Verifica recupero tramite ID
        Segretario recuperato = segretarioDao.get(nuovoSegretario.getIdUtente());

        assertNotNull(recuperato, "Il segretario recuperato non deve essere null");
        assertEquals("Maria", recuperato.getNome());
        assertEquals("Verdi", recuperato.getCognome());
        assertEquals(1, recuperato.getLivelloPermessi());
        assertEquals("SEGRETARIO", recuperato.getTipoUtente());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test: Aggiornamento Dati")
    void testAggiornamento() {
        // 1. Prepariamo e salviamo un segretario
        Segretario segretario = new Segretario(0, "Luca", "Gialli", "1980-01-01", 1);
        segretarioDao.save(segretario);
        int idSalvato = segretario.getIdUtente();

        // 2. Modifica dei dati in memoria
        segretario.setNome("Luca Aggiornato");
        segretario.setLivelloPermessi(3); // Promozione!

        // 3. Esecuzione update nel DB
        segretarioDao.update(segretario);

        // 4. Verifica ricaricando dal DB
        Segretario aggiornato = segretarioDao.get(idSalvato);
        assertEquals("Luca Aggiornato", aggiornato.getNome());
        assertEquals(3, aggiornato.getLivelloPermessi());
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test: Eliminazione Segretario")
    void testEliminazione() {
        // 1. Creiamo e salviamo
        Segretario daEliminare = new Segretario(0, "Da Eliminare", "Neri", "1999-12-31", 1);
        segretarioDao.save(daEliminare);
        int id = daEliminare.getIdUtente();

        // Assicuriamoci che esista
        assertNotNull(segretarioDao.get(id));

        // 2. Cancellazione
        segretarioDao.delete(daEliminare);

        // 3. Verifica che non esista più
        Segretario check = segretarioDao.get(id);
        assertNull(check, "Il segretario eliminato dovrebbe restituire null al recupero");
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test: Lista di tutti i segretari")
    void testGetAll() {
        // Questo test verifica semplicemente che il metodo non lanci eccezioni e ritorni una lista
        List<Segretario> lista = segretarioDao.getAll();

        assertNotNull(lista, "La lista dei segretari non deve essere null");
        System.out.println("Numero di segretari trovati nel DB: " + lista.size());

        // Opzionale: Se abbiamo eseguito i test in ordine, dovremmo avere dei residui nel DB
        // (i test precedenti lasciano i dati a meno che non facciamo una pulizia esplicita o usiamo transazioni rollback)
        // Quindi la lista non dovrebbe essere vuota.
        assertFalse(lista.isEmpty(), "Dovrebbero esserci dei segretari nel DB dopo i test precedenti");
    }
}
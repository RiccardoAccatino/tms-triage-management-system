package model.test;

import dbManager.db;
import model.dao.RepartoDao;
import model.pojo.Reparto;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite per la gestione dei Reparti.
 * Verifica il funzionamento del POJO e l'integrazione con il Database tramite DAO.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepartoTest {

    private RepartoDao repartoDao;

    @BeforeAll
    static void initDb() {
        // Inizializza il database e le tabelle prima di tutto
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        repartoDao = new RepartoDao();
    }

    @Test
    @Order(1)
    @DisplayName("Unit Test: Verifica Getter e Setter del POJO")
    void testRepartoPojo() {
        Reparto r = new Reparto();
        r.setNome("Cardiologia");
        r.setCodice("CARDIO_01");
        r.setSale(5);

        assertEquals("Cardiologia", r.getNome());
        assertEquals("CARDIO_01", r.getCodice());
        assertEquals(5, r.getSale());
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test: Salvataggio e Recupero dal DB")
    void testSalvataggioERecupero() {
        // 1. Creazione
        String codiceUnivoco = "TEST_REP_01";
        Reparto nuovoReparto = new Reparto(0, "Reparto Test A", codiceUnivoco, 10);

        // 2. Salvataggio
        repartoDao.save(nuovoReparto);

        // 3. Recupero ID (Workaround perché il DAO save non restituisce l'ID generato)
        int idGenerato = recuperaIdTramiteCodice(codiceUnivoco);
        assertTrue(idGenerato > 0, "Il reparto dovrebbe essere stato salvato con un ID valido");

        // 4. Verifica recupero tramite ID
        Reparto recuperato = repartoDao.get(idGenerato);
        assertNotNull(recuperato, "Il reparto recuperato non deve essere null");
        assertEquals("Reparto Test A", recuperato.getNome());
        assertEquals(10, recuperato.getSale());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test: Aggiornamento Dati")
    void testAggiornamento() {
        // 1. Prepariamo un reparto
        String codiceUnivoco = "TEST_REP_UPD";
        Reparto reparto = new Reparto(0, "Vecchio Nome", codiceUnivoco, 3);
        repartoDao.save(reparto);
        int id = recuperaIdTramiteCodice(codiceUnivoco);
        reparto.setIdReparto(id); // Impostiamo l'ID per poter fare l'update

        // 2. Modifica
        reparto.setNome("Nuovo Nome Aggiornato");
        reparto.setSale(20);

        // Eseguiamo l'update
        repartoDao.update(reparto);

        // 3. Verifica
        Reparto aggiornato = repartoDao.get(id);
        assertEquals("Nuovo Nome Aggiornato", aggiornato.getNome());
        assertEquals(20, aggiornato.getSale());
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test: Eliminazione Reparto")
    void testEliminazione() {
        // 1. Creiamo e salviamo
        String codiceUnivoco = "TEST_REP_DEL";
        Reparto daEliminare = new Reparto(0, "Da Eliminare", codiceUnivoco, 1);
        repartoDao.save(daEliminare);

        int id = recuperaIdTramiteCodice(codiceUnivoco);
        daEliminare.setIdReparto(id);

        // 2. Cancellazione
        repartoDao.delete(daEliminare);

        // 3. Verifica che non esista più
        Reparto check = repartoDao.get(id);
        assertNull(check, "Il reparto eliminato dovrebbe essere null quando cercato nel DB");
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test: Lista di tutti i reparti")
    void testGetAll() {
        List<Reparto> lista = repartoDao.getAll();
        assertNotNull(lista);
        // La lista potrebbe non essere vuota se ci sono altri test o dati,
        // ma verifichiamo che non dia errore.
        System.out.println("Reparti trovati nel DB: " + lista.size());
    }

    // --- Metodi di Supporto ---

    /**
     * Helper method: Poiché RepartoDao.save() non aggiorna l'oggetto con l'ID generato,
     * lo recuperiamo cercando per il codice univoco.
     */
    private int recuperaIdTramiteCodice(String codice) {
        List<Reparto> tutti = repartoDao.getAll();
        for (Reparto r : tutti) {
            if (r.getCodice().equals(codice)) {
                return r.getIdReparto();
            }
        }
        return -1; // Non trovato
    }
}
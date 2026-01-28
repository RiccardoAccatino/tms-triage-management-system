package model.test;

import dbManager.db;
import model.dao.*;
import model.pojo.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite per la gestione delle Visite.
 * Questo test è il più completo perché la Visita collega tutte le altre entità:
 * Ticket, Dottore, Paziente e Reparto.
 *
 * @author angie
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VisitaTest {

    // DAO necessari per costruire l'ambiente di test
    private VisitaDao visitaDao;
    private TicketDao ticketDao;
    private DottoreDao dottoreDao;
    private PazienteDao pazienteDao;
    private RepartoDao repartoDao;

    // ID delle entità collegate create nel setUp
    private int idRepartoTest;
    private int idPazienteTest;
    private int idDottoreTest;
    private int idTicketTest;

    @BeforeAll
    static void initDb() {
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        // 1. Inizializzazione DAO
        visitaDao = new VisitaDao();
        ticketDao = new TicketDao();
        dottoreDao = new DottoreDao();
        pazienteDao = new PazienteDao();
        repartoDao = new RepartoDao();

        // Generatore random per evitare duplicati (Unique Constraints)
        Random rand = new Random();
        int suffix = rand.nextInt(900000) + 100000;

        // 2. CREAZIONE DIPENDENZE (Ordine importante per le Foreign Key!)

        // A. Reparto
        String codReparto = "R_TEST_" + suffix;
        Reparto r = new Reparto(0, "Reparto Test Visita", codReparto, 5);
        repartoDao.save(r);
        this.idRepartoTest = recuperaIdReparto(codReparto); // Helper per recuperare ID

        // B. Paziente
        Paziente p = new Paziente(0, "Paziente", "Visita", "1990-01-01", "CF_VIS_" + suffix, "Via Roma");
        pazienteDao.save(p);
        this.idPazienteTest = p.getIdUtente();

        // C. Dottore (richiede Reparto)
        Dottore d = new Dottore(0, "Dottore", "Visita", "1980-01-01", "MAT_" + suffix, "H24", idRepartoTest);
        dottoreDao.save(d);
        this.idDottoreTest = d.getIdUtente();

        // D. Ticket (richiede Paziente)
        Ticket t = new Ticket(0, "Verde", 2, "Test Visita", "ACCETTATO", "2023-11-01", idPazienteTest);
        ticketDao.save(t);
        this.idTicketTest = t.getIdTicket();
    }

    @AfterEach
    void tearDown() {
        // Pulizia opzionale: per brevità e sicurezza sui vincoli, in test locali complessi
        // spesso si lascia che il DB (se è file locale) cresca o si resetta il file DB.
        // Qui lasciamo i dati per permettere l'analisi in caso di fallimento,
        // ma l'uso di 'suffix' random garantisce che i test successivi non falliscano.
    }

    @Test
    @Order(1)
    @DisplayName("Unit Test: Verifica POJO Visita")
    void testVisitaPojo() {
        Visita v = new Visita();
        v.setDataOraInizio("2023-11-01 10:00");
        v.setDataOraFine("2023-11-01 10:30");
        v.setSala("Sala Rossa");
        v.setIdTicket(1);
        v.setIdDottore(2);
        v.setIdPaziente(3);
        v.setIdReparto(4);

        assertEquals("2023-11-01 10:00", v.getDataOraInizio());
        assertEquals("Sala Rossa", v.getSala());
        assertEquals(2, v.getIdDottore());
        assertEquals(4, v.getIdReparto());
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test: Salvataggio e Recupero Visita")
    void testSalvataggioERecupero() {
        // Creiamo la visita usando gli ID reali generati nel setUp
        Visita nuovaVisita = new Visita(0, "2023-11-02 09:00", "2023-11-02 09:15", "Sala Test 1",
                idTicketTest, idDottoreTest, idPazienteTest, idRepartoTest);

        // Salvataggio
        visitaDao.save(nuovaVisita);

        // Verifica ID generato
        assertTrue(nuovaVisita.getIdVisita() > 0, "La visita deve avere un ID dopo il salvataggio");

        // Recupero
        Visita recuperata = visitaDao.get(nuovaVisita.getIdVisita());
        assertNotNull(recuperata, "La visita salvata deve essere recuperabile");
        assertEquals("Sala Test 1", recuperata.getSala());
        assertEquals(idDottoreTest, recuperata.getIdDottore());
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test: Aggiornamento Visita")
    void testAggiornamento() {
        // Setup
        Visita v = new Visita(0, "2023-11-02 10:00", null, "Sala 2",
                idTicketTest, idDottoreTest, idPazienteTest, idRepartoTest);
        visitaDao.save(v);
        int idVisita = v.getIdVisita();

        // Modifica: La visita finisce
        v.setDataOraFine("2023-11-02 10:45");
        v.setSala("Sala 2 (Chiusa)");

        // Update
        visitaDao.update(v);

        // Verifica
        Visita aggiornata = visitaDao.get(idVisita);
        assertEquals("2023-11-02 10:45", aggiornata.getDataOraFine());
        assertEquals("Sala 2 (Chiusa)", aggiornata.getSala());
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test: Recupero visite per Dottore (Calendario)")
    void testGetByDottore() {
        // 1. Creiamo 2 visite per il dottore corrente
        Visita v1 = new Visita(0, "2023-11-03 08:00", "08:30", "S1", idTicketTest, idDottoreTest, idPazienteTest, idRepartoTest);
        Visita v2 = new Visita(0, "2023-11-03 09:00", "09:30", "S1", idTicketTest, idDottoreTest, idPazienteTest, idRepartoTest);

        visitaDao.save(v1);
        visitaDao.save(v2);

        // 2. Recuperiamo la lista dal DAO
        List<Visita> visiteDottore = visitaDao.getByDottore(idDottoreTest);

        // 3. Verifica
        assertNotNull(visiteDottore);
        assertTrue(visiteDottore.size() >= 2, "Il dottore dovrebbe avere almeno 2 visite assegnate");

        // Verifichiamo che una delle visite sia quella appena creata
        boolean trovata = visiteDottore.stream().anyMatch(v -> v.getIdVisita() == v1.getIdVisita());
        assertTrue(trovata, "La visita v1 deve essere presente nella lista del dottore");
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test: Eliminazione Visita")
    void testEliminazione() {
        Visita daEliminare = new Visita(0, "2023-12-01 10:00", null, "S-Del",
                idTicketTest, idDottoreTest, idPazienteTest, idRepartoTest);
        visitaDao.save(daEliminare);
        int id = daEliminare.getIdVisita();

        visitaDao.delete(daEliminare);

        Visita check = visitaDao.get(id);
        assertNull(check, "La visita eliminata non deve più esistere");
    }

    // --- Metodi di Supporto ---

    /**
     * Recupera l'ID del reparto dato il codice, poiché RepartoDao.save() non restituisce l'ID.
     */
    private int recuperaIdReparto(String codice) {
        for (Reparto r : repartoDao.getAll()) {
            if (r.getCodice().equals(codice)) {
                return r.getIdReparto();
            }
        }
        throw new RuntimeException("Setup fallito: Reparto non trovato");
    }
}
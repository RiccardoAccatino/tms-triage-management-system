package model.test;

import dbManager.db;
import model.CompilazioneTriage;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite per CompilazioneTriage usando JUnit 5.
 * Include sia Unit Test per la validazione che Integration Test per il DB.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompilazioneTriageTest {

    private CompilazioneTriage compilazioneTriage;
    private PazienteDao pazienteDao;

    // Eseguito una volta sola prima di tutti i test per preparare il DB
    @BeforeAll
    static void setupGlobal() {
        db.initializeDb();
    }

    // Eseguito prima di ogni singolo test
    @BeforeEach
    void setUp() {
        compilazioneTriage = new CompilazioneTriage();
        pazienteDao = new PazienteDao();
    }

    // ---------------------------------------------------------
    // SEZIONE 1: Test di Validazione (Eccezioni)
    // ---------------------------------------------------------

    @Test
    @DisplayName("Deve lanciare eccezione se l'oggetto Paziente è null")
    void testPazienteNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(null);
        });
        assertEquals("Dati paziente assenti.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lanciare eccezione per campi obbligatori mancanti")
    void testCampiVuoti() {
        Paziente p = new Paziente(); // Oggetto vuoto

        // 1. Test Nome mancante
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(p));
        p.setNome("Mario");

        // 2. Test Cognome mancante
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(p));
        p.setCognome("Rossi");

        // 3. Test Indirizzo mancante
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(p));
        p.setIndirizzo("Via Roma 1");

        // 4. Test Codice Fiscale mancante
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(p));
        p.setCodiceFiscale("RSSMRA80A01H501U");

        // 5. Test Data di Nascita mancante
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(p));
    }

    @Test
    @DisplayName("Deve lanciare eccezione se il formato della data è errato")
    void testFormatoDataErrato() {
        // Data formattata male (DD-MM-YYYY invece di YYYY-MM-DD)
        Paziente p = new Paziente(0, "Mario", "Rossi", "31-12-1980", "RSSMRA80A01H501U", "Via Roma 1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(p);
        });

        // Verifica che il messaggio contenga l'indicazione del formato
        assertTrue(exception.getMessage().contains("YYYY-MM-DD"));
    }

    @Test
    @DisplayName("Deve lanciare eccezione se la lunghezza del CF non è 16")
    void testLunghezzaCF() {
        // CF troppo corto
        Paziente p = new Paziente(0, "Mario", "Rossi", "1980-01-01", "ABC", "Via Roma 1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(p);
        });

        assertTrue(exception.getMessage().contains("16 caratteri"));
    }

    // ---------------------------------------------------------
    // SEZIONE 2: Test di Integrazione (Database)
    // ---------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("Integrazione: Deve salvare un NUOVO paziente nel DB e restituire ID")
    void testNuovoPazienteDB() {
        String cfTest = "TEST_JUNIT5_0001";
        puliziaPazienteTest(cfTest); // Sicurezza pre-test

        Paziente nuovo = new Paziente(0, "Luigi", "Verdi", "1990-05-05", cfTest, "Via Milano 2");

        // Azione
        Paziente salvato = compilazioneTriage.gestisciAnagraficaPaziente(nuovo);

        // Asserzioni
        assertNotNull(salvato);
        assertTrue(salvato.getIdUtente() > 0, "L'ID dovrebbe essere stato generato dal DB");
        assertEquals(cfTest, salvato.getCodiceFiscale());

        // Cleanup finale
        pazienteDao.delete(salvato);
    }

    @Test
    @Order(2)
    @DisplayName("Integrazione: Deve trovare un paziente ESISTENTE tramite CF")
    void testPazienteEsistenteDB() {
        String cfTest = "TEST_JUNIT5_EXIST";
        puliziaPazienteTest(cfTest);

        // 1. Creiamo e salviamo manualmente un paziente nel DB
        Paziente esistente = new Paziente(0, "Anna", "Neri", "1985-10-10", cfTest, "Corso Italia");
        pazienteDao.save(esistente);
        int idOriginale = esistente.getIdUtente();

        // 2. Simuliamo un nuovo inserimento con lo STESSO Codice Fiscale
        Paziente inputDaForm = new Paziente(0, "Anna", "Neri", "1985-10-10", cfTest, "Corso Italia");

        // Azione
        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(inputDaForm);

        // Asserzioni: Deve restituire l'ID di quello che era già nel DB
        assertEquals(idOriginale, risultato.getIdUtente(), "Deve restituire l'ID del record già esistente");

        // Cleanup finale
        pazienteDao.delete(risultato);
    }

    // Helper per pulire il DB ed evitare errori di duplicati nei test
    private void puliziaPazienteTest(String cf) {
        List<Paziente> lista = pazienteDao.getAll();
        for (Paziente p : lista) {
            if (p.getCodiceFiscale().equals(cf)) {
                pazienteDao.delete(p);
            }
        }
    }
}
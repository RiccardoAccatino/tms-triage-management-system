package model.test;

import dbManager.db;
import model.CompilazioneTriage;
import model.Sessione;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import model.pojo.Segretario;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite "Ad Hoc" per CompilazioneTriage.
 * Copre Validazione, Database e Sicurezza (Sessione).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompilazioneTriageTest {

    private CompilazioneTriage compilazioneTriage;
    private PazienteDao pazienteDao;

    @BeforeAll
    static void initGlobal() {
        System.out.println("--- Inizializzazione Database per i Test ---");
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        // 1. Reset Sessione
        Sessione.getInstance().logout();

        // 2. Setup Componenti
        compilazioneTriage = new CompilazioneTriage();
        pazienteDao = new PazienteDao();

        // 3. LOGIN STAFF: Simuliamo un Segretario loggato per autorizzare le operazioni
        Segretario seg = new Segretario(10, "Anna", "Admin", "1980-01-01", 1);
        Sessione.getInstance().setUtenteLoggato(seg);
    }

    @AfterEach
    void tearDown() {
        Sessione.getInstance().logout();
    }

    // ===================================================================================
    // SEZIONE 1: TEST SICUREZZA (Nuova)
    // ===================================================================================

    @Test
    @DisplayName("SICUREZZA: Paziente tenta di registrare -> Accesso Negato")
    void testAccessoNegato_Paziente() {
        // Simuliamo login Paziente (sovrascrive il segretario del setUp)
        Paziente pLoggato = new Paziente(99, "Mario", "User", "1990-01-01", "CFTEST", "Via A");
        Sessione.getInstance().setUtenteLoggato(pLoggato);

        Paziente pNuovo = new Paziente(0, "Luigi", "Verdi", "1985-05-05", "TEST_SEC_00001", "Via Milano");

        Exception e = assertThrows(SecurityException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(pNuovo);
        });

        assertTrue(e.getMessage().contains("Accesso Negato"), "Deve bloccare utenti non staff");
    }

    @Test
    @DisplayName("SICUREZZA: Nessun login -> Accesso Negato")
    void testAccessoNegato_NoLogin() {
        Sessione.getInstance().logout(); // Nessuno loggato

        Paziente pNuovo = new Paziente(0, "Luigi", "Verdi", "1985-05-05", "TEST_SEC_00002", "Via Milano");

        assertThrows(SecurityException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(pNuovo);
        });
    }

    // ===================================================================================
    // SEZIONE 2: TEST DI VALIDAZIONE (Input Errati) - (Invariati ma ora con Auth attiva)
    // ===================================================================================

    @Test
    @DisplayName("ERRORE: Paziente Null -> IllegalArgumentException")
    void testPazienteNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(null);
        });
        assertEquals("Dati paziente assenti.", exception.getMessage());
    }

    @Test
    @DisplayName("ERRORE: Codice Fiscale Lunghezza Errata")
    void testLunghezzaCodiceFiscale() {
        String cfErrato = "ABCDE123456789012"; // 17 char
        Paziente p = new Paziente(0, "Mario", "Rossi", "1990-01-01", cfErrato, "Via Roma");

        assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(p);
        });
    }

    // ===================================================================================
    // SEZIONE 3: TEST DI INTEGRAZIONE (Logica Database)
    // ===================================================================================

    @Test
    @Order(1)
    @DisplayName("SUCCESS: Nuovo Paziente (Staff Loggato) -> Salvataggio OK")
    void testSalvataggioNuovoPaziente() {
        String cfNuovo = "TEST_NUOVO_00001";
        puliziaPreventiva(cfNuovo);

        Paziente nuovoP = new Paziente(0, "Luigi", "Verdi", "1985-05-05", cfNuovo, "Via Milano 10");

        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(nuovoP);

        assertNotNull(risultato);
        assertTrue(risultato.getIdUtente() > 0, "ID deve essere generato");
    }

    @Test
    @Order(2)
    @DisplayName("SUCCESS: Paziente Esistente -> Restituisce Vecchio ID")
    void testRecuperoPazienteEsistente() {
        String cfEsistente = "TEST_EXIST_00001";
        puliziaPreventiva(cfEsistente);

        // 1. Inserimento manuale nel DB
        Paziente pOriginale = new Paziente(0, "Anna", "Neri", "1990-10-10", cfEsistente, "Corso Italia");
        pazienteDao.save(pOriginale);
        int idOriginale = pOriginale.getIdUtente();

        // 2. Tentativo di "nuova" registrazione con stessi dati
        Paziente pInput = new Paziente(0, "Anna", "Neri", "1990-10-10", cfEsistente, "Corso Italia");
        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(pInput);

        // 3. Verifica
        assertEquals(idOriginale, risultato.getIdUtente(), "Deve restituire l'ID esistente");
    }

    private void puliziaPreventiva(String cf) {
        List<Paziente> lista = pazienteDao.getAll();
        for (Paziente p : lista) {
            if (p.getCodiceFiscale().equalsIgnoreCase(cf)) {
                pazienteDao.delete(p);
            }
        }
    }
}
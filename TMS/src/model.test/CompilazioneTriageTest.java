package model.test;

import dbManager.db;
import model.CompilazioneTriage;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Suite "Ad Hoc" per CompilazioneTriage.
 * Copre sia la validazione dei dati che l'interazione con il Database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompilazioneTriageTest {

    private CompilazioneTriage compilazioneTriage;
    private PazienteDao pazienteDao;

    // Eseguito una sola volta prima di tutti i test: Prepara il DB
    @BeforeAll
    static void initGlobal() {
        System.out.println("--- Inizializzazione Database per i Test ---");
        db.initializeDb();
    }

    // Eseguito prima di OGNI singolo test: Resetta gli oggetti
    @BeforeEach
    void setUp() {
        compilazioneTriage = new CompilazioneTriage();
        pazienteDao = new PazienteDao();
    }

    // ===================================================================================
    // SEZIONE 1: TEST DI VALIDAZIONE (Input Errati)
    // ===================================================================================

    @Test
    @DisplayName("ERRORE: Paziente Null -> Deve lanciare IllegalArgumentException")
    void testPazienteNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(null);
        });
        assertEquals("Dati paziente assenti.", exception.getMessage());
    }

    @Test
    @DisplayName("ERRORE: Codice Fiscale Lunghezza Errata -> Deve fallire se != 16 caratteri")
    void testLunghezzaCodiceFiscale() {
        // CF di 17 caratteri (Troppo lungo)
        String cfErrato = "ABCDE123456789012";
        Paziente p = new Paziente(0, "Mario", "Rossi", "1990-01-01", cfErrato, "Via Roma");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(p);
        });

        // Verifica che il messaggio d'errore sia quello specifico
        assertTrue(exception.getMessage().contains("16 caratteri"));
    }

    @Test
    @DisplayName("ERRORE: Formato Data Errato -> Deve accettare solo YYYY-MM-DD")
    void testFormatoData() {
        // Formato errato: DD-MM-YYYY
        Paziente p = new Paziente(0, "Mario", "Rossi", "31-12-1990", "ABCDE12345678901", "Via Roma");

        assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(p);
        }, "Dovrebbe fallire perché la data non è nel formato YYYY-MM-DD");
    }

    @Test
    @DisplayName("ERRORE: Campi Obbligatori Mancanti -> Nome, Cognome, Indirizzo")
    void testCampiMancanti() {
        // Paziente senza Nome
        Paziente pNoNome = new Paziente(0, "", "Rossi", "1990-01-01", "ABCDE12345678901", "Via Roma");
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(pNoNome));

        // Paziente senza Cognome
        Paziente pNoCognome = new Paziente(0, "Mario", null, "1990-01-01", "ABCDE12345678901", "Via Roma");
        assertThrows(IllegalArgumentException.class, () -> compilazioneTriage.gestisciAnagraficaPaziente(pNoCognome));
    }

    // ===================================================================================
    // SEZIONE 2: TEST DI INTEGRAZIONE (Logica Database)
    // ===================================================================================

    @Test
    @Order(1)
    @DisplayName("SUCCESS: Nuovo Paziente -> Deve salvarlo e generare ID")
    void testSalvataggioNuovoPaziente() {
        // CF di esattamente 16 caratteri per il test
        String cfNuovo = "TEST_NUOVO_00001";
        puliziaPreventiva(cfNuovo); // Cancella se esiste già per avanzi di test precedenti

        Paziente nuovoP = new Paziente(0, "Luigi", "Verdi", "1985-05-05", cfNuovo, "Via Milano 10");

        // Esecuzione
        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(nuovoP);

        // Verifiche
        assertNotNull(risultato);
        assertTrue(risultato.getIdUtente() > 0, "Il database avrebbe dovuto generare un ID > 0");
        assertEquals("PAZIENTE", risultato.getTipoUtente(), "Il tipo utente deve essere impostato automaticamente");

        System.out.println("Test Nuovo Paziente superato. ID Generato: " + risultato.getIdUtente());
    }

    @Test
    @Order(2)
    @DisplayName("SUCCESS: Paziente Esistente -> Non deve duplicarlo, ma restituire quello vecchio")
    void testRecuperoPazienteEsistente() {
        String cfEsistente = "TEST_EXIST_00001"; // 16 Caratteri
        puliziaPreventiva(cfEsistente);

        // 1. PREPARAZIONE: Inseriamo manualmente un paziente nel DB
        Paziente pOriginale = new Paziente(0, "Anna", "Neri", "1990-10-10", cfEsistente, "Corso Italia");
        pazienteDao.save(pOriginale);
        int idOriginale = pOriginale.getIdUtente();
        assertTrue(idOriginale > 0, "Errore setup test: ID non generato");

        // 2. ESECUZIONE: Simuliamo che arrivi un paziente con LO STESSO codice fiscale
        // Notare che passiamo ID 0, come se fosse un nuovo oggetto dalla GUI
        Paziente pInput = new Paziente(0, "Anna", "Neri", "1990-10-10", cfEsistente, "Corso Italia");

        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(pInput);

        // 3. VERIFICA: L'ID restituito deve essere quello VECCHIO (idOriginale), non uno nuovo
        assertEquals(idOriginale, risultato.getIdUtente(),
                "Il sistema non ha riconosciuto il paziente esistente e ne ha creato uno nuovo duplicato!");

        System.out.println("Test Paziente Esistente superato. ID Mantenuto: " + risultato.getIdUtente());
    }

    /**
     * Metodo helper per pulire il DB prima di un test,
     * evitando che esecuzioni precedenti falsino i risultati.
     */
    private void puliziaPreventiva(String cf) {
        List<Paziente> lista = pazienteDao.getAll();
        for (Paziente p : lista) {
            if (p.getCodiceFiscale().equalsIgnoreCase(cf)) {
                pazienteDao.delete(p);
            }
        }
    }
}
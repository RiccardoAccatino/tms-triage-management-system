package model.test;

import dbManager.db;
import model.CompilazioneTriage;
import model.Sessione;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
//@author Accatino Riccardo
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CompilazioneTriageTest {

    private CompilazioneTriage compilazioneTriage;
    private PazienteDao pazienteDao;

    @BeforeAll
    static void initGlobal() {
        db.initializeDb();
    }

    @BeforeEach
    void setUp() {
        // Puliamo la sessione per testare il caso "non loggato"
        Sessione.getInstance().logout();
        compilazioneTriage = new CompilazioneTriage();
        pazienteDao = new PazienteDao();
    }

    @Test
    @DisplayName("SUCCESS: Utente non loggato può registrare un paziente")
    void testRegistrazioneSenzaLogin() {
        String cfNuovo = "TEST_PUB_0000001";
        puliziaPreventiva(cfNuovo);

        Paziente nuovoP = new Paziente(0, "Utente", "Anonimo", "1995-01-01", cfNuovo, "Via Pubblica 1");

        // Ora questo metodo non deve più lanciare SecurityException
        Paziente risultato = compilazioneTriage.gestisciAnagraficaPaziente(nuovoP);

        assertNotNull(risultato);
        assertTrue(risultato.getIdUtente() > 0, "L'ID deve essere generato anche senza login");
    }

    @Test
    @DisplayName("ERRORE: Validazione dati ancora attiva su utente non loggato")
    void testValidazioneSenzaLogin() {
        Paziente pErrato = new Paziente(0, "", "Rossi", "1990-01-01", "CF_CORTO", "Via Roma");

        assertThrows(IllegalArgumentException.class, () -> {
            compilazioneTriage.gestisciAnagraficaPaziente(pErrato);
        }, "La validazione deve comunque bloccare dati incompleti");
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
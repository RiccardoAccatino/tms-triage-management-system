package model.test;

import dbManager.db;
import model.LoginService;
import model.Sessione;
import model.dao.DottoreDao;
import model.dao.PazienteDao;
import model.dao.SegretarioDao;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {

    private LoginService loginService;
    private DottoreDao dottoreDao;
    private PazienteDao pazienteDao;
    private SegretarioDao segretarioDao;

    @BeforeEach
    void setUp() {
        db.initializeDb(); // Assicura che il DB sia pronto

        loginService = new LoginService();
        dottoreDao = new DottoreDao();
        pazienteDao = new PazienteDao();
        segretarioDao = new SegretarioDao();

        // IMPORTANTE: Puliamo la sessione prima di ogni test per partire da zero
        Sessione.getInstance().logout();
    }

    @AfterEach
    void tearDown() {
        // Pulizia post-test
        Sessione.getInstance().logout();
    }

    @Test
    void testLoginDottore() {
        // 1. Creiamo un dottore nel DB
        Dottore doc = new Dottore(0, "Gregory", "House", "1960-05-15", "DOC-LOGIN", "Mattina", 1);
        dottoreDao.save(doc);
        int idGenerato = doc.getIdUtente();

        // 2. Tentiamo il login con l'ID appena generato
        boolean esito = loginService.login(idGenerato);

        // 3. Verifiche
        assertTrue(esito, "Il login deve avere successo per un ID valido");
        assertNotNull(Sessione.getInstance().getUtenteLoggato(), "L'utente deve essere salvato in sessione");
        assertTrue(Sessione.getInstance().isDottore(), "L'utente loggato deve essere riconosciuto come Dottore");
        assertEquals("House", Sessione.getInstance().getUtenteLoggato().getCognome());
    }

    @Test
    void testLoginPaziente() {
        // 1. Creiamo un paziente
        Paziente paz = new Paziente(0, "Mario", "Rossi", "1990-01-01", "CF-LOGIN-TEST", "Via Login");
        pazienteDao.save(paz);
        int idGenerato = paz.getIdUtente();

        // 2. Login
        boolean esito = loginService.login(idGenerato);

        // 3. Verifiche
        assertTrue(esito);
        assertTrue(Sessione.getInstance().isPaziente(), "Deve essere riconosciuto come Paziente");
        assertEquals("CF-LOGIN-TEST", Sessione.getInstance().getPaziente().getCodiceFiscale());
    }

    @Test
    void testLoginSegretario() {
        // 1. Creiamo un segretario
        Segretario seg = new Segretario(0, "Anna", "Verdi", "1985-05-05", 3);
        segretarioDao.save(seg);
        int idGenerato = seg.getIdUtente();

        // 2. Login
        boolean esito = loginService.login(idGenerato);

        // 3. Verifiche
        assertTrue(esito);
        assertTrue(Sessione.getInstance().isSegretario(), "Deve essere riconosciuto come Segretario");
        assertEquals(3, Sessione.getInstance().getSegretario().getLivelloPermessi());
    }

    @Test
    void testLoginFallito() {
        // Usiamo un ID sicuramente inesistente (es. negativo o molto alto se hai pochi dati)
        int idInesistente = -999;

        boolean esito = loginService.login(idInesistente);

        assertFalse(esito, "Il login deve fallire con ID inesistente");
        assertNull(Sessione.getInstance().getUtenteLoggato(), "La sessione deve rimanere vuota");
    }

    @Test
    void testLogout() {
        // 1. Loggiamo qualcuno prima
        Paziente p = new Paziente(0, "Test", "Logout", "2000-01-01", "LOGOUT-TEST", "Via");
        pazienteDao.save(p);
        loginService.login(p.getIdUtente());

        assertNotNull(Sessione.getInstance().getUtenteLoggato(), "Precondizione: utente loggato");

        // 2. Eseguiamo Logout
        loginService.logout();

        // 3. Verifica
        assertNull(Sessione.getInstance().getUtenteLoggato(), "Dopo il logout la sessione deve essere null");
    }
}
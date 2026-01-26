package model;

import model.dao.DottoreDao;
import model.dao.PazienteDao;
import model.dao.SegretarioDao;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Segretario;

public class LoginService {

    private final DottoreDao dottoreDao;
    private final PazienteDao pazienteDao;
    private final SegretarioDao segretarioDao;

    public LoginService() {
        this.dottoreDao = new DottoreDao();
        this.pazienteDao = new PazienteDao();
        this.segretarioDao = new SegretarioDao();
    }

    /**
     * Tenta il login cercando l'ID in tutte le tabelle utente.
     * Se trova una corrispondenza, imposta la Sessione.
     *
     * @param idUtente L'ID inserito dall'utente
     * @return true se il login ha successo, false altrimenti
     */
    public boolean login(int idUtente) {
        // 1. Proviamo a vedere se è un DOTTORE
        Dottore dottore = dottoreDao.get(idUtente);
        if (dottore != null) {
            Sessione.getInstance().setUtenteLoggato(dottore);
            System.out.println("Login effettuato: DOTTORE -> " + dottore.getCognome());
            return true;
        }

        // 2. Proviamo a vedere se è un PAZIENTE
        Paziente paziente = pazienteDao.get(idUtente);
        if (paziente != null) {
            Sessione.getInstance().setUtenteLoggato(paziente);
            System.out.println("Login effettuato: PAZIENTE -> " + paziente.getCognome());
            return true;
        }

        // 3. Proviamo a vedere se è un SEGRETARIO
        Segretario segretario = segretarioDao.get(idUtente);
        if (segretario != null) {
            Sessione.getInstance().setUtenteLoggato(segretario);
            System.out.println("Login effettuato: SEGRETARIO -> Livello " + segretario.getLivelloPermessi());
            return true;
        }

        // 4. Nessun utente trovato con questo ID
        System.out.println("Login fallito: Nessun utente con ID " + idUtente);
        return false;
    }

    /**
     * Esegue il logout chiamando il metodo della Sessione.
     */
    public void logout() {
        Sessione.getInstance().logout();
        System.out.println("Logout effettuato.");
    }
}
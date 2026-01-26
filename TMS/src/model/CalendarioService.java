package model;

import model.dao.VisitaDao;
import model.pojo.Visita;
import java.util.List;

/**
 * Gestisce la logica per la visione del calendario - RF4
 * Aggiornato con gestione Sessione e Sicurezza.
 *
 * @autor Angie Riccardo
 */
public class CalendarioService {
    private VisitaDao visitaDao;

    public CalendarioService() {
        this.visitaDao = new VisitaDao();
    }

    /**
     * Ritorna tutte le visite.
     * Accessibile SOLO al Segretario.
     * @throws SecurityException se l'utente non è un segretario.
     */
    public List<Visita> getEventiGlobali() {
        Sessione s = Sessione.getInstance();

        if (s.getUtenteLoggato() == null) {
            throw new SecurityException("Nessun utente loggato.");
        }

        if (!s.isSegretario()) {
            throw new SecurityException("Accesso Negato: Solo i segretari possono vedere il calendario globale.");
        }

        return visitaDao.getAll();
    }

    /**
     * Ritorna solo le visite del medico attualmente loggato.
     * Accessibile SOLO ai Dottori.
     * @throws SecurityException se l'utente non è un dottore.
     */
    public List<Visita> getEventiPersonali() {
        Sessione s = Sessione.getInstance();

        if (s.getUtenteLoggato() == null) {
            throw new SecurityException("Nessun utente loggato.");
        }

        if (!s.isDottore()) {
            throw new SecurityException("Accesso Negato: Solo i dottori possono accedere al calendario personale.");
        }

        // Recupero sicuro dell'ID dalla sessione
        int idDottore = s.getDottore().getIdUtente();

        return visitaDao.getByDottore(idDottore);
    }
}
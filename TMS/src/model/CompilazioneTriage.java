package model;

import model.dao.PazienteDao;
import model.pojo.Paziente;
import java.util.List;
import java.util.regex.Pattern;

public class CompilazioneTriage {

    private PazienteDao pazienteDao;
    private static final String DATA_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final int CF_LENGHT = 16;

    public CompilazioneTriage() {
        this.pazienteDao = new PazienteDao();
    }

    /**
     * Gestisce l'identificazione o registrazione del paziente.
     * MODIFICA: Rimosso il controllo dei permessi per consentire l'uso pubblico.
     */
    public Paziente gestisciAnagraficaPaziente(Paziente pNuovo) {
        // Il controllo checkPermessiPersonale() è stato rimosso per permettere l'accesso non loggato

        // VALIDAZIONE DEI DATI
        validaDatiPaziente(pNuovo);

        // LOGICA DI BUSINESS (Recupero o Salvataggio)
        List<Paziente> tuttiPazienti = pazienteDao.getAll();

        for (Paziente esistente : tuttiPazienti) {
            if (esistente.getCodiceFiscale().equalsIgnoreCase(pNuovo.getCodiceFiscale())) {
                return esistente;
            }
        }

        try {
            pazienteDao.save(pNuovo);
            return pNuovo;
        } catch (Exception e) {
            throw new RuntimeException("Errore tecnico durante la registrazione: " + e.getMessage());
        }
    }

    private void validaDatiPaziente(Paziente p) {
        if (p == null) throw new IllegalArgumentException("Dati paziente assenti.");
        if (isVuoto(p.getNome())) throw new IllegalArgumentException("Il nome è obbligatorio.");
        if (isVuoto(p.getCognome())) throw new IllegalArgumentException("Il cognome è obbligatorio.");
        if (isVuoto(p.getIndirizzo())) throw new IllegalArgumentException("L'indirizzo è obbligatorio.");
        if (isVuoto(p.getCodiceFiscale())) throw new IllegalArgumentException("Il codice fiscale è obbligatorio.");
        if (isVuoto(p.getDataNascita())) throw new IllegalArgumentException("La data di nascita è obbligatoria.");

        if (!Pattern.matches(DATA_REGEX, p.getDataNascita().trim())) {
            throw new IllegalArgumentException("La data deve essere nel formato YYYY-MM-DD (es. 1990-12-31).");
        }

        if (p.getCodiceFiscale().trim().length() != CF_LENGHT) {
            throw new IllegalArgumentException("Il Codice Fiscale deve essere di esattamente 16 caratteri.");
        }
    }

    private boolean isVuoto(String s) {
        return s == null || s.trim().isEmpty();
    }
}
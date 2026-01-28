package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import model.CalendarioService;
import model.Sessione;
import model.pojo.Visita;

import java.io.IOException;
import java.util.List;

public class MenuController {
    @FXML
    private ListView<String> listaAppuntamenti;

    private final CalendarioService calendarioService = new CalendarioService();

    /**
     * Metodo chiamato automaticamente da JavaFX quando carica l'FXML.
     * Serve per popolare il calendario se ci troviamo in quella schermata.
     */
    @FXML
    public void initialize() {
        // Se listaAppuntamenti non è null, significa che siamo nella schermata PannelloCalendario
        if (listaAppuntamenti != null) {
            caricaDatiCalendario();
        }
    }

    // --- NAVIGAZIONE ---

    @FXML
    public void ApriCalendarioGenerale(ActionEvent actionEvent) {
        // Usato dal Segretario per vedere tutti gli eventi
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void VisualizzaCalendario(ActionEvent actionEvent) {
        // Usato dal Dottore per vedere i propri eventi
        navigaVerso("PannelloCalendario");
    }

    @FXML
    public void CreaNuovoTicket(ActionEvent actionEvent) {
        // Porta alla schermata di creazione ticket (Triage)
        navigaVerso("PannelloTriage");
    }

    @FXML
    public void AccettaTicket(ActionEvent actionEvent) {
        // Porta alla schermata di gestione coda (Accettazione)
        navigaVerso("PannelloAccettazione");
    }

    @FXML
    public void EffettuaLogout(ActionEvent actionEvent) {
        // 1. Resetta l'utente loggato nella sessione
        Sessione.getInstance().logout();

        System.out.println("Logout effettuato con successo.");

        // 2. Torna alla schermata principale (Login)
        navigaVerso("PannelloUtente");
    }

    @FXML
    public void TornaAlMenu(ActionEvent actionEvent) {
        // Controlla chi è loggato per tornare al menu corretto
        if (Sessione.getInstance().isDottore()) {
            navigaVerso("PannelloDottore");
        } else if (Sessione.getInstance().isSegretario()) {
            navigaVerso("PannelloSegretario");
        } else {
            // Fallback: se per errore non c'è sessione o è un paziente, torna alla home
            navigaVerso("PannelloUtente");
        }
    }

    // --- LOGICA INTERNA ---

    private void navigaVerso(String fxml) {
        try {
            GuiMain.setRoot(fxml);
        } catch (IOException e) {
            mostraErrore("Impossibile caricare la schermata: " + fxml);
            e.printStackTrace();
        }
    }

    private void caricaDatiCalendario() {
        listaAppuntamenti.getItems().clear();
        List<Visita> visite;

        try {
            // Se è Segretario -> Vede tutto (Globali)
            if (Sessione.getInstance().isSegretario()) {
                visite = calendarioService.getEventiGlobali();
            }
            // Se è Dottore -> Vede solo i suoi (Personali)
            else if (Sessione.getInstance().isDottore()) {
                visite = calendarioService.getEventiPersonali();
            } else {
                return;
            }

            if (visite.isEmpty()) {
                listaAppuntamenti.getItems().add("Nessun appuntamento in programma.");
            } else {
                for (Visita v : visite) {
                    // Formattiamo la stringa da mostrare in lista
                    String riga = String.format("Visita #%d - %s (Sala: %s)",
                            v.getIdVisita(), v.getDataOraInizio(), v.getSala());
                    listaAppuntamenti.getItems().add(riga);
                }
            }

        } catch (SecurityException e) {
            mostraErrore("Permesso negato: " + e.getMessage());
        } catch (Exception e) {
            mostraErrore("Errore caricamento dati: " + e.getMessage());
        }
    }

    private void mostraErrore(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
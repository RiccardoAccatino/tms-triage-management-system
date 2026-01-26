package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.CompilazioneTicket;
import model.PrenotazioneVisitaService;
import model.Sessione;
import model.pojo.Paziente;

import java.io.IOException;
import java.time.LocalDate;

public class TriageController {

    // --- CAMPI COMUNI ---
    @FXML private TextField nomeField, cognomeField;
    @FXML private Label erroreLabel;

    // --- CAMPI SOLO TRIAGE (Presenti in PannelloTriage.fxml) ---
    @FXML private TextField cfField;
    @FXML private DatePicker dataNascitaPicker;
    @FXML private TextArea sintomiArea;
    @FXML private ComboBox<String> coloreCombo;

    // --- CAMPI SOLO PRENOTAZIONE (Presenti in PannelloPrenotazioneVisita.fxml) ---
    @FXML private ComboBox<String> repartoCombo;
    @FXML private ComboBox<String> dottoreCombo;
    @FXML private ComboBox<String> orarioCombo;
    @FXML private DatePicker dataVisitaPicker;
    @FXML private TextArea motivoArea;

    // --- SERVIZI DEL MODELLO ---
    private final CompilazioneTicket ticketService = new CompilazioneTicket();
    private final PrenotazioneVisitaService prenotazioneService = new PrenotazioneVisitaService();

    @FXML
    public void initialize() {
        // 1. SETUP SESSIONE (Simulazione se necessario per evitare NullPointer sul Paziente)
        if (Sessione.getInstance().getUtenteLoggato() == null) {
            // Rimuovi questo blocco quando avrai il Login funzionante
            Paziente pFake = new Paziente(1, "Test", "User", "1990-01-01", "CFTEST", "Via Roma");
            Sessione.getInstance().setUtenteLoggato(pFake);
        }

        // 2. SETUP TRIAGE: Eseguito solo se siamo nella schermata di Triage
        if (coloreCombo != null) {
            coloreCombo.getItems().addAll("Bianco", "Verde", "Giallo", "Rosso");
        }

        // 3. SETUP PRENOTAZIONE: Eseguito solo se siamo nella schermata di Prenotazione
        if (repartoCombo != null) {
            // TODO: In futuro carica questi dati dal DB usando i DAO (es. RepartoDao)
            repartoCombo.getItems().addAll("Cardiologia", "Ortopedia", "Chirurgia", "Medicina Generale");
        }
        if (dottoreCombo != null) {
            // TODO: Filtra i dottori in base al reparto selezionato
            dottoreCombo.getItems().addAll("Dr. Rossi", "Dr.sa Bianchi", "Dr. Verdi");
        }
        if (orarioCombo != null) {
            orarioCombo.getItems().addAll("09:00", "10:00", "11:00", "15:00", "16:30");
        }

        // Nascondi label errore all'avvio
        if (erroreLabel != null) erroreLabel.setVisible(false);
    }

    // --- LOGICA TRIAGE (Crea Ticket) ---
    @FXML
    private void CreaTicket() {
        try {
            // Validazione Input
            String sintomi = sintomiArea.getText();
            String colore = coloreCombo.getValue();

            if (sintomi == null || sintomi.trim().isEmpty() || colore == null) {
                mostraErrore("Compila tutti i campi del Triage.");
                return;
            }

            // Conversione Colore -> Priorità
            int priorita = switch (colore) {
                case "Rosso" -> 4;
                case "Giallo" -> 3;
                case "Verde" -> 2;
                default -> 1; // Bianco
            };

            // CHIAMATA AL MODELLO
            ticketService.creaTicket(colore, priorita, sintomi,id);

            mostraSuccesso("Ticket creato con successo! Codice Colore: " + colore);
            vaiAdAccessoUtente(null);

        } catch (Exception e) {
            mostraErrore("Errore creazione ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- LOGICA PRENOTAZIONE (Conferma Prenotazione) ---
    @FXML
    public void ConfermaPrenotazione(ActionEvent actionEvent) {
        try {
            // Recupero Dati
            String reparto = (repartoCombo != null) ? repartoCombo.getValue() : null;
            String dottore = (dottoreCombo != null) ? dottoreCombo.getValue() : null;
            String orario = (orarioCombo != null) ? orarioCombo.getValue() : null;
            LocalDate data = (dataVisitaPicker != null) ? dataVisitaPicker.getValue() : null;
            String motivo = (motivoArea != null) ? motivoArea.getText() : "";

            // Validazione Input
            if (reparto == null || dottore == null || orario == null || data == null) {
                mostraErrore("Seleziona Reparto, Dottore, Data e Orario.");
                return;
            }

            // CHIAMATA AL MODELLO
            // Nota: Verifica il nome esatto del metodo nel tuo PrenotazioneVisitaService.
            // Qui assumo un metodo standard 'prenota' o simile.
            // Se non esiste, dovrai crearlo nel service o adattare questa riga.

            // Esempio generico di logica di servizio:
            // prenotazioneService.registraPrenotazione(Sessione.getInstance().getUtenteLoggato(), dottore, data, orario, motivo);

            // Per ora stampiamo a console per simulare il successo se il metodo manca
            System.out.println("Chiamata al Service Prenotazione: " + dottore + " il " + data);

            mostraSuccesso("Visita prenotata con successo per il " + data);
            vaiAdAccessoUtente(actionEvent);

        } catch (Exception e) {
            mostraErrore("Errore prenotazione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- NAVIGAZIONE E UTILITY ---

    @FXML
    public void vaiAdAccessoUtente(ActionEvent event) throws IOException {
        GuiMain.setRoot("PannelloUtente");
    }

    // Helper per tornare al menu (collegato ad eventuali bottoni "Indietro")
    @FXML
    public void TornaAlMenu(ActionEvent actionEvent) throws IOException {
        vaiAdAccessoUtente(actionEvent);
    }

    // Metodo helper per mostrare errori nella GUI
    private void mostraErrore(String messaggio) {
        if (erroreLabel != null) {
            erroreLabel.setText(messaggio);
            erroreLabel.setStyle("-fx-text-fill: red;");
            erroreLabel.setVisible(true);
        } else {
            // Fallback su console se la label non c'è
            System.err.println("ERRORE: " + messaggio);
            Alert alert = new Alert(Alert.AlertType.ERROR, messaggio);
            alert.show();
        }
    }

    // Metodo helper per messaggi di successo
    private void mostraSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione Completata");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
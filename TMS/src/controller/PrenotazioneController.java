package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.*;
import model.dao.DottoreDao;
import model.dao.PazienteDao;
import model.pojo.Paziente;
import model.pojo.Ticket;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.pojo.Dottore;
import java.time.LocalDate;
import java.util.Optional;

import java.io.IOException;
import java.util.List;

public class PrenotazioneController {

    // --- CAMPI COMUNI ---
    @FXML private TextField nomeField, cognomeField;
    @FXML private Label erroreLabel;

    // --- CAMPI SOLO PRENOTAZIONE (Presenti in PannelloPrenotazioneVisita.fxml) ---
    @FXML private ComboBox<String> repartoCombo;
    @FXML private ComboBox<String> dottoreCombo;
    @FXML private ComboBox<String> orarioCombo;
    @FXML private DatePicker dataVisitaPicker;
    @FXML private TextArea motivoArea;

    // --- CAMPI SOLO ACCETTAZIONE (Presenti in PannelloAccettazione.fxml) ---
    @FXML private VBox ticketsContainer;

    // --- SERVIZI DEL MODELLO ---
    private final CompilazioneTicket ticketService = new CompilazioneTicket();
    private final PrenotazioneVisitaService prenotazioneService = new PrenotazioneVisitaService();
    private final accRifTicket accettazioneService = new accRifTicket();


    @FXML
    public void initialize() {
        // 1. SETUP SESSIONE (Simulazione se necessario per evitare NullPointer sul Paziente)
        if (Sessione.getInstance().getUtenteLoggato() == null) {
            Paziente pFake = new Paziente(1, "Test", "User", "1990-01-01", "0000000000000000", "Via Roma");
            Sessione.getInstance().setUtenteLoggato(pFake);
        }

        // 2. SETUP PRENOTAZIONE: Eseguito solo se siamo nella schermata di Prenotazione
        if (repartoCombo != null) {
            repartoCombo.getItems().addAll("Cardiologia", "Ortopedia", "Chirurgia", "Medicina Generale");
        }
        if (dottoreCombo != null) {
            dottoreCombo.getItems().addAll("Dr. Rossi", "Dr.sa Bianchi", "Dr. Verdi");
        }
        if (orarioCombo != null) {
            orarioCombo.getItems().addAll("09:00", "10:00", "11:00", "15:00", "16:30");
        }

        // 3. SETUP ACCETTAZIONE: Carica i ticket se il container è presente
        if (ticketsContainer != null) {
            caricaTicketsDalDb();
        }

        // Nascondi label errore all'avvio
        if (erroreLabel != null) erroreLabel.setVisible(false);
    }

    // --- NUOVA LOGICA: CARICAMENTO TICKET DAL DB ---

    private void caricaTicketsDalDb() {
        if (ticketsContainer == null) return;

        ticketsContainer.getChildren().clear();

        try {
            // Recupera i ticket con stato "IN_ATTESA" dal database tramite il service
            List<Ticket> tickets = accettazioneService.getTicketsInAttesa();

            if (tickets.isEmpty()) {
                Label emptyLabel = new Label("Nessun ticket in attesa di accettazione.");
                emptyLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
                ticketsContainer.getChildren().add(emptyLabel);
                return;
            }

            for (Ticket t : tickets) {
                ticketsContainer.getChildren().add(creaCardTicket(t));
            }

        } catch (SecurityException e) {
            mostraErrore("Accesso negato: Solo il segretario può visualizzare i ticket.");
        } catch (Exception e) {
            mostraErrore("Errore nel caricamento ticket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private HBox creaCardTicket(Ticket t) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(5);
        Label lblId = new Label("TICKET #" + t.getIdTicket() + " - Paziente: " + t.getIdPaziente());
        lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblPriorita = new Label("Priorità: " + t.getColore());
        Label lblSintomi = new Label("Sintomi: " + t.getSintomi());
        lblSintomi.setWrapText(true);

        info.getChildren().addAll(lblId, lblPriorita, lblSintomi);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnAccetta = new Button("Accetta");
        btnAccetta.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        btnAccetta.setOnAction(e -> processaSceltaTicket(t.getIdTicket(), true));

        Button btnRifiuta = new Button("Rifiuta");
        btnRifiuta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        btnRifiuta.setOnAction(e -> processaSceltaTicket(t.getIdTicket(), false));

        card.getChildren().addAll(info, btnAccetta, btnRifiuta);
        return card;
    }

    private void processaSceltaTicket(int id, boolean accettato) {
        try {
            accettazioneService.valutaTicket(id, accettato);
            caricaTicketsDalDb(); // Refresh della lista
            mostraSuccesso("Ticket " + (accettato ? "accettato" : "rifiutato") + " con successo.");



        } catch (Exception e) {
            mostraErrore("Errore durante l'operazione: " + e.getMessage());
        }
    }

    // --- LOGICA PRENOTAZIONE (Conferma Prenotazione) ---
    @FXML
    public void ConfermaPrenotazione(ActionEvent actionEvent) {
        try {
            // 1. Recupero Dati dal form
            String nomeReparto = (repartoCombo != null) ? repartoCombo.getValue() : null;
            String nomeDottore = (dottoreCombo != null) ? dottoreCombo.getValue() : null;
            String orario = (orarioCombo != null) ? orarioCombo.getValue() : null;
            LocalDate data = (dataVisitaPicker != null) ? dataVisitaPicker.getValue() : null;
            String motivo = (motivoArea != null) ? motivoArea.getText() : "";

            String nomeInput = nomeField.getText().trim();
            String cognomeInput = cognomeField.getText().trim();

            // 2. Validazione
            if (nomeReparto == null || nomeDottore == null || orario == null || data == null || nomeInput.isEmpty() || cognomeInput.isEmpty()) {
                mostraErrore("Compila tutti i campi per prenotare la visita.");
                return;
            }

            // 3. Gestione Paziente (Trova o Crea)
            PazienteDao pazienteDao = new PazienteDao();
            Paziente pazienteScelto = null;
            for (Paziente p : pazienteDao.getAll()) {
                if (p.getNome().equalsIgnoreCase(nomeInput) && p.getCognome().equalsIgnoreCase(cognomeInput)) {
                    pazienteScelto = p;
                    break;
                }
            }

            if (pazienteScelto == null) {
                pazienteScelto = new Paziente(0, nomeInput, cognomeInput, "1990-01-01", "CF-TEMP-" + System.currentTimeMillis(), "Indirizzo Temp");
                pazienteDao.save(pazienteScelto);
            }

            // 4. Preparazione Dati per il Service
            // Combinazione Data + Orario nel formato corretto per il DB
            String dataOraString = data.toString() + " " + orario; // Es: 2023-11-01 09:00

            // *** QUI SIMULIAMO GLI ID *** // (Nel tuo progetto dovresti recuperare l'ID vero dall'oggetto selezionato nella ComboBox)
            int idDottore = 1; // Default Dottore ID
            int idReparto = 1; // Default Reparto ID
            if(nomeDottore.contains("Bianchi")) idDottore = 2; // Esempio logica
            if(nomeReparto.contains("Ortopedia")) idReparto = 2;

            // Creazione oggetto richiesta
            PrenotazioneVisita richiesta = new PrenotazioneVisita(
                    pazienteScelto.getIdUtente(),
                    idDottore,
                    idReparto,
                    dataOraString,
                    motivo
            );

            // 5. CHIAMATA AL NUOVO SERVICE
            boolean esito = prenotazioneService.registraPrenotazioneDiretta(richiesta);

            if (esito) {
                mostraSuccesso("Visita confermata!\nStato: ACCETTATO\nData: " + dataOraString);
                vaiAdAccessoUtente(actionEvent);
            } else {
                mostraErrore("Errore tecnico durante il salvataggio della visita.");
            }

        } catch (Exception e) {
            mostraErrore("Errore imprevisto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- NAVIGAZIONE E UTILITY ---

    @FXML
    public void vaiAdAccessoUtente(ActionEvent event) throws IOException {
        GuiMain.setRoot("PannelloUtente");
    }

    @FXML
    public void TornaAlMenu(ActionEvent actionEvent) throws IOException {
        // Se siamo nel pannello segretario, torna al menu segretario, altrimenti utente
        if (ticketsContainer != null) {
            GuiMain.setRoot("PannelloSegretario");
        } else {
            vaiAdAccessoUtente(actionEvent);
        }
    }

    @FXML
    public void CaricaDati(ActionEvent actionEvent) {
        caricaTicketsDalDb();
    }

    private void mostraErrore(String messaggio) {
        if (erroreLabel != null) {
            erroreLabel.setText(messaggio);
            erroreLabel.setStyle("-fx-text-fill: red;");
            erroreLabel.setVisible(true);
        } else {
            System.err.println("ERRORE: " + messaggio);
            Alert alert = new Alert(Alert.AlertType.ERROR, messaggio);
            alert.show();
        }
    }

    private void mostraSuccesso(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operazione Completata");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }


}

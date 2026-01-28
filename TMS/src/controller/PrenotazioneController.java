package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.*;
import model.dao.*;
import model.pojo.Reparto;
import model.pojo.Dottore;
import model.pojo.Paziente;
import model.pojo.Ticket;
import model.pojo.Visita;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PrenotazioneController {

    // --- CAMPI COMUNI ---
    @FXML private TextField nomeField, cognomeField;
    @FXML private Label erroreLabel;

    // --- CAMPI SOLO PRENOTAZIONE ---
    @FXML private ComboBox<Reparto> repartoCombo;
    @FXML private ComboBox<Dottore> dottoreCombo;
    @FXML private ComboBox<String> orarioCombo;
    @FXML private DatePicker dataVisitaPicker;
    @FXML private TextArea motivoArea;

    // --- CAMPI SOLO ACCETTAZIONE ---
    @FXML private VBox ticketsContainer;

    // --- SERVIZI ---
    private final PrenotazioneVisitaService prenotazioneService = new PrenotazioneVisitaService();
    private final accRifTicket accettazioneService = new accRifTicket();

    @FXML
    public void initialize() {
        DottoreDao dottoreDao = new DottoreDao();
        RepartoDao repartoDao = new RepartoDao();

        if (repartoCombo != null) {
            repartoCombo.getItems().addAll(repartoDao.getAll());
            repartoCombo.setConverter(new StringConverter<Reparto>() {
                @Override
                public String toString(Reparto r) {
                    return (r != null) ? r.getNome() : " ";
                }

                @Override
                public Reparto fromString(String s) {
                    return null;
                }
            });
        }

        if (dottoreCombo != null) {
            dottoreCombo.getItems().addAll(dottoreDao.getAll());
            dottoreCombo.setConverter(new StringConverter<Dottore>() {
                @Override
                public String toString(Dottore d) {
                    return (d != null) ? d.getNome() + " " + d.getCognome() : "";
                }
                @Override
                public Dottore fromString(String s) {
                    return null;
                }
            });
        }


        if (orarioCombo != null) {
            orarioCombo.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                    "11:00", "11:30", "12:00", "14:00", "14:30", "15:00",
                    "15:30", "16:00", "16:30", "17:00");
            orarioCombo.getSelectionModel().selectFirst();
        }

        if (ticketsContainer != null) {
            caricaTicketsDalDb();
        }

        if (erroreLabel != null) erroreLabel.setVisible(false);
    }

    // --- GESTIONE LISTA TICKET ---
    private void caricaTicketsDalDb() {
        if (ticketsContainer == null) return;
        ticketsContainer.getChildren().clear();

        try {
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
        } catch (Exception e) {
            mostraErrore("Errore nel caricamento ticket: " + e.getMessage());
        }
    }

    private HBox creaCardTicket(Ticket t) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(5);
        Label lblId = new Label("TICKET #" + t.getIdTicket() + " - Paziente ID: " + t.getIdPaziente());
        lblId.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label lblPriorita = new Label("Priorità: " + t.getColore());
        Label lblSintomi = new Label("Sintomi: " + t.getSintomi());
        lblSintomi.setWrapText(true);

        info.getChildren().addAll(lblId, lblPriorita, lblSintomi);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button btnAccetta = new Button("Accetta");
        btnAccetta.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
        // Qui chiamiamo il metodo che apre il dialog
        btnAccetta.setOnAction(e -> mostraDialogAssegnazione(t.getIdTicket()));

        Button btnRifiuta = new Button("Rifiuta");
        btnRifiuta.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
        btnRifiuta.setOnAction(e -> rifiutaTicketDiretto(t.getIdTicket()));


        card.getChildren().addAll(info, btnAccetta, btnRifiuta);
        return card;
    }

    // --- LOGICA "COLLASSATA" NEL CONTROLLER ---

    /**
     * Mostra il dialog e gestisce INTERAMENTE la logica di salvataggio
     * (Update Ticket + Insert Visita) senza passare dal Service.
     */


    private void mostraDialogAssegnazione(int idTicket) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Assegnazione Visita");
        dialog.setHeaderText("Seleziona Dottore e Orario per il Ticket #" + idTicket);

        ButtonType confirmButtonType = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Setup Dottori
        ComboBox<Dottore> comboDottori = new ComboBox<>();
        DottoreDao dottoreDao = new DottoreDao();
        comboDottori.getItems().addAll(dottoreDao.getAll());
        comboDottori.setConverter(new StringConverter<Dottore>() {
            @Override
            public String toString(Dottore d) {
                return (d != null) ? d.getNome() + " " + d.getCognome() : "";
            }
            @Override
            public Dottore fromString(String s) { return null; }
        });

        // Setup Orari
        ComboBox<String> comboOrari = new ComboBox<>();
        comboOrari.getItems().addAll("08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "14:00", "14:30", "15:00",
                "15:30", "16:00", "16:30", "17:00");
        comboOrari.getSelectionModel().selectFirst();

        grid.add(new Label("Dottore:"), 0, 0);
        grid.add(comboDottori, 1, 0);
        grid.add(new Label("Orario (Oggi):"), 0, 1);
        grid.add(comboOrari, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> dialogButton == confirmButtonType);

        Optional<Boolean> result = dialog.showAndWait();

        result.ifPresent(confermato -> {
            Dottore docScelto = comboDottori.getValue();
            String oraScelta = comboOrari.getValue();

            if (docScelto == null || oraScelta == null) {
                mostraErrore("Devi selezionare un dottore e un orario!");
                return;
            }

            try {
                // 1. Instanziamo i DAO necessari
                TicketDao ticketDao = new TicketDao();
                VisitaDao visitaDao = new VisitaDao();
                accRifTicket accettazione = new accRifTicket();

                // 2. Aggiorniamo il Ticket
                Ticket ticket = ticketDao.get(idTicket);

                accettazione.valutaTicket(idTicket,true);


                // 3. Creiamo la Visita
                Visita nuovaVisita = new Visita();
                nuovaVisita.setIdTicket(idTicket);
                // Attenzione: se ticket è null qui crasherebbe, ma assumiamo esista
                nuovaVisita.setIdPaziente(ticket.getIdPaziente());
                nuovaVisita.setIdDottore(docScelto.getIdUtente());
                nuovaVisita.setIdReparto(docScelto.getIdReparto());

                String dataOra = LocalDate.now().toString() + " " + oraScelta;
                nuovaVisita.setDataOraInizio(dataOra);
                nuovaVisita.setDataOraFine(dataOra); // Per semplicità
                nuovaVisita.setSala("Ambulatorio " + docScelto.getIdReparto());

                visitaDao.save(nuovaVisita);
                // --- FINE LOGICA DI BUSINESS DIRETTA ---

                mostraSuccesso("Ticket Accettato e Visita creata con successo!");
                caricaTicketsDalDb(); // Refresh UI

            } catch (Exception e) {
                mostraErrore("Errore DB: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void rifiutaTicketDiretto(int idTicket) {
        accRifTicket accettazione = new accRifTicket();;
        try {
                accettazione.valutaTicket(idTicket,false);
                caricaTicketsDalDb();

        } catch (Exception e) {
            mostraErrore("Errore: " + e.getMessage());
        }
    }



    // --- PRENOTAZIONE ---
    @FXML
    public void ConfermaPrenotazione(ActionEvent actionEvent) {
        try {
            String nomeReparto = (repartoCombo != null) ? String.valueOf(repartoCombo.getValue()) : null;
            String nomeDottore = (dottoreCombo != null) ? String.valueOf(dottoreCombo.getValue()) : null;
            String orario = (orarioCombo != null) ? orarioCombo.getValue() : null;
            LocalDate data = (dataVisitaPicker != null) ? dataVisitaPicker.getValue() : null;
            String motivo = (motivoArea != null) ? motivoArea.getText() : "";
            String nomeInput = nomeField.getText().trim();
            String cognomeInput = cognomeField.getText().trim();

            if (nomeReparto == null || nomeDottore == null || orario == null || data == null || nomeInput.isEmpty() || cognomeInput.isEmpty()) {
                mostraErrore("Compila tutti i campi per prenotare la visita.");
                return;
            }

            PazienteDao pazienteDao = new PazienteDao();
            Paziente pazienteScelto = null;
            for (Paziente p : pazienteDao.getAll()) {
                if (p.getNome().equalsIgnoreCase(nomeInput) && p.getCognome().equalsIgnoreCase(cognomeInput)) {
                    pazienteScelto = p;
                    break;
                }
            }

            if (pazienteScelto == null) {
                pazienteScelto = new Paziente(0, nomeInput, cognomeInput, "0000-00-00", "CF-TEMP-" + System.currentTimeMillis(), "Indirizzo Temp");
                pazienteDao.save(pazienteScelto);
            }

            String dataOraString = data.toString() + " " + orario;
            int idDottore = 1;
            int idReparto = 1;
            if(nomeDottore.contains("Bianchi")) idDottore = 2;
            if(nomeReparto.contains("Ortopedia")) idReparto = 2;

            PrenotazioneVisita richiesta = new PrenotazioneVisita(
                    pazienteScelto.getIdUtente(), idDottore, idReparto, dataOraString, motivo
            );

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
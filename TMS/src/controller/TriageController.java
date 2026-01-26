package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.CompilazioneTriage;
import model.CompilazioneTicket;
import model.pojo.Paziente;
import model.pojo.Ticket;

public class TriageController {

    @FXML private TextField nomeField, cognomeField, cfField;
    @FXML private DatePicker dataNascitaPicker;
    @FXML private TextArea sintomiArea;
    @FXML private ComboBox<String> coloreCombo;
    @FXML private Label erroreLabel;

    private CompilazioneTriage triageService = new CompilazioneTriage();
    private CompilazioneTicket ticketService = new CompilazioneTicket();

    @FXML
    public void initialize() {
        // Popola la combo dei colori (livelli di urgenza)
        coloreCombo.getItems().addAll("Bianco", "Verde", "Giallo", "Rosso");
    }

    @FXML
    private void CreaTicket() {
        try {
            // 1. Creiamo l'oggetto Paziente dai dati della View
            Paziente p = new Paziente(0, nomeField.getText(), cognomeField.getText(),
                    dataNascitaPicker.getValue().toString(),
                    cfField.getText(), "Indirizzo non specificato");

            // 2. Usiamo il Model per gestire l'anagrafica
            Paziente salvato = triageService.gestisciAnagraficaPaziente(p);

            // 3. Creiamo il Ticket di triage
            ticketService.creaTicket(coloreCombo.getValue(), 1, sintomiArea.getText());

            erroreLabel.setText("Ticket creato con successo!");
            erroreLabel.setStyle("-fx-text-fill: green;");
            erroreLabel.setVisible(true);
        } catch (Exception e) {
            erroreLabel.setText("Errore: " + e.getMessage());
            erroreLabel.setVisible(true);
        }
    }


}
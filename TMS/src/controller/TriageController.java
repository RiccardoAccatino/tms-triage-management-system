package controller;

import gui.vista.GuiMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.CompilazioneTicket;
import model.CompilazioneTriage;
import model.pojo.Paziente;
import model.pojo.Ticket;

import java.io.IOException;

public class TriageController {

    @FXML
    private TextField nomeField;

    @FXML
    private TextField cognomeField;

    @FXML
    private TextField cfField;

    @FXML
    private DatePicker dataNascitaPicker;

    @FXML
    private TextField indirizzo;


    @FXML
    private TextArea sintomiArea;

    @FXML
    private ComboBox<Integer> coloreCombo;

    @FXML
    private Label erroreLabel;

    private final CompilazioneTriage compilazioneTriage = new CompilazioneTriage();
    private final CompilazioneTicket compilazioneTicket = new CompilazioneTicket();

    // Inizializzazione del ComboBox livelloDolore con i valori accettabili
    @FXML
    private void initialize() {
        coloreCombo.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10); // Livelli da 1 a 10
    }

    @FXML
    private void CreaTicket(ActionEvent event) {
        try {
            // Recupero e validazione dati paziente
            Paziente paziente = new Paziente();
            paziente.setNome(nomeField.getText().trim());
            paziente.setCognome(cognomeField.getText().trim());
            paziente.setCodiceFiscale(cfField.getText().trim());
            paziente.setDataNascita(dataNascitaPicker.getValue().toString());
            paziente.setIndirizzo(indirizzo.getText().trim());

            // Registrazione o recupero del paziente dal database tramite la classe CompilazioneTriage
            Paziente pazienteRegistrato = compilazioneTriage.gestisciAnagraficaPaziente(paziente);

            // Recupero livello di dolore selezionato
            Integer livelloDolore = coloreCombo.getValue();
            if (livelloDolore == null) {
                throw new IllegalArgumentException("Selezionare un livello di dolore.");
            }

            // Trasformazione del livello di dolore in priorità e colore
            int priorita = calcolaPrioritaDaLivelloDolore(livelloDolore);
            String colore = determinaColoreDaPriorita(priorita);

            // Recupero i sintomi
            String sintomi = sintomiArea.getText().trim();
            if (sintomi.isEmpty()) {
                throw new IllegalArgumentException("Descrivere i sintomi del paziente.");
            }

            // Creazione del ticket
            Ticket nuovoTicket = compilazioneTicket.creaTicket(colore, priorita, sintomi, paziente.getIdUtente());
            erroreLabel.setVisible(false);

            mostraMessaggioSuccesso("Ticket creato con successo! ID: " + nuovoTicket.getIdTicket());
        } catch (Exception e) {
            erroreLabel.setText(e.getMessage());
            erroreLabel.setVisible(true);
        }
    }

    @FXML
    private void vaiAdAccessoUtente(ActionEvent event) throws IOException {
        // Logica per ritornare alla schermata precedente o accedere a una nuova schermata.
        GuiMain.setRoot("PannelloUtente");
        System.out.println("Eseguito il ritorno indietro.");
    }

    private int calcolaPrioritaDaLivelloDolore(int livelloDolore) {
        if (livelloDolore >= 8) {
            return 2; // Alta priorità
        } else if (livelloDolore >= 4) {
            return 3; // Media priorità
        } else {
            return 4; // Bassa priorità
        }
    }


    private String determinaColoreDaPriorita(int priorita) {
        switch (priorita) {
            case 2:
                return "Arancione"; // Colore arancione per alta priorità
            case 3:
                return "Verde";     // Colore verde per media priorità
            case 4:
                return "Bianco";    // Colore bianco per bassa priorità
            default:
                throw new IllegalArgumentException("Priorità non valida.");
        }
    }

    private void mostraMessaggioSuccesso(String messaggio) {
        // Mostra un messaggio di successo all'utente per la creazione del ticket
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
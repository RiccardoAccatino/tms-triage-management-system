package model.dao;

import dbManager.db;
import model.exception.PazienteException;
import model.pojo.Paziente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PazienteDao implements Dao<Paziente> {

    @Override
    public void save(Paziente paziente) {
        String sqlUtente = "INSERT INTO utente(nome, cognome, dataNascita, tipoUtente) VALUES(?,?,?,?)";
        String sqlPaziente = "INSERT INTO paziente(idUtente, codiceFiscale, indirizzo) VALUES(?,?,?)";

        Connection conn = null;
        try {
            conn = db.connect();
            // Disabilita auto-commit per gestire la transazione manualmente
            conn.setAutoCommit(false);

            // 1. Inserimento in UTENTE
            int idGenerato = -1;
            try (PreparedStatement pstmtU = conn.prepareStatement(sqlUtente, Statement.RETURN_GENERATED_KEYS)) {
                pstmtU.setString(1, paziente.getNome());
                pstmtU.setString(2, paziente.getCognome());
                pstmtU.setString(3, paziente.getDataNascita());
                pstmtU.setString(4, "PAZIENTE"); // Fisso come da tua logica
                pstmtU.executeUpdate();

                ResultSet rs = pstmtU.getGeneratedKeys();
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                    paziente.setIdUtente(idGenerato); // Aggiorna l'oggetto
                }
            }

            if (idGenerato == -1) throw new SQLException("Creazione utente fallita, nessun ID ottenuto.");

            // 2. Inserimento in PAZIENTE
            try (PreparedStatement pstmtP = conn.prepareStatement(sqlPaziente)) {
                pstmtP.setInt(1, idGenerato);
                pstmtP.setString(2, paziente.getCodiceFiscale());
                pstmtP.setString(3, paziente.getIndirizzo());
                pstmtP.executeUpdate();
            }

            // Conferma la transazione
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annulla tutto se qualcosa va storto
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new PazienteException("Errore nel salvataggio del paziente: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Paziente> getAll() {
        // Unisce le due tabelle per ricostruire l'oggetto Paziente
        String sql = "SELECT u.*, p.codiceFiscale, p.indirizzo " +
                "FROM utente u " +
                "JOIN paziente p ON u.idUtente = p.idUtente";
        List<Paziente> pazienti = new ArrayList<>();

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Paziente p = new Paziente(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getString("codiceFiscale"),
                        rs.getString("indirizzo")
                );
                pazienti.add(p);
            }
        } catch (SQLException e) {
            throw new PazienteException("Errore nel recupero pazienti: " + e.getMessage());
        }
        return pazienti;
    }

    @Override
    public Paziente get(int id) {
        String sql = "SELECT u.*, p.codiceFiscale, p.indirizzo " +
                "FROM utente u " +
                "JOIN paziente p ON u.idUtente = p.idUtente " +
                "WHERE u.idUtente = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Paziente(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getString("codiceFiscale"),
                        rs.getString("indirizzo")
                );
            }
        } catch (SQLException e) {
            throw new PazienteException("Errore nel recupero paziente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Paziente paziente) {
        // Simile al save, serve una transazione per aggiornare entrambe le tabelle
        String sqlUtente = "UPDATE utente SET nome=?, cognome=?, dataNascita=? WHERE idUtente=?";
        String sqlPaziente = "UPDATE paziente SET codiceFiscale=?, indirizzo=? WHERE idUtente=?";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false);

            try(PreparedStatement psU = conn.prepareStatement(sqlUtente)) {
                psU.setString(1, paziente.getNome());
                psU.setString(2, paziente.getCognome());
                psU.setString(3, paziente.getDataNascita());
                psU.setInt(4, paziente.getIdUtente());
                psU.executeUpdate();
            }

            try(PreparedStatement psP = conn.prepareStatement(sqlPaziente)) {
                psP.setString(1, paziente.getCodiceFiscale());
                psP.setString(2, paziente.getIndirizzo());
                psP.setInt(3, paziente.getIdUtente());
                psP.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch(SQLException ex) { ex.printStackTrace(); }
            throw new PazienteException("Errore aggiornamento paziente: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch(SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void delete(Paziente paziente) {
        String sqlPaziente = "DELETE FROM paziente WHERE idUtente = ?";
        String sqlUtente = "DELETE FROM utente WHERE idUtente = ?";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false); // Avvia transazione

            // 1. Cancella prima il figlio (Paziente)
            try (PreparedStatement psP = conn.prepareStatement(sqlPaziente)) {
                psP.setInt(1, paziente.getIdUtente());
                psP.executeUpdate();
            }

            // 2. Cancella poi il padre (Utente)
            try (PreparedStatement psU = conn.prepareStatement(sqlUtente)) {
                psU.setInt(1, paziente.getIdUtente());
                psU.executeUpdate();
            }

            conn.commit(); // Conferma modifiche
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new PazienteException("Errore eliminazione paziente: " + e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
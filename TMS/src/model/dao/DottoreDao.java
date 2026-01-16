package model.dao;

import dbManager.db;
import model.exception.DottoreException;
import model.pojo.Dottore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DottoreDao implements Dao<Dottore> {

    @Override
    public void save(Dottore dottore) {
        String sqlUtente = "INSERT INTO utente(nome, cognome, dataNascita, tipoUtente) VALUES(?,?,?,?)";
        String sqlDottore = "INSERT INTO dottore(idUtente, matricola, turni, idReparto) VALUES(?,?,?,?)";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false);

            int idGenerato = -1;
            try (PreparedStatement pstmtU = conn.prepareStatement(sqlUtente, Statement.RETURN_GENERATED_KEYS)) {
                pstmtU.setString(1, dottore.getNome());
                pstmtU.setString(2, dottore.getCognome());
                pstmtU.setString(3, dottore.getDataNascita());
                pstmtU.setString(4, "DOTTORE");
                pstmtU.executeUpdate();

                ResultSet rs = pstmtU.getGeneratedKeys();
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                    dottore.setIdUtente(idGenerato);
                }
            }

            if (idGenerato == -1) throw new SQLException("Creazione dottore fallita, ID non generato.");

            try (PreparedStatement pstmtD = conn.prepareStatement(sqlDottore)) {
                pstmtD.setInt(1, idGenerato);
                pstmtD.setString(2, dottore.getMatricola());
                pstmtD.setString(3, dottore.getTurni());
                pstmtD.setInt(4, dottore.getIdReparto());
                pstmtD.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new DottoreException("Errore salvataggio dottore: " + e.getMessage());
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public List<Dottore> getAll() {
        String sql = "SELECT u.*, d.matricola, d.turni, d.idReparto " +
                "FROM utente u JOIN dottore d ON u.idUtente = d.idUtente";
        List<Dottore> dottori = new ArrayList<>();

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dottore d = new Dottore(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getString("matricola"),
                        rs.getString("turni"),
                        rs.getInt("idReparto")
                );
                dottori.add(d);
            }
        } catch (SQLException e) {
            throw new DottoreException("Errore recupero dottori: " + e.getMessage());
        }
        return dottori;
    }

    @Override
    public Dottore get(int id) {
        String sql = "SELECT u.*, d.matricola, d.turni, d.idReparto " +
                "FROM utente u JOIN dottore d ON u.idUtente = d.idUtente WHERE u.idUtente = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Dottore(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getString("matricola"),
                        rs.getString("turni"),
                        rs.getInt("idReparto")
                );
            }
        } catch (SQLException e) {
            throw new DottoreException("Errore recupero dottore: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Dottore dottore) {
        String sqlUtente = "UPDATE utente SET nome=?, cognome=?, dataNascita=? WHERE idUtente=?";
        String sqlDottore = "UPDATE dottore SET matricola=?, turni=?, idReparto=? WHERE idUtente=?";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(sqlUtente)) {
                psU.setString(1, dottore.getNome());
                psU.setString(2, dottore.getCognome());
                psU.setString(3, dottore.getDataNascita());
                psU.setInt(4, dottore.getIdUtente());
                psU.executeUpdate();
            }

            try (PreparedStatement psD = conn.prepareStatement(sqlDottore)) {
                psD.setString(1, dottore.getMatricola());
                psD.setString(2, dottore.getTurni());
                psD.setInt(3, dottore.getIdReparto());
                psD.setInt(4, dottore.getIdUtente());
                psD.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch(SQLException ex) { ex.printStackTrace(); }
            throw new DottoreException("Errore aggiornamento dottore: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch(SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void delete(Dottore dottore) {
        String sql = "DELETE FROM utente WHERE idUtente = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dottore.getIdUtente());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DottoreException("Errore eliminazione dottore: " + e.getMessage());
        }
    }
}
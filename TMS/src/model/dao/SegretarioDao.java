package model.dao;

import dbManager.db;
import model.exception.SegretarioException;
import model.pojo.Segretario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SegretarioDao implements Dao<Segretario> {

    @Override
    public void save(Segretario segretario) {
        String sqlUtente = "INSERT INTO utente(nome, cognome, dataNascita, tipoUtente) VALUES(?,?,?,?)";
        String sqlSegretario = "INSERT INTO segretario(idUtente, livelloPermessi) VALUES(?,?)";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false);

            int idGenerato = -1;
            try (PreparedStatement pstmtU = conn.prepareStatement(sqlUtente, Statement.RETURN_GENERATED_KEYS)) {
                pstmtU.setString(1, segretario.getNome());
                pstmtU.setString(2, segretario.getCognome());
                pstmtU.setString(3, segretario.getDataNascita());
                pstmtU.setString(4, "SEGRETARIO");
                pstmtU.executeUpdate();

                ResultSet rs = pstmtU.getGeneratedKeys();
                if (rs.next()) {
                    idGenerato = rs.getInt(1);
                    segretario.setIdUtente(idGenerato);
                }
            }

            if (idGenerato == -1) throw new SQLException("ID Segretario non generato.");

            try (PreparedStatement pstmtS = conn.prepareStatement(sqlSegretario)) {
                pstmtS.setInt(1, idGenerato);
                pstmtS.setInt(2, segretario.getLivelloPermessi());
                pstmtS.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch(SQLException ex) { ex.printStackTrace(); }
            throw new SegretarioException("Errore salvataggio segretario: " + e.getMessage());
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch(SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public List<Segretario> getAll() {
        String sql = "SELECT u.*, s.livelloPermessi FROM utente u JOIN segretario s ON u.idUtente = s.idUtente";
        List<Segretario> lista = new ArrayList<>();

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Segretario(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getInt("livelloPermessi")
                ));
            }
        } catch (SQLException e) {
            throw new SegretarioException("Errore recupero segretari: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Segretario get(int id) {
        String sql = "SELECT u.*, s.livelloPermessi FROM utente u JOIN segretario s ON u.idUtente = s.idUtente WHERE u.idUtente = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Segretario(
                        rs.getInt("idUtente"),
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("dataNascita"),
                        rs.getInt("livelloPermessi")
                );
            }
        } catch (SQLException e) {
            throw new SegretarioException("Errore recupero segretario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Segretario segretario) {
        String sqlUtente = "UPDATE utente SET nome=?, cognome=?, dataNascita=? WHERE idUtente=?";
        String sqlSegretario = "UPDATE segretario SET livelloPermessi=? WHERE idUtente=?";

        Connection conn = null;
        try {
            conn = db.connect();
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(sqlUtente)) {
                psU.setString(1, segretario.getNome());
                psU.setString(2, segretario.getCognome());
                psU.setString(3, segretario.getDataNascita());
                psU.setInt(4, segretario.getIdUtente());
                psU.executeUpdate();
            }
            try (PreparedStatement psS = conn.prepareStatement(sqlSegretario)) {
                psS.setInt(1, segretario.getLivelloPermessi());
                psS.setInt(2, segretario.getIdUtente());
                psS.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch(SQLException ex) { ex.printStackTrace(); }
            throw new SegretarioException("Errore aggiornamento segretario: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch(SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void delete(Segretario segretario) {
        String sql = "DELETE FROM utente WHERE idUtente = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, segretario.getIdUtente());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SegretarioException("Errore eliminazione segretario: " + e.getMessage());
        }
    }
}
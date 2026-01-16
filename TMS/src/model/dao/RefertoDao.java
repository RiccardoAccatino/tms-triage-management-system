package model.dao;

import dbManager.db;
import model.exception.RefertoException;
import model.pojo.Referto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefertoDao implements Dao<Referto> {

    @Override
    public List<Referto> getAll() {
        List<Referto> referti = new ArrayList<>();
        String sql = "SELECT * FROM referto";
        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                referti.add(new Referto(
                        rs.getInt("idReferto"),
                        rs.getString("diagnosi"),
                        rs.getString("prognosi"),
                        rs.getString("prescrizioni"),
                        rs.getString("dataCreazione"),
                        rs.getInt("idVisita")
                ));
            }
        } catch (SQLException e) {
            throw new RefertoException("Errore recupero referti: " + e.getMessage());
        }
        return referti;
    }

    @Override
    public Referto get(int id) {
        String sql = "SELECT * FROM referto WHERE idReferto = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Referto(
                        rs.getInt("idReferto"),
                        rs.getString("diagnosi"),
                        rs.getString("prognosi"),
                        rs.getString("prescrizioni"),
                        rs.getString("dataCreazione"),
                        rs.getInt("idVisita")
                );
            }
        } catch (SQLException e) {
            throw new RefertoException("Errore recupero referto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Referto referto) {
        String sql = "INSERT INTO referto(diagnosi, prognosi, prescrizioni, dataCreazione, idVisita) VALUES(?,?,?,?,?)";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, referto.getDiagnosi());
            pstmt.setString(2, referto.getPrognosi());
            pstmt.setString(3, referto.getPrescrizioni());
            pstmt.setString(4, referto.getDataCreazione());
            pstmt.setInt(5, referto.getIdVisita());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                referto.setIdReferto(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RefertoException("Errore salvataggio referto: " + e.getMessage());
        }
    }

    @Override
    public void update(Referto referto) {
        String sql = "UPDATE referto SET diagnosi=?, prognosi=?, prescrizioni=?, dataCreazione=?, idVisita=? WHERE idReferto=?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, referto.getDiagnosi());
            pstmt.setString(2, referto.getPrognosi());
            pstmt.setString(3, referto.getPrescrizioni());
            pstmt.setString(4, referto.getDataCreazione());
            pstmt.setInt(5, referto.getIdVisita());
            pstmt.setInt(6, referto.getIdReferto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RefertoException("Errore aggiornamento referto: " + e.getMessage());
        }
    }

    @Override
    public void delete(Referto referto) {
        String sql = "DELETE FROM referto WHERE idReferto = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, referto.getIdReferto());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RefertoException("Errore eliminazione referto: " + e.getMessage());
        }
    }
}
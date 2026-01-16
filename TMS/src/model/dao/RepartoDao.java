package model.dao;

import dbManager.db;
import model.exception.RepartoException;
import model.pojo.Reparto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class RepartoDao implements Dao<Reparto> {

    @Override
    public List<Reparto> getAll() {
        List<Reparto> reparti = new ArrayList<>();
        String sql = "SELECT * FROM reparto";

        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reparto r = new Reparto(
                        rs.getInt("idReparto"),
                        rs.getString("nome"),
                        rs.getString("codice"),
                        rs.getInt("sale")
                );
                reparti.add(r);
            }
        } catch (SQLException e) {
            throw new RepartoException("Errore nel recupero dei reparti: " + e.getMessage());
        }
        return reparti;
    }

    @Override
    public Reparto get(int id) {
        String sql = "SELECT * FROM reparto WHERE idReparto = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Reparto(
                        rs.getInt("idReparto"),
                        rs.getString("nome"),
                        rs.getString("codice"),
                        rs.getInt("sale")
                );
            }
        } catch (SQLException e) {
            throw new RepartoException("Errore nel recupero del reparto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Reparto reparto) {
        String sql = "INSERT INTO reparto(nome, codice, sale) VALUES(?,?,?)";

        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reparto.getNome());
            pstmt.setString(2, reparto.getCodice());
            pstmt.setInt(3, reparto.getSale());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepartoException("Errore nel salvataggio del reparto: " + e.getMessage());
        }
    }

    @Override
    public void update(Reparto reparto) {
        String sql = "UPDATE reparto SET nome = ?, codice = ?, sale = ? WHERE idReparto = ?";

        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, reparto.getNome());
            pstmt.setString(2, reparto.getCodice());
            pstmt.setInt(3, reparto.getSale());
            pstmt.setInt(4, reparto.getIdReparto());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepartoException("Errore nell'aggiornamento del reparto: " + e.getMessage());
        }
    }

    @Override
    public void delete(Reparto reparto) {
        String sql = "DELETE FROM reparto WHERE idReparto = ?";

        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reparto.getIdReparto());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RepartoException("Errore nella cancellazione del reparto: " + e.getMessage());
        }
    }
}
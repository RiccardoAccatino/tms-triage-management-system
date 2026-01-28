package model.dao;

import dbManager.db;
import model.exception.VisitaException;
import model.pojo.Visita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author angie riccardo
 */
public class VisitaDao implements Dao<Visita> {

    @Override
    public List<Visita> getAll() {
        List<Visita> visite = new ArrayList<>();
        String sql = "SELECT * FROM visita ";
        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                visite.add(new Visita(
                        rs.getInt("idVisita"),
                        rs.getString("dataOraInizio"),
                        rs.getString("dataOraFine"),
                        rs.getString("sala"),
                        rs.getInt("idTicket"),
                        rs.getInt("idDottore"),
                        rs.getInt("idPaziente"),
                        rs.getInt("idReparto")
                ));
            }
        } catch (SQLException e) {
            throw new VisitaException("Errore recupero visite: " + e.getMessage());
        }
        return visite;
    }

    /**
     * Recupera tutte le visite
     */
    @Override
    public Visita get(int id) {
        String sql = "SELECT * FROM visita WHERE idVisita = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Visita(
                        rs.getInt("idVisita"),
                        rs.getString("dataOraInizio"),
                        rs.getString("dataOraFine"),
                        rs.getString("sala"),
                        rs.getInt("idTicket"),
                        rs.getInt("idDottore"),
                        rs.getInt("idPaziente"),
                        rs.getInt("idReparto")
                );
            }
        } catch (SQLException e) {
            throw new VisitaException("Errore recupero visita: " + e.getMessage());
        }
        return null;
    }

    /**
     * Salva la visita nel DB
     */
    @Override
    public void save(Visita visita) {
        String sql = "INSERT INTO visita(dataOraInizio, dataOraFine, sala, idTicket, idDottore, idPaziente, idReparto) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, visita.getDataOraInizio());
            pstmt.setString(2, visita.getDataOraFine());
            pstmt.setString(3, visita.getSala());
            pstmt.setInt(4, visita.getIdTicket());
            pstmt.setInt(5, visita.getIdDottore());
            pstmt.setInt(6, visita.getIdPaziente());
            pstmt.setInt(7, visita.getIdReparto());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                visita.setIdVisita(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new VisitaException("Errore salvataggio visita: " + e.getMessage());
        }
    }


    @Override
    public void update(Visita visita) {
        String sql = "UPDATE visita SET dataOraInizio=?, dataOraFine=?, sala=?, idTicket=?, idDottore=?, idPaziente=?, idReparto=? WHERE idVisita=?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, visita.getDataOraInizio());
            pstmt.setString(2, visita.getDataOraFine());
            pstmt.setString(3, visita.getSala());
            pstmt.setInt(4, visita.getIdTicket());
            pstmt.setInt(5, visita.getIdDottore());
            pstmt.setInt(6, visita.getIdPaziente());
            pstmt.setInt(7, visita.getIdReparto());
            pstmt.setInt(8, visita.getIdVisita());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VisitaException("Errore aggiornamento visita: " + e.getMessage());
        }
    }

    @Override
    public void delete(Visita visita) {
        String sql = "DELETE FROM visita WHERE idVisita = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, visita.getIdVisita());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new VisitaException("Errore eliminazione visita: " + e.getMessage());
        }
    }

    /**
     * Recupera tutte le visite assegnate a un dottore specifico.
     * Utile per la vista calendario "Personale" del medico - RF4
     */
    public List<Visita> getByDottore(int idDottore) {
        List<Visita> visite = new ArrayList<>();
        String sql = "SELECT * FROM visita WHERE idDottore = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idDottore);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                visite.add(new Visita(
                        rs.getInt("idVisita"),
                        rs.getString("dataOraInizio"),
                        rs.getString("dataOraFine"),
                        rs.getString("sala"),
                        rs.getInt("idTicket"),
                        rs.getInt("idDottore"),
                        rs.getInt("idPaziente"),
                        rs.getInt("idReparto")
                ));
            }
        } catch (SQLException e) {
            throw new VisitaException("Errore recupero visite per dottore: " + e.getMessage());
        }
        return visite;
    }
}
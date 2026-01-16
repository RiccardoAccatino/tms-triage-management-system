package model.dao;

import dbManager.db;
import model.exception.TicketException;
import model.pojo.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDao implements Dao<Ticket> {

    @Override
    public List<Ticket> getAll() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket";
        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tickets.add(new Ticket(
                        rs.getInt("idTicket"),
                        rs.getString("colore"),
                        rs.getInt("priorita"),
                        rs.getString("sintomi"),
                        rs.getString("stato"),
                        rs.getString("timestamp"),
                        rs.getInt("idPaziente")
                ));
            }
        } catch (SQLException e) {
            throw new TicketException("Errore recupero tickets: " + e.getMessage());
        }
        return tickets;
    }

    @Override
    public Ticket get(int id) {
        String sql = "SELECT * FROM ticket WHERE idTicket = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Ticket(
                        rs.getInt("idTicket"),
                        rs.getString("colore"),
                        rs.getInt("priorita"),
                        rs.getString("sintomi"),
                        rs.getString("stato"),
                        rs.getString("timestamp"),
                        rs.getInt("idPaziente")
                );
            }
        } catch (SQLException e) {
            throw new TicketException("Errore recupero ticket: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO ticket(colore, priorita, sintomi, stato, timestamp, idPaziente) VALUES(?,?,?,?,?,?)";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ticket.getColore());
            pstmt.setInt(2, ticket.getPriorita());
            pstmt.setString(3, ticket.getSintomi());
            pstmt.setString(4, ticket.getStato());
            pstmt.setString(5, ticket.getTimestamp());
            pstmt.setInt(6, ticket.getIdPaziente());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                ticket.setIdTicket(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new TicketException("Errore salvataggio ticket: " + e.getMessage());
        }
    }

    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE ticket SET colore=?, priorita=?, sintomi=?, stato=?, timestamp=?, idPaziente=? WHERE idTicket=?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ticket.getColore());
            pstmt.setInt(2, ticket.getPriorita());
            pstmt.setString(3, ticket.getSintomi());
            pstmt.setString(4, ticket.getStato());
            pstmt.setString(5, ticket.getTimestamp());
            pstmt.setInt(6, ticket.getIdPaziente());
            pstmt.setInt(7, ticket.getIdTicket());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TicketException("Errore aggiornamento ticket: " + e.getMessage());
        }
    }

    @Override
    public void delete(Ticket ticket) {
        String sql = "DELETE FROM ticket WHERE idTicket = ?";
        try (Connection conn = db.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticket.getIdTicket());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new TicketException("Errore eliminazione ticket: " + e.getMessage());
        }
    }
}
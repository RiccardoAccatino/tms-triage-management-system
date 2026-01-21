package model;

import model.dao.TicketDao;
import model.pojo.Ticket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CompilazioneTicket {

    private TicketDao ticketDao = new TicketDao();

    public CompilazioneTicket(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Ticket creaTicket(int idPaziente, String colore, int priorita, String sintomi) {

        Ticket nuovoTicket = new Ticket();

        nuovoTicket.setIdPaziente(idPaziente);
        nuovoTicket.setColore(colore);
        nuovoTicket.setPriorita(priorita);
        nuovoTicket.setSintomi(sintomi);

        nuovoTicket.setStato("IN_ATTESA");

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        nuovoTicket.setTimestamp(now.format(formatter));

        ticketDao.save(nuovoTicket);

        return nuovoTicket;
    }
}
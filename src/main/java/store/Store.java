package store;

import model.Account;
import model.Ticket;

import java.sql.SQLException;
import java.util.Collection;

public interface Store {

    Collection<Ticket> findAllTicker();

    void save(Ticket ticket) throws SQLException;

    int save(Account account) throws SQLException;

    Ticket findByTicket(int id);
}

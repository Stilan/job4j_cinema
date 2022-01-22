package store;

import model.Account;
import model.Ticket;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class DbStore implements Store {

    private static final Logger LOG = LogManager.getLogger(DbStore.class.getName());

    private final BasicDataSource pool = new BasicDataSource();

    private DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        DbStore.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new DbStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    @Override
    public Collection<Ticket> findAllTicker()  {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(new Ticket(it.getInt("id"),
                            Integer.parseInt(it.getString("session_id")),
                            it.getInt("row"),
                            it.getInt("cell"),
                            Integer.parseInt(it.getString("account_id"))));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return tickets;
    }

    @Override
    public Ticket findByTicket(int id)  {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Ticket(it.getInt("id"),
                            Integer.parseInt(it.getString("session_id")),
                            it.getInt("row"),
                            it.getInt("cell"),
                            Integer.parseInt(it.getString("account_id")));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;

    }
    @Override
    public void save(Ticket ticket) throws SQLException {
        if (ticket.getId() == 0) {
             create(ticket);
        } else {
            update(ticket);
        }

    }

    @Override
    public int save(Account account) throws SQLException {
        if (account.getId() == 0) {
            account = create(account);
        } else {
            update(account);
        }
        return account.getId();
    }

    private Account create(Account account) throws SQLException {
        try (Connection cn  = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        }
        return account;
    }

    private void update(Account account) throws SQLException {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE ticket SET username = ?, email = ?, phone = ? WHERE id = ?")
        ) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setInt(4, account.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void create(Ticket ticket) throws SQLException {
        try (Connection cn  = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO ticket(session_id, row, cell, account_id) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccountId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt(1));
                }
            }
        }

    }

    private void update(Ticket ticket) throws SQLException {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE ticket SET session_id = ?, row = ?, cell = ?, account_id = ? WHERE id = ?")
        ) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccountId());
            ps.setInt(5, ticket.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}

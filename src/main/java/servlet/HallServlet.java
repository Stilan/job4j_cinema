package servlet;

import model.Account;
import model.Ticket;
import store.DbStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;



public class HallServlet extends HttpServlet {

    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; character=utf-8");
        OutputStream out = resp.getOutputStream();
        String json = GSON.toJson(DbStore.instOf().findAllTicker());
        out.write(json.getBytes(StandardCharsets.UTF_8));
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Account account = null;
        int idAcc = 0;
        try {
          idAcc = DbStore.instOf().save(new Account(0,
                    req.getParameter("username"),
                    req.getParameter("email"),
                    req.getParameter("phone")));
            DbStore.instOf().save(new Ticket(
                    0, 1, Integer.parseInt(req.getParameter("row")),
                    Integer.parseInt(req.getParameter("cell")),
                    idAcc
            ));

        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect("error.html");
        }

    }
}

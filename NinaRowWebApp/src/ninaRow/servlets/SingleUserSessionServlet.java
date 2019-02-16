package ninaRow.servlets;

import com.google.gson.Gson;
import ninaRow.managers.UserManager;
import ninaRow.utils.ServletUtils;
import ninaRow.utils.SessionUtils;
import player.Player;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "SingleUserSession", urlPatterns = {"/singleusersession"})
public class SingleUserSessionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String usernameFromSession = SessionUtils.getUsername(request);

        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Player currentPlayer = userManager.getUserByName(usernameFromSession);
        Gson gson = new Gson();
        String userAsJson = gson.toJson(currentPlayer);
        try (PrintWriter out = response.getWriter()) {
            out.println(userAsJson);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

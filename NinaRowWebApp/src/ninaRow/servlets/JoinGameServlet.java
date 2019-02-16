package ninaRow.servlets;

import com.google.gson.Gson;
import engine.NinaRowEngine;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
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

@WebServlet(name = "JoinGame", urlPatterns = {"/joingame"})
public class JoinGameServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String gameTitle = request.getParameter(Constants.GAME_TITLE);
            Gson gson = new Gson();
            GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
            NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
            String usernameFromSession = SessionUtils.getUsername(request);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Player currentPlayer = userManager.getUserByName(usernameFromSession);
            engine.addPlayer(currentPlayer);
            if (engine.getPlayers().size() == engine.getTotalPlayers()) {
                engine.setGameRunning();
            }

            String jsonResponse = gson.toJson(usernameFromSession + "join to " + gameTitle);
            out .println(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

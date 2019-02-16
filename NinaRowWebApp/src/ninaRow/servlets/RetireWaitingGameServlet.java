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

@WebServlet(name = "RetireWaitingGameServlet", urlPatterns = {"/pages/game/retireWaitingGame"})
public class RetireWaitingGameServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        String mode = request.getParameter("mode");
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
        String leavingPlayerName = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Player leavingPlayer = userManager.getUserByName(leavingPlayerName);
        if (mode.equals("player")) {
            engine.retireWaitingGame(leavingPlayer);
        }
        else {
            engine.removeWatcher(leavingPlayerName);
        }

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String message = gson.toJson("out");
            out.println(message);
            out.flush();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

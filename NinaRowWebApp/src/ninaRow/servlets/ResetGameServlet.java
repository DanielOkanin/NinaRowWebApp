package ninaRow.servlets;

import engine.NinaRowEngine;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ResetGameServlet", urlPatterns = {"/pages/game/resetGame"})
public class ResetGameServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
        engine.resetGame();
        gameRoomsManager.getGameChat(gameTitle).reset();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

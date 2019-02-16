package ninaRow.servlets;

import com.google.gson.Gson;
import engine.GameStatus;
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
import java.io.PrintWriter;

@WebServlet(name = "GameStatusServlet", urlPatterns = {"/pages/game/gameStatus"})
public class GameStatusServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GameStatus gameStatus = engine.getGameStatus();
            String statusAsJson = gson.toJson(gameStatus);
            out.println(statusAsJson);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

package ninaRow.servlets;

import com.google.gson.Gson;
import engine.NinaRowEngine;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;
import turn.Turn;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TurnsHistoryServlet", urlPatterns = {"/pages/game/turnsHistory"})
public class TurnsHistoryServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        int turnsIndex = Integer.parseInt(request.getParameter("turnsIndex"));

        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);

        List<Turn> turnsList = engine.getTurnsHistory(turnsIndex);
        if (turnsList != null) {
            List<String> turnsListAsStrings = new ArrayList<>();

            for (Turn turn : turnsList) {
                turnsListAsStrings.add(turn.toString());
            }

            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String turnsAsJson = gson.toJson(turnsListAsStrings);
                out.println(turnsAsJson);
                out.flush();
            }
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

package ninaRow.servlets;

import com.google.gson.Gson;
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

@WebServlet(name = "GameStatusCheckServlet", urlPatterns = {"/pages/game/gameStatusCheck"})
public class GameStatusCheckServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
        String statusMessage = getGameStatusMessage(engine);

        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String messageAsJson = gson.toJson(statusMessage);
            out.println(messageAsJson);
            out.flush();
        }
    }

    private String getGameStatusMessage(NinaRowEngine i_Engine) {
        StringBuilder message = new StringBuilder();

        if (i_Engine.getPlayers().stream().filter(i -> i.getIsWinner()).count() == 0) {
            message.append("Tie. Boars is full.");
        }
        else {
            i_Engine.getPlayers().stream().filter(i -> i.getIsWinner()).forEach
                    (i -> message.append(i.getName() + " Wins" + System.lineSeparator()));
        }

        return message.toString();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

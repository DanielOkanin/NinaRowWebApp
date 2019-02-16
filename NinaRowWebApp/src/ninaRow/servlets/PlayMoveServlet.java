package ninaRow.servlets;

import com.google.gson.Gson;
import engine.NinaRowEngine;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;
import ninaRow.utils.SessionUtils;
import turn.MoveType;
import turn.Turn;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "PlayMoveServlet", urlPatterns = {"/pages/game/playmove"})
public class PlayMoveServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        String playerName = request.getParameter("playername");
        MoveType moveType = MoveType.valueOf(request.getParameter("movetype"));
        int colIndex = Integer.parseInt(request.getParameter("colindex"));
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
        String playerToPlayName = SessionUtils.getUsername(request);
        String message = "";
        boolean isValidMove = false;

        if (!playerName.equals(playerToPlayName)) {
            message = "It is not your turn!";
        }
        else {
            int rowToInsert = moveType.equals(MoveType.INSERT_DISC) ?
                    engine.getRowIndexToInsert(colIndex) : engine.getRows() - 1;

            isValidMove = moveType.equals(MoveType.INSERT_DISC) ?
                    rowToInsert != NinaRowEngine.UNDEFINED : engine.isLegalPopoutMove(colIndex);

            message = isValidMove ? "" : (moveType.equals(MoveType.INSERT_DISC) ? "Invalid move. The column is full"
                    : "Invalid move. The column is empty or you try to popout disc that is not your.");

            if (isValidMove) {
                engine.executeMove(new Turn(engine.getPlayers().get(engine.getCurrentPlayerTurn()),
                        moveType, colIndex, rowToInsert));
            }
        }

        Gson gson = new Gson();
        String messageAsJson = gson.toJson(message);
        try (PrintWriter out = response.getWriter()) {
            out.println(messageAsJson);
            out.flush();
        }
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

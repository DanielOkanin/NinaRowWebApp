package ninaRow.servlets;

import ninaRow.chat.ChatManager;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ClearChatServlet", urlPatterns = {"/pages/game/clearChat"})
public class ClearChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        ChatManager chatManager = gameRoomsManager.getGameChat(gameTitle);
        chatManager.reset();
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

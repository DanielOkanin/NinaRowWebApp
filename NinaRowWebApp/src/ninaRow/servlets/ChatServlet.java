package ninaRow.servlets;

import com.google.gson.Gson;
import ninaRow.chat.ChatManager;
import ninaRow.chat.SingleChatEntry;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ChatServlet", urlPatterns = {"/pages/game/chat"})
public class ChatServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        int index = Integer.parseInt(request.getParameter("index"));
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        ChatManager chatManager = gameRoomsManager.getGameChat(gameTitle);
        List<SingleChatEntry> chatEntries = chatManager.getChatEntries(index);
        List<String> chatEntriesAsStrings = new ArrayList<>();

        if (chatEntries != null) {

            for (SingleChatEntry singleChatEntry : chatEntries) {
                chatEntriesAsStrings.add(singleChatEntry.toString());
            }

            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(chatEntriesAsStrings);
                out.print(jsonResponse);
                out.flush();
            }
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
    

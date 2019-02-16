package ninaRow.servlets;

import com.google.gson.Gson;
import ninaRow.chat.ChatManager;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;
import ninaRow.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "SendChat", urlPatterns = {"/pages/game/sendChat"})
public class SendChatServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String gameTitle = request.getParameter(Constants.GAME_TITLE);
        String chatMessage = request.getParameter("chatMessage");
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        ChatManager chatManager = gameRoomsManager.getGameChat(gameTitle);

        if (chatMessage != "") {
            logServerMessage("Adding chat string from " + username + ": " + chatMessage);
            chatManager.addChatString(chatMessage, username);

            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(chatMessage);
                out.println(jsonResponse);
                out.flush();
            }
        }
    }

    private void logServerMessage(String message) {
        System.out.println(message);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

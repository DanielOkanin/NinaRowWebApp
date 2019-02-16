package ninaRow.servlets;

import com.google.gson.Gson;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "GameRoomsListServlet", urlPatterns = {"/gamerooms"})
public class GameRoomsListServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
//            List<NinaRowEngine> engines = gameRoomsManager.getEngines();
//            GameRooms gameRooms = new GameRooms(engines);
//
//            String jsonResponse = gson.toJson(gameRooms);
//            out.print(jsonResponse);
//            out.flush();
            String jsonResponse = gson.toJson(gameRoomsManager);
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

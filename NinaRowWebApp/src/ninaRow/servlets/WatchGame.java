package ninaRow.servlets;

import com.google.gson.Gson;
import engine.NinaRowEngine;
import ninaRow.constants.Constants;
import ninaRow.managers.GameRoomsManager;
import ninaRow.managers.UserManager;
import ninaRow.utils.ServletUtils;
import ninaRow.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "WatchGame", urlPatterns = {"/watchGame"})
public class WatchGame extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            String gameTitle = request.getParameter(Constants.GAME_TITLE);
            Gson gson = new Gson();
            GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());
            NinaRowEngine engine = gameRoomsManager.getEngine(gameTitle);
            String usernameFromSession = SessionUtils.getUsername(request);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            engine.addWatcher(usernameFromSession);


            String jsonResponse = gson.toJson(usernameFromSession + "watch " + gameTitle);
            out .println(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}

package ninaRow.servlets;


import com.google.gson.Gson;
import engine.NinaRowEngine;
import engine.NinaRowFileAnalyzer;
import engine.XmlGameFileAnalyzed;
import ninaRow.managers.GameRoomsManager;
import ninaRow.utils.ServletUtils;
import ninaRow.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)

@WebServlet(name = "UploadGameFile", urlPatterns = {"/pages/lobby/upload"})
public class UploadGameFileServlet extends HttpServlet {

    private final String LOBBY_URL = "../lobby/lobby.html";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        Collection<Part> parts = request.getParts();
        String status = "";
        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        InputStream fileContentStream = new ByteArrayInputStream(fileContent.toString().getBytes());
        NinaRowFileAnalyzer ninaRowFileAnalyzer = new NinaRowFileAnalyzer(fileContentStream);
        XmlGameFileAnalyzed analyzed = ninaRowFileAnalyzer.analyzeFile();
        GameRoomsManager gameRoomsManager = ServletUtils.getGameRoomsManager(getServletContext());

        if (analyzed.getIsProper()) {
            if (!gameRoomsManager.isGameExist(analyzed.getGameTitle())) {
                NinaRowEngine engine = new NinaRowEngine();
                engine.initEngine(analyzed.getGameDescriptor());
                String usernameFromSession = SessionUtils.getUsername(request);
                gameRoomsManager.addGameEngine(engine, usernameFromSession);
                status = "Game was uploaded successfully";
            } else {
                status = "Game is already exist.";
            }
        } else {
            status = analyzed.getError();
        }

        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        String json = gson.toJson(status);
        out.println(json);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}

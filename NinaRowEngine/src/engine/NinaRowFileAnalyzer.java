package engine;

import engine.gameProperties.GameDescriptor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class NinaRowFileAnalyzer {
    private InputStream m_InputStream;

    public NinaRowFileAnalyzer(InputStream i_InputStream) {
        m_InputStream = i_InputStream;
    }

    public XmlGameFileAnalyzed analyzeFile() {
        JAXBContext jc = null;
        GameDescriptor gameDescriptor = null;
        boolean isProper = false;
        String gameTitle = "";

        try {
            jc = JAXBContext.newInstance("engine.gameProperties");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        Unmarshaller u = null;
        try {
            u = jc.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        StringBuilder errorMessage = new StringBuilder();

        try {
            gameDescriptor = (GameDescriptor) u.unmarshal(m_InputStream);
        } catch (Exception e) {
            errorMessage.append("Not a N in a Row File");
        }

        if (isLegalProperties(gameDescriptor, errorMessage)) {
            isProper = true;
            gameTitle = gameDescriptor.getDynamicPlayers().getGameTitle();
        }

        return new XmlGameFileAnalyzed(gameDescriptor, isProper, errorMessage.toString(), gameTitle);
    }

    private boolean isLegalProperties(GameDescriptor i_Game, StringBuilder i_ErrorMessage) {
        boolean isLegalProperties = true;

        if (i_Game.getDynamicPlayers().getGameTitle() == null || i_Game.getDynamicPlayers().getGameTitle() == "") {
            isLegalProperties = false;
            i_ErrorMessage.append("Game has no title." + System.lineSeparator());
        }
        if (!isLegalBoard(i_Game, i_ErrorMessage)) {
            isLegalProperties = false;
        }
        if (!isLegalPlayers(i_Game, i_ErrorMessage)) {
            isLegalProperties = false;
        }

        return isLegalProperties;
    }

    private boolean isLegalPlayers(GameDescriptor i_Game, StringBuilder i_ErrorMessage) {
        boolean isLegalPlayers = true;
        int totalPlayers = i_Game.getDynamicPlayers().getTotalPlayers();

        if (totalPlayers < NinaRowEngine.MIN_PLAYERS || totalPlayers > NinaRowEngine.MAX_PLAYERS) {
            isLegalPlayers = false;
            i_ErrorMessage.append("Total players number is illegal." + System.lineSeparator());
        }
        return isLegalPlayers;
    }

    private boolean isLegalBoard(GameDescriptor i_Game, StringBuilder i_ErrorMessage) {
        boolean isLegalBoardRows = false, isLegalBoardColumns = false, isLegalBoardTarget = false;

        if (i_Game.getGame().getBoard().getRows() >= NinaRowEngine.MIN_ROWS && i_Game.getGame().getBoard().getRows() <= NinaRowEngine.MAX_ROWS) {
            isLegalBoardRows = true;
        }
        else {
            i_ErrorMessage.append("Board's rows number is illegal.").append(System.lineSeparator());

        }
        if (i_Game.getGame().getBoard().getColumns().intValue() >= NinaRowEngine.MIN_COLS && i_Game.getGame().getBoard().getColumns().intValue() <= NinaRowEngine.MAX_COLS) {
            isLegalBoardColumns = true;
        }
        else {
            i_ErrorMessage.append("Board's columns number is illegal.").append(System.lineSeparator());
        }
        if (i_Game.getGame().getTarget().intValue() >= NinaRowEngine.MIN_TARGET && i_Game.getGame().getTarget().intValue() < i_Game.getGame().getBoard().getColumns().intValue() && i_Game.getGame().getTarget().intValue() < i_Game.getGame().getBoard().getRows())
        {
            isLegalBoardTarget = true;
        }
        else {
            i_ErrorMessage.append("Board's target is illegal.").append(System.lineSeparator());
        }

        return isLegalBoardRows && isLegalBoardColumns && isLegalBoardTarget;
    }
}

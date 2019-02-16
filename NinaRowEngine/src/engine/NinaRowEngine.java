package engine;

import engine.gameProperties.GameDescriptor;
import player.Player;
import turn.MoveType;
import turn.Turn;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NinaRowEngine implements Serializable {
    public static final int EMPTY = -1;
    public static final int UNDEFINED = -1;
    public static final int MIN_ROWS = 5;
    public static final int MAX_ROWS = 50;
    public static final int MIN_COLS = 6;
    public static final int MAX_COLS = 30;
    public static final int MIN_TARGET = 2;
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    private int m_BoardRows;
    private int m_BoardCols;
    private int m_Sequence;
    private byte[][] m_Board;
    private Variant m_Variant;
    private int m_TotalPlayers;
    private String m_GameTitle;
    private GameStatus m_Status;
    private List<Player> m_Players = new ArrayList<>();
    private List<String> m_Watchers = new ArrayList<>();
    private final List<Turn> m_TurnsHistory = new ArrayList<Turn>();
    private int m_CurrentTurn;

    public List<Player> getPlayers() {
        return m_Players;
    }

    public int getCurrentPlayerTurn() {
        return m_CurrentTurn;
    }

    public List<Turn> getTurnsHistory() {
        return m_TurnsHistory;
    }

    public List<Turn> getTurnsHistory(int i_Index) {
        if (i_Index < m_TurnsHistory.size()) {
            return m_TurnsHistory.subList(i_Index, m_TurnsHistory.size());
        }
        else {
            return null;
        }
    }

    public byte[][] getBoard() {return m_Board; }

    public GameStatus getGameStatus() { return m_Status; }

    public int getRows() { return m_BoardRows; }

    public int getCols() { return m_BoardCols; }

    public int getSequence() { return m_Sequence; }

    public Variant getVariant () {  return m_Variant; }

    public int getTotalPlayers() {return m_TotalPlayers;}

    public String getGameTitle() {return m_GameTitle;}

    public List<String> getWatchers() {return m_Watchers;}


    private void initBoard() {
        m_Board = new byte[getRows()][getCols()];

        for (int i = 0; i < m_BoardRows; i++)
        {
            for (int j = 0; j < m_BoardCols; j++)
            {
                m_Board[i][j] = EMPTY;
            }
        }
    }

    public void changeTurn() {
        do {
            m_CurrentTurn = (m_CurrentTurn + 1) % m_Players.size();
        } while (m_Players.get(m_CurrentTurn).getIsRetire() || (checkIsBoardFull() && !canCurrentPlayerPopoutDisc()));

//        if (m_UpdateCurrentPlayerNameDelegate != null)
//        {
//            m_UpdateCurrentPlayerNameDelegate.action();
//        }
    }

    public void addPlayer(Player i_Player) {
        m_Players.add(i_Player);
    }

    private Variant getGameVariantFromGameProperties(GameDescriptor i_GameProperties) {
        String variant = i_GameProperties.getGame().getVariant().toUpperCase();
        return Variant.valueOf(variant);
    }


    public String  getPlayersDetails() {
        StringBuilder playersDetails = new StringBuilder();

        for (Player player : m_Players)
        {
            playersDetails.append(System.lineSeparator()).append("---- Player #")
                    .append(m_Players.indexOf(player) + 1).append(" -----").append(System.lineSeparator())
                    .append(player).append(System.lineSeparator());
        }

        return playersDetails.toString();
    }

    public void initEngine(GameDescriptor i_GameDescriptor) {
        m_BoardRows = i_GameDescriptor.getGame().getBoard().getRows();
        m_BoardCols = i_GameDescriptor.getGame().getBoard().getColumns().intValue();
        m_Sequence = i_GameDescriptor.getGame().getTarget().intValue();
        m_Variant = Variant.valueOf(i_GameDescriptor.getGame().getVariant().toUpperCase());
        m_GameTitle = i_GameDescriptor.getDynamicPlayers().getGameTitle();
        m_TotalPlayers = i_GameDescriptor.getDynamicPlayers().getTotalPlayers();
        m_Status = GameStatus.WAITING;
        m_CurrentTurn = -1;
        m_TurnsHistory.clear();
        initBoard();
    }

    public boolean isCurrentTurnIsHuman() {
        return m_Players.get(m_CurrentTurn).getIsHuman();
    }


    public void checkForWinning(int i_ColIndex) {
        for (int i = 0; i < m_BoardRows; i++) {
            if (m_Board[i][i_ColIndex] != EMPTY) {
                checkSequenceForSpecificCell(i, i_ColIndex);
            }
        }
    }

    private void checkSequenceForSpecificCell(int i_Row, int i_Col) {
        checkForRowAndColSequence(i_Row, i_Col);
        checkForDiagonalsSequence(i_Row, i_Col);
    }

    private void checkForRowAndColSequence(int i_Row, int i_Col) {
        int colRightDelimiter, rowBottomDelimiter, rowLength = m_Sequence * 2 - 1,
                colLength = m_Sequence * 2 - 1;
        Point rowStartPosition = new Point(), colStartPosition = new Point();

        colStartPosition.x = i_Col - m_Sequence + 1;
        colStartPosition.y = i_Row;
        rowStartPosition.y = i_Row - m_Sequence + 1;
        rowStartPosition.x = i_Col;

        if (colStartPosition.x < 0)
        {
            colStartPosition.x = m_Variant.equals(Variant.CIRCULAR) ?
                    m_BoardCols + i_Col - m_Sequence + 1 : 0;
        }

        if (rowStartPosition.y < 0)
        {
            rowStartPosition.y = m_Variant.equals(Variant.CIRCULAR) ?
                    m_BoardRows + i_Row - m_Sequence + 1 : 0;
        }

        if (!m_Variant.equals(Variant.CIRCULAR))
        {
            colRightDelimiter = i_Col + m_Sequence - 1 > m_BoardCols - 1 ? m_BoardCols - 1 : i_Col + m_Sequence - 1;
            rowLength = colRightDelimiter - colStartPosition.x + 1;

            rowBottomDelimiter = i_Row + m_Sequence - 1 > m_BoardRows - 1 ? m_BoardRows - 1 : i_Row + m_Sequence - 1;
            colLength = rowBottomDelimiter - rowStartPosition.y + 1;
        }

        checkSequence(rowStartPosition, colLength, 0, 1);
        checkSequence(colStartPosition, rowLength, 1, 0);
    }

    private void checkForDiagonalsSequence(int i_Row, int i_Col) {
        int leftToRightDiagonalLength = 1, rightToLeftDiagonalLength = 1;
        Point leftToRightDiagonalStart = new Point(i_Col, i_Row), rightToLeftDiagonalStart = new Point(i_Col, i_Row);

        for (int i = 1; i <= m_Sequence - 1; i++)
        {
            if (i_Row - i >= 0 && i_Col - i >= 0)
            {
                leftToRightDiagonalStart.x = i_Col - i;
                leftToRightDiagonalStart.y = i_Row - i;
                leftToRightDiagonalLength++;
            }
            if (i_Row + i < m_BoardRows && i_Col + i < getCols())
            {
                leftToRightDiagonalLength++;
            }
            if (i_Row - i >= 0 && i_Col + i < m_BoardCols)
            {
                rightToLeftDiagonalStart.x = i_Col + i;
                rightToLeftDiagonalStart.y = i_Row - i;
                rightToLeftDiagonalLength++;
            }
            if (i_Row + i < m_BoardRows && i_Col - i >=0)
            {
                rightToLeftDiagonalLength++;
            }
        }

        checkSequence(leftToRightDiagonalStart, leftToRightDiagonalLength, 1, 1);
        checkSequence(rightToLeftDiagonalStart, rightToLeftDiagonalLength, -1, 1);
    }

    private void checkSequence(Point i_StartPosition, int i_Length, int i_ColFactor, int i_RowFactor) {
        int[] playersSequenceCounter = new int[m_Players.size()];

        for (int i = 0; i < i_Length; i++) {
            for (int j = 0; j < m_Players.size(); j++) {
                playersSequenceCounter[j] = m_Board[(i_StartPosition.y + i * i_RowFactor) % m_BoardRows][(i_StartPosition.x + i * i_ColFactor) % m_BoardCols] == j ? playersSequenceCounter[j] + 1 : 0;
                if (playersSequenceCounter[j] == m_Sequence) {
                    m_Players.get(j).setIsWinner(true);
                }
            }
        }
    }


    private void updateGameStatus(int i_ColDelimiter) {
        /*
            if the i_ColDelimiter is equal to the number of board cols, it means that a player retired, so
            we check all the board columns.
            else, we check the specific column that is the i_ColDelimiter.
         */
        long numberOfWinners;
        int startCol = i_ColDelimiter == m_BoardCols ? 0 : i_ColDelimiter;
        int endCol = i_ColDelimiter == m_BoardCols ? m_BoardCols : i_ColDelimiter + 1;

        for (int i = startCol; i < endCol; i++)
        {
            checkForWinning(i);
        }

        numberOfWinners = m_Players.stream().filter(i -> i.getIsWinner()).count();

        if (numberOfWinners > 0) {
            if (numberOfWinners > 1) {
                m_Status = GameStatus.TIE;
            }
            else {
                m_Status = GameStatus.WIN;
            }
        }
        else if (!m_Variant.equals(Variant.POPOUT) && checkIsBoardFull()) {
            m_Status = GameStatus.TIE;
        }
    }

    private boolean checkIsBoardFull() {
        boolean isBoardFull = true;

        for (int i = 0; i < getCols(); i ++)
        {
            if (m_Board[0][i] == EMPTY)
            {
                isBoardFull = false;
                break;
            }
        }

        return isBoardFull;
    }

    public int getRowIndexToInsert(int i_ColToInsert) {
        int rowToInsert = UNDEFINED;

        for (int i = m_BoardRows - 1; i >= 0; i --) {
            if (m_Board[i][i_ColToInsert] == EMPTY) {
                rowToInsert = i;
                break;
            }
        }

        return rowToInsert;
    }

    public void playTurnByComputer() {
        List<List<Integer>> possibleMoves = new ArrayList<>();
        List<Integer> colsToPopout = getValidColumnsToPlay(m_BoardRows - 1, m_CurrentTurn);
        List<Integer> colsToInsert = getValidColumnsToPlay(0, EMPTY);
        int randomMoveTypeIndex;
        MoveType moveType;

        if (colsToInsert.size() > 0) {
            possibleMoves.add(colsToInsert);
        }

        if (colsToPopout.size() > 0 && m_Variant.equals(Variant.POPOUT)) {
            possibleMoves.add(colsToPopout);
        }

        randomMoveTypeIndex = new Random().nextInt(possibleMoves.size());
        moveType = colsToInsert.size() == 0 ? MoveType.POPOUT_DISC : (colsToPopout.size() == 0 ? MoveType.INSERT_DISC : MoveType.values()[randomMoveTypeIndex]);
        playRandomMoveByComputer(possibleMoves.get(randomMoveTypeIndex),
                moveType);
    }

    private void playRandomMoveByComputer(List<Integer> i_PossibleColsToPlay, MoveType i_MoveType) {
        int colToPlay = i_PossibleColsToPlay.get(new Random().nextInt(i_PossibleColsToPlay.size())) ;
        int rowToPlay = i_MoveType == MoveType.POPOUT_DISC ?
                m_BoardRows - 1 : getRowIndexToInsert(colToPlay);

        executeMove(new Turn(m_Players.get(m_CurrentTurn), i_MoveType, colToPlay, rowToPlay));
    }

    private List<Integer> getValidColumnsToPlay(int i_RowToCheck, int i_CellValueToCheck) {
        List<Integer> colsToPlay = new ArrayList<>();

        for (int i = 0; i < m_BoardCols; i++) {
            if (m_Board[i_RowToCheck][i] == i_CellValueToCheck) {
                colsToPlay.add(i);
            }
        }

        return colsToPlay;
    }

    public void executeMove(Turn i_newTurn) {
        addTurn(i_newTurn);
        i_newTurn.getPlayer().incrementTurnsCounter();

        int chosenColumn = i_newTurn.getColumn(), chosenRow = i_newTurn.getRow();
        MoveType moveType = i_newTurn.getMoveType();
        byte cellValueBeforeChange = m_Board[chosenRow][chosenColumn];
        m_Board[chosenRow][chosenColumn] = moveType.equals(MoveType.INSERT_DISC) ?
                (byte) m_CurrentTurn : EMPTY;

        if (moveType.equals(MoveType.POPOUT_DISC)) {
            minimizeColAfterRemoveDisc(chosenColumn);
        }

        updateGameStatus(chosenColumn);
        if (m_Status == GameStatus.RUNNING)
        {
           changeTurn();
        }
    }

    public void addTurn(Turn i_newTurn) {
        m_TurnsHistory.add(i_newTurn);
    }

    public void resetGame() {
        m_Status = GameStatus.WAITING;
        m_CurrentTurn = -1;
        m_TurnsHistory.clear();
        resetPlayers();
        m_Players.clear();
        m_Watchers.clear();
        initBoard();
    }

    private void resetPlayers() {
        for (Player player : m_Players) {
            player.resetTurnsCounter();
            player.setIsWinner(false);
            player.setIsRetire(false);
        }
    }

    public boolean isLegalPopoutMove(int i_ColToPopout) {
        return m_Board[m_BoardRows - 1][i_ColToPopout] == m_CurrentTurn;
    }

    public void retireGame() {
        addTurn(new Turn(m_Players.get(m_CurrentTurn), MoveType.RETIRE));
        removeDiscsOfRetiredPlayer();
        reorderBoardAfterRetirement();
        m_Players.get(m_CurrentTurn).setIsRetire(true);
        updateGameStatusAfterRetirement();
        if (m_Status == GameStatus.RUNNING)
        {
            changeTurn();
        }
    }

    private void removeDiscsOfRetiredPlayer() {
        for (int i = 0; i < m_BoardRows; i++) {
            for (int j = 0; j < m_BoardCols; j++) {
                if (m_Board[i][j] == m_CurrentTurn)
                {
                    m_Board[i][j] = EMPTY;
                }
            }
        }
    }

    private void reorderBoardAfterRetirement() {
        for (int i = 0; i < m_BoardCols; i++) {
            minimizeColAfterRemoveDisc(i);
        }
    }

    private void updateGameStatusAfterRetirement() {
        if (m_Players.stream().filter(i -> !i.getIsRetire()).count() < 2)
        {
            m_Status = GameStatus.WIN;
            m_Players.stream().filter(i -> !i.getIsRetire()).forEach(i -> i.setIsWinner(true));
        }
        else
        {
            updateGameStatus(m_BoardCols);
        }
    }

    private void minimizeColAfterRemoveDisc(int i_ColToRemove)
    {
        int newRowLocationForDisc;
        byte numberOfPlayer;

        for (int i = m_BoardRows - 1; i >= 0; i--)
        {
            numberOfPlayer = m_Board[i][i_ColToRemove];
            newRowLocationForDisc = i;
            while (newRowLocationForDisc + 1 < m_BoardRows &&
                    m_Board[newRowLocationForDisc + 1][i_ColToRemove] == EMPTY
                    && m_Board[i][i_ColToRemove] != EMPTY)
            {
                newRowLocationForDisc++;
            }

            m_Board[i][i_ColToRemove] = EMPTY;
            m_Board[newRowLocationForDisc][i_ColToRemove] = numberOfPlayer;
        }
    }


    public void incrementPlayerTurnsCounter() {
        Turn playedTurn = m_TurnsHistory.get(m_TurnsHistory.size() - 1);
        playedTurn.getPlayer().incrementTurnsCounter();
    }

    private boolean canCurrentPlayerPopoutDisc() {
        boolean canPopout = false;

        if (m_Variant.equals(Variant.POPOUT))
        {
            for (int i = 0; i < m_BoardCols; i++) {
                if (m_Board[m_BoardRows - 1][i] == m_CurrentTurn) {
                    canPopout = true;
                }
            }
        }

        return canPopout;
    }

    public void setGameRunning() {
        m_Status = GameStatus.RUNNING;
        m_CurrentTurn = 0;
    }

    public void retireWaitingGame(Player i_RetirePlayer) {
        m_Players.remove(i_RetirePlayer);
    }

    public void addWatcher(String i_WatcherName) {
        m_Watchers.add(i_WatcherName);
    }

    public void removeWatcher(String i_WatcherName) {
        m_Watchers.remove(i_WatcherName);
    }

}
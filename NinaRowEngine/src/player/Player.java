package player;

import java.io.Serializable;

public class Player implements Serializable {
    private final String  m_Name;
    private final boolean m_IsHuman;
    private boolean m_IsRetire = false;
    private int m_TurnsCounter = 0;
    private boolean m_IsWinner = false;

    public Player(String i_PlayerName, boolean i_IsPlayerHuman) {
        this.m_Name = i_PlayerName;
        this.m_IsHuman = i_IsPlayerHuman;
    }


    public void setIsRetire(boolean i_Value) {m_IsRetire = i_Value;}

    public String getName() {return m_Name;}

    public boolean getIsHuman() {return m_IsHuman;}

    public boolean getIsRetire() {return m_IsRetire;}

    public int getTurnsCounter() {return m_TurnsCounter;}

    public boolean getIsWinner() {return m_IsWinner;}

    public void setIsWinner(boolean i_Value) {m_IsWinner = i_Value;}

    public void incrementTurnsCounter() {
        m_TurnsCounter++;
    }

    public void resetTurnsCounter() {
        m_TurnsCounter = 0;
    }

}

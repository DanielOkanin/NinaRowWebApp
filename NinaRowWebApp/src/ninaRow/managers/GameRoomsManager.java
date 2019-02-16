package ninaRow.managers;

import engine.NinaRowEngine;
import ninaRow.chat.ChatManager;

import java.util.ArrayList;
import java.util.List;

public class GameRoomsManager {
    private final List<NinaRowEngine> m_EnginesList;
    private final List<String> m_UploadGamesUsersList;
    private final List<ChatManager> m_ChatsManagersList;

    public GameRoomsManager() {
        m_EnginesList = new ArrayList<>();
        m_UploadGamesUsersList = new ArrayList<>();
        m_ChatsManagersList = new ArrayList<>();
    }


    public synchronized void addGameEngine(NinaRowEngine i_Engine, String i_UserName) {

        m_EnginesList.add(i_Engine);
        m_UploadGamesUsersList.add(i_UserName);
        m_ChatsManagersList.add(new ChatManager());
    }

    public synchronized NinaRowEngine getEngine(int i_EngineIndex){
        return m_EnginesList.get(i_EngineIndex);
    }

    public synchronized NinaRowEngine getEngine(String i_GameTitle) {
        NinaRowEngine game = null;
        for (NinaRowEngine ninaRowEngine : m_EnginesList) {
            if (ninaRowEngine.getGameTitle().equals(i_GameTitle)) {
                game = ninaRowEngine;
                break;
            }
        }

        return game;
    }

    public synchronized boolean isGameExist(String i_GameTitle) {
        return m_EnginesList.stream().filter(i -> i.getGameTitle().equals(i_GameTitle)).count() > 0;
    }

    public synchronized List<NinaRowEngine> getEngines() {return m_EnginesList;}
    public synchronized List<String> getUsersName() {return m_UploadGamesUsersList;}

    private synchronized int getGameIndex(String i_GameTitle) {
        int index = -1;

        for (int i = 0; i < m_EnginesList.size(); i++) {
            if (m_EnginesList.get(i).getGameTitle().equals(i_GameTitle)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public synchronized ChatManager getGameChat(String i_GameTitle) {
        int index = getGameIndex(i_GameTitle);
        return m_ChatsManagersList.get(index);
    }
}

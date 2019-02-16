package ninaRow.managers;

import player.Player;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserManager {
    private final Set<Player> m_UsersSet;

    public UserManager() {
        m_UsersSet = new LinkedHashSet<>();
    }

    public synchronized void addUser(Player i_Player) {
        m_UsersSet.add(i_Player);
    }

    public synchronized void removeUser(String i_Username) {
        Player playerToRemove = getUserByName(i_Username);
        m_UsersSet.remove(playerToRemove);
    }

    public Player getUserByName(String i_username) {
        Player desirePlayer = null;

        for (Player player : m_UsersSet) {
            if (player.getName().equals(i_username)) {
                desirePlayer = player;
                break;
            }
        }

        return desirePlayer;
    }

    public synchronized Set<Player> getUsers() {
        return Collections.unmodifiableSet(m_UsersSet);
    }

    public boolean isUserExists(String i_Username) {
        return m_UsersSet.stream().filter(i -> i.getName().equals(i_Username)).count() > 0;
    }
}

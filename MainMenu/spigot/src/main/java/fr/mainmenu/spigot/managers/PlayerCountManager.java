package fr.mainmenu.spigot.managers;

import java.util.HashMap;
import java.util.Map;

public class PlayerCountManager {

    private final Map<String, Integer> playerCounts = new HashMap<>();

    public void updateCount(String serverName, int count) {
        playerCounts.put(serverName, count);
    }

    public int getCount(String serverName) {
        return playerCounts.getOrDefault(serverName, 0);
    }

    public Map<String, Integer> getAllCounts() {
        return new HashMap<>(playerCounts);
    }

    public void clear() {
        playerCounts.clear();
    }
}

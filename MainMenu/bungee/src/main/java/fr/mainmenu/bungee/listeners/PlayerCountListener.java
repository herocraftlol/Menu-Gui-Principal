package fr.mainmenu.bungee.listeners;

import fr.mainmenu.bungee.MainMenuBungee;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerCountListener implements Listener {

    private final MainMenuBungee plugin;

    public PlayerCountListener(MainMenuBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        broadcastPlayerCounts();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        broadcastPlayerCounts();
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        broadcastPlayerCounts();
    }

    /**
     * Envoie le nombre de joueurs par serveur à tous les serveurs Spigot connectés.
     */
    private void broadcastPlayerCounts() {
        plugin.getProxy().getServers().forEach((name, serverInfo) -> {
            if (!serverInfo.getPlayers().isEmpty()) {
                try {
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    java.io.DataOutputStream out = new java.io.DataOutputStream(baos);
                    out.writeUTF("PlayerCount");

                    // Envoi des counts de tous les serveurs
                    int serverCount = plugin.getProxy().getServers().size();
                    out.writeInt(serverCount);
                    for (java.util.Map.Entry<String, net.md_5.bungee.api.config.ServerInfo> entry :
                            plugin.getProxy().getServers().entrySet()) {
                        out.writeUTF(entry.getKey());
                        out.writeInt(entry.getValue().getPlayers().size());
                    }

                    serverInfo.sendData("mainmenu:data", baos.toByteArray());
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

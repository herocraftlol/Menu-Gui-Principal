package fr.mainmenu.bungee.messaging;

import fr.mainmenu.bungee.MainMenuBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginMessageHandler implements Listener {

    private final MainMenuBungee plugin;

    public PluginMessageHandler(MainMenuBungee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("mainmenu:data")) return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String subChannel = in.readUTF();

            // Demande de téléportation vers un serveur
            if (subChannel.equals("ConnectTo")) {
                String playerName = in.readUTF();
                String serverName = in.readUTF();

                ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);
                ServerInfo targetServer = plugin.getProxy().getServerInfo(serverName);

                if (player != null && targetServer != null) {
                    player.connect(targetServer);
                    plugin.getLogger().info("Téléportation de " + playerName + " vers " + serverName);
                } else {
                    plugin.getLogger().warning("Serveur introuvable: " + serverName + " ou joueur: " + playerName);
                }
            }

            // Demande du nombre de joueurs
            if (subChannel.equals("RequestPlayerCount")) {
                // La réponse est envoyée automatiquement par PlayerCountListener
                // On force un broadcast immédiat
                broadcastPlayerCounts();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastPlayerCounts() {
        plugin.getProxy().getServers().forEach((name, serverInfo) -> {
            if (!serverInfo.getPlayers().isEmpty()) {
                try {
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    java.io.DataOutputStream out = new java.io.DataOutputStream(baos);
                    out.writeUTF("PlayerCount");

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

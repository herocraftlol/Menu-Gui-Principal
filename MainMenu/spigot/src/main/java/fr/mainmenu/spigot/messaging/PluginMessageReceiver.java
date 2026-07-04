package fr.mainmenu.spigot.messaging;

import fr.mainmenu.spigot.MainMenuSpigot;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginMessageReceiver implements PluginMessageListener {

    private final MainMenuSpigot plugin;

    public PluginMessageReceiver(MainMenuSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("mainmenu:data")) return;

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF();

            if (subChannel.equals("PlayerCount")) {
                int serverCount = in.readInt();
                for (int i = 0; i < serverCount; i++) {
                    String serverName = in.readUTF();
                    int count = in.readInt();
                    plugin.getPlayerCountManager().updateCount(serverName, count);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

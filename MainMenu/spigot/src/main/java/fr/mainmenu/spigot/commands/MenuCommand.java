package fr.mainmenu.spigot.commands;

import fr.mainmenu.spigot.MainMenuSpigot;
import fr.mainmenu.spigot.gui.MenuGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private final MainMenuSpigot plugin;
    private final MenuGui menuGui;

    public MenuCommand(MainMenuSpigot plugin) {
        this.plugin = plugin;
        this.menuGui = new MenuGui(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Sous-commande reload (console ou joueur avec permission)
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mainmenu.reload")) {
                sender.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
                return true;
            }
            plugin.getConfigManager().reload();
            sender.sendMessage("§aConfiguration rechargée avec succès !");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cCette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        if (!player.hasPermission("mainmenu.use")) {
            player.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        menuGui.open(player);
        return true;
    }
}

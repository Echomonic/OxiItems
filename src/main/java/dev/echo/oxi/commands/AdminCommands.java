package dev.echo.oxi.commands;

import dev.echo.oxi.guis.ItemGUI;
import dev.echo.utils.general.Color;
import dev.echo.utils.spigot.api.CommandContext;
import dev.echo.utils.spigot.api.annotations.Command;
import org.bukkit.entity.Player;

public class AdminCommands {

    @Command(aliases = {"oxiplugin"},
             desc = "The main command for thie plugin",
             fallbackPrefix = "oxiplugin",max = 2)
    private void oxiCommand(CommandContext context){
        Player player = context.getPlayer();
        if(!context.isPermissible("oxi.admin")){
            player.sendMessage(Color.c("&cYou don't have permission to perform this command!"));
            return;
        }
        if(context.getString(1).equalsIgnoreCase("items")){
            new ItemGUI().open(player);
        }

    }

}

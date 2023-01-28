package dev.echo.oxi;

import dev.echo.oxi.files.InfoFile;
import dev.echo.utils.general.PluginInstance;
import dev.echo.utils.spigot.api.CommandMethodHandler;
import dev.echo.utils.spigot.listeners.BasicListeners;
import dev.echo.utils.spigot.listeners.EventUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class OxiPlugin extends JavaPlugin {

    @Getter
    private InfoFile infoFile;


    @Override
    public void onEnable() {
        this.infoFile = new InfoFile(this);
        PluginInstance.setSpigotClass(this);
        EventUtil.registerEvents(this);
        CommandMethodHandler.getCommands("oxi","dev.echo.oxi.commands");
    }

    @Override
    public void onDisable() {

    }
}

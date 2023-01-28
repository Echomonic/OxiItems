package dev.echo.oxi.files;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class InfoFile {

    private final JavaPlugin plugin;
    private File file;
    private YamlConfiguration configuration;

    @SneakyThrows
    public InfoFile(JavaPlugin plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        if (file == null)
            file = new File(plugin.getDataFolder(), "info.yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration config(){
        return configuration;
    }
    public <T> T getT(String string) {
        return (T) configuration.get(string);
    }
    public <T> T getTOrDefault(String string,T def) {
        if(!contains(string))
            return def;
        return (T) configuration.get(string);
    }
    public List<?> list(String string) {

        return configuration.getList(string);
    }
    public boolean contains(String string){
        System.out.println("Contains: " + configuration.contains(string) + " String: " + string);
        return configuration.contains(string);
    }
    @SneakyThrows
    public void save() {
        configuration.save(file);
    }

}

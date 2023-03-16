package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;

public class FileManager {

    @Inject
    private OpWatchPlugin plugin;
    private final HashMap<String, FileConfiguration> configurationMap = new HashMap<>();
    private static final String LANG_FORMAT = "lang-%s.yml";


    public void loadAllFileConfigurations(){
        configurationMap.put("config", loadFileConfiguration("config.yml"));
        configurationMap.put("opList", loadFileConfiguration("op-list.yml"));

        loadFileConfiguration("lang-en.yml");
        loadFileConfiguration("lang-es.yml");

        String lang = String.format(LANG_FORMAT, get("config").getString("language"));
        FileConfiguration langFileConfiguration = loadFileConfiguration(lang);

        if (langFileConfiguration == null) {
            Bukkit.getLogger().info(Utils.format(get("config"),"&c[OpWatch] Language file not found. Using 'lang-en.yml'"));
            langFileConfiguration = loadFileConfiguration(String.format(LANG_FORMAT, "en"));

            if (langFileConfiguration == null) {
                Bukkit.getLogger().severe(Utils.format(get("config"),"&c[OpWatch] lang-en.yml file not found. Disabling..."));
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        configurationMap.put("language", langFileConfiguration);
    }

    public FileConfiguration get(String name){
        return configurationMap.get(name);
    }

    public FileConfiguration loadFileConfiguration(String name){
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()){
            try{
                plugin.saveResource(name, true);
            } catch (IllegalArgumentException e){
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}

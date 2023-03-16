package team.plugincrafters.opwatch.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.map.MapView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class Utils {

    public static String format(FileConfiguration configFile, String message) {
        if (message.isEmpty()) return "";
        message = message.replace("%prefix%", configFile.getString("prefix"));
        String startTag = configFile.getString("hex-formatting.start-tag");
        String endTag = configFile.getString("hex-formatting.end-tag");

        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static int getVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String stringVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
        return Integer.parseInt(stringVersion.split("_")[1].split("_")[0]);
    }

    public static short getMapID(MapView view) {
        try {
            return view.getId();
        } catch (NoSuchMethodError e) {
            try {
                Class<?> MapView = Class.forName("org.bukkit.map.MapView");
                Object mapID = MapView.getMethod("getId").invoke(view);
                return (short) mapID;
            } catch (Exception e1) {
                return 1;
            }
        }
    }
}
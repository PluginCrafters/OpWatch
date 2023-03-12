package team.plugincrafters.opwatch.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.COLOR_CHAR;

public class Utils {

    public static String format(FileConfiguration configFile, String message) {
        if (message.isEmpty()) return "";
        message = configFile.getString("prefix") + message;
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
}
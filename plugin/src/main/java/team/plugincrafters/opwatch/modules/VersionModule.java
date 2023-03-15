package team.plugincrafters.opwatch.modules;

import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import org.bukkit.Bukkit;
import team.plugincrafters.opwatch.common.QRMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class VersionModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(QRMap.class).toProvider(() -> {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String stringVersion = packageName.substring(packageName.lastIndexOf('.') + 1);
            int version = Integer.parseInt(stringVersion.split("_")[1].split("_")[0]);

            Class<?> clazz = null;

            try {
                clazz = version >= 13 ? Class.forName("team.plugincrafters.opwatch.v1_13_R1.QRMapImpl")
                        : Class.forName("team.plugincrafters.opwatch.v1_8_R3.QRMapImpl");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (QRMap.class.isAssignableFrom(Objects.requireNonNull(clazz))){
                try {
                    return (QRMap) clazz.getConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }
}

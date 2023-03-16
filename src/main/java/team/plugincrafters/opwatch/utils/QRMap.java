package team.plugincrafters.opwatch.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QRMap {

    @Inject
    private JavaPlugin plugin;

    public void giveQRCodeItem(String secret, Player player, String qrName, String serverName) {
        new BukkitRunnable() {
            final MapView mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));

            @Override
            public void run() {
                mapView.getRenderers().clear();

                try {
                    mapView.addRenderer(new QRMapRenderer(secret, serverName, player.getName()));
                    ItemStack mapItem = getMap(mapView, qrName);
                    System.out.println(Utils.getVersion());

                    if (Utils.getVersion() >= 13) {
                        player.getInventory().setItemInMainHand(mapItem);
                    } else {
                        player.setItemInHand(mapItem);
                    }

                    player.getInventory().setHeldItemSlot(4);
                } catch (IOException | NumberFormatException exception) {
                    exception.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this.plugin);
    }

    private ItemStack getMap(MapView mapView, String qrName){
        ItemStack mapItem = new ItemStack(Material.MAP, 1, Utils.getMapID(mapView));
        if (Utils.getVersion() >= 13) {
            mapItem = new ItemStack(Material.FILLED_MAP);

            if (mapItem.getItemMeta() instanceof MapMeta) {
                MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

                if (mapMeta != null) {
                    mapMeta.setMapId(mapView.getId());
                    mapItem.setItemMeta(mapMeta);
                }
            }
        }

        ItemMeta itemMeta = mapItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(qrName);
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(qrName);
        mapItem.setItemMeta(itemMeta);

        return mapItem;
    }
}

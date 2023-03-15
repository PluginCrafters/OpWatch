package team.plugincrafters.opwatch.common;

import com.google.zxing.WriterException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;
import team.plugincrafters.opwatch.common.utils.QRMapRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class QRMap {

    protected abstract Material getMaterial();

    public void createQRMap(String secret, Player player, String qrName, String serverName){
        ItemStack map = new ItemStack(getMaterial());
        ItemMeta itemMeta = map.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add(qrName);
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(qrName);
        map.setItemMeta(itemMeta);

        MapView mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
        mapView.getRenderers().clear();

        try {
            mapView.getRenderers().clear();
            mapView.addRenderer(new QRMapRenderer(secret, serverName, player.getName()));
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }

        map.setDurability(mapView.getId());
        player.setItemInHand(map);
        player.sendMap(mapView);
    }
}

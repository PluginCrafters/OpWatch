package team.plugincrafters.opwatch.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;
import java.util.HashMap;

public class BlockEvents implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private TwoAuthFactorManager twoAuthFactorManager;

    private String validationMessage;
    private boolean enabled;
    private HashMap<String, Long> timeMap = new HashMap<>();

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        enabled = fileManager.get("config").getBoolean("auth.enabled");
        reload();
    }

    public void reload(){
        this.validationMessage = Utils.format(fileManager.get("config"), fileManager.get("language").getString("authentication-needed"));
        enabled = fileManager.get("config").getBoolean("auth.enabled");
    }

    private void sendMessage(Player player){
        long currentTime = System.currentTimeMillis();
        if (timeMap.containsKey(player.getName())){
            long lastTime = timeMap.get(player.getName());
            if ((currentTime - lastTime)/1000.0 < 5) return;
        }
        player.sendRawMessage(validationMessage);
        timeMap.put(player.getName(), System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        if (event.getTo() != null && (
                event.getTo().getX() != event.getFrom().getX() ||
                event.getTo().getY() != event.getFrom().getY() ||
                event.getTo().getZ() != event.getFrom().getZ())) {

            event.getPlayer().teleport(event.getFrom());
            sendMessage(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!enabled) return;

        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!enabled) return;

        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!enabled) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.closeInventory();
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSlotChange(PlayerItemHeldEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        player.getInventory().setHeldItemSlot(4);
        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        sendMessage(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (!enabled) return;

        if (!twoAuthFactorManager.isQR(event.getItem())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (!enabled) return;

        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        Player player = event.getPlayer();

        if (!twoAuthFactorManager.isQR(player.getItemInHand())) return;

        event.setCancelled(true);
        sendMessage(player);
    }

}
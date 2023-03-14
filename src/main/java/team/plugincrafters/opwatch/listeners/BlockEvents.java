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

public class BlockEvents implements Listener {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private TwoAuthFactorManager twoAuthFactorManager;

    private String validationMessage;

    public void start(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
        reload();
    }

    public void reload(){
        this.validationMessage = Utils.format(fileManager.get("config.yml"), fileManager.get("language").getString("authentication-needed"));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        if (event.getTo() != null && (
                event.getTo().getBlockX() != event.getFrom().getBlockX() ||
                event.getTo().getBlockY() != event.getFrom().getBlockY() ||
                event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {

            event.getPlayer().teleport(event.getFrom());
            player.sendMessage(validationMessage);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.closeInventory();
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSlotChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        player.getInventory().setHeldItemSlot(4);
        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (twoAuthFactorManager.playerIsAuthenticated(player)) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMove(InventoryMoveItemEvent event) {
        if (!twoAuthFactorManager.isQR(event.getItem())) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        Player player = event.getPlayer();

        if (!twoAuthFactorManager.isQR(player.getItemInHand())) return;

        event.setCancelled(true);
        player.sendMessage(validationMessage);
    }

}
package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.common.QRMap;
import team.plugincrafters.opwatch.utils.Utils;
import team.plugincrafters.opwatch.conversations.AuthPrompt;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.users.UserState;

import javax.inject.Inject;


public class TwoAuthFactorManager {

    @Inject
    private UserManager userManager;
    @Inject
    private OpWatchPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private QRMap qrMapImpl;

    public void joinPlayer(Player player){
        User user = userManager.getUserByUUID(player.getUniqueId());
        if (user != null){
            this.checkConnection(user, player);
        } else{
            userManager.getUserByUUIDAsync(player.getUniqueId(), u -> this.checkConnection(u, player));
        }
    }

    private void checkConnection(User user, Player player){
        if (user == null) user = this.createUser(player);

        String playerIp = player.getAddress().getAddress().getHostAddress();
        if (user.getIp().equals(playerIp) || playerIsAuthenticated(player)){
            user.setUserState(UserState.LOGGED_IN);
            return;
        }

        user.setUserState(UserState.WAITING_CONFIRMATION);

        ConversationFactory cf = new ConversationFactory(plugin);
        Conversation conversation = cf
                .withFirstPrompt(new AuthPrompt(fileManager, this))
                .withLocalEcho(false)
                .buildConversation(player);
        conversation.begin();
    }

    public boolean certificateCode(Player player, String code){
        boolean isCodeValid = false;

        try {
            int verificationCode = Integer.parseInt(code);
            String secret = userManager.getUserByUUID(player.getUniqueId()).getSecret();
            isCodeValid = plugin.getgAuth().authorize(secret, verificationCode);
            if (isCodeValid) saveNewUser(player);

        } catch (NumberFormatException ignored) {

        }

        return isCodeValid;
    }

    private void saveNewUser(Player player){
        User user = userManager.getUserByUUID(player.getUniqueId());
        user.setUserState(UserState.LOGGED_IN);
        user.changeIp(player.getAddress().getAddress().getHostAddress());

        userManager.saveUser(user);
        FileConfiguration langFile = fileManager.get("language");
        player.sendRawMessage(Utils.format(fileManager.get("config"),langFile.getString("success")));
        String message = Utils.format(fileManager.get("config"), langFile.getString("success-log"));
        Bukkit.getConsoleSender().sendMessage(message.replace("%player%", player.getName()));

        player.getInventory().setHeldItemSlot(4);
        player.setItemInHand(user.getItem());
        user.setItem(null);
    }

    private User createUser(Player player){
        String secret = plugin.getgAuth().createCredentials().getKey();

        player.getInventory().setHeldItemSlot(4);
        ItemStack item = player.getItemInHand();
        qrMapImpl.createQRMap(secret, player, Utils.format(fileManager.get("config"), "&7QR"),
                fileManager.get("config").getString("auth.server-name"));

        player.sendMessage(Utils.format(fileManager.get("config"), fileManager.get("language").getString("auth-code")
                .replace("%secret%", secret)));
        User user = new User(player.getUniqueId(), player.getName(), "", UserState.WAITING_CONFIRMATION, secret);
        user.setItem(item);
        userManager.loadUser(user);
        userManager.saveUser(user);

        return user;
    }


    public boolean playerIsAuthenticated(Player player){
        User user = userManager.getUserByUUID(player.getUniqueId());
        if (user == null) return true;

        return user.getUserState().equals(UserState.LOGGED_IN);
    }

    public boolean isQR(ItemStack item) {
        return item != null && item.getType() == Material.MAP && item.hasItemMeta() && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().get(0).equalsIgnoreCase(Utils.format(fileManager.get("config"), "&7QR"));
    }
}
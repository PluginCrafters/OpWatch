package team.plugincrafters.opwatch.managers;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.conversations.AuthPrompt;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.users.UserState;
import team.plugincrafters.opwatch.utils.Utils;

import javax.inject.Inject;

public class TwoAuthFactorManager {

    @Inject
    private UserManager userManager;
    @Inject
    private OpWatchPlugin plugin;
    @Inject
    private FileManager fileManager;

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
        userManager.loadUser(user);

        String playerIp = player.getAddress().getAddress().getHostAddress();
        if (user.getIp().equals(playerIp) || playerIsAuthenticated(player)) return;

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
        player.sendRawMessage(Utils.format(fileManager.get("config"), fileManager.get("language").getString("success")));
    }

    private User createUser(Player player){
        //TODO Enviar mapa con QR para que se una a GoogleAuth
        String secret = plugin.getgAuth().createCredentials().getKey();
        player.sendMessage(secret);
        User user = new User(player.getUniqueId(), player.getName(), "", UserState.WAITING_CONFIRMATION, secret);
        userManager.saveUser(user);

        return user;
    }

    public boolean playerIsAuthenticated(Player player){
        User user = userManager.getUserByUUID(player.getUniqueId());
        if (user == null) return true;

        return user.getUserState().equals(UserState.LOGGED_IN);
    }

    public boolean isQR(ItemStack item) {
        return item != null && item.getType() == Material.MAP && item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().get(0).equalsIgnoreCase("&7QR");
    }
}
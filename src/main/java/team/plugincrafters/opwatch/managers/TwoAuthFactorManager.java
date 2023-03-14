package team.plugincrafters.opwatch.managers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.users.UserState;

import javax.inject.Inject;

public class TwoAuthFactorManager {

    @Inject
    private UserManager userManager;

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
        if (user.getIp().equals(playerIp)) return;

        user.setUserState(UserState.WAITING_CONFIRMATION);
        // Enviar mensaje de verificación por googleAuth. No permitirle jugar hasta que lo ingrese.


    }

    private User createUser(Player player){
        String playerIp = player.getAddress().getAddress().getHostAddress();
        // Enviar mensaje para que se una a GoogleAuth y no permitirle jugar hasta que lo haga
        User user = new User(player.getUniqueId(), player.getName(), playerIp, UserState.LOGGED_IN);
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

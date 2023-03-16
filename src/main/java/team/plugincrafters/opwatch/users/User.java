package team.plugincrafters.opwatch.users;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private String ip;
    private String secret;
    private UserState userState;
    private ItemStack item;

    public User(UUID uuid, String name, String ip, UserState userState, String secret){
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.userState = userState;
        this.secret = secret;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public String getSecret() {
        return secret;
    }

    public void changeIp(String newIp){
        this.ip = newIp;
    }

    public UUID getUuid(){
        return uuid;
    }

    public String getName(){
        return name;
    }

    public String getIp(){
        return ip;
    }

    public UserState getUserState() {
        if (userState == null) userState = UserState.WAITING_CONFIRMATION;
        return userState;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }
}

package team.plugincrafters.opwatch.users;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private String ip;
    private final String secret;
    private UserState userState;

    public User(UUID uuid, String name, String ip, UserState userState, String secret){
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.userState = userState;
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void changeIp(String newIp){
        this.ip = ip;
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

    public void setUserState(UserState userState) {
        this.userState = userState;
    }
}

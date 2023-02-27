package team.plugincrafters.opwatch;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private final String name;
    private String ip;

    public User(UUID uuid, String name, String ip){
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
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
}

package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.storage.Callback;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;
import team.plugincrafters.opwatch.users.User;
import team.plugincrafters.opwatch.users.UserState;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class UserManager {

    @Inject
    private ObjectRepository<User, String> userRepository;
    @Inject
    private JavaPlugin plugin;

    private final Set<User> users = new HashSet<>();

    public void saveAll(){
        Map<Boolean, List<User>> partitionedUsers = users.stream()
                .collect(Collectors.partitioningBy(user -> user.getUserState().equals(UserState.WAITING_REGISTRATION)));

        Set<User> unregistered = new HashSet<>(partitionedUsers.get(true));
        unregistered.forEach(user -> {
            Player player = Bukkit.getPlayerExact(user.getName());
            if (player == null) return;

            player.getInventory().setHeldItemSlot(4);
            player.setItemInHand(user.getItem());
            user.setItem(null);
        });

        userRepository.saveAll(new HashSet<>(partitionedUsers.get(false)));
    }

    public void saveUser(User user){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> userRepository.saveAsync(user));
    }

    public User getUserByName(String name){
        return users.stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void loadUser(User user){
        this.users.add(user);
    }

    public Set<User> getUsers(){
        return users;
    }

    public User getUserByUUID(UUID uuid){
        return users.stream().filter(user -> user.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public void getUserByUUIDAsync(UUID uuid, Callback<User> callback){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = userRepository.load(String.valueOf(uuid));
            callback.call(user);
        });
    }

    public void removeUser(User user){
        unloadUser(user);
        userRepository.delete(user.getUuid().toString());
    }

    public void unloadUser(User user){
        this.users.remove(user);
    }

    public void start(){
        userRepository.start();
        this.users.addAll(userRepository.loadAll());
    }
}

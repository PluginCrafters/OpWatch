package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.storage.Callback;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;
import team.plugincrafters.opwatch.users.User;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    @Inject
    private ObjectRepository<User, String> userRepository;
    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    private final Set<User> users = new HashSet<>();

    public void saveUser(User user){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> userRepository.save(user));
    }

    public void loadUser(User user){
        this.users.add(user);
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

    public void start(){
        this.users.addAll(userRepository.loadAll());
    }
}

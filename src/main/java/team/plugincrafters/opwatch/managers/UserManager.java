package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.plugincrafters.opwatch.User;
import team.plugincrafters.opwatch.storage.Callback;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class UserManager {

    @Inject
    private ObjectRepository<User, String> userRepository;
    @Inject
    private JavaPlugin plugin;

    public void saveUser(User user){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> userRepository.save(user));
    }

    public void getUserList(Callback<List<User>> callback){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<User> usersList = userRepository.loadAll();
            callback.call(usersList);
        });
    }

    public void getUserByUUID(UUID uuid, Callback<User> callback){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = userRepository.load(String.valueOf(uuid));
            callback.call(user);
        });
    }

    public void start(){
        userRepository.start();
    }
}

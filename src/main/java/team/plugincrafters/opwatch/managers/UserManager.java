package team.plugincrafters.opwatch.managers;

import org.bukkit.Bukkit;
import team.plugincrafters.opwatch.OpWatchPlugin;
import team.plugincrafters.opwatch.User;
import team.plugincrafters.opwatch.storage.Callback;
import team.plugincrafters.opwatch.storage.repositories.ObjectRepository;

import javax.inject.Inject;
import java.util.List;

public class UserManager {

    @Inject
    private ObjectRepository<User, String> userRepository;
    @Inject
    private OpWatchPlugin plugin;
    @Inject
    private FileManager fileManager;

    public void saveUser(User user){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> userRepository.save(user));
    }

    public void getUserList(Callback<List<User>> callback){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<User> usersList = userRepository.loadAll();
            callback.call(usersList);
        });
    }

    public void getUserByUUID(String uuid, Callback<User> callback){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            User user = userRepository.load(uuid);
            callback.call(user);
        });
    }

    public void start(){
        userRepository.start();

        int interval = fileManager.get("config").getInt("op-check-interval") * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkOpPlayers, 0, interval);
    }

    private void checkOpPlayers(){
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.isOp()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (!fileManager.get("opList").getStringList("op-list").contains(player.getName())){
                        // bla bla
                    }
                });
            }
        });
    }
}

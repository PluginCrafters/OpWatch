package team.plugincrafters.opwatch.conversations;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import team.plugincrafters.opwatch.managers.FileManager;
import team.plugincrafters.opwatch.managers.TwoAuthFactorManager;
import team.plugincrafters.opwatch.utils.Utils;

import java.util.Objects;

public class AuthPrompt extends StringPrompt {

    private final FileManager fileManager;
    private final TwoAuthFactorManager twoAuthFactorManager;
    private boolean firstTime;

    public AuthPrompt(FileManager fileManager, TwoAuthFactorManager twoAuthFactorManager){
        this.fileManager = fileManager;
        this.twoAuthFactorManager = twoAuthFactorManager;
        firstTime = true;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        String key = firstTime ? "authentication-needed" : "incorrect-code";
        return Utils.format(fileManager.get("config"), fileManager.get("language").getString(key));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s){
        Objects.requireNonNull(s);

        Player player = (Player) context.getForWhom();
        if (!twoAuthFactorManager.certificateCode(player, s.replace(" ", ""))){
            firstTime = false;
            return this;
        }
        return END_OF_CONVERSATION;
    }
}
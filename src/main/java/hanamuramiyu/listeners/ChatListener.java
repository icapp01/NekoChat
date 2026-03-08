package hanamuramiyu.listeners;

import hanamuramiyu.chat.ChatManager;
import hanamuramiyu.core.NekoChat;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final NekoChat plugin;
    private final ChatManager chatManager;

    public ChatListener(NekoChat plugin, ChatManager chatManager) {
        this.plugin = plugin;
        this.chatManager = chatManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        plugin.getSchedulerManager().runTaskOnEntity(player, () -> chatManager.handleChat(player, message));
    }
}
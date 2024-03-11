package tr.com.wraith.connectionmonitor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tr.com.wraith.connectionmonitor.MongoHandler;

import org.bson.Document;

import java.util.UUID;


public class PlayerJoinListener implements Listener {
    private final MongoHandler mongoHandler;
    public PlayerJoinListener(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        long loginTime = System.currentTimeMillis();

        Document userDoc = mongoHandler.findUserDocument(uuid);
        if (userDoc == null) {
            mongoHandler.createUserDocument(uuid);
        }

        mongoHandler.updateLoginConnections(uuid, loginTime);
    }
}

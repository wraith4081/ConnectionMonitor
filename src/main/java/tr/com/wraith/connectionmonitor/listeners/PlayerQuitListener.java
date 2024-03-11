package tr.com.wraith.connectionmonitor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import tr.com.wraith.connectionmonitor.MongoHandler;

import java.util.UUID;

public class PlayerQuitListener implements Listener {
    private final MongoHandler mongoHandler;
    public PlayerQuitListener(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long logoutTime = System.currentTimeMillis();

        mongoHandler.updateLogoutConnections(uuid, logoutTime);
    }
}
package tr.com.wraith.connectionmonitor;

import org.bukkit.plugin.java.JavaPlugin;
import tr.com.wraith.connectionmonitor.listeners.PlayerJoinListener;
import tr.com.wraith.connectionmonitor.listeners.PlayerQuitListener;

public final class ConnectionMonitor extends JavaPlugin {

    @Override
    public void onEnable() {
        String connectionString = "";

        MongoHandler mongoHandler = new MongoHandler(connectionString);
        mongoHandler.setDatabase("Cluster0");

        if (mongoHandler.checkConnection()) {
            System.out.println("Successfully connected into MongoDB!");
        } else {
            throw new Error("Cannot connect into MongoDB!");
        }

        System.out.println("The ConnectionMonitor is enabled!");

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(mongoHandler),this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(mongoHandler),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        System.out.println("The ConnectionMonitor is disabled!");
    }
}

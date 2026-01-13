package net.pxr.greeter;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class GreeterPlugin extends JavaPlugin {

    private Set<UUID> knownPlayers;
    private Path playerDataFile;
    private Config<GreeterConfig> config;

    public GreeterPlugin(JavaPluginInit init) {
        super(init);
        this.knownPlayers = new HashSet<>();
        this.config = withConfig(GreeterConfig.CODEC);
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Greeter is setting up...");

        Path configPath = getDataDirectory().resolve("config.json");
        if (!Files.exists(configPath)) {
            getLogger().at(Level.INFO).log("Generating default config...");
            config.save();
        }

        playerDataFile = getDataDirectory().resolve("known_players.txt");
        loadKnownPlayers();

        getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, this::onAddPlayerToWorld);
        getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);

        getCommandRegistry().registerCommand(new GreeterCommand());

        getLogger().at(Level.INFO).log("Greeter setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("Greeter has started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("Greeter is shutting down...");
        saveKnownPlayers();
    }

    public GreeterConfig getConfig() {
        return config.get();
    }

    public CompletableFuture<Void> reloadConfig() {
        return config.load().thenAccept(cfg -> {
            getLogger().at(Level.INFO).log("Config reloaded!");
        });
    }

    private void onAddPlayerToWorld(AddPlayerToWorldEvent event) {
        if (getConfig().isSuppressNativeWelcome()) {
            event.setBroadcastJoinMessage(false);
        }
    }

    private void onPlayerReady(PlayerReadyEvent event) {
        GreeterConfig cfg = getConfig();
        if (!cfg.isEnableJoinMessages()) return;

        Player player = event.getPlayer();
        String playerName = player.getDisplayName();
        UUID playerId = player.getUuid();

        if (!knownPlayers.contains(playerId) && cfg.isEnableFirstTimeWelcome()) {
            knownPlayers.add(playerId);
            saveKnownPlayers();

            player.sendMessage(Message.raw(cfg.getWelcomeMessage()).color("#FFD700").bold(true));
            player.sendMessage(Message.raw(cfg.getFirstTimeMessage().replace("{player}", playerName)).color("#FFFF00"));

            String broadcast = cfg.getNewPlayerBroadcast().replace("{player}", playerName);
            broadcastMessage(Message.raw(broadcast).color("#55FF55"));

            getLogger().at(Level.INFO).log("New player joined: " + playerName);
        } else {
            player.sendMessage(Message.raw(cfg.getReturnMessage().replace("{player}", playerName)).color("#55FF55"));

            String broadcast = cfg.getJoinBroadcast().replace("{player}", playerName);
            broadcastMessage(Message.raw(broadcast).color("#AAAAAA"));

            getLogger().at(Level.INFO).log("Player rejoined: " + playerName);
        }
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        GreeterConfig cfg = getConfig();
        if (!cfg.isEnableLeaveMessages()) return;

        PlayerRef playerRef = event.getPlayerRef();
        String playerName = playerRef.getUsername();

        String broadcast = cfg.getLeaveBroadcast().replace("{player}", playerName);
        broadcastMessage(Message.raw(broadcast).color("#AAAAAA"));

        getLogger().at(Level.INFO).log("Player left: " + playerName);
    }

    private void broadcastMessage(Message message) {
        Universe universe = Universe.get();
        if (universe != null) {
            for (PlayerRef playerRef : universe.getPlayers()) {
                playerRef.sendMessage(message);
            }
        }
    }

    private void loadKnownPlayers() {
        try {
            Files.createDirectories(getDataDirectory());

            if (Files.exists(playerDataFile)) {
                List<String> lines = Files.readAllLines(playerDataFile);
                for (String line : lines) {
                    if (!line.trim().isEmpty()) {
                        try {
                            knownPlayers.add(UUID.fromString(line.trim()));
                        } catch (IllegalArgumentException e) {
                            getLogger().at(Level.WARNING).log("Invalid UUID in player data: " + line);
                        }
                    }
                }
                getLogger().at(Level.INFO).log("Loaded " + knownPlayers.size() + " known players.");
            }
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).log("Failed to load known players: " + e.getMessage());
        }
    }

    private void saveKnownPlayers() {
        try {
            Files.createDirectories(getDataDirectory());

            List<String> lines = new ArrayList<>();
            for (UUID uuid : knownPlayers) {
                lines.add(uuid.toString());
            }
            Files.write(playerDataFile, lines);

            getLogger().at(Level.INFO).log("Saved " + knownPlayers.size() + " known players.");
        } catch (Exception e) {
            getLogger().at(Level.SEVERE).log("Failed to save known players: " + e.getMessage());
        }
    }

    private class GreeterCommand extends AbstractCommand {

        public GreeterCommand() {
            super("greeter", "Greeter plugin commands");
            addSubCommand(new ReloadCommand());
            addSubCommand(new StatusCommand());
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            context.sendMessage(Message.raw("Greeter Commands:").color("#55FF55"));
            context.sendMessage(Message.raw("  /greeter reload - Reload config").color("#AAAAAA"));
            context.sendMessage(Message.raw("  /greeter status - Show current settings").color("#AAAAAA"));
            return CompletableFuture.completedFuture(null);
        }
    }

    private class ReloadCommand extends AbstractCommand {

        public ReloadCommand() {
            super("reload", "Reload the Greeter config");
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            return reloadConfig().thenRun(() -> {
                context.sendMessage(Message.raw("Greeter config reloaded!").color("#55FF55"));
            });
        }
    }

    private class StatusCommand extends AbstractCommand {

        public StatusCommand() {
            super("status", "Show current Greeter settings");
        }

        @Override
        protected CompletableFuture<Void> execute(CommandContext context) {
            GreeterConfig cfg = getConfig();
            context.sendMessage(Message.raw("=== Greeter Status ===").color("#55FF55"));
            context.sendMessage(Message.raw("Join Messages: ").color("#AAAAAA")
                .insert(Message.raw(cfg.isEnableJoinMessages() ? "Enabled" : "Disabled").color(cfg.isEnableJoinMessages() ? "#55FF55" : "#FF5555")));
            context.sendMessage(Message.raw("Leave Messages: ").color("#AAAAAA")
                .insert(Message.raw(cfg.isEnableLeaveMessages() ? "Enabled" : "Disabled").color(cfg.isEnableLeaveMessages() ? "#55FF55" : "#FF5555")));
            context.sendMessage(Message.raw("First-Time Welcome: ").color("#AAAAAA")
                .insert(Message.raw(cfg.isEnableFirstTimeWelcome() ? "Enabled" : "Disabled").color(cfg.isEnableFirstTimeWelcome() ? "#55FF55" : "#FF5555")));
            context.sendMessage(Message.raw("Suppress Native: ").color("#AAAAAA")
                .insert(Message.raw(cfg.isSuppressNativeWelcome() ? "Enabled" : "Disabled").color(cfg.isSuppressNativeWelcome() ? "#55FF55" : "#FF5555")));
            context.sendMessage(Message.raw("Welcome Message: ").color("#AAAAAA")
                .insert(Message.raw(cfg.getWelcomeMessage()).color("#FFFF00")));
            return CompletableFuture.completedFuture(null);
        }
    }
}

package it.einjojo.regiondiscoveries;


import com.sk89q.worldguard.WorldGuard;
import de.netzkronehd.wgregionevents.events.RegionEnteredEvent;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.mongo.Mongo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;

public class RegionDiscoveryPlugin extends JavaPlugin {
    private DPlayerManager playerManager;


    @Override
    public void onEnable() {
        var provider = getServer().getServicesManager().getRegistration(Mongo.class);
        if (provider == null) {
            getLogger().severe("MongoDB provider not found.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Commands.create()
                .assertPermission("regiondiscoveries.admin")
                .assertUsage("<id> <name>")
                .tabHandler(context -> {
                    if (context.args().size() > 1) {
                        return List.of("");
                    }
                    return WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().stream()
                            .flatMap(region -> region.getRegions().keySet().stream())
                            .toList();
                }).handler(context -> {
                    String id = context.arg(0).parseOrFail(String.class);
                    StringBuilder name = new StringBuilder(context.arg(1).parseOrFail(String.class));
                    int allArgs = context.args().size();
                    for (int i = 2; i < allArgs; i++) {
                        name.append(" ").append(context.rawArg(i));
                    }
                    DiscoverableRegion region = new DiscoverableRegion(id, MiniMessage.miniMessage().deserialize(name.toString()), Component.text(""));
                    saveDiscoverableRegion(region);
                    context.sender().sendRichMessage("<green>Region saved.");
                }).register("regiondiscoveries");
        Commands.create()
                .assertPermission("regiondiscoveries.admin")
                .assertUsage("<player>")
                .tabHandler(context -> {
                    if (context.args().size() <= 1) {
                        return getServer().getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(context.label())).toList();
                    }
                    return List.of("");
                }).handler(context -> {
                    String name = context.arg(0).parseOrFail(String.class);
                    OfflinePlayer player = getServer().getOfflinePlayer(name);
                    playerManager.savePlayer(new DPlayer(player.getUniqueId()));
                    context.sender().sendRichMessage("<green>Player resetted.");
                    reloadConfig();
                }).register("regiondiscoveriesreset");

        playerManager = new DPlayerManager(provider.getProvider());
        Events.subscribe(AsyncPlayerPreLoginEvent.class).handler(this::onJoin);
        Events.subscribe(RegionEnteredEvent.class).handler(this::onRegionEnter);
    }


    public Title createTitle(DiscoverableRegion region) {
        return Title.title(region.displayTitle(), Component.text("NEUEN BEREICH ENTDECKT", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofMillis(200)));
    }

    public @Nullable DiscoverableRegion getDiscoverableRegion(String worldGuardId) {
        var res = getConfig().getRichMessage("regions." + worldGuardId + ".name");
        if (res == null) {
            return null;
        }
        var desc = getConfig().getRichMessage("regions." + worldGuardId + ".lore");
        if (desc == null) {
            desc = Component.text("");
        }
        return new DiscoverableRegion(worldGuardId, res, desc);
    }

    public void saveDiscoverableRegion(DiscoverableRegion region) {
        getConfig().setRichMessage("regions." + region.worldGuardId() + ".name", region.displayTitle());
        getConfig().setRichMessage("regions." + region.worldGuardId() + ".lore", region.description());
        saveConfig();
    }

    public void onRegionEnter(RegionEnteredEvent event) {
        Schedulers.async().run(() -> {
            String wGuard = event.getRegion().getId();
            DiscoverableRegion region = getDiscoverableRegion(wGuard);
            if (region == null) {
                return;
            }
            DPlayer player = playerManager.loadPlayer(event.getPlayer().getUniqueId());
            if (player == null) {
                getLogger().warning("Player not found in database: " + event.getPlayer().getName());
                return;
            }
            if (player.getDiscoveredRegionsIds().contains(wGuard)) {
                return;
            }
            event.getPlayer().showTitle(createTitle(region));
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 0.1f);
            event.getPlayer().sendMessage(region.description());
            player.addDiscoveredRegion(wGuard);
            playerManager.savePlayer(player);
        });
    }

    public void onJoin(AsyncPlayerPreLoginEvent event) {
        DPlayer loaded = playerManager.loadPlayer(event.getUniqueId());
        if (loaded == null) {
            DPlayer player = new DPlayer(event.getUniqueId());
            playerManager.savePlayer(player);
        }
    }

}

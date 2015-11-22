package com.doctordark.hcf.listener;

import com.doctordark.hcf.HCF;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class CoreListener implements Listener {

    private static final String DEFAULT_WORLD_NAME = "world";

    private final HCF plugin;

    public CoreListener(HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld(CoreListener.DEFAULT_WORLD_NAME).getSpawnLocation().add(0.5, 0, 0.5));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            plugin.getEconomyManager().addBalance(player.getUniqueId(), 250);    // give player some starting money
            event.setSpawnLocation(Bukkit.getWorld(CoreListener.DEFAULT_WORLD_NAME).getSpawnLocation().add(0.5, 0, 0.5));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner &&
                !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot break spawners in the nether.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getState() instanceof CreatureSpawner &&
                !player.hasPermission(ProtectionListener.PROTECTION_BYPASS_PERMISSION)) {

            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place spawners in the nether.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        plugin.getVisualiseHandler().clearVisualBlocks(event.getChunk());
    }
}
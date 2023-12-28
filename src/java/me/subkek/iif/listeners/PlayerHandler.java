package me.subkek.iif.listeners;

import me.subkek.iif.InvisibleIF;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerHandler implements Listener {
    private final InvisibleIF plugin = InvisibleIF.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(HangingPlaceEvent event) {
        if (!isFrameEntity(event.getEntity())) return;
        Player player = event.getPlayer();

        if (!isInvisibleFrame(player.getInventory().getItemInMainHand())) return;

        ItemFrame itemFrame = (ItemFrame) event.getEntity();
        itemFrame.setGlowing(true);
        itemFrame.getPersistentDataContainer().set(plugin.invisibleKey, PersistentDataType.STRING, "true");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHangingBreak(HangingBreakEvent event) {
        if (!isFrameEntity(event.getEntity())) return;
        if (!isInvisibleFrame((ItemFrame) event.getEntity())) return;

        event.setCancelled(true);
        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (!itemFrame.getItem().getType().isAir()) itemFrame.getWorld().dropItem(getDropLocation(itemFrame), itemFrame.getItem());
        itemFrame.getWorld().dropItem(getDropLocation(itemFrame), plugin.getIFItem());
        itemFrame.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!isFrameEntity(event.getRightClicked())) return;
        if (!isInvisibleFrame((ItemFrame) event.getRightClicked())) return;
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        makeVisible(itemFrame, false);

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (itemFrame.getItem().getType().isAir()) {
                makeVisible(itemFrame, true);
            }
        }, 20);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isFrameEntity(event.getEntity())) return;
        if (!isInvisibleFrame((ItemFrame) event.getEntity())) return;
        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        makeVisible(itemFrame, true);

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (!itemFrame.getItem().getType().isAir()) {
                makeVisible(itemFrame, false);
            }
        }, 20);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType().equals(Material.SNOW) || event.getBlock().getType().equals(Material.ICE))
            event.setCancelled(true);
    }

    private boolean isFrameEntity(Entity entity)
    {
        return (entity != null && entity.getType().equals(EntityType.ITEM_FRAME));
    }

    private boolean isInvisibleFrame(ItemStack itemStack) {
        PersistentDataContainer data = itemStack.getItemMeta().getPersistentDataContainer();
        return data.has(plugin.invisibleKey, PersistentDataType.STRING);
    }

    private boolean isInvisibleFrame(ItemFrame itemFrame) {
        PersistentDataContainer data = itemFrame.getPersistentDataContainer();
        return data.has(plugin.invisibleKey, PersistentDataType.STRING);
    }

    private Location getDropLocation(Entity entity) {
        return entity.getLocation().toBlockLocation().add(0.5, 0.5, 0.5);
    }

    private void makeVisible(ItemFrame itemFrame, boolean val) {
        itemFrame.setGlowing(val);
        itemFrame.setVisible(val);
    }
}

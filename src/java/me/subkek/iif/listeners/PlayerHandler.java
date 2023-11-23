package me.subkek.iif.listeners;

import me.subkek.iif.InvisibleIF;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
        itemFrame.getWorld().dropItem(itemFrame.getLocation(), itemFrame.getItem());
        itemFrame.remove();

        ItemStack itemStack = new ItemStack(Material.ITEM_FRAME);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();

        data.set(plugin.invisibleKey, PersistentDataType.STRING, "true");

        itemStack.setItemMeta(itemMeta);

        itemFrame.getWorld().dropItem(event.getEntity().getLocation(), itemStack);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!isFrameEntity(event.getRightClicked())) return;
        if (!isInvisibleFrame((ItemFrame) event.getRightClicked())) return;
        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        itemFrame.setGlowing(false);
        itemFrame.setVisible(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isFrameEntity(event.getEntity())) return;
        if (!isInvisibleFrame((ItemFrame) event.getEntity())) return;
        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        itemFrame.setGlowing(true);
        itemFrame.setVisible(true);
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
}

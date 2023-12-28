package me.subkek.iif;

import me.subkek.iif.listeners.PlayerHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class InvisibleIF extends JavaPlugin {
    private static InvisibleIF instance;
    public final NamespacedKey invisibleKey = new NamespacedKey("iif", "iif_invisible");
    public static InvisibleIF getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);

        ShapedRecipe recipe = new ShapedRecipe(invisibleKey, getIFItem());

        recipe.shape("GSG", "SFS", "GSG");

        recipe.setIngredient('G', Material.GLASS_PANE);
        recipe.setIngredient('F', Material.ITEM_FRAME);
        recipe.setIngredient('S', Material.GLOWSTONE_DUST);

        getServer().addRecipe(recipe);
    }

    @Override
    public void onDisable() {
        getServer().removeRecipe(invisibleKey);
    }

    public ItemStack getIFItem() {
        ItemStack itemStack = new ItemStack(Material.ITEM_FRAME);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.displayName(Component.text("§fНевидимая рамка"));

        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        data.set(invisibleKey, PersistentDataType.STRING, "true");

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }
}

package org.enchanted.veryballer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;

import javax.naming.Name;

public class Veryballer extends JavaPlugin implements Listener {
    private final NamespacedKey wildcardKey = new NamespacedKey(this, "wildcard");
    @Override
    public void onEnable() {
        // Register the event listener
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Check if the player who died is a valid player
        Player player = event.getEntity();

        // Create the "Wildcard" book item
        ItemStack wildcardBook = new ItemStack(Material.ENCHANTED_BOOK);

        // Set a specific NBT name to identify it as a "Wildcard" book
        wildcardBook.editMeta(meta -> {
            meta.getPersistentDataContainer().set(
                    wildcardKey,
                    PersistentDataType.STRING,
                    "Wildcard"
            );
        });
        Item itemEntity = player.getWorld().dropItem(player.getLocation(), wildcardBook);
        // Add the "Wildcard" book to the player's inventory
        EntityDropItemEvent dropEvent = new EntityDropItemEvent(player, itemEntity);

        // Call the event
        Bukkit.getServer().getPluginManager().callEvent(dropEvent);

        // Check if the event was not cancelled
        if (!dropEvent.isCancelled()) {
            // Add the dropped item to the world
            player.getWorld().dropItemNaturally(player.getLocation(), wildcardBook);
        }
    }
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemStack offhandItem = player.getInventory().getItemInOffHand();

        // Check if the player right-clicked with a valid "Wildcard" book
        if (event.getAction().name().contains("RIGHT") && offhandItem != null) {
            // Check if the offhand item has the specific NBT name "Wildcard"
            if (offhandItem.hasItemMeta() && offhandItem.getItemMeta().getPersistentDataContainer().has(wildcardKey, PersistentDataType.STRING)) {
                // Apply the Sharpness enchantment to the item in the main hand
                if (itemInHand != null && !itemInHand.getType().equals(Material.AIR) && itemInHand.getEnchantmentLevel(Enchantment.DAMAGE_ALL) <= Enchantment.DAMAGE_ALL.getMaxLevel()) {
                    itemInHand.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, itemInHand.getEnchantmentLevel(Enchantment.DAMAGE_ALL) + 1);
                }
                // Remove the "Wildcard" book from the player's offhand
                player.getInventory().setItemInOffHand(null);
            }
        }
    }
}

package fr.yohem.modostick;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class ModoStick extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("ModoStick oppen");
        getCommand("stick").setExecutor((commandSender, command, s, strings) -> {
                    if (commandSender instanceof Player) {
                        Player p = (Player) commandSender;
                        p.getInventory().addItem(getStick());
                        return true;
                    }
                    return false;
                });
        getServer().getPluginManager().registerEvents(new ModoStickListerner(), this);
        // Plugin startup logic

    }
    static public ItemStack getStick(){
        ItemStack i = new ItemStack(Material.BLAZE_ROD);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName("§6Modo Stick");
        m.setLore(Arrays.asList("       Utilisation : ", "click droit sur un joueur"));
        m.addEnchant(Enchantment.ARROW_INFINITE, 0, false);
        i.setItemMeta(m);
        return new ItemStack(i);
    }

    @Override
    public void onDisable() {
        System.out.println("ModoStick close");
        // Plugin shutdown logic
    }
}

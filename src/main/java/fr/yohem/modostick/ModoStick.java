package fr.yohem.modostick;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class ModoStick extends JavaPlugin {
    
    Map<Player, Long>counterShowPlayer = new HashMap<Player, Long>(); 

    static int timer = 3;

    public void hidePlayerName(Player p){
        ArmorStand armorStand = (ArmorStand) p.getWorld().spawnEntity(p.getLocation().add(0, 200, 0), EntityType.ARMOR_STAND);
        armorStand.setCustomName("mask");
        armorStand.setVisible(false);
        p.addPassenger(armorStand);
    }
    public void showPlayerName(Player p){
        int i =0;
        while (i<p.getPassengers().size()) {
            Entity e = p.getPassengers().get(i);
            if (e.getType() == EntityType.ARMOR_STAND && ((ArmorStand)e).getCustomName().equals("mask")) {
                p.removePassenger(e);
            }else i++;
        }
        counterShowPlayer.put(p, System.currentTimeMillis());
    }

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
        getServer().getPluginManager().registerEvents(new ModoStickListerner(this), this);
        // Plugin startup logic

        for (Player player : getServer().getOnlinePlayers())
            hidePlayerName(player);

        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Entry<Player, Long>> iterator = counterShowPlayer.entrySet().iterator();
                
                while (iterator.hasNext()) {
                    Entry<Player, Long> entry = iterator.next();
                    if (System.currentTimeMillis() - entry.getValue()>= timer*1000) {
                        hidePlayerName(entry.getKey());
                        counterShowPlayer.remove(entry);
                    }
                }
            }
            
        }.runTaskTimer(this, 0, 20L);

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

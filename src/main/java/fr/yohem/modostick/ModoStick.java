package fr.yohem.modostick;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public final class ModoStick extends JavaPlugin {
    
    Map<List<UUID>, Long> counterShowPlayer = new HashMap<List<UUID>, Long>(); 
    Map<UUID, EntityArmorStand> playerPassager = new  HashMap<UUID, EntityArmorStand>();
    Map<UUID, List<UUID>> playerHideFor = new  HashMap<UUID, List<UUID>>();

    
    static int timer = 3;
    
    public void hidePlayerName(Player p){
        addArmorSand(p);
    }
    // public void showPlayerName(Player p){
    //     remArmorSand(p);
    // }

    public void hideFromList(Player player, List<Player> hidesFrom){
        hidesFrom.forEach(pl -> hide(player, pl));
    }
    public void fullHide(Player player){
        hideFromList(player, new ArrayList<>(getServer().getOnlinePlayers()));
        hidesFromPlayer(new ArrayList<>(getServer().getOnlinePlayers()), player);
    }
    public void hidesFromPlayer(List<Player> players, Player hideFrom){
        players.forEach(pl -> hide(pl, hideFrom));
    }
    public void hide(Player player, Player hideFrom){
        if (player.equals(hideFrom)) return;
        if (playerHideFor.containsKey(player.getUniqueId()) && playerHideFor.get(player.getUniqueId()).contains(hideFrom.getUniqueId())) return;
        
        EntityArmorStand armorStand;

        if (!playerPassager.containsKey(player.getUniqueId())) {
            armorStand = addArmorSand(player);
            playerPassager.put(player.getUniqueId(), armorStand);
        }
        else armorStand = playerPassager.get(player.getUniqueId());

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);

        CraftPlayer cp = ((CraftPlayer) hideFrom);
        cp.getHandle().playerConnection.sendPacket(packet);
        if (!playerHideFor.containsKey(player.getUniqueId())) playerHideFor.put(player.getUniqueId(),new ArrayList<>());
        playerHideFor.get(player.getUniqueId()).add(hideFrom.getUniqueId());
        
    }
    public void unhide(Player player, Player hideFrom){
        if (player.equals(player)) return;
        List asso = Arrays.asList(player.getUniqueId(), hideFrom.getUniqueId());
        if (counterShowPlayer.containsKey(asso)) return;

        if (!playerHideFor.containsKey(player.getUniqueId()) || !playerHideFor.get(player.getUniqueId()).contains(hideFrom)) return;

        EntityArmorStand armorStand = playerPassager.get(player.getUniqueId());
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.getId());
        
        ((CraftPlayer)hideFrom).getHandle().playerConnection.sendPacket(packet);
        
    
        playerHideFor.get(player.getUniqueId()).remove(hideFrom.getUniqueId());
        counterShowPlayer.put(asso, System.currentTimeMillis());
    }

    public EntityArmorStand addArmorSand(Player p){
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld)p.getWorld()).getHandle(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        armorStand.setCustomName("mask");
        armorStand.setInvisible(true);;
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setCustomNameVisible(false);

        CraftPlayer cp = (CraftPlayer) p;
        cp.getHandle().passengers.add(armorStand);
        return armorStand;
    }
    // public void remArmorSand(Player p){
    //     int i =0;
    //     while (i<p.getPassengers().size()) {
    //         Entity e = p.getPassengers().get(i);
    //         if (e.getType() == EntityType.ARMOR_STAND && ((ArmorStand)e).getCustomName().equals("mask")) {
    //             p.removePassenger(e);
    //         }else i++;
    //     }
    //     counterShowPlayer.put(p, System.currentTimeMillis());
    // }

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

        for (Player player : getServer().getOnlinePlayers()){
            fullHide(player);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                Iterator<Entry<List<UUID>, Long>> iterator = counterShowPlayer.entrySet().iterator();
                
                while (iterator.hasNext()) {
                    Entry<List<UUID>, Long> entry = iterator.next();
                    if (System.currentTimeMillis() - entry.getValue()>= timer*1000) {
                        unhide(Bukkit.getPlayer(entry.getKey().get(0)), Bukkit.getPlayer(entry.getKey().get(1)));
                        counterShowPlayer.remove(entry.getKey());
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

package fr.yohem.modostick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ModoStickListerner implements Listener {
    public ModoStickListerner (){

    }
    @EventHandler
    public void onJoin(PlayerInteractAtEntityEvent playerInteractEvent){
        Player p = playerInteractEvent.getPlayer();
        if (/*playerInteractEvent.getRightClicked() instanceof Player &&*/ p.getInventory().getItemInMainHand().equals(ModoStick.getStick())){
            if (p.hasPermission("stick.use"))
                p.sendMessage( playerInteractEvent.getRightClicked().getName());
            else{
                p.sendMessage("Petit tricheur à la prochainne filouterie, la sentence risque d'être irrévocable");
                p.getInventory().removeItem(ModoStick.getStick());
            }
        }
    }
}

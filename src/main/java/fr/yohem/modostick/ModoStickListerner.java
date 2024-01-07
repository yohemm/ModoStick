package fr.yohem.modostick;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ModoStickListerner implements Listener {
    ModoStick modoStick;
    public ModoStickListerner (ModoStick modoStick){
        this.modoStick = modoStick;
    }
    @EventHandler
    public void onIteract(PlayerInteractAtEntityEvent playerInteractEvent){
        Player p = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getRightClicked() instanceof Player && p.getInventory().getItemInMainHand().equals(ModoStick.getStick())){
            if (p.hasPermission("stick.use")){
                p.sendMessage(playerInteractEvent.getRightClicked().getName());
                modoStick.hide((Player)playerInteractEvent.getRightClicked(), p);
                // modoStick.hidePlayerName((Player)playerInteractEvent.getRightClicked());
            }else{
                p.sendMessage("Petit tricheur à la prochainne filouterie, la sentence risque d'être irrévocable");
                p.getInventory().removeItem(ModoStick.getStick());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        // modoStick.hidePlayerName(event.getPlayer());
    }
}

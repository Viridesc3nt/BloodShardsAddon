package me.justinjaques.bloodshards;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import static org.bukkit.event.block.Action.LEFT_CLICK_AIR;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;

public class BloodShardsListener implements Listener {

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {

            Player player = event.getPlayer();

            System.out.println("Player has sneaked");
            BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

            if(bPlayer.canBend(CoreAbility.getAbility(BloodShards.class))) {
                new BloodShards(player);



        }

    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if(event.getAction() != LEFT_CLICK_AIR && event.getAction() != LEFT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        BloodShards bloodShards = CoreAbility.getAbility(player, BloodShards.class);

        if(bloodShards == null) {
            return;
        }
        bloodShards.onClick();

    }



}


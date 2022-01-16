package me.justinjaques.bloodshards;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.BloodAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public final class BloodShards extends BloodAbility implements AddonAbility {

    private enum States {
        SHARDS_READY, TRAVELLING

    }


    private static final String AUTHOR = ChatColor.BLUE + "Viridescent_";
    private static final String VERSION = ChatColor.BLUE + "1.0.0";
    private static final String NAME = "BloodShards";
    static String path = "ExtraAbilities.Viridescent_.Blood.BloodShards.";
    private static int NUMOFSHARDS;
    private static long COOLDOWN;
    private static long RANGE;
    private static double SPEED;
    private static double DAMAGEPERSHARD;
    private States state;


    private Location location;
    private Location locationOfDisplay;
    private Permission perm;
    private Vector direction;
    private double distanceTravelled;
    private Listener listener;




    private void setFields() {
        NUMOFSHARDS = ConfigManager.defaultConfig.get().getInt(path+"NUMOFSHARDS");
        COOLDOWN = ConfigManager.defaultConfig.get().getLong(path+"COOLDOWN");
        RANGE = ConfigManager.defaultConfig.get().getLong(path+"RANGE");
        SPEED = ConfigManager.defaultConfig.get().getInt(path+"SPEED");
        DAMAGEPERSHARD = ConfigManager.defaultConfig.get().getInt(path+"DAMAGE");


    }




    public BloodShards(Player player) {
        super(player);
        setFields();





        state = States.SHARDS_READY;


        if(!bPlayer.isOnCooldown(this)) {
            start();
        }
    }


    public void onClick() {

        if(state == States.SHARDS_READY) {

            direction = player.getLocation().getDirection();
            direction.multiply(SPEED);
            location = player.getEyeLocation();
            distanceTravelled = 0;
            NUMOFSHARDS--;
            state = States.TRAVELLING;
        }
    }

    public void removeWithCooldown() {
        bPlayer.addCooldown(this);
        remove();
    }


    private void progressShardsReady() {
        Vector v = player.getLocation().getDirection();
        v.setY(0);
        locationOfDisplay = player.getEyeLocation().subtract(0, 0.6, 0).add(v);
        if(!player.isSneaking()) {
            removeWithCooldown();
        }
        for(int i = 0; i <= NUMOFSHARDS; i++) {
            ParticleEffect.REDSTONE.display(locationOfDisplay, 1, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 0, 0), (float) 1.2));
        }
    }

    private void affectTargets() {
        List<Entity> targets  = GeneralMethods.getEntitiesAroundPoint(location, 1);
        for (Entity target : targets) {
            if (target.getUniqueId() == player.getUniqueId()) {
                continue;
            }

            DamageHandler.damageEntity(target, DAMAGEPERSHARD, this);

        }

    }


    private void progressTravelling() {
        if(!player.isSneaking()) {
            removeWithCooldown();
        }
        for (double d = 0; d < SPEED; d += 0.05 * SPEED) {
            ParticleEffect.REDSTONE.display(location, 10, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(102, 0 ,0), (float) 1.2));
            location.add(direction);
            this.distanceTravelled += SPEED;
            affectTargets();

            if(this.distanceTravelled > RANGE) {
                if(NUMOFSHARDS == 0) {
                    removeWithCooldown();
                    return;
                }
                state = States.SHARDS_READY;
            }


        }
        
    }


    @Override
    public void progress() {
        if(!bPlayer.canBend(this)) {
            removeWithCooldown();
            return;
        }

        switch(state) {
            case SHARDS_READY:
                progressShardsReady();
                break;
            case TRAVELLING:
                progressTravelling();
                break;
        }
        
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return COOLDOWN;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void load() {
        perm = new Permission("bending.ability.BloodShards");
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
        listener = new BloodShardsListener();
        ConfigManager.defaultConfig.get().addDefault(path+"NUMOFSHARDS", 4);
        ConfigManager.defaultConfig.get().addDefault(path+"DAMAGERPERSHARD", 1);
        ConfigManager.defaultConfig.get().addDefault(path+"RANGE", 40);
        ConfigManager.defaultConfig.get().addDefault(path+"SPEED", 5);
        ConfigManager.defaultConfig.get().addDefault(path+"COOLDOWN", 5000);
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);

    }

    @Override
    public void stop() {
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
        HandlerList.unregisterAll(listener);


    }

    @Override
    public String getAuthor() {
        return AUTHOR;
    }

    @Override
    public String getInstructions() {
        return ChatColor.BLUE + "Hold sneak to draw out Blood from your own body and solidify it to form 3 pellets. Afterwards, LEFT-CLICK a desired target";

    }


    @Override
    public String getDescription() {
        return ChatColor.BLUE + "BloodShards is a forbidden BloodBending technique that allows it's user to draw out Blood from their own body, turn it into Ice, and shoot it to their will.";

    }


    @Override
    public String getVersion() {
        return VERSION;
    }
}


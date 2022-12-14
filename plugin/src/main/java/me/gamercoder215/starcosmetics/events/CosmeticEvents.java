package me.gamercoder215.starcosmetics.events;

import me.gamercoder215.starcosmetics.StarCosmetics;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.cosmetics.CosmeticLocation;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.Pet;
import me.gamercoder215.starcosmetics.api.cosmetics.pet.StarPet;
import me.gamercoder215.starcosmetics.api.cosmetics.trail.Trail;
import me.gamercoder215.starcosmetics.api.cosmetics.trail.TrailType;
import me.gamercoder215.starcosmetics.api.player.SoundEventSelection;
import me.gamercoder215.starcosmetics.api.player.StarPlayer;
import me.gamercoder215.starcosmetics.api.player.StarPlayerUtil;
import me.gamercoder215.starcosmetics.wrapper.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.*;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;

import static me.gamercoder215.starcosmetics.wrapper.Wrapper.getWrapper;

public final class CosmeticEvents implements Listener {

    private static final Wrapper w = getWrapper();

    private final StarCosmetics plugin;

    public CosmeticEvents(StarCosmetics plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        RegisteredListener reg = new RegisteredListener(this, (l, e) -> onEvent(e), EventPriority.NORMAL, plugin, false);
        for (HandlerList h : HandlerList.getHandlerLists()) h.register(reg);
    }

    public void onEvent(Event e) {
        if (SoundEventSelection.isValid(e.getClass())) {
            Player p = null;
            try {
                Method get = e.getClass().getDeclaredMethod("getPlayer");
                get.setAccessible(true);
                p = (Player) get.invoke(e);
            } catch (NoSuchMethodException ignored) {
                Class<?> superC = e.getClass();
                while (superC.getSuperclass() != null) try {
                    superC = superC.getSuperclass();
                    Method get = superC.getDeclaredMethod("getPlayer");
                    get.setAccessible(true);

                    p = (Player) get.invoke(e);
                    break;
                } catch (NoSuchMethodException ignored2) {
                } catch (ReflectiveOperationException err2) {
                    StarConfig.print(err2);
                }
            } catch (ReflectiveOperationException err) {
                StarConfig.print(err);
            }

            if (p == null) throw new IllegalStateException("Invalid Selection Event: " + e.getClass().getName());

            StarPlayer sp = new StarPlayer(p);

            for (SoundEventSelection sel : sp.getSoundSelections())
                if (sel.getEvent().isAssignableFrom(e.getClass())) {
                    sel.play(p);
                    break;
                }
        }
    }

    @EventHandler
    public void onShoot(ProjectileLaunchEvent e) {
        Projectile en = e.getEntity();
        if (!(en.getShooter() instanceof Player)) return;
        Player p = (Player) en.getShooter();
        StarPlayer sp = new StarPlayer(p);

        CosmeticLocation<?> proj = sp.getSelectedTrail(TrailType.PROJECTILE);
        if (proj != null) ((Trail<?>) proj.getParent()).run(en, proj);

        CosmeticLocation<?> sProj = sp.getSelectedTrail(TrailType.PROJECTILE_SOUND);
        if (sProj != null) ((Trail<?>) sProj.getParent()).run(en, sProj);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        Projectile en = e.getEntity();
        en.setMetadata("stopped", new FixedMetadataValue(plugin, true));
    }

    private static boolean pitchChanged(Location l1, Location l2) {
        return !l1.getDirection().equals(l2.getDirection()) &&
                l1.getX() == l2.getX() &&
                l1.getY() == l2.getY() &&
                l1.getZ() == l2.getZ();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        StarPlayer sp = StarCosmetics.getCached(p);

        if (sp.getSpawnedPet() != null) {
            Pet spawned = sp.getSpawnedPet();
            if (spawned.getEntity() instanceof ArmorStand) {
                ArmorStand as = (ArmorStand) spawned.getEntity();

                as.teleport(StarPlayerUtil.createPetLocation(p));
                w.setRotation(as, p.getLocation().getYaw(), p.getLocation().getPitch());

                if (pitchChanged(e.getFrom(), e.getTo())) return;

                if (spawned instanceof StarPet) {
                    StarPet shp = (StarPet) spawned;
                    shp.getInfo().tick(as);
                }
            }
        }

        if (!pitchChanged(e.getFrom(), e.getTo())) {
            CosmeticLocation<?> ground = sp.getSelectedTrail(TrailType.GROUND);
            if (ground != null) ((Trail<?>) ground.getParent()).run(p, ground);
        }
    }

}

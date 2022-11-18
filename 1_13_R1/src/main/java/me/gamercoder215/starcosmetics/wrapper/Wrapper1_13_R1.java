package me.gamercoder215.starcosmetics.wrapper;

import com.mojang.authlib.GameProfile;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.events.CompletionEvents1_12_R1;
import me.gamercoder215.starcosmetics.util.entity.StarSelector;
import me.gamercoder215.starcosmetics.util.inventory.StarInventory;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper1_13_R1;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class Wrapper1_13_R1 implements Wrapper {

    @Override
    public int getCommandVersion() {
        return 2;
    }

    @Override
    public NBTWrapper getNBTWrapper(ItemStack item) {
        return new NBTWrapper1_13_R1(item);
    }

    @Override
    public void sendActionbar(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public void spawnFakeEntity(Player p, EntityType type, Location loc, long deathTicks) {
        CraftWorld cw = (CraftWorld) loc.getWorld();
        EntityPlayer sp = ((CraftPlayer) p).getHandle();
        Entity nmsEntity = cw.createEntity(loc, type.getEntityClass());

        PacketPlayOutSpawnEntity add = new PacketPlayOutSpawnEntity(nmsEntity, 0);
        sp.playerConnection.sendPacket(add);

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(nmsEntity.getId());
                sp.playerConnection.sendPacket(remove);
            }
        }.runTaskLater(StarConfig.getPlugin(), deathTicks);
    }

    @Override
    public void spawnFakeItem(ItemStack item, Location loc, long deathTicks) {
        WorldServer ws = ((CraftWorld) loc.getWorld()).getHandle();
        EntityItem nmsEntity = new EntityItem(ws, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        nmsEntity.p();
        ws.addEntity(nmsEntity);

        new BukkitRunnable() {
            @Override
            public void run() {
                nmsEntity.killEntity();
            }
        }.runTaskLater(StarConfig.getPlugin(), deathTicks);
    }

    private static EntityPlayer createPlayer(Location loc) {
        try {
            MinecraftServer srv = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer sw = ((CraftWorld) loc.getWorld()).getHandle();

            UUID uid = UUID.randomUUID();
            EntityPlayer sp = new EntityPlayer(srv, sw, new GameProfile(uid, uid.toString().substring(0, 16)), new PlayerInteractManager(sw));
            sp.playerConnection = new PlayerConnection(srv, new NetworkManager(EnumProtocolDirection.SERVERBOUND), sp);
            sp.setPosition(loc.getX(), loc.getY(), loc.getZ());

            for (Player p : loc.getWorld().getPlayers()) {
                EntityPlayer sph = ((CraftPlayer) p).getHandle();
                sph.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, sp));
                sph.playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(sp));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sph.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, sp));
                    }
                }.runTaskLaterAsynchronously(StarConfig.getPlugin(), 1);
            }

            return sp;
        } catch (Exception e) {
            StarConfig.print(e);
            return null;
        }
    }

    private static float normalize(float rot) {
        float v = rot;
        while (v < -180.0F) v += 360.0F;
        while (v >= 180.0F) v -= 360.0F;
        return v;
    }

    @Override
    public void attachRiptide(org.bukkit.entity.Entity en) {
        EntityPlayer sp = createPlayer(en.getLocation());
        sp.setSilent(true);
        sp.setInvulnerable(true);
        sp.setNoGravity(true);
        sp.setInvisible(true);

        DataWatcher dw = sp.getDataWatcher();
        dw.set(DataWatcherRegistry.a.a(8), (byte) 0x04);

        for (Player p : en.getWorld().getPlayers()) {
            EntityPlayer sph = ((CraftPlayer) p).getHandle();
            sph.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(sp.getId(), dw, true));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (StarSelector.isStopped(en)) {
                    cancel();
                    sp.die();

                    for (Player p : en.getWorld().getPlayers()) {
                        EntityPlayer sph = ((CraftPlayer) p).getHandle();
                        sph.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(sp.getId()));
                    }

                    return;
                }

                Location l = en.getLocation();

                sp.setLocation(l.getX(), l.getY(), l.getZ(), normalize(en.getLocation().getYaw() - 180.0F), normalize(en.getLocation().getPitch() - 180.0F));
                sp.setHeadRotation(normalize(en.getLocation().getYaw() - 180.0F));
                for (Player p : en.getWorld().getPlayers()) {
                    EntityPlayer sph = ((CraftPlayer) p).getHandle();
                    sph.playerConnection.sendPacket(new PacketPlayOutEntityTeleport(sp));
                }
            }
        }.runTaskTimer(StarConfig.getPlugin(), 0, 2);
    }

    @Override
    public void setRotation(org.bukkit.entity.Entity en, float yaw, float pitch) {
        Entity nms = ((CraftEntity) en).getHandle();

        nms.yaw = yaw % 360.0F;
        nms.pitch = pitch % 360.0F;
    }

    @Override
    public boolean isItem(org.bukkit.Material m) {
        if (m == org.bukkit.Material.AIR) return false;
        return m.isItem();
    }

    @Override
    public StarInventory createInventory(String key, int size, String title) {
        return new StarInventory1_13_R1(key, size, title);
    }

    @Override
    public void registerEvents() {
        new CompletionEvents1_12_R1();
    }

}

package me.gamercoder215.starcosmetics.wrapper;

import com.mojang.authlib.GameProfile;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.events.CompletionEvents1_12_R1;
import me.gamercoder215.starcosmetics.util.entity.StarSelector;
import me.gamercoder215.starcosmetics.util.inventory.StarInventory;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper1_17_R1;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public final class Wrapper1_17_R1 implements Wrapper {

    @Override
    public int getCommandVersion() {
        return 2;
    }

    @Override
    public boolean isItem(org.bukkit.Material m) {
        if (m == org.bukkit.Material.AIR) return false;
        return m.isItem();
    }

    @Override
    public NBTWrapper getNBTWrapper(ItemStack item) {
        return new NBTWrapper1_17_R1(item);
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

        PacketPlayOutSpawnEntity add = new PacketPlayOutSpawnEntity(nmsEntity);
        sp.b.sendPacket(add);

        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(nmsEntity.getId());
                sp.b.sendPacket(remove);
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
            DedicatedServer srv = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer sw = ((CraftWorld) loc.getWorld()).getHandle();

            UUID uid = UUID.randomUUID();
            EntityPlayer sp = new EntityPlayer(srv, sw, new GameProfile(uid, uid.toString().substring(0, 16)));
            sp.b = new PlayerConnection(srv, new NetworkManager(EnumProtocolDirection.a), sp);
            sp.setPosition(loc.getX(), loc.getY(), loc.getZ());

            for (Player p : loc.getWorld().getPlayers()) {
                EntityPlayer sph = ((CraftPlayer) p).getHandle();
                sph.b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, sp));
                sph.b.sendPacket(new PacketPlayOutNamedEntitySpawn(sp));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sph.b.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, sp));
                    }
                }.runTaskLaterAsynchronously(StarConfig.getPlugin(), 1);
            }

            return sp;
        } catch (Exception e) {
            StarConfig.print(e);
            return null;
        }
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
            sph.b.sendPacket(new PacketPlayOutEntityMetadata(sp.getId(), dw, true));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (StarSelector.isStopped(en)) {
                    cancel();
                    sp.a(Entity.RemovalReason.b);

                    for (Player p : en.getWorld().getPlayers()) {
                        EntityPlayer sph = ((CraftPlayer) p).getHandle();
                        sph.b.sendPacket(new PacketPlayOutEntityDestroy(sp.getId()));
                    }

                    return;
                }

                Location l = en.getLocation().clone();

                Vec3D pos = sp.getPositionVector();
                Vector dir = l.toVector().subtract(new Location(en.getWorld(), pos.getX(), pos.getY(), pos.getZ()).toVector()).normalize().multiply(-1);

                if (Double.isNaN(dir.getX())) dir.setX(0);
                if (Double.isNaN(dir.getY())) dir.setY(0);
                if (Double.isNaN(dir.getZ())) dir.setZ(0);

                l.setDirection(dir);

                sp.setLocation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                sp.setHeadRotation(l.getYaw());
                for (Player p : en.getWorld().getPlayers()) {
                    EntityPlayer sph = ((CraftPlayer) p).getHandle();
                    sph.b.sendPacket(new PacketPlayOutEntityTeleport(sp));
                }
            }
        }.runTaskTimer(StarConfig.getPlugin(), 0, 2);
    }

    @Override
    public void setRotation(org.bukkit.entity.Entity en, float yaw, float pitch) {
        en.setRotation(yaw, pitch);
    }

    @Override
    public String getKey(Sound s) {
        return s.getKey().toString();
    }

    @Override
    public void stopSound(Player p) {
        PacketPlayOutStopSound packet = new PacketPlayOutStopSound(null, SoundCategory.a);
        ((CraftPlayer) p).getHandle().b.sendPacket(packet);
    }

    @Override
    public void sendBlockChange(Player p, Location loc, Material m, BlockState data) {
        EntityPlayer sp = ((CraftPlayer) p).getHandle();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());

        if (data == null) {
            p.sendBlockChange(loc, m.createBlockData());
            return;
        }

        IBlockData nmsState = ((CraftBlockState) data).getHandle();
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(pos, nmsState);
        sp.b.sendPacket(packet);
    }

    @Override
    public StarInventory createInventory(String key, int size, String title) {
        return new StarInventory1_17_R1(key, size, title);
    }

    @Override
    public void registerEvents() {
        new CompletionEvents1_12_R1();
    }

    @Override
    public String getAdvancementDescription(String s) {
        CraftAdvancement ca = (CraftAdvancement) Bukkit.getAdvancement(NamespacedKey.minecraft(s));
        AdvancementDisplay display = ca.getHandle().c();

        return display.b().getString();
    }

}

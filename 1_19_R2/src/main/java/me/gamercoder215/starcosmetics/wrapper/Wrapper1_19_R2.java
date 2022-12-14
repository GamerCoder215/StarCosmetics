package me.gamercoder215.starcosmetics.wrapper;

import com.mojang.authlib.GameProfile;
import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.events.CompletionEvents1_12_R1;
import me.gamercoder215.starcosmetics.util.StarRunnable;
import me.gamercoder215.starcosmetics.util.entity.StarSelector;
import me.gamercoder215.starcosmetics.util.inventory.StarInventory;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper1_19_R2;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_19_R2.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.UUID;

public final class Wrapper1_19_R2 implements Wrapper {

    @Override
    public int getCommandVersion() {
        return 2;
    }

    @Override
    public NBTWrapper getNBTWrapper(ItemStack item) {
        return new NBTWrapper1_19_R2(item);
    }

    @Override
    public void sendActionbar(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public void spawnFakeEntity(Player p, EntityType type, Location loc, long deathTicks) {
        CraftWorld cw = (CraftWorld) loc.getWorld();
        ServerPlayer sp = ((CraftPlayer) p).getHandle();
        Entity nmsEntity = cw.createEntity(loc, type.getEntityClass());

        ClientboundAddEntityPacket add = new ClientboundAddEntityPacket(nmsEntity);
        sp.connection.send(add);

        new BukkitRunnable() {
            @Override
            public void run() {
                ClientboundRemoveEntitiesPacket remove = new ClientboundRemoveEntitiesPacket(nmsEntity.getId());
                sp.connection.send(remove);
            }
        }.runTaskLater(StarConfig.getPlugin(), deathTicks);
    }

    @Override
    public void spawnFakeItem(ItemStack item, Location loc, long deathTicks) {
        ServerLevel sw = ((CraftWorld) loc.getWorld()).getHandle();
        ItemEntity nmsEntity = new ItemEntity(sw, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        nmsEntity.setNeverPickUp();
        sw.addFreshEntity(nmsEntity);

        StarRunnable.syncLater(nmsEntity::kill, deathTicks);
    }

    private static ServerPlayer createPlayer(Location loc) {
        try {
            DedicatedServer srv = ((CraftServer) Bukkit.getServer()).getServer();
            ServerLevel sw = ((CraftWorld) loc.getWorld()).getHandle();
            UUID uid = UUID.randomUUID();

            ServerPlayer sp = new ServerPlayer(srv, sw, new GameProfile(uid, uid.toString().substring(0, 16)));
            sp.connection = new ServerGamePacketListenerImpl(srv, new Connection(PacketFlow.CLIENTBOUND), sp);
            sp.setPos(loc.getX(), loc.getY(), loc.getZ());

            for (Player p : loc.getWorld().getPlayers()) {
                ServerPlayer sph = ((CraftPlayer) p).getHandle();
                sph.connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, sp));
                sph.connection.send(new ClientboundAddPlayerPacket(sp));
                StarRunnable.asyncLater(() -> sph.connection.send(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(uid))),
                        1);
            }

            return sp;
        } catch (Exception e) {
            StarConfig.print(e);
            return null;
        }
    }

    @Override
    public void attachRiptide(org.bukkit.entity.Entity en) {
        ServerPlayer sp = createPlayer(en.getLocation());
        sp.setSilent(true);
        sp.setInvulnerable(true);
        sp.setNoGravity(true);
        sp.setInvisible(true);

        SynchedEntityData dw = sp.getEntityData();
        dw.set(EntityDataSerializers.BYTE.createAccessor(8), (byte) 0x04);

        for (Player p : en.getWorld().getPlayers()) {
            ServerPlayer sph = ((CraftPlayer) p).getHandle();
            sph.connection.send(new ClientboundSetEntityDataPacket(sp.getId(), dw.packDirty()));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (StarSelector.isStopped(en)) {
                    cancel();
                    sp.remove(Entity.RemovalReason.DISCARDED);

                    for (Player p : en.getWorld().getPlayers()) {
                        ServerPlayer sph = ((CraftPlayer) p).getHandle();
                        sph.connection.send(new ClientboundRemoveEntitiesPacket(sp.getId()));
                    }

                    return;
                }

                Location l = en.getLocation().clone();

                Vec3 pos = sp.position();
                Vector dir = l.toVector().subtract(new Location(en.getWorld(), pos.x, pos.y, pos.z).toVector()).normalize().multiply(-1);

                if (Double.isNaN(dir.getX())) dir.setX(0);
                if (Double.isNaN(dir.getY())) dir.setY(0);
                if (Double.isNaN(dir.getZ())) dir.setZ(0);

                l.setDirection(dir);

                sp.absMoveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                sp.setYHeadRot(l.getYaw());

                for (Player p : en.getWorld().getPlayers()) {
                    ServerPlayer sph = ((CraftPlayer) p).getHandle();
                    sph.connection.send(new ClientboundTeleportEntityPacket(sp));
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
        p.stopAllSounds();
    }

    @Override
    public void sendBlockChange(Player p, Location loc, Material m, BlockState data) {
        ServerPlayer sp = ((CraftPlayer) p).getHandle();
        BlockPos pos = new BlockPos(loc.getX(), loc.getY(), loc.getZ());

        if (data == null) {
            p.sendBlockChange(loc, m.createBlockData());
            return;
        }

        net.minecraft.world.level.block.state.BlockState nmsState = ((CraftBlockState) data).getHandle();
        ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(pos, nmsState);
        sp.connection.send(packet);
    }

    @Override
    public boolean isItem(org.bukkit.Material m) {
        if (m == org.bukkit.Material.AIR) return false;
        return m.isItem();
    }

    @Override
    public StarInventory createInventory(String key, int size, String title) {
        return new StarInventory1_19_R2(key, size, title);
    }

    @Override
    public void registerEvents() {
        new CompletionEvents1_12_R1();
    }

    @Override
    public String getAdvancementDescription(String s) {
        CraftAdvancement ca = (CraftAdvancement) Bukkit.getAdvancement(NamespacedKey.minecraft(s));
        DisplayInfo display = ca.getHandle().getDisplay();

        return display.getDescription().getString();
    }

}


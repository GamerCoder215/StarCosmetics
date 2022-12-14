package me.gamercoder215.starcosmetics.wrapper;

import io.netty.buffer.Unpooled;
import me.gamercoder215.starcosmetics.util.StarRunnable;
import me.gamercoder215.starcosmetics.util.inventory.StarInventory;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper;
import me.gamercoder215.starcosmetics.wrapper.nbt.NBTWrapper1_10_R1;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_10_R1.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_10_R1.CraftSound;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public final class Wrapper1_10_R1 implements Wrapper {

    @Override
    public int getCommandVersion() {
        return 2;
    }

    @Override
    public NBTWrapper getNBTWrapper(ItemStack item) {
        return new NBTWrapper1_10_R1(item);
    }

    @Override
    public void sendActionbar(Player p, String message) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    @Override
    public String getKey(Sound s) {
        SoundEffect se = CraftSound.getSoundEffect(CraftSound.getSound(s));
        MinecraftKey key = SoundEffect.a.b(se);

        return key.toString();
    }

    @Override
    public void stopSound(Player p) {
        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|StopSound", new PacketDataSerializer(Unpooled.buffer()).a(""));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void spawnFakeEntity(Player p, EntityType type, Location loc, long deathTicks) {
        CraftWorld cw = (CraftWorld) loc.getWorld();
        EntityPlayer sp = ((CraftPlayer) p).getHandle();
        Entity nmsEntity = cw.createEntity(loc, type.getEntityClass());

        PacketPlayOutSpawnEntity add = new PacketPlayOutSpawnEntity(nmsEntity, 0);
        sp.playerConnection.sendPacket(add);

        StarRunnable.syncLater(() -> {
            PacketPlayOutEntityDestroy remove = new PacketPlayOutEntityDestroy(nmsEntity.getId());
            sp.playerConnection.sendPacket(remove);
        }, deathTicks);
    }

    @Override
    public void spawnFakeItem(ItemStack item, Location loc, long deathTicks) {
        WorldServer ws = ((CraftWorld) loc.getWorld()).getHandle();
        EntityItem nmsEntity = new EntityItem(ws, loc.getX(), loc.getY(), loc.getZ(), CraftItemStack.asNMSCopy(item));
        nmsEntity.s();
        ws.addEntity(nmsEntity);

         StarRunnable.syncLater(nmsEntity::Q, deathTicks);
    }

    @Override
    public void attachRiptide(org.bukkit.entity.Entity en) {
        throw new UnsupportedOperationException();
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
        return Item.getById(m.getId()) != null;
    }

    @Override
    public void sendBlockChange(Player p, Location loc, org.bukkit.Material m, BlockState data) {
        p.sendBlockChange(loc, m, data == null ? (byte) 0 : data.getRawData());
    }

    @Override
    public StarInventory createInventory(String key, int size, String title) {
        return new StarInventory1_10_R1(key, size, title);
    }

    @Override
    public String getAdvancementDescription(String s) {
        throw new UnsupportedOperationException();
    }

}

package me.gamercoder215.starcosmetics.wrapper.nbt;

import me.gamercoder215.starcosmetics.api.StarConfig;
import me.gamercoder215.starcosmetics.api.player.cosmetics.SoundEventSelection;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.event.Event;

import java.util.Date;
import java.util.UUID;

public final class NBTWrapper1_13_R1 extends NBTWrapper {

    public NBTWrapper1_13_R1(org.bukkit.inventory.ItemStack item) {
        super(item);
    }

    @Override
    public String getString(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.getString(key);
    }

    @Override
    public void set(String key, String value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setString(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public boolean getBoolean(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.getBoolean(key);
    }

    @Override
    public void set(String key, boolean value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setBoolean(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public int getInt(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.getInt(key);
    }

    @Override
    public void set(String key, int value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setInt(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public double getDouble(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.getDouble(key);
    }

    @Override
    public void set(String key, double value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setDouble(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public UUID getUUID(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.a(key);
    }

    @Override
    public void set(String key, UUID value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.a(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public Class<?> getClass(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        try {
            return Class.forName(new String(starcosmetics.getByteArray(key)));
        } catch (ClassNotFoundException e) {
            StarConfig.print(e);
            return null;
        }
    }

    @Override
    public void set(String key, Class<?> clazz) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setByteArray(key, clazz.getName().getBytes());
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public void set(String key, SoundEventSelection value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        NBTTagCompound selection = new NBTTagCompound();
        selection.setString("sound", value.getSound().name());
        selection.setString("event", value.getEvent().getName());
        selection.setLong("timestamp", value.getTimestamp().getTime());
        selection.a("player", value.getPlayer().getUniqueId());
        selection.getFloat("volume");
        selection.getFloat("pitch");

        starcosmetics.set(key, selection);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public SoundEventSelection getSoundEventSelection(String key) {
        try {
            ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
            NBTTagCompound tag = nmsitem.getOrCreateTag();
            NBTTagCompound starcosmetics = tag.getCompound(ROOT);

            NBTTagCompound selection = starcosmetics.getCompound(key);

            return SoundEventSelection.of(
                    Class.forName(selection.getString("event")).asSubclass(Event.class),
                    Sound.valueOf(selection.getString("sound")),
                    selection.getFloat("volume"),
                    selection.getFloat("pitch"),
                    Bukkit.getOfflinePlayer(selection.a("player")),
                    new Date(selection.getLong("timestamp"))
            );
        } catch (ClassNotFoundException | ClassCastException e) {
            StarConfig.print(e);
            return null;
        }
    }

    @Override
    public void set(String key, float value) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        starcosmetics.setFloat(key, value);
        tag.set(ROOT, starcosmetics);
        nmsitem.setTag(tag);
        this.item = CraftItemStack.asBukkitCopy(nmsitem);
    }

    @Override
    public float getFloat(String key) {
        ItemStack nmsitem = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nmsitem.getOrCreateTag();
        NBTTagCompound starcosmetics = tag.getCompound(ROOT);

        return starcosmetics.getFloat(key);
    }

}

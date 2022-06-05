package me.redtea.factionrevolutions.utils;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.redtea.factionrevolutions.core.FRevolutions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ItemUtil {
    private final FRevolutions plugin;

    public ItemStack create(Material material, int amount, byte data, String displayName, ArrayList<String> lore) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            assert meta != null;
            meta.setDisplayName(displayName);
        }
        if (lore.size() != 0) {
            assert meta != null;
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack create(Material material, int amount, byte data, String displayName) {
        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            assert meta != null;
            meta.setDisplayName(displayName);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack create(Material material, int amount, byte data) {
        return new ItemStack(material, amount, data);
    }

    public ItemStack stringToItem(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            plugin.getLog().sendError("Unable to deserialize item stack");
            throw new IllegalStateException(e);
        }
    }

    public String itemToString(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            plugin.getLog().sendError("Unable to serialize item stack");
            throw new IllegalStateException(e);
        }
    }

}

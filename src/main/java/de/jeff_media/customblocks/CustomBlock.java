package de.jeff_media.customblocks;

import de.jeff_media.customblocks.implentation.HeadBlock;
import de.jeff_media.customblocks.implentation.ItemsAdderBlock;
import de.jeff_media.customblocks.implentation.OraxenBlock;
import de.jeff_media.customblocks.implentation.VanillaBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import de.jeff_media.jefflib.exceptions.MissingPluginException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.util.*;

public abstract class CustomBlock {

    public static CustomBlock fromStringOrDefault(String fullId, Material fallback) {
        try {
            return fromStringOrThrow(fullId);
        } catch (MissingPluginException | InvalidBlockDataException e) {
            return new VanillaBlock(fallback);
        }
    }

    public static CustomBlock fromStringOrThrow(String fullId) throws InvalidBlockDataException, MissingPluginException {
            if (fullId.startsWith("minecraft:") || !fullId.contains(":")) {
                return new VanillaBlock(fullId);
            }

            String[] split = fullId.split(":",2);
            if(split.length==1) {
                throw new InvalidBlockDataException("Could not parse custom block data: " + fullId);
            }

            String namespace = split[0];
            String id = split[1];

            switch (namespace.toLowerCase(Locale.ROOT)) {
                case "head":
                    return new HeadBlock(id);
                case "itemsadder":
                    checkForPlugin("itemsadder","ItemsAdder");
                    return new ItemsAdderBlock(id);
                case "oraxen":
                    checkForPlugin("oraxen","Oraxen");
                    return new OraxenBlock(id);
            }

            throw new InvalidBlockDataException("Could not parse custom block data: " + fullId);
    }

    private static void checkForPlugin(String namespace, String pluginName) throws MissingPluginException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if(plugin == null || !plugin.isEnabled()) {
            throw new MissingPluginException(String.format("Placing custom blocks from namespace \"%s\" requires the following plugin to be installed: \"%s\"",
                    namespace, pluginName));
        }
    }

    public abstract PlacedCustomBlock place(Block block);

    public abstract PlacedCustomBlock place(Block block, OfflinePlayer player);

    public CustomBlock(String id) {
        this.id = id;
    };

    public abstract String getNamespace();

    @Getter private final String id;

    public abstract Material getMaterial();

}

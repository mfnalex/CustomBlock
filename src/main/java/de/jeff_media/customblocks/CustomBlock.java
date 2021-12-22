package de.jeff_media.customblocks;

import de.jeff_media.customblocks.implentation.HeadBlock;
import de.jeff_media.customblocks.implentation.ItemsAdderBlock;
import de.jeff_media.customblocks.implentation.OraxenBlock;
import de.jeff_media.customblocks.implentation.VanillaBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import de.jeff_media.jefflib.exceptions.MissingPluginException;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public abstract class CustomBlock {

    public static CustomBlock fromString(String fullId) throws InvalidBlockDataException, MissingPluginException {
            if (fullId.startsWith("minecraft:") || !fullId.contains(":")) {
                return new VanillaBlock(fullId);
            }

            String[] split = fullId.split(":");
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

    public abstract void place(Block block);

    public CustomBlock(String id) {
        this.id = id;
    };

    public abstract String getNamespace();

    @Getter private final String id;
}

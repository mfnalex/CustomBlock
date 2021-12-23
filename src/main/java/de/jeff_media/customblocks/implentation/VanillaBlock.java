package de.jeff_media.customblocks.implentation;

import com.google.common.base.Enums;
import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.PlacedCustomBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.Collections;
import java.util.Locale;

public class VanillaBlock extends CustomBlock {

    @Getter
    @Setter
    private final BlockData blockData;

    public VanillaBlock(Material mat) {
        super(mat.name());
        this.blockData = Bukkit.createBlockData(mat);
    }

    public VanillaBlock(String id) throws InvalidBlockDataException {
        super(id.replace("minecraft:",""));

        // Vanilla Material
        if (!id.contains(":") && !id.contains("[")) {
            final Material material = Enums.getIfPresent(Material.class, id.toUpperCase(Locale.ROOT)).orNull();
            if (material == null) throw new InvalidBlockDataException("Could not find material with ID " + id);
            this.blockData = Bukkit.createBlockData(material);
            return;
        }

        // Vanilla BlockData
        try {
            this.blockData = Bukkit.createBlockData(id.startsWith("minecraft:") ? id : "minecraft:" + id);
        } catch (IllegalArgumentException exception) {
            throw new InvalidBlockDataException("Could not parse blockdata: " + id);
        }

    }

    @Override
    public String getNamespace() {
        return "minecraft";
    }

    @Override
    public Material getMaterial() {
        return blockData.getMaterial();
    }


    @Override
    public PlacedCustomBlock place(Block block) {
        return place(block, null);
    }

    @Override
    public PlacedCustomBlock place(Block block, OfflinePlayer player) {
        block.setBlockData(blockData);
        return new PlacedCustomBlock(null, Collections.singletonList(block.getLocation()));
    }
}

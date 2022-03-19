package de.jeff_media.customblocks;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomBlockUtils {

    public static Collection<ArmorStand> getArmorStands(Block block) {
        return block.getWorld().getNearbyEntities(BoundingBox.of(block))
                .stream()
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .collect(Collectors.toList());
    }
}

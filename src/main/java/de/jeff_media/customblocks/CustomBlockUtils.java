package de.jeff_media.customblocks;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CustomBlockUtils {

    @Getter
    @Setter
    @NonNull
    private static Logger logger = Bukkit.getLogger();

    public static void debug(String message) {

        Plugin angelchest = Bukkit.getPluginManager().getPlugin("AngelChest");
        if(angelchest == null) return;


    }

    public static Collection<ArmorStand> getArmorStands(Block block) {
        return block.getWorld().getNearbyEntities(BoundingBox.of(block))
                .stream()
                .filter(entity -> entity instanceof ArmorStand)
                .map(entity -> (ArmorStand) entity)
                .collect(Collectors.toList());
    }
}

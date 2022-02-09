package de.jeff_media.customblocks;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PlacedCustomBlock implements ConfigurationSerializable {

    @Getter @NonNull private final List<UUID> placedEntities;
    @Getter @NonNull private final List<Location> placedBlocks;

    public PlacedCustomBlock(@Nullable List<UUID> placedEntities, @Nullable List<Location> placedBlocks) {
        this.placedEntities = placedEntities != null ? placedEntities : new ArrayList<>();
        this.placedBlocks = placedBlocks != null ? placedBlocks : new ArrayList<>();
    }

    public PlacedCustomBlock(Block block) {
        this.placedBlocks = Collections.singletonList(block.getLocation());
        this.placedEntities = new ArrayList<>();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new LinkedHashMap<>();
        List<String> entities = this.placedEntities.stream().map(UUID::toString).collect(Collectors.toList());
        map.put("entities",entities);
        map.put("blocks", this.placedBlocks);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static PlacedCustomBlock deserialize(Map<String,Object> map) {
        List<UUID> placedEntities = ((List<String>)map.get("entities")).stream().map(UUID::fromString).collect(Collectors.toList());
        List<Location> placedBlocks = (List<Location>) map.get("blocks");
        return new PlacedCustomBlock(placedEntities, placedBlocks);
    }

    @Override
    public String toString() {
        return "PlacedCustomBlock{" +
                "placedEntities=" + placedEntities +
                ", placedBlocks=" + placedBlocks +
                '}';
    }

    public void remove() {
        //System.out.println("Removing " + this);
        placedEntities.forEach(uuid -> {
            Entity entity = Bukkit.getEntity(uuid);
            if(entity != null) {
                //System.out.println("Removing Entity: " + Bukkit.getEntity(uuid));
                if(entity instanceof ItemFrame) {
                    ItemFrame itemFrame = (ItemFrame) entity;
                    itemFrame.setItem(null, false);
                }
                entity.remove();
                if(entity.isValid()) {
                    //System.out.println("WARNING: ENTITY IS STILL ALIVE!");
                }
            }
        });
        placedBlocks.forEach(location -> location.getBlock().setType(Material.AIR));
    }
}

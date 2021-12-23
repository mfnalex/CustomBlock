package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.PlacedCustomBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.*;

public class ItemsAdderBlock extends CustomBlock {

    private final CustomStack customStack;
    private final ItemType type;
    private UUID entityUUID;

    private enum ItemType {
        BLOCK, FURNITURE
    }

    public ItemsAdderBlock(String id) throws InvalidBlockDataException {
        super(id);
        customStack = CustomStack.getInstance(id);
        if(customStack == null) {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        }

        if(customStack instanceof dev.lone.itemsadder.api.CustomBlock) {
            type = ItemType.BLOCK;
        } else if(customStack instanceof CustomFurniture) {
            type = ItemType.FURNITURE;
        } else {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        }
    }

    @Override
    public PlacedCustomBlock place(Block block) {
        return place(block, null);
    }

    @Override
    public PlacedCustomBlock place(Block block, OfflinePlayer player) {
        switch (type) {
            case BLOCK:
                ((dev.lone.itemsadder.api.CustomBlock) customStack).place(block.getLocation());
                return new PlacedCustomBlock(null, Collections.singletonList(block.getLocation()));
            case FURNITURE:
                Entity armorStand = ((CustomFurniture) customStack).getArmorstand();
                List<UUID> placedEntities = new ArrayList<>();
                if(armorStand != null) {
                    entityUUID = armorStand.getUniqueId();
                    ((CustomFurniture)customStack).teleport(block.getLocation().add(0.5,0.0,5));
                    placedEntities.add(entityUUID);
                }
                return new PlacedCustomBlock(placedEntities, Collections.singletonList(block.getLocation()));
            default:
                throw new IllegalStateException();
        }
    }

    /*@Override
    public void remove(Block block) {
        if(type == ItemType.FURNITURE && entityUUID != null) {
            Entity armorStand = Bukkit.getEntity(entityUUID);
            if(armorStand != null) {
                armorStand.remove();
            }
        }
        super.remove(block);
    }*/

    @Override
    public String getNamespace() {
        return "itemsadder";
    }

    @Override
    public Material getMaterial() {
        switch(type) {
            case BLOCK: return ((dev.lone.itemsadder.api.CustomBlock)customStack).getBaseBlockData().getMaterial();
            case FURNITURE: return Material.BARRIER;
            default: throw new IllegalStateException();
        }
    }

}

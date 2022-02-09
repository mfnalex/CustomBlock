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
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemsAdderBlock extends CustomBlock {

    private dev.lone.itemsadder.api.CustomBlock customBlock = null;
    private CustomStack customFurniture = null;
    private final ItemType type;
    private UUID entityUUID;

    private enum ItemType {
        BLOCK, FURNITURE
    }

    public ItemsAdderBlock(String id) throws InvalidBlockDataException {
        super(id);
        CustomStack stack = CustomStack.getInstance(id);
        if(stack == null) {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        }

        if(stack.isBlock()) {
            customBlock = dev.lone.itemsadder.api.CustomBlock.getInstance(id);
            type = ItemType.BLOCK;
        } else if (CustomFurniture.getInstance(id) != null) {
            customFurniture = CustomFurniture.getInstance(id);
            type = ItemType.FURNITURE;
        } else {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        }
    }

    /*public static ItemsAdderBlock fromItemStack(ItemStack item) throws InvalidBlockDataException {
        CustomStack stack = CustomStack.byItemStack(item);
        System.out.println(stack.getNamespacedID());
        return new ItemsAdderBlock(CustomStack.byItemStack(item).getNamespace());
    }*/

    @Override
    public PlacedCustomBlock place(Block block) {
        return place(block, null);
    }

    @Override
    public PlacedCustomBlock place(Block block, OfflinePlayer player) {
        switch (type) {
            case BLOCK:
                customBlock.place(block.getLocation());
                return new PlacedCustomBlock(null, Collections.singletonList(block.getLocation()));
            case FURNITURE:
                CustomFurniture placedFurniture = CustomFurniture.spawn(customFurniture.getNamespacedID(), block);
                Entity armorStand = placedFurniture.getArmorstand();
                List<UUID> placedEntities = new ArrayList<>();
                /*if(armorStand != null) {
                    entityUUID = armorStand.getUniqueId();
                    ((CustomFurniture)customStack).teleport(block.getLocation().add(0.5,0.0,5));*/
                    placedEntities.add(entityUUID);
                //}
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
            case BLOCK: return customBlock.getBaseBlockData().getMaterial();
            case FURNITURE: return Material.BARRIER;
            default: throw new IllegalStateException();
        }
    }

}

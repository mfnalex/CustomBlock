package de.jeff_media.customblocks.implentation;

import com.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import de.jeff_media.customblocks.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;

import java.util.Objects;

public class NexoBlock extends CustomBlock {

    private FurnitureMechanic furnitureMechanic = null;
    private ItemType type;

    public NexoBlock(String id) throws InvalidBlockDataException {
        super(id);

        // Blocks
        if (NexoBlocks.noteBlockMechanic(id) != null) {
            type = ItemType.NOTE_BLOCK;
            return;
        }

        // Furniture
        FurnitureMechanic mechanicFactory = NexoFurniture.furnitureMechanic(id);
        if (mechanicFactory != null) {
            type = ItemType.FURNITURE;
            furnitureMechanic = mechanicFactory;
            return;
        }


        throw new InvalidBlockDataException("Could not find Nexo block: " + id);
    }

    private static float getYaw() {
        return Float.parseFloat(System.getProperty("customblocks.nexooraxen.yaw", "0"));
    }

    private static BlockFace getBlockFace() {
        return BlockFace.valueOf(System.getProperty("customblocks.nexooraxen.blockface", "DOWN"));
    }


    @Override
    public void place(Block block, OfflinePlayer player) {
        super.place(block, player);
        switch (type) {
            case FURNITURE:
                block.setType(Material.AIR);
                Entity placed = furnitureMechanic.place(block.getLocation(), getYaw(), getBlockFace());
                if (placed != null) {
                    entities.add(placed.getUniqueId());
                }
                break;
            case NOTE_BLOCK:
                NoteBlockMechanicFactory.Companion.setBlockModel(block, getId());
                break;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespace() {
        return "nexo";
    }

    @Override
    public void remove() {
        if (furnitureMechanic != null) {

            entities.stream().map(Bukkit::getEntity).filter(Objects::nonNull).forEach(entity -> {
                try {
                    furnitureMechanic.removeBaseEntity((ItemDisplay) entity);
                } catch (Exception ignored) {
                }
            });

        }
        super.remove();
    }

    @Override
    public Material getMaterial() {
        switch (type) {
            case FURNITURE:
                return Material.BARRIER;
            case NOTE_BLOCK:
                return Material.NOTE_BLOCK;
            default:
                throw new IllegalStateException();
        }
    }

    private enum ItemType {
        NOTE_BLOCK, FURNITURE
    }
}
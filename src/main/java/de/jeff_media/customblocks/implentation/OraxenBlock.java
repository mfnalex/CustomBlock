package de.jeff_media.customblocks.implentation;

import com.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import de.jeff_media.customblocks.CustomBlock;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

public class OraxenBlock extends CustomBlock {

    private FurnitureMechanic furnitureMechanic = null;
    private ItemType type;

    public OraxenBlock(String id) throws InvalidBlockDataException {
        super(id);

        // Blocks
        if (NoteBlockMechanicFactory.getInstance().getMechanic(id) != null) {
            type = ItemType.NOTE_BLOCK;
            return;
        }

        // Furniture
        MechanicFactory mechanicFactory = MechanicsManager.getMechanicFactory("furniture");
        Mechanic mechanic = mechanicFactory.getMechanic(id);
        if (mechanic != null) {
            type = ItemType.FURNITURE;
            furnitureMechanic = (FurnitureMechanic) mechanic;
            return;
        }


        throw new InvalidBlockDataException("Could not find Oraxen block: " + id);
    }

    private static float getYaw() {
        return Float.parseFloat(System.getProperty("customblocks.oraxen.yaw", "0"));
    }

    private static BlockFace getBlockFace() {
        return BlockFace.valueOf(System.getProperty("customblocks.oraxen.blockface", "DOWN"));
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

                    // Interaction Entity
                    try {
                        entities.add(furnitureMechanic.getInteractionEntity(placed).getUniqueId());
                    } catch (Throwable ignored) { }

                }
                break;
            case NOTE_BLOCK:
                NoteBlockMechanicFactory.setBlockModel(block, getId());
                break;

            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespace() {
        return "oraxen";
    }

    @Override
    public void remove() {
        if (furnitureMechanic != null) {

            entities.stream().map(Bukkit::getEntity).forEach(entity -> {
                try {
                    furnitureMechanic.removeSolid(entity, block.getLocation(), 0);
                } catch (Exception ignored) {
                }
                try {
                    furnitureMechanic.removeNonSolidFurniture(entity);
                } catch (Exception ignored) {
                }
            });

            try {
                furnitureMechanic.getBarriers().forEach(blockLocation -> {
                    Block barrier = block.getRelative(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
                    if (barrier.getType() == Material.BARRIER) {
                        barrier.setType(Material.AIR);
                    }
                });
            } catch (Exception ignored) {
            }
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

/*
package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import org.bukkit.block.Block;

import java.lang.reflect.Method;

public class OraxenBlock extends CustomBlock {

    private static boolean disabled = false;
    private static final Class<?> noteBlockMechanicFactoryClass;
    private static final Object noteBlockMechanicFactoryInstance;
    private static final Method getMechanicMethod;
    private static final Method setBlockModelMethod;

    static {
        noteBlockMechanicFactoryClass = Class.forName("io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory");
        noteBlockMechanicFactoryInstance = noteBlockMechanicFactoryClass.getMethod("getInstance").invoke(null);
        getMechanicMethod
    }

    public OraxenBlock(String id) throws InvalidBlockDataException {
        super(id);
        if(NoteBlockMechanicFactory.getInstance().getMechanic(id) == null) throw new InvalidBlockDataException("Could not find Oraxen block: " + id);
    }

    @Override
    public void place(Block block) {
        NoteBlockMechanicFactory.setBlockModel(block, getId());
    }

    @Override
    public String getNamespace() {
        return null;
    }
}

*/
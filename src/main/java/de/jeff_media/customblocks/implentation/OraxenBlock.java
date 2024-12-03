package de.jeff_media.customblocks.implentation;

import com.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.CustomBlockUtils;
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
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;

import static de.jeff_media.customblocks.CustomBlockUtils.debug;
import static de.jeff_media.customblocks.CustomBlockUtils.isDebug;

public class OraxenBlock extends CustomBlock {

    private FurnitureMechanic furnitureMechanic = null;
    private ItemType type;

    public OraxenBlock(String id) throws InvalidBlockDataException {
        super(id);

        init(id);
    }

    private void init(String id) throws InvalidBlockDataException {
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
        debug("Placing Oraxen block: " + getId() + " for " + player + " at " + block);
        switch (type) {
            case FURNITURE:
                block.setType(Material.AIR);
                debug("Placing furniture at " + block.getLocation());
                Entity placed;
                try {
                    placed = furnitureMechanic.place(block.getLocation(), new ItemStack(Material.AIR), getYaw(), getBlockFace(), false);
                } catch (Exception e) {
                    if(isDebug()) {
                        CustomBlockUtils.getLogger().log(Level.WARNING, "Couldn't use FurnitureMechanic#place with 5 arguments, trying with 3 arguments instead", e);
                    }
                    placed = furnitureMechanic.place(block.getLocation(), getYaw(), getBlockFace());
                    debug("Placed furniture: " + placed + " at " + block.getLocation() + " with yaw " + getYaw() + " and blockface " + getBlockFace());
                }
                if (placed != null) {
                    entities.add(placed.getUniqueId());

                    // Interaction Entity
                    try {
                        entities.add(furnitureMechanic.getInteractionEntity(placed).getUniqueId());
                    } catch (Throwable ignored) { }

                }
                break;
            case NOTE_BLOCK:
                debug("Placing note block at " + block.getLocation());
                NoteBlockMechanicFactory.setBlockModel(block, getId());
                break;

            default:
                throw new IllegalStateException();
        }
        debug("Placed Oraxen block: " + getId() + " for " + player + " at " + block);
    }

    @Override
    public String getNamespace() {
        return "oraxen";
    }

    @Override
    public void remove() {
        if (furnitureMechanic != null) {
            try {
                furnitureMechanic.removeNonSolidFurniture(furnitureMechanic.getBaseEntity(block));
            } catch (Exception ignored) {
            }
            try {
                furnitureMechanic.removeSolid(furnitureMechanic.getBaseEntity(block), block.getLocation(), getYaw());
            } catch (Exception ignored) {
            }

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

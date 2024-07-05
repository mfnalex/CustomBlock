package de.jeff_media.customblocks.implentation;

import com.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.CustomBlockUtils;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;



public class ItemsAdderBlock extends CustomBlock {

    private enum ItemType {
        BLOCK, FURNITURE
    }

    private ItemType type;
    //private Block block;
    private String namespacedId;
    private Material material;
    private dev.lone.itemsadder.api.CustomBlock placedBlock;
    private CustomFurniture placedFurniture;

    public ItemsAdderBlock(String id) throws InvalidBlockDataException {
        super(id);
        namespacedId = id;
        CustomStack stack = CustomStack.getInstance(id);
        if (stack == null) {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        } else if (stack.isBlock()) {
            type = ItemType.BLOCK;
        } else {
            type = ItemType.FURNITURE;
        }
    }

    @Override
    public void place(Block block, OfflinePlayer player) {
        super.place(block, player);
        switch (type) {
            case BLOCK:
                placedBlock = dev.lone.itemsadder.api.CustomBlock.getInstance(namespacedId).place(block.getLocation());
                break;
            case FURNITURE:
                placedFurniture = CustomFurniture.spawn(namespacedId, block);
                entities.add(placedFurniture.getArmorstand().getUniqueId());
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void remove(boolean unsetBlock) {
        if(Thread.currentThread().getStackTrace().length <= 40) { // Prevent Stack overflow when calling from parent class
            remove();
        }
    }

    @Override
    public void remove() {
        if (block != null) {
            if (placedBlock != null) {
                try {
                    dev.lone.itemsadder.api.CustomBlock.remove(block.getLocation());
                } catch (Exception ignored) {
                }
                placedBlock = null;
            }

            try {
                dev.lone.itemsadder.api.CustomBlock.remove(block.getLocation());
            } catch (Exception ignored) {
            }

            for (ArmorStand armorStand : CustomBlockUtils.getArmorStands(block)) {
                try {
                    CustomFurniture.remove(armorStand, false);
                } catch (Exception ignored) {
                }
            }

        } else {
            Bukkit.getLogger().info("CustomBlock is null");
        }

        super.remove();
    }


    @Override
    public String getNamespace() {
        return "itemsadder";
    }

    @Override
    public Material getMaterial() {
        switch (type) {
            case BLOCK: {
                if (material != null) return material;
                return material = dev.lone.itemsadder.api.CustomBlock.getInstance(namespacedId).getBaseBlockData().getMaterial();
            }
            case FURNITURE:
                return null;
            default:
                throw new IllegalStateException();
        }
    }

}

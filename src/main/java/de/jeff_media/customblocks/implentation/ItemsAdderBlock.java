package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.CustomBlockUtils;
import com.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

public class ItemsAdderBlock extends CustomBlock {

    private ItemType type;
    private Block block;
    private String namespacedId;
    private Material material;
    private dev.lone.itemsadder.api.CustomBlock placedBlock;
    private CustomFurniture placedFurniture;

    private enum ItemType {
        BLOCK, FURNITURE
    }

    public ItemsAdderBlock(String id) throws InvalidBlockDataException {
        super(id);
        namespacedId = id;
        CustomStack stack = CustomStack.getInstance(id);
        if(stack == null) {
            throw new InvalidBlockDataException("Could not find ItemsAdder block or furniture: " + id);
        } else if(stack.isBlock()) {
            type = ItemType.BLOCK;
        } else {
            type = ItemType.FURNITURE;
        }
    }

    @Override
    public void place(Block block) {
        place(block, null);
    }

    @Override
    public void place(Block block, OfflinePlayer player) {
        this.block = block;
        System.out.println("place");
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
    public void remove() {
        System.out.println("remove");
        /*if(placedBlock != null) {
            placedBlock.remove();
        }
        if(placedFurniture != null) {
            placedFurniture.remove(false);
        }*/
        if(block != null) {
            dev.lone.itemsadder.api.CustomBlock.remove(block.getLocation());
            for(ArmorStand armorStand : CustomBlockUtils.getArmorStands(block)) {
                System.out.println(armorStand);
                CustomFurniture.remove(armorStand, false);
            }
        }
        super.remove();
    }


    @Override
    public String getNamespace() {
        return "itemsadder";
    }

    @Override
    public Material getMaterial() {
        switch(type) {
            case BLOCK: {
                if (material != null) return material;
                return material = dev.lone.itemsadder.api.CustomBlock.getInstance(namespacedId).getBaseBlockData().getMaterial();
            }
            case FURNITURE: return null;
            default: throw new IllegalStateException();
        }
    }

}

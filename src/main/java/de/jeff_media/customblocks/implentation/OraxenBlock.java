package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.customblocks.PlacedCustomBlock;
import de.jeff_media.jefflib.EntityUtils;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import io.th0rgal.oraxen.shaded.customblockdata.CustomBlockData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic.*;

public class OraxenBlock extends CustomBlock {

    private FurnitureMechanic furnitureMechanic = null;
    private UUID entityUUID;
    private ItemType type;

    private enum ItemType {
        NOTE_BLOCK, FURNITURE
    }

    public OraxenBlock(String id) throws InvalidBlockDataException {
        super(id);

        // Blocks
        if(NoteBlockMechanicFactory.getInstance().getMechanic(id) != null) {
            type = ItemType.NOTE_BLOCK;
            return;
        }

        // Furniture
        MechanicFactory mechanicFactory = MechanicsManager.getMechanicFactory("furniture");
        Mechanic mechanic = mechanicFactory.getMechanic(id);
        if(mechanic != null) {
            type = ItemType.FURNITURE;
            furnitureMechanic = (FurnitureMechanic) mechanic;
            return;
        }


        throw new InvalidBlockDataException("Could not find Oraxen block: " + id);
    }

    @Override
    public PlacedCustomBlock place(Block block) {
        return place(block, null);
    }

    @Override
    public PlacedCustomBlock place(Block block, OfflinePlayer player) {
        switch(type) {
            case NOTE_BLOCK:
                NoteBlockMechanicFactory.setBlockModel(block, getId());
                return new PlacedCustomBlock(null, Collections.singletonList(block.getLocation()));
            case FURNITURE:
                furnitureMechanic.place(Rotation.NONE, 0, BlockFace.SELF, block.getLocation(), getId());
                ItemFrame itemFrame = FurnitureMechanic.getItemFrame(block.getLocation());
                List<UUID> entityUUIDs = new ArrayList<>();
                if(itemFrame != null) {
                    entityUUID = itemFrame.getUniqueId();
                    entityUUIDs.add(entityUUID);
                }
                return new PlacedCustomBlock(entityUUIDs,Collections.singletonList(block.getLocation()));
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespace() {
        return null;
    }

    /*@Override
    public void remove(Block block) {
        ItemFrame itemFrame = FurnitureMechanic.getItemFrame(block.getLocation());
        if(itemFrame != null) {
            itemFrame.remove();
        }
        CustomBlockData cbd = new CustomBlockData(block,OraxenPlugin.get());
        cbd.clear();
        super.remove(block);
    }*/

    @Override
    public Material getMaterial() {
        switch (type) {
            case FURNITURE: return Material.BARRIER;
            case NOTE_BLOCK: return Material.NOTE_BLOCK;
            default: throw new IllegalStateException();
        }
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
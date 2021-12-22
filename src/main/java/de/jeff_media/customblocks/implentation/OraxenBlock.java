package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import de.jeff_media.jefflib.exceptions.InvalidBlockDataException;
import io.th0rgal.oraxen.mechanics.provided.gameplay.noteblock.NoteBlockMechanicFactory;
import org.bukkit.block.Block;

public class OraxenBlock extends CustomBlock {

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

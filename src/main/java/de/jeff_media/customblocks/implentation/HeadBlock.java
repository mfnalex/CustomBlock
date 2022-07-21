package de.jeff_media.customblocks.implentation;

import de.jeff_media.customblocks.CustomBlock;
import com.jeff_media.jefflib.SkullUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class HeadBlock extends CustomBlock {

    public HeadBlock(String id) {
        super(id);
    }

    @Override
    public void place(Block block) {
        super.place(block);
        place(block,null);
    }

    public void place(Block block, OfflinePlayer player) {
        super.place(block,player);
        block.setType(Material.AIR);
        block.setType(Material.PLAYER_HEAD);
        //System.out.println("Placing HeadBlock");

        // Set dynamic player
        if(getId().equalsIgnoreCase("player")) {
            //System.out.println("Using dynamic player");
            if(player == null) {
                throw new IllegalArgumentException("Using head:player requires an OfflinePlayer");
            }
            setOfflinePlayer(block, player);
        }

        // Set static player from name
        else if(isValidAccountName(getId())) {
            //System.out.println("Using static player name");
            setOfflinePlayer(block, getOfflinePlayerByName(getId()));
        }

        // Set static player from UUID
        else if(isValidUUID(getId())) {
            //System.out.println("Using static player UUID");
            setOfflinePlayer(block, getOfflinePlayerByUUID(getId()));
        }

        // Base64 Texture
        else {
            //System.out.println("Using Base64: " + getId());
            setBase64Texture(block);
        }
    }

    private void setBase64Texture(Block block) {
        BlockState state = block.getState();
        if(state instanceof Skull) {
            SkullUtils.setBase64Texture(block, getId());
        }
    }

    private static void setOfflinePlayer(Block block, OfflinePlayer player) {
        BlockState state = block.getState();
        if(state instanceof Skull) {
            final Skull skull = (Skull) state;
            skull.setOwningPlayer(player);
            skull.update();
        }
    }

    private static boolean isValidAccountName(String name) {
        return name.matches("^\\w{3,16}$");
    }

    private static OfflinePlayer getOfflinePlayerByName(String name) {
        final Player player = Bukkit.getPlayerExact(name);
        if(player != null) return player;
        return Bukkit.getOfflinePlayer(name);
    }

    private OfflinePlayer getOfflinePlayerByUUID(String string) {
        if(string.length()==36) return Bukkit.getOfflinePlayer(UUID.fromString(string));
        if(string.length()==32) return Bukkit.getOfflinePlayer(fromStringWithoutDashes(string));
        throw new IllegalArgumentException("Not a valid UUID: " + getId());
    }

    private static UUID fromStringWithoutDashes(String string) {
        return UUID.fromString(string
                .replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
    }

    private static boolean isValidUUID(String string) {
        return string.replace("-","").matches("^\\p{XDigit}{32}$");
    }


    @Override
    public String getNamespace() {
        return "head";
    }

    @Override
    public Material getMaterial() {
        return Material.PLAYER_HEAD;
    }
}

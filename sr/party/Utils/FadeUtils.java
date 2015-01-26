/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sr.party.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Material;

/**
 *
 * @author Cross
 */
public class FadeUtils 
{
    static HashSet<Byte> losTransparentBlocks = new HashSet<Byte>(new ArrayList<Byte>());
    
    public static HashSet<Byte> getTransparentBlocks() 
    {
        losTransparentBlocks.add((byte)Material.AIR.getId());
        losTransparentBlocks.add((byte)Material.TORCH.getId());
        losTransparentBlocks.add((byte)Material.REDSTONE_WIRE.getId());
        losTransparentBlocks.add((byte)Material.REDSTONE_TORCH_ON.getId());
        losTransparentBlocks.add((byte)Material.REDSTONE_TORCH_OFF.getId());
        losTransparentBlocks.add((byte)Material.YELLOW_FLOWER.getId());
        losTransparentBlocks.add((byte)Material.RED_ROSE.getId());
        losTransparentBlocks.add((byte)Material.BROWN_MUSHROOM.getId());
        losTransparentBlocks.add((byte)Material.RED_MUSHROOM.getId());
        losTransparentBlocks.add((byte)Material.LONG_GRASS.getId());
        losTransparentBlocks.add((byte)Material.DEAD_BUSH.getId());
        losTransparentBlocks.add((byte)Material.DIODE_BLOCK_ON.getId());
        losTransparentBlocks.add((byte)Material.DIODE_BLOCK_OFF.getId());
        
        return losTransparentBlocks;
    }
}

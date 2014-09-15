package com.github.isatwospirit.kittyslilhelpers.command.createmaze.builders;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.github.isatwospirit.kittyslilhelpers.command.createmaze.Labyrinth;
import com.github.isatwospirit.kittyslilhelpers.command.createmaze.LabyrinthBuilder;

public class StoneWallLabyrinthBuilder implements LabyrinthBuilder {

	@SuppressWarnings("deprecation")
	@Override
	public boolean build(Location source, Labyrinth labyrinth) {
		
		World w=source.getWorld();
		int xSource = source.getBlockX();
		int zSource = source.getBlockZ();
		int ySource = source.getBlockY();
		
		for(int x=0;x<labyrinth.getSizeWE()*5;x++){
			for(int z=0;z<labyrinth.getSizeNS()*5;z++){
				Material floor;
    			if(Math.random()*10 < 7)
    				floor=Material.COBBLESTONE;
    			else
    				floor=Material.MOSSY_COBBLESTONE;
    			w.getBlockAt(xSource + x, ySource, zSource + z).setType(floor);
    			for(int y=1; y<4;y++){
    				w.getBlockAt(xSource + x, ySource + y, zSource + z).setType(Material.AIR);
    			}
			}
		}
		
		for(int x=0;x<labyrinth.getSizeWE();x++){
			for(int z=0;z<labyrinth.getSizeNS();z++){
				//"Pillars"
				w.getBlockAt(xSource + x*5, ySource + 1, zSource + z*5).setType(Material.STONE);
				w.getBlockAt(xSource + x*5, ySource + 1, zSource + z*5+4).setType(Material.STONE);
				w.getBlockAt(xSource + x*5+4, ySource + 1, zSource + z*5).setType(Material.STONE);
				w.getBlockAt(xSource + x*5+4, ySource + 1, zSource + z*5+4).setType(Material.STONE);
				w.getBlockAt(xSource + x*5, ySource + 2, zSource + z*5).setType(Material.SMOOTH_BRICK);
				w.getBlockAt(xSource + x*5, ySource + 2, zSource + z*5+4).setType(Material.SMOOTH_BRICK);
				w.getBlockAt(xSource + x*5+4, ySource + 2, zSource + z*5).setType(Material.SMOOTH_BRICK);
				w.getBlockAt(xSource + x*5+4, ySource + 2, zSource + z*5+4).setType(Material.SMOOTH_BRICK);
				w.getBlockAt(xSource + x*5, ySource + 2, zSource + z*5).setData((byte) 3);
				w.getBlockAt(xSource + x*5, ySource + 2, zSource + z*5+4).setData((byte) 3);
				w.getBlockAt(xSource + x*5+4, ySource + 2, zSource + z*5).setData((byte) 3);
				w.getBlockAt(xSource + x*5+4, ySource + 2, zSource + z*5+4).setData((byte) 3);
				
				if(labyrinth.getCellAt(x, z).getNorthOpen()==false){
					w.getBlockAt(xSource + x*5 + 1, ySource + 1, zSource + z*5).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 2, ySource + 1, zSource + z*5).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 3, ySource + 1, zSource + z*5).setType(Material.SMOOTH_BRICK);					
				}
				if(labyrinth.getCellAt(x, z).getSouthOpen()==false){
					w.getBlockAt(xSource + x*5 + 1, ySource + 1, zSource + z*5 + 4).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 2, ySource + 1, zSource + z*5 + 4).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 3, ySource + 1, zSource + z*5 + 4).setType(Material.SMOOTH_BRICK);					
				}
				if(labyrinth.getCellAt(x, z).getWestOpen()==false){
					w.getBlockAt(xSource + x*5, ySource + 1, zSource + z*5 + 1).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5, ySource + 1, zSource + z*5 + 2).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5, ySource + 1, zSource + z*5 + 3).setType(Material.SMOOTH_BRICK);					
				}
				if(labyrinth.getCellAt(x, z).getEastOpen()==false){
					w.getBlockAt(xSource + x*5 + 4, ySource + 1, zSource + z*5 + 1).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 4, ySource + 1, zSource + z*5 + 2).setType(Material.SMOOTH_BRICK);
					w.getBlockAt(xSource + x*5 + 4, ySource + 1, zSource + z*5 + 3).setType(Material.SMOOTH_BRICK);					
				}
			}
		}
		return true;
	}

}

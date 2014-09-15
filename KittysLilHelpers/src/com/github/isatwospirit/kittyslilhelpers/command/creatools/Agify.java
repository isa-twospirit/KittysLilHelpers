package com.github.isatwospirit.kittyslilhelpers.command.creatools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class Agify implements CreaTool {

	private Player owner;
	private Integer percent;
	private Block block1 = null;
	private Block block2 = null;
	private Boolean isRunning = false;
	private BlockEvent blockEvent = null;
	
	public Agify(Player owner, Integer percent){
		this.owner = owner;
		this.percent = percent;
		if(this.percent<0)
			this.percent=0;
		else if(this.percent>100)
			this.percent=100;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		this.isRunning=true;
		World w = this.block1.getLocation().getWorld();
		Integer x1 = this.block1.getLocation().getBlockX();
		Integer y1 = this.block1.getLocation().getBlockY();
		Integer z1 = this.block1.getLocation().getBlockZ();
		Integer x2 = this.block2.getLocation().getBlockX();
		Integer y2 = this.block2.getLocation().getBlockY();
		Integer z2 = this.block2.getLocation().getBlockZ();
		Integer swap = 0;
		
		if(x1 > x2){
			swap = x1;
			x1 = x2;
			x2 = swap;
		}
		if(y1 > y2){
			swap = y1;
			y1 = y2;
			y2 = swap;
		}
		if(z1 > z2){
			swap = z1;
			z1 = z2;
			z2 = swap;
		}
		
		for(Integer x = x1; x <= x2; x++){
			for(Integer y = y1; y <= y2; y++){
				for(Integer z = z1; z <= z2; z++){
					Integer rnd = (int)(Math.random() * 100);
					Block current = w.getBlockAt(new Location(w, x, y, z));
					if(current.getType() == Material.COBBLESTONE || current.getType() == Material.MOSSY_COBBLESTONE){
						if(rnd<this.percent)
							current.setType(Material.MOSSY_COBBLESTONE);
						else
							current.setType(Material.COBBLESTONE);
					}else if(current.getType() == Material.COBBLE_WALL){
						if(rnd<this.percent)
							current.setData((byte) 1);
						else
							current.setData((byte) 0);
					}else if(current.getType() == Material.SMOOTH_BRICK){
						if(rnd<this.percent){
							byte rnd2 = (byte)((Math.random() * 2) + 1);
							current.setData(rnd2);
						}else
							current.setData((byte)0);
					}
				}
			}
		}
		this.isRunning=false;
	}

	@Override
	public Player getOwner() {
		return this.owner;
	}

	@Override
	public String getName() {
		return "Agify";
	}

	@Override
	public boolean isActive() {
		if(this.block1==null || this.block2==null)
			return true;
		else
			return false;
	}

	@Override
	public boolean isRunning() {
		return this.isRunning;
	}

	@Override
	public boolean canRun() {
		if(this.block1==null || this.block2==null)
			return true;
		return false;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public boolean undo() {
		return false;
	}

	@Override
	public Block getStartBlock() {
		return this.block1;
	}

	@Override
	public BlockEvent getBlockEvent() {
		return this.blockEvent;
	}

	@Override
	public boolean prepareRun(Block startBlock, BlockEvent event) {
		if(event == BlockEvent.INTERACT){
			if(this.block1==null){
				this.block1 = startBlock;
				this.getOwner().sendMessage("Block 1 set: " + Utils.getLocationText(this.block1.getLocation()));
				return false;
			}else if(this.block2==null){
				this.block2 = startBlock;
				this.getOwner().sendMessage("Block 2 set: " + Utils.getLocationText(this.block1.getLocation()));
				return true;
			}
		}
		return false;
	}
}

package com.github.isatwospirit.kittyslilhelpers.command.creatools;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;

public class FloodFill implements CreaTool{
	public enum Plane implements ItemContainer{
		X,
		Y,
		Z,
		UP,
		DOWN,
		THREE_D;

		@Override
		public String getItemDisplayName() {
			return "Plane";
		}

		@Override
		public Class<?> getItemType() {
			return this.getClass();
		}

		@Override
		public Object getItem(String key) {
			if(key.compareToIgnoreCase("3d")==0)
				return Plane.THREE_D;
			else
				return valueOf(key.toUpperCase());
		}

		@Override
		public OfflinePlayer getItemOwner(String key) {
			return null;
		}
	}
	
	private Player owner = null;
	private Block startBlock = null;
	private BlockEvent blockEvent = null;
	private int maxRange = 200;
	private int maxDepth = 500;
	private Plane plane = Plane.Y;
	private boolean isRunning = false;
	private Set<Location> changedBlocks = null;
	private Set<Location> restartPoints = null;
	
	private Material oldMaterial = null;
	private Byte oldData = 0;
	private Material newMaterial = null;
	private Byte newData = 0;
	
	public FloodFill(Player owner, Integer maxRange, Plane plane){
		try{
			this.owner = owner;
			if(maxRange==null)
				this.maxRange = 200;
			else
				this.maxRange = maxRange;
			
			if(this.plane==null)
				this.plane = Plane.Y;
			else
				this.plane = plane;
		}catch(Exception e){
			
		}
	}
	
	@Override
	public String getName(){
		return "FloodFill";
	}
	
	@Override
	public boolean canUndo(){
		return (this.getChangedBlocks()!=null);
	}
	
	public boolean canRun(){
		if(this.isRunning())
			return false;
		return(this.getChangedBlocks()==null);
	}
	
	@Override
	public boolean isActive(){
		return (this.getChangedBlocks()==null);
	}
	
	@Override
	public Player getOwner(){
		return this.owner;
	}
	
	public Block getStartBlock(){
		return this.startBlock;
	}

	public BlockEvent getBlockEvent(){
		return this.blockEvent;
	}
	
	public Integer getMaxRange(){
		return this.maxRange;
	}
	public boolean setMaxRange(Integer value){
		if(this.isRunning())
			return false;
		this.maxRange = value;
		return true;
	}
	
	public Material getOldMaterial(){
		return this.oldMaterial;
	}
	public byte getOldData(){
		return this.oldData;
	}
	public Material getNewMaterial(){
		return this.newMaterial;
	}
	public byte getNewData(){
		return this.newData;
	}
	
	public boolean isRunning(){
		return this.isRunning;
	}
	
	public Integer getMaxDepth(){
		return this.maxDepth;
	}
	
	public Set<Location> getChangedBlocks(){
		return this.changedBlocks;
	}
	
	public Set<Location> getRestartPoints(){
		return this.restartPoints;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean prepareRun(Block startBlock, BlockEvent event){
		if(event == BlockEvent.INTERACT){
			return false;
		}else if(this.isRunning()==true){
			return false;
		}else if(this.canUndo()==true){
			return false;
		}else{
			this.startBlock=startBlock;
			this.blockEvent=event;
			if(this.blockEvent == BlockEvent.PLACE){
				this.newMaterial = this.getStartBlock().getType();
				this.newData = this.getStartBlock().getData();
				this.oldMaterial = Material.AIR;
				this.oldData = 0;
			}else{
				this.oldMaterial = this.getStartBlock().getType();
				this.oldData = this.getStartBlock().getData();
				this.newMaterial = Material.AIR;
				this.newData = 0;
			}
			changedBlocks = new HashSet<Location>();
			restartPoints = new HashSet<Location>();
			return true;
		}
	}
	
	@Override
	public void run() {
		if(this.isRunning())
			return;
		
		this.isRunning = true;
		this.getOwner().sendMessage("Calculating FloodFill...");
		this.setMaterial(this.getStartBlock().getLocation(), this.getOldMaterial(), this.getOldData());
		this.doFloodFill(this.getStartBlock().getLocation(), 0, null);
		while(this.getRestartPoints().size()>0){
			while(this.getRestartPoints().iterator().hasNext()){
				Location newStart = this.getRestartPoints().iterator().next();
				this.getRestartPoints().remove(newStart);
				this.doFloodFill(newStart, 0, null);
			}
		}

		this.getOwner().sendMessage("Performing changes...");
		this.doChangeBlocks(this.getNewMaterial(), this.getNewData());
		this.getOwner().sendMessage("Done.");
		this.isRunning = false;
	}
	
	@Override
	public boolean undo(){
		if(this.canUndo()){
			this.getOwner().sendMessage("Reverting changes...");
			this.doChangeBlocks(this.getOldMaterial(), this.getOldData());
			this.getOwner().sendMessage("Done.");
			return true;
		}else{
			return false;
		}
	}
	
	private void doFloodFill(Location l, Integer depth, BlockFace from){
		if(this.getChangedBlocks().contains(l))
			return;
		
		if(this.getStartBlock().getLocation().distance(l)>=this.getMaxRange())
			return;
		
		depth+=1;
		if(depth>this.getMaxDepth()){
			this.getRestartPoints().add(l);
			return;
		}
		if(this.getRestartPoints().contains(l))
			this.getRestartPoints().remove(l);
			
		if(this.isMaterialMatch(l, this.getOldMaterial(), this.getOldData())){
			this.getChangedBlocks().add(l);
			//this.setMaterial(l, this.getNewMaterial(), this.getNewData());
			try{
				World w = l.getWorld();
				Integer x = l.getBlockX();
				Integer y = l.getBlockY();
				Integer z = l.getBlockZ();
				
				if(this.plane!=Plane.Z){
					if(from != BlockFace.SOUTH)
						doFloodFill(new Location(w, x, y, z+1), depth, BlockFace.NORTH);
					if(from != BlockFace.NORTH)
						doFloodFill(new Location(w, x, y, z-1), depth, BlockFace.SOUTH);
				}
				if(this.plane!=Plane.X){
					if(from != BlockFace.EAST)
						doFloodFill(new Location(w, x+1, y, z), depth, BlockFace.WEST);
					if(from != BlockFace.WEST)
						doFloodFill(new Location(w, x-1, y, z), depth, BlockFace.EAST);
				}
				
				if(this.plane!=Plane.Y){
					if(from != BlockFace.UP && y < 254)
						if((this.plane == Plane.DOWN && y<this.getStartBlock().getLocation().getBlockY()) || this.plane != Plane.DOWN)
							doFloodFill(new Location(w, x, y+1, z), depth, BlockFace.WEST);
					if(from != BlockFace.DOWN && y > 1){
						if((this.plane == Plane.UP && y>this.getStartBlock().getLocation().getBlockY()) || this.plane!= Plane.UP)
						doFloodFill(new Location(w, x-1, y-1, z), depth, BlockFace.EAST);
					}
				}
			}catch(Exception e){
				this.owner.sendMessage("Exception performing FloodFill: " + e.getMessage());
			}
		}
	}
	
	private void doChangeBlocks(Material type, Byte data){
		for(Location l : this.getChangedBlocks()){
			this.setMaterial(l, type, data);
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean isMaterialMatch(Location l, Material checkMaterial, Byte checkData){
		Material current = l.getBlock().getType();
		Byte currentData = l.getBlock().getData();
		if(checkMaterial==Material.AIR && current==Material.AIR 
				|| (current.name().endsWith("WATER") && currentData > 0 && currentData < 8)
				|| (current.name().endsWith("LAVA")  && currentData > 0 && currentData < 7)){
			return true;
		}else if(checkMaterial.name().endsWith("WATER") && current.name().endsWith("WATER")){
			return true;
		}else if(checkMaterial.name().endsWith("LAVA") && current.name().endsWith("LAVA")){
			return true;
		}else if(checkMaterial==current && currentData==checkData){
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	private void setMaterial(Location l, Material type, Byte data){
		l.getBlock().setType(type);
		l.getBlock().setData(data);
	}

}

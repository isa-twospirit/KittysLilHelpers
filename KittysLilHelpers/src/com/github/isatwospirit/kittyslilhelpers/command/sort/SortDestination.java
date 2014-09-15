package com.github.isatwospirit.kittyslilhelpers.command.sort;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class SortDestination{
	public enum DestinationContentType{
		INCOMPLETE,
		EMPTY,
		OK
	}
	
	private String name = null;
	private OfflinePlayer owner = null;
	private Location location1 = null;
	private Location location2 = null;
	private ConfigSection config = null;

	//Constructors
	public SortDestination(ConfigSection parentConfig, String name){
		this.name = name;
		config = parentConfig.getConfigurationSection(name);
		this.location1 = config.getLocation("coord1");
		this.location2 = config.getLocation("coord2");
		this.owner = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("owner")));
	}
	
	public SortDestination(ConfigSection parentConfig, String name, OfflinePlayer owner, Location location1, Location location2){
		this.config=parentConfig.createSection(name);
		this.name = name;
		this.owner = owner;
		this.location1 = location1;
		this.location2 = location2;
	}
	
	//Getters/Setters
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		switch(this.getContentType()){
			case INCOMPLETE:
				return KittysLilHelpers.COLOR_DESTINATION_INCOMPLETE + this.getName() + ChatColor.RESET;
			case EMPTY:
				return KittysLilHelpers.COLOR_DESTINATION_EMPTY + this.getName() + ChatColor.RESET;
			case OK:
				return KittysLilHelpers.COLOR_DESTINATION_OK + this.getName() + ChatColor.RESET;
			default:
				return KittysLilHelpers.COLOR_ERROR + this.getName() + ChatColor.RESET;
		}
	}
	
	public OfflinePlayer getOwner(){
		return this.owner;
	}
	
	public void setOwner(OfflinePlayer owner){
		if(owner!=null){
			this.owner = owner;
			this.save();
		}
	}
	
	public Location getLocation1(){
		return this.location1.clone();
	}
	
	public void setLocation1(Location location){
		if(location!=null){
			this.location1 = location;
			if(this.getLocation2()!=null){
				if(this.getLocation2().getWorld()!=location.getWorld()){
					this.setLocation2(null);
				}
			}
			this.save();
		}
	}
	
	public Location getLocation2(){
		if(this.location2==null)
			return null;
		else
			return this.location2.clone();
	}
	
	public void setLocation2(Location location){
		if(location==null){
			if(this.getLocation2()==null)
				this.location2 = this.getLocation1();
			else if(this.getLocation2().getWorld() != this.getLocation1().getWorld())
				this.location2 = this.getLocation1();
		}else if(location.getWorld() == this.getLocation1().getWorld())
			this.location2 = location;
		this.save();
	}

	public DestinationContentType getContentType(){
		if(this.getLocation2()==null)
			return DestinationContentType.INCOMPLETE;
		else if(this.getInventories().size()==0)
			return DestinationContentType.EMPTY;
		else
			return DestinationContentType.OK;
	}
	
	public String getDescription(Boolean shortDescription){
		String msg="";
		if(shortDescription){
			msg = this.getDisplayName() + ": " + this.getOwner().getName() + ", " + Utils.getLocationText(this.getLocation1()) + "-" + Utils.getLocationText(this.getLocation2(), false) + ", " + this.getInventories().size() + " Chest(s)"; 
		}else{
			msg = "SortDestination: " + this.getDisplayName() + "\n" +
				  "Owned by:    " + this.getOwner().getName() + "\n" +
				  "Range:       " + Utils.getLocationText(this.getLocation1(), true) + " - " + Utils.getLocationText(this.getLocation2(), false) + 
				  "# of Chests: " + this.getInventories().size();

		}
		return msg;
	}
	
	public HashMap<Location, Inventory> getInventories(){
		HashMap<Location, Inventory>result = new HashMap<Location, Inventory>();
		if(this.getLocation1()==null || this.getLocation2()==null)
			return result;
		
		try{
			int xMin=this.getLocation1().getBlockX();
			int xMax=this.getLocation2().getBlockX();
			int yMin=this.getLocation1().getBlockY();
			int yMax=this.getLocation2().getBlockY();
			int zMin=this.getLocation1().getBlockZ();
			int zMax=this.getLocation2().getBlockZ();
			
			if(this.getLocation1().getBlockX()>this.getLocation2().getBlockX()){
				xMin=this.getLocation2().getBlockX();
				xMax=this.getLocation1().getBlockX();
			}
			if(this.getLocation1().getBlockY()>this.getLocation2().getBlockY()){
				yMin=this.getLocation2().getBlockY();
				yMax=this.getLocation1().getBlockY();
			}
			if(this.getLocation1().getBlockZ()>this.getLocation2().getBlockZ()){
				zMin=this.getLocation2().getBlockZ();
				zMax=this.getLocation1().getBlockZ();
			}
			KittysLilHelpers plugin = (KittysLilHelpers)Bukkit.getPluginManager().getPlugin("KittysLilHelpers");
			Sort sort = (Sort)plugin.getCmd("sort");
			for(int xc=xMin;xc<=xMax;xc++){
				for(int yc=yMin;yc<=yMax;yc++){
					for(int zc=zMin;zc<=zMax;zc++){
						try{
							Inventory newDest=Utils.getInventoryFrom(new Location(this.getLocation1().getWorld(), xc, yc, zc));
							if(newDest!=null){
								if(sort.getSources().get(newDest)==null){
									Location l=Utils.getInventoryLocation(newDest);
									if(!result.containsKey(l)){
										result.put(l, newDest);
									}
								}
							}						
						}catch(Exception e){
							Bukkit.getLogger().severe(e.getMessage());
						}
					}
				}
			}
		}catch(Exception outer){
			Bukkit.getLogger().severe(outer.getMessage());
		}
		return result;
	}

	//Public methods
	public void delete(){
		this.config.getParent().set(this.getName(), null);
		this.config.save();
	}
	
	//Strictly internal stuff
	private void save(){
		this.config.set("owner", "" + this.getOwner().getUniqueId());
		this.config.set("coord1", this.getLocation1());
		this.config.set("coord2", this.getLocation2());
		this.config.save();
	}
}

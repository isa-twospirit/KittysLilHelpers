package com.github.isatwospirit.kittyslilhelpers.command.sort;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class SortSource{
	public enum SourceChestType{
		NONE,
		CHEST,
		TRAPPED_CHEST
	}
	
	private String name = null;
	private OfflinePlayer owner = null;
	private Location location = null;
	private ConfigSection config = null;
	
	//Constructors
	public SortSource(ConfigSection parentConfig, String name){
		this.name = name;
		config = parentConfig.getConfigurationSection(name);
		this.location = config.getLocation("coord");
		this.owner = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("owner")));
	}
	
	public SortSource(ConfigSection parentConfig, String name, OfflinePlayer owner, Location location){
		this.name = name;
		this.owner = owner;
		this.location = location;
		this.config = parentConfig.createSection(name);
		this.save();
	}
	
	//Getters/Setters
	public String getName(){
		return name;
	}
	
	public String getDisplayName(){
		switch(this.getSourceType()){
			case NONE:
				return KittysLilHelpers.COLOR_SOURCE_NO_CHEST + this.getName() + ChatColor.RESET;
			case CHEST:
				return KittysLilHelpers.COLOR_SOURCE_CHEST + this.getName() + ChatColor.RESET;
			case TRAPPED_CHEST:
				return KittysLilHelpers.COLOR_SOURCE_TRAPPED_CHEST + this.getName() + ChatColor.RESET;
			default:
				return KittysLilHelpers.COLOR_ERROR + this.getName() + ChatColor.RESET;
		}
		
	}
	
	public OfflinePlayer getOwner(){
		return owner;
	}
	
	public void setOwner(OfflinePlayer owner){
		if(owner!=null){
			this.owner = owner;
			this.save();
		}
	}
	
	public Location getLocation(){
		return location.clone();
	}
	
	public void setLocation(Location location){
		if(location!=null){
			this.location = Utils.getInventoryLocation(location);
			this.save();
		}
	}
	
	public SourceChestType getSourceType(){
		Material check = this.getLocation().getBlock().getType();
		if(check == Material.CHEST)
			return SourceChestType.CHEST;
		else if(check == Material.TRAPPED_CHEST)
			return SourceChestType.TRAPPED_CHEST;
		else
			return SourceChestType.NONE;
	}
	
	public String getDescription(Boolean shortDescription){
		String msg = "";
		
		if(shortDescription){
			msg = this.getDisplayName() + ": " + this.getOwner().getName() + ", " + Utils.getLocationText(this.getLocation(), true) + ")";			
		}else{
			msg = "SortSource: " + this.getDisplayName() + "\n" +
				 "Owned by:   " + this.getOwner().getName() + "\n" +
				 "Located:    " + Utils.getLocationText(this.getLocation(), true);
		}
		return msg;
	}

	public Inventory getInventory(){
		if(this.getLocation()==null)
			return null;
		return Utils.getInventoryFrom(this.getLocation());
	}
	
	//Public methods
	public void delete(){
		this.config.getParent().set(this.getName(), null);
		this.config.save();
	}
	
	//Strictly internal stuff
	private void save(){
		this.config.set("owner", "" + this.getOwner().getUniqueId());
		this.config.set("coord", this.getLocation());
		this.config.save();
	}
}

package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class WorldInfo implements WorldFlagContainer{
	
	private String name;
	private ConfigSection config;
	private Boolean autoLoad;
	private Boolean unloadWhenEmpty;
	private WorldGroup worldGroup;
	private OfflinePlayer owner;
	private WorldFlags flags;
	private List<String> lore;
	
	public WorldInfo(File container){
		this.name = container.getName();
		this.config = KittysLilHelpers.getConfig("world").getConfigurationSection("worlds", true).getConfigurationSection(this.name, true);
		this.autoLoad = config.getBoolean("can_auto_load", true);
		this.unloadWhenEmpty = config.getBoolean("unload_when_empty", true);
		try{
			this.owner = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("owner")));
		}catch(Exception e){
			
		}
		this.lore = config.getStringList("lore");
		this.worldGroup = WorldGroups.getInstance().add(config.getString("worldgroup", getDefaultWorldGroupName(this.name)));
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		return Utils.colorize(this.name, ChatColor.BLUE);
	}
	
	public String getDescription(Boolean shortDescription){
		String description = this.getDisplayName();

		if(shortDescription){
			if(this.getOwner()!=null)
				description += ", Owned by " + this.getOwner().getName();
			description += ", Group: " + this.getWorldGroup().getName();
			if(this.isLoaded())
				description += ", " + Utils.colorize("loaded", ChatColor.GREEN);
			else
				description += ", " + Utils.colorize("unloaded", ChatColor.RED);
			if(this.canAutoLoad())
				description += ", " + Utils.colorize("auto-load", ChatColor.GREEN);
			if(this.doUnloadWhenEmpty())
				description += ", " + Utils.colorize("auto-unload", ChatColor.GREEN);
		}else{
			if(this.getOwner()!=null)
				description += "\nOwned by " + this.getOwner().getName();
			description +=     "\nGroup:   " + this.getWorldGroup().getName();
			description +=     "\nStatus:  ";
			if(this.isLoaded())
				description += Utils.colorize("loaded", ChatColor.GREEN);
			else
				description += Utils.colorize("unloaded", ChatColor.RED);
			if(this.canAutoLoad())
				description += ", " + Utils.colorize("auto-load", ChatColor.GREEN);
			if(this.doUnloadWhenEmpty())
				description += ", " + Utils.colorize("auto-unload", ChatColor.GREEN);
			if(this.getLore()!=null)
				description += "\nWorld Lore:\n" + this.getLore();
		}
		
		return description;
	}
	
	public boolean isLoaded(){
		if(this.getWorld()!=null)
			return true;
		else
			return false;
	}
	
	public World getWorld(){
		try{
			return Bukkit.getWorld(this.name);
		}catch(Exception e){
			return null;
		}
	}
	
	public OfflinePlayer getOwner(){
		if(this.owner==null)
			return this.getWorldGroup().getOwner();
		else
			return this.owner;
	}
	public void setOwner(OfflinePlayer newOwner){
		this.owner = newOwner;
		if(newOwner == null){
			this.config.set("owner", null);
		}else{
			this.config.set("owner", "" + newOwner.getUniqueId());
		}
		this.config.save();
	}
	
	public boolean canAutoLoad(){
		return this.autoLoad;
	}
	public void canAutoLoad(Boolean value){
		this.autoLoad = value;
		this.config.set("can_auto_load", this.autoLoad);
		this.config.save();
	}
	
	public boolean doUnloadWhenEmpty(){
		return this.unloadWhenEmpty;
	}
	public void doUnloadWhenEmpty(Boolean value){
		this.unloadWhenEmpty = value;
		this.config.set("unload_when_empty", this.unloadWhenEmpty);
		this.config.save();
	}
	
	public Boolean hasLore(){
		if(this.lore==null)
			return false;
		if(this.lore.size()==0)
			return false;
		if(this.lore.size()==1 && this.lore.get(0)=="")
			return false;
		return true;
	}
	public List<String> getLore(){
		return this.lore;
	}
	public void setLore(List<String> value){
		this.lore = value;
		this.config.set("lore", this.lore);
		this.config.save();
	}
	
	public void giveLoreBook(Player p){
		ItemStack loreBook = null;
		BookMeta book = null;
		
		if(this.getOwner()!=null){
			if(p.getUniqueId()==this.getOwner().getUniqueId()){
				loreBook = new ItemStack(Material.BOOK_AND_QUILL);
				book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.BOOK_AND_QUILL);
			}
		}
		if(loreBook==null){
			loreBook = new ItemStack(Material.WRITTEN_BOOK);
			book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		}
		
		book.setDisplayName("World Lore Of " + this.getName());
		if(this.hasLore()){
			book.setPages(this.getLore());
		}else{
			book.addPage(ChatColor.BLUE + "The Tale Of " + this.getName() + ChatColor.BLACK + "\n");
		}
		loreBook.setItemMeta(book);
		if(p.getInventory().addItem(loreBook).isEmpty()==false){
			p.getWorld().dropItem(p.getLocation(), loreBook);
		}
	}
	
	public WorldGroup getWorldGroup(){
		return this.worldGroup;
	}
	public void setWorldGroup(WorldGroup value){
		if(value==null){
			this.worldGroup = WorldGroups.getInstance().get(getDefaultWorldGroupName(this.name));
			this.config.set("worldgroup", null);
		}else{
			this.worldGroup = value;
			this.config.set("worldgroup", this.worldGroup.getName());
		}
		this.config.save();
	}
	
    private static String getDefaultWorldGroupName(String worldName){
    	String result = worldName;
    	if(result.endsWith("_nether")){
    		result = result.substring(0, result.length() - 7);
    	}else if(result.endsWith("_the_end")){
    		result = result.substring(0, result.length() - 8);
    	}
    	return result;
    }

    public void setSpawnBedLocation(Player p, Location l){
    	this.config.getConfigurationSection("spawnbedlocations", true).set("" + p.getUniqueId(), l);
    	this.config.save();
    }
    public Location getSpawnBedLocation(Player p){
    	Location def = this.getWorld().getSpawnLocation();
    	Location saved = this.config.getConfigurationSection("spawnbedlocations", true).getLocation("" + p.getUniqueId(), this.getWorld().getSpawnLocation()); 

    	if(saved==null){
    		return def;
    	}else if(saved.getBlock().getType()!=Material.BED_BLOCK){
    		return def;
    	}else{
        	return saved; 
    	}
    }
    
    public void setLastLocation(Player p, Location l){
    	this.config.getConfigurationSection("lastlocations", true).set("" + p.getUniqueId(), l);
    	this.config.save();
    }
    public Location getLastLocation(Player p){
    	return this.getLastLocation(p, this.getSpawnBedLocation(p));
    }
    public Location getLastLocation(Player p, Location def){
    	return this.config.getConfigurationSection("lastlocations", true).getLocation("" + p.getUniqueId(), def);
    }
    
	@Override
	public ContainerType getContainerType() {
		return ContainerType.WORLD;
	}

	@Override
	public WorldFlags getWorldFlags() {
		if(this.flags==null)
			this.flags=new WorldFlags(this, this.getWorldGroup().getWorldFlags(), this.config.getConfigurationSection("flags", true));
		return this.flags;
	}

	@Override
	public String getContainerName() {
		return "world:" + this.getName();
	}	
}

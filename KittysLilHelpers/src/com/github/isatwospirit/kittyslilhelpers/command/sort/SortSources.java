package com.github.isatwospirit.kittyslilhelpers.command.sort;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;

import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class SortSources implements ItemContainer {
	private HashMap<String, SortSource> sources= null;
	private ConfigSection source = null;

	public SortSources(ConfigSection source){
		this.sources = new HashMap<String, SortSource>();
		this.source = source;
		for(String name : this.source.getKeys(false)){
			SortSource newSource = new SortSource(this.source, name);
			this.sources.put(name.toLowerCase(), newSource);
		}

	}

	public Integer size(){
		return this.sources.size();
	}
	
	public Boolean contains(String name){
		return this.sources.containsKey(name.toLowerCase());
	}
	public Boolean contains(SortSource item){
		return this.sources.containsValue(item);
	}
	
	public SortSource get(String name){
		return this.sources.get(name.toLowerCase());
	}

	public SortSource get(Inventory i){
		Location lCheck = Utils.getInventoryLocation(i); 
		for(SortSource current : this.all()){
			Location lCurrent = Utils.getInventoryLocation(current.getInventory());
			if(lCheck.equals(lCurrent)){
				return current;
			}
		}
		return null;
	}
	
	public SortSource get(Location lCheck){
		try{
			Inventory check=Utils.getInventoryFrom(lCheck);
			if(check!=null){
				return this.get(check);
			}
		}catch(Exception e){
			Bukkit.getLogger().severe(e.getMessage());
		}
		return null;
	}
	
	public Collection<SortSource> all(){
		return this.sources.values();
	}
	
	public SortSource add(SortSource newItem){
		if(this.contains(newItem)){
			return newItem;
		}else if(this.sources.containsKey(newItem.getName().toLowerCase())){
			return null;
		}else{
			this.sources.put(newItem.getName().toLowerCase(), newItem);
			return newItem;
		}
	}
	public SortSource add(String name, OfflinePlayer owner, Location location){
		SortSource newItem = new SortSource(this.source, name, owner, location);
		return this.add(newItem);
	}
	
	public Boolean delete(SortSource item){
		if(this.contains(item)){
			this.sources.remove(item.getName().toLowerCase());
			item.delete();
			return true;
		}
		return false;
	}
	
	@Override
	public String getItemDisplayName() {
		return "SortSource";
	}

	@Override
	public Class<?> getItemType() {
		return SortSource.class;
	}

	@Override
	public Object getItem(String key) {
		return this.get(key);
	}

	@Override
	public OfflinePlayer getItemOwner(String key) {
		if(this.contains(key)){
			return this.get(key).getOwner();
		}else{
			return null;
		}
	}
}

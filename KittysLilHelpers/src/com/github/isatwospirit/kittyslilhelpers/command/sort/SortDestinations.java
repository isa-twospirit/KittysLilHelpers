package com.github.isatwospirit.kittyslilhelpers.command.sort;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;

public class SortDestinations implements ItemContainer{
	private HashMap<String, SortDestination> destinations = null;
	private ConfigSection source = null;

	public SortDestinations(ConfigSection source){
		this.destinations = new HashMap<String, SortDestination>();
		this.source = source;
		for(String name : this.source.getKeys(false)){
			SortDestination newDestination = new SortDestination(this.source, name);
			this.destinations.put(name.toLowerCase(), newDestination);
		}

	}

	public Integer size(){
		return this.destinations.size();
	}
	
	public Boolean contains(String name){
		return this.destinations.containsKey(name.toLowerCase());
	}
	public Boolean contains(SortDestination item){
		return this.destinations.containsValue(item);
	}
	
	public SortDestination get(String name){
		return this.destinations.get(name.toLowerCase());
	}

	public Collection<SortDestination> all(){
		return this.destinations.values();
	}
	
	public SortDestination add(SortDestination newItem){
		if(this.contains(newItem)){
			return newItem;
		}else if(this.destinations.containsKey(newItem.getName().toLowerCase())){
			return null;
		}else{
			this.destinations.put(newItem.getName().toLowerCase(), newItem);
			return newItem;
		}
	}
	public SortDestination add(String name, OfflinePlayer owner, Location location1, Location location2){
		SortDestination newItem = new SortDestination(this.source, name, owner, location1, location2);
		return this.add(newItem);
	}
	
	public Boolean delete(SortDestination item){
		if(this.contains(item)){
			this.destinations.remove(item.getName().toLowerCase());
			item.delete();
			return true;
		}
		return false;
	}
	
	@Override
	public String getItemDisplayName() {
		return "SortDestination";
	}

	@Override
	public Class<?> getItemType() {
		return SortDestination.class;
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

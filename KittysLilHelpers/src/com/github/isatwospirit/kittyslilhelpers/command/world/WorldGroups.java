package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;

public class WorldGroups implements ItemContainer{
	private static WorldGroups instance = null;
	
	private Map<String, WorldGroup> worldGroups = new HashMap<String, WorldGroup>();
	private ConfigSection config = null;
	
	public static WorldGroups getInstance(){
		if(instance == null){
			instance = new WorldGroups(); 
		}
		return instance;
	}
	
	private WorldGroups(){
		KittysLilHelpers.logInfo("Loading WorldGroups...");
		this.config = KittysLilHelpers.getConfig("world").getConfigurationSection("worldgroups", true);
		for(String key : this.config.getKeys(false)){
			if(this.config.isConfigurationSection(key)){
				WorldGroup newItem = new WorldGroup(key, this.config);
				this.worldGroups.put(newItem.getName().toLowerCase(), newItem);
			}
		}
		KittysLilHelpers.logInfo("Done, " + this.worldGroups.size() + " WorldGroups loaded.");
	}
	
	public boolean contains(String name){
		return this.worldGroups.containsKey(name.toLowerCase());
	}
	public boolean contains(WorldGroup worldGroup){
		return this.worldGroups.containsValue(worldGroup);
	}
	public boolean contains(World forWorld){
		for(WorldGroup g : this.worldGroups.values()){
			if(g.getWorlds().contains(forWorld)){
				return true;
			}
		}
		return false;
	}
	
	public WorldGroup get(String name){
		return this.worldGroups.get(name.toLowerCase());
	}
	public WorldGroup get(World forWorld){
		for(WorldGroup g : this.worldGroups.values()){
			if(g.getWorlds().contains(forWorld)){
				return g;
			}
		}
		return null;
	}
	
	public WorldGroup add(String name){
		if(this.contains(name)==false){
			return this.add(new WorldGroup(name, this.config, Bukkit.getDefaultGameMode()));
		}else{
			return this.get(name);
		}
	}
	
	public WorldGroup add(String name, GameMode gameMode){
		if(this.contains(name)==false){
			return this.add(new WorldGroup(name, this.config, gameMode));
		}else{
			return this.get(name);
		}
	}
	public WorldGroup add(WorldGroup newItem){
		if(this.contains(newItem.getName())){
			return this.get(newItem.getName());
		}else{
			this.worldGroups.put(newItem.getName().toLowerCase(), newItem);
			return newItem; 
		}
	}

	public Integer delete(WorldGroup w){
		Integer result = w.getWorlds().size();
		if(result==0){
			if(this.worldGroups.remove(w.getName().toLowerCase())!=null){
				this.config.set(w.getName(), null);
			}
		}
		return result;
	}
	
	public Collection<WorldGroup> values(){
		return this.worldGroups.values();
	}

	@Override
	public String getItemDisplayName() {
		return "WorldGroup";
	}

	@Override
	public Class<?> getItemType() {
		return WorldGroup.class;
	}

	@Override
	public Object getItem(String key) {
		return this.get(key);
	}

	@Override
	public OfflinePlayer getItemOwner(String key) {
		return null;
	}
}

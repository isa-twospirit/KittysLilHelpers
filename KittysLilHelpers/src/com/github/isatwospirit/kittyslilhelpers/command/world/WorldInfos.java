package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;

public class WorldInfos implements ItemContainer, FilenameFilter {
	private static WorldInfos instance = null;
	
	private Map<String, WorldInfo> worldInfos = new HashMap<String, WorldInfo>();
	
	public static WorldInfos getInstance(){
		if(instance == null){
			instance = new WorldInfos();
		}
		return instance;
	}
	
	private WorldInfos(){
		KittysLilHelpers.logInfo("Loading WorldInfos...");
		File[] dirs = Bukkit.getWorldContainer().listFiles();
		for(File dir : dirs){
			if(dir.isDirectory()){
				if(dir.listFiles(this).length==1){
					WorldInfo newItem = new WorldInfo(dir);
					this.worldInfos.put(newItem.getName().toLowerCase(), newItem);
				}
			}
		}
		KittysLilHelpers.logInfo("Done, " + this.worldInfos.size() + " WorldInfos loaded.");
	}
	
	public WorldInfo get(String name){
		return this.worldInfos.get(name.toLowerCase());
	}
	
	public WorldInfo get(World world){
		for(WorldInfo w : this.worldInfos.values()){
			if(w.isLoaded()){
				if(w.getWorld().getUID() == world.getUID()){
					return w;
				}
			}
		}
		return null;
	}
	
	public Integer size(){
		return this.worldInfos.size();
	}
	
	public Collection<WorldInfo> values(){
		return this.worldInfos.values();
	}
	
	//ItemContainer
	@Override
	public String getItemDisplayName() {
		return "World";
	}

	@Override
	public Class<?> getItemType() {
		return WorldInfo.class;
	}

	@Override
	public Object getItem(String key) {
		return this.get(key);
	}
	
	public void setLastLocation(Player p, Location l){
		this.get(l.getWorld()).setLastLocation(p, l);
	}
	public void setSpawnBedLocation(Player p, Location l){
		this.get(l.getWorld()).setSpawnBedLocation(p, l);
	}

	@Override
	public OfflinePlayer getItemOwner(String key) {
		WorldInfo item = this.get(key);
		if(item!=null){
			return item.getOwner();
		}else{
			return null;
		}
	}

	//FilenameFilter
	@Override
	public boolean accept(File dir, String name) {
		return(name.compareToIgnoreCase("level.dat")==0);
	}
}

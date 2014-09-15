package com.github.isatwospirit.kittyslilhelpers.command.world;

import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;

public class WorldFlagLookup implements ItemContainer {
	private static WorldFlagLookup instance;

	public static WorldFlagLookup getInstance(){
		if(instance==null)
			instance = new WorldFlagLookup();
		return instance;
	}
	
	private WorldFlagLookup(){
	}
	
	@Override
	public String getItemDisplayName() {
		return "WorldFlagContainer";
	}

	@Override
	public Class<?> getItemType() {
		return WorldFlagContainer.class;
	}

	@Override
	public Object getItem(String key) {
		WorldFlagContainer result = null;
		key = key.toLowerCase();
		if(key.compareToIgnoreCase("default")==0){
			result = (WorldFlagContainer)KittysLilHelpers.getInstance();
		}else if(key.startsWith("group:")){
			key = key.substring(6);
			result = WorldGroups.getInstance().get(key);
		}else if(key.startsWith("world:")){
			key = key.substring(6);
			result = WorldInfos.getInstance().get(key);
		}
		return result;
	}

	@Override
	public OfflinePlayer getItemOwner(String key) {
		WorldFlagContainer item = (WorldFlagContainer)this.getItem(key);
		return item.getOwner();
	}

}

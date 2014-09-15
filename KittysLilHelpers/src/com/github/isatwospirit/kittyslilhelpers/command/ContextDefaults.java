package com.github.isatwospirit.kittyslilhelpers.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentOfflinePlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentPlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentWorld;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldGroup;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldInfo;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldInfos;

public class ContextDefaults {
	private HashMap<Class<?>, Object> defaults = new HashMap<Class<?>, Object>();
	
	public Boolean hasDefault(Class<?> type){
		//System.out.println("Has Default for " + type.getSimpleName() + ": " + this.defaults.containsKey(type));
		return this.defaults.containsKey(type);
	}
	
	public Object getDefault(Class<?> type){
		if(this.defaults.containsKey(type)){
			return this.defaults.get(type);			
		}else{
			return null;
		}
	}
	
	public void setDefault(Class<?> type, Object value){
		//System.out.println("Setting Default for " + value.getClass().getSimpleName() + ". ");
		this.defaults.put(type, value);
	}
	
	public ContextDefaults(CommandSender sender){
		if(sender instanceof Player){
			Player pDefault = (Player)sender;
			Location lDefault = this.getTargetLocation(pDefault, 5);
			if(lDefault==null)
				lDefault=pDefault.getLocation();

			this.setDefault(ArgumentPlayer.class, (org.bukkit.entity.Player)pDefault);						//Default Player
			this.setDefault(ArgumentOfflinePlayer.class, (org.bukkit.OfflinePlayer)pDefault);				//Default OfflinePlayer
			this.setDefault(ArgumentWorld.class, (org.bukkit.World)pDefault.getWorld());					//Default World
			this.setDefault(ArgumentLocation.class, (org.bukkit.Location)lDefault);							//Default Location
			this.setDefault(WorldInfo.class, WorldInfos.getInstance().get(pDefault.getWorld()));
			this.setDefault(WorldGroup.class, WorldInfos.getInstance().get(pDefault.getWorld()).getWorldGroup());
		}else if(sender instanceof BlockCommandSender){
			Block bCmd = ((BlockCommandSender)sender).getBlock();
			this.setDefault(ArgumentWorld.class, (org.bukkit.World)bCmd.getWorld());						//Default World
			this.setDefault(ArgumentLocation.class, (org.bukkit.Location)bCmd.getLocation());				//Default Location
			this.setDefault(WorldInfo.class, WorldInfos.getInstance().get(bCmd.getWorld()));
			this.setDefault(WorldGroup.class, WorldInfos.getInstance().get(bCmd.getWorld()).getWorldGroup());
		}else{
			org.bukkit.World wDefault = Bukkit.getWorlds().get(0);
			this.setDefault(ArgumentWorld.class, wDefault);	//Default World
			this.setDefault(WorldInfo.class, WorldInfos.getInstance().get(wDefault));
			this.setDefault(WorldGroup.class, WorldInfos.getInstance().get(wDefault).getWorldGroup());
		}
	}
	
	private Location getTargetLocation(Player p, int range){
		BlockIterator b = new BlockIterator(p, range);
		Block next;
		while(b.hasNext()){
			next = b.next();
			if(next.getType()!=Material.AIR){
				return next.getLocation();
			}
		}
		return null;
	}
}

package com.github.isatwospirit.kittyslilhelpers.command.argument;

import java.io.File;
import java.io.FilenameFilter;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentWorld extends CommandArgument implements FilenameFilter{
	private boolean allowUnloadedWorlds = false;
	
	public ArgumentWorld() {
		super(World.class);
	}
	
	public ArgumentWorld(boolean allowUnloadedWorlds) {
		super(World.class);
		this.allowUnloadedWorlds = allowUnloadedWorlds;
	}

	@Override
	public OfflinePlayer getOwningPlayer() {
		return null;
	}
	
	@Override
	public String getFormattedName() {
		return this.getName();
	}

	@Override
	public World getValue() {
		return (World)super.getValue();
	}

	@Override
	public World getDefaultValue() {
		return (World)super.getDefaultValue();
	}
	
	@Override
	public ArgumentWorld clone(){
		ArgumentWorld result = new ArgumentWorld(this.allowUnloadedWorlds);
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;

	}
	
	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		World wFound = null;
		String id = args[startIndex];
		try{
			//Try to find by UUID
			wFound = Bukkit.getWorld(UUID.fromString(id));
		}catch(Exception e1){
			try{
				//By name - case-sensitive
				wFound = Bukkit.getWorld(args[startIndex]);
			}catch(Exception e2){
				//by name - case-insensitive
				for(World wTemp : Bukkit.getWorlds()){
					if(wTemp.getName().compareToIgnoreCase(args[startIndex])==0){
						wFound = wTemp;
						break;
					}
				}
			}
		}
		if(wFound==null && this.allowUnloadedWorlds==true){
			File[] dirs = Bukkit.getWorldContainer().listFiles();
			for(File dir : dirs){
				if(dir.isDirectory()){
					if(dir.listFiles(this).length==1){
						if(dir.getName().compareToIgnoreCase(id)==0){
							Bukkit.getLogger().info("Loading world " + dir.getName() + "...");
							wFound = Bukkit.createWorld(WorldCreator.name(dir.getName()));
							Bukkit.getLogger().info("World" + dir.getName() + " loaded.");
							break;
						}
					}
				}
			}
		} 
		if(wFound!=null){
			if(defaultValues.hasDefault(ArgumentWorld.class)==false)
				defaultValues.setDefault(ArgumentWorld.class, wFound);
			this.setValue(wFound);
			this.setFindValueResult(FindValueResult.SUCCESS);
			this.setFindValueMessage(null);
			return 1;
		}else{
			this.setValue(null);
			this.setFindValueResult(FindValueResult.FAILED_NOT_FOUND);
			this.setFindValueMessage("World with name or UUID " + args[startIndex] + " does not exist.");
			return 0;
		}
	}

	@Override
	public boolean accept(File dir, String name) {
		return(name.compareToIgnoreCase("level.dat")==0);
	}
}

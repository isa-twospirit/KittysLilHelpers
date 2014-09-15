package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.net.URL;
import java.security.CodeSource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;

public class WorldGenerators implements ItemContainer{
	private static WorldGenerators instance;
	private Map<String, Class<?>> generators;
	
	public static WorldGenerators getInstance(){
		if(instance==null){
			instance = new WorldGenerators();
		}
		return instance;
	}
	
	private WorldGenerators(){
		this.generators = new LinkedHashMap<String, Class<?>>();

		KittysLilHelpers.logInfo("Listing WorldGenerators...");
		//this.addGeneratorsFromClass(Bukkit.class);
		for(Plugin p : Bukkit.getPluginManager().getPlugins()){
			this.addGeneratorsFromClass(p);
		}
		KittysLilHelpers.logInfo("Done, " + this.generators.size() + " WorldGenerator(s) found.");
	}

	public Integer size(){
		return this.generators.size();
	}
	
	public Class<?> get(String key){
		return this.generators.get(key);
	}
	
	public Collection<Class<?>> values(){
		return this.generators.values();
	}
	
	private void addGeneratorsFromClass(Plugin source){
		try{
			CodeSource src = source.getClass().getProtectionDomain().getCodeSource();
			//ChunkGenerator def = source.getDefaultWorldGenerator(arg0, arg1)
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			ZipEntry entry=zip.getNextEntry();
			while(entry != null){
				try{
					if(entry.getName().endsWith(".class")){
						String className = entry.getName().substring(0, entry.getName().length()-6).replace("/", ".");
						KittysLilHelpers.logInfo(className);
						Class<?> check = Class.forName(className);
						if(ChunkGenerator.class.isAssignableFrom(check)){
							this.generators.put(className, check);
						}
					}
				}catch(Exception e){
					System.out.println("   Exception: " + e.getMessage());
				}
				entry = zip.getNextEntry();
			}
			zip.close();
		}catch(Exception e2){
			System.out.println("Exception: " + e2.getMessage());
		}		
	}
	
	@Override
	public String getItemDisplayName() {
		return "WorldGenerator";
	}

	@Override
	public Class<?> getItemType() {
		return ChunkGenerator.class;
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

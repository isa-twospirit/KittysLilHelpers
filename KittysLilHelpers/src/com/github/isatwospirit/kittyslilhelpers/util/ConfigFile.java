package com.github.isatwospirit.kittyslilhelpers.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;

public class ConfigFile{
	private YamlConfiguration config;
	private String fileName;
	
	public ConfigFile(String configName){
		if(configName.endsWith(".yml")==false)
			configName+=".yml";

		this.fileName=configName;
    	File configFile = Utils.findFile(KittysLilHelpers.getFolder(), this.fileName, false);
    	if(configFile==null){
    		try{
        		KittysLilHelpers.getInstance().saveResource(this.fileName, true);
        		configFile = Utils.findFile(KittysLilHelpers.getFolder(), this.fileName, false);
    		}catch(Exception e){
    			configFile = new File(KittysLilHelpers.getFolder(), this.fileName);
    		}
    	}
    	this.config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public String getConfigFileName(){
		return this.fileName;
	}
	
	public ConfigSection getRootSection(){
		return ConfigSection.fromConfigurationSection(this, config);
	}
	
	public void save(){
		try {
			this.config.save(KittysLilHelpers.getFolder().getAbsolutePath() + "/" + fileName);
		} catch (IOException e) {
			KittysLilHelpers.logSevere("Unable to save configuration file " + this.fileName + ": " + e.getMessage());
		}
	}
}

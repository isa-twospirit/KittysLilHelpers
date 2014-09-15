package com.github.isatwospirit.kittyslilhelpers.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;

public class CommandOption {
	private CommandEx command = null;
	private String name = "";
	private String shortDescription = "";
	private String longDescription = "";
	private Map<String, CommandArgument> arguments = new LinkedHashMap<String, CommandArgument>();
	private Map<Integer, String> argumentordinals = new LinkedHashMap<Integer, String>();
	private String permission = "";
	private Boolean hasOthersPermission = false;
	private Integer minArgCount = null;
	
	public CommandEx getCommand(){
		return this.command;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		return KittysLilHelpers.COLOR_OPTION + this.getName() + ChatColor.RESET;
	}
	
	public String getShortDescription(){
		if(this.shortDescription==null)
			return "(Description not available)";
		else
			return this.shortDescription;
	}
	
	public String getLongDescription(){
		if(this.longDescription==null)
			return this.getShortDescription();
		else
			return this.longDescription;
	}
	
	public String getPermission(){
		if(this.permission==null)
			return null;
		else if(this.permission.compareToIgnoreCase("")==0)
			return null;
		else
			return this.permission;
	}
	public Boolean hasOthersPermission(){
		return this.hasOthersPermission;
	}

	private Map<String, CommandArgument> getArguments(){
		return this.arguments;
	}
	private Map<Integer, String> getArgumentOrdinals(){
		return this.argumentordinals;
	}
	
	public CommandArgument getArgument(String name){
		if(this.getArguments().containsKey(name.toLowerCase()))
			return this.getArguments().get(name.toLowerCase());
		else
			return null;
	}
	public CommandArgument getArgument(Integer ordinal){
		if(this.getArgumentOrdinals().containsKey(ordinal))
			return this.getArgument(this.getArgumentOrdinals().get(ordinal).toLowerCase());
		else
			return null;
	}

	public CommandArgument addArgument(CommandArgument argument){
		if(this.getArguments().containsKey(argument.getName())){
			return null;
		}else{
			this.getArgumentOrdinals().put(argument.getOrdinal(), argument.getName().toLowerCase());
			this.getArguments().put(argument.getName().toLowerCase(), argument);
			return argument;
		}
	}
	
	public CommandArgument addArgument(CommandArgument argument, String name, String description, OptionalStyle optionalStyle){
		Integer ordinal = this.getArgumentCount();
		argument.doInitialize(this, ordinal, name, description, optionalStyle, null, null);
		return this.addArgument(argument);
	}
	
	public CommandArgument addArgument(CommandArgument argument, String name, String description, OptionalStyle optionalStyle, Object defaultValue, String defaultValueDescription){
		Integer ordinal = this.getArgumentCount();
		argument.doInitialize(this, ordinal, name, description, optionalStyle, defaultValue, defaultValueDescription);
		return this.addArgument(argument);
	}

	public Collection<CommandArgument> getAllArguments(){
		return this.arguments.values();
	}
	
	public Integer getArgumentCount(){
		try{
			return this.getArguments().size();
		}catch(Exception e){
			return 0;
		}
	}	
	public Integer getMinArgumentCount(){
		if(this.minArgCount == null){
			if(this.getArgumentCount() == 0){
				return 0;
			}else{
				this.minArgCount = 0;
				for(CommandArgument current : this.getArguments().values()){
					if(current.getOptionalStyle()==OptionalStyle.REQUIRED)
						this.minArgCount+=1;
				}
			}
		}
		return this.minArgCount;
	}
	
	public CommandOption clone(){
		HashMap<String, CommandArgument> arguments = new HashMap<String, CommandArgument>();
		HashMap<Integer, String> argumentOrdinals = new HashMap<Integer, String>();
		
		for(CommandArgument arg : this.getArguments().values()){
			CommandArgument clone = arg.clone();
			arguments.put(clone.getName().toLowerCase(), clone);
			argumentOrdinals.put(clone.getOrdinal(), clone.getName().toLowerCase());
		}
		
		return new CommandOption(
				this.getCommand(), 
				this.getName(),
				this.getShortDescription(),
				this.longDescription,
				this.getPermission(),
				this.hasOthersPermission(),
				arguments,
				argumentOrdinals);
	}
	
	public CommandOption(CommandEx command, String name, String shortDescription, String longDescription, String permission, Boolean hasOthersPermission){
		this.command = command;
		this.name = name;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permission = permission;
		this.hasOthersPermission = hasOthersPermission;		
	}

	private CommandOption(CommandEx command, String name, String shortDescription, String longDescription, String permission, Boolean hasOthersPermission,
			HashMap<String, CommandArgument> arguments, HashMap<Integer, String> argumentOrdinals){
		this.command = command;
		this.name = name;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
		this.permission = permission;
		this.hasOthersPermission = hasOthersPermission;	
		this.arguments = arguments;
		this.argumentordinals = argumentOrdinals;
	}
}

package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentBoolean extends CommandArgument {
	public ArgumentBoolean() {
		super(Boolean.class);
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
	protected Integer findValueInternal(String[] args, Integer startIndex,
			ContextDefaults defaultValues) {
		if(args[startIndex].compareToIgnoreCase("true")==0 ||
			args[startIndex].compareToIgnoreCase("yes")==0 ||
			args[startIndex].compareToIgnoreCase("on")==0){
			
			this.setValue(true);
		}else{
			this.setValue(false);
		}
		this.setFindValueResult(FindValueResult.SUCCESS);
		return 1;
	}

	@Override
	public ArgumentBoolean clone() {
		ArgumentBoolean result = new ArgumentBoolean();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}

}

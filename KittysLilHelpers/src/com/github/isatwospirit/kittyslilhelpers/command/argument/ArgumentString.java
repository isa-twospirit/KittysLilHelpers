package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentString extends CommandArgument {

	public ArgumentString() {
		super(String.class);
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
	public String getValue() {
		return (String)super.getValue();
	}

	@Override
	public String getDefaultValue() {
		return (String)super.getDefaultValue();
	}
	
	@Override
	public ArgumentString clone(){
		ArgumentString result = new ArgumentString();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}

	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		this.setValue(args[startIndex]);
		this.setFindValueResult(FindValueResult.SUCCESS);
		return 1;
	}
}

package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentInteger extends CommandArgument {

	public ArgumentInteger() {
		super(Integer.class);
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
	public Integer getValue() {
		return (Integer)super.getValue();
	}

	@Override
	public Integer getDefaultValue() {
		return (Integer)super.getDefaultValue();
	}
	
	@Override
	public ArgumentInteger clone(){
		ArgumentInteger result = new ArgumentInteger();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}
	
	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		try{
			this.setValue(Integer.parseInt(args[startIndex]));
			this.setFindValueResult(FindValueResult.SUCCESS);
			return 1;
		}catch(Exception e){
			this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
			return 0;
		}
	}
}

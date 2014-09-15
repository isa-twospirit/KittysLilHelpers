package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.apache.commons.lang.StringUtils;
import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentMultiString extends CommandArgument {

	public ArgumentMultiString() {
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
	public ArgumentMultiString clone(){
		ArgumentMultiString result = new ArgumentMultiString();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}

	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		String result = StringUtils.join(args, " ", startIndex, args.length);
		this.setValue(result);
		this.setFindValueResult(FindValueResult.SUCCESS);
		return args.length - startIndex;
	}

}

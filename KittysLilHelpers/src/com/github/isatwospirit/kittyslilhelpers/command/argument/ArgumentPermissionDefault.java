package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.PermissionDefault;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentPermissionDefault extends CommandArgument{
	public ArgumentPermissionDefault(){
		super(PermissionDefault.class);
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
		PermissionDefault result = null;
		if(args[startIndex].compareToIgnoreCase("true")==0){
			result = PermissionDefault.TRUE;
		}else if(args[startIndex].compareToIgnoreCase("false")==0){
			result = PermissionDefault.FALSE;
		}else if(args[startIndex].compareToIgnoreCase("op")==0){
			result = PermissionDefault.OP;
		}else if(args[startIndex].compareToIgnoreCase("not-op")==0){
			result = PermissionDefault.NOT_OP;
		}else if(args[startIndex].compareToIgnoreCase("default")==0){
			this.setValue(null);
			this.setFindValueResult(FindValueResult.SUCCESS);
			return 1;
		}
		
		if(result==null){
			this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
			this.setFindValueMessage("Invalue PermissionDefault, must be DEFAULT, TRUE, FALSE, OP or NOT-OP.");
			return 0;
		}else{
			this.setValue(result);
			this.setFindValueResult(FindValueResult.SUCCESS);
			return 1;
		}
	}

	@Override
	public CommandArgument clone() {
		ArgumentPermissionDefault result = new ArgumentPermissionDefault();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;		
	}
}

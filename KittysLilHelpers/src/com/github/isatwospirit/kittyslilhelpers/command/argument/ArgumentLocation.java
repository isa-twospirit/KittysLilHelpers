package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentLocation extends CommandArgument{

	public ArgumentLocation() {
		super(Location.class);
	}

	@Override
	public OfflinePlayer getOwningPlayer() {
		if(this.getValue()==null)
			return null;
		else 
			return this.getOption().getCommand().getOwningPlugin().getBlockOwner(this.getValue());
	}
	
	@Override
	public String getFormattedName() {
		return this.getName() + "_X " + this.getName() + "_Y " + this.getName() + "_Z";
	}

	@Override
	public Location getValue() {
		return (Location)super.getValue();
	}

	@Override
	public Location getDefaultValue() {
		return (Location)super.getDefaultValue();
	}
	
	@Override
	public ArgumentLocation clone(){
		ArgumentLocation result = new ArgumentLocation();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}

	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		Integer x = 0;
		Integer y = 0;
		Integer z = 0;
		if(args.length<startIndex+3){
			this.setValue(null);
			this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISSING);
			return 0;
		}else{
			try{
				x = Integer.decode(args[startIndex]);
				y = Integer.decode(args[startIndex + 1]);
				z = Integer.decode(args[startIndex + 2]);
				if(defaultValues.hasDefault(ArgumentWorld.class)){
					this.setValue(new Location((World)defaultValues.getDefault(ArgumentWorld.class), x, y, z));
					this.setFindValueResult(FindValueResult.SUCCESS);
					return 3;
				}else{
					this.setValue(null);
					this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISSING);
					this.setFindValueMessage("Target world not specified.");
					return 0;
				}
			}catch(Exception e){
				this.setValue(null);
				this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
				return 0;
			}
		}
	}
}

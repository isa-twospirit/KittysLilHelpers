package com.github.isatwospirit.kittyslilhelpers.command.argument;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentOfflinePlayer extends CommandArgument {

	public ArgumentOfflinePlayer() {
		super(OfflinePlayer.class);
	}

	@Override
	public OfflinePlayer getOwningPlayer() {
		return this.getValue();
	}
	
	@Override
	public String getFormattedName() {
		return this.getName();
	}

	@Override
	public OfflinePlayer getValue() {
		return (OfflinePlayer)super.getValue();
	}

	@Override
	public OfflinePlayer getDefaultValue() {
		return (OfflinePlayer)super.getDefaultValue();
	}
	
	@Override
	public ArgumentOfflinePlayer clone(){
		ArgumentOfflinePlayer result = new ArgumentOfflinePlayer();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}
	
	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		OfflinePlayer pFound = null;
		Server s = this.getOption().getCommand().getOwningPlugin().getServer();
		try{
			pFound = s.getOfflinePlayer(UUID.fromString(args[startIndex]));
		}catch(Exception e1){
			try{
				for(Player pTemp : s.getOnlinePlayers()){
					if(pTemp.getName().compareToIgnoreCase(args[startIndex])==0){
						pFound = pTemp;
						break;
					}
						
				}
				if(pFound==null){
					for(OfflinePlayer pTemp : s.getOfflinePlayers()){
						if(pTemp.getName().compareTo(args[startIndex])==0){
							pFound = pTemp;
							break;
						}
					}
				}
			}catch(Exception e2){
				pFound = null;
			}
		}
		if(pFound!=null){
			if(defaultValues.hasDefault(ArgumentOfflinePlayer.class)==false)
				defaultValues.setDefault(ArgumentOfflinePlayer.class, (OfflinePlayer)pFound);
			this.setValue((OfflinePlayer)pFound);
			this.setFindValueResult(FindValueResult.SUCCESS);
			this.setFindValueMessage(null);
			return 1;
		}else{
			this.setValue(null);
			this.setFindValueResult(FindValueResult.FAILED_NOT_FOUND);
			this.setFindValueMessage("OfflinePlayer with name or UUID " + args[startIndex] + " does not exist.");
			return 0;
		}		
	}

}

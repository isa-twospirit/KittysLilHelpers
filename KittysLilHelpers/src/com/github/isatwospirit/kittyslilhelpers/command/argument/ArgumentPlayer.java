package com.github.isatwospirit.kittyslilhelpers.command.argument;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentPlayer extends CommandArgument{

	public ArgumentPlayer() {
		super(Player.class);
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
	public Player getValue() {
		return (Player)super.getValue();
	}

	@Override
	public Player getDefaultValue() {
		return (Player)super.getDefaultValue();
	}
	
	@Override
	public ArgumentPlayer clone(){
		ArgumentPlayer result = new ArgumentPlayer();
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		return result;
	}
	
	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		Player pFound = null;
		Server s = this.getOption().getCommand().getOwningPlugin().getServer();
		try{
			pFound = s.getPlayer(UUID.fromString(args[startIndex]));
		}catch(Exception e1){
			try{
				for(Player pTemp : s.getOnlinePlayers()){
					if(pTemp.getName().compareTo(args[startIndex])==0){
						pFound = pTemp;
						break;
					}
				}
				if(pFound==null){
					for(Player pTemp : s.getOnlinePlayers()){
						if(pTemp.getName().compareToIgnoreCase(args[startIndex])==0){
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
			if(defaultValues.hasDefault(ArgumentPlayer.class)==false)
				defaultValues.setDefault(ArgumentPlayer.class, pFound);
			if(defaultValues.hasDefault(ArgumentOfflinePlayer.class)==false)
				defaultValues.setDefault(ArgumentOfflinePlayer.class, (OfflinePlayer)pFound);
			this.setValue(pFound);
			this.setFindValueResult(FindValueResult.SUCCESS);
			this.setFindValueMessage(null);
			return 1;
		}else{
			this.setValue(null);
			this.setFindValueResult(FindValueResult.FAILED_NOT_FOUND);
			this.setFindValueMessage("Player with name or UUID " + args[startIndex] + " does not exist.");
			return 0;
		}		
	}
}

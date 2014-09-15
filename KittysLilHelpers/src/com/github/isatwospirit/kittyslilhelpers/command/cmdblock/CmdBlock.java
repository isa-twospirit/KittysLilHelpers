 package com.github.isatwospirit.kittyslilhelpers.command.cmdblock;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentMultiString;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentOfflinePlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentWorld;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class CmdBlock extends CommandEx{
	
	public CmdBlock(){
		super("cmdblock");
		this.setDescription("Allows manipulation of CommandBlocks in Survival Mode");
	}

	@Override
	public String getLongDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent bpe){
		try{
			if(bpe.getBlock().getType()==Material.COMMAND){
				CommandBlock cb = (CommandBlock) bpe.getBlock().getState();
				if(bpe.getItemInHand().hasItemMeta()){
					List<String>lore = bpe.getItemInHand().getItemMeta().getLore();
					if(lore.size()>0){
						String cmd = "";
						for(int i=0;i<lore.size();i++){
							cmd += lore.get(i);
						}
						cb.setCommand(cmd);
						cb.update(true);
					}
				}
				this.doSetOwner(cb, bpe.getPlayer());
			}	
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		Location l = (Location)option.getArgument("coord").getValue();
		if(l.getBlock().getType() != Material.COMMAND){
			sender.sendMessage("No commandblock at given coordinates " + Utils.formatError(Utils.getLocationText(l)));
		}else{
			CommandBlock cmdBlock = (CommandBlock)l.getBlock().getState();
			if(option.getName().compareToIgnoreCase("setcmd")==0){
				if(this.doSetCommand(cmdBlock, (String)option.getArgument("command").getValue())){
					sender.sendMessage("Command updated:");
					sender.sendMessage(this.getCmdBlockInfo(cmdBlock));
				}else{
					sender.sendMessage("Setting the command failed.");
				}
			}else if(option.getName().compareToIgnoreCase("setname")==0){	
				if(this.doSetName(cmdBlock, (String)option.getArgument("name").getValue())){
					sender.sendMessage("Name updated:");
					sender.sendMessage(this.getCmdBlockInfo(cmdBlock));
				}else{
					sender.sendMessage("Setting the name failed.");
				}
			}else if(option.getName().compareToIgnoreCase("setowner")==0){
				if(this.doSetOwner(cmdBlock, (OfflinePlayer)option.getArgument("newowner").getValue())){
					sender.sendMessage("Owner changed:");
					sender.sendMessage(this.getCmdBlockInfo(cmdBlock));
				}else{
					sender.sendMessage("Setting the owner failed.");
				}
			}else if(option.getName().compareToIgnoreCase("info")==0){
				sender.sendMessage(this.getCmdBlockInfo(cmdBlock));
				return true;
			}else if(option.getName().compareToIgnoreCase("break")==0){
				return this.doBreak(cmdBlock);
			}	
		}		
		return true;
	}

	@Override
	protected boolean doInitialize() {
		CommandOption option = null;
		option = this.addOption("setcmd", "Sets the command stored in a CommandBlock", null, "edit", true);
		option.addArgument(new ArgumentWorld(), "World", "World the CommandBlock resides in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of the CommandBlock.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentMultiString(), "Command", "Command", OptionalStyle.REQUIRED);
		option = this.addOption("setname", "Sets the name of a CommandBlock", null, "edit", true);
		option.addArgument(new ArgumentWorld(), "World", "World the CommandBlock resides in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of the CommandBlock.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentMultiString(), "Name", "New name, may include spaces.", OptionalStyle.REQUIRED);
		option = this.addOption("setowner", "Sets the owner of a CommandBlock", null, "edit", true);
		option.addArgument(new ArgumentWorld(), "World", "World the CommandBlock resides in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of the CommandBlock.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentOfflinePlayer(), "NewOwner", "Name or UUID of new owner.", OptionalStyle.REQUIRED);
		option = this.addOption("info", "Displays information about a CommandBlock", null, "info", false);
		option.addArgument(new ArgumentWorld(), "World", "World the CommandBlock resides in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of the CommandBlock.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("break", "\"Mines\" a commandblock, retaining name and command", null, "edit", true);
		option.addArgument(new ArgumentWorld(), "World", "World the CommandBlock resides in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of the CommandBlock.", OptionalStyle.OPTIONAL_IF_DEFAULT);

		return false;
	}
 	
	private boolean doSetName(CommandBlock cb, String name){
		try{
			cb.setName(name);
			cb.update(true);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean doSetCommand(CommandBlock cb, String command){
		try{
			if(!command.startsWith("/"))
				command = "/" + command;
			cb.setCommand(command);
			cb.update(true);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean doSetOwner(CommandBlock cb, OfflinePlayer owner){
		try{
			System.out.println("doSetOwner");
			if(owner==null)
				return false;
			this.getOwningPlugin().setBlockOwner(cb.getLocation(), owner);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private boolean doBreak(CommandBlock cb){
		try{
			ItemStack drop = new ItemStack(Material.COMMAND, 1);
			ItemMeta meta = drop.getItemMeta();
			meta.setDisplayName(cb.getName());
			meta.setLore(Arrays.asList(cb.getCommand()));
			drop.setItemMeta(meta);
			this.getOwningPlugin().resetBlockOwner(cb.getLocation());
			cb.getLocation().getWorld().dropItem(cb.getLocation(), drop);
			cb.getLocation().getBlock().setType(Material.AIR);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private String getCmdBlockInfo(CommandBlock cb){
		String msg = "Commandblock at " + Utils.getLocationText(cb.getLocation(), true) + ":\n";
		msg += "Name:    " + cb.getName() + "\n" + "Owner:   ";
		OfflinePlayer p = this.getOwningPlugin().getBlockOwner(cb.getLocation());
		if(p==null)
			msg += "(unknown)\n";
		else
			msg += p.getName() + "\n";
		msg += "Command: " + cb.getCommand();
		return msg;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
}

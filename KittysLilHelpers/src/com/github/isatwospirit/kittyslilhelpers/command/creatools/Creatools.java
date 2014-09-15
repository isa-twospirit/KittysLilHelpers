package com.github.isatwospirit.kittyslilhelpers.command.creatools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentInteger;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject;
import com.github.isatwospirit.kittyslilhelpers.command.creatools.CreaTool.BlockEvent;

public class Creatools extends CommandEx{

	private Map<Player, CreaTool>activeTools = new HashMap<Player, CreaTool>();
	
	public Creatools(){
		super("creatools");
		this.setDescription("Adds some building helpers to creative mode");
	}
	
	@Override
	public String getLongDescription(){
		return this.getDescription();
	}

	private Map<Player, CreaTool> getActiveTools(){
		return this.activeTools;
	}
	
	private void stopTools(Player p){
		if(this.getActiveTools().containsKey(p)){
			if(this.getActiveTools().get(p).canRun())
				p.sendMessage("Stopped your " + this.getActiveTools().get(p).getName() + ".");
			this.getActiveTools().remove(p);
		}
	}
	
	private void startTool(CreaTool c){
		stopTools(c.getOwner());
		if(c.getOwner().getGameMode()==GameMode.CREATIVE){
			this.getActiveTools().put(c.getOwner(), c);
			c.getOwner().sendMessage("Activated your " + c.getName()+ ".");			
		}else{
			c.getOwner().sendMessage("CreaTools may only be used in creative mode.");
		}
	}
	
	private boolean runTool(Player p, Block startBlock, BlockEvent event){
		if(this.getActiveTools().containsKey(p)){
			if(p.getGameMode()==GameMode.CREATIVE){
				CreaTool current = this.getActiveTools().get(p);
				if(current.canRun()){
					if(current.prepareRun(startBlock, event)){
						Thread t = new Thread(current);
						t.run();
						return true;
					}
				}
			}else{
				p.sendMessage("CreaTools may only be used in creative mode.");
			}
		}
		return false;
	}
	
	private boolean undoTool(Player p){
		if(this.getActiveTools().containsKey(p)){
			CreaTool current = this.getActiveTools().get(p);
			if(current.canUndo()){
				if(current.undo()){
					p.sendMessage("Reverted your " + current.getName() + ".");
				}else{
					p.sendMessage("Unable to revert your " + current.getName() + ".");
				}
			}else{
				p.sendMessage("Your " + current.getName() + " cannot be undone.");				
			}
		}
		return false;
	}
	
	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		if(sender instanceof Player){
			if(option.getName().compareToIgnoreCase("flood")==0){
				FloodFill newFF = new FloodFill((Player)sender, 
						(Integer)option.getArgument("maxrange").getValue(),
						(FloodFill.Plane)option.getArgument("plane").getValue());
				this.startTool(newFF);
			}else if(option.getName().compareToIgnoreCase("agify")==0){
				Agify newAg = new Agify((Player)sender,
						(Integer)option.getArgument("Percent").getValue());
				this.startTool(newAg);
			}else if(option.getName().compareToIgnoreCase("cancel")==0){
				this.stopTools((Player)sender);
			}else if(option.getName().compareToIgnoreCase("undo")==0){
				this.undoTool((Player)sender);
			}else if(option.getName().compareToIgnoreCase("status")==0){
				if(this.activeTools.containsKey((Player)sender)){
					sender.sendMessage("Active CreaTool: " + this.activeTools.get((Player)sender).getName() + ".");
				}else{
					sender.sendMessage("No active CreaTool.");
				}
			}
		}else{
			sender.sendMessage("This command can only be issued by a player in creative mode.");
		}
		return true;
	}

	@Override
	protected boolean doInitialize() {
		CommandOption option = this.addOption("flood", "Switches to FloodFill-mode.", null);
		option.addArgument(new ArgumentInteger(), "MaxRange", "Maximum range for floodfill, -1 for default.", OptionalStyle.OPTIONAL, 200, "");
		option.addArgument(new ArgumentListedObject(FloodFill.Plane.DOWN), "Plane", "Orientation of floodfill", OptionalStyle.OPTIONAL, FloodFill.Plane.Y, "");
		option = this.addOption("agify", "Agifies walls made of cobblestone and stone bricks.", null);
		option.addArgument(new ArgumentInteger(), "Percent", "Percentage of agification to be applied.", OptionalStyle.OPTIONAL, 25, "");
		this.addOption("cancel", "Cancels any creatools registered to issueing player", null, null, false);
		this.addOption("status", "DisCreaTool", null, null, false);
		this.addOption("undo", "Reverts all changes performed by your last use of a CreaTool", null, null, false);
		return true;
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent pge){
		if(pge.getNewGameMode()!=GameMode.CREATIVE)
			this.stopTools(pge.getPlayer());
	}
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent pie){
		this.runTool(pie.getPlayer(), pie.getClickedBlock(), BlockEvent.INTERACT);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent bpe){
		this.runTool(bpe.getPlayer(), bpe.getBlockPlaced(), BlockEvent.PLACE);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent bbe){
		this.runTool(bbe.getPlayer(), bbe.getBlock(), BlockEvent.DESTROY);
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent pbe){
		try{
			if(this.getActiveTools().containsKey(pbe.getPlayer())){
				Location l = pbe.getBlockClicked().getLocation();
				BlockFace b = pbe.getBlockFace();
				l.add(b.getModX(), b.getModY(), b.getModZ());
				pbe.setCancelled(true);
				if(pbe.getBucket().name().contains("LAVA")){
					l.getBlock().setType(Material.LAVA);
				}else if(pbe.getBucket().name().contains("WATER")){
					l.getBlock().setType(Material.WATER);
				}else{
					System.out.println(pbe.getBucket().name());
				}
				this.runTool(pbe.getPlayer(), l.getBlock(), BlockEvent.PLACE);
				
			}
		}catch(Exception e){
			System.out.println("onBucketEmpty: " + e.getMessage());
		}
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent pbe){
		try{
			if(this.runTool(pbe.getPlayer(), pbe.getBlockClicked(), BlockEvent.DESTROY)){
				pbe.setCancelled(true);
			}
		}catch(Exception e){
			System.out.println("onBucketFill: " + e.getMessage());
		}
	}
}

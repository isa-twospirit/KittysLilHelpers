package com.github.isatwospirit.kittyslilhelpers.command.sort;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject.ItemExistanceType;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentOfflinePlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentWorld;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class Sort extends CommandEx {
	private SortSources sources = null; 
	private SortDestinations destinations = null;

	//Constructor
	public Sort(){
		super("sort");
		this.setDescription("For sorting chests and maintenance of SortSources and Destinations");
	}

	@Override
	public String getLongDescription(){
		return this.getDescription();
	}

	public SortSources getSources(){
		if(this.sources==null){
			this.getOwningPlugin().getLogger().info("Loading SortSources...");
			this.sources = new SortSources(this.getConfiguration().getConfigurationSection(Utils.SECTION_SOURCES, true));
			this.getOwningPlugin().getLogger().info("Done, " + this.sources.size() + " SortSources loaded.");
		}
		return this.sources;
	}	
	public SortDestinations getDestinations(){
		if(this.destinations==null){
			this.getOwningPlugin().getLogger().info("Loading SortDestinations...");
			this.destinations= new SortDestinations(this.getConfiguration().getConfigurationSection(Utils.SECTION_DESTINATIONS, true));
			this.getOwningPlugin().getLogger().info("Done, " + this.destinations.size() + " SortDestinations loaded.");
		}
		return this.destinations;
	}
		
	//Command implementations
	private boolean doChestSort(CommandSender sender, SortSource source, SortDestination destination){
		if(source.getOwner().getUniqueId() != destination.getOwner().getUniqueId()){
			sender.sendMessage("SortSource and SortDestination need to belong to the same player.");
		}else if(destination.getInventories().size() == 0){
			sender.sendMessage("SortDestination does not contain any chests.");
		}else{
			Inventory sourceInv = source.getInventory();
			Set<Location>destLocations = destination.getInventories().keySet();
			
			//this.getLogger().info("Found " + destLocations.size() + " possible destinations.");
			for(int i=0;i<sourceInv.getContents().length;i++){
				ItemStack current=sourceInv.getContents()[i];
				if(current!=null){
					for(Location destLoc : destLocations){
						Inventory currentDest = Utils.getInventoryFrom(destLoc);
						if(currentDest.contains(current.getType())){
							//this.getLogger().info("Inventory at " + destLoc + " contains " + current.getType() + ", add it.");
							HashMap<Integer, ItemStack> result=currentDest.addItem(current);
							if(result.isEmpty()){
								sourceInv.clear(i);
								//this.getLogger().info("Returned an empty Stack, so everything ended in chest - next one.");
								break;
							}else{
								current.setAmount(result.get(0).getAmount());
								//this.getLogger().info("There's still " + result.get(0).getAmount() + " items to be placed, continue.");
							}
						}
					}
				}
			}    			
		}
		return true;
	}
	
	private boolean doShowSourceList(CommandSender sender, OfflinePlayer owner){
		if(owner==null){
			sender.sendMessage("All SortSources:");
			for(SortSource current : this.getSources().all()){
		    	sender.sendMessage(current.getDescription(true));
			}
		}else{
			sender.sendMessage("SortSources owned by " + owner.getName() + ":");
			for(SortSource current : this.getSources().all()){
				if(current.getOwner().getUniqueId() == owner.getUniqueId())
					sender.sendMessage(current.getDescription(true));
			}
		}
		return true;
	}
	
	private boolean doShowSourceInfo(CommandSender sender, SortSource source){
		sender.sendMessage("\n " + source.getDescription(false));
		return true;
	}
	
	private boolean doSourceAdd(CommandSender sender, String name, OfflinePlayer owner, Location location){
		if(this.getSources().contains(name)){
			sender.sendMessage("SortSource " + Utils.formatError(name) + " already exists.");
		}else if(Utils.getInventoryFrom(location)==null){
			sender.sendMessage("There's no chest at given coordinate " + Utils.getLocationText(location, true) + ".");
		}else{
			SortSource newSource = this.getSources().add(name, owner, location);
			sender.sendMessage("SortSource created:\n" + newSource.getDescription(false));
		}
		return true;
	}
	
	private boolean doSourceUpdate(CommandSender sender, SortSource source, Location newLocation){
		if(Utils.getInventoryFrom(newLocation)==null){
			sender.sendMessage("There's no chest at given coordinate " + Utils.getLocationText(newLocation, true) + ".");
		}else{
			source.setLocation(newLocation);
			sender.sendMessage("SortSource updated:\n" + source.getDescription(false));
		}
		return true;
	}
	
	private boolean doSourceSetOwner(CommandSender sender, SortSource source, OfflinePlayer newOwner){
		source.setOwner(newOwner);
		sender.sendMessage("SortSource updated:\n" + source.getDescription(false));
		return true;
	}
	
	private boolean doSourceDelete(CommandSender sender, SortSource source){
		this.getSources().delete(source);
		sender.sendMessage("SortSource " + source.getDisplayName() + " deleted.");
		return true;
	}
	
	private boolean doShowDestinationList(CommandSender sender, OfflinePlayer owner){
		if(owner==null){
			sender.sendMessage("All SortDestinations:");
			for(SortDestination current : this.getDestinations().all()){
		    	sender.sendMessage(current.getDescription(true));
			}
		}else{
			sender.sendMessage("SortDestinations owned by " + owner.getName() + ":");
			for(SortDestination current : this.getDestinations().all()){
				if(current.getOwner().getUniqueId() == owner.getUniqueId())
					sender.sendMessage(current.getDescription(true));
			}
		}
		return true;
	}
	
	private boolean doShowDestinationInfo(CommandSender sender, SortDestination destination){
		sender.sendMessage("\n" + destination.getDescription(false));
		return true;
	}
	
	private boolean doDestinationAdd(CommandSender sender, String newName, OfflinePlayer owner, Location coord1, Location coord2){
		if(this.getDestinations().contains(newName)){
			sender.sendMessage("SortDestination " + Utils.formatError(newName) + " already exists.");
		}else{
			SortDestination newDest = this.getDestinations().add(newName, owner, coord1, coord2);
			sender.sendMessage("SortDestination created:\n" + newDest.getDescription(false));
		}
		return true;
	}
	
	private boolean doDestinationUpdate(CommandSender sender, SortDestination destination, Location coord1, Location coord2){
		destination.setLocation1(coord1);
		destination.setLocation2(coord2);
		sender.sendMessage("SortDestination updated:\n" + destination.getDescription(false));
		return true;
	}

	private boolean doDestinationUpdate2(CommandSender sender, SortDestination destination, Location coord2){
		destination.setLocation2(coord2);
		sender.sendMessage("SortDestination updated:\n" + destination.getDescription(false));
		return true;
	}

	private boolean doDestinationSetOwner(CommandSender sender, SortDestination destination, OfflinePlayer newOwner){
		destination.setOwner(newOwner);
		sender.sendMessage("SortDestination updated:\n" + destination.getDescription(false));
		return true;
	}
	
	private boolean doDestinationDelete(CommandSender sender, SortDestination destination){
		this.getDestinations().delete(destination);
		sender.sendMessage("SortDestination " + destination.getDisplayName() + " deleted.");
		return true;
	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent bbe){
		try{
			if(bbe.getBlock().getType()==Material.CHEST || bbe.getBlock().getType()==Material.TRAPPED_CHEST){
				Inventory i = Utils.getInventoryFrom(bbe.getBlock().getLocation());
				//this.getOwningPlugin().getLogger().info("Checking inventory " + i.toString());
				SortSource source = this.getSources().get(i);
				if(source!=null){
					if(source.getOwner().getUniqueId().equals(bbe.getPlayer().getUniqueId()) || bbe.getPlayer().isOp()){
		    			bbe.getPlayer().sendMessage("Removing source " + ChatColor.RED + source.getName() + ChatColor.WHITE + ".");
		    			source.delete();
	    			}else{
		    			bbe.getPlayer().sendMessage("You cannot remove this chest.");
		    			bbe.setCancelled(true);
					}
				}
			}
		}catch(Exception e){
			this.getOwningPlugin().getLogger().info(e.getMessage());
		}
	}
	
	//Overrides from KittysCommand
	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		if(option.getName().compareToIgnoreCase("chests")==0)
			return doChestSort(sender, (SortSource)option.getArgument("SortSource").getValue(), (SortDestination)option.getArgument("SortDestination").getValue());
		else if(option.getName().compareToIgnoreCase("srclist")==0)
			return doShowSourceList(sender, (OfflinePlayer)option.getArgument("Owner").getValue());
		else if(option.getName().compareToIgnoreCase("srcinfo")==0)
			return doShowSourceInfo(sender, (SortSource)option.getArgument("SortSource").getValue());
		else if(option.getName().compareToIgnoreCase("srcadd")==0)
			return doSourceAdd(sender, (String)option.getArgument("Name").getValue(), (OfflinePlayer)option.getArgument("Owner").getValue(), (Location)option.getArgument("Coord").getValue());
		else if(option.getName().compareToIgnoreCase("srcupdate")==0)
			return doSourceUpdate(sender, (SortSource)option.getArgument("SortSource").getValue(), (Location)option.getArgument("Coord").getValue());
		else if(option.getName().compareToIgnoreCase("srcsetowner")==0)
			return doSourceSetOwner(sender, (SortSource)option.getArgument("SortSource").getValue(), (OfflinePlayer)option.getArgument("NewOwner").getValue());
		else if(option.getName().compareToIgnoreCase("srcdelete")==0)
			return doSourceDelete(sender, (SortSource)option.getArgument("SortSource").getValue());
		else if(option.getName().compareToIgnoreCase("destlist")==0)
			return doShowDestinationList(sender, (OfflinePlayer)option.getArgument("Owner").getValue());
		else if(option.getName().compareToIgnoreCase("destinfo")==0)
			return doShowDestinationInfo(sender, (SortDestination)option.getArgument("SortDestination").getValue());
		else if(option.getName().compareToIgnoreCase("destadd")==0)
			return doDestinationAdd(sender, (String)option.getArgument("Name").getValue(), (OfflinePlayer)option.getArgument("Owner").getValue(), (Location)option.getArgument("Coord1").getValue(), (Location)option.getArgument("Coord2").getValue());
		else if(option.getName().compareToIgnoreCase("destupdate1")==0)
			return doDestinationUpdate(sender, (SortDestination)option.getArgument("SortDestination").getValue(), (Location)option.getArgument("Coord1").getValue(), (Location)option.getArgument("Coord2").getValue());
		else if(option.getName().compareToIgnoreCase("destupdate2")==0)
			return doDestinationUpdate2(sender, (SortDestination)option.getArgument("SortDestination").getValue(), (Location)option.getArgument("Coord2").getValue());
		else if(option.getName().compareToIgnoreCase("destsetowner")==0)
			return doDestinationSetOwner(sender, (SortDestination)option.getArgument("SortDestination").getValue(), (OfflinePlayer)option.getArgument("NewOwner").getValue());
		else if(option.getName().compareToIgnoreCase("destdelete")==0)
			return doDestinationDelete(sender, (SortDestination)option.getArgument("SortDestination").getValue());
		return false;
	}

	@Override
	protected boolean doInitialize() {
		CommandOption option = null;
		option = this.addOption("chests", "Performs sorting from sortsource to sortdestination", null, "chests", true);
		option.addArgument(new ArgumentListedObject(this.getSources()), "SortSource", "SortSource used in this operation", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "SortDestination used in this operation", OptionalStyle.REQUIRED);

		option = this.addOption("srclist", "Displays known SortSources", null, "showinfo", false);
		option.addArgument(new ArgumentOfflinePlayer(), "Owner", "Name or UUID of Player owning listed SortSources", OptionalStyle.OPTIONAL);

		option = this.addOption("srcinfo", "Displays information on a SortSource", null, "showinfo", false);
		option.addArgument(new ArgumentListedObject(this.getSources()), "SortSource", "Name of SortSource to display information on.", OptionalStyle.REQUIRED);

		option = this.addOption("srcadd", "Creates a new SortSource.", "Long description here", "srcedit", true);
		option.addArgument(new ArgumentListedObject(this.getSources(), ItemExistanceType.ITEM_MUST_NOT_EXIST), "Name", "Name of new SortSource.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentOfflinePlayer(), "Owner", "Name of UUID of Player this SortSource should belong to. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentWorld(), "World", "Name or UUID of World this SortSource should be created in. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinate. Can be omitted if issued by a player targetting a chest.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		
		option = this.addOption("srcupdate", "Updates an existing SortSource.", null, "srcedit", true);
		option.addArgument(new ArgumentListedObject(this.getSources()), "SortSource", "Name of SortSource to update.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentWorld(), "World", "Name or UUID of World this SortSource should exist in. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinate. Can be omitted if issued by a player targetting a chest.", OptionalStyle.OPTIONAL_IF_DEFAULT);				

		option = this.addOption("srcsetowner", "Changes the owner of a SortSource.", null, "srcedit", true);
		option.addArgument(new ArgumentListedObject(this.getSources()), "SortSource", "Name of SortSource to update.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentOfflinePlayer(), "NewOwner", "Name or UUID of new owner.", OptionalStyle.REQUIRED);

		option = this.addOption("srcdelete", "Deletes a SortSource", null, "srcedit", true);
		option.addArgument(new ArgumentListedObject(this.getSources()), "SortSource", "Name of SortSource to delete.", OptionalStyle.REQUIRED);

		option = this.addOption("destlist", "Displays known SortDestinations", null, "showinfo", false);
		option.addArgument(new ArgumentOfflinePlayer(), "Owner", "Name or UUID of Player owning listed SortDestinations", OptionalStyle.OPTIONAL);

		option = this.addOption("destinfo", "Displays information on a SortDestinations", null, "showinfo", false);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "Name of SortDestination to display information on.", OptionalStyle.REQUIRED);

		option = this.addOption("destadd", "Creates a new SortDestination.", null, "destedit", true);
		option.addArgument(new ArgumentListedObject(this.getDestinations(), ItemExistanceType.ITEM_MUST_NOT_EXIST), "Name", "Name of SortDestination to create.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentOfflinePlayer(), "Owner", "Name of UUID of Player this SortDestination should belong to. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentWorld(), "World", "Name or UUID of World this SortDestination should be created in.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord1", "First Coordinate.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord2", "Second Coordinate.", OptionalStyle.OPTIONAL);				

		option = this.addOption("destupdate", "Updates Coordinate an existing SortDestination.", null, "destedit", true);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "Name of SortDestination to update.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentWorld(), "World", "Name or UUID of World this SortDestination should be created in.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord1", "Coordinate 1. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);				
		option.addArgument(new ArgumentLocation(), "Coord2", "Coordinate 2. When omitted, the SortDestinations' range is set to zero.", OptionalStyle.OPTIONAL);			

		option = this.addOption("destupdate2", "Updates Coordinate 2 of an existing SortDestination.", null, "destedit", true);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "Name of SortDestination to update.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentLocation(), "Coord2", "Coordinate 2. Can be omitted if issued by a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);			

		option = this.addOption("destsetowner", "Changes the owner of a SortDestination.", null, "destedit", true);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "Name of SortDestination to update.", OptionalStyle.REQUIRED);				
		option.addArgument(new ArgumentOfflinePlayer(), "NewOwner", "Name or UUID of new owner.", OptionalStyle.REQUIRED);

		option = this.addOption("destdelete", "Deletes a SortDestination", null, "destedit", true);
		option.addArgument(new ArgumentListedObject(this.getDestinations()), "SortDestination", "Name of SortDestination to delete.", OptionalStyle.REQUIRED);
		
		return true;
	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

}

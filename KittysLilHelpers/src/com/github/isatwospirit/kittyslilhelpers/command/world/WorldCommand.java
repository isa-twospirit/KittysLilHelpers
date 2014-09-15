package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentBoolean;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentInteger;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentPlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentWorld;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject.ItemExistanceType;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentOfflinePlayer;

public class WorldCommand extends CommandEx {
	private List<Player> justLoggedIn = new LinkedList<Player>();
	private Map<Player, World> playerWorlds = new HashMap<Player, World>();
	
	public WorldCommand(){
		super("world");
		this.setDescription("Configuration and maintenance of different worlds");
	}
	
	@Override
	public String getLongDescription() {
		return this.getDescription();
	}

	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		String out = "";
		if(option.getName().compareToIgnoreCase("show")==0){
			if(option.getArgument("world").getValue()==null){
				out = "Worlds:";
				for(WorldInfo w : WorldInfos.getInstance().values()){
					out += "\n" + w.getDescription(true);
				}
			}else{
				out = "World " + ((WorldInfo)option.getArgument("world").getValue()).getDescription(false);
			}
			sender.sendMessage(out);
		}else if(option.getName().compareToIgnoreCase("showgroup")==0){
			if(option.getArgument("group").getValue()==null){
				out = "World Groups:";
				for(WorldGroup w : WorldGroups.getInstance().values()){
					out += "\n" + w.getDescription(true);
				}
			}else{
				out = "World Group " + ((WorldGroup)option.getArgument("group").getValue()).getDescription(false);
			}
			sender.sendMessage(out);
		}else if(option.getName().compareToIgnoreCase("addgroup")==0){
			WorldGroup result = WorldGroups.getInstance().add((String)option.getArgument("group").getValue(), 
															  (GameMode)option.getArgument("gamemode").getValue());
			sender.sendMessage("WorldGroup created:\n" + result.getDescription(false));
			return true;
		}else if(option.getName().compareToIgnoreCase("setgroupmode")==0){
			WorldGroup w = (WorldGroup)option.getArgument("group").getValue();
			w.setGameMode((GameMode)option.getArgument("gamemode").getValue());
			sender.sendMessage("WorldGroup updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("setgroupowner")==0){
			WorldGroup w = (WorldGroup)option.getArgument("group").getValue();
			w.setOwner((OfflinePlayer)option.getArgument("newowner").getValue());
			sender.sendMessage("WorldGroup updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("deletegroup")==0){
			WorldGroup w = (WorldGroup)option.getArgument("group").getValue();
			Integer result = WorldGroups.getInstance().delete(w);
			if(result==0){
				sender.sendMessage("WorldGroup " + w.getDisplayName() + " deleted.");
			}else{
				sender.sendMessage("Unable to delete WorldGroup " + w.getDisplayName() + " while " + result + " world(s) assigned");
			}
		}else if(option.getName().compareToIgnoreCase("setgroup")==0){
			WorldInfo w = (WorldInfo)option.getArgument("world").getValue();
			w.setWorldGroup((WorldGroup)option.getArgument("group").getValue());
			sender.sendMessage("World updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("setowner")==0){
			WorldInfo w = (WorldInfo)option.getArgument("world").getValue();
			w.setOwner((OfflinePlayer)option.getArgument("newowner").getValue());
			sender.sendMessage("World updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("setunload")==0){
			WorldInfo w = (WorldInfo)option.getArgument("world").getValue();
			w.doUnloadWhenEmpty((Boolean)option.getArgument("doautounload").getValue());
			sender.sendMessage("World updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("setautoload")==0){
			WorldInfo w = (WorldInfo)option.getArgument("world").getValue();
			w.canAutoLoad((Boolean)option.getArgument("doautoload").getValue());
			sender.sendMessage("World updated:\n" + w.getDescription(false));
		}else if(option.getName().compareToIgnoreCase("go")==0){
			return this.goWorld(sender, 
						((ArgumentPlayer)option.getArgument("player")).getValue(), 
						((ArgumentWorld)option.getArgument("world")).getValue(),
						((ArgumentLocation)option.getArgument("coord")).getValue());
		}else if(option.getName().compareToIgnoreCase("givechronicle")==0){
			return this.giveChronicle(((ArgumentPlayer)option.getArgument("player")).getValue());
		}else if(option.getName().compareToIgnoreCase("giveworldlore")==0){
			WorldInfo wi = (WorldInfo)option.getArgument("world").getValue();
			Player p = (Player)option.getArgument("player").getValue();
			if(wi==null)
				sender.sendMessage("WI NULL");
			if(p==null)
				sender.sendMessage("P NULL");
			wi.giveLoreBook(p);
			return true;
		}else if(option.getName().compareToIgnoreCase("givegrouplore")==0){
			WorldGroup wg = (WorldGroup)option.getArgument("group").getValue();
			Player p = (Player)option.getArgument("player").getValue();
			if(wg==null)
				sender.sendMessage("WG NULL");
			if(p==null)
				sender.sendMessage("P NULL");
			wg.giveLoreBook(p);
			return true;
		}else if(option.getName().compareToIgnoreCase("setrealistictreecut")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setRealisticTreeCut((WorldFlags.TriState)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("setsaplingplantrate")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setSaplingAutoPlantRate((Integer)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("setbigtreerate")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setBigTreeRate((Integer)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("setcropsplantrate")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setCropsAutoPlantRate((Integer)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("setpotatoplantrate")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setPotatoAutoPlantRate((Integer)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("setcarrotplantrate")==0){
			WorldFlagContainer container = (WorldFlagContainer)option.getArgument("scope").getValue();
			container.getWorldFlags().setCarrotAutoPlantRate((Integer)option.getArgument("value").getValue());
			sender.sendMessage("Flags updated - " + container.getWorldFlags().getSummary());
		}else if(option.getName().compareToIgnoreCase("listgen")==0){
			String msg = "Known WorldGenerators:";
			for(Class<?> gen : WorldGenerators.getInstance().values()){
				msg += "\n" + gen.getName();
			}
			sender.sendMessage(msg);
		}
		return false;
	}

	@Override
	protected boolean doInitialize() {
		CommandOption option = this.addOption("show", "List worlds on this server", null, "list", false);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of a world for detailed information", OptionalStyle.OPTIONAL);
		option = this.addOption("setgroup", "Assigns a world to a world group.", null, "worldadmin", true);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of world", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of world group", OptionalStyle.REQUIRED);
		option = this.addOption("setowner", "Sets the owner of a specific world", null, "worldadmin", true);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of world", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentOfflinePlayer(), "NewOwner", "Name or UUID of new owner.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("setunload", "Sets whether world is unloaded automatically when the last player leaves.", null, "worldadmin", true);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of world", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentBoolean(), "DoAutoUnload", "on/off", OptionalStyle.REQUIRED);
		option = this.addOption("setautoload", "Sets whether world is loaded automatically when a player teleports in.", null, "worldadmin", true);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of world", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentBoolean(), "DoAutoLoad", "on/off", OptionalStyle.REQUIRED);
		option = this.addOption("showgroup", "List all world groups on this server", null, "list", false);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of a world group for detailed information", OptionalStyle.OPTIONAL);
		option = this.addOption("addgroup", "Creates a new world group.", null, "worldgroupadmin", true);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance(), ItemExistanceType.ITEM_MUST_NOT_EXIST), "Group", "Name for new group", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentListedObject(GameMode.class), "GameMode", "Default GameMode for this WorldGroup.", OptionalStyle.OPTIONAL, Bukkit.getDefaultGameMode(), "Server's default GameMode.");
		option = this.addOption("setgroupmode", "Set default gamemode for a world group", null, "worldgroupadmin", true);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of World Group", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentListedObject(GameMode.class), "GameMode", "Default GameMode for this WorldGroup.", OptionalStyle.REQUIRED);
		option = this.addOption("setgroupowner", "Set owner of a world group", null, "worldgroupadmin", true);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of World Group", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentOfflinePlayer(), "NewOwner", "Name or UUID of new owner.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("deletegroup", "Deletes a world group", null, "worldgroupadmin", true);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of World Group", OptionalStyle.REQUIRED);
		option = this.addOption("go", "Moves a player to a different world, loading it when unloaded.", null, "teleport", true);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentWorld(true), "World", "Name or UUID of a world.", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates in given world", OptionalStyle.OPTIONAL);
		option = this.addOption("givechronicle", "Passes a copy of the worlds chronicle to a player.", null, "givebook", false);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("giveworldlore", "Passes a player a worlds' lore book (Editable when player owns the world).", null, "givebook", false);
		option.addArgument(new ArgumentListedObject(WorldInfos.getInstance()), "World", "Name of world", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("givegrouplore", "Passes a player a world groups' lore book (Editable when player owns the world).", null, "givebook", false);
		option.addArgument(new ArgumentListedObject(WorldGroups.getInstance()), "Group", "Name of a world group for detailed information", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of a player.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("setrealistictreecut", "Enables or disables \"realistic\" tree cut for a world, world group, or server-wide", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentListedObject(WorldFlags.TriState.class), "Value", "ON, OFF or DEFAULT", OptionalStyle.REQUIRED);
		option = this.addOption("setsaplingplantrate", "Sets how many percent of dropped saplings are planted automatically", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentInteger(), "Value", "0 - 100, -1 for server default", OptionalStyle.REQUIRED);
		option = this.addOption("setbigtreerate", "Sets how many percent of dropped saplings create big trees", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentInteger(), "Value", "0 - 100, -1 for server default", OptionalStyle.REQUIRED);
		option = this.addOption("setcropsplantrate", "Sets how many percent of dropped crops are planted automatically", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentInteger(), "Value", "0 - 100, -1 for server default", OptionalStyle.REQUIRED);
		option = this.addOption("setpotatoplantrate", "Sets how many percent of dropped potatoes are planted automatically", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentInteger(), "Value", "0 - 100, -1 for server default", OptionalStyle.REQUIRED);
		option = this.addOption("setcarrotplantrate", "Sets how many percent of dropped carrots are planted automatically", null, "worldflagadmin", true);
		option.addArgument(new ArgumentListedObject(WorldFlagLookup.getInstance()), "Scope", "world:worldname, group:groupname or default", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentInteger(), "Value", "0 - 100, -1 for server default", OptionalStyle.REQUIRED);
		option = this.addOption("listgen", "Lists all installed ChunkGenerators", null);
		return true;
	}

	@Override
	public void onDisable() {
		// Nothing, I think
	}

	private boolean goWorld(CommandSender sender, Player p, World w, Location l){
		if(l==null){
			l = WorldInfos.getInstance().get(w).getLastLocation(p);
		}
		if(w.getUID()!=p.getWorld().getUID()){
			p.teleport(l);
			boolean sendMsg = true;
			if(sender instanceof Player){
				sendMsg = (((Player)sender).getUniqueId() != p.getUniqueId());
			}
			if(sendMsg)
				p.sendMessage("You have been moved to " + w.getName() + " by " + sender.getName());
		}else{
			sender.sendMessage("Player " + p.getName() + " is already in " + w.getName() + ".");
		}
		return true;
	}

	private boolean giveChronicle(Player p){
		ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		book.setTitle("The Chronicles Of " + Bukkit.getName());
		book.setAuthor("KittysLilHelpers");
		book.setLore(new ArrayList<String>());
		book.getLore().add("All the tales and mysteries");
		book.getLore().add("from this beautiful");
		book.getLore().add("universe.");
		
		book.addPage(ChatColor.BLUE + "Welcome to the Chronicles Of " + Bukkit.getName() + "!\n" + ChatColor.BLACK);
		book.addPage("Index");

		Integer pageNo = 3;
		String index = ChatColor.BLUE + "Index:\n" + ChatColor.BLACK;
		
		for(WorldGroup wg : WorldGroups.getInstance().values()){
			index += "\n" + pageNo + " - " + wg.getName();
			if(wg.hasLore()){
				for(String page : wg.getLore()){
					book.addPage(page);
					pageNo+=1;
				}
			}else{
				book.addPage(ChatColor.BLUE + wg.getName() + ChatColor.BLACK + "\nNot much is known about the worlds of " + wg.getName() + " or how this group was formed.");
				pageNo+=1;
			}
			for(WorldInfo wi : wg.getWorlds()){
				index += "\n  " + pageNo + " - " + wi.getName();
				if(wi.hasLore()){
					for(String page : wi.getLore()){
						book.addPage(page);
						pageNo+=1;
					}
				}else{
					book.addPage(ChatColor.BLUE + wi.getName() + ChatColor.BLACK + "\n...");
					pageNo+=1;
				}
			}
		}
		book.setPage(2, index);
		result.setItemMeta(book);
		if(p.getInventory().addItem(result).isEmpty()==false){
			p.getWorld().dropItem(p.getLocation(), result);
		}
		return true;
	}

	@EventHandler
	public void onPlayerEditBook(PlayerEditBookEvent peb){
		if(peb.getPreviousBookMeta().getDisplayName().startsWith("World Lore Of ")){
			String worldName = peb.getPreviousBookMeta().getDisplayName().substring(14);
			WorldInfo w = WorldInfos.getInstance().get(worldName);
			if(w!=null){
				if(w.getOwner()!=null){
					if(w.getOwner().getUniqueId()==peb.getPlayer().getUniqueId()){
						w.setLore(peb.getNewBookMeta().getPages());
						KittysLilHelpers.logInfo("Updated lore of world " + worldName);
					}
				}
			}
		}else if(peb.getPreviousBookMeta().getDisplayName().startsWith("World Group Lore Of ")){
			String worldGroup = peb.getPreviousBookMeta().getDisplayName().substring(20);
			WorldGroup w = WorldGroups.getInstance().get(worldGroup);
			if(w!=null){
				if(w.getOwner()!=null){
					if(w.getOwner().getUniqueId()==peb.getPlayer().getUniqueId()){
						w.setLore(peb.getNewBookMeta().getPages());
						KittysLilHelpers.logInfo("Updated lore of world group " + worldGroup);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent ple){
		this.justLoggedIn.add(ple.getPlayer());
	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent ple){
		WorldInfos.getInstance().setSpawnBedLocation(ple.getPlayer(), ple.getBed().getLocation());
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent pte){
		if(this.justLoggedIn.contains(pte.getPlayer())){
			this.justLoggedIn.remove(pte.getPlayer());
			WorldGroup wg = WorldInfos.getInstance().get(pte.getTo().getWorld()).getWorldGroup();
			if(pte.getPlayer().getGameMode()!=wg.getGameMode()){
				wg.saveInventoryFor(pte.getPlayer());
				pte.getPlayer().getInventory().clear();
				pte.getPlayer().setGameMode(wg.getGameMode());				
			}
		}else{
			if(pte.getFrom().getWorld().getUID() != pte.getTo().getWorld().getUID()){
				WorldInfos.getInstance().setLastLocation(pte.getPlayer(), pte.getFrom());
			}
		}
		this.playerWorlds.put(pte.getPlayer(), pte.getTo().getWorld());
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent pwe){
		WorldInfo fromWorld = WorldInfos.getInstance().get(pwe.getFrom());
		WorldInfo toWorld = WorldInfos.getInstance().get(pwe.getPlayer().getWorld());
		
		if(fromWorld.getWorldGroup().getName().compareTo(toWorld.getWorldGroup().getName())!=0){
			fromWorld.getWorldGroup().saveInventoryFor(pwe.getPlayer());
			pwe.getPlayer().getInventory().clear();
			if(pwe.getPlayer().getGameMode()==toWorld.getWorldGroup().getGameMode()){
				toWorld.getWorldGroup().restoreInventoryFor(pwe.getPlayer());
			}else{
				pwe.getPlayer().setGameMode(toWorld.getWorldGroup().getGameMode());
			}
		}
		
		if(pwe.getFrom().getPlayers().size()==0){
			String wName = pwe.getFrom().getName();
			Bukkit.getLogger().info("Unloading world " + wName + "...");
			Bukkit.unloadWorld(pwe.getFrom(), true);
			Bukkit.getLogger().info("World" + wName + " unloaded.");
		}
	}

	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent pge){
		WorldGroup group = WorldInfos.getInstance().get(pge.getPlayer().getWorld()).getWorldGroup();
		group.saveInventoryFor(pge.getPlayer());
		pge.getPlayer().getInventory().clear();
		group.restoreInventoryFor(pge.getPlayer(), pge.getNewGameMode());
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent pre){
		if(this.playerWorlds.containsKey(pre.getPlayer())){
			System.out.println("Respawn in: " + this.playerWorlds.get(pre.getPlayer()).getName());
			pre.setRespawnLocation(WorldInfos.getInstance().get(this.playerWorlds.get(pre.getPlayer())).getSpawnBedLocation(pre.getPlayer()));
		}
	}
}

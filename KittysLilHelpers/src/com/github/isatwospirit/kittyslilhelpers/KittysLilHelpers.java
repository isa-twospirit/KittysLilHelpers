package com.github.isatwospirit.kittyslilhelpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.isatwospirit.kittyslilhelpers.bookshelves.BookShelfInventories;
import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.cmdblock.CmdBlock;
import com.github.isatwospirit.kittyslilhelpers.command.creatools.Creatools;
import com.github.isatwospirit.kittyslilhelpers.command.klh.KittysLilHelper;
import com.github.isatwospirit.kittyslilhelpers.command.recipes.Recipes;
import com.github.isatwospirit.kittyslilhelpers.command.sort.Sort;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldCommand;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldFlagContainer;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldFlags;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class KittysLilHelpers extends JavaPlugin implements Listener, WorldFlagContainer{
	public static final String SECTION_OWNERS = "blockowners";

	public static final ChatColor COLOR_TITLE = ChatColor.WHITE;
	public static final ChatColor COLOR_COMMAND = ChatColor.LIGHT_PURPLE;
	public static final ChatColor COLOR_OPTION = ChatColor.BLUE;
	public static final ChatColor COLOR_ARGUMENT = ChatColor.GREEN;
	public static final ChatColor COLOR_OPTIONARG = ChatColor.DARK_GREEN;
	public static final ChatColor COLOR_ERROR = ChatColor.RED;
	
	public static final ChatColor COLOR_SOURCE_CHEST = ChatColor.GREEN;
	public static final ChatColor COLOR_SOURCE_TRAPPED_CHEST = ChatColor.DARK_GREEN;
	public static final ChatColor COLOR_SOURCE_NO_CHEST = ChatColor.DARK_RED;
	public static final ChatColor COLOR_DESTINATION_OK = ChatColor.BLUE;
	public static final ChatColor COLOR_DESTINATION_EMPTY = ChatColor.DARK_BLUE;
	public static final ChatColor COLOR_DESTINATION_INCOMPLETE = ChatColor.DARK_RED;
	
	private Map<String, CommandEx>commands = new HashMap<String, CommandEx>();
	private WorldFlags defaultFlags;
	
	public String getCommandPrefix(){
		return "klh";
	}

	public void onLoad(){
		this.saveDefaultConfig();
		this.addCmd(new KittysLilHelper());
		if(getConfig("settings").getBoolean("worldenabled", true)==true)
			this.addCmd(new WorldCommand());
		if(getConfig("settings").getBoolean("cmdblockenabled", true)==true)
			this.addCmd(new CmdBlock());
		if(getConfig("settings").getBoolean("sortenabled", true)==true)
			this.addCmd(new Sort());
		if(getConfig("settings").getBoolean("recipesenabled", true)==true)
			this.addCmd(new Recipes());
		if(getConfig("settings").getBoolean("creatoolsenabled", true)==true)
			this.addCmd(new Creatools());

		for(CommandEx check : this.getCommands().values()){
			check.onLoad(this, this.getConfigSection(check.getName().toLowerCase()));
		}
	}
		
	public void onEnable(){
		this.getLogger().info("Enabling event listeners and commands...");
		this.getServer().getPluginManager().registerEvents(this, this);
		for(CommandEx cmd : this.getCommands().values()){
			this.getServer().getPluginManager().registerEvents(cmd, this);
			cmd.onEnable();
		}
		BookShelfInventories.getInstance();
		this.getLogger().info("Done.");
	}
	
	public void onDisable(){
		this.getLogger().info("Disabling commands...");
		for(CommandEx cmd : this.getCommands().values()){
			cmd.onDisable();
		}
		this.getLogger().info("Done.");

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		sender.sendMessage("onCommand in KLH - DaFuq? - " + commandLabel);
		return true;
    }
	
	private Map<String, CommandEx> getCommands(){
		return this.commands;
	}
	
	public CommandEx getCmd(String name){
		if(this.getCommands().containsKey(name.toLowerCase()))
			return this.getCommands().get(name.toLowerCase());
		else
			return null;
	}
	
	public boolean addCmd(CommandEx cmd){
		if(this.getCommands().containsKey(cmd.getName().toLowerCase()))
			return false;

		this.getCommands().put(cmd.getName().toLowerCase(), cmd);
		return true;		
	}
	
	private ConfigSection getConfigSection(String name){
		ConfigurationSection temp = this.getConfig().getConfigurationSection(name);
    	if(temp==null){
    		temp = this.getConfig().createSection(name);
    		this.saveConfig();
    	}
    	return new ConfigSection(null, temp);
	}

/*    private boolean doCreate(World w, Player p, String[] args){
    	Location l = null;
    	if(args.length>2){
    		l = new Location(w, Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])); 
    	}else if(p!=null){
    		l = p.getLocation();
    	}
    	
    	try{
    		LabyrinthGenerator gen = new TemplateLabyrinthGenerator();
    		Labyrinth lab = gen.generate(1, 1, null);
    		LabyrinthBuilder builder = new StoneWallLabyrinthBuilder();
    		return builder.build(l, lab);
    	}catch(Exception e){
    		this.getLogger().info(e.getMessage());
    		return false;
    	}
    }
*/
    
    public boolean setBlockOwner(Block b, OfflinePlayer owner){
    	return setBlockOwner(b.getLocation(), owner);
    }
    
    public OfflinePlayer getBlockOwner(Block b){
    	return getBlockOwner(b.getLocation());
    }
    
    public boolean resetBlockOwner(Block b){
    	return resetBlockOwner(b.getLocation());
    }
    
    public boolean setBlockOwner(Location l, OfflinePlayer owner){
    	this.getConfigSection(SECTION_OWNERS).set(Utils.getLocationText(l), "" + owner.getUniqueId());
    	this.saveConfig();
    	return true;
    }
    
    public OfflinePlayer getBlockOwner(Location l){
    	try{
    		UUID uid = UUID.fromString(this.getConfigSection(SECTION_OWNERS).getString(Utils.getLocationText(l)));
    		return this.getServer().getOfflinePlayer(uid);
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public boolean resetBlockOwner(Location l){
    	try{
    		this.getConfigSection(SECTION_OWNERS).set(Utils.getLocationText(l), null);
    		return true;
    	}catch(Exception e){
    		return false;
    	}
    }

    public static void log(Level level, String message){
    	Bukkit.getPluginManager().getPlugin("KittysLilHelpers").getLogger().log(level, message);
    }
    public static void logInfo(String message){
    	Bukkit.getPluginManager().getPlugin("KittysLilHelpers").getLogger().log(Level.INFO, message);
    }
    public static void logWarning(String message){
    	Bukkit.getPluginManager().getPlugin("KittysLilHelpers").getLogger().log(Level.WARNING, message);
    }
    public static void logSevere(String message){
    	Bukkit.getPluginManager().getPlugin("KittysLilHelpers").getLogger().log(Level.SEVERE, message);
    }
    
    public static Plugin getInstance(){
    	return Bukkit.getPluginManager().getPlugin("KittysLilHelpers");
    }
    
    public static ConfigSection getConfig(String name){
    	return ConfigSection.fromConfigurationSection(null, Bukkit.getPluginManager().getPlugin("KittysLilHelpers").getConfig()).getConfigurationSection(name, true);
    }
    
    public static CommandMap getCommandMap() {
    	CommandMap commandMap = null;
    	 
    	try {
    	if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
    	java.lang.reflect.Field f = SimplePluginManager.class.getDeclaredField("commandMap");
    	f.setAccessible(true);
    	 
    	commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
    	}
    	} catch (NoSuchFieldException e) {
    	e.printStackTrace();
    	} catch (SecurityException e) {
    	e.printStackTrace();
    	} catch (IllegalArgumentException e) {
    	e.printStackTrace();
    	} catch (IllegalAccessException e) {
    	e.printStackTrace();
    	}
    	 
    	return commandMap;
    }

    public static File getFolder(){
    	File result = getInstance().getDataFolder();
    	if(result.exists()==false){
    		try{
        		result.createNewFile();
        	}catch(Exception e){}
    		
    	}
    	return result;
    }
  
	@Override
	public ContainerType getContainerType() {
		return ContainerType.SERVER;
	}

	@Override
	public String getContainerName() {
		return "default";
	}

	@Override
	public WorldFlags getWorldFlags() {
		if(this.defaultFlags==null)
			this.defaultFlags = new WorldFlags(this, null, getConfig("default_flags"));

		return this.defaultFlags;
	}

	@Override
	public OfflinePlayer getOwner() {
		return Bukkit.getOperators().iterator().next();
	}
}

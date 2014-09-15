package com.github.isatwospirit.kittyslilhelpers.command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentString;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public abstract class CommandEx extends Command implements Listener {
	private KittysLilHelpers owningPlugin = null;
	private ConfigSection configuration = null;
	private String permission = null;
	
	private Map<String, CommandOption> options = new HashMap<String, CommandOption>();
	private Map<String, Permission>permissions = new HashMap<String, Permission>();

	public CommandEx(String name){
		super(name);
	}
		
	public final String getDisplayName(){
		return KittysLilHelpers.COLOR_COMMAND + "/" + this.getName() + ChatColor.RESET;
	}
	
	public final KittysLilHelpers getOwningPlugin(){
		return this.owningPlugin;
	}
	
	public final ConfigSection getConfiguration(){
		return this.configuration;
	}
	
	private final Map<String, CommandOption> getOptions(){
		return this.options;
	}
	public final Integer getOptionCount(){
		return this.getOptions().size();
	}
	public final CommandOption getOption(String name){
		if(this.getOptions().containsKey(name.toLowerCase())){
			return this.getOptions().get(name.toLowerCase());
		}else{
			return null;
		}
	}
	public final Set<String> getOptionKeys(){
		return this.getOptions().keySet();
	}
	protected final CommandOption addOption(String name, String shortDescription, String longDescription){
		return this.addOption(new CommandOption(this, name, shortDescription, longDescription, this.getPermission() + "." + name.toLowerCase(), false));
	}
	protected final CommandOption addOption(String name, String shortDescription, String longDescription, String requiredPermission, Boolean hasOthersPermission){
		if(requiredPermission==null)
			requiredPermission = this.getPermission();
		else if(requiredPermission.contains(".")==false)
			requiredPermission = this.getPermission() + "." + requiredPermission;
		return this.addOption(new CommandOption(this, name, shortDescription, longDescription, requiredPermission, hasOthersPermission));
	}
	protected final CommandOption addOption(CommandOption option){
		try{
			this.initPermission(option.getPermission(), option);
			this.getOptions().put(option.getName().toLowerCase(), option);
			return option;
		}catch(Exception e){
			return null;
		}	
	}
	
	public final Permission getBasePermission(){
		return Bukkit.getPluginManager().getPermission(this.getPermission());
	}
	
	public final String getPermission(){
		if(this.permission==null){
			if(this.getOwningPlugin()==null){
				return null;
			}else{
				return this.getOwningPlugin().getName().toLowerCase() + "." + this.getName().toLowerCase();
			}
		}else{
			return this.permission;
		}
	}
	
	public final void setPermission(String permissionName){
		if(this.getOwningPlugin()!=null){
			if(permissionName.contains(".")){
				this.permission = permissionName.toLowerCase();
			}else{
				this.permission = this.getOwningPlugin().getName().toLowerCase() + "." + permissionName.toLowerCase();
			}
		}
	}
	
	public final void onLoad(KittysLilHelpers owningPlugin, ConfigSection configuration){
		try{
			this.owningPlugin = owningPlugin;
			this.configuration = configuration;
			this.loadPermissions();
			this.initPermission(this.getPermission(), null);
		}catch(Exception e){
			System.out.println("onLoad: " + e.getClass().getName() + " (" + e.getMessage() + ")");
		}
	}
	
	public final void onEnable(){
		CommandOption option = this.addOption("help", "Displays information on available command options and their parameters.", null, null, false);
		option.addArgument(new ArgumentString(), "Option", "Name of an option for detailed information.", OptionalStyle.OPTIONAL);
		this.doInitialize();
		if(this.getUsage()==null){
			this.setUsage("Type " + this.getDisplayName() + " or " + this.getDisplayName() + " " + Utils.colorize("help", KittysLilHelpers.COLOR_OPTION) + " for detailed information.");
		}
		this.setAliases();
		KittysLilHelpers.getCommandMap().register(this.getName(), this);
	}
	
	@Override
	public final boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if(args.length==0)
			args = new String[]{"help"};
		
		if(args[0].compareToIgnoreCase("help")==0){
			return this.showHelp(sender, args);
		}else{
			CommandOption option = this.getOption(args[0]);
			if(option==null){
				sender.sendMessage(Utils.formatError(args[0]) + " is not a valid option for command " + this.getDisplayName() + ".");
			}else{
				Boolean useOthersIfAvailable = false;
				Player pSender = null;
				if(sender instanceof Player)
					pSender = (Player)sender;
				else
					useOthersIfAvailable = true;
				CommandOption optionWithValues = this.parseArguments(sender, option, args);
				Boolean hasFailed = false;
				for(CommandArgument check : optionWithValues.getAllArguments()){
					if(check.didValueCheckPass()==false){
						sender.sendMessage("Error in Argument " + check.getDisplayName() + ": " + Utils.formatError(check.getFindValueMessage()));
						hasFailed = true;
						break;
					}else if(useOthersIfAvailable==false && check.getOwningPlayer()!=null){
						useOthersIfAvailable = (pSender.getUniqueId()!=check.getOwningPlayer().getUniqueId());
					}
				}
				if(hasFailed==false){
					if(optionWithValues.getPermission()!=null){
						String permission = optionWithValues.getPermission();
						if(optionWithValues.hasOthersPermission() && useOthersIfAvailable)
							permission += Utils.CONF_PERM_OTHERS;
						if(sender.hasPermission(permission)==false){
							sender.sendMessage("You are not allowed to " + this.getDisplayName() + " " + optionWithValues.getDisplayName() + " here, lacking permission " + Utils.formatError(permission) + ".");
							return true;
						}
					}
					return this.doCommand(sender, optionWithValues);
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	public abstract String getLongDescription();
	
	protected abstract boolean doCommand(CommandSender sender, CommandOption option);
	protected abstract boolean doInitialize();
	public abstract void onDisable();
	
	private CommandOption parseArguments(CommandSender sender, CommandOption option, String[] args){
		CommandOption result = option.clone();
		ContextDefaults defaultValues = new ContextDefaults(sender);
		Integer startIndex = 1;
		
		for(Integer i=0;i<result.getArgumentCount();i++){
			try{
				CommandArgument arg = result.getArgument(i);
				startIndex += arg.findValue(defaultValues, args, startIndex);
			}catch(Exception e){
				sender.sendMessage(e.getMessage());
			}
		}
		return result;
	}
	
	private boolean showHelp(CommandSender sender, String[] args){
		try{
			if(args.length==1){
				sender.sendMessage(this.getHelpTitle(null));
				for(CommandOption option : this.getOptions().values()){
					this.showCommandInfoShort(sender, option);
				}
				sender.sendMessage("For detailed information on a specific option, type");
				sender.sendMessage(this.getDisplayName() + " help " + KittysLilHelpers.COLOR_OPTION + "OptionName");
			}else{
				CommandOption option = this.getOption(args[1]);
				if(option==null)
					sender.sendMessage(Utils.formatError(args[1]) + " is not a recognized Option for command " + this.getDisplayName() + ".");
				else
					this.showCommandInfoLong(sender, option);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return true;
	}

	private void showCommandInfoShort(CommandSender sender, CommandOption option){
		try{
			String msg=option.getDisplayName();
			if(option.getName().length()<11){
				msg += StringUtils.repeat(" ", 11 - option.getName().length());
			}else
				msg += " ";
				for(Integer i=0;i<option.getArgumentCount();i++){
					try{
						msg += option.getArgument(i).getDisplayName() + " ";
					}catch(Exception e){
						msg += e.getMessage();
					}
				}
			sender.sendMessage(msg.trim());
			sender.sendMessage("   " + option.getShortDescription().trim());
		}catch(Exception e){
			sender.sendMessage(e.getMessage());
		}
	}
	
	private void showCommandInfoLong(CommandSender sender, CommandOption option){
		try{
			sender.sendMessage(this.getHelpTitle(option.getName()));
			sender.sendMessage(option.getLongDescription());
			if(option.getArgumentCount()==0){
				sender.sendMessage("Option has no additional arguments.");
			}else{
				sender.sendMessage("Additional arguments:");
				for(Integer i=0;i<option.getArgumentCount();i++){
					try{
						CommandArgument current = option.getArgument(i);
						sender.sendMessage("   " + current.getDisplayName() + " " + current.getDescription());
					}catch(Exception e){
						System.out.println("showCommandInfoLong: " + e.getMessage());
					}
				}
				if(option.getPermission()==null){
					sender.sendMessage("No further permission is needed to issue this option.");
				}else{
					sender.sendMessage("Required permission(s):");
					if(option.hasOthersPermission()){
						sender.sendMessage("   " + option.getPermission() + "       (self-owned objects)");
						sender.sendMessage("   " + option.getPermission() + ".other (objects owned by other players)");
					}else{
						sender.sendMessage("   " + option.getPermission());
					}
				}
			}
		}catch(Exception e){
			sender.sendMessage(e.getMessage());
		}
	}

	private String getHelpTitle(String option){
		String msg = KittysLilHelpers.COLOR_TITLE + "KittysLilHelpers - Help System: " + this.getDisplayName();
		if(option != null)
			msg += " " + KittysLilHelpers.COLOR_OPTION + option;
		return msg;
	}

	private void initPermission(String name, CommandOption option){
		Boolean hasOthersPermission = false;
		if(name==null && option==null)
			return;
		if(name==null && option!=null){
			name = option.getPermission();
		}
		if(option != null)
			hasOthersPermission = option.hasOthersPermission();
		
		if(this.permissions.containsKey(name)==false){
			ConfigSection perm = this.getConfiguration().getConfigurationSection(Utils.CONF_PERMISSIONS, true);
			Permission newPerm = Bukkit.getPluginManager().getPermission(name);
			String desc;
			if(newPerm==null){
				if(option==null){
					desc = "Allows to use /" + this.getName() + ".";
				}else{
					desc = "Allows to use /" + this.getName() + " " + option.getName();
					if(option.hasOthersPermission())
						desc += " on self-owned objects.";
					else
						desc += ".";
				}
				newPerm = new Permission(name, desc, PermissionDefault.TRUE);
				System.out.println("initPermission: Add " + name);
				Bukkit.getPluginManager().addPermission(newPerm);
			}
			perm.set(newPerm.getName().replace(".", "-"), newPerm);
			this.permissions.put(newPerm.getName().toLowerCase(), newPerm);

			if(hasOthersPermission){
				newPerm = Bukkit.getPluginManager().getPermission(name + Utils.CONF_PERM_OTHERS);
				if(newPerm==null){
					desc = "Allows to use " + this.getName() + " " + option.getName() + " on objects owned by other players.";
					newPerm = new Permission(name + Utils.CONF_PERM_OTHERS, desc, PermissionDefault.OP);
					System.out.println("initPermission: Add " + newPerm.getName());
					Bukkit.getPluginManager().addPermission(newPerm);					
				}
				perm.set(newPerm.getName().replace(".", "-"), newPerm);
				this.permissions.put(newPerm.getName().toLowerCase(), newPerm);
			}
			this.configuration.save();
		}
	}
	
	private void loadPermissions(){
		ConfigSection perm = this.getConfiguration().getConfigurationSection(Utils.CONF_PERMISSIONS, true);
		for(String name : perm.getKeys(false)){
			Permission newPerm = perm.getPermission(name);
			this.permissions.put(newPerm.getName().toLowerCase(), newPerm);
		}	
	}
	
	private void setAliases(){
		List<String>aliases = null;
		if(this.getAliases()==null){
			aliases = new LinkedList<String>();
		}else{
			aliases = this.getAliases();
		}
		aliases.add(this.getName());
		aliases.add(this.getOwningPlugin().getCommandPrefix() + this.getName());
		this.setAliases(aliases);
	}
}
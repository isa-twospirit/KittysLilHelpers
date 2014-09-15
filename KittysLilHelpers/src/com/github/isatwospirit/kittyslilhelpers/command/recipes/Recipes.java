package com.github.isatwospirit.kittyslilhelpers.command.recipes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.permissions.PermissionDefault;

import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentListedObject.ItemExistanceType;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentPermissionDefault;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentPlayer;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentWorld;
import com.github.isatwospirit.kittyslilhelpers.command.recipes.KittysRecipe.DescriptionFormat;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class Recipes extends CommandEx{

	public Recipes(){
		super("recipe");
		this.setDescription("Maintenance of custom recipes contained within KittysLilHelpers.");
	}

	@Override
	public String getLongDescription(){
		return this.getDescription();
	}

	public KittysRecipes getRecipes(){
		return KittysRecipes.getInstance();
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent ple){
		if(this.getConfiguration().getConfigurationSection("handedcookbook", true).getBoolean("" + ple.getPlayer().getUniqueId(), false)==false){
			this.giveCookBook(ple.getPlayer());
			this.getConfiguration().getConfigurationSection("handedcookbook", true).set("" + ple.getPlayer().getUniqueId(), true);
		}
	}
	
	@EventHandler
	public void onRecipeComplete(CraftItemEvent cie){
		Player p = (Player)cie.getWhoClicked();
		KittysRecipe r = this.getRecipes().get(cie.getRecipe());
		
		if(r!=null){
			if(r.isDeleted()){
				cie.setCancelled(true);
				p.sendMessage("Recipe " + r.getDisplayName() + " is deleted.");
			}else if(r.isEnabled()==false){
				cie.setCancelled(true);
				p.sendMessage("Recipe " + r.getDisplayName() + " is disabled.");
			}else if(p.hasPermission(r.getPermissionName())==false){
				cie.setCancelled(true);
				p.sendMessage("You do not have the required permission to use recipe " + r.getDisplayName());
			}
		}
	}

	private void doListRecipes(CommandSender sender){
		sender.sendMessage("KittysLilHelpers - Custom Recipes:");
		for(KittysRecipe recipe : this.getRecipes().getValues()){
			sender.sendMessage(recipe.getDescription(DescriptionFormat.LONG));		
		}
	}

	private void giveCookBook(Player p){
		ItemStack result = KittysRecipes.getInstance().getCookBook();
		if(p.getInventory().addItem(result).isEmpty()==false){
			p.getWorld().dropItem(p.getLocation(), result);
		}
/*		ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		book.setTitle("Custom Recipe Cookbook");
		book.setAuthor("KittysLilHelpers");
		book.setLore(new ArrayList<String>());
		book.getLore().add("Refer to this book when");
		book.getLore().add("unsure about custom recipes");
		book.getLore().add("available on this server.");
		book.addPage(ChatColor.BLUE + "Welcome to the Custom Recipe Cookbook!\n" + ChatColor.BLACK + "\nThe following pages contain custom recipes available on this server. Since they may change, you might want to acquire an updated version occasionally by issueing the command\n" + ChatColor.BLUE + "/recipe givecookbook\n");
		String deleted = ChatColor.BLUE + "Recipes marked for deletion:\n" + ChatColor.BLACK;
		String disabled = ChatColor.BLUE + "Disabled recipes:\n" + ChatColor.BLACK;
		for(KittysRecipe r : this.getRecipes().getValues()){
			if(r.isDeleted()){
				deleted += " - " + r.getName() + "\n";
			}else if(r.isEnabled()==false){
				disabled += " - " + r.getName() + "\n";
			}else{
				book.addPage(r.getDescription(DescriptionFormat.COOKBOOK));
			}
		}
		book.addPage(disabled);
		book.addPage(deleted);
		
		result.setItemMeta(book);
		if(p.getInventory().addItem(result).isEmpty()==false){
			p.getWorld().dropItem(p.getLocation(), result);
		}*/
	}
	
//--Implementations for "KittysCommand":----------------
	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		if(option.getName().compareToIgnoreCase("list")==0){
			this.doListRecipes(sender);
		}else if(option.getName().compareToIgnoreCase("enableall")==0){
			for(KittysRecipe current : this.getRecipes().getValues()){
				current.setEnabled(true);
				sender.sendMessage(current.getDescription(DescriptionFormat.LONG));
			}
		}else if(option.getName().compareToIgnoreCase("disableall")==0){
			for(KittysRecipe current : this.getRecipes().getValues()){
				current.setEnabled(false);
				sender.sendMessage(current.getDescription(DescriptionFormat.LONG));
			}
		}else if(option.getName().compareToIgnoreCase("enable")==0){
			KittysRecipe r = ((KittysRecipe)option.getArgument("name").getValue());
			r.setEnabled(true);
			sender.sendMessage("Enabled recipe:\n" + r.getDescription(DescriptionFormat.SHORT));
		}else if(option.getName().compareToIgnoreCase("disable")==0){
			KittysRecipe r = ((KittysRecipe)option.getArgument("name").getValue());
			r.setEnabled(false);
			sender.sendMessage("Disabled recipe:\n" + r.getDescription(DescriptionFormat.SHORT));
		}else if(option.getName().compareToIgnoreCase("show")==0){
			sender.sendMessage("Recipe " + ((KittysRecipe)option.getArgument("name").getValue()).getDescription(DescriptionFormat.SHORT));
		}else if(option.getName().compareToIgnoreCase("delete")==0){
			KittysRecipe del = (KittysRecipe)option.getArgument("name").getValue();
			if(this.getRecipes().delete(del)){
				sender.sendMessage("Recipe deleted:\n" + del.getDescription(DescriptionFormat.SHORT));
			}else{
				sender.sendMessage("Unable to delte Recipe:\n" + del.getDescription(DescriptionFormat.SHORT));
			}
		}else if(option.getName().compareToIgnoreCase("add")==0){
			Location l = (Location)option.getArgument("coord").getValue();
			if(l.getBlock().getType()==Material.CHEST){
				Inventory i = Utils.getInventoryFrom(l);
				try{
					KittysRecipe r = this.getRecipes().add((String)option.getArgument("Name").getValue(), i);
					sender.sendMessage("Recipe created:\n" + r.getDescription(DescriptionFormat.SHORT));
				}catch(Exception e){
					sender.sendMessage("Unable to create recipe " + option.getArgument("name").getFindValueMessage() + ": " + e.getMessage());
				}	
			}else{
				sender.sendMessage("No chest at given coordinates " + Utils.formatError(Utils.getLocationText(l)));
				sender.sendMessage(l.getBlock().getType().name());
			}
		}else if(option.getName().compareToIgnoreCase("perm")==0){
			KittysRecipe r = (KittysRecipe)option.getArgument("name").getValue();
			r.setPermissionDefault((PermissionDefault)option.getArgument("PermDefault").getValue());
			sender.sendMessage("Recipe updated:\n" + r.getDescription(DescriptionFormat.SHORT));
		}else if(option.getName().compareToIgnoreCase("defaultenabled")==0){
			if(option.getArgument("EnabledDefault").getValue().toString().compareToIgnoreCase("true")==0){
				this.getRecipes().setDefaultEnabled(true);
				sender.sendMessage("Settings updated - New recipes are by default ENABLED.");
			}else{
				this.getRecipes().setDefaultEnabled(false);
				sender.sendMessage("Settings updated - New recipes are by default DISABLED.");
			}
		}else if(option.getName().compareToIgnoreCase("defaultperm")==0){
			this.getRecipes().setDefaultUseGranted((PermissionDefault)option.getArgument("PermDefault").getValue());
			sender.sendMessage("Settings updated - New recipes have a default permission of " + this.getRecipes().getDefaultUseGranted().name() + ".");
		}else if(option.getName().compareToIgnoreCase("givecookbook")==0){
			this.giveCookBook((Player)option.getArgument("player").getValue());
		}
			
		return true;
	}
	@Override
	protected boolean doInitialize() {
		CommandOption option = null;
		this.addOption("list", "Lists custom recipes in KittysLilHelpers", null, "view", false);
		this.addOption("enableall", "Enables all custom recipes.", null, "xable", false);
		this.addOption("disableall", "Disables all custom recipes.", null, "xable", false);		
		option = this.addOption("enable", "Enables a custom recipe", null, "xable", false);
		option.addArgument(new ArgumentListedObject(this.getRecipes()), "Name", "Name of custom recipe", OptionalStyle.REQUIRED);
		option = this.addOption("disable", "Disables a custom recipe.", null, "xable", false);
		option.addArgument(new ArgumentListedObject(this.getRecipes()), "Name", "Name of custom recipe", OptionalStyle.REQUIRED);
		option = this.addOption("show", "Display ingredients and shape of a custom recipe", null, "view", false);
		option.addArgument(new ArgumentListedObject(this.getRecipes()), "Name", "Name of custom recipe", OptionalStyle.REQUIRED);
		option = this.addOption("delete", "Deletes a custom recipe.", null, "maintain", false);
		option.addArgument(new ArgumentListedObject(this.getRecipes()), "Name", "Name of custom recipe", OptionalStyle.REQUIRED);
		option = this.addOption("add", "Creates a new custom recipe from a chest.", null, "maintain", false);
		option.addArgument(new ArgumentWorld(), "World", "World the chest exists in.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentLocation(), "Coord", "Coordinates of chest.", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option.addArgument(new ArgumentListedObject(this.getRecipes(), ItemExistanceType.ITEM_MUST_NOT_EXIST), "Name", "Name for new recipe.", OptionalStyle.REQUIRED);
		option = this.addOption("perm", "Sets the default permission value to use given recipe.", null, "maintain", false);
		option.addArgument(new ArgumentListedObject(this.getRecipes()), "Name", "Name of recipe", OptionalStyle.REQUIRED);
		option.addArgument(new ArgumentPermissionDefault(), "PermDefault", "PermissionDefault to be applied to recipe.", OptionalStyle.REQUIRED);
		option = this.addOption("defaultenabled", "Sets whether new recipes are enabled by default", null, "maintain", false);
		option.addArgument(new ArgumentListedObject("true", "false"), "EnabledDefault", "'true' or 'false'", OptionalStyle.REQUIRED);
		option = this.addOption("defaultperm", "Sets the default permission for new recipes", null, "maintain", false);
		option.addArgument(new ArgumentPermissionDefault(), "PermDefault", "PermissionDefault to be applied to new recipes.", OptionalStyle.REQUIRED);
		option = this.addOption("givecookbook", "Hands a book containing all custom recipes to a player", null, "view", false);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of a Player", OptionalStyle.OPTIONAL_IF_DEFAULT);
		return true;
	}	
	@Override
	public void onDisable() {
		// Nothing, I think
	}


	@EventHandler
	public void onHeldItemChange(PlayerItemHeldEvent pie){
		try{
			ItemStack heldItem = pie.getPlayer().getInventory().getItem(pie.getNewSlot());
			if(heldItem.getType()==Material.WRITTEN_BOOK){
				BookMeta content = (BookMeta)heldItem.getItemMeta();
				if(content.getTitle().startsWith(KittysRecipes.getInstance().getCookBookTitle())){
					if(content.getTitle().endsWith("v" + KittysRecipes.getInstance().getCookBookRevision())==false){
						heldItem.setItemMeta(KittysRecipes.getInstance().getCookBookContent());
					}
				}
			}
		}catch(Exception e){
			
		}
	}
//------------------------------------------------------

}

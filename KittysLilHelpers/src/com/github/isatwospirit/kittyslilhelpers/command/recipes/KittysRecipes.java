package com.github.isatwospirit.kittyslilhelpers.command.recipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.permissions.PermissionDefault;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;
import com.github.isatwospirit.kittyslilhelpers.command.recipes.KittysRecipe.DescriptionFormat;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigFile;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;

public class KittysRecipes implements ItemContainer{
	private static KittysRecipes instance;
	
	private ConfigFile config = null;
	private Map<String, KittysRecipe> customRecipes = null;
	private HashSet<KittysRecipe> deletedRecipes = null;
	private boolean defaultEnabled = false;
	private PermissionDefault defaultUseGranted = PermissionDefault.TRUE;
	private Integer revision=0;
	private String cookBookTitle="";
	private BookMeta cookBookContent=null;
	private Boolean isInit = false;
	
	public static KittysRecipes getInstance(){
		if(instance==null){
			instance = new KittysRecipes();
		}
		return instance;
	}
	
	private KittysRecipes(){
		this.isInit=true;
		
		this.config = new ConfigFile("recipes");

		this.defaultEnabled = this.getConfiguration().getBoolean("default_enabled", true);
		this.defaultUseGranted = this.getConfiguration().getPermissionDefault("default_use_granted", PermissionDefault.TRUE);
		this.customRecipes = new LinkedHashMap<String, KittysRecipe>();
		this.deletedRecipes = new HashSet<KittysRecipe>();
		this.cookBookTitle = this.getConfiguration().getString("cookbooktitle", "Custom Recipe CookBook");
		this.revision = this.getConfiguration().getInt("revision", 0);
		KittysLilHelpers.logInfo("Loading custom recipes...");
		for(String name : this.getConfiguration().getKeys(false)){
			try{
				if(this.getConfiguration().isConfigurationSection(name)){
					this.add(name);
				}
			}catch(Exception e){
				KittysLilHelpers.logWarning("Error reading recipe " + name + ": " + e.getMessage());
			}
		}
		this.isInit=false;
		KittysLilHelpers.logInfo("Done, " + this.size() + " Recipes loaded.");
	}
	
	public ConfigSection getConfiguration(){
		return this.config.getRootSection();
	}
	
	public boolean getDefaultEnabled(){
		return this.defaultEnabled;
	}
	public void setDefaultEnabled(boolean value){
		this.defaultEnabled = value;
		this.getConfiguration().set("DefaultEnabled", value);
		this.getConfiguration().save();
	}
	
	public PermissionDefault getDefaultUseGranted(){
		return this.defaultUseGranted;
	}
	public void setDefaultUseGranted(PermissionDefault value){
		if(value==null)
			this.defaultUseGranted = PermissionDefault.TRUE;
		else
			this.defaultUseGranted = value;
		this.getConfiguration().set("DefaultUseGranted", value);
		this.getConfiguration().save();
	}

	public boolean contains(String name){
		return this.customRecipes.containsKey(name.toLowerCase());
	}
	public boolean contains(KittysRecipe recipe){
		return this.contains(recipe.getName());
	}
	public boolean contains(Recipe recipe){
		for(KittysRecipe check : this.customRecipes.values()){
			if(check.equals(recipe)){
				return true;
			}
		}
		return false;
	}
	
	public KittysRecipe get(String name){
		if(this.customRecipes.containsKey(name.toLowerCase())){
			return this.customRecipes.get(name.toLowerCase());
		}else{
			for(KittysRecipe r : this.deletedRecipes){
				if(r.getName().compareToIgnoreCase(name)==0){
					return r;
				}
			}
		}
		return null;
	}
	public KittysRecipe get(Recipe r){
		for(KittysRecipe kr : this.customRecipes.values()){
			if(kr.equals(r)){
				return kr;
			}
		}
		for(KittysRecipe kr : this.deletedRecipes){
			if(kr.equals(r)){
				return kr;
			}
		}
		return null;
	}

	public KittysRecipe add(KittysRecipe recipe) throws Exception{
		if(this.contains(recipe.getName())){
			throw new Exception("A recipe named " + recipe.getName() + " already exists.");
		}else{
			this.customRecipes.put(recipe.getName().toLowerCase(), recipe);
			if(this.isInit==false)
				this.nextRevision();
			return recipe;
		}
	}
	public KittysRecipe add(String name, Inventory source) throws Exception{
		return this.add(new KittysRecipe(name, source, this.getConfiguration(), this.getDefaultEnabled()));
	}
	public KittysRecipe add(String name, Recipe source) throws Exception{
		return this.add(new KittysRecipe(name, source, this.getConfiguration(), this.getDefaultEnabled()));
	}
	public KittysRecipe add(String name) throws Exception{
		return this.add(new KittysRecipe(this.getConfiguration().getConfigurationSection(name)));
	}
	
	public String getCookBookTitle(){
		return this.cookBookTitle;
	}
	public Integer getCookBookRevision(){
		return this.revision;
	}
	public BookMeta getCookBookContent(){
		if(this.cookBookContent==null){
			this.buildCookBookContent();
		}
		return this.cookBookContent;
	}
	public ItemStack getCookBook(){
		ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
		result.setItemMeta(this.getCookBookContent());
		return result;
	}
	
	public void nextRevision(){
		this.revision+=1;
		this.cookBookContent=null;
		this.getConfiguration().set("revision", this.revision);
		this.getConfiguration().save();
	}
	
	public Integer size(){
		return this.customRecipes.size();
	}
	
	public Collection<KittysRecipe> getValues(){
		return this.customRecipes.values();
	}
	
	public boolean delete(KittysRecipe recipe){
		if(this.contains(recipe)){
			this.getConfiguration().set(recipe.getName(), null);
			this.getConfiguration().save();
			recipe.delete();
			this.customRecipes.remove(recipe);
			this.deletedRecipes.add(recipe);
			this.nextRevision();
			return true;
		}
		return false;
	}
	
	private void buildCookBookContent(){
		this.cookBookContent = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		this.cookBookContent.setTitle(this.getCookBookTitle() + " v" + this.revision);
		this.cookBookContent.setAuthor("KittysLilHelpers");
		this.cookBookContent.setLore(new ArrayList<String>());
		this.cookBookContent.getLore().add("Refer to this book when");
		this.cookBookContent.getLore().add("unsure about custom recipes");
		this.cookBookContent.getLore().add("available on this server.");
		this.cookBookContent.addPage(ChatColor.BLUE + "Welcome to the Custom Recipe Cookbook!\n" + ChatColor.BLACK + 
				"The following pages\n" + 
				"contain custom recipes\n" + 
				"available on this server.\n" +
				"They may change over\n" +
				"time, but the cookbook\n" + 
				"will stay up-to-date.\n" + 
				"If you loose your copy,\n" + 
				ChatColor.BLUE + "/recipe givecookbook\n" + ChatColor.BLACK +
				"gives you a new one.");
		
		String deleted = ChatColor.BLUE + "Recipes marked for deletion:\n" + ChatColor.BLACK;
		String disabled = ChatColor.BLUE + "Disabled recipes:\n" + ChatColor.BLACK;
		for(KittysRecipe r : this.getValues()){
			if(r.isDeleted()){
				deleted += " - " + r.getName() + "\n";
			}else if(r.isEnabled()==false){
				disabled += " - " + r.getName() + "\n";
			}else{
				this.cookBookContent.addPage(r.getDescription(DescriptionFormat.COOKBOOK));
			}
		}
		this.cookBookContent.addPage(disabled);
		this.cookBookContent.addPage(deleted);
	}
	
//--Interface "ItemContainer":--------------------------
	@Override
	public String getItemDisplayName() {
		return "Recipe";
	}
	@Override
	public Class<?> getItemType() {
		return KittysRecipe.class;
	}
	@Override
	public KittysRecipe getItem(String key) {
		return this.get(key);
	}
	@Override
	public OfflinePlayer getItemOwner(String key) {
		return null;
	}
//------------------------------------------------------
}

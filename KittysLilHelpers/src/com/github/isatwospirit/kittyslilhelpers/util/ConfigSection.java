package com.github.isatwospirit.kittyslilhelpers.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.command.recipes.KittysRecipe;

public class ConfigSection implements ConfigurationSection{
	private ConfigFile container;
	private org.bukkit.configuration.ConfigurationSection source=null;
	
	public static ConfigSection fromConfigurationSection(ConfigFile container, ConfigurationSection source){
		return new ConfigSection(container, source);
	}
	
	public ConfigSection(ConfigFile container, ConfigurationSection source){
		if(source==null)
			throw new IllegalArgumentException("Source cannot be null.");
		this.container = container;
		this.source = source;
	}

	public boolean save(){
		try{
			if(this.container!=null){
				this.container.save();
			}else{
				Bukkit.getPluginManager().getPlugin("KittysLilHelpers").saveConfig();
			}
			return true;
		}catch(Exception e){
			KittysLilHelpers.logWarning("ConfigSection.save: " + e.getMessage());
			return false;
		}
	}

	@Override
	public Object get(String path) {
		if(this.isItemStack(path)){
			return this.getItemStack(path);
		}else if(this.isPermissionDefault(path)){
			return this.getPermissionDefault(path);
		}else if(this.isRecipe(path)){
			return this.getRecipe(path);
		}else if(this.isEnchantmentMap(path)){
			return this.getEnchantmentMap(path);
		}else if(this.isItemMeta(path)){
			return this.getItemMeta(path);
		}else if(this.isLocation(path)){
			return this.getLocation(path);
		}else if(this.isPermission(path)){
			return this.getPermission(path);
		}else{
			return this.getSource().get(path);
		}
	}

	@Override
	public Object get(String path, Object def) {
		Object result = this.get(path);
		if(result==null){
			return def;
		}else{
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(String path, Object value) {
		if(value==null){
			this.getSource().set(path, null);
		}else if(ItemStack.class.isAssignableFrom(value.getClass())){
			this.setItemStack(path, (ItemStack)value);
		}else if(PermissionDefault.class.isAssignableFrom(value.getClass())){
			this.setPermissionDefault(path, (PermissionDefault)value);
		}else if(Recipe.class.isAssignableFrom(value.getClass())){
			this.setRecipe(path, (Recipe)value);
		}else if(ItemMeta.class.isAssignableFrom(value.getClass())){
			this.setItemMeta(path, (ItemMeta)value);
		}else if(Map.class.isAssignableFrom(value.getClass())){
			this.setEnchantmentMap(path, (Map<Enchantment, Integer>)value);
		}else if(Location.class.isAssignableFrom(value.getClass())){
			this.setLocation(path, (Location)value);
		}else if(Permission.class.isAssignableFrom(value.getClass())){
			this.setPermission(path, (Permission)value);
		}else{
			this.getSource().set(path, value);
		}
	}
	
	private boolean isType(String path, String type){
		if(this.isConfigurationSection(path)){
			return this.getConfigurationSection(path).getString(Utils.KEY_TYPE_NAME, Utils.VALUE_EMPTY).startsWith(type);
		}
		return false;
	}
	
	public PermissionDefault getPermissionDefault(String path){
		String value=this.getString(path, Utils.VALUE_EMPTY);
		if(value.startsWith(Utils.VALUE_PERMISSION_DEFAULT)){
			return PermissionDefault.getByName(value.substring(Utils.VALUE_PERMISSION_DEFAULT.length() + 1));
		}else{
			return null;
		}
	}
	
	public PermissionDefault getPermissionDefault(String path, PermissionDefault def){
		PermissionDefault result = this.getPermissionDefault(path);
		if(result==null)
			return def;
		else
			return result;
	}

	public boolean isPermissionDefault(String path){
		return this.isType(path, Utils.VALUE_PERMISSION_DEFAULT);
	}
	
	private void setPermissionDefault(String path, PermissionDefault value){
		this.set(path, Utils.VALUE_PERMISSION_DEFAULT + Utils.VALUE_SEPARATOR + value.name());
	}

	@Override
	public ItemStack getItemStack(String path, ItemStack def) {
		if(this.isItemStack(path))
			return this.getItemStack(path);
		else
			return def;
	}
	
	@Override
	public ItemStack getItemStack(String path) {
		try{
			if(this.isConfigurationSection(path)){
				ConfigSection stacksource = this.getConfigurationSection(path);
				if(stacksource.getString(Utils.KEY_TYPE_NAME).compareToIgnoreCase(Utils.VALUE_ITEMSTACK)==0){
					Material m = Material.getMaterial(stacksource.getString(Utils.CONF_MATERIAL));
					ItemStack result = new ItemStack(m, stacksource.getInt(Utils.CONF_AMOUNT, 1), Short.parseShort(stacksource.getInt(Utils.CONF_DURABILITY, 0) + Utils.VALUE_EMPTY));
					Map<Enchantment, Integer>enchantments = stacksource.getEnchantmentMap(Utils.CONF_ENCHANTMENTS);
					if(enchantments!=null){
						for(Entry<Enchantment, Integer>entry : enchantments.entrySet()){
							result.addEnchantment(entry.getKey(), entry.getValue());
						}
					}
					result.setItemMeta(stacksource.getItemMeta(Utils.CONF_ITEM_META));
					return result;
				}
			}
		}catch(Exception e){
			KittysLilHelpers.logWarning("ConfigSection.getItemStack: " + e.getMessage());
		}
		return null;
	}

	@Override
	public boolean isItemStack(String path) {
		return this.isType(path, Utils.VALUE_ITEMSTACK);
	}

	private void setItemStack(String path, ItemStack value){
		if(this.isSet(path))
			this.getSource().set(path, null);
		
		ConfigSection section = this.createSection(path);
		section.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMSTACK);
		section.set(Utils.CONF_MATERIAL, value.getType().name());
		if(value.getAmount()!=1)
			section.set(Utils.CONF_AMOUNT, value.getAmount());
		if(value.getDurability()!=0)
			section.set(Utils.CONF_DURABILITY, value.getDurability());
		if(value.hasItemMeta()){
			section.set(Utils.CONF_ITEM_META, value.getItemMeta());
		}
		section.set(Utils.CONF_ENCHANTMENTS, value.getEnchantments());
	}

	public Recipe getRecipe(String path, Recipe def){
		Recipe result = this.getRecipe(path);
		if(result==null)
			return def;
		else
			return result;
	}
	
	public Recipe getRecipe(String path){
		Recipe result = null;
		try{
			if(this.isConfigurationSection(path)){
				ConfigSection rsource = this.getConfigurationSection(path);
				String type = rsource.getString(Utils.KEY_TYPE_NAME);
				if(type.startsWith(Utils.VALUE_RECIPE)){
					ItemStack output = rsource.getItemStack(Utils.CONF_RESULT);
					if(type.compareToIgnoreCase(Utils.VALUE_RECIPE_SHAPELESS)==0){
						ShapelessRecipe temp = new ShapelessRecipe(output);
						ConfigSection ingredients = rsource.getConfigurationSection(Utils.CONF_INGREDIENTS);
						for(String key : ingredients.getKeys(false)){
							if(ingredients.isItemStack(key)){
								ItemStack ingr = ingredients.getItemStack(key);
								temp.addIngredient(ingr.getAmount(), ingr.getData());
							}
						}
						result = temp;
					}else if(type.compareToIgnoreCase(Utils.VALUE_RECIPE_SHAPED)==0){
						ShapedRecipe temp = new ShapedRecipe(output);
						String[] shape = new String[3];
						shape[0] = rsource.getString(Utils.CONF_SHAPE + "0", null);
						shape[1] = rsource.getString(Utils.CONF_SHAPE + "1", null);
						shape[2] = rsource.getString(Utils.CONF_SHAPE + "2", null);

						KittysRecipe.setShape(temp, shape);

						ConfigSection ingredients = rsource.getConfigurationSection(Utils.CONF_INGREDIENTS);
						for(String key : ingredients.getKeys(false)){
							if(ingredients.isItemStack(key)){
								ItemStack ingr = ingredients.getItemStack(key);
								temp.setIngredient(key.charAt(0), ingr.getData());
							}
						}
						result = temp;
					}else if(type.compareToIgnoreCase(Utils.VALUE_RECIPE_FURNACE)==0){
						ItemStack input = rsource.getItemStack(Utils.CONF_INPUT);
						result = new FurnaceRecipe(output, input.getType());
					}else{
						KittysLilHelpers.logWarning("Unsupported recipe type: " + type);
					}
				}
			}
		}catch(Exception e){
			KittysLilHelpers.logWarning("ConfigSection.getRecipe(" + this.getCurrentPath() + "." + path + "): " + e.getMessage());
		}
		return result;
	}
	
	public boolean isRecipe(String path){
		return this.isType(path, Utils.VALUE_RECIPE);
	}
	
	private void setRecipe(String path, Recipe value){
		ConfigSection rdest = this.createSection(path);
		rdest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_RECIPE);
		rdest.set(Utils.CONF_RESULT, value.getResult());

		if(value instanceof ShapelessRecipe){
			ShapelessRecipe recipe = (ShapelessRecipe)value;
			rdest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_RECIPE_SHAPELESS);
			ConfigSection ingredients = rdest.createSection(Utils.CONF_INGREDIENTS);
			for(Integer counter=0;counter<recipe.getIngredientList().size();counter++){
				ingredients.set(counter.toString(), recipe.getIngredientList().get(counter));
			}
		}else if(value instanceof ShapedRecipe){
			ShapedRecipe recipe = (ShapedRecipe)value;
			rdest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_RECIPE_SHAPED);
			for(Integer counter=0;counter<3;counter++){
				if(counter<recipe.getShape().length)
					rdest.set(Utils.CONF_SHAPE + counter, recipe.getShape()[counter]);
			}
			
			ConfigSection ingredients = rdest.createSection(Utils.CONF_INGREDIENTS);
			for(Entry<Character, ItemStack> entry : recipe.getIngredientMap().entrySet()){
				if(entry.getValue()!=null)
					ingredients.set(entry.getKey().toString(), entry.getValue());					
			}
		}else if(value instanceof FurnaceRecipe){
			FurnaceRecipe recipe = (FurnaceRecipe)value;
			rdest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_RECIPE_FURNACE);
			rdest.set(Utils.CONF_INPUT, recipe.getInput().getType().name());
		}else{
			KittysLilHelpers.logWarning("Unsupported recipe type: " + value.getClass().getCanonicalName());
		}		
	}
	
	public Map<Enchantment, Integer>getEnchantmentMap(String path, Map<Enchantment, Integer>def){
		Map<Enchantment, Integer> result = this.getEnchantmentMap(path);
		if(result==null){
			return def;
		}else{
			return result;
		}
	}
	
	public Map<Enchantment, Integer>getEnchantmentMap(String path){
		if(this.isConfigurationSection(path)){
			ConfigurationSection econfig = this.getConfigurationSection(path);
			Map<Enchantment, Integer>result=new HashMap<Enchantment, Integer>(); 
			if(econfig.getString(Utils.KEY_TYPE_NAME).compareToIgnoreCase(Utils.VALUE_ENCHANTMENTMAP)==0){
				for(String key : econfig.getKeys(false)){
					if(key.compareToIgnoreCase(Utils.KEY_TYPE_NAME)!=0){
						Enchantment ench = Enchantment.getByName(key);
						Integer level = econfig.getInt(key);
						result.put(ench, level);
					}
				}
				return result;
			}
		}
		return null;
	}
	
	public boolean isEnchantmentMap(String path){
		return this.isType(path, Utils.VALUE_ENCHANTMENTMAP);
	}
	
	private void setEnchantmentMap(String path, Map<Enchantment, Integer> value){
		if(value.size()==0){
			this.set(path, null);
		}else{
			ConfigurationSection ench = this.createSection(path);
			ench.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ENCHANTMENTMAP);
			for(Entry<Enchantment, Integer> enchantment : value.entrySet()){
				ench.set(enchantment.getKey().getName(), enchantment.getValue());
			}
		}
	}
	
	public ItemMeta getItemMeta(String path, ItemMeta def){
		ItemMeta result = this.getItemMeta(path);
		if(result==null)
			return def;
		else
			return result;
	}
	
	public ItemMeta getItemMeta(String path){
		try{
			if(this.isConfigurationSection(path)){
				ConfigSection source = this.getConfigurationSection(path);
				String type = source.getString(Utils.KEY_TYPE_NAME);
				if(type.startsWith(Utils.VALUE_ITEMMETA)){
					ItemMeta result = null;
					if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_BOOK)==0){
						BookMeta b = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.BOOK_AND_QUILL);
						if(source.isSet(Utils.CONF_AUTHOR))
							b.setAuthor(source.getString(Utils.CONF_AUTHOR));
						if(source.isSet(Utils.CONF_TITLE))
							b.setTitle(source.getString(Utils.CONF_TITLE));
						if(source.isSet(Utils.CONF_PAGES)){
							b.setPages(source.getStringList(Utils.CONF_PAGES));
						}
						result=b;
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_ENCHANTMENT_STORAGE)==0){
						EnchantmentStorageMeta e = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
						if(source.isSet(Utils.CONF_STORED_ENCHANTMENTS)){
							for(Entry<Enchantment, Integer> entry : this.getEnchantmentMap(Utils.CONF_STORED_ENCHANTMENTS).entrySet()){
								e.addStoredEnchant(entry.getKey(), entry.getValue(), true);
							}
						}
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_FIREWORK_EFFECT)==0){
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_FIREWORK)==0){
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_LEATHER_ARMOR)==0){
						LeatherArmorMeta l = (LeatherArmorMeta) Bukkit.getItemFactory().getItemMeta(Material.LEATHER_BOOTS);
						if(source.isSet(Utils.CONF_COLOR))
							l.setColor(source.getColor(Utils.CONF_COLOR));
						result=l;
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_MAP)==0){
						MapMeta m = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.MAP);
						if(source.isSet(Utils.CONF_IS_SCALING))
							m.setScaling(source.getBoolean(Utils.CONF_IS_SCALING));
						result=m;
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_POTION)==0){
						PotionMeta p = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);
						for(String key : source.getKeys(false)){
							if(source.isConfigurationSection(key)){
								ConfigurationSection fxSource = source.getConfigurationSection(key);
								PotionEffect fx = new PotionEffect(PotionEffectType.getByName(key),
																fxSource.getInt(Utils.CONF_DURATION),
																fxSource.getInt(Utils.CONF_AMPLIFIER),
																fxSource.getBoolean(Utils.CONF_IS_AMBIENT));
								p.addCustomEffect(fx, false);
							}
						}
						result=p;
					}else if(type.compareToIgnoreCase(Utils.VALUE_ITEMMETA_SKULL)==0){
						SkullMeta s = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
						if(source.isSet(Utils.CONF_OWNER))
							s.setOwner(source.getString(Utils.CONF_OWNER));
						result=s;
					}
					if(result==null)
						result = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_AXE);
					if(source.isSet(Utils.CONF_DISPLAYNAME))
						result.setDisplayName(source.getString(Utils.CONF_DISPLAYNAME));
					if(source.isSet(Utils.CONF_LORE))
						result.setLore(source.getStringList(Utils.CONF_LORE));
					if(source.isSet(Utils.CONF_ENCHANTMENTS)){
						for(Entry<Enchantment, Integer> entry : this.getEnchantmentMap(Utils.CONF_ENCHANTMENTS).entrySet()){
							result.addEnchant(entry.getKey(), entry.getValue(), true);
						}
					}
					return result;
				}
			}
		}catch(Exception e){
			KittysLilHelpers.logWarning("ConfigSection.getItemMeta (" + this.getCurrentPath() + "\\" +  path + "): " + e.getMessage());
		}
		return null;
	}
	
	public boolean isItemMeta(String path){
		return this.isType(path, Utils.VALUE_ITEMMETA);
	}
	
	private void setItemMeta(String path, ItemMeta value){
		ConfigSection dest = this.createSection(path);
		dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA);

		if(value.hasDisplayName())
			dest.set(Utils.CONF_DISPLAYNAME, value.getDisplayName());
		if(value.hasLore())
			dest.set(Utils.CONF_LORE, value.getLore());
		if(value.hasEnchants())
			dest.set(Utils.CONF_ENCHANTMENTS, value.getEnchants());
		
		if(value instanceof BookMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_BOOK);
			BookMeta book = (BookMeta)value;
			if(book.hasAuthor())
				dest.set(Utils.CONF_AUTHOR, book.getAuthor());
			if(book.hasTitle())
				dest.set(Utils.CONF_TITLE, book.getTitle());
			if(book.hasPages()){
				dest.set(Utils.CONF_PAGES, book.getPages());
			}
		}else if(value instanceof EnchantmentStorageMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_ENCHANTMENT_STORAGE);
			EnchantmentStorageMeta e = (EnchantmentStorageMeta)value;
			if(e.hasStoredEnchants())
				dest.set(Utils.CONF_STORED_ENCHANTMENTS, e.getStoredEnchants());
		}else if(value instanceof FireworkEffectMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_FIREWORK_EFFECT);
			FireworkEffectMeta effect = (FireworkEffectMeta)value;
			if(effect.hasEffect()){
				
			}
		}else if(value instanceof FireworkMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_FIREWORK);
			FireworkMeta firework = (FireworkMeta)value;
			if(firework.hasEffects()){
				
			}
		}else if(value instanceof LeatherArmorMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_LEATHER_ARMOR);
			LeatherArmorMeta armor = (LeatherArmorMeta)value;
			dest.set(Utils.CONF_COLOR, armor.getColor());
		}else if(value instanceof MapMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_MAP);
			MapMeta map = (MapMeta)value;
			dest.set(Utils.CONF_IS_SCALING, map.isScaling());
		}else if(value instanceof PotionMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_POTION);
			PotionMeta potion = (PotionMeta)value;
			if(potion.hasCustomEffects()){
				for(PotionEffect effect : potion.getCustomEffects()){
					ConfigurationSection fxstore = dest.createSection(effect.getType().getName());
					fxstore.set(Utils.CONF_AMPLIFIER, effect.getAmplifier());
					fxstore.set(Utils.CONF_DURATION, effect.getDuration());
					fxstore.set(Utils.CONF_IS_AMBIENT, effect.isAmbient());
				}
			}
		}else if(value instanceof SkullMeta){
			dest.set(Utils.KEY_TYPE_NAME, Utils.VALUE_ITEMMETA_SKULL);
			SkullMeta skull = (SkullMeta)value;
			if(skull.hasOwner())
				dest.set(Utils.CONF_OWNER, skull.getOwner());
		}
	}
	
	public Location getLocation(String path, Location def){
		Location result = this.getLocation(path);
		if(result==null)
			return def;
		else
			return result;
	}
	
	public Location getLocation(String path){
		Location result = null;
		try{
			ConfigSection loc = this.getConfigurationSection(path);
			World w = Bukkit.getWorld(UUID.fromString(loc.getString(Utils.CONF_WORLD)));
			result = new Location(w, loc.getDouble(Utils.CONF_X), loc.getDouble(Utils.CONF_Y), loc.getDouble(Utils.CONF_Z));
			result.setDirection(loc.getVector(Utils.CONF_DIRECTION, new Vector(0,0,1)));
			result.setPitch((float)loc.getDouble(Utils.CONF_PITCH, 0));
			result.setYaw((float)loc.getDouble(Utils.CONF_YAW, 0));
		}catch(Exception e){
			
		}
		return result;
	}
	
	public Boolean isLocation(String path){
		return this.isType(path, Utils.VALUE_LOCATION);
	}
	
	private void setLocation(String path, Location value){
		ConfigSection loc = this.createSection(path);
		loc.set(Utils.KEY_TYPE_NAME, Utils.VALUE_LOCATION);
		loc.set(Utils.CONF_WORLD, Utils.VALUE_EMPTY + value.getWorld().getUID());
		loc.set(Utils.CONF_X, value.getX());
		loc.set(Utils.CONF_Y, value.getY());
		loc.set(Utils.CONF_Z, value.getZ());
		if(value.getDirection().getX()!=0 && value.getDirection().getBlockY()!=0 && value.getDirection().getZ()!=1)
			loc.set(Utils.CONF_DIRECTION, value.getDirection());			
		if(value.getPitch()!=0)
			loc.set(Utils.CONF_PITCH, value.getPitch());
		if(value.getYaw()!=0)
			loc.set(Utils.CONF_YAW, value.getYaw());
	}
	
	public Permission getPermission(String path, Permission def){
		Permission result = this.getPermission(path);
		if(result==null)
			return def;
		else
			return result;
	}
	
	public Permission getPermission(String path){
		try{
			ConfigSection perm = this.getConfigurationSection(path);
			if(perm.getString(Utils.KEY_TYPE_NAME, Utils.VALUE_EMPTY).compareToIgnoreCase(Utils.VALUE_PERMISSION)==0){
				String name = perm.getString(Utils.CONF_NAME);
				Permission p = Bukkit.getPluginManager().getPermission(name);
				if(p==null){
					p = new Permission(name);
					p.setDefault(perm.getPermissionDefault(Utils.CONF_DEFAULT));
					p.setDescription(perm.getString(Utils.CONF_DESCRIPTION));
					Bukkit.getPluginManager().addPermission(p);
				}
				return p;
			}
		}catch(Exception e){
		}
		return null;
	}
	
	public Boolean isPermission(String path){
		return this.isType(path, Utils.VALUE_PERMISSION);
	}
	
	private void setPermission(String path, Permission value){
		ConfigSection perm = this.createSection(path);
		perm.set(Utils.KEY_TYPE_NAME, Utils.VALUE_PERMISSION);
		perm.set(Utils.CONF_NAME, value.getName());
		perm.set(Utils.CONF_DESCRIPTION, value.getDescription());
		perm.set(Utils.CONF_DEFAULT, value.getDefault());
	}
	
	private ConfigurationSection getSource(){
		return this.source;
	}
	
	@Override
	public void addDefault(String path, Object value) {
		this.getSource().addDefault(path, value);
	}

	@Override
	public boolean contains(String path) {
		return this.getSource().contains(path);
	}

	@Override
	public ConfigSection createSection(String path) {
		return new ConfigSection(this.container, this.getSource().createSection(path));
	}

	@Override
	public ConfigSection createSection(String path, Map<?, ?> map) {
		return new ConfigSection(this.container, this.getSource().createSection(path, map));
	}

	@Override
	public boolean getBoolean(String path) {
		return this.getSource().getBoolean(path);
	}

	@Override
	public boolean getBoolean(String path, boolean def) {
		return this.getSource().getBoolean(path, def);
	}

	@Override
	public List<Boolean> getBooleanList(String path) {
		return this.getSource().getBooleanList(path);
	}

	@Override
	public List<Byte> getByteList(String path) {
		return this.getSource().getByteList(path);
	}

	@Override
	public List<Character> getCharacterList(String path) {
		return this.getSource().getCharacterList(path);
	}

	@Override
	public Color getColor(String path) {
		return this.getSource().getColor(path);
	}

	@Override
	public Color getColor(String path, Color def) {
		return this.getSource().getColor(path, def);
	}

	@Override
	public ConfigSection getConfigurationSection(String path) {
		if(this.isConfigurationSection(path))
			return new ConfigSection(this.container, this.getSource().getConfigurationSection(path));
		else
			return null;
	}
	public ConfigSection getConfigurationSection(String path, Boolean create) {
		if(this.isConfigurationSection(path)){
			return this.getConfigurationSection(path);
		}else if(create==true){
			return this.createSection(path);
		}else{
			return null;
		}
	}

	@Override
	public String getCurrentPath() {
		return this.getSource().getCurrentPath();
	}

	@Override
	public ConfigSection getDefaultSection() {
		return new ConfigSection(this.container, this.getSource().getDefaultSection());
	}

	@Override
	public double getDouble(String path) {
		return this.getSource().getDouble(path);
	}

	@Override
	public double getDouble(String path, double def) {
		return this.getSource().getDouble(path, def);
	}

	@Override
	public List<Double> getDoubleList(String path) {
		return this.getSource().getDoubleList(path);
	}

	@Override
	public List<Float> getFloatList(String path) {
		return this.getSource().getFloatList(path);
	}

	@Override
	public int getInt(String path) {
		return this.getSource().getInt(path);
	}

	@Override
	public int getInt(String path, int def) {
		return this.getSource().getInt(path, def);
	}

	@Override
	public List<Integer> getIntegerList(String path) {
		return this.getSource().getIntegerList(path);
	}

	@Override
	public Set<String> getKeys(boolean deep) {
		return this.getSource().getKeys(deep);
	}

	@Override
	public List<?> getList(String path) {
		return this.getSource().getList(path);
	}

	@Override
	public List<?> getList(String path, List<?> def) {
		return this.getSource().getList(path, def);
	}

	@Override
	public long getLong(String path) {
		return this.getSource().getLong(path);
	}

	@Override
	public long getLong(String path, long def) {
		return this.getSource().getLong(path, def);
	}

	@Override
	public List<Long> getLongList(String path) {
		return this.getSource().getLongList(path);
	}

	@Override
	public List<Map<?, ?>> getMapList(String path) {
		return this.getSource().getMapList(path);
	}

	@Override
	public String getName() {
		return this.getSource().getName();
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path) {
		return this.getSource().getOfflinePlayer(path);
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def) {
		return this.getSource().getOfflinePlayer(path, def);
	}

	@Override
	public ConfigSection getParent() {
		return new ConfigSection(this.container, this.getSource().getParent());
	}

	@Override
	public Configuration getRoot() {
		return this.getSource().getRoot();
	}

	@Override
	public List<Short> getShortList(String path) {
		return this.getSource().getShortList(path);
	}

	@Override
	public String getString(String path) {
		return this.getSource().getString(path);
	}

	@Override
	public String getString(String path, String def) {
		return this.getSource().getString(path, def);
	}

	@Override
	public List<String> getStringList(String path) {
		return this.getSource().getStringList(path);
	}

	@Override
	public Map<String, Object> getValues(boolean deep) {
		return this.getSource().getValues(deep);
	}

	@Override
	public Vector getVector(String path) {
		return this.getSource().getVector(path);
	}

	@Override
	public Vector getVector(String path, Vector def) {
		return this.getSource().getVector(path, def);
	}

	@Override
	public boolean isBoolean(String path) {
		return this.getSource().isBoolean(path);
	}

	@Override
	public boolean isColor(String path) {
		return this.getSource().isColor(path);
	}

	@Override
	public boolean isConfigurationSection(String path) {
		return this.getSource().isConfigurationSection(path);
	}

	@Override
	public boolean isDouble(String path) {
		return this.getSource().isDouble(path);
	}

	@Override
	public boolean isInt(String path) {
		return this.getSource().isInt(path);
	}

	@Override
	public boolean isList(String path) {
		return this.getSource().isList(path);
	}

	@Override
	public boolean isLong(String path) {
		return this.getSource().isLong(path);
	}

	@Override
	public boolean isOfflinePlayer(String path) {
		return this.getSource().isOfflinePlayer(path);
	}

	@Override
	public boolean isSet(String path) {
		return this.getSource().isSet(path);
	}

	@Override
	public boolean isString(String path) {
		return this.getSource().isString(path);
	}

	@Override
	public boolean isVector(String path) {
		return this.getSource().isVector(path);
	}
}

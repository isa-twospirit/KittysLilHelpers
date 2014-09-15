package com.github.isatwospirit.kittyslilhelpers.util;

import java.io.File;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SandstoneType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Coal;
import org.bukkit.material.Dye;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.Leaves;
import org.bukkit.material.LongGrass;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sandstone;
import org.bukkit.material.SmoothBrick;
import org.bukkit.material.SpawnEgg;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.Tree;
import org.bukkit.material.WoodenStep;
import org.bukkit.material.Wool;
import org.bukkit.permissions.PermissionDefault;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;

public class Utils {
	public static final String META_WORLDGROUP = "klh-WorldGroup";
	
	public static final String SECTION_SOURCES = "sources";
	public static final String SECTION_DESTINATIONS = "destinations";

	public final static String KEY_TYPE_NAME = "kcs-type";
	public final static String VALUE_ITEMSTACK = "kcs-ItemStack";
	public final static String VALUE_ENCHANTMENTMAP = "kcs-EnchantmentMap";
	public final static String VALUE_PERMISSION_DEFAULT = "kcs-PermissionDefault";
	public final static String VALUE_RECIPE = "kcs-Recipe";
	public final static String VALUE_RECIPE_SHAPED = "kcs-Recipe-Shaped";
	public final static String VALUE_RECIPE_SHAPELESS = "kcs-Recipe-Shapeless";
	public final static String VALUE_RECIPE_FURNACE = "kcs-Recipe-Furnace";
	public final static String VALUE_LOCATION = "kcs-Location";
	public final static String VALUE_ITEMMETA = "kcs-ItemMeta";
	public final static String VALUE_ITEMMETA_BOOK = "kcs-ItemMeta-Book";
	public final static String VALUE_ITEMMETA_ENCHANTMENT_STORAGE = "kcs-ItemMeta-EnchantmentStorage";
	public final static String VALUE_ITEMMETA_FIREWORK_EFFECT = "kcs-ItemMeta-FireworkEffect";
	public final static String VALUE_ITEMMETA_FIREWORK = "kcs-ItemMeta-Firework";
	public final static String VALUE_ITEMMETA_LEATHER_ARMOR = "kcs-ItemMeta-LeatherArmor";
	public final static String VALUE_ITEMMETA_MAP = "kcs-ItemMeta-Map";
	public final static String VALUE_ITEMMETA_POTION = "kcs-ItemMeta-Potion";
	public final static String VALUE_ITEMMETA_SKULL = "kcs-ItemMeta-Skull";
	public final static String VALUE_PERMISSION = "kcs-Permission";
	public final static String VALUE_EMPTY = "";
	public final static String VALUE_SEPARATOR = "-";
	
	public final static String CONF_PERMISSIONS = "permissions";
	public final static String CONF_PERM_OTHERS = ".others";
	public final static String CONF_NAME = "name";
	public final static String CONF_DEFAULT = "default";
	public final static String CONF_DESCRIPTION = "description";
	public final static String CONF_MATERIAL = "material";
	public final static String CONF_AMOUNT = "amount";
	public final static String CONF_ENCHANTMENTS = "enchantments";
	public final static String CONF_ITEM_META = "itemmeta";
	public final static String CONF_DURABILITY = "durability";
	public final static String CONF_RESULT = "result";
	public final static String CONF_INGREDIENTS = "ingredients";
	public final static String CONF_SHAPE = "shape_";
	public final static String CONF_INPUT = "input";
	public final static String CONF_AUTHOR = "author";
	public final static String CONF_TITLE = "title";
	public final static String CONF_PAGES = "pages";
	public final static String CONF_STORED_ENCHANTMENTS = "stored_enchantments";
	public final static String CONF_COLOR = "color";
	public final static String CONF_IS_SCALING = "is_scaling";
	public final static String CONF_DURATION = "duration";
	public final static String CONF_AMPLIFIER = "amplifier";
	public final static String CONF_IS_AMBIENT = "is_ambient";
	public final static String CONF_OWNER = "owner";
	public final static String CONF_DISPLAYNAME = "displayname";
	public final static String CONF_LORE = "lore";
	public final static String CONF_WORLD = "world";
	public final static String CONF_X = "x";
	public final static String CONF_Y = "y";
	public final static String CONF_Z = "z";
	public final static String CONF_DIRECTION = "direction";
	public final static String CONF_PITCH = "pitch";
	public final static String CONF_YAW = "yaw";
	
	private static Method playerGetHealth = null;
	
	public static String center(String s, Integer lineLength){
		String result = fixLength(s, lineLength);
		result = StringUtils.repeat(" ", (Integer)((lineLength - result.length())/2)) + result;
		return result;
	}
	
	public static String fixLength(String s, Integer lineLength){
		String result = s;
		if(result.length()>lineLength){
			result = result.substring(0, lineLength-1) + "…";
		}
		return result;
	}
	
	public static String rtrim(String s) {
	    int i = s.length()-1;
	    while (i >= 0 && Character.isWhitespace(s.charAt(i))) {
	        i--;
	    }
	    return s.substring(0,i+1);
	}
	
	public static String toMixedCase(String s){
		String out = "";
		Boolean doUpper = true;
		for(Character c : s.toCharArray()){
			if(doUpper)
				out += c.toString().toUpperCase();
			else
				out += c.toString().toLowerCase();
			if(Character.isWhitespace(c) || c.compareTo('(')==0)
				doUpper = true;
			else
				doUpper = false;
		}
		return out;
	}
	
	public static String colorize(String input, ChatColor color){
		return color + input + ChatColor.RESET;
	}
	
	public static String colorize(PermissionDefault permdef){
		switch(permdef){
			case TRUE:   return ChatColor.GREEN + "TRUE" + ChatColor.RESET;
			case FALSE:  return ChatColor.RED + "FALSE" + ChatColor.RESET;
			case OP:     return ChatColor.BLUE + "OP" + ChatColor.RESET;
			case NOT_OP: return ChatColor.DARK_BLUE + "NOT-OP" + ChatColor.RESET;
		}
		return "(undefined)";
	}	   
	
	public static String getLocationText(Location l){
		return getLocationText(l, true);
	}

	public static String getLocationText(Location l, Boolean includeWorldName){
		if(l==null)
			return formatError("(Missing)");
		else if(includeWorldName)
			return l.getWorld().getName() + ":" + l.getBlockX() + "/" + l.getBlockY() + "/" + l.getBlockZ();
		else
			return l.getBlockX() + "/" + l.getBlockY() + "/" + l.getBlockZ();
	}

	public static Inventory getInventoryFrom(Location l){
		BlockState b = l.getBlock().getState();
		if(b.getType()==Material.CHEST || b.getType()==Material.TRAPPED_CHEST){
			try{
				//Check if it's a double chest
				return ((DoubleChest)b).getInventory();
			}catch(Exception e){
				//Probably not
				return ((Chest)b).getInventory();
			}
		}else{
			return null;
		}
	}

	public static Location getInventoryLocation(Location l){
		BlockState b = l.getBlock().getState();
		if(b.getType()==Material.CHEST || b.getType()==Material.TRAPPED_CHEST){
			try{
				//Check if it's a double chest
				return ((DoubleChest)b).getLocation();
			}catch(Exception e){
				//Probably not
					return ((Chest)b).getLocation();
				}
			}else{
				return null;
			}
		}
	
	public static Location getInventoryLocation(Inventory i){
		try{
			return ((DoubleChest)i.getHolder()).getLocation();
		}catch(Exception eNoDouble){
			try{
				return ((Chest)i.getHolder()).getLocation();
			}catch(Exception eNoChest){
				return null;
			}
		}
	}

	public static String getItemName(ItemStack i){
		String name = i.getType().name();
		MaterialData d = i.getData();
		if(d.getClass()==MaterialData.class){
			
		}else if(d.getClass().isAssignableFrom(Coal.class)){
			name = ((Coal)d).getType().name();
		}else if(d.getClass().isAssignableFrom(Dye.class)){
			name = ((Dye)d).getColor().name() + " " + name;
		}else if(d.getClass().isAssignableFrom(FlowerPot.class)){
			name = name + " (" + ((FlowerPot)d).getContents().getClass().getName();
		}else if(d.getClass().isAssignableFrom(Leaves.class)){
			name = getWoodName(i.getDurability()) + " " + name;
		}else if(d.getClass().isAssignableFrom(LongGrass.class)){
			name = ((LongGrass)d).getSpecies().name() + " " + name;
		}else if(d.getClass().isAssignableFrom(Sandstone.class)){
			if(((Sandstone)d).getType()!=SandstoneType.CRACKED){
				name = ((Sandstone)d).getType().name() + " " + name;
			}
		}else if(d.getClass().isAssignableFrom(SmoothBrick.class)){
			SmoothBrick s = (SmoothBrick)d;
			if(s.getMaterial()==Material.STONE)
				name = "Stone Brick";
			else if(s.getMaterial()==Material.MOSSY_COBBLESTONE)
				name = "Mossy Stone Brick";
			else if(s.getMaterial()==Material.COBBLESTONE)
				name = "Cracked Stone Brick";
			else if(s.getMaterial()==Material.SMOOTH_BRICK)
				name = "Chiseled Stone Brick";
			else
				name = ((SmoothBrick)d).getMaterial().name() + " " + name;
		}else if(d.getClass().isAssignableFrom(SpawnEgg.class)){
			name = ((SpawnEgg)d).getSpawnedType().name() + " Egg";
		}else if(d.getClass().isAssignableFrom(Stairs.class)){
			if(i.getType()==Material.SMOOTH_STAIRS){
				name = "Stone Brick Stairs";
			}
		}else if(d.getClass().isAssignableFrom(Step.class)){
			name = ((Step)d).getMaterial().name() + " Slab";
		}else if(d.getClass().isAssignableFrom(Tree.class)){
			if(name.compareToIgnoreCase("wood")==0){
				name = getWoodName(i.getDurability());
			}else{
				name = getWoodName(i.getDurability()) + " " + name;
			}
		}else if(d.getClass().isAssignableFrom(WoodenStep.class)){
			name = getWoodName(i.getDurability()) + " Slab";
		}else if(d.getClass().isAssignableFrom(Wool.class)){
			name = ((Wool)d).getColor().name() + " " + name;
		}else if(i.getType()==Material.WOOD_STAIRS){
			name = "Oak Wood Stairs";
		//}else{
		//	System.out.println("Unimplemented MaterialData: " + name + " " + d.getClass().getName());
		}
		
		if(name.toLowerCase().endsWith("shovel")){
			name=name.substring(0, name.length()-6) + "Spade";
		}else if(name.toLowerCase().endsWith("barding")){
			name=name.substring(0, name.length()-7) + "Horse Armor";
		}
		name = toMixedCase(name.replace('_', ' '));
		if(i.getType()==Material.COBBLE_WALL){
			if(i.getDurability()==1)
				name = "Mossy Cobblestone Wall";
			else
				name = "Cobblestone Wall";
		}else if(i.getType()==Material.SKULL_ITEM){
			if(i.getDurability()==1)
				name = "Wither Skull";
			else if(i.getDurability()==2)
				name = "Zombie Head";
			else if(i.getDurability()==3)
				if(i.hasItemMeta()){
					name = ((SkullMeta)i.getItemMeta()).getOwner() + "'s Head";
				}else{
					name = "Head";					
				}
			else if(i.getDurability()==4)
				name = "Creeper Head";
			else
				name = "Skull";
		}
		return name;
	}
	
	public static String getWoodName(Short species){
		switch(species){
			case 0: return "Oak Wood";
			case 1: return "Spruce Wood";
			case 2: return "Birch Wood";
			case 3: return "Jungle Wood";
			case 4: return "Acacia Wood";
			case 5: return "Dark Oak Wood";
			default: return "[?] Wood";
		}
	}
	
	public static String formatError(String message){
		return KittysLilHelpers.COLOR_ERROR + message + ChatColor.RESET;
	}
	
	public static Boolean isEmpty(PlayerInventory inventory){
		for(ItemStack item : inventory.getContents()){
			if(item!=null){
				if(item.getAmount()!=0){
					return false;
				}
			}
		}
		for(ItemStack item : inventory.getArmorContents()){
			if(item!=null){
				if(item.getAmount()!=0){
					return false;
				}
			}
		}
		return true;
	}
	
	public static double getPlayerHealth(Player p){
		try{
			if(playerGetHealth==null){
				for(Method m : Player.class.getMethods()){
					if(m.getName().compareTo("getHealth")==0){
						if(m.getReturnType()==double.class){
							playerGetHealth = m;
							break;
						}
					}
					
				}
			}
			return (Double)playerGetHealth.invoke(p);
		}catch(Exception e){
			return 20;
		}
	}
	
	public static File findFile(File directory, String name, Boolean deep){
		File result=null;
		if(directory.isDirectory()){
			for(File check : directory.listFiles()){
				if(check.getName().compareTo(name)==0){
					result=check;
					break;
				}else if(check.isDirectory() && deep){
					result=findFile(check, name, true);
					if(result!=null)
						break;
				}
			}
		}
		return result;
	}
}

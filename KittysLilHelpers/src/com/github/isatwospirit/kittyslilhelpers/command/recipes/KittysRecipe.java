package com.github.isatwospirit.kittyslilhelpers.command.recipes;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.github.isatwospirit.kittyslilhelpers.util.Comparers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class KittysRecipe {
	public enum DescriptionFormat{
		LONG,
		SHORT,
		COOKBOOK
	}
	
	private final static String PERM_RECIPE_USE = "kittyslilhelpers.recipe.use";
	
	private String name="";
	private Recipe recipe=null;
	private Boolean isEnabled=false;
	private Boolean isDeleted=false;
	private ConfigSection config=null;
	private PermissionDefault permDefault=null;
	
	public KittysRecipe(ConfigSection section){
		this.name = section.getName();
		if(this.name.startsWith("_"))
			this.name=this.name.substring(1);
		this.config = section;
		this.permDefault=section.getPermissionDefault("permission_default", null);
		this.recipe = section.getRecipe("recipe");
		this.isEnabled = section.getBoolean("is_enabled", true);
		this.activate();
	}
	
	public KittysRecipe(String name, Inventory source, ConfigSection parent, boolean isEnabled) throws Exception{
		this.name = name;
		if(parent.isConfigurationSection(name)){
			throw new Exception("Custom recipe already exists.");
		}else{
			ItemStack result = source.getItem(17);
			if(result==null){
				throw new Exception("No resulting item set.");
			}else if(source.getItem(15)!=null){
				//Furnace recipe
				this.recipe = new FurnaceRecipe(result, source.getItem(15).getType());
			}else if(source.getItem(3)!=null || source.getItem(4)!=null || source.getItem(5)!=null ||
					 source.getItem(12)!=null || source.getItem(13)!=null || source.getItem(14)!=null ||
					 source.getItem(21)!=null || source.getItem(22)!=null || source.getItem(23)!=null){
				//Shapeless recipe
				ShapelessRecipe recipe = new ShapelessRecipe(result);
				for(Integer row=3; row<22; row+=9){
					for(Integer col=0; col<3; col++){
						ItemStack ingredient = source.getItem(row + col);
						if(ingredient!=null){
							Integer amount = ingredient.getAmount();
							if(amount>9)
								amount=9;
							recipe.addIngredient(amount, ingredient.getData());
						}
					}
				}
				if(recipe.getIngredientList().size()>0){
					this.recipe = recipe;
				}
			}else if(source.getItem(0)!=null || source.getItem(1)!=null || source.getItem(2)!=null ||
					 source.getItem(9)!=null || source.getItem(10)!=null || source.getItem(11)!=null ||
					 source.getItem(18)!=null || source.getItem(19)!=null || source.getItem(20)!=null){
				//Shaped recipe
				ShapedRecipe recipe=new ShapedRecipe(result);
				String[] shape = new String[3];
				HashMap<Character, MaterialData>ingredients = new HashMap<Character, MaterialData>();
				for(Integer row=0; row<3; row++){
					shape[row]="";
					for(Integer col=0; col<3; col++){
						Integer index = row*9+col;
						ItemStack ingredient = source.getItem(index);
						if(ingredient!=null){
							shape[row] += this.getIngredientKey(ingredients, ingredient.getData());
						}else{
							shape[row] += " ";
						}
					}
				}
				setShape(recipe, shape);
				
				for(Entry<Character, MaterialData>entry : ingredients.entrySet()){
					recipe.setIngredient(entry.getKey(), entry.getValue());
				}
				
				if(recipe.getIngredientMap().size()>0){
					this.recipe = recipe;
				}
			}
		}
		if(this.recipe==null)
			throw new Exception("No recipe set.");
		
		this.isEnabled = isEnabled;
		this.save(parent);
		this.activate();
	}
	
	public KittysRecipe(String name, Recipe recipe, ConfigSection parent, boolean isEnabled){
		this.name = name;
		this.recipe = recipe;
		this.isEnabled = isEnabled;
		this.save(parent);
		this.activate();
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		return ChatColor.BLUE + this.getName() + ChatColor.RESET;
	}
	
	public String getDescription(DescriptionFormat format){
		String description = this.getDisplayName();
		String separator = "\n";
		/*
		 * Name
		 * Status:       enabled/disabled
		 * Default Perm: TRUE/FALSE/OP/NOT_OP
		 * Type:         Shaped/Shapeless/Furnace
		 * Input:        ...
		 * Output:       x Material
		 */

		if(format==DescriptionFormat.SHORT){
			description += ": ";
			if(this.isDeleted)
				description += Utils.colorize("deleted", ChatColor.DARK_RED) + ", ";
			else if(this.isEnabled)
				description += Utils.colorize("enabled", ChatColor.GREEN) + ", ";
			else
				description += Utils.colorize("disabled", ChatColor.RED) + ", ";
			description += Utils.colorize(this.getPermissionDefault()) + ", ";
			description += this.getTypeName();
			return description;
		}
		if(format==DescriptionFormat.LONG){
			description += "\nStatus:       ";
			separator = " ";
			if(this.isDeleted)
				description += Utils.colorize("deleted", ChatColor.DARK_RED) + "\n";
			else if(this.isEnabled)
				description += Utils.colorize("enabled", ChatColor.GREEN) + "\n";
			else
				description += Utils.colorize("disabled", ChatColor.RED) + "\n";
			
			description += "Default Perm: " + Utils.colorize(this.getPermissionDefault());
			description += "\nRecipe Type:  " + this.getTypeName() 
						 + "\nOutput:       " + this.getRecipe().getResult().getAmount() + " " + Utils.getItemName(this.getRecipe().getResult())
						 + "\nInput:        ";
	
			if(this.getRecipe() instanceof ShapelessRecipe){
				for(ItemStack ingredient : ((ShapelessRecipe)this.getRecipe()).getIngredientList()){
					description += Utils.getItemName(ingredient) + separator;
				}
			}else if(this.getRecipe() instanceof ShapedRecipe){
				String ingr[][] = new String[3][3];
				for(Integer row=0; row<3; row++){
					for(Integer col=0; col<3; col++){
						ItemStack ingredient = getIngredientAt((ShapedRecipe)this.getRecipe(), row, col);
						if(ingredient==null){
							ingr[row][col] = Utils.colorize("(Empty)", ChatColor.DARK_GREEN);
						}else{
							ingr[row][col] = Utils.colorize(Utils.getItemName(ingredient), ChatColor.GREEN);
						}
					}
				}
				for(Integer col=0; col<3; col++){
					Integer longest=0;
					for(Integer row=0; row<3; row++){
						if(ingr[row][col].length()>longest)
							longest=ingr[row][col].length();
					}
					for(Integer row=0; row<3; row++){
						ingr[row][col] = ingr[row][col] + StringUtils.repeat(" ", longest + 2 - ingr[row][col].length());
					}
				}
				for(Integer row=0; row<3; row++){
					if(row>0)
						description += "\n              ";
					for(Integer col=0; col<3; col++){
						description += ingr[row][col];
					}
				}
			}else if(this.getRecipe() instanceof FurnaceRecipe){
				description += Utils.getItemName(((FurnaceRecipe)this.getRecipe()).getInput());
			}
		}else if(format==DescriptionFormat.COOKBOOK){
			ItemStack result = this.getRecipe().getResult();
			String line = ""; 
			if(result.getAmount() > 1){
				line = result.getAmount() + " " + Utils.getItemName(result);
				if(line.toLowerCase().endsWith("s")==false)
					line += "s";
			}else{
				line = Utils.getItemName(result);
			}
			description = ChatColor.BLUE + line + "\n\n" + ChatColor.BLACK;
			
			if(this.getRecipe() instanceof ShapelessRecipe){
				description += "Ingredients:\n";
				for(ItemStack ingr : ((ShapelessRecipe)this.getRecipe()).getIngredientList()){
					description += Utils.fixLength(" - " + Utils.getItemName(ingr), 20) + "\n";
				}
			}else if(this.getRecipe() instanceof ShapedRecipe){
				description += "Ingredients:\n";
				for(Entry<Character, ItemStack> ingredient : ((ShapedRecipe)this.getRecipe()).getIngredientMap().entrySet()){
					try{
						description += Utils.fixLength(" (" + ingredient.getKey() + ") " + Utils.getItemName(ingredient.getValue()), 20) + "\n";					
					}catch(Exception e){}
				}
				description += " (0) Empty Slot\n";
				description += "Shape:\n";
				for(Integer i=0;i<3;i++){
					try{
						description += "      " + ((ShapedRecipe)this.getRecipe()).getShape()[i].replace(" ", "0") + "\n";
					}catch(Exception e){
						
					}
				}
			}else if(this.getRecipe() instanceof FurnaceRecipe){
				description += "Input:\n"
						    + Utils.fixLength(Utils.getItemName(((FurnaceRecipe)this.getRecipe()).getInput()), 20) + "\n"
						    + "Put into oven and smelt.";
			}
		}
		
		return description;
	}
	
	public Recipe getRecipe(){
		return this.recipe;
	}
	
	public String getPermissionName(){
		return PERM_RECIPE_USE + "." + this.getName();
	}
	
	public PermissionDefault getPermissionDefault(){
		if(this.permDefault==null)
			return this.config.getParent().getPermissionDefault("UseRecipePermissionDefault", PermissionDefault.TRUE);
		else
			return this.permDefault;
	}
	
	public void setPermissionDefault(PermissionDefault permissionDefault){
		this.permDefault = permissionDefault;
		this.config.set("PermissionDefault", this.permDefault);
		this.config.save();
		setPermission();
	}

	public Boolean isDeleted(){
		return this.isDeleted;
	}
	
	public Boolean isEnabled(){
		if(this.isDeleted()==true)
			return false;
		return this.isEnabled;
	}
	
	public void setEnabled(Boolean isEnabled){
		if(this.isEnabled!=isEnabled){
			this.isEnabled=isEnabled;
			this.config.set("is_enabled", this.isEnabled);
			this.config.save();
			KittysRecipes.getInstance().nextRevision();
		}
	}
	
	public boolean delete(){
		this.isDeleted=true;
		return true;
	}
	
	private boolean save(ConfigSection parent){
		if(parent.isConfigurationSection(this.name))
			return false;

		this.config = parent.createSection(this.name);
		this.config.set("is_enabled", this.isEnabled);
		this.config.set("permission_default", this.permDefault);
		this.config.set("recipe", this.getRecipe());		
		this.config.save();
		return true;
	}

	private Character getIngredientKey(HashMap<Character, MaterialData>ingredients, MaterialData ingredient){
		Character result = ' ';
		for(Entry<Character, MaterialData> entry : ingredients.entrySet()){
			if(entry.getValue().equals(ingredient)){
				result=entry.getKey();
				break;
			}
		}
		if(result==' '){
			result = String.valueOf(ingredients.size()+1).charAt(0);
			ingredients.put(result, ingredient);
		}
		return result;
	}

	public static void setShape(ShapedRecipe r, String[] shape){
		String[] oShape = new String[3];
		Integer index = 0;
		Integer maxLen = 0;
		
		if(shape[0]!=null){
			if(shape[0].trim().length()>0){
				oShape[0]=Utils.rtrim(shape[0]);
				maxLen = oShape[0].length();
				index+=1;
			}
		}
		if(shape[1]!=null || index>0){
			if(shape[1].trim().length()>0){
				oShape[index]=Utils.rtrim(shape[1]);
				if(oShape[index].length()>maxLen)
					maxLen=oShape[index].length();
			}else{
				oShape[index]="";
			}
			index+=1;
		}
		if(shape[2]!=null){
			if(shape[2].trim().length()>0){
				oShape[index]=Utils.rtrim(shape[2]);
				if(oShape[index].length()>maxLen)
					maxLen=oShape[index].length();
			}else{
				oShape[index]=null;
				index-=1;
				if(oShape[index].compareToIgnoreCase("   ")==0){
					oShape[index]=null;
					index-=1;
				}
			}
		}else{
			oShape[index]=null;
			index-=1;
			if(oShape[index].compareToIgnoreCase("   ")==0){
				oShape[index]=null;
				index-=1;
			}
		}
		
		for(Integer counter=0; counter<3; counter++){
			if(oShape[counter]!=null){
				while(oShape[counter].length()<maxLen){
					oShape[counter]+=" ";
				}
			}
		}
		
		if(index==0){
			r.shape(oShape[0]);
		}else if(index==1){
			r.shape(oShape[0], oShape[1]);
		}else if(index==2){
			r.shape(oShape[0], oShape[1], oShape[2]);
		}
	}
	
	private void activate(){
		Bukkit.addRecipe(this.getRecipe());
		setPermission();
	}
	
	private void setPermission(){
		PluginManager pm = Bukkit.getPluginManager();
		Permission p = pm.getPermission(PERM_RECIPE_USE + "." + this.getName());
		if(p==null){
			p = new Permission(PERM_RECIPE_USE + "." + this.getName(), "Allows to use custom recipe " + this.getName(), this.getPermissionDefault());
			pm.addPermission(p);
		}else{
			p.setDefault(this.getPermissionDefault());
		}
	}

	public static ItemStack getIngredientAt(ShapedRecipe r, Integer row, Integer col){
		try{
			Character key = r.getShape()[row].charAt(col);
			return r.getIngredientMap().get(key);
		}catch(Exception e){
			return null;
		}
	}

	public boolean equals(Recipe r, boolean IgnoreOutput){
		return Comparers.recipeEquals(this.getRecipe(), r, IgnoreOutput);
	}
	
	public boolean equals(Recipe r){
		return Comparers.recipeEquals(this.getRecipe(), r);
	}
	
	public boolean equals(KittysRecipe r){
		if(r==null){
			return false;
		}else{
			return Comparers.recipeEquals(this.getRecipe(), r.getRecipe());
		}
	}
	
	public String getTypeName(){
		if(this.getRecipe()==null){
			return "NULL";
		}else if(this.getRecipe() instanceof FurnaceRecipe){
			return "Furnace";
		}else if(this.getRecipe() instanceof ShapedRecipe){
			return "Shaped";
		}else if(this.getRecipe() instanceof ShapelessRecipe){
			return "Shapeless";
		}else{
			return "Unknown type: " + this.recipe.getClass().getSimpleName();
		}
		
	}
}

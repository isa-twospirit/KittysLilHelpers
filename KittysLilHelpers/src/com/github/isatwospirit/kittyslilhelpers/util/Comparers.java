package com.github.isatwospirit.kittyslilhelpers.util;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.github.isatwospirit.kittyslilhelpers.command.recipes.KittysRecipe;

public final class Comparers {
	public static boolean recipeEquals(Recipe r1, Recipe r2){
		return recipeEquals(r1, r2, false);
	}
	
	public static boolean recipeEquals(Recipe r1, Recipe r2, boolean IgnoreOutput){
		if(!canEqual(r1, r2)){
			return false;
		}else if(r1==null){
			return true;
		}else if(itemStackEquals(r1.getResult(), r2.getResult())==false && IgnoreOutput==false){
			return false;
		}else if(r1.getClass()==FurnaceRecipe.class){
			FurnaceRecipe f1 = (FurnaceRecipe)r1;
			FurnaceRecipe f2 = (FurnaceRecipe)r2;
			if(!itemStackEquals(f1.getInput(), f2.getInput())){
				//System.out.println("FurnaceRecipe: Different input.");
				return false;
			}
		}else if(r1.getClass()==ShapelessRecipe.class){
			ShapelessRecipe s1 = (ShapelessRecipe)r1;
			ShapelessRecipe s2 = (ShapelessRecipe)r2;
			if(s1.getIngredientList()!=s2.getIngredientList()){
				//System.out.println("ShapelessRecipe: Different ingredient list.");
				return false;
			}
		}else if(r1.getClass()==ShapedRecipe.class){
			ShapedRecipe s1 = (ShapedRecipe)r1;
			ShapedRecipe s2 = (ShapedRecipe)r2;
			for(Integer row=0; row<3; row++){
				for(Integer col=0; col<3; col++){
					if(!itemStackEquals(KittysRecipe.getIngredientAt(s1, row, col), KittysRecipe.getIngredientAt(s2, row, col))){
						//System.out.println("ShapedRecipe: Different ingredient at " + row + "/" + col + ".");
						return false;
					}
				}
			}
		}else{
			return false;
		}
		return true;
	}

	public static boolean itemStackEquals(ItemStack i1, ItemStack i2){
		if(!canEqual(i1, i2)){
			return false;
		}else if(i1==null && i2==null){
			return true;
		}else if(i1.getAmount()!=i2.getAmount()){
			return false;
		}else if(i1.getDurability()!=i2.getDurability()){
			return false;
		}else if(i1.getType()!=i2.getType()){
			return false;
		}else if(!materialDataEquals(i1.getData(), i2.getData())){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean materialDataEquals(MaterialData m1, MaterialData m2){
		if(!canEqual(m1, m2)){
			return false;
		}
		if(m1==null && m2==null){
			return true;
		}else if(m1.getData()!=m2.getData()){
			return false;
		}
		return true;
	}
	
	public static boolean canEqual(Object o1, Object o2){
		if(o1==null && o2==null){
			return true;
		}else if((o1==null && o2!=null) || (o1!=null && o2==null)){
			return false;
		}else if(!o1.getClass().isAssignableFrom(o2.getClass())){
			return false;
		}
		return true;
	}
}

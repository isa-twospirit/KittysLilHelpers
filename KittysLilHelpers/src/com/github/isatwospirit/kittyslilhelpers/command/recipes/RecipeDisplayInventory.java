package com.github.isatwospirit.kittyslilhelpers.command.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeDisplayInventory implements CraftingInventory, FurnaceInventory {
	private Player owner = null;
	private KittysRecipe recipe;
	private FurnaceRecipe fRecipe;
	private ShapedRecipe sRecipe;
	private ShapelessRecipe lRecipe;
	
	public RecipeDisplayInventory(Player owner, KittysRecipe recipe){
		this.owner = owner;
		this.recipe = recipe;
		
		if(this.recipe.getRecipe() instanceof FurnaceRecipe){
			this.fRecipe = (FurnaceRecipe)this.recipe.getRecipe();
		}else if(this.recipe.getRecipe() instanceof ShapedRecipe){
			this.sRecipe = (ShapedRecipe)this.recipe.getRecipe();
		}else if(this.recipe.getRecipe() instanceof ShapelessRecipe){
			this.lRecipe = (ShapelessRecipe)this.recipe.getRecipe();
		}
	}
	
	public void show(){
		this.owner.openInventory(this);
	}
	
	@Override
	public HashMap<Integer, ItemStack> addItem(ItemStack... items)
			throws IllegalArgumentException {
		throw new IllegalArgumentException("This inventory is read-only.");
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(int materialId) {
		return null;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material material)
			throws IllegalArgumentException {
		throw new IllegalArgumentException("This inventory is read-only");
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public void clear(int index) {
	}

	@Override
	public boolean contains(int materialId) {
		return false;
	}

	@Override
	public boolean contains(Material material) throws IllegalArgumentException {
		return false;
	}

	@Override
	public boolean contains(ItemStack item) {
		return false;
	}

	@Override
	public boolean contains(int materialId, int amount) {
		return false;
	}

	@Override
	public boolean contains(Material material, int amount) {
		return false;
	}

	@Override
	public boolean contains(ItemStack item, int amount) {
		return false;
	}

	@Override
	public boolean containsAtLeast(ItemStack item, int amount) {
		return false;
	}

	@Override
	public int first(int materialId) {
		return 0;
	}

	@Override
	public int first(Material material) throws IllegalArgumentException {
		throw new IllegalArgumentException("This inventory is read-only.");
	}

	@Override
	public int first(ItemStack item) {
		return 0;
	}

	@Override
	public int firstEmpty() {
		return 0;
	}

	@Override
	public ItemStack[] getContents() {
		ItemStack[] result = null;
		if(this.sRecipe!=null){
			result = new ItemStack[9];
			for(Integer row=0; row<3; row++){
				for(Integer col=0; col<3; col++){
					result[row*3+col] = KittysRecipe.getIngredientAt(sRecipe, row, col);
				}
			}
		}else if(this.lRecipe!=null){
			result = (ItemStack[]) lRecipe.getIngredientList().toArray();
		}else if(this.fRecipe!=null){
			result = new ItemStack[1];
			result[0] = this.fRecipe.getInput();
		}
		return result;
	}

	public Furnace getHolder() {
		return null;
	}
	
	@Override
	public ItemStack getItem(int index) {
		return this.getContents()[index];
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public String getName() {
		return this.recipe.getName();
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public String getTitle() {
		if(this.sRecipe!=null){
			return "Shaped Recipe";
		}else if(this.lRecipe!=null){
			return "Shapeless Recipe";
		}else if(this.fRecipe!=null){
			return "Furnace Recipe";
		}else{
			return "Whatever.";
		}
	}

	@Override
	public InventoryType getType() {
		if(this.fRecipe!=null){
			return InventoryType.FURNACE;
		}else{
			return InventoryType.CRAFTING;
		}
	}

	@Override
	public List<HumanEntity> getViewers() {
		List<HumanEntity>viewers = new ArrayList<HumanEntity>();
		viewers.add((HumanEntity)this.owner);
		return viewers;
	}

	@Override
	public ListIterator<ItemStack> iterator() {
		return null;
	}

	@Override
	public ListIterator<ItemStack> iterator(int index) {
		return null;
	}

	@Override
	public void remove(int materialId) {
	}

	@Override
	public void remove(Material material) throws IllegalArgumentException {
	}

	@Override
	public void remove(ItemStack item) {
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... items)
			throws IllegalArgumentException {
		throw new IllegalArgumentException("This inventory is read-only.");
	}

	@Override
	public void setContents(ItemStack[] items) throws IllegalArgumentException {
	}

	@Override
	public void setItem(int index, ItemStack item) {
	}

	@Override
	public void setMaxStackSize(int size) {
	}

	@Override
	public ItemStack[] getMatrix() {
		return this.getContents();
	}

	@Override
	public Recipe getRecipe() {
		return this.recipe.getRecipe();
	}

	@Override
	public ItemStack getResult() {
		return this.recipe.getRecipe().getResult();
	}

	@Override
	public void setMatrix(ItemStack[] arg0) {
	}

	@Override
	public void setResult(ItemStack arg0) {
	}

	@Override
	public ItemStack getFuel() {
		return null;
	}

	@Override
	public ItemStack getSmelting() {
		return this.fRecipe.getInput();
	}

	@Override
	public void setFuel(ItemStack arg0) {
	}

	@Override
	public void setSmelting(ItemStack arg0) {
	}
}

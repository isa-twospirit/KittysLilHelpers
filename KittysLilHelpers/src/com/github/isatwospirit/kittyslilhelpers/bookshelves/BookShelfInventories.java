package com.github.isatwospirit.kittyslilhelpers.bookshelves;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigFile;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class BookShelfInventories implements Listener {
	private static String BOOKSHELF = "Bookshelf";
	private static Integer BOOKSHELF_SIZE = 54;
	private static BookShelfInventories instance;
	private Map<Location, Inventory> bookshelves;
	private ConfigFile config = null;

	public static BookShelfInventories getInstance(){
		if(instance==null)
			instance = new BookShelfInventories();
		return instance;
	}
	
	private BookShelfInventories(){
		this.bookshelves = new HashMap<Location, Inventory>();
		this.config = new ConfigFile("bookshelves");
		Bukkit.getPluginManager().registerEvents(this, KittysLilHelpers.getInstance());
		KittysLilHelpers.logInfo("Loading Bookshelves...");
		ConfigSection root = this.config.getRootSection();
		for(String name : root.getKeys(false)){
			try{
				if(root.isConfigurationSection(name)){
					this.addFromConfig(root.getConfigurationSection(name));
				}
			}catch(Exception e){
			}
		}
		KittysLilHelpers.logInfo("Done, " + this.bookshelves.size() + " Bookshelves loaded.");
	}
	
	public Integer size(){
		return this.bookshelves.size();
	}
	
	public Inventory get(Location l){
		if(this.bookshelves.containsKey(l)){
			return this.bookshelves.get(l);
		}else{
			Inventory i = Bukkit.createInventory(null, BOOKSHELF_SIZE, BOOKSHELF);
			this.bookshelves.put(l, i);
			return i;
		}
		
	}
	
	public Location getKey(Inventory i){
		for(Entry<Location, Inventory>entry : this.bookshelves.entrySet()){
			if(entry.getValue().equals(i)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public void put(Location l, Inventory i){
		this.bookshelves.put(l, i);
	}
	
	public void remove(Location l){
		if(this.contains(l)){
			Inventory i = this.get(l);
			for(ItemStack item : i.getContents()){
				if(item!=null)
					l.getWorld().dropItemNaturally(l, item);
			}
			this.bookshelves.remove(l);
			this.config.getRootSection().set(Utils.getLocationText(l, true), null);
			this.config.save();
		}
	}
	
	public Boolean contains(Location l){
		return this.bookshelves.containsKey(l);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent pie){
		if(pie.getClickedBlock()!=null 
				&& pie.getPlayer().isSneaking()==false 
				&& (pie.getAction()==Action.RIGHT_CLICK_BLOCK || pie.getAction()==Action.RIGHT_CLICK_AIR)){
			if(pie.getClickedBlock().getType()==Material.BOOKSHELF){
				pie.getPlayer().openInventory(this.get(pie.getClickedBlock().getLocation()));
				pie.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent ide){
		if(this.isBookShelf(ide.getInventory())){
			for(ItemStack item : ide.getNewItems().values()){
				if(!this.isBookItem(item)){
					ide.setCancelled(true);
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryMove(InventoryMoveItemEvent ime){
		if(this.isBookShelf(ime.getDestination())){
			ime.setCancelled(!this.isBookItem(ime.getItem()));
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent ice){
		if(this.isBookShelf(ice.getInventory())){
			if(this.isBookItem(ice.getCurrentItem())){
				ice.setCancelled(!this.isBookItem(ice.getCursor()));
			}else{
				ice.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent ice){
		if(this.isBookShelf(ice.getInventory())){
			Location l = this.getKey(ice.getInventory());
			this.saveToConfig(l, ice.getInventory());
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent bbe){
		if(this.contains(bbe.getBlock().getLocation())){
			this.remove(bbe.getBlock().getLocation());
		}
	}
	
	private boolean isBookShelf(Inventory inventory){
		return(inventory.getName().compareToIgnoreCase("Bookshelf")==0);
	}
	
	private boolean isBookItem(ItemStack item){
		if(item==null)
			return true;
		return(item.getType()==Material.BOOK 
			|| item.getType()==Material.BOOK_AND_QUILL
			|| item.getType()==Material.ENCHANTED_BOOK
			|| item.getType()==Material.WRITTEN_BOOK
			|| item.getType()==Material.PAPER
			|| item.getType()==Material.AIR);
		
		//AIR is not exactly a book... But it's okay to put some AIR into a bookshelf.
	}

	private void addFromConfig(ConfigSection source){
		Location l = source.getLocation("location");
		Inventory i = Bukkit.createInventory(null, BOOKSHELF_SIZE, BOOKSHELF);
		
		for(String key : source.getKeys(false)){
			if(key.compareToIgnoreCase("location")!=0){
				i.setItem(Integer.parseInt(key), source.getItemStack(key));
			}
		}
		this.put(l, i);
	}
	
	private void saveToConfig(Location l, Inventory inv){
		ConfigSection dest = this.config.getRootSection().getConfigurationSection(Utils.getLocationText(l, true), true);
		dest.set("location", l);
		for(Integer i=0; i<inv.getSize(); i++){
			dest.set("" + i, inv.getItem(i));
		}
		this.config.save();
	}
}

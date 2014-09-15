package com.github.isatwospirit.kittyslilhelpers.command.world;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;
import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;
import com.github.isatwospirit.kittyslilhelpers.util.Utils;

public class WorldGroup implements WorldFlagContainer{
	private String name;
	private GameMode gameMode;
	private OfflinePlayer owner;
	private List<String> lore;
	private ConfigSection config;
	private WorldFlags flags;
	
	public WorldGroup(String name, ConfigSection parentConfig){
		this.name = name;
		this.config = parentConfig.getConfigurationSection(this.name, true);
		this.gameMode = GameMode.valueOf(this.config.getString("gamemode", Bukkit.getDefaultGameMode().name()));
		this.lore = config.getStringList("lore");
		try{
			this.owner = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("owner")));
		}catch(Exception e){}
	}
	public WorldGroup(String name, ConfigSection parentConfig, GameMode gameMode){
		this.name = name;
		this.config = parentConfig.getConfigurationSection(this.name, true);
		this.setGameMode(gameMode);
	}
	
	public String getName(){
		return this.name;
	}
	
	public GameMode getGameMode(){
		return this.gameMode;
	}
	
	public void setGameMode(GameMode value){
		this.gameMode = value;
		this.config.set("gamemode", this.getGameMode().name());
		this.config.save();
	}
	
	public OfflinePlayer getOwner(){
		return this.owner;
	}
	public void setOwner(OfflinePlayer owner){
		this.owner = owner;
		if(this.owner!=null){
			this.config.set("owner", "" + this.owner.getUniqueId());
		}else{
			this.config.set("owner", null);
		}
		this.config.save();
	}
	
	public Boolean hasLore(){
		if(this.lore==null)
			return false;
		if(this.lore.size()==0)
			return false;
		if(this.lore.size()==1 && this.lore.get(0)=="")
			return false;
		return true;
	}
	public List<String> getLore(){
		return this.lore;
	}
	public void setLore(List<String> lore){
		this.lore = lore;
		this.config.set("lore", this.lore);
		this.config.save();
	}
	
	public Set<WorldInfo> getWorlds(){
		Set<WorldInfo> result = new HashSet<WorldInfo>();

		for(WorldInfo w : WorldInfos.getInstance().values()){
			if(w.getWorldGroup().equals(this)){
				result.add(w);
			}
		}
		return result;
	}

	public String getDisplayName(){
		return Utils.colorize(this.name, ChatColor.BLUE);
	}
	
	public String getDescription(Boolean shortDescription){
		String description = this.getDisplayName();
		
		if(shortDescription){
			if(this.getOwner()!=null)
				description += ", Owned by " + this.getOwner().getName();
			description += ", Game Mode: " + this.getGameMode().name() + ", " + this.getWorlds().size() + " world(s).";
		}else{
			if(this.getOwner()!=null)
				description += "\nOwned by:  " + this.getOwner().getName();
			description +=     "\nGame Mode: " + this.getGameMode().name()
					     +     "\nWorlds:";
			
			for(WorldInfo w : this.getWorlds()){
				description += "\n   " + w.getDescription(true);
			}
			if(this.getLore()!=null){
				description += "\n Lore:\n" + this.getLore();
			}
		}
		
		return description;
	}
	
	public void saveInventoryFor(Player p){
		this.saveInventoryFor(p, p.getGameMode());
	}
	public void saveInventoryFor(Player p, GameMode g){
		if(Utils.isEmpty(p.getInventory())==false){
			ConfigSection store = this.config.getConfigurationSection("inventories", true).getConfigurationSection(g.name(), true);
			store = store.getConfigurationSection("" + p.getUniqueId(), true);
			for(Integer i=0; i<p.getInventory().getSize(); i++){
				store.set("" + i, p.getInventory().getItem(i));
			}
			store.set("helmet", p.getInventory().getHelmet());
			store.set("chestplate", p.getInventory().getChestplate());
			store.set("leggings", p.getInventory().getLeggings());
			store.set("boots", p.getInventory().getBoots());
			store.set("health", Utils.getPlayerHealth(p));
			store.set("exhaustion", (double)p.getExhaustion());
			store.set("saturation", (double)p.getSaturation());
			store.set("exp", (double)p.getExp());
						
			this.config.save();
			p.sendMessage("Inventory saved for Worldgroup " + this.getDisplayName() + ", GameMode " + g.name() + ".");
		}
	}

	public void restoreInventoryFor(Player p){
		this.restoreInventoryFor(p, p.getGameMode());
	}
	public void restoreInventoryFor(Player p, GameMode g){
		ConfigSection restore = this.config.getConfigurationSection("inventories", true).getConfigurationSection(g.name(), true);
		if(restore.isConfigurationSection("" + p.getUniqueId())){
			restore = restore.getConfigurationSection("" + p.getUniqueId());
			for(Integer i=0; i<p.getInventory().getSize(); i++){
				if(restore.isItemStack("" + i)){
					p.getInventory().setItem(i, restore.getItemStack("" + i));
				}else{
					p.getInventory().setItem(i, null);				
				}
			}
			if(restore.isItemStack("helmet")){
				p.getInventory().setHelmet(restore.getItemStack("helmet"));
			}else{
				p.getInventory().setHelmet(null);
			}
			if(restore.isItemStack("chestplate")){
				p.getInventory().setChestplate(restore.getItemStack("chestplate"));
			}else{
				p.getInventory().setChestplate(null);
			}
			if(restore.isItemStack("leggings")){
				p.getInventory().setLeggings(restore.getItemStack("leggings"));
			}else{
				p.getInventory().setLeggings(null);
			}
			if(restore.isItemStack("boots")){
				p.getInventory().setBoots(restore.getItemStack("boots"));
			}else{
				p.getInventory().setBoots(null);
			}
			
			if(restore.isDouble("health"))
				p.setHealth(restore.getDouble("health"));

			if(restore.isDouble("exhaustion"))
				p.setExhaustion((float) restore.getDouble("exhaustion"));

			if(restore.isDouble("saturation"))
				p.setSaturation((float) restore.getDouble("saturation"));

			if(restore.isDouble("exp"))
				p.setExp((float) restore.getDouble("exp"));
			
			p.sendMessage("Inventory restored for Worldgroup " + this.getDisplayName() + ", GameMode " + g.name() + ".");
		}
	}

	
	public void giveLoreBook(Player p){
		ItemStack loreBook = null;
		BookMeta book = null;
		
		if(this.getOwner()!=null){
			if(p.getUniqueId()==this.getOwner().getUniqueId()){
				loreBook = new ItemStack(Material.BOOK_AND_QUILL);
				book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.BOOK_AND_QUILL);
			}
		}
		if(loreBook==null){
			loreBook = new ItemStack(Material.WRITTEN_BOOK);
			book = (BookMeta)Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		}
		
		book.setDisplayName("World Group Lore Of " + this.getName());
		if(this.hasLore()){
			book.setPages(this.getLore());
		}else{
			book.addPage(ChatColor.BLUE + "The Tale Of " + this.getName() + ChatColor.BLACK + "\n");
		}
		loreBook.setItemMeta(book);
		if(p.getInventory().addItem(loreBook).isEmpty()==false){
			p.getWorld().dropItem(p.getLocation(), loreBook);
		}
	}
	@Override
	public ContainerType getContainerType() {
		return ContainerType.WORLDGROUP;
	}
	@Override
	public String getContainerName() {
		return "group:" + this.getName();
	}
	@Override
	public WorldFlags getWorldFlags() {
		if(this.flags==null)
			this.flags = new WorldFlags(this, ((WorldFlagContainer)KittysLilHelpers.getInstance()).getWorldFlags(), this.config.getConfigurationSection("flags", true));

		return this.flags;
	}	
}

package com.github.isatwospirit.kittyslilhelpers.command.klh;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument.OptionalStyle;
import com.github.isatwospirit.kittyslilhelpers.command.CommandEx;
import com.github.isatwospirit.kittyslilhelpers.command.CommandOption;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentLocation;
import com.github.isatwospirit.kittyslilhelpers.command.argument.ArgumentPlayer;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldFlags;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldInfos;
import com.github.isatwospirit.kittyslilhelpers.command.world.WorldFlags.TriState;

public class KittysLilHelper extends CommandEx{
	
	
	public KittysLilHelper(){
		super("klh");
		this.setDescription("Various little commandlets and tools");
	}

	@Override
	public String getLongDescription(){
		return this.getDescription();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean doCommand(CommandSender sender, CommandOption option) {
		if(option.getName().compareToIgnoreCase("unsit")==0){
			((Player)option.getArgument("player").getValue()).leaveVehicle();
		}else if(option.getName().compareToIgnoreCase("whatis")==0){
			Block b = ((Location)option.getArgument("coord").getValue()).getBlock();
			sender.sendMessage(b.getType().name() + ", Data: " + b.getData());
		}
		return true;
	}

	@Override
	protected boolean doInitialize() {
		CommandOption option = this.addOption("unsit", "Removes a passenger from a vehicle", null);
		option.addArgument(new ArgumentPlayer(), "Player", "Name or UUID of player to unsit", OptionalStyle.OPTIONAL_IF_DEFAULT);
		option = this.addOption("whatis", "Displays information on a block", null);
		option.addArgument(new ArgumentLocation(), "Coord", "Location of block", OptionalStyle.OPTIONAL_IF_DEFAULT);
		return true;
	}

	@Override
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent ide){
		ItemStack item = ide.getEntity().getItemStack();
		WorldFlags current = WorldInfos.getInstance().get(ide.getLocation().getWorld()).getWorldFlags();
		Block below = ide.getLocation().getBlock().getRelative(BlockFace.DOWN);
		if((item.getType()==Material.SAPLING) && (below.getType()==Material.DIRT || below.getType()==Material.GRASS)){
			this.doSaplingAutoPlant(ide.getLocation(), (byte) item.getDurability(), item.getAmount(), current.getSaplingAutoPlantRate(), current.getBigTreeRate());
		}else if(item.getType()==Material.SEEDS && (below.getType()==Material.SOIL)){
			this.doAutoPlant(ide.getLocation(), Material.CROPS, item.getAmount(), current.getCropsAutoPlantRate());
		}else if(item.getType()==Material.POTATO_ITEM && (below.getType()==Material.SOIL)){
			this.doAutoPlant(ide.getLocation(), Material.POTATO, item.getAmount(), current.getPotatoAutoPlantRate());
		}else if(item.getType()==Material.CARROT_ITEM && (below.getType()==Material.SOIL)){
			this.doAutoPlant(ide.getLocation(), Material.CARROT, item.getAmount(), current.getCarrotAutoPlantRate());
		}
	}
	
	@EventHandler 
	public void onBlockBreak(BlockBreakEvent bbe){
		try{
			Material tool = bbe.getPlayer().getItemInHand().getType();
			if((WorldInfos.getInstance().get(bbe.getPlayer().getWorld()).getWorldFlags().doRealisticTreeCut()==TriState.ON) && 
				(tool==Material.DIAMOND_AXE || tool==Material.IRON_AXE || tool==Material.STONE_AXE || tool==Material.GOLD_AXE || tool==Material.WOOD_AXE)){
				if(bbe.getBlock().getType()==Material.LOG || bbe.getBlock().getType()==Material.LOG_2){
					Short cut = (short) (this.realisticTreeCut(bbe.getBlock(), null) + 1);
					ItemStack hand = bbe.getPlayer().getItemInHand();
					if(hand.containsEnchantment(Enchantment.DURABILITY)){
						Integer level = hand.getEnchantmentLevel(Enchantment.DURABILITY);
						if(level==1)
							level = 50;
						else if(level==2)
							level = 67;
						else if(level==3)
							level = 75;
						Short newCut = 0;
						for(Integer count=0;count<cut;count++){
							Integer rand = (int) (Math.random() * 100);
							if(rand>=level)
								newCut = (short) (newCut + 1);
						}
						cut = newCut;
					}
					hand.setDurability((short) (hand.getDurability()+cut));
					if(hand.getDurability()>tool.getMaxDurability()){
						bbe.getPlayer().setItemInHand(null);
					}
				}
			}
		}catch(Exception e){
			
		}
	}
	
	private void doSaplingAutoPlant(Location l, Byte species, Integer count, Integer rate, Integer bigTreeRate){
		if(species==5)
			bigTreeRate=100;
		else if(species==0 || species==2 || species==4)
			bigTreeRate=0;
		
		Block here = l.getBlock();
		for(Integer c=0;c<count;c++){
			Integer random = this.getRandomPercent();
			if(random<=rate){
				Integer random2 = this.getRandomPercent();
				if(random2<=bigTreeRate){
					this.plantSapling(here, species);

					if(here.getRelative(BlockFace.NORTH).getType()==Material.AIR &&
					   here.getRelative(BlockFace.NORTH_EAST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.EAST).getType()==Material.AIR){
						this.plantSapling(here.getRelative(BlockFace.NORTH), species);
						this.plantSapling(here.getRelative(BlockFace.NORTH_EAST), species);
						this.plantSapling(here.getRelative(BlockFace.EAST), species);
					}else if(here.getRelative(BlockFace.EAST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.SOUTH_EAST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.SOUTH).getType()==Material.AIR){
						this.plantSapling(here.getRelative(BlockFace.EAST), species);
						this.plantSapling(here.getRelative(BlockFace.SOUTH_EAST), species);
						this.plantSapling(here.getRelative(BlockFace.SOUTH), species);
					}else if(here.getRelative(BlockFace.SOUTH).getType()==Material.AIR &&
					   here.getRelative(BlockFace.SOUTH_WEST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.WEST).getType()==Material.AIR){
						this.plantSapling(here.getRelative(BlockFace.SOUTH), species);
						this.plantSapling(here.getRelative(BlockFace.SOUTH_WEST), species);
						this.plantSapling(here.getRelative(BlockFace.WEST), species);
					}else if(here.getRelative(BlockFace.WEST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.NORTH_WEST).getType()==Material.AIR &&
					   here.getRelative(BlockFace.NORTH).getType()==Material.AIR){
						this.plantSapling(here.getRelative(BlockFace.WEST), species);
						this.plantSapling(here.getRelative(BlockFace.NORTH_WEST), species);
						this.plantSapling(here.getRelative(BlockFace.NORTH), species);
					}
				}else{
					this.plantSapling(here, species);
				}
				break;
			}
		}		
	}
	
	private void doAutoPlant(Location l, Material d, Integer count, Integer rate){
		for(Integer c=0;c<count;c++){
			Integer random = this.getRandomPercent();
			if(random<=rate){
				l.getBlock().setType(d);
				break;
			}
		}
	}
	
	private Short realisticTreeCut(Block b, List<Block>checked){
		Short count = 0;
		Block check;

		if(checked==null){
			checked = new LinkedList<Block>();
			checked.add(b);
		}
		
		for(BlockFace face : BlockFace.values()){
			if(face!=BlockFace.DOWN){
				check = b.getRelative(face);
				if(checked.contains(check)==false){
					if(isSameWood(check, b)){
						checked.add(check);
						count = (short) (count + this.realisticTreeCut(check, checked) + 1);
						check.breakNaturally();
					}
				}
				if(face!=BlockFace.UP){
					check = check.getRelative(BlockFace.UP);
					if(checked.contains(check)==false){
						if(isSameWood(check, b)){
							checked.add(check);
							count = (short) (count + this.realisticTreeCut(check, checked) + 1);
							check.breakNaturally();
						}
					}
				}
			}
		}
		return count;
	}
	
	@SuppressWarnings("deprecation")
	private boolean isSameWood(Block compareTo, Block b){
		if(b.getType()==compareTo.getType()){
			if((b.getData()%4)==(compareTo.getData()%4)){
				return true;
			}
		}
		return false;
	}

	private Integer getRandomPercent(){
		return (int) (Math.random()*100+1);
	}

	@SuppressWarnings("deprecation")
	private void plantSapling(Block b, byte species){
		b.setType(Material.SAPLING);
		b.setData(species);
	}
}
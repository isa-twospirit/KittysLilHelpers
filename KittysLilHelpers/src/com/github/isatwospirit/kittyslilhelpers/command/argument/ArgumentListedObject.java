package com.github.isatwospirit.kittyslilhelpers.command.argument;

import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.command.ItemContainer;
import com.github.isatwospirit.kittyslilhelpers.command.CommandArgument;
import com.github.isatwospirit.kittyslilhelpers.command.ContextDefaults;

public class ArgumentListedObject extends CommandArgument{
	public enum ItemExistanceType{
		ITEM_MUST_EXIST,
		ITEM_MUST_NOT_EXIST
	}
	private ItemContainer container = null;
	private OfflinePlayer owningPlayer = null;
	private ItemExistanceType itemExistance = null;
	private String[] items = null;
	@SuppressWarnings("rawtypes")
	private Class<? extends Enum> enumItems = null;
	
	public ArgumentListedObject(String... items){
		super(String.class);
		this.items = items;
		this.itemExistance = ItemExistanceType.ITEM_MUST_EXIST;
	}
	
	public ArgumentListedObject(ItemContainer container, ItemExistanceType itemExist) {
		super(ArgumentListedObject.getResultClass(container, itemExist));
		this.container = container;
		this.itemExistance = itemExist;
	}
	
	public ArgumentListedObject(ItemContainer container) {
		super(container.getItemType());
		this.container = container;
		this.itemExistance = ItemExistanceType.ITEM_MUST_EXIST;
	}
	
	@SuppressWarnings("rawtypes")
	public ArgumentListedObject(Class<? extends Enum> enumItems){
		super(enumItems);
		this.enumItems = enumItems;
	}
	
	private ItemExistanceType getItemExistanceType(){
		return this.itemExistance;
	}

	private ItemContainer getItemContainer(){
		return this.container;
	}
	
	@Override
	public OfflinePlayer getOwningPlayer() {
		return this.owningPlayer;
	}
	
	@Override
	public String getFormattedName() {
		return this.getName();
	}
	
	@Override
	public ArgumentListedObject clone(){
		ArgumentListedObject result = null;
		
		if(this.getItemContainer()!=null)
			result = new ArgumentListedObject(this.getItemContainer(), this.getItemExistanceType());
		else if(this.enumItems!=null)
			result = new ArgumentListedObject(this.enumItems);
		else
			result = new ArgumentListedObject(this.items);
		
		result.doInitialize(this.getOption(), this.getOrdinal(), 
				this.getName(), this.getDescription(), this.getOptionalStyle(), 
				this.getDefaultValue(), this.getDefaultValueDescription());
		
		return result;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	protected Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues) {
		Object result = null;
		String validItems = "";
		
		if(this.enumItems!=null){
			for(Enum e : this.enumItems.getEnumConstants()){
				validItems += e.name() + " ";
				if(e.name().compareToIgnoreCase(args[startIndex])==0){
					result = e;
					break;
				}
			}
			if(result!=null){
				this.setValue(result);
				this.setFindValueResult(FindValueResult.SUCCESS);
				return 1;
			}else{
				this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
				this.setFindValueMessage("Value " + args[startIndex] + " is not a valid " + this.enumItems.getSimpleName() + ", must be one of these: " + validItems);
				return 0;
			}
		}else if(this.items!=null){
			for(String check : this.items){
				validItems += check + " ";
				if(check.compareToIgnoreCase(args[startIndex])==0){
					result = check;
					break;
				}
			}
			if(result!=null){
				this.setValue(result);
				this.setFindValueResult(FindValueResult.SUCCESS);
				return 1;
			}else{
				this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
				this.setFindValueMessage("Invalid value " + args[startIndex] + ", must be one of these: " + validItems);
				return 0;
			}
		}else{
			try{
				result = this.getItemContainer().getItem(args[startIndex]); 
			}catch(Exception e){
			}
		}
		
		if(this.getItemExistanceType()==ItemExistanceType.ITEM_MUST_NOT_EXIST){
			if(result==null){
				this.setValue(args[startIndex]);
				this.setFindValueResult(FindValueResult.SUCCESS);
				return 1;
			}else{
				this.setValue(null);
				this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
				this.setFindValueMessage(this.getItemContainer().getItemDisplayName() + " \"" + args[startIndex] + "\" already exists.");
				return 1;
			}
		}else{
			if(result==null){
				this.setValue(null);
				this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISMATCH);
				this.setFindValueMessage(this.getItemContainer().getItemDisplayName() + " \"" + args[startIndex] + "\" does not exist.");
				return 0;			
			}else{
				this.setValue(result);
				this.owningPlayer = this.getItemContainer().getItemOwner(args[startIndex]);
				this.setFindValueResult(FindValueResult.SUCCESS);
				return 1;
			}
		}		
	}
	
	public static Class<?> getResultClass(ItemContainer container, ItemExistanceType itemExist){
		if(itemExist==ItemExistanceType.ITEM_MUST_EXIST)
			return container.getItemType();
		else
			return String.class;
	}
}

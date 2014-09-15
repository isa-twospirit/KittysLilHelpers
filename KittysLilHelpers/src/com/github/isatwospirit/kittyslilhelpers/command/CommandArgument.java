package com.github.isatwospirit.kittyslilhelpers.command;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.github.isatwospirit.kittyslilhelpers.KittysLilHelpers;

public abstract class CommandArgument {
	public enum OptionalStyle{
		REQUIRED,
		OPTIONAL_IF_DEFAULT,
		OPTIONAL
	}

	public enum FindValueResult{
		NOT_CHECKED,
		SUCCESS,
		SUCCESS_USE_DEFAULT,
		SUCCESS_WHILE_MISSING,
		FAILED_NOT_FOUND,
		FAILED_ARGUMENT_MISMATCH,
		FAILED_ARGUMENT_MISSING
	}
	
	private CommandOption option = null;
	private Integer ordinal = -1;
	private String name = "";
	private String description = "";
	private Class<?> valueType = null;
	private Object value = null;
	private Object defaultValue = null;
	private String defaultValueDescription = "";
	private OptionalStyle optionalStyle;
	private FindValueResult findValueResult;
	private String findValueMessage;

	public abstract OfflinePlayer getOwningPlayer();
	public abstract String getFormattedName();
	protected abstract Integer findValueInternal(String[] args, Integer startIndex, ContextDefaults defaultValues);

	public CommandOption getOption(){
		return this.option;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDisplayName(){
		switch(this.optionalStyle){
			case REQUIRED:
				return KittysLilHelpers.COLOR_ARGUMENT + this.getFormattedName() + ChatColor.RESET;
			case OPTIONAL:
				return KittysLilHelpers.COLOR_OPTIONARG + "[" + this.getFormattedName() + "]" + ChatColor.RESET;
			case OPTIONAL_IF_DEFAULT:
				return KittysLilHelpers.COLOR_OPTIONARG + "[" + this.getFormattedName() + "*]" + ChatColor.RESET;
			default:
				return KittysLilHelpers.COLOR_ERROR + this.getFormattedName() + ChatColor.RESET;
		}
	}
	
	public Integer getOrdinal(){
		return this.ordinal;
	}
		
	public String getDescription(){
		return this.description;
	}
	
	public Class<?> getValueType(){
		return this.valueType;
	}
	
	public OptionalStyle getOptionalStyle(){
		return this.optionalStyle;
	}
	
	public String getDefaultValueDescription(){
		if(this.defaultValueDescription!=null)
			return this.defaultValueDescription;
		else if(this.getDefaultValue()!=null)
			return this.getDefaultValue().toString();
		else if(this.getOptionalStyle()==OptionalStyle.OPTIONAL_IF_DEFAULT)
			return "Default " + this.valueType.getName() + " for current context.";
		else if(this.getOptionalStyle()==OptionalStyle.OPTIONAL)
			return "No default value specified.";
		else
			return "";
	}
	
	public Object getValue(){
		return this.value;
	}
	public String getValueText(){
		if(this.value==null)
			return null;
		else
			return this.value.toString();
	}
	
	protected void setValue(Object value){
		if(value==null)
			this.value = null;
		else if(this.getValueType().isAssignableFrom(value.getClass())){
			this.value = value;
		}else{
			System.out.println("ArgumentMismatch in setValue, expected " + this.getValueType().getName() + ", found " + value.getClass().getName());
		}	
	}
	
	public Object getDefaultValue(){
		return this.defaultValue;
	}
	protected void setDefaultValue(Object value){
		if(value == null)
			this.defaultValue = null;
		else if(this.getValueType().isAssignableFrom(value.getClass())){
			this.defaultValue = value;
		}else{
			System.out.println("ArgumentMismatch in setDefaultValue, expected " + this.getValueType().getName() + ", found " + value.getClass().getName());			
		}
	}	
	
	public FindValueResult getFindValueResult(){
		return this.findValueResult;
	}
	
	protected void setFindValueResult(FindValueResult result){
		this.findValueResult = result;
	}
	
	public String getFindValueMessage(){
		if(this.findValueMessage==null){
			switch(this.getFindValueResult()){
				case NOT_CHECKED:
					return "Value not checked.";
				case SUCCESS:
					return "Value found.";
				case SUCCESS_USE_DEFAULT:
					return "Found value from defaults.";
				case SUCCESS_WHILE_MISSING:
					return "No value in optional argument.";
				case FAILED_NOT_FOUND:
					return "No " + this.getValueType().getName() + " matched given argument.";
				case FAILED_ARGUMENT_MISMATCH:
					return "Given argument can not be cast to " + this.getValueType().getName() + ".";
				case FAILED_ARGUMENT_MISSING:		
					return "Missing argument #" + this.getOrdinal() + ".";
			}
		}
		return this.findValueMessage;
	}
	
	protected void setFindValueMessage(String message){
		this.findValueMessage = message;
	}
	
	public Boolean didValueCheckPass(){
		switch(this.getFindValueResult()){
			case FAILED_NOT_FOUND:
			case FAILED_ARGUMENT_MISMATCH:
			case FAILED_ARGUMENT_MISSING:
			case NOT_CHECKED:
				return false;
			default:
				return true;
		}
	}
	
	public Integer findValue(ContextDefaults defaultValues, String[] args, Integer startIndex){
		try{
			Integer iConsumed = 0;
			try{
				if(args.length>startIndex){
					//There's at least one more argument... that's sufficient in most cases. Do type-specific lookup:
					iConsumed = this.findValueInternal(args, startIndex, defaultValues);
				}else{
					//Ouchies... no arguments left. Set values to "failed" - might be changed in defaults check
					this.setFindValueResult(FindValueResult.FAILED_ARGUMENT_MISSING);
					this.setValue(null);
				}
			}catch(Exception e){
				System.out.println("findValue - Size check:" + e.getMessage());
			}
				
			if(iConsumed==0 && this.getOptionalStyle()!=OptionalStyle.REQUIRED){
				if(this.getDefaultValue()!= null){
					this.setValue(this.getDefaultValue());
					this.setFindValueResult(FindValueResult.SUCCESS_USE_DEFAULT);
					this.setFindValueMessage("Using default value: " + this.getDefaultValueDescription() + ".");
				}else if(this.getOptionalStyle()==OptionalStyle.OPTIONAL_IF_DEFAULT && defaultValues.hasDefault(this.getClass())){
					this.setValue(defaultValues.getDefault(this.getClass()));
					this.setFindValueResult(FindValueResult.SUCCESS_USE_DEFAULT);
					this.setFindValueMessage("Using context default value: " + this.getDefaultValueDescription() + ".");
				}else if(this.getOptionalStyle()==OptionalStyle.OPTIONAL_IF_DEFAULT && defaultValues.hasDefault(this.getValueType())){
					this.setValue(defaultValues.getDefault(this.getValueType()));
					this.setFindValueResult(FindValueResult.SUCCESS_USE_DEFAULT);
					this.setFindValueMessage("Using context default value: " + this.getDefaultValueDescription() + ".");
				}else if(this.getOptionalStyle()==OptionalStyle.OPTIONAL){
					this.setFindValueResult(FindValueResult.SUCCESS_WHILE_MISSING);
					this.setFindValueMessage("No value for optional argument.");
				}
			}
			return iConsumed;
		}catch(Exception e){
			System.out.println("findValue: " + e.getMessage());
			return 0;
		}
	}
	
	public abstract CommandArgument clone();

	public void doInitialize(CommandOption option, Integer ordinal, String name, String description, OptionalStyle optionalStyle, Object defaultValue, String defaultValueDescription){
		this.option = option;
		this.ordinal = ordinal;
		this.name = name;
		this.description = description;
		this.optionalStyle = optionalStyle;
		this.findValueResult = FindValueResult.NOT_CHECKED;
		this.defaultValue = defaultValue;
		this.defaultValueDescription = defaultValueDescription;
		this.findValueMessage = null;
	}
	
	public CommandArgument(Class<?> valueType){
		this.valueType = valueType;
	}
}

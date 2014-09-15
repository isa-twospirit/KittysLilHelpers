package com.github.isatwospirit.kittyslilhelpers.command.world;

import com.github.isatwospirit.kittyslilhelpers.util.ConfigSection;

public class WorldFlags {
	public enum TriState{
		ON,
		OFF,
		DEFAULT
	};
	
	private WorldFlagContainer scope;
	private WorldFlags inheritedFrom;
	private ConfigSection config;
	private TriState doRealisticTreeCut = TriState.DEFAULT;
	private Integer saplingAutoPlantRate = 10;
	private Integer bigTreeRate = 25;
	private Integer cropsAutoPlantRate = 10;
	private Integer potatoAutoPlantRate = 10;
	private Integer carrotAutoPlantRate = 10;
	
	public WorldFlags(WorldFlagContainer scope, WorldFlags inheritedFrom, ConfigSection config){
		this.scope = scope; 
		this.inheritedFrom = inheritedFrom;
		this.config = config;
		
		this.doRealisticTreeCut = TriState.valueOf(config.getString("do_realistic_tree_cut", "DEFAULT"));
		this.saplingAutoPlantRate = config.getInt("sapling_auto_plant_rate", -1);
		this.bigTreeRate = config.getInt("big_tree_rate", -1);
		this.cropsAutoPlantRate = config.getInt("crops_auto_plant_rate", -1);
		this.potatoAutoPlantRate = config.getInt("potato_auto_plant_rate", -1);
		this.carrotAutoPlantRate = config.getInt("carrot_auto_plant_rate", -1);
	}

	public TriState doRealisticTreeCut(){
		if(this.doRealisticTreeCut==TriState.DEFAULT){
			if(this.inheritedFrom==null)
				return TriState.ON;
			else
				return this.inheritedFrom.doRealisticTreeCut();
		}else{
			return this.doRealisticTreeCut;
		}
	}
	public void setRealisticTreeCut(TriState value){
		this.doRealisticTreeCut=value;
		this.config.set("do_realistic_tree_cut", this.doRealisticTreeCut.name());
		this.config.save();
	}

	public Integer getSaplingAutoPlantRate(){
		if(this.saplingAutoPlantRate<0){
			if(this.inheritedFrom==null)
				return 10;
			else
				return this.inheritedFrom.getSaplingAutoPlantRate();
		}else{
			return this.saplingAutoPlantRate;
		}
	}
	public void setSaplingAutoPlantRate(Integer value){
		if(value<0)
			value=-1;
		else if(value>100)
			value=100;
		this.saplingAutoPlantRate=value;
		this.config.set("sapling_auto_plant_rate", value);
		this.config.save();
	}
	
	public Integer getBigTreeRate(){
		if(this.bigTreeRate<0){
			if(this.inheritedFrom==null)
				return 25;
			else
				return this.inheritedFrom.getBigTreeRate();
		}else{
			return this.bigTreeRate;
		}
	}
	public void setBigTreeRate(Integer value){
		if(value<0)
			value=-1;
		else if(value>100)
			value=100;
		this.bigTreeRate=value;
		this.config.set("big_tree_rate", value);
		this.config.save();
	}
	
	public Integer getCropsAutoPlantRate(){
		if(this.cropsAutoPlantRate<0){
			if(this.inheritedFrom==null)
				return 10;
			else
				return this.inheritedFrom.getCropsAutoPlantRate();
		}else{
			return this.cropsAutoPlantRate;
		}
	}
	public void setCropsAutoPlantRate(Integer value){
		if(value<0)
			value=-1;
		else if(value>100)
			value=100;
		this.cropsAutoPlantRate=value;
		this.config.set("crops_auto_plant_rate", value);
		this.config.save();
	}

	public Integer getPotatoAutoPlantRate(){
		if(this.potatoAutoPlantRate<0){
			if(this.inheritedFrom==null)
				return 10;
			else
				return this.inheritedFrom.getPotatoAutoPlantRate();
		}else{
			return this.potatoAutoPlantRate;
		}
	}
	public void setPotatoAutoPlantRate(Integer value){
		if(value<0)
			value=-1;
		else if(value>100)
			value=100;
		this.potatoAutoPlantRate=value;
		this.config.set("potato_auto_plant_rate", value);
		this.config.save();
	}

	public Integer getCarrotAutoPlantRate(){
		if(this.carrotAutoPlantRate<0){
			if(this.inheritedFrom==null)
				return 10;
			else
				return this.inheritedFrom.getCarrotAutoPlantRate();
		}else{
			return this.carrotAutoPlantRate;
		}
	}
	public void setCarrotAutoPlantRate(Integer value){
		if(value<0)
			value=-1;
		else if(value>100)
			value=100;
		this.carrotAutoPlantRate=value;
		this.config.set("carrot_auto_plant_rate", value);
		this.config.save();
	}

	public String getSummary(){
		String desc = "";
		desc = "WorldFlags in " + this.scope.getContainerName() + ":"
			+"\nRealisticTreeCut - local: " + this.doRealisticTreeCut + ", effective: " + this.doRealisticTreeCut() 
			+"\nSaplingPlantRate - local: "	+ this.saplingAutoPlantRate + ", effective: " + this.getSaplingAutoPlantRate()
			+"\nBigTreeRate      - local: "	+ this.bigTreeRate + ", effective: " + this.getBigTreeRate()
			+"\nCropsPlantRate   - local: "	+ this.cropsAutoPlantRate + ", effective: " + this.getCropsAutoPlantRate()
			+"\nPotatoPlantRate  - local: "	+ this.potatoAutoPlantRate + ", effective: " + this.getPotatoAutoPlantRate()
			+"\nCarrotPlantRate  - local: "	+ this.carrotAutoPlantRate + ", effective: " + this.getCarrotAutoPlantRate();
			
		
		return desc;
	}
	
}

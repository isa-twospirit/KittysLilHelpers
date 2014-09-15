package com.github.isatwospirit.kittyslilhelpers.command.createmaze;

public class LabyrinthCell {
	private boolean isNorthOpen = false;
	private boolean isSouthOpen = false;
	private boolean isWestOpen = false;
	private boolean isEastOpen = false;
	private boolean isInUse = false;
	
	public boolean getNorthOpen(){
		return this.isNorthOpen;
	}
	public void setNorthOpen(boolean isOpen){
		this.isNorthOpen = isOpen;
	}
	
	public boolean getSouthOpen(){
		return this.isSouthOpen;
	}
	public void setSouthOpen(boolean isOpen){
		this.isSouthOpen = isOpen;
	}
	
	public boolean getWestOpen(){
		return this.isWestOpen;
	}
	public void setWestOpen(boolean isOpen){
		this.isWestOpen = isOpen;
	}
	
	public boolean getEastOpen(){
		return this.isEastOpen;
	}
	public void setEastOpen(boolean isOpen){
		this.isEastOpen = isOpen;
	}
	
	public boolean isInUse(){
		return this.isInUse;
	}
	public void setInUse(boolean isInUse){
		this.isInUse = isInUse;
	}
	
	public LabyrinthCell(){
	}
	
	private void Initialize(boolean isNorthOpen, boolean isSouthOpen, boolean isWestOpen, boolean isEastOpen, boolean isInUse){
		this.isNorthOpen = isNorthOpen;
		this.isSouthOpen = isSouthOpen;
		this.isWestOpen = isWestOpen;
		this.isEastOpen = isEastOpen;
		this.isInUse = isInUse;
	}
	
	public LabyrinthCell(boolean isNorthOpen, boolean isSouthOpen, boolean isWestOpen, boolean isEastOpen, boolean isInUse){
		this.Initialize(isNorthOpen, isSouthOpen, isWestOpen, isEastOpen, isInUse);
	}
	
	public LabyrinthCell(String pattern, boolean isInUse){
		this.Initialize(pattern.contains("N"), 
				pattern.contains("S"),
				pattern.contains("W"),
				pattern.contains("E"),
				isInUse);
	}
}

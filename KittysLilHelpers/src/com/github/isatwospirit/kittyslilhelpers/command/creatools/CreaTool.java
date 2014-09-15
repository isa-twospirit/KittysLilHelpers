package com.github.isatwospirit.kittyslilhelpers.command.creatools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface CreaTool extends Runnable{
	public enum BlockEvent{
		PLACE,
		DESTROY,
		INTERACT
	}
	
	public Player getOwner();
	public String getName();
	public boolean isActive();
	public boolean isRunning();
	public boolean canRun();
	public boolean canUndo();
	public boolean undo();
	public Block getStartBlock();
	public BlockEvent getBlockEvent();
	public boolean prepareRun(Block startBlock, BlockEvent event);
}

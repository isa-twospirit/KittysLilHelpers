package com.github.isatwospirit.kittyslilhelpers.command.world;

import org.bukkit.OfflinePlayer;

public interface WorldFlagContainer {
	public enum ContainerType{
		SERVER,
		WORLDGROUP,
		WORLD
	};
	
	public OfflinePlayer getOwner();
	public ContainerType getContainerType();
	public String getContainerName();
	public WorldFlags getWorldFlags();
}

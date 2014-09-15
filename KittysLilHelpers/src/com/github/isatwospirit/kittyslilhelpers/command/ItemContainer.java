package com.github.isatwospirit.kittyslilhelpers.command;

import org.bukkit.OfflinePlayer;

public interface ItemContainer {
	public String getItemDisplayName();
	public Class<?> getItemType();
	public Object getItem(String key);
	public OfflinePlayer getItemOwner(String key);
}

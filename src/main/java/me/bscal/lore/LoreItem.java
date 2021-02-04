package me.bscal.lore;

import org.bukkit.inventory.ItemStack;

public interface LoreItem
{

	//public abstract void CanApply(ItemStack item);

	public abstract void Apply(ItemStack item);

	public abstract void Remove(ItemStack item);

	public abstract void Update(ItemStack item);

	/**
	 * Fetch the name used by the API. Do not use color codes here.
	 */
	public abstract String GetName();

	/**
	 * Fetch the formatted name to display in the lore. Color codes are OK.
	 */
	public abstract String GetLocalizedName();

}

package me.bscal.lore.stats;

import me.bscal.lore.ColorConstants;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Objects;

public abstract class Stat implements Listener
{

	private static final DecimalFormat fFormat = new DecimalFormat("#.00");

	public final String name;
	public final ChatColor prefixColor;
	public final ChatColor nameColor;
	public final ValueType type;

	public Stat(String name, ChatColor prefixColor, ChatColor nameColor, ValueType type)
	{
		this.name = name;
		this.prefixColor = prefixColor;
		this.nameColor = nameColor;
		this.type = type;
	}

	public StatContainer CreateContainer(float value, Operation operation)
	{
		return new StatContainer(Character.MIN_VALUE, value, operation, this);
	}

	public StatContainer CreateContainer(char prefix, float value, Operation operation)
	{
		return new StatContainer(prefix, value, operation, this);
	}

	public String CreateLine(StatContainer container)
	{
		ChatColor preColor;
		if (prefixColor == ColorConstants.GOOD)
			preColor = (container.value > 0f) ? ColorConstants.GOOD : ColorConstants.BAD;
		else if (prefixColor == ColorConstants.BAD)
			preColor = (container.value > 0f) ? ColorConstants.BAD : ColorConstants.GOOD;
		else
			preColor = ColorConstants.NEUTRAL;

		if (container.value < 0.0f) // TODO
			container.prefix = Character.MIN_VALUE;
		else
			container.prefix = '+';

		return MessageFormat.format("{3}{0}{1} {4}{2}",
				(container.prefix == Character.MIN_VALUE) ? "" : container.prefix,
				fFormat.format(container.value), name, preColor, nameColor);
	}

	// TODO for procs
	public boolean HandleProcess(ItemStack item) {return false;}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Stat stat = (Stat) o;
		return Objects.equals(name, stat.name);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(name);
	}
}

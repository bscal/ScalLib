package me.bscal.lore.stats;

import java.util.Objects;

public class StatContainer
{

	public char prefix;
	public float value;
	public Operation operation;
	public final Stat parent;

	public StatContainer(char prefix, float value, Operation operation, Stat parent)
	{
		this.prefix = prefix;
		this.value = value;
		this.operation = operation;
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StatContainer that = (StatContainer) o;
		return Objects.equals(parent, that.parent);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(parent);
	}
}

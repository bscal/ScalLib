package me.bscal.lore.stats;

import me.bscal.lore.LoreManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoreStats extends LoreManager<Stat>
{

	public void Register(Stat stat, Plugin plugin)
	{
		AddKeyword(stat.name, stat);

		for (Method m : stat.getClass().getMethods())
		{
			if (m.isAnnotationPresent(EventHandler.class))
			{
				Bukkit.getPluginManager().registerEvents(stat, plugin);
				break;
			}
		}
	}

	public Stat GetStat(String name)
	{
		return GetKeyword(name);
	}

	public StatContainer ContainerFromLore(LoreLookupContainer line)
	{
		if (!line.contains)
			return null;

		return new StatContainer(line.prefix, line.value, Operation.ADD,
				m_keywords.get(line.keyword));
	}

	public Map<String, Float> GetAllStats(LivingEntity ent)
	{
		Map<String, Float> map = new HashMap<>();

		for (ItemStack piece : GetAllGear(ent))
		{
			if (piece == null || piece.getType()
					.isEmpty() || !piece.hasItemMeta() || !piece.getItemMeta().hasLore())
				continue;

			List<LoreLookupContainer> containers = FindContainers(piece.getItemMeta().getLore());

			for (LoreLookupContainer c : containers)
			{
				float f = map.getOrDefault(c.keyword, 0f);
				map.put(c.keyword, f + c.value);
				// TODO handle operations
			}
		}
		return map;
	}

	public Map<String, Float> GetAllOfStat(LivingEntity ent, String stat)
	{
		Map<String, Float> map = new HashMap<>();
		for (ItemStack piece : GetAllGear(ent))
		{
			if (piece == null || piece.getType()
					.isEmpty() || !piece.hasItemMeta() || !piece.getItemMeta().hasLore())
				continue;

			List<LoreLookupContainer> containers = FindContainers(piece.getItemMeta().getLore());

			for (LoreLookupContainer c : containers)
			{
				if (!c.keyword.equals(stat))
					continue;
				float f = map.getOrDefault(c.keyword, 0f);
				map.put(c.keyword, f + c.value);
				// TODO handle operations
			}
		}
		return map;
	}

	public Map<String, Float> GetStatOfItem(ItemStack item)
	{
		Map<String, Float> map = new HashMap<>();

		if (item.getType().isEmpty() || !item.hasItemMeta() || !item.getItemMeta().hasLore())
			return map;

		List<LoreLookupContainer> containers = FindContainers(item.getItemMeta().getLore());

		for (LoreLookupContainer c : containers)
		{
			float f = map.getOrDefault(c.keyword, 0f);
			map.put(c.keyword, f + c.value);
			// TODO handle operations
		}
		return map;
	}

	public void AddStatToItem(ItemStack item, StatContainer container)
	{
		ItemMeta im = item.getItemMeta();
		List<String> lore;
		if (im.hasLore())
		{
			lore = item.getLore();
			LoreLookupContainer line = FindContainer(lore, container.parent.name);

			StatContainer loreStat = ContainerFromLore(line);

			if (container.equals(loreStat))
			{
				float newTotal = 0;

				if (loreStat.operation == Operation.ADD)
					newTotal = loreStat.value + container.value;
				else if (loreStat.operation == Operation.MULTIPLY)
					newTotal = loreStat.value * container.value;
				if (newTotal == 0.0f)
				{
					lore.remove(line.index);
					im.setLore(lore);
					item.setItemMeta(im);
					return;
				}
				loreStat.value = newTotal;
				lore = SetStat(lore, line, container.parent.CreateLine(loreStat));
			}
			else
				lore.add(container.parent.CreateLine(container));
		}
		else
		{
			lore = new ArrayList<>(1);
			lore.add(container.parent.CreateLine(container));
		}
		im.setLore(lore);
		item.setItemMeta(im);
	}

	public void RemoveStatFromItem(ItemStack item, StatContainer container)
	{
		ItemMeta im = item.getItemMeta();
		if (!im.hasLore())
			return;

		List<String> lore = item.getLore();
		LoreLookupContainer line = FindContainer(lore, container.parent.name);

		if (!line.contains)
			return;

		StatContainer loreStat = ContainerFromLore(line);

		if (container.equals(loreStat))
		{
			lore.remove(line.index);
			im.setLore(lore);
			item.setItemMeta(im);
		}
	}

	public ItemStack[] GetAllGear(LivingEntity ent)
	{
		return (ent.getEquipment() == null) ?
				new ItemStack[0] :
				new ItemStack[] { ent.getEquipment().getItemInMainHand(),
						ent.getEquipment().getItemInOffHand(), ent.getEquipment().getHelmet(),
						ent.getEquipment().getChestplate(), ent.getEquipment().getLeggings(),
						ent.getEquipment().getBoots()
				};
	}
}

package me.bscal.lore;

import me.bscal.common.Debug;
import me.bscal.logcraft.LogLevel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoreManager<T>
{

	public static final LoreLookupContainer NULL_CONTAINER = new LoreLookupContainer(-1, false,
			Character.MIN_VALUE, 0, "", "", false);

	protected String m_header;
	protected String m_footer;
	protected ChatColor m_color;
	protected char m_prefix;
	protected String m_splitStr;
	protected int m_splitOffset;

	protected final Map<String, T> m_keywords;

	public LoreManager()
	{
		this("", "", Character.MIN_VALUE, ChatColor.WHITE);
	}

	public LoreManager(String header, String footer)
	{
		this(header, footer, Character.MIN_VALUE, ChatColor.WHITE);
	}

	public LoreManager(String header, String footer, char prefix, ChatColor prefixColor)
	{
		m_header = header;
		m_footer = footer;
		if (!(prefix == Character.MIN_VALUE))
		{
			m_color = prefixColor;
			m_prefix = prefix;
		}
		m_splitStr = " ";
		m_splitOffset = 1;
		m_keywords = new HashMap<>();
	}

	public void UpdateItem(ItemStack item)
	{
		if (item.getType() == Material.AIR || !item.hasItemMeta() || !item.getItemMeta().hasLore())
			return;

		GetKeywords(item.getLore()).forEach((key) -> {
			T obj = m_keywords.get(key);
			if (obj instanceof LoreItem)
				((LoreItem) obj).Update(item);
		});
	}

	public void UpdateInv(Inventory inv)
	{
		inv.forEach(this::UpdateItem);
	}

	public void AddKeywordToItem(ItemStack item, LoreItem loreItem)
	{
		ItemMeta im = item.getItemMeta();

		List<String> lore;
		if (im.hasLore())
		{
			lore = im.getLore();
			LoreLookupData data = FindFirstLine(lore);
			PushLineToLore(lore, loreItem.GetLocalizedName(), data.index, data.contains);
		}
		else
		{
			lore = new ArrayList<>(3);
			PushLineToLore(lore, loreItem.GetLocalizedName(), 0, false);

		}
		im.setLore(lore);
		item.setItemMeta(im);

		loreItem.Apply(item);

		UpdateItem(item);

		if (Debug.HasLogger() && Debug.GetLogger().IsLevel(LogLevel.DEVELOPER))
			Debug.GetLogger().Log("Adding Condition to item!");
	}

	public void RemoveKeywordFromItem(ItemStack item, LoreItem loreItem)
	{
		ItemMeta im = item.getItemMeta();

		if (im.hasLore())
		{
			List<String> lore = im.getLore();
			LoreLookupData data = Find(lore, loreItem.GetName());
			if (Debug.HasLogger())
				Debug.GetLogger().Log(data);
			if (data.contains)
			{
				lore.remove(data.index);
				im.setLore(lore);
				item.setItemMeta(im);
				loreItem.Remove(item);
			}
		}

		UpdateItem(item);



		if (Debug.HasLogger() && Debug.GetLogger().IsLevel(LogLevel.DEVELOPER))
			Debug.GetLogger().Log("Removing Condition to item!");
	}

	public void RemoveAllConditions(ItemStack item, boolean removeHeaders)
	{
		ItemMeta im = item.getItemMeta();

		if (im.hasLore())
			RemoveAllKeywords(im.getLore(), removeHeaders);
	}

	public boolean HasAnyCondition(ItemStack item)
	{
		ItemMeta im = item.getItemMeta();

		if (im.hasLore())
		{
			LoreLookupData data = FindFirstLine(im.getLore());
			return data.contains && m_keywords.containsKey(
					ExtractKeyword(im.getLore().get(data.index)));
		}
		return false;
	}

	public boolean HasCondition(ItemStack item, String key)
	{
		ItemMeta im = item.getItemMeta();
		return im.hasLore() && Find(im.getLore(), key).contains;
	}

	public LoreLookupData FindFirstLine(List<String> lore)
	{
		if (lore.size() < 2)
			return new LoreLookupData(lore.size(), false);

		for (int i = 0; i < lore.size(); i++)
		{
			if (lore.get(i).equals(m_header))
				return new LoreLookupData(i + 1, true);
		}
		return new LoreLookupData(lore.size(), false);
	}

	/**
	 * Finds a keyword in lore.
	 *
	 * @param lore - Lore list
	 * @param key  - Key to check for
	 * @return If true LoreLookupData's index will equal line index, contains will equal true
	 */
	public LoreLookupData Find(List<String> lore, String key)
	{
		for (int i = 0; i < lore.size(); i++)
		{
			String line = ChatColor.stripColor(lore.get(i));

			if (line.isBlank())
				continue;

			//if (line.equals(m_header))
			//has = true;
			//if (!has)
			//continue;
			//if (line.equals(m_footer) || line.isBlank())
			//break;
			//line = ChatColor.stripColor(lore.get(i));
			// Makes sures that any ChatColor charaters are not blocking
			//if (!line.startsWith(m_prefix))
			//continue;
			//WeaponConditions.Logger.Log(line.charAt(0));

			if (ExtractKeyword(line).equals(key))
				return new LoreLookupData(i, true);
		}
		return new LoreLookupData(-1, false);
	}

	protected List<String> GetKeywords(List<String> lore)
	{
		List<String> list = new ArrayList<>();
		for (String s : lore)
		{
			String line = ChatColor.stripColor(s);
			String key = ExtractKeyword(line);
			if (m_keywords.containsKey(key))
				list.add(key);
		}
		return list;
	}

	protected List<String> GetKeywordLines(List<String> lore)
	{
		List<String> list = new ArrayList<>();
		for (String s : lore)
		{
			String line = ChatColor.stripColor(s);
			if (m_keywords.containsKey(ExtractKeyword(line)))
				list.add(line);
		}
		return list;
	}

	protected void RemoveAllKeywords(List<String> lore, boolean removeHeaders)
	{
		for (int i = lore.size() - 1; i > -1; i--)
		{
			String line = lore.get(i);

			if (removeHeaders && !m_header.isBlank() && line.equals(
					m_header) || !m_footer.isBlank() && line.equals(m_footer))
			{
				lore.remove(i);
				continue;
			}

			line = ChatColor.stripColor(lore.get(i));
			if (m_keywords.containsKey(ExtractKeyword(line)))
				continue;
			lore.remove(i);
		}
	}

	/**
	 * Pushes a line to the inserted lore.
	 *
	 * @param lore       - List to add line at index
	 * @param line       - String to add
	 * @param index      - Insert index
	 * @param hasHeaders - Does list have headers. If false will generate headers
	 */
	protected void PushLineToLore(List<String> lore, String line, int index, boolean hasHeaders)
	{
		String formatted = MessageFormat.format("{0}{1}{2}{3}", m_color, m_prefix, m_splitStr,
				line);
		if (!hasHeaders)
		{
			if (!m_footer.isEmpty())
				lore.add(index, m_footer);
			lore.add(index, formatted);
			if (!m_header.isEmpty())
				lore.add(index, m_header);
			return;
		}
		lore.add(index, formatted);
	}

	public List<String> SetLoreVariable(List<String> lore, String keyword, String var)
	{
		int i = Find(lore, keyword).index;
		String[] split = lore.get(i).split("\\[");
		lore.set(i, MessageFormat.format("{0}{1}[{2}]", split[0], split[0].endsWith(" ") ? "" : " ",
				var));
		return lore;
	}

	public String GetLoreVariable(String line)
	{
		String[] split = line.split("\\[");
		if (split.length > 0)
		{
			return split[1].substring(0, split[1].length() - 1);
		}
		return "";
	}

	public String ExtractKeyword(String line)
	{
		String[] split = line.split(" ");
		return (split.length == 0) ? "" : (split.length > 1 && IsPrefix(split[0].charAt(0))) ? split[1] : split[0];
	}

	public boolean IsPrefix(char prefix)
	{
		return prefix == m_prefix || prefix == '+' || prefix == '-';
	}

	/**-
	 * **************
	 * * Lore Stats *
	 * **************
	 */

	public LoreLookupContainer FindContainer(List<String> lore, String statName)
	{
		for (int i = 0; i < lore.size(); i++)
		{
			String line = ChatColor.stripColor(lore.get(i));
			LoreLookupContainer stat = LoreLineToContainer(i, line);
			if (stat != null)
				return stat;
		}
		return NULL_CONTAINER;
	}

	public List<LoreLookupContainer> FindContainers(List<String> lore)
	{
		List<LoreLookupContainer> list = new ArrayList<>();
		for (int i = 0; i < lore.size(); i++)
		{
			String line = ChatColor.stripColor(lore.get(i));
			LoreLookupContainer stat = LoreLineToContainer(i, line);
			if (stat != null)
				list.add(stat);
		}
		return list;
	}

	public List<String> SetStat(List<String> lore, LoreLookupContainer loreStat, String statStr)
	{
		if (loreStat.value == 0)
			lore.remove(loreStat.index);

		lore.set(loreStat.index, statStr);
		return lore;
	}

	public LoreLookupContainer LoreLineToContainer(int i, String line)
	{
		if (line.isBlank())
			return null;

		String[] split = line.split(" ");
		if (split.length < 2)
			return null;

		String keyword = ExtractKeyword(line);
		if (m_keywords.containsKey(keyword))
		{
			char c = split[0].charAt(0);
			char prefix = IsPrefix(c) ? c : ' ';
			String valStr = (c == '-' || c == '+') ? split[0] : split[0].substring(1);

			float val = ParseValue(valStr);
			boolean isVal = val != 0;

			// TODO maybe update to support multiple lore variables
			String data = (split.length > 2) ? split[3] : "";

			return new LoreLookupContainer(i, true, prefix, val, keyword, data, isVal);
		}
		return null;
	}

	public static float ParseValue(String valueStr)
	{
		try
		{
			return Float.parseFloat(valueStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	public boolean ContainsKey(String key)
	{
		return m_keywords.containsKey(key);
	}

	public void AddKeyword(String key, T obj)
	{
		m_keywords.put(key, obj);
	}

	public T GetKeyword(String key)
	{
		return m_keywords.get(key);
	}

	public Map<String, T> GetKeywords()
	{
		return m_keywords;
	}

	public String getHeader()
	{
		return m_header;
	}

	public void setHeader(String m_header)
	{
		this.m_header = m_header;
	}

	public String getFooter()
	{
		return m_footer;
	}

	public void setFooter(String m_footer)
	{
		this.m_footer = m_footer;
	}

	public char getPrefix()
	{
		return m_prefix;
	}

	public void setPrefix(char m_prefix)
	{
		this.m_prefix = m_prefix;
	}

	public String getSplitStr()
	{
		return m_splitStr;
	}

	public void setSplitStr(String m_splitStr)
	{
		this.m_splitStr = m_splitStr;
	}

	public int getSplitOffset()
	{
		return m_splitOffset;
	}

	public void setSplitOffset(int m_splitOffset)
	{
		this.m_splitOffset = m_splitOffset;
	}

	protected static class LoreLookupData
	{
		public final int index;
		public final boolean contains;

		public LoreLookupData(int i, boolean contains)
		{
			this.index = i;
			this.contains = contains;
		}

		@Override
		public String toString()
		{
			return "LoreLookupData{" + "index=" + index + ", contains=" + contains + '}';
		}
	}

	public static class LoreLookupContainer extends LoreLookupData
	{
		public final char prefix;
		public final float value;
		public final String keyword;
		public final String data;

		public final boolean hasValue;

		public LoreLookupContainer(int i, boolean contains, char prefix, float value,
				String keyword, String data, boolean hasValue)
		{
			super(i, contains);
			this.prefix = prefix;
			this.keyword = keyword;
			this.value = value;
			this.data = data;
			this.hasValue = hasValue;
		}
	}

}

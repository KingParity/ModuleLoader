package com.redstoner.modules.clearonjoin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 1, revision = 0, compatible = 2)
public class ClearOnJoin implements Module, Listener
{
	private File listLocation = new File(Main.plugin.getDataFolder(), "clearonjoins.json");
	private JSONArray list;
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Command(hook = "clearonjoin")
	public void clearOnJoin(CommandSender sender, String player)
	{
		list.add("!" + Bukkit.getServer().getOfflinePlayer(player).getUniqueId().toString());
		saveList();
		Utils.sendMessage(sender, null, player + "'s inventory will be cleared next time they join.");
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "clearonjoinself")
	public void clearOnJoinSelf(CommandSender sender)
	{
		String name = ((Player)sender).getUniqueId().toString();
		if (list.contains(name))
		{
			list.remove(name);
			Utils.sendMessage(sender, null, "Your inventory will no longer be cleared upon joining.");
			saveList();
			return;
		}
		list.add(name);
		saveList();
		Utils.sendMessage(sender, null, "Your inventory will now be cleared upon joining.");
	}
	
	@EventHandler
	public void uponJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		String playerUUID = player.getUniqueId().toString();
		String playerName = player.getName();
		if (list.contains(playerName) || list.contains(playerUUID))
		{
			e.getPlayer().getInventory().clear();
			Utils.sendMessage(player, null, "Inventory cleared.");
		}
		else if (list.contains("!" + playerName))
		{
			player.getInventory().clear();
			list.remove("!" + playerName);
			saveList();
			Utils.sendMessage(player, null, "Inventory cleared.");
		}
	}
	
	public void saveList()
	{
		JsonManager.save(list, listLocation);
	}
	
	@Override
	public boolean onEnable()
	{
		list = JsonManager.getArray(listLocation);
		if (list == null)
			list = new JSONArray();
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		return true;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command clearonjoin {\n" + 
				"    [string:name] {\n" + 
				"        help Clears that player's inventory the next time they join.;\n" + 
				"        run clearonjoin name;\n" + 
				"        perm utils.clearonjoin.other;\n" + 
				"    }\n" + 
				"    [empty] {\n" + 
				"        help Clears your inventory every time you join.;\n" + 
				"        run clearonjoinself;\n" + 
				"        perm utils.clearonjoin.self;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}

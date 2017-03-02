package com.redstoner.modules.adminnotes;

import java.io.File;
import java.text.SimpleDateFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class AdminNotes implements Module, Listener
{
	JSONArray notes;
	File saveFile = new File(Main.plugin.getDataFolder(), "adminnotes.json");
	
	@Override
	public boolean onEnable()
	{
		notes = JsonManager.getArray(saveFile);
		if (notes == null)
			notes = new JSONArray();
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (e.getPlayer().hasPermission("utils.adminnotes"))
		{
			if (notes.size() > 0)
			{
				Utils.sendMessage(e.getPlayer(), null, "§cThere are " + notes.size() + " open notes!");
			}
		}
	}
	
	@Override
	public void onDisable()
	{
		saveNotes();
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "an_create")
	public void createNote(CommandSender sender, String note)
	{
		JSONArray temp = new JSONArray();
		temp.add(sender.getName());
		temp.add(note);
		temp.add((double) System.currentTimeMillis() / 1000);
		notes.add(temp);
		Utils.sendMessage(sender, null, "§aNote added!");
		saveNotes();
	}
	
	@Command(hook = "an_del")
	public void delNote(CommandSender sender, int id)
	{
		if (id < notes.size() && id >= 0 && notes.get(id) != null)
		{
			notes.remove(id);
			Utils.sendMessage(sender, null, "§aNote " + id + " has been removed!");
			saveNotes();
		}
		else
		{
			Utils.sendMessage(sender, null, "§cThat note does not exist!");
		}
	}
	
	@Command(hook = "an_list")
	public void list(CommandSender sender)
	{
		Utils.sendModuleHeader(sender);
		for (Object note : notes)
		{
			String string = ChatColor.YELLOW + "" + notes.indexOf(note) + ": ";
			string += "§a" + ((JSONArray) note).get(1);
			string += "\n§e - " + ((JSONArray) note).get(0) + ", §6";
			SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm");
			string += format.format((double) ((JSONArray) note).get(2) * 1000);
			Utils.sendMessage(sender, "", string);
		}
	}
	
	public void saveNotes()
	{
		JsonManager.save(notes, saveFile);
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command an {\n" + 
				"	perm utils.adminnotes;\n" + 
				"	\n" + 
				"	add [string:note...] {\n" + 
				"		type player;\n" + 
				"		help Creates a new admin note;\n" + 
				"		run an_create note;\n" + 
				"		perm utils.an;" +
				"	}\n" + 
				"	\n" + 
				"	del [int:id] {\n" + 
				"		help Deletes an admin note;\n" + 
				"		run an_del id;\n" + 
				"		perm utils.an;" +
				"	}\n" + 
				"	\n" + 
				"	list {\n" + 
				"		help Lists all notes;\n" + 
				"		run an_list;\n" + 
				"		perm utils.an;" +
				"	}\n" + 
				"}";
	}
	// @format
}

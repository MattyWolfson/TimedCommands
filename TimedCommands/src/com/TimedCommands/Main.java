package com.TimedCommands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 *
 * @author Matty Wolfson
 * @version Jun 14, 2020
 *
 *          This work complies with the JMU Honor Code.
 */
public class Main extends JavaPlugin
{

	private Map<String, CommandStorage> commandStorage;
	private FileConfiguration storageConfig;
	private File storageFile;

	public void onEnable()
	{
		System.out.println("TimedCommands v1.0 enabled!");
		ConfigurationSerialization.registerClass(CommandStorage.class);
		loadStorageFile();

		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				refresh();
			}
		}, 0L, 1200L);
	}

	public void refresh()
	{
		Iterator<Entry<String, CommandStorage>> i = commandStorage.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, CommandStorage> c = i.next();
			CommandStorage cs = c.getValue();
			System.out.println(cs.endTime() + " < " + System.currentTimeMillis());
			if (cs.endTime() < System.currentTimeMillis())
			{
				forceCommand(c.getKey());
			}
		}
	}

	public void onDisable()
	{
		Iterator<Entry<String, CommandStorage>> i = commandStorage.entrySet().iterator();
		while (i.hasNext())
		{
			Entry<String, CommandStorage> e = i.next();
			storageConfig.set(e.getKey().toString(), e.getValue());
		}
		System.out.println("TimedCommands v1.0 disabled!");
		try
		{
			storageConfig.save(storageFile);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private void loadStorageFile()
	{
		storageFile = new File(getDataFolder(), "storage.yml");
		if (!storageFile.exists())
		{
			storageFile.getParentFile().mkdirs();
			saveResource("storage.yml", false);
		}

		storageConfig = new YamlConfiguration();
		try
		{
			storageConfig.load(storageFile);
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		commandStorage = new HashMap<>();
		for (String key : storageConfig.getConfigurationSection("").getKeys(false))
		{
			CommandStorage player = storageConfig.getSerializable(key, CommandStorage.class);
			commandStorage.put(key, player);
		}

	}

	private void forceCommand(String hashCode)
	{
		CommandStorage cs = commandStorage.get(hashCode);
		String command = cs.endCommand();
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
		commandStorage.remove(hashCode);
		storageConfig.set(hashCode, null);
		try
		{
			storageConfig.save(storageFile);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{

		switch (args[0].toLowerCase())
		{
		case "queue":
			sender.sendMessage("Current Time: " + System.currentTimeMillis());
			Iterator<Entry<String, CommandStorage>> i = commandStorage.entrySet().iterator();
			while (i.hasNext())
			{
				Entry<String, CommandStorage> e = i.next();
				sender.sendMessage(e.getKey() + "- Command: " + e.getValue().endCommand() + ", Duration: "
						+ e.getValue().duration() + ", End: " + e.getValue().endTime());
			}

			return true;
		case "add":
			String cmd = args[2];
			for (int j = 3; j < args.length; j++)
			{
				cmd += " " + args[j];
			}
			CommandStorage newCommand = new CommandStorage(Integer.parseInt(args[1]), cmd);
			commandStorage.put(String.valueOf(newCommand.hashCode()), newCommand);
			return true;
		case "remove":
			commandStorage.remove(args[1]);
			return true;
		case "force":
			forceCommand(args[1]);
			return true;
		}

		return false;
	}
}

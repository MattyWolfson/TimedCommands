package com.TimedCommands;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 *
 * @author Matty Wolfson
 * @version Jun 14, 2020
 *
 *          This work complies with the JMU Honor Code.
 */
public class CommandStorage implements ConfigurationSerializable
{
	private String endCommand;
	private Long startTime;
	private Integer duration;
	private Long endTime;

	public CommandStorage(Map<String, Object> serialized)
	{
		endCommand = (String) serialized.get("endCommand");
		startTime = (Long) serialized.get("startTime");
		duration = (Integer) serialized.get("duration");
		endTime = (Long) serialized.get("endTime");
	}

	public CommandStorage(int duration, String endCommand)
	{
		this.endCommand = endCommand;
		this.duration = duration;
		this.startTime = System.currentTimeMillis();
		this.endTime = startTime + (duration * 1000);
	}

	public String endCommand()
	{
		return endCommand;
	}

	public long duration()
	{
		return duration;
	}

	public long startTime()
	{
		return startTime;
	}

	public long endTime()
	{
		return endTime;
	}

	/**
	 * @return
	 */
	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> serialized = new HashMap<>();
		serialized.put("endCommand", endCommand);
		serialized.put("startTime", startTime);
		serialized.put("duration", duration);
		serialized.put("endTime", endTime);

		return serialized;
	}
}

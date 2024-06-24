package com.lizin5ths.indypets.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Allows Gson to de/serialize Blocklists */
public class BlocklistTypeAdapter extends TypeAdapter<Blocklist> {
	public static final BlocklistTypeAdapter INST = new BlocklistTypeAdapter();

	private static String[] split(String id) {
		int i = id.indexOf(':');

		return new String[] {
			i >= 1 ? id.substring(0, i) : "minecraft",
			i >= 0 ? id.substring(i + 1) : ""
		};
	}

	public static Blocklist blocklistFromRaw(List<String> rawBlocklist) {
		Blocklist blocklist = new Blocklist();

		for (String blocked : rawBlocklist) {
			String[] strings = split(blocked);

			if (strings[1].isEmpty())
				throw new IllegalArgumentException("empty identifier");

			if (strings[1].equals("*")) {
				blocklist.modBlocklist.add(strings[0]);
				continue;
			}

			// proper identifier
			Identifier id = new Identifier(blocked);
			blocklist.idBlocklist.add(id);
		}

		return blocklist;
	}

	public static List<String> blocklistToRaw(Blocklist blocklist) {
		List<String> rawBlocklist = new ArrayList<>();

		for (String modId : blocklist.modBlocklist) {
			rawBlocklist.add(modId + ":*");
		}
		for (Identifier id : blocklist.idBlocklist) {
			rawBlocklist.add(id.toString());
		}

		return rawBlocklist;
	}

	private BlocklistTypeAdapter() { }

	public void write(JsonWriter out, Blocklist value) throws IOException {
		out.beginArray();
		for (String blocked : blocklistToRaw(value))
			out.value(blocked);
		out.endArray();
	}

	public Blocklist read(JsonReader reader) throws IOException {
		List<String> rawBlocklist = new ArrayList<>();

		reader.beginArray();
		while (reader.hasNext())
			rawBlocklist.add(reader.nextString());
		reader.endArray();

		return blocklistFromRaw(rawBlocklist);
	}
}

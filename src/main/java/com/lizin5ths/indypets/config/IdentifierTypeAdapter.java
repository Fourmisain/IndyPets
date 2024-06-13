package com.lizin5ths.indypets.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

/** Allows Gson to de/serialize Identifiers */
public class IdentifierTypeAdapter extends TypeAdapter<Identifier> {
	public static final IdentifierTypeAdapter INST = new IdentifierTypeAdapter();

	private IdentifierTypeAdapter() { }

	public void write(JsonWriter out, Identifier value) throws IOException {
		if (value == null) {
			out.nullValue();
		} else {
			out.value(value.toString());
		}
	}

	public Identifier read(JsonReader reader) throws IOException {
		if (reader.peek() == JsonToken.NULL) {
			reader.nextNull();
			return null;
		} else {
			return Identifier.of(reader.nextString());
		}
	}
}
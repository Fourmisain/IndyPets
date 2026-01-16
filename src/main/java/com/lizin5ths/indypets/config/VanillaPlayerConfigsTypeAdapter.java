package com.lizin5ths.indypets.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/** Serialize only the used fields */
public class VanillaPlayerConfigsTypeAdapter implements TypeAdapterFactory {
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		if (!TypeToken.getParameterized(Map.class, UUID.class, Config.class).equals(type))
			return null;

		var delegate = gson.getDelegateAdapter(this, type);

		return new TypeAdapter<>() {
			@SuppressWarnings("unchecked")
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				var map = (Map<UUID, Config>) value;

				out.beginObject();

				for (var entry : map.entrySet()) {
					var uuid = entry.getKey();
					var config = entry.getValue();

					out.name(uuid.toString());
					out.beginObject();

					// see Config.vanillaCopyOf(Config)
					out.name("regularInteract").value(config.regularInteract);
					out.name("sneakInteract").value(config.sneakInteract);
					out.name("silentMode").value(config.silentMode);
					out.name("homeRadius").value(config.homeRadius);
					out.name("whistleRadius").value(config.whistleRadius);
					out.name("hornState").value(config.hornState);

					out.name("hornConfig");
					out.beginObject();
					for (var hornEntry : config.hornConfig.entrySet()) {
						String hornId = hornEntry.getKey().toString();
						String hornSetting = hornEntry.getValue().toString();
						out.name(hornId).value(hornSetting);
					}
					out.endObject();

					out.endObject();
				}

				out.endObject();
			}

			@Override
			public T read(JsonReader in) throws IOException {
				return delegate.read(in);
			}
		};
	}
}
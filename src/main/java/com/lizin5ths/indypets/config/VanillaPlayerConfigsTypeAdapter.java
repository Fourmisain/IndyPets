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

		TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

		return new TypeAdapter<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				Map<UUID, Config> map = (Map<UUID, Config>) value;

				out.beginObject();

				for (Map.Entry<UUID, Config> entry : map.entrySet()) {
					UUID uuid = entry.getKey();
					Config config = entry.getValue();

					out.name(uuid.toString());
					out.beginObject();

					// see Config.vanillaCopyOf(Config)
					out.name("regularInteract").value(config.regularInteract);
					out.name("sneakInteract").value(config.sneakInteract);
					out.name("silentMode").value(config.silentMode);
					out.name("homeRadius").value(config.homeRadius);

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
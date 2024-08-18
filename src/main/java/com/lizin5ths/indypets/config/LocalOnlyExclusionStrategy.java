package com.lizin5ths.indypets.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class LocalOnlyExclusionStrategy implements ExclusionStrategy {
	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return clazz.getAnnotation(LocalOnly.class) != null;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(LocalOnly.class) != null;
	}
}

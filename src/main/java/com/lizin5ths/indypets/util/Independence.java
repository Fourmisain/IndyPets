package com.lizin5ths.indypets.util;

import net.minecraft.util.math.BlockPos;

public interface Independence {
	boolean indypets$isIndependent();
	void indypets$toggleIndependence();
	BlockPos indypets$getHomePos();
}

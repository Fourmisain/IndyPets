package com.lizin5ths.indypets.util;

import net.minecraft.core.BlockPos;

public interface IndependentPet {
	boolean indypets$isIndependent();
	void indypets$setIndependent(boolean value);
	BlockPos indypets$getHomePos();
	void indypets$setHome();
}

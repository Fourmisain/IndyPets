package com.lizin5ths.indypets.util;

import net.minecraft.util.math.BlockPos;

public interface Follower {
	boolean isFollowing();
	void setFollowing(boolean value);
	BlockPos getHomePos();
}

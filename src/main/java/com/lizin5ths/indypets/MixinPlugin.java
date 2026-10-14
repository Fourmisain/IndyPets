package com.lizin5ths.indypets;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
	public static boolean test(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean testVersion(String modId, String versionRange) {
		try {
			Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modId);
			if (container.isEmpty())
				return false;

			VersionPredicate pred = VersionPredicate.parse(versionRange);
			Version version = container.get().getMetadata().getVersion();

			return pred.test(version);
		} catch (VersionParsingException e) {
			LogManager.getLogger("indypets").error("version matching failed!", e);
			return false;
		}
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		// com.lizin5ths.indypets.mixin
		String[] split = mixinClassName.split("\\.");

		if (split[4].equals("compat")) {
			// compat.modId.mixinName
			String modId = split[5];
			String mixinName = split[split.length - 1];

			if (modId.equals("friendsandfoes")) return testVersion(modId, ">=3.0.0");

			if (modId.equals("followersteleporttoo")) {
				if (mixinName.equals("TeleportEventMixin")) return testVersion(modId, "<3");
				if (mixinName.equals("TeleportEventV3Mixin")) return testVersion(modId, ">=3");
			}

			return test(modId);
		}

		return true;
	}

	@Override public void onLoad(String mixinPackage) { }
	@Override public String getRefMapperConfig() { return null; }
	@Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
	@Override public List<String> getMixins() { return null; }
	@Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
	@Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}

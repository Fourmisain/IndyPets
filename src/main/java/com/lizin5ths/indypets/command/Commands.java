package com.lizin5ths.indypets.command;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.HornSetting;
import com.lizin5ths.indypets.config.ServerConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class Commands {
	private static class WhistleSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
		public static final WhistleSuggestionProvider INDEPENDENT = new WhistleSuggestionProvider(true);
		public static final WhistleSuggestionProvider FOLLOWING = new WhistleSuggestionProvider(false);

		private final boolean independent;

		public WhistleSuggestionProvider(boolean independent) {
			this.independent = independent;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			var level = context.getSource().getLevel();
			var player = context.getSource().getPlayerOrException();

			var suggestions = new ArrayList<Identifier>();

			var config = ServerConfig.getDefaultedPlayerConfig(player.getUUID());

			// suggest ids of owned, nearby pets that can be affected
			for (var entity : level.getEntities((Entity) null,
					new AABB(player.position(), player.position()).inflate(config.whistleRadius),
					entity -> canInteract(player, entity) && independent == isIndependent(entity))) {
				suggestions.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()));
			}

			return SharedSuggestionProvider.suggestResource(suggestions, builder);
		}
	}

	public static class WhistleCommand implements Command<CommandSourceStack> {
		public static final WhistleCommand WHISTLE = new WhistleCommand(false, false);
		public static final WhistleCommand UNWHISTLE = new WhistleCommand(false, true);
		public static final WhistleCommand TARGETED_WHISTLE = new WhistleCommand(true, false);
		public static final WhistleCommand TARGETED_UNWHISTLE = new WhistleCommand(true, true);

		public static WhistleCommand untargeted(boolean unwhistle) {
			return unwhistle ? UNWHISTLE : WHISTLE;
		}

		private final boolean targeted;
		private final boolean unwhistle;

		private WhistleCommand(boolean targeted, boolean unwhistle) {
			this.targeted = targeted;
			this.unwhistle = unwhistle;
		}

		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			Identifier targets = targeted ? IdentifierArgument.getId(context, "targets") : null;

			var player = context.getSource().getPlayerOrException();
			var level = context.getSource().getLevel();

			run(level, player, targets);

			return 0;
		}

		public void run(ServerLevel level, ServerPlayer player) {
			run(level, player, null);
		}

		public void run(ServerLevel level, ServerPlayer player, Identifier targets) {
			var config = ServerConfig.getDefaultedPlayerConfig(player.getUUID());

			for (var entity : level.getEntities((Entity) null,
					new AABB(player.position(), player.position()).inflate(config.whistleRadius),
					entity -> {
						boolean canWhistle = canInteract(player, entity) && unwhistle == !isIndependent(entity);

						if (targeted) {
							return canWhistle && entity.getType().equals(BuiltInRegistries.ENTITY_TYPE.getValue(targets));
						} else {
							return canWhistle;
						}
					})) {
				toggleIndependence(entity);
				showPetStatus(player, entity, false);
			}
		}
	}

	private static class SetConfigOptionCommand<T> implements Command<CommandSourceStack> {
		public interface ConfigSetter<T> {
			void set(Config config, T value) throws CommandSyntaxException;
		}

		private final ConfigSetter<T> setter;

		public SetConfigOptionCommand(ConfigSetter<T> setter) {
			this.setter = setter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			var nodes = context.getNodes();
			String argumentName = nodes.getLast().getNode().getName();
			String option = nodes.get(nodes.size() - 2).getNode().getName();

			var config = Config.vanilla(player.getUUID());

			T value = (T) context.getArgument(argumentName, Object.class);
			setter.set(config, value);

			player.sendSystemMessage(Component.literal("set " + option + " to " + value));

			return 0;
		}
	}

	// note: a EnumArgumentType<HornSetting> would need to be registered in ArgumentTypes, which requires the client knowing about the enum, hence we just use strings
	private static class HornSettingSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			for (var hornSetting : HornSetting.values()) {
				builder.suggest(hornSetting.getSerializedName());
			}

			return builder.buildFuture();
		}
	}

	private static class HornTypeSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			context.getSource().registryAccess()
				.lookupOrThrow(Registries.INSTRUMENT)
				.get(InstrumentTags.GOAT_HORNS)
				.ifPresent(horns -> {
					for (var entry : horns) {
						builder.suggest(entry.getRegisteredName());
					}
				});

			return builder.buildFuture();
		}
	}

	private static class SetHornSettingCommand implements Command<CommandSourceStack> {
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			Identifier hornId = context.getArgument("horn_type", Identifier.class);
			String setting = context.getArgument("setting", String.class);
			HornSetting hornSetting = HornSetting.valueOf(HornSetting.class, setting.toUpperCase(Locale.ROOT));

			var config = Config.vanilla(player.getUUID());
			config.setHornSetting(hornId, hornSetting);

			player.sendSystemMessage(Component.literal("set horn " + hornId + " to " + hornSetting.getSerializedName()));

			return 0;
		}
	}

	private static class GetHornSettingsCommand implements Command<CommandSourceStack> {
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			var config = Config.vanilla(player.getUUID());

			if (config.hornConfig.isEmpty()) {
				player.sendSystemMessage(Component.literal("no horns are set"));
			} else {
				for (var entry : config.hornConfig.entrySet()) {
					player.sendSystemMessage(Component.literal("horn " + entry.getKey() + " is set to " + entry.getValue().getSerializedName()));
				}
			}

			return 0;
		}
	}

	private static class GetConfigCommand implements Command<CommandSourceStack> {
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			var config = Config.vanilla(player.getUUID());

			player.sendSystemMessage(Component.literal("regularInteract: " + config.regularInteract));
			player.sendSystemMessage(Component.literal("sneakInteract: " + config.sneakInteract));
			player.sendSystemMessage(Component.literal("silentMode: " + config.silentMode));
			player.sendSystemMessage(Component.literal("homeRadius: " + config.homeRadius));
			player.sendSystemMessage(Component.literal("whistleRadius: " + config.whistleRadius));

			return 0;
		}
	}

	private static class ResetConfigCommand implements Command<CommandSourceStack> {
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			Config.resetVanilla(player.getUUID());

			player.sendSystemMessage(Component.literal("Reset config to server default"));

			return 0;
		}
	}

	private static class HelpCommand implements Command<CommandSourceStack> {
		@Override
		public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
			ServerPlayer player = context.getSource().getPlayerOrException();

			String input = context.getNodes().getLast().getNode().getName();

			String message = switch (input) {
				case "silentMode" -> "Use particle effects instead of status messages. Pets show green sparkles when they are set to follow and thunder clouds when they are set independent.";
				case "regularInteract" -> "Cycle a pet's state between sitting, following and independent by regular interact (right click). Note that e.g. parrots cannot be interacted with when flying.";
				case "sneakInteract" -> "Change a pet's state between following and independent by sneak interact (shift + right click)";
				case "homeRadius" -> "Pets can roam within this block radius of their home before turning back.\"Home\" is where the pet was last set independent.";
				case "whistleRadius" -> "How many blocks the whistle reaches.";
				default -> throw new SimpleCommandExceptionType(new LiteralMessage("no help for " + input)).create();
			};

			player.sendSystemMessage(Component.literal(input + ": " + message));

			return 0;
		}
	}

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal(IndyPets.MOD_ID)
				.then(literal("whistle")
					.executes(WhistleCommand.WHISTLE)
					.then(argument("targets", IdentifierArgument.id())
						.suggests(WhistleSuggestionProvider.INDEPENDENT)
						.executes(WhistleCommand.TARGETED_WHISTLE)))
				.then(net.minecraft.commands.Commands.literal("unwhistle")
					.executes(WhistleCommand.UNWHISTLE)
					.then(argument("targets", IdentifierArgument.id())
						.suggests(WhistleSuggestionProvider.FOLLOWING)
						.executes(WhistleCommand.TARGETED_UNWHISTLE)))
				.then(net.minecraft.commands.Commands.literal("horn")
					.requires(Commands::isVanillaPlayer)
					.executes(new GetHornSettingsCommand())
					.then(argument("horn_type", IdentifierArgument.id())
						.suggests(new HornTypeSuggestionProvider())
						.then(argument("setting", StringArgumentType.string())
							.suggests(new HornSettingSuggestionProvider())
							.executes(new SetHornSettingCommand()))))
				.then(net.minecraft.commands.Commands.literal("config")
					.requires(Commands::isVanillaPlayer)
					.then(net.minecraft.commands.Commands.literal("set")
						.then(net.minecraft.commands.Commands.literal("silentMode")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.silentMode = value;
								}))))
						.then(net.minecraft.commands.Commands.literal("regularInteract")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.regularInteract = value;
								}))))
						.then(net.minecraft.commands.Commands.literal("sneakInteract")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.sneakInteract = value;
								}))))
						.then(net.minecraft.commands.Commands.literal("homeRadius")
							.then(argument("0 - 128", integer())
								.executes(new SetConfigOptionCommand<Integer>((config, value) -> {
									if (!(0 <= value && value <= 128)) throw new SimpleCommandExceptionType(new LiteralMessage("needs to be between 0 and 128")).create();
									config.homeRadius = value;
								}))))
						.then(net.minecraft.commands.Commands.literal("whistleRadius")
							.then(argument("4 - 144", integer())
								.executes(new SetConfigOptionCommand<Integer>((config, value) -> {
									if (!(4 <= value && value <= 144)) throw new SimpleCommandExceptionType(new LiteralMessage("needs to be between 4 and 144")).create();
									config.whistleRadius = value;
								}))))
					)
					.then(net.minecraft.commands.Commands.literal("help")
						.then(net.minecraft.commands.Commands.literal("silentMode")
							.executes(new HelpCommand()))
						.then(net.minecraft.commands.Commands.literal("regularInteract")
							.executes(new HelpCommand()))
						.then(net.minecraft.commands.Commands.literal("sneakInteract")
							.executes(new HelpCommand()))
						.then(net.minecraft.commands.Commands.literal("homeRadius")
							.executes(new HelpCommand()))
						.then(net.minecraft.commands.Commands.literal("whistleRadius")
							.executes(new HelpCommand()))
					)
					.then(net.minecraft.commands.Commands.literal("get")
						.executes(new GetConfigCommand()))
					.then(net.minecraft.commands.Commands.literal("reset")
						.executes(new ResetConfigCommand()))));

		});
	}

	private static boolean isVanillaPlayer(CommandSourceStack source) {
		return source.getPlayer() != null && !ServerConfig.HAS_MOD_INSTALLED.contains(source.getPlayer().getUUID());
	}
}

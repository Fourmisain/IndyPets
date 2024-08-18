package com.lizin5ths.indypets.command;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.config.Config;
import com.lizin5ths.indypets.config.ServerConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.lizin5ths.indypets.util.IndyPetsUtil.*;
import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
	public static final int WHISTLE_RADIUS = 96;

	private static class WhistleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
		public static final WhistleSuggestionProvider INDEPENDENT = new WhistleSuggestionProvider(true);
		public static final WhistleSuggestionProvider FOLLOWING = new WhistleSuggestionProvider(false);

		private final boolean independent;

		public WhistleSuggestionProvider(boolean independent) {
			this.independent = independent;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			ServerWorld world = context.getSource().getWorld();
			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

			List<Identifier> suggestions = new ArrayList<>();

			// suggest ids of owned, nearby pets that can be affected
			for (Entity entity : world.getOtherEntities(null,
					new Box(player.getPos(), player.getPos()).expand(WHISTLE_RADIUS),
					entity -> canInteract(player, entity) && independent == isIndependent((TameableEntity) entity))) {
				suggestions.add(Registry.ENTITY_TYPE.getId(entity.getType()));
			}

			return CommandSource.suggestIdentifiers(suggestions, builder);
		}
	}

	private static class WhistleCommand implements Command<ServerCommandSource> {
		public static final WhistleCommand WHISTLE = new WhistleCommand(false, false);
		public static final WhistleCommand UNWHISTLE = new WhistleCommand(false, true);
		public static final WhistleCommand TARGETED_WHISTLE = new WhistleCommand(true, false);
		public static final WhistleCommand TARGETED_UNWHISTLE = new WhistleCommand(true, true);

		private final boolean targeted;
		private final boolean unwhistle;

		private WhistleCommand(boolean targeted, boolean unwhistle) {
			this.targeted = targeted;
			this.unwhistle = unwhistle;
		}

		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			Identifier id = targeted ? IdentifierArgumentType.getIdentifier(context, "targets") : null;

			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
			ServerWorld world = context.getSource().getWorld();

			for (Entity entity : world.getOtherEntities(null,
					new Box(player.getPos(), player.getPos()).expand(WHISTLE_RADIUS),
					entity -> {
						boolean canWhistle = canInteract(player, entity) && unwhistle == !isIndependent((TameableEntity) entity);

						if (targeted) {
							return canWhistle && entity.getType().equals(Registry.ENTITY_TYPE.get(id));
						} else {
							return canWhistle;
						}
					})) {
				TameableEntity tameable = (TameableEntity) entity;

				toggleIndependence(tameable);
				showPetStatus(player, tameable, false);
			}

			return 0;
		}
	}

	private static class SetConfigOptionCommand<T> implements Command<ServerCommandSource> {
		public interface ConfigSetter<T> {
			void set(Config config, T value) throws CommandSyntaxException;
		}

		private final ConfigSetter<T> setter;

		public SetConfigOptionCommand(ConfigSetter<T> setter) {
			this.setter = setter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

			var nodes = context.getNodes();
			String argumentName = nodes.get(nodes.size() - 1).getNode().getName();
			String option = nodes.get(nodes.size() - 2).getNode().getName();

			Config config = Config.vanilla(player.getUuid());

			T value = (T) context.getArgument(argumentName, Object.class);
			setter.set(config, value);

			Config.save();

			player.sendMessage(Text.literal("set " + option + " to " + value));

			return 0;
		}
	}

	private static class GetConfigCommand implements Command<ServerCommandSource> {
		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

			Config config = Config.vanilla(player.getUuid());

			player.sendMessage(Text.literal("regularInteract: " + config.regularInteract));
			player.sendMessage(Text.literal("sneakInteract: " + config.sneakInteract));
			player.sendMessage(Text.literal("silentMode: " + config.silentMode));
			player.sendMessage(Text.literal("homeRadius: " + config.homeRadius));

			return 0;
		}
	}

	private static class ResetConfigCommand implements Command<ServerCommandSource> {
		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

			Config.resetVanilla(player.getUuid());
			Config.save();

			player.sendMessage(Text.literal("Reset config to server default"));

			return 0;
		}
	}

	private static class HelpCommand implements Command<ServerCommandSource> {
		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			ServerPlayerEntity player = context.getSource().getPlayerOrThrow();

			var nodes = context.getNodes();
			String input = nodes.get(nodes.size() - 1).getNode().getName();

			String message = switch (input) {
				case "silentMode" -> "Use particle effects instead of status messages. Pets show green sparkles when they are set to follow and thunder clouds when they are set independent.";
				case "regularInteract" -> "Cycle a pet's state between sitting, following and independent by regular interact (right click). Note that e.g. parrots cannot be interacted with when flying.";
				case "sneakInteract" -> "Change a pet's state between following and independent by sneak interact (shift + right click)";
				case "homeRadius" -> "Pets can roam within this block radius of their home before turning back.\"Home\" is where the pet was last set independent.";
				default -> throw new SimpleCommandExceptionType(new LiteralMessage("no help for " + input)).create();
			};

			player.sendMessage(Text.literal(input + ": " + message));

			return 0;
		}
	}

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal(IndyPets.MOD_ID)
				.then(literal("whistle")
					.executes(WhistleCommand.WHISTLE)
					.then(argument("targets", IdentifierArgumentType.identifier())
						.suggests(WhistleSuggestionProvider.INDEPENDENT)
						.executes(WhistleCommand.TARGETED_WHISTLE)))
				.then(CommandManager.literal("unwhistle")
					.executes(WhistleCommand.UNWHISTLE)
					.then(argument("targets", IdentifierArgumentType.identifier())
						.suggests(WhistleSuggestionProvider.FOLLOWING)
						.executes(WhistleCommand.TARGETED_UNWHISTLE)))
				.then(CommandManager.literal("config")
					// only available for vanilla players
					.requires(source -> source.getPlayer() != null && !ServerConfig.HAS_MOD_INSTALLED.contains(source.getPlayer().getUuid()))
					.then(CommandManager.literal("set")
						.then(CommandManager.literal("silentMode")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.silentMode = value;
								}))))
						.then(CommandManager.literal("regularInteract")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.regularInteract = value;
								}))))
						.then(CommandManager.literal("sneakInteract")
							.then(argument("value", bool())
								.executes(new SetConfigOptionCommand<Boolean>((config, value) -> {
									config.sneakInteract = value;
								}))))
						.then(CommandManager.literal("homeRadius")
							.then(argument("0 - 128", integer())
								.executes(new SetConfigOptionCommand<Integer>((config, value) -> {
									if (!(0 <= value && value <= 128)) throw new SimpleCommandExceptionType(new LiteralMessage("needs to be between 0 and 128")).create();
									config.homeRadius = value;
								}))))
					)
					.then(CommandManager.literal("help")
						.then(CommandManager.literal("silentMode")
							.executes(new HelpCommand()))
						.then(CommandManager.literal("regularInteract")
							.executes(new HelpCommand()))
						.then(CommandManager.literal("sneakInteract")
							.executes(new HelpCommand()))
						.then(CommandManager.literal("homeRadius")
							.executes(new HelpCommand()))
					)
					.then(CommandManager.literal("get")
						.executes(new GetConfigCommand()))
					.then(CommandManager.literal("reset")
						.executes(new ResetConfigCommand()))));

		});
	}
}

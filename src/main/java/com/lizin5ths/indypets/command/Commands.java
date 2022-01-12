package com.lizin5ths.indypets.command;

import com.lizin5ths.indypets.IndyPets;
import com.lizin5ths.indypets.util.Follower;
import com.lizin5ths.indypets.util.IndyPetsUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.lizin5ths.indypets.util.IndyPetsUtil.isFollowing;
import static com.lizin5ths.indypets.util.IndyPetsUtil.isPetOf;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
	private static class WhistleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
		public static WhistleSuggestionProvider FOLLOWING = new WhistleSuggestionProvider(true);
		public static WhistleSuggestionProvider INDEPENDENT = new WhistleSuggestionProvider(false);

		private final boolean following;

		public WhistleSuggestionProvider(boolean following) {
			this.following = following;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			ServerWorld world = context.getSource().getWorld();
			ServerPlayerEntity player = context.getSource().getPlayer();

			List<Identifier> suggestions = new ArrayList<>();

			// suggest ids of owned, nearby pets that can be affected
			for (Entity entity : world.getOtherEntities(null,
				new Box(player.getPos(), player.getPos()).expand(64),
				(entity -> isPetOf(entity, player) && (following == isFollowing(entity))))) {
				suggestions.add(Registry.ENTITY_TYPE.getId(entity.getType()));
			}

			return CommandSource.suggestIdentifiers(suggestions, builder);
		}
	}

	private static class WhistleCommand implements Command<ServerCommandSource> {
		public static WhistleCommand WHISTLE = new WhistleCommand(false, false);
		public static WhistleCommand UNWHISTLE = new WhistleCommand(false, true);
		public static WhistleCommand TARGETED_WHISTLE = new WhistleCommand(true, false);
		public static WhistleCommand TARGETED_UNWHISTLE = new WhistleCommand(true, true);

		private final boolean targeted;
		private final boolean unwhistle;

		private WhistleCommand(boolean targeted, boolean unwhistle) {
			this.targeted = targeted;
			this.unwhistle = unwhistle;
		}

		@Override
		public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			Identifier id;
			if (targeted) {
				id = IdentifierArgumentType.getIdentifier(context, "targets");
			} else {
				id = null;
			}

			ServerPlayerEntity player = (ServerPlayerEntity) context.getSource().getEntityOrThrow();
			ServerWorld world = context.getSource().getWorld();

			for (Entity entity : world.getOtherEntities(null,
				new Box(player.getPos(), player.getPos()).expand(64),
				(entity -> {
					if (targeted) {
						return isPetOf(entity, player) && entity.getType().equals(Registry.ENTITY_TYPE.get(id));
					} else {
						return isPetOf(entity, player);
					}
				}))) {
				Follower follower = (Follower) entity;

				if (unwhistle == follower.isFollowing()) {
					IndyPetsUtil.changeFollowing(player, (TameableEntity) entity);
				}
			}

			return 0;
		}
	}

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
						.executes(WhistleCommand.TARGETED_UNWHISTLE))));
		});
	}
}

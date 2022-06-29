package com.lizin5ths.indypets.mixin.access;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Identifier.class)
public interface IdentifierAccessor {
    @Invoker
    static String[] invokeSplit(String id, char delimiter) {
        throw new AssertionError();
    }
}

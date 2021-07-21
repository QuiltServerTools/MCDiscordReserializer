/*
 * MCDiscordReserializer: A library for transcoding between Minecraft and Discord.
 * Copyright (C) 2018-2021 Vankka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.quiltservertools.mcdiscordreserializer;

import com.github.quiltservertools.mcdiscordreserializer.minecraft.MinecraftSerializer;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;

public class TestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        Text text = MinecraftSerializer.INSTANCE.serialize("**Bold**");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    CommandManager.literal("markdown")
                            .then(CommandManager.argument("markdown", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        var string = StringArgumentType.getString(context, "markdown");
                                        context.getSource().sendFeedback(MinecraftSerializer.INSTANCE.serialize(string), false);

                                        return 0;
                                    }))
            );
        });
    }
}

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

package io.github.quiltservertools.mcdiscordreserializer.renderer.implementation;

import io.github.quiltservertools.mcdiscordreserializer.renderer.MinecraftRenderer;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

/**
 * The default implementation for the {@link MinecraftRenderer}.
 */
public class DefaultMinecraftRenderer implements MinecraftRenderer {

    /**
     * The instance of {@link DefaultMinecraftRenderer}.
     */
    public static final DefaultMinecraftRenderer INSTANCE = new DefaultMinecraftRenderer();

    private static final Pattern PATTERN_NEWLINE = Pattern.compile("\n");

    /**
     * Creates a new instance of the {@link DefaultMinecraftRenderer} unless you're extending the class you shouldn't use this.
     *
     * @see #INSTANCE
     */
    public DefaultMinecraftRenderer() {
    }

    @Override
    public MutableText strikethrough(MutableText component) {
        return component.formatted(Formatting.STRIKETHROUGH);
    }

    @Override
    public MutableText underline(MutableText component) {
        return component.formatted(Formatting.UNDERLINE);
    }

    @Override
    public MutableText italics(MutableText component) {
        return component.formatted(Formatting.ITALIC);
    }

    @Override
    public MutableText bold(MutableText component) {
        return component.formatted(Formatting.BOLD);
    }

    @Override
    public MutableText codeString(MutableText component) {
        return component.formatted(Formatting.DARK_GRAY);
    }

    @Override
    public MutableText codeBlock(MutableText component) {
        return component.formatted(Formatting.DARK_GRAY);
    }

    @Override
    public MutableText appendSpoiler(MutableText component, MutableText content) {
        return component.append(new LiteralText("▌".repeat(content.getString().length())).styled(style ->
                style.withColor(Formatting.DARK_GRAY)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, content))));
    }

    @Override
    public MutableText appendQuote(MutableText component, MutableText content) {
        MutableText prefix = new LiteralText("| ").formatted(Formatting.DARK_GRAY, Formatting.BOLD);
        // TODO fix multiline quotes
        // return new LiteralText("").append(prefix).append(component.replaceText(PATTERN_NEWLINE, builder -> builder.append(prefix)));
        return new LiteralText("").append(prefix).append(component);
    }

    @Override
    public MutableText appendEmoteMention(MutableText component, String name, String id) {
        return component.append(new LiteralText(":" + name + ":"));
    }

    @Override
    public MutableText appendChannelMention(MutableText component, String id) {
        return component.append(new LiteralText("<#" + id + ">"));
    }

    @Override
    public MutableText appendUserMention(MutableText component, String id) {
        return component.append(new LiteralText("<@" + id + ">"));
    }

    @Override
    public MutableText appendRoleMention(MutableText component, String id) {
        return component.append(new LiteralText("<@&" + id + ">"));
    }
}

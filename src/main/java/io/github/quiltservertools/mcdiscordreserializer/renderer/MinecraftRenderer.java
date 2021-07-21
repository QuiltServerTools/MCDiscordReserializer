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

package io.github.quiltservertools.mcdiscordreserializer.renderer;

import io.github.quiltservertools.mcdiscordreserializer.minecraft.MinecraftSerializerOptions;
import io.github.quiltservertools.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.TextStyle;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.StyleNode;
import dev.vankka.simpleast.core.node.TextNode;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Interface for rendering formatting {@link dev.vankka.simpleast.core.node.Node}s into Minecraft
 * {@link MutableText}s for standard {@link dev.vankka.simpleast.core.TextStyle}s.
 */
public interface MinecraftRenderer extends MinecraftNodeRenderer {

    @Override
    default MutableText render(MutableText mutableText, Node<Object> node, MinecraftSerializerOptions<MutableText> serializerOptions,
                             Function<Node<Object>, MutableText> renderWithChildren) {
        if (node instanceof TextNode) {
            mutableText = new LiteralText(((TextNode<Object>) node).getContent());
        } else if (node instanceof StyleNode) {
            List<TextStyle> styles = new ArrayList<>(((StyleNode<?, TextStyle>) node).getStyles());
            for (TextStyle style : styles) {
                switch (style.getType()) {
                    case STRIKETHROUGH:
                        mutableText = strikethrough(mutableText);
                        break;
                    case UNDERLINE:
                        mutableText = underline(mutableText);
                        break;
                    case ITALICS:
                        mutableText = italics(mutableText);
                        break;
                    case BOLD:
                        mutableText = bold(mutableText);
                        break;
                    case CODE_STRING:
                        mutableText = codeString(mutableText);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case CODE_BLOCK:
                        mutableText = codeBlock(mutableText);
                        ((StyleNode<?, TextStyle>) node).getStyles().remove(style);
                        break;
                    case QUOTE:
                        MutableText content = new LiteralText("");
                        for (Node<Object> objectNode : serializerOptions.getParser().parse(style.getExtra().get("content"),
                                new DiscordMarkdownRules.QuoteState(true),
                                serializerOptions.getRules(),
                                serializerOptions.isDebuggingEnabled())) {
                            content = content.append(renderWithChildren.apply(objectNode));
                        }

                        mutableText = appendQuote(mutableText, content);
                        break;
                    case SPOILER:
                        content = new LiteralText("");
                        for (Node<Object> objectNode : serializerOptions.getParser().parse(style.getExtra().get("content"),
                                null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled())) {
                            content = content.append(renderWithChildren.apply(objectNode));
                        }

                        mutableText = appendSpoiler(mutableText, content);
                        break;
                    case MENTION_EMOJI:
                        mutableText = appendEmoteMention(mutableText, style.getExtra().get("name"), style.getExtra().get("id"));
                        break;
                    case MENTION_CHANNEL:
                        mutableText = appendChannelMention(mutableText, style.getExtra().get("id"));
                        break;
                    case MENTION_USER:
                        mutableText = appendUserMention(mutableText, style.getExtra().get("id"));
                        break;
                    case MENTION_ROLE:
                        mutableText = appendRoleMention(mutableText, style.getExtra().get("id"));
                        break;
                    default:
                        break;
                }
            }
        }

        return mutableText;
    }

    /**
     * Renders the provided {@link MutableText} as strikethrough.
     *
     * @param part the {@link MutableText} to render as strikethrough
     * @return the strikethrough {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText strikethrough(@NotNull MutableText part);

    /**
     * Renders the provided {@link MutableText} as underlined.
     *
     * @param part the {@link MutableText} to render as underlined
     * @return the underlined {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText underline(@NotNull MutableText part);

    /**
     * Renders the provided {@link MutableText} as italics.
     *
     * @param part the {@link MutableText} to render as italics
     * @return the italics {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText italics(@NotNull MutableText part);

    /**
     * Renders the provided {@link MutableText} as bold.
     *
     * @param part the {@link MutableText} to render as bold
     * @return the bold {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText bold(@NotNull MutableText part);

    /**
     * Renders the provided {@link MutableText} as a code string.
     *
     * @param part the {@link MutableText} to render the code string to
     * @return the code stringed {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText codeString(@NotNull MutableText part);

    /**
     * Renders the provided {@link MutableText} as a code block.
     *
     * @param part the {@link MutableText} to render as a code block
     * @return the code blocked {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText codeBlock(@NotNull MutableText part);

    /**
     * Renders the spoiler and appends it to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render the spoiler to
     * @param content   the content of the spoiler
     * @return the spoiler'ed {@link MutableText} or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendSpoiler(@NotNull MutableText MutableText, @NotNull MutableText content);

    /**
     * Adds the required formatting for quotes to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render to
     * @param content   the content of the quote
     * @return the {@link MutableText} with the quote rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendQuote(@NotNull MutableText MutableText, @NotNull MutableText content);

    /**
     * Renders a emote mention and appends it to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render to
     * @param name      the name of the emote
     * @param id        the id of the emote
     * @return the {@link MutableText} with emote rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendEmoteMention(@NotNull MutableText MutableText, @NotNull String name, @NotNull String id);

    /**
     * Renders a channel mention and appends it to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render to
     * @param id        the id of the channel
     * @return the {@link MutableText} with the channel mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendChannelMention(@NotNull MutableText MutableText, @NotNull String id);

    /**
     * Renders a user mention and appends it to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render to
     * @param id        the id of the user
     * @return the {@link MutableText} with the user mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendUserMention(@NotNull MutableText MutableText, @NotNull String id);

    /**
     * Renders a role mention and appends it to the provided {@link MutableText}.
     *
     * @param MutableText the {@link MutableText} to render to
     * @param id        the id of the role
     * @return the {@link MutableText} with the role mention rendered or {@code null} if this renderer does not process that kinds of styles
     */
    @Nullable
    MutableText appendRoleMention(@NotNull MutableText MutableText, @NotNull String id);
}

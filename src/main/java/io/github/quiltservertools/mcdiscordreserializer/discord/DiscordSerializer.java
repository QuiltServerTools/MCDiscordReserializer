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

package io.github.quiltservertools.mcdiscordreserializer.discord;

import io.github.quiltservertools.mcdiscordreserializer.rules.DiscordMarkdownRules;
import io.github.quiltservertools.mcdiscordreserializer.text.Text;
import net.minecraft.text.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * DiscordSerializer, for serializing from Minecraft {@link net.minecraft.text.MutableText}s to Discord messages.
 *
 * @author Vankka
 *
 * @see DiscordSerializerOptions
 * @see DiscordMarkdownRules
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class DiscordSerializer {

    /**
     * Default instance of the DiscordSerializer, incase that's all you need.
     * Using {@link DiscordSerializer#setDefaultOptions(DiscordSerializerOptions)} is not allowed.
     */
    public static final DiscordSerializer INSTANCE = new DiscordSerializer() {
        @Override
        public void setDefaultOptions(DiscordSerializerOptions defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setKeybindProvider(Function<KeybindText, String> provider) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @SuppressWarnings("deprecation")
        @Override
        @Deprecated
        public void setTranslationProvider(Function<TranslatableText, String> provider) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link DiscordSerializerOptions} to use for this serializer.
     */
    private DiscordSerializerOptions defaultOptions;
    private Function<KeybindText, String> keybindProvider;
    private Function<TranslatableText, String> translationProvider;

    /**
     * Constructor for creating a serializer, which {@link DiscordSerializerOptions#defaults()} as defaults.
     */
    public DiscordSerializer() {
        this(DiscordSerializerOptions.defaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link DiscordSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see DiscordSerializerOptions#defaults()
     * @see DiscordSerializerOptions#DiscordSerializerOptions(boolean, boolean, Function, Function)
     */
    public DiscordSerializer(@NotNull DiscordSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    /**
     * Constructor fore creating a serializer with translations provided with arguments.
     *
     * @param keybindProvider     The keybind provider.
     * @param translationProvider The translation provider.
     * @deprecated Use {@link #DiscordSerializer(DiscordSerializerOptions)}
     * {@link DiscordSerializerOptions#withKeybindProvider(Function)}
     * {@link DiscordSerializerOptions#withTranslationProvider(Function)}
     */
    @Deprecated
    public DiscordSerializer(Function<KeybindText, String> keybindProvider,
                             Function<TranslatableText, String> translationProvider) {
        this.defaultOptions = DiscordSerializerOptions.defaults();
        this.keybindProvider = keybindProvider;
        this.translationProvider = translationProvider;
    }

    /**
     * Returns the keybind provider for this serializer.
     *
     * @return keybind provider, a KeybindText to String function
     * @deprecated Use {@link #getDefaultOptions()} {@link DiscordSerializerOptions#getKeybindProvider()}
     */
    @Deprecated
    public Function<KeybindText, String> getKeybindProvider() {
        return keybindProvider;
    }

    /**
     * Sets the keybind provider for this serializer.
     *
     * @param provider a KeybindText to String function
     * @deprecated Use {@link #setDefaultOptions(DiscordSerializerOptions)} {@link DiscordSerializerOptions#withKeybindProvider(Function)}
     */
    @Deprecated
    public void setKeybindProvider(Function<KeybindText, String> provider) {
        keybindProvider = provider;
    }

    /**
     * Returns the translation provider for this serializer.
     *
     * @return keybind provider, a TranslatableComponent to String function
     * @deprecated Use {@link #getDefaultOptions()} {@link DiscordSerializerOptions#getTranslationProvider()}
     */
    @Deprecated
    public Function<TranslatableText, String> getTranslationProvider() {
        return translationProvider;
    }

    /**
     * Sets the translation provider for this serializer.
     *
     * @param provider a TranslationComponent to String function
     * @deprecated Use {@link #setDefaultOptions(DiscordSerializerOptions)} {@link DiscordSerializerOptions#withTranslationProvider(Function)}
     */
    @Deprecated
    public void setTranslationProvider(Function<TranslatableText, String> provider) {
        translationProvider = provider;
    }

    /**
     * Serializes a {@link net.minecraft.text.MutableText} to Discord formatting (markdown) with this serializer's {@link DiscordSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link DiscordSerializer#serialize(MutableText, DiscordSerializerOptions)} to fine tune the serialization options.
     *
     * @param component The text component from a Minecraft chat message
     * @return Discord markdown formatted String
     */
    public String serialize(@NotNull final MutableText component) {
        DiscordSerializerOptions options = getDefaultOptions();
        if (keybindProvider != null) {
            options = options.withKeybindProvider(keybindProvider);
        }
        if (translationProvider != null) {
            options = options.withTranslationProvider(translationProvider);
        }
        return serialize(component, options);
    }

    /**
     * Serializes a MutableText (from a chat message) to Discord formatting (markdown).
     *
     * @param component     The text component from a Minecraft chat message
     * @param embedLinks    Makes messages format as [message content](url) when there is a open_url clickEvent (for embeds)
     * @return Discord markdown formatted String
     * @deprecated Use {@link #serialize(MutableText, DiscordSerializerOptions)} {@link DiscordSerializerOptions#withEmbedLinks(boolean)}
     */
    @Deprecated
    public String serialize(@NotNull final MutableText component, boolean embedLinks) {
        return serialize(component, defaultOptions.withEmbedLinks(embedLinks));
    }

    /**
     * Serializes MutableText (from a chat message) to Discord formatting (markdown).
     *
     * @param component         The text component from a Minecraft chat message
     * @param serializerOptions The options to use for this serialization
     * @return Discord markdown formatted String
     * @see DiscordSerializerOptions#defaults()
     * @see DiscordSerializerOptions#DiscordSerializerOptions(boolean, boolean, Function, Function)
     */
    public String serialize(@NotNull final MutableText component, @NotNull final DiscordSerializerOptions serializerOptions) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Text> texts = getTexts(new LinkedList<>(), component, new Text(), serializerOptions);
        for (Text text : texts) {
            String content = text.getContent();
            if (content.isEmpty()) {
                // won't work
                continue;
            }

            if (text.isBold()) {
                stringBuilder.append("**");
            }
            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }
            if (text.isItalic()) {
                stringBuilder.append("_");
            }
            if (text.isUnderline()) {
                stringBuilder.append("__");
            }

            if (serializerOptions.isEscapeMarkdown()) {
                content = content.replace("(?<!\\\\)(?:\\\\\\\\)*\\*", "\\*")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*~", "\\~")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*_", "\\_")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*`", "\\`")
                        .replace("(?<!\\\\)(?:\\\\\\\\)*\\|", "\\|");
            }

            stringBuilder.append(content);

            if (text.isUnderline()) {
                stringBuilder.append("__");
            }
            if (text.isItalic()) {
                stringBuilder.append("_");
            }
            if (text.isStrikethrough()) {
                stringBuilder.append("~~");
            }
            if (text.isBold()) {
                stringBuilder.append("**");
            }

            stringBuilder.append("\u200B"); // zero width space
        }
        int length = stringBuilder.length();
        return length < 1 ? "" : stringBuilder.substring(0, length - 1);
    }

    private LinkedList<Text> getTexts(@NotNull final List<Text> input, @NotNull final net.minecraft.text.Text component,
                                      @NotNull final Text text, @NotNull final DiscordSerializerOptions serializerOptions) {
        LinkedList<Text> output = new LinkedList<>(input);

        String content;

        // TODO maybe fix?
        if (component instanceof KeybindText keybindText) {
            content = keybindProvider.apply(keybindText);
        } else if (component instanceof ScoreText scoreText) {
            content = scoreText.getObjective();
        } else if (component instanceof SelectorText selectorText) {
            content = selectorText.getPattern();
        } else if (component instanceof LiteralText) {
            content = component.asString();
        } else if (component instanceof TranslatableText translatableText) {
            content = translationProvider.apply(translatableText);
        } else {
            content = "";
        }


        ClickEvent clickEvent = component.getStyle().getClickEvent();
        if (serializerOptions.isEmbedLinks() && clickEvent != null && clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            text.setContent("[" + content + "](" + clickEvent.getValue() + ")");
        } else {
            text.setContent(content);
        }

        if (component.getStyle().isBold()) {
            text.setBold(true);
        }
        text.setBold(component.getStyle().isBold());
        text.setItalic(component.getStyle().isItalic());
        text.setUnderline(component.getStyle().isUnderlined());
        text.setStrikethrough(component.getStyle().isStrikethrough());

        if (!output.isEmpty()) {
            Text previous = output.getLast();
            // if the formatting matches (color was different), merge the text objects to reduce length
            if (text.formattingMatches(previous)) {
                output.removeLast();
                text.setContent(previous.getContent() + text.getContent());
            }
        }
        output.add(text);

        for (net.minecraft.text.Text child : component.getSiblings()) {
            Text next = text.clone();
            next.setContent("");
            output = getTexts(output, child, next, serializerOptions);
        }

        return output;
    }

    public DiscordSerializerOptions getDefaultOptions() {
        return this.defaultOptions;
    }

    public void setDefaultOptions(DiscordSerializerOptions defaultOptions) {
        this.defaultOptions = defaultOptions;
    }
}

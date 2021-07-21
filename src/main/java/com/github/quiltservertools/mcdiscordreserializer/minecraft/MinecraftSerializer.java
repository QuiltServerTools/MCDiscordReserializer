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

package com.github.quiltservertools.mcdiscordreserializer.minecraft;

import com.github.quiltservertools.mcdiscordreserializer.renderer.MinecraftRenderer;
import com.github.quiltservertools.mcdiscordreserializer.renderer.NodeRenderer;
import com.github.quiltservertools.mcdiscordreserializer.renderer.implementation.DefaultDiscordEscapingRenderer;
import com.github.quiltservertools.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.node.TextNode;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * MinecraftSerializer, for serializing from Discord messages to Minecraft {@link MutableText}s.
 *
 * @author Vankka
 *
 * @see MinecraftSerializerOptions
 * @see MinecraftRenderer
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class MinecraftSerializer {

    /**
     * Default instance of the MinecraftSerializer, incase that's all you need.
     * Using {@link MinecraftSerializer#setDefaultOptions(MinecraftSerializerOptions)} and
     * {@link MinecraftSerializer#setMarkdownDefaultOptions(MinecraftSerializerOptions)} are not allowed.
     */
    public static final MinecraftSerializer INSTANCE = new MinecraftSerializer() {

        @Override
        public void setDefaultOptions(MinecraftSerializerOptions<MutableText> defaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }

        @Override
        public void setMarkdownDefaultOptions(MinecraftSerializerOptions<String> markdownDefaultOptions) {
            throw new UnsupportedOperationException("Cannot modify public instance");
        }
    };

    /**
     * The default {@link MinecraftSerializerOptions}
     * to use for this serializer.
     * @see #serialize(String)
     */
    private MinecraftSerializerOptions<MutableText> defaultOptions;

    /**
     * The default {@link MinecraftSerializerOptions}
     * to use for escaping markdown.
     * @see #escapeMarkdown(String, MinecraftSerializerOptions)
     */
    private MinecraftSerializerOptions<String> markdownDefaultOptions;

    /**
     * Constructor for creating a serializer, with {@link MinecraftSerializerOptions#defaults()}
     * and {@link MinecraftSerializerOptions#escapeDefaults()} as defaults.
     */
    public MinecraftSerializer() {
        this(MinecraftSerializerOptions.defaults(), MinecraftSerializerOptions.escapeDefaults());
    }

    /**
     * Constructor for creating a serializer, with the specified {@link MinecraftSerializerOptions} as defaults.
     *
     * @param defaultOptions the default serializer options (can be overridden on serialize)
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(dev.vankka.simpleast.core.parser.Parser, List, List, boolean)
     */
    public MinecraftSerializer(@NotNull MinecraftSerializerOptions<MutableText> defaultOptions,
                               @NotNull MinecraftSerializerOptions<String> markdownDefaultOptions) {
        this.defaultOptions = defaultOptions;
        this.markdownDefaultOptions = markdownDefaultOptions;
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link MutableText} using this serializer's
     * {@link MinecraftSerializer#getDefaultOptions() default options}.<br/>
     * Use {@link MinecraftSerializer#serialize(String, MinecraftSerializerOptions)} to fine tune the serialization options.
     *
     * @param discordMessage a Discord markdown message
     * @return the Discord message formatted to a Minecraft TextComponent
     */
    public MutableText serialize(@NotNull final String discordMessage) {
        MinecraftSerializerOptions<MutableText> options = getDefaultOptions();
        return serialize(discordMessage, options);
    }

    /**
     * Serializes Discord formatting (markdown) to a Minecraft {@link MutableText}.
     *
     * @param discordMessage    a Discord markdown message
     * @param serializerOptions The options to use for this serialization
     * @return the Discord message formatted to a Minecraft TextComponent
     * @see MinecraftSerializerOptions#defaults()
     * @see MinecraftSerializerOptions#MinecraftSerializerOptions(dev.vankka.simpleast.core.parser.Parser, List, List, boolean)
     */
    public MutableText serialize(@NotNull final String discordMessage, @NotNull final MinecraftSerializerOptions<MutableText> serializerOptions) {
        List<MutableText> components = new ArrayList<>();

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        nodes = flattenTextNodes(nodes); // reduce the amount of single character nodes caused by special characters
        for (Node<Object> node : nodes) {
            components.add(addChild(node, new LiteralText(""), serializerOptions));
        }

        var text = new LiteralText("");
        components.forEach(text::append);
        return text;
    }

    /**
     * Escapes the given Discord message of Discord markdown. Should include the entire message (not just a part) to be effective.
     *
     * @param discordMessage    the Discord message
     * @return the Discord markdown message with markdown escaped
     * @see MinecraftSerializerOptions#escapeDefaults()
     * @see MinecraftSerializer#escapeMarkdown(String, MinecraftSerializerOptions)
     */
    public String escapeMarkdown(@NotNull final String discordMessage) {
        return escapeMarkdown(discordMessage, getMarkdownDefaultOptions());
    }

    /**
     * Escapes the given Discord message of Discord markdown. Should include the entire message (not just a part) to be effective.
     *
     * @param discordMessage    the Discord message
     * @param serializerOptions options for this escape
     * @return the Discord markdown message with markdown escaped
     * @see MinecraftSerializerOptions#escapeDefaults()
     * @see MinecraftSerializer#escapeMarkdown(String)
     */
    public String escapeMarkdown(@NotNull final String discordMessage, @NotNull final MinecraftSerializerOptions<String> serializerOptions) {
        String output = "";

        List<Node<Object>> nodes = serializerOptions.getParser().parse(discordMessage, null, serializerOptions.getRules(), serializerOptions.isDebuggingEnabled());
        nodes = flattenTextNodes(nodes); // reduce the amount of single character nodes caused by special characters
        for (Node<Object> node : nodes) {
            output = addChild(node, output, serializerOptions);
        }

        return output;
    }

    private MutableText addChild(final Node<Object> node, final MutableText styleNode,
                               final MinecraftSerializerOptions<MutableText> serializerOptions) {
        MutableText component = new LiteralText("").setStyle(styleNode.getStyle());
        Function<Node<Object>, MutableText> renderWithChildren = otherNode -> addChild(otherNode, component, serializerOptions);

        MutableText output = null;
        NodeRenderer<MutableText> render = null;
        for (NodeRenderer<MutableText> renderer : serializerOptions.getRenderers()) {
            output = renderer.render(component, node, serializerOptions, renderWithChildren);
            if (output != null) {
                render = renderer;
                break;
            }
        }
        if (output == null) {
            render = DefaultMinecraftRenderer.INSTANCE;
            output = render.render(component, node, serializerOptions, renderWithChildren);
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                output = output.append(addChild(child, output, serializerOptions));
            }
        }

        MutableText newOutput = render.renderAfterChildren(output, node, serializerOptions, renderWithChildren);
        if (newOutput != null) {
            output = newOutput;
        }

        return output;
    }

    private String addChild(final Node<Object> node, final String input,
                          final MinecraftSerializerOptions<String> serializerOptions) {
        Function<Node<Object>, String> renderWithChildren = otherNode -> addChild(otherNode, input, serializerOptions);

        String output = null;
        NodeRenderer<String> render = null;
        for (NodeRenderer<String> renderer : serializerOptions.getRenderers()) {
            output = renderer.render(output, node, serializerOptions, renderWithChildren);
            if (output != null) {
                render = renderer;
                break;
            }
        }
        if (output == null) {
            render = DefaultDiscordEscapingRenderer.INSTANCE;
            output = render.render(input, node, serializerOptions, renderWithChildren);
        }

        Collection<Node<Object>> children = node.getChildren();
        if (children != null) {
            for (Node<Object> child : children) {
                output = addChild(child, output, serializerOptions);
            }
        }

        String newOutput = render.renderAfterChildren(output, node, serializerOptions, renderWithChildren);
        if (newOutput != null) {
            output = newOutput;
        }

        return output;
    }

    @SuppressWarnings("unchecked")
    private <R, T extends Node<R>> List<T> flattenTextNodes(List<T> nodes) {
        List<T> newNodes = new ArrayList<>();
        TextNode<T> previousNode = null;
        for (T node : nodes) {
            List<Node<R>> children = node.getChildren();
            if (!children.isEmpty()) {
                if (previousNode != null) {
                    newNodes.add((T) previousNode);
                    previousNode = null;
                }

                List<T> childNodes = flattenTextNodes((List<T>) children);
                node.getChildren().clear();
                node.getChildren().addAll(childNodes);
                newNodes.add(node);
                continue;
            }
            if (!(node instanceof TextNode)) {
                if (previousNode != null) {
                    newNodes.add((T) previousNode);
                    previousNode = null;
                }
                newNodes.add(node);
                continue;
            }

            if (previousNode == null) {
                previousNode = (TextNode<T>) node;
            } else {
                previousNode = new TextNode<>(previousNode.getContent() + ((TextNode<?>) node).getContent());
            }
        }
        if (previousNode != null) {
            newNodes.add((T) previousNode);
        }
        return newNodes;
    }

    public MinecraftSerializerOptions<MutableText> getDefaultOptions() {
        return this.defaultOptions;
    }

    public MinecraftSerializerOptions<String> getMarkdownDefaultOptions() {
        return this.markdownDefaultOptions;
    }

    public void setDefaultOptions(MinecraftSerializerOptions<MutableText> defaultOptions) {
        this.defaultOptions = defaultOptions;
    }

    public void setMarkdownDefaultOptions(MinecraftSerializerOptions<String> markdownDefaultOptions) {
        this.markdownDefaultOptions = markdownDefaultOptions;
    }
}

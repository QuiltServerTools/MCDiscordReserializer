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

import com.github.quiltservertools.mcdiscordreserializer.renderer.NodeRenderer;
import com.github.quiltservertools.mcdiscordreserializer.renderer.implementation.DefaultMinecraftRenderer;
import com.github.quiltservertools.mcdiscordreserializer.rules.DiscordMarkdownRules;
import dev.vankka.simpleast.core.node.Node;
import dev.vankka.simpleast.core.parser.Parser;
import dev.vankka.simpleast.core.parser.Rule;
import dev.vankka.simpleast.core.simple.SimpleMarkdownRules;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Options for {@link MinecraftSerializer}s.
 *
 * @param <O> the type of the result produced with the renderers
 */
public class MinecraftSerializerOptions<O> {

    public MinecraftSerializerOptions(@NotNull Parser<Object, Node<Object>, Object> parser, List<Rule<Object, Node<Object>, Object>> rules, @NotNull List<NodeRenderer<O>> renderers, boolean debuggingEnabled) {
        this.parser = parser;
        this.rules = rules;
        this.renderers = renderers;
        this.debuggingEnabled = debuggingEnabled;
    }

    /**
     * Creates the default {@link MinecraftSerializerOptions} for serialization.
     *
     * @return the default {@link MinecraftSerializerOptions}.
     */
    public static MinecraftSerializerOptions<MutableText> defaults() {
        return new MinecraftSerializerOptions<>(new Parser<>(),
                DiscordMarkdownRules.createAllRulesForDiscord(true),
                Collections.emptyList(),
                false);
    }

    /**
     * Creates the default {@link MinecraftSerializerOptions} for escaping markdown.
     *
     * @return the default {@link MinecraftSerializerOptions}.
     */
    public static MinecraftSerializerOptions<String> escapeDefaults() {
        List<Rule<Object, Node<Object>, Object>> rules = new ArrayList<>();
        rules.addAll(SimpleMarkdownRules.createSimpleMarkdownRules(false));
        rules.addAll(DiscordMarkdownRules.createStyleRules());
        rules.add(SimpleMarkdownRules.createTextRule());

        return new MinecraftSerializerOptions<>(new Parser<>(),
                rules,
                Collections.emptyList(),
                false);
    }

    /**
     * Creates a instance of {@link MinecraftSerializerOptions}
     * with the given renderer added.
     *
     * @param renderer the renderer to add
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance
     *                                            or is of type {@link DefaultMinecraftRenderer}
     * @see List#add(Object)
     */
    public MinecraftSerializerOptions<O> addRenderer(NodeRenderer<O> renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer.getClass().equals(DefaultMinecraftRenderer.class)) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.add(renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a instance of {@link MinecraftSerializerOptions}
     * with the given renderer added at the given index, keep in mind a default renderer is always present.
     *
     * @param renderer the renderer to add
     * @param index    the index to add the renderer at
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is already included in this options instance
     *                                            or is of type {@link DefaultMinecraftRenderer}
     * @see List#add(int, Object)
     */
    public MinecraftSerializerOptions<O> addRenderer(int index, NodeRenderer<O> renderer) {
        if (renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is already included in this options instance");
        }
        if (renderer.getClass().equals(DefaultMinecraftRenderer.class)) {
            throw new IllegalArgumentException("DefaultMinecraftRenderer cannot be added to serializer options");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.add(index, renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Creates a instance of {@link MinecraftSerializerOptions}
     * without the given renderer.
     *
     * @param renderer the renderer to remove
     * @return the new instance of options
     * @throws java.lang.IllegalArgumentException if the renderer is not included in this options instance
     */
    public MinecraftSerializerOptions<O> removeRenderer(NodeRenderer<O> renderer) {
        if (!renderers.contains(renderer)) {
            throw new IllegalArgumentException("The provided renderer is not included in this options instance");
        }
        List<NodeRenderer<O>> renderers = new ArrayList<>(this.renderers);
        renderers.remove(renderer);
        return new MinecraftSerializerOptions<>(parser, rules, renderers, debuggingEnabled);
    }

    /**
     * Returns the renderers for this options instance.
     *
     * @return the ordered unmodifiable list of
     */
    public List<NodeRenderer<O>> getRenderers() {
        return Collections.unmodifiableList(renderers);
    }

    /**
     * The SimpleAST {@link Parser} to use to generate the abstract syntax tree.
     */
    @NotNull
    private final Parser<Object, Node<Object>, Object> parser;

    /**
     * The {@link dev.vankka.simpleast.core.parser.Rule Rules} for the {@link Parser},
     * {@code null} to use the {@link Parser Parsers} default rules.
     */
    private final List<Rule<Object, Node<Object>, Object>> rules;

    /**
     * The {@link NodeRenderer}s to use to render formatting for Minecraft.
     */
    @NotNull
    private final List<NodeRenderer<O>> renderers;

    /**
     * Weather or not to use debug logging for the {@link Parser}.
     */
    private final boolean debuggingEnabled;

    public String toString() {
        return "MinecraftSerializerOptions(parser=" + this.parser + ", rules=" + this.rules + ", renderers=" + this.getRenderers() + ", debuggingEnabled=" + this.debuggingEnabled + ")";
    }

    public @NotNull Parser<Object, Node<Object>, Object> getParser() {
        return this.parser;
    }

    public List<Rule<Object, Node<Object>, Object>> getRules() {
        return this.rules;
    }

    public boolean isDebuggingEnabled() {
        return this.debuggingEnabled;
    }

    public MinecraftSerializerOptions<O> withParser(@NotNull Parser<Object, Node<Object>, Object> parser) {
        return this.parser == parser ? this : new MinecraftSerializerOptions<O>(parser, this.rules, this.renderers, this.debuggingEnabled);
    }

    public MinecraftSerializerOptions<O> withRules(List<Rule<Object, Node<Object>, Object>> rules) {
        return this.rules == rules ? this : new MinecraftSerializerOptions<O>(this.parser, rules, this.renderers, this.debuggingEnabled);
    }

    public MinecraftSerializerOptions<O> withDebuggingEnabled(boolean debuggingEnabled) {
        return this.debuggingEnabled == debuggingEnabled ? this : new MinecraftSerializerOptions<O>(this.parser, this.rules, this.renderers, debuggingEnabled);
    }
}

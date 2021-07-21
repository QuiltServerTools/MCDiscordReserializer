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
import dev.vankka.simpleast.core.node.Node;
import net.minecraft.text.MutableText;

import java.util.function.Function;

/**
 * Interface for rendering {@link dev.vankka.simpleast.core.node.Node}s into Minecraft {@link net.minecraft.text.MutableText}s.
 */
public interface MinecraftNodeRenderer extends NodeRenderer<MutableText> {

    /**
     * Renders the given {@link dev.vankka.simpleast.core.node.Node} onto the provided
     * {@link MutableText} using the given
     * {@link MinecraftSerializerOptions}.
     *
     * @param baseComponent      the input component to apply the node to
     * @param node               the node
     * @param serializerOptions  the serializer options for this render
     * @param renderWithChildren a function to allow rendering a node recursively
     * @return the new component with the node applied to it
     */
    MutableText render(MutableText baseComponent, Node<Object> node, MinecraftSerializerOptions<MutableText> serializerOptions,
                       Function<Node<Object>, MutableText> renderWithChildren);
}

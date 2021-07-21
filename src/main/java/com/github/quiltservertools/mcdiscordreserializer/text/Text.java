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

package com.github.quiltservertools.mcdiscordreserializer.text;

/**
 * Text class, for defining segments of text with formatting rules.
 */
public class Text {
    private String content;
    private boolean bold;
    private boolean strikethrough;
    private boolean underline;
    private boolean italic;

    private Text(String content, boolean bold, boolean strikethrough, boolean underline, boolean italic) {
        this.content = content;
        this.bold = bold;
        this.strikethrough = strikethrough;
        this.underline = underline;
        this.italic = italic;
    }

    public Text() {
    }

    /**
     * Checks if the formatting matches between this and another Text object.
     *
     * @param other The other Text object.
     * @return true if the formatting matches the other Text object.
     */
    public boolean formattingMatches(Text other) {
        return other != null
                && bold == other.bold
                && strikethrough == other.strikethrough
                && underline == other.underline
                && italic == other.italic;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Text clone() {
        return new Text(content, bold, strikethrough, underline, italic);
    }

    public String getContent() {
        return this.content;
    }

    public boolean isBold() {
        return this.bold;
    }

    public boolean isStrikethrough() {
        return this.strikethrough;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isItalic() {
        return this.italic;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public String toString() {
        return "Text(content=" + this.getContent() + ", bold=" + this.isBold() + ", strikethrough=" + this.isStrikethrough() + ", underline=" + this.isUnderline() + ", italic=" + this.isItalic() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Text)) return false;
        final Text other = (Text) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (this$content == null ? other$content != null : !this$content.equals(other$content)) return false;
        if (this.isBold() != other.isBold()) return false;
        if (this.isStrikethrough() != other.isStrikethrough()) return false;
        if (this.isUnderline() != other.isUnderline()) return false;
        if (this.isItalic() != other.isItalic()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Text;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        result = result * PRIME + (this.isBold() ? 79 : 97);
        result = result * PRIME + (this.isStrikethrough() ? 79 : 97);
        result = result * PRIME + (this.isUnderline() ? 79 : 97);
        result = result * PRIME + (this.isItalic() ? 79 : 97);
        return result;
    }
}

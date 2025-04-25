package pdfviewer.models;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.TextPosition;

import java.awt.*;
import java.util.List;

public class WordInfo {
    private final String word;
    private final List<TextPosition> positions;
    private String info;
    private Rectangle boundingBox;

    public WordInfo(String word, List<TextPosition> positions) {
        this.word = word;
        this.positions = positions;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getWord() {
        return word;
    }

    public List<TextPosition> getPositions() {
        return positions;
    }

    public float getPosition() {
        return positions.get(0).getY();
    }

    public PDFont getPDFont() {
        return positions.get(0).getFont();
    }

    public String getJustFont() {
        return getPDFont().getName();
    }

    public String getFontName() {
        String font = getJustFont();
        if (font == null) {
            return null;
        }
        if (font.contains("+")) {
            font = font.substring(font.indexOf("+") + 1);
        }
        if (font.contains("-")) {
            font = font.replace(font.substring(font.lastIndexOf("-")), "");
        } else if (font.contains(",")) {
            font = font.replace(font.substring(font.lastIndexOf(",")), "");
        }
        return font.replace("mt", "").replace("MT", "");
    }

    public String getFontStyle() {
        String font = getJustFont();
        if (font == null) {
            return "unknown";
        }
        font = font.toLowerCase().replace("mt", "");
        if (font.contains("-")) {
            return font.substring(font.lastIndexOf("-") + 1);
        } else if (font.contains(",")) {
            return font.substring(font.lastIndexOf(",") + 1);
        }

        return "regular";
    }

    public int getFontSize() {
        return Math.round(positions.get(0).getFontSize());
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rectangle boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getDetails() {
        return word + "\n" +
                "Font: " + getFontName() + "\n" +
                "Size: " + getFontSize() + "\n" +
                "Style: " + getFontStyle();
    }


    @Override
    public String toString() {
        return "WordInfo{" +
                "word='" + word + '\'' +
                ", font='" + getFontName() + '\'' +
                ", style='" + getFontStyle() + '\'' +
                ", size=" + getFontSize() +
                '}';
    }
}

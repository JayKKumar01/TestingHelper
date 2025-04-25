package pdfviewer.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import pdfviewer.models.WordInfo;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfWordExtractor {
    private static final float DPI = 200f;
    private static final float POINTS_PER_INCH = 72f;
    private static final float SCALE = DPI / POINTS_PER_INCH;
    private static final float PADDING = 1f;

    public static List<WordInfo> extractWords(PDDocument document, int pageNum) throws IOException {
        List<WordInfo> wordInfoList = new ArrayList<>();
        if (document == null || pageNum < 1){
            return wordInfoList;
        }

        PDFTextStripper stripper = new PDFTextStripper() {
            @Override
            protected void writeString(String string, List<TextPosition> textPositions) {
                String[] words = string.split(getWordSeparator());
                int i = 0;
                for (String word : words) {
                    if (!word.isEmpty() && i < textPositions.size()) {
                        List<TextPosition> positions = new ArrayList<>();
                        int len = i + word.length();
                        for (int j = i; j < len && j < textPositions.size(); j++) {
                            positions.add(textPositions.get(j));
                        }
                        System.out.println(word + ": " +positions.size());
                        WordInfo wordInfo = new WordInfo(word, positions);

                        Rectangle bounds = calculateBoundingBox(positions);
                        wordInfo.setBoundingBox(bounds);

                        wordInfoList.add(wordInfo);
                    }
                    i += word.length() + 1;
                }
            }

            private Rectangle calculateBoundingBox(List<TextPosition> positions) {
                TextPosition first = positions.get(0);
                TextPosition last = positions.get(positions.size()-1);

                // Calculate position and size with padding
                float x = first.getX() * SCALE - PADDING; // Apply padding on left
                float y = first.getY() * SCALE - PADDING; // Apply padding on top
                float width = (last.getX() + last.getWidth()) * SCALE - x; // Apply padding on both sides
                float height = first.getHeight() * SCALE; // Apply padding on top and bottom

                return new Rectangle(Math.round(x), Math.round(y - height), Math.round(width + (PADDING)), Math.round(height + (2 * PADDING)));
            }

        };

        stripper.setStartPage(pageNum);
        stripper.setEndPage(pageNum);
        stripper.getText(document);  // trigger extraction

        return wordInfoList;
    }
}

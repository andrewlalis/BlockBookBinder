package nl.andrewlalis.blockbookbinder.model.build;

import nl.andrewlalis.blockbookbinder.model.Book;
import nl.andrewlalis.blockbookbinder.model.BookPage;
import nl.andrewlalis.blockbookbinder.model.CharWidthMapper;

import java.util.ArrayList;
import java.util.List;

public class BookBuilder2 {
    private final int MAX_LINES_PER_PAGE;
    private final int MAX_CHARS_PER_PAGE;
    private final int MAX_LINE_PIXEL_WIDTH;

    private final List<String> lines;

    private final StringBuilder lineBuilder;
    private final StringBuilder wordBuilder;

    public BookBuilder2(int maxLinesPerPage, int maxCharsPerPage, int maxLinePixelWidth) {
        this.MAX_LINES_PER_PAGE = maxLinesPerPage;
        this.MAX_CHARS_PER_PAGE = maxCharsPerPage;
        this.MAX_LINE_PIXEL_WIDTH = maxLinePixelWidth;
        this.lines = new ArrayList<>();
        this.lineBuilder = new StringBuilder(64);
        this.wordBuilder = new StringBuilder(64);
    }

    public BookBuilder2 addText(String text) {
        int idx = 0;
        while (idx < text.length()) {
            final char c = text.charAt(idx++);
            if (c == '\n') {
                appendLine();
            } else if (c == ' ' && lineBuilder.length() == 0) {
                continue; // Skip spaces at the start of lines.
            } else if (Character.isWhitespace(c)) {
                if (CharWidthMapper.getWidth(lineBuilder.toString() + c) > MAX_LINE_PIXEL_WIDTH) {
                    appendLine();
                    if (c != ' ') {
                        lineBuilder.append(c);
                    }
                } else {
                    lineBuilder.append(c);
                }
            } else { // Read a continuous word.
                String word = readWord(text, idx - 1);
                idx += word.length() - 1;
                if (CharWidthMapper.getWidth(lineBuilder + word) <= MAX_LINE_PIXEL_WIDTH) {
                    // Append the word if it'll fit completely.
                    lineBuilder.append(word);
                } else if (CharWidthMapper.getWidth(word) <= MAX_LINE_PIXEL_WIDTH) {
                    // Go to the next line and put the word there, since it'll fit.
                    appendLine();
                    lineBuilder.append(word);
                } else {
                    // The word is so large that it doesn't fit on a line on its own.
                    // Find the largest substring of the word that'll fit with a hyphen.
                    int subStringSize = word.length() - 2;
                    while (CharWidthMapper.getWidth(word.substring(0, subStringSize) + "-") > MAX_LINE_PIXEL_WIDTH) {
                        subStringSize--;
                    }
                    appendLine();
                    lineBuilder.append(word, 0, subStringSize).append('-');
                    appendLine();
                    lineBuilder.append(word.substring(subStringSize));
                }
            }
        }
        return this;
    }

    public Book build() {
        Book book = new Book();
        BookPage page = new BookPage();
        int currentPageLineCount = 0;
        int currentPageCharCount = 0;

        // Flush anything remaining in lineBuilder to a final line.
        if (lineBuilder.length() > 0) {
            appendLine();
        }

        for (String line : lines) {
            if (currentPageCharCount + line.length() > MAX_CHARS_PER_PAGE) {
                book.addPage(page);
                page = new BookPage();
                currentPageLineCount = 0;
                currentPageCharCount = 0;
            }
            page.addLine(line);
            currentPageLineCount++;
            currentPageCharCount += line.length();
            if (currentPageLineCount == MAX_LINES_PER_PAGE) {
                book.addPage(page);
                page = new BookPage();
                currentPageLineCount = 0;
                currentPageCharCount = 0;
            }
        }
        if (page.hasContent()) {
            book.addPage(page);
        }
        return book;
    }

    private String readWord(String text, int firstCharIdx) {
        wordBuilder.setLength(0);
        int idx = firstCharIdx;
        while (idx < text.length()) {
            char c = text.charAt(idx++);
            if (!Character.isWhitespace(c)) {
                wordBuilder.append(c);
            } else {
                break;
            }
        }
        return wordBuilder.toString();
    }

    private void appendLine() {
        this.lines.add(this.lineBuilder.toString());
        this.lineBuilder.setLength(0);
    }
}

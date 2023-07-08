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

    private List<String> lines;

    private StringBuilder lineBuilder;
    private StringBuilder wordBuilder;
    private int currentLine;
    private int currentLinePixelWidth;
    private int currentWordPixelWidth;

    public BookBuilder2(int maxLinesPerPage, int maxCharsPerPage, int maxLinePixelWidth) {
        this.MAX_LINES_PER_PAGE = maxLinesPerPage;
        this.MAX_CHARS_PER_PAGE = maxCharsPerPage;
        this.MAX_LINE_PIXEL_WIDTH = maxLinePixelWidth;
        this.lines = new ArrayList<>();
        this.lineBuilder = new StringBuilder(64);
        this.wordBuilder = new StringBuilder(64);
        this.currentLine = 0;
        this.currentLinePixelWidth = 0;
        this.currentWordPixelWidth = 0;
    }

    public BookBuilder2 addText(String text) {
        int idx = 0;
        while (idx < text.length()) {
            final char c = text.charAt(idx++);
            if (c == '\n') {
                appendLine();
            } else if (c == ' ' && lineBuilder.length() == 0) {
                continue; // Skip spaces at the start of lines.
            } else { // Read a continuous word.
                int charsRead = readWord(text, idx - 1);
                idx += charsRead - 1;
            }
        }
        return this;
    }

    public Book build() {
        Book book = new Book();
        BookPage page = new BookPage();
        int currentPageLineCount = 0;

        for (String line : lines) {
            page.addLine(line);
            currentPageLineCount++;
            if (currentPageLineCount == MAX_LINES_PER_PAGE) {
                book.addPage(page);
                page = new BookPage();
                currentPageLineCount = 0;
            }
        }
        if (page.hasContent()) {
            book.addPage(page);
        }
        return book;
    }

    private int readWord(String text, int firstCharIdx) {
        currentWordPixelWidth = 0;
        wordBuilder.setLength(0);
        int idx = firstCharIdx;
        while (idx < text.length()) {
            char c = text.charAt(idx++);
            if (!Character.isWhitespace(c)) {
                currentWordPixelWidth += CharWidthMapper.getInstance().getWidth(c) + 1;
                wordBuilder.append(c);

                // If we notice that our word will cause the current line to exceed max width, go to a newline.
                if (currentLinePixelWidth + currentWordPixelWidth > MAX_LINE_PIXEL_WIDTH) {
                    appendLine();
                }
            } else {
                break;
            }
        }
        String word = wordBuilder.toString();
        return word.length();
    }

    private void appendLine() {
        this.lines.add(this.lineBuilder.toString());
        this.lineBuilder.setLength(0);
        this.currentLine++;
        this.currentLinePixelWidth = 0;
    }
}

package nl.andrewlalis.blockbookbinder.model.build;

import nl.andrewlalis.blockbookbinder.model.Book;
import nl.andrewlalis.blockbookbinder.model.BookPage;
import nl.andrewlalis.blockbookbinder.model.CharWidthMapper;
import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;

import java.util.ArrayList;
import java.util.List;

public class BookBuilder {
	/**
	 * Builds a full book of pages from the given source text.
	 * @param source The source text to convert.
	 * @return A book containing the source text formatted for a minecraft book.
	 */
	public Book build(String source) {
		final int maxLines = ApplicationProperties.getIntProp("book.page_max_lines");
		List<String> lines = this.convertSourceToLines(source);
		Book book = new Book();
		BookPage page = new BookPage();
		int currentPageLineCount = 0;

		for (String line : lines) {
			page.addLine(line);
			currentPageLineCount++;
			if (currentPageLineCount == maxLines) {
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

	/**
	 * Converts the given source string into a formatted list of lines that can
	 * be copied to a minecraft book.
	 * @param source The source string.
	 * @return A list of lines.
	 */
	private List<String> convertSourceToLines(String source) {
		List<String> lines = new ArrayList<>();
		final char[] sourceChars = source.toCharArray();
		final int maxLinePixelWidth = ApplicationProperties.getIntProp("book.page_max_width");
		int sourceIndex = 0;
		StringBuilder lineBuilder = new StringBuilder(64);
		int linePixelWidth = 0;
		StringBuilder symbolBuilder = new StringBuilder(64);

		while (sourceIndex < sourceChars.length) {
			final char c = sourceChars[sourceIndex];
			sourceIndex++;
			symbolBuilder.setLength(0);
			symbolBuilder.append(c);
			int symbolWidth = CharWidthMapper.getWidth(c);

			// Since there's a 1-pixel gap between characters, add it to the width if this isn't the first char.
			if (lineBuilder.length() > 0) {
				symbolWidth++;
			}

			// If we encounter a non-newline whitespace at the beginning of the line, skip it.
			if (c == ' ' && lineBuilder.length() == 0) {
				continue;
			}

			// If we encounter a newline, immediately skip to a new line.
			if (c == '\n') {
				lines.add(lineBuilder.toString());
				lineBuilder.setLength(0);
				linePixelWidth = 0;
				continue;
			}

			// If we encounter a word, keep accepting characters until we reach the end.
			if (Character.isLetterOrDigit(c)) {
				while (
						sourceIndex < sourceChars.length
						&& Character.isLetterOrDigit(sourceChars[sourceIndex])
				) {
					char nextChar = sourceChars[sourceIndex];
					symbolBuilder.append(nextChar);
					symbolWidth += 1 + CharWidthMapper.getWidth(nextChar);
					sourceIndex++;
				}
			}

			final String symbol = symbolBuilder.toString();
			// Check if we need to go to the next line to fit the symbol.
			if (linePixelWidth + symbolWidth > maxLinePixelWidth) {
				lines.add(lineBuilder.toString());
				lineBuilder.setLength(0);
				linePixelWidth = 0;
			}

			// Finally, append the symbol.
			lineBuilder.append(symbol);
			linePixelWidth += symbolWidth;
		}

		// Append any remaining text.
		if (lineBuilder.length() > 0) {
			lines.add(lineBuilder.toString());
		}

		return lines;
	}
}

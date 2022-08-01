package nl.andrewlalis.blockbookbinder.model;

import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;

import java.util.ArrayList;
import java.util.List;

public class Book {
	private final List<BookPage> pages;

	public Book() {
		this.pages = new ArrayList<>();
	}

	public int getPageCount() {
		return this.pages.size();
	}

	public List<BookPage> getPages() {
		return pages;
	}

	public void addPage(BookPage page) {
		this.pages.add(page);
	}

	/**
	 * Gets a book containing the pages specified by the range.
	 * @param firstIndex The index of the first page to include.
	 * @param count The number of pages to include.
	 * @return The book containing the range of pages.
	 */
	public Book getPageRange(int firstIndex, int count) {
		Book book = new Book();
		for (int i = 0; i < count; i++) {
			book.addPage(this.pages.get(firstIndex + i));
		}
		return book;
	}

	public List<Book> splitByPageLimit() {
		final int pagesPerBook = ApplicationProperties.getIntProp("book.max_pages");
		List<Book> books = new ArrayList<>((this.getPageCount() / pagesPerBook) + 1);
		Book currentBook = new Book();
		for (BookPage page : this.getPages()) {
			currentBook.addPage(page.copy());
			if (currentBook.getPageCount() == pagesPerBook) {
				books.add(currentBook);
				currentBook = new Book();
			}
		}
		return books;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Book of " + this.getPageCount() + " pages:\n");
		for (int i = 0; i < this.getPageCount(); i++) {
			BookPage page = this.pages.get(i);
			sb.append("Page ").append(i + 1).append(":\n").append(page).append('\n');
		}
		return sb.toString();
	}
}

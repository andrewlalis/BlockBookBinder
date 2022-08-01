package nl.andrewlalis.blockbookbinder.view.book;

import nl.andrewlalis.blockbookbinder.model.Book;
import nl.andrewlalis.blockbookbinder.model.BookPage;
import nl.andrewlalis.blockbookbinder.util.IconLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * A customized panel that's dedicated to showing a book's contents.
 */
public class BookPreviewPanel extends JPanel {
	private Book book;
	private int currentPage = 0;

	private final JTextArea previewPageTextArea;
	private final JLabel titleLabel;

	private final JButton previousPageButton;
	private final JButton nextPageButton;
	private final JButton firstPageButton;
	private final JButton lastPageButton;

	private final SpinnerNumberModel currentPageNumberModel;
	private boolean ignoreCurrentPageChange = false;

	public BookPreviewPanel() {
		super(new BorderLayout());

		this.titleLabel = new JLabel("Book Preview");
		this.add(this.titleLabel, BorderLayout.NORTH);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.previewPageTextArea = new JTextArea();
		this.previewPageTextArea.setEditable(false);
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("fonts/1_Minecraft-Regular.otf");
			if (is == null) {
				throw new IOException("Could not read minecraft font.");
			}
			Font mcFont = Font.createFont(Font.TRUETYPE_FONT, is);
			mcFont = mcFont.deriveFont(24.0f);
			this.previewPageTextArea.setFont(mcFont);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		JScrollPane previewPageScrollPane = new JScrollPane(this.previewPageTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(previewPageScrollPane, BorderLayout.CENTER);

		JPanel previewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		currentPageNumberModel = new SpinnerNumberModel(0, 0, 0, 1);
		this.firstPageButton = new JButton();
		this.firstPageButton.setIcon(IconLoader.load("images/page_first.png", 16, 16));
		this.firstPageButton.addActionListener(e -> {
			this.currentPage = 0;
			updateCurrentPageModel();
			displayCurrentPage();
		});

		this.previousPageButton = new JButton();
		this.previousPageButton.setIcon(IconLoader.load("images/page_left.png", 16, 16));
		this.previousPageButton.addActionListener(e -> {
			if (currentPage > 0) {
				currentPage--;
				updateCurrentPageModel();
				displayCurrentPage();
			}
		});

		this.nextPageButton = new JButton();
		this.nextPageButton.setIcon(IconLoader.load("images/page_right.png", 16, 16));
		this.nextPageButton.addActionListener(e -> {
			if (currentPage < book.getPageCount() - 1) {
				currentPage++;
				updateCurrentPageModel();
				displayCurrentPage();
			}
		});
		this.lastPageButton = new JButton();
		this.lastPageButton.setIcon(IconLoader.load("images/page_last.png", 16, 16));
		this.lastPageButton.addActionListener(e -> {
			this.currentPage = Math.max(this.book.getPageCount() - 1, 0);
			updateCurrentPageModel();
			displayCurrentPage();
		});

		JSpinner currentPageSpinner = new JSpinner(currentPageNumberModel);
		currentPageSpinner.addChangeListener(e -> {
			if (!ignoreCurrentPageChange) {
				this.currentPage = (int) currentPageNumberModel.getValue() - 1;
				displayCurrentPage();
			}
		});

		previewButtonPanel.add(this.firstPageButton);
		previewButtonPanel.add(this.previousPageButton);
		previewButtonPanel.add(currentPageSpinner);
		previewButtonPanel.add(this.nextPageButton);
		previewButtonPanel.add(this.lastPageButton);
		this.add(previewButtonPanel, BorderLayout.SOUTH);

		this.setBook(new Book());
	}

	private void displayCurrentPage() {
		if (this.book.getPageCount() == 0) {
			return;
		}
		BookPage currentPage = this.book.getPages().get(this.currentPage);
		this.previewPageTextArea.setText(currentPage.toString());
		this.titleLabel.setText("Book Preview (Page " + (this.currentPage + 1) + " of " + this.book.getPageCount() + ")");
	}

	public void setBook(Book book) {
		this.book = book;
		ignoreCurrentPageChange = true;
		if (book.getPageCount() == 0) {
			currentPageNumberModel.setMinimum(0);
			currentPageNumberModel.setMaximum(0);
			currentPageNumberModel.setValue(0);
		} else {
			currentPageNumberModel.setMinimum(1);
			currentPageNumberModel.setMaximum(book.getPageCount());
			currentPageNumberModel.setValue(1);
		}
		ignoreCurrentPageChange = false;
		this.currentPage = 0;
		this.displayCurrentPage();
	}

	public Book getBook() {
		return book;
	}

	public void updateCurrentPageModel() {
		ignoreCurrentPageChange = true;
		currentPageNumberModel.setValue(currentPage + 1);
		ignoreCurrentPageChange = false;
	}

	public void setCurrentPage(int page) {
		this.currentPage = page;
		this.displayCurrentPage();
	}

	public void enableNavigation(boolean enabled) {
		this.firstPageButton.setEnabled(enabled);
		this.previousPageButton.setEnabled(enabled);
		this.nextPageButton.setEnabled(enabled);
		this.lastPageButton.setEnabled(enabled);
	}
}

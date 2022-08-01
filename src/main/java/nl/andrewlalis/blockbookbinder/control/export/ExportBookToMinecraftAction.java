package nl.andrewlalis.blockbookbinder.control.export;

import nl.andrewlalis.blockbookbinder.model.Book;
import nl.andrewlalis.blockbookbinder.view.book.BookPreviewPanel;
import nl.andrewlalis.blockbookbinder.view.export.ExportToBookDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExportBookToMinecraftAction extends AbstractAction {
	private static final ExportBookToMinecraftAction instance = new ExportBookToMinecraftAction();

	public ExportBookToMinecraftAction() {
		super("Export to Minecraft");
		this.putValue(SHORT_DESCRIPTION, "Export the current book to Minecraft.");
	}

	private BookPreviewPanel bookPreviewPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		final Book book = bookPreviewPanel.getBook();
		if (book == null || book.getPageCount() == 0) {
			JOptionPane.showMessageDialog(
					this.bookPreviewPanel.getRootPane(),
					"Cannot export an empty book.",
					"Empty Book",
					JOptionPane.WARNING_MESSAGE
			);
			return;
		}
		ExportToBookDialog dialog = new ExportToBookDialog(SwingUtilities.getWindowAncestor(this.bookPreviewPanel), bookPreviewPanel.getBook());
		dialog.setupAndShow();
	}

	public void setBookPreviewPanel(BookPreviewPanel bookPreviewPanel) {
		this.bookPreviewPanel = bookPreviewPanel;
	}

	public static ExportBookToMinecraftAction getInstance() {
		return instance;
	}
}

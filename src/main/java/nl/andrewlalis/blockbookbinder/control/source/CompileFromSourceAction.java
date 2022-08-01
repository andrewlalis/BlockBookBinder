package nl.andrewlalis.blockbookbinder.control.source;

import nl.andrewlalis.blockbookbinder.model.build.BookBuilder;
import nl.andrewlalis.blockbookbinder.view.SourceTextPanel;
import nl.andrewlalis.blockbookbinder.view.book.BookPreviewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CompileFromSourceAction extends AbstractAction {
	private static final CompileFromSourceAction instance = new CompileFromSourceAction();

	private SourceTextPanel sourceTextPanel;
	private BookPreviewPanel bookPreviewPanel;

	public CompileFromSourceAction() {
		super("Compile From Source");
		this.putValue(SHORT_DESCRIPTION, "Compile the current source text into a book.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.bookPreviewPanel.setBook(
				new BookBuilder().build(this.sourceTextPanel.getSourceText())
		);
	}

	public static CompileFromSourceAction getInstance() {
		return instance;
	}

	public void setSourceTextPanel(SourceTextPanel sourceTextPanel) {
		this.sourceTextPanel = sourceTextPanel;
	}

	public void setBookPreviewPanel(BookPreviewPanel bookPreviewPanel) {
		this.bookPreviewPanel = bookPreviewPanel;
	}
}

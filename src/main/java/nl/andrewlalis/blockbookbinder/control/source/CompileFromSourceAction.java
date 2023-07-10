package nl.andrewlalis.blockbookbinder.control.source;

import lombok.Getter;
import lombok.Setter;
import nl.andrewlalis.blockbookbinder.model.build.BookBuilder;
import nl.andrewlalis.blockbookbinder.view.SourceTextPanel;
import nl.andrewlalis.blockbookbinder.view.book.BookPreviewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CompileFromSourceAction extends AbstractAction {
	@Getter
	private static final CompileFromSourceAction instance = new CompileFromSourceAction();

	@Setter
	private SourceTextPanel sourceTextPanel;
	@Setter
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
}

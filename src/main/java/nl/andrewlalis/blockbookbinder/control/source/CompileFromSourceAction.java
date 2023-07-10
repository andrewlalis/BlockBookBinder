package nl.andrewlalis.blockbookbinder.control.source;

import lombok.Getter;
import lombok.Setter;
import nl.andrewlalis.blockbookbinder.model.build.BookBuilder;
import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;
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
				new BookBuilder(
						ApplicationProperties.getIntProp("book.page_max_lines"),
						ApplicationProperties.getIntProp("book.page_max_chars"),
						ApplicationProperties.getIntProp("book.page_max_width")
				).addText(this.sourceTextPanel.getSourceText()).build()
		);
	}
}

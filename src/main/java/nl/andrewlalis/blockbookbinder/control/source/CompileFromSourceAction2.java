package nl.andrewlalis.blockbookbinder.control.source;

import lombok.Getter;
import lombok.Setter;
import nl.andrewlalis.blockbookbinder.model.build.BookBuilder2;
import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;
import nl.andrewlalis.blockbookbinder.view.SourceTextPanel;
import nl.andrewlalis.blockbookbinder.view.book.BookPreviewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CompileFromSourceAction2 extends AbstractAction {
	@Getter
	private static final CompileFromSourceAction2 instance = new CompileFromSourceAction2();

	@Setter
	private SourceTextPanel sourceTextPanel;
	@Setter
	private BookPreviewPanel bookPreviewPanel;

	public CompileFromSourceAction2() {
		super("Compile From Source 2");
		this.putValue(SHORT_DESCRIPTION, "Compile the current source text into a book.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.bookPreviewPanel.setBook(
				new BookBuilder2(
						ApplicationProperties.getIntProp("book.page_max_lines"),
						ApplicationProperties.getIntProp("book.page_max_chars"),
						ApplicationProperties.getIntProp("book.page_max_width")
				).addText(this.sourceTextPanel.getSourceText()).build()
		);
	}
}

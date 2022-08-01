package nl.andrewlalis.blockbookbinder.control.source;

import nl.andrewlalis.blockbookbinder.view.SourceTextPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CleanSourceAction extends AbstractAction {
	private final static CleanSourceAction instance = new CleanSourceAction();

	private SourceTextPanel sourceTextPanel;

	public CleanSourceAction() {
		super("Clean Source");
		this.putValue(SHORT_DESCRIPTION, "Clean up the source text.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String source = this.sourceTextPanel.getSourceText();
		String updated = source.trim()
				.replaceAll("(?>\\v)+(\\v)", "\n\n") // Replace large chunks of newline with just two.
				.replaceAll("\\t", " ") // Replace tabs with single-spaces, for space savings.
				.replaceAll("  +", " "); // Remove any double spaces.
		updated = this.removeNewlineWrapping(updated);
		this.sourceTextPanel.setSourceText(updated);
	}

	private String removeNewlineWrapping(String source) {
		final StringBuilder sb = new StringBuilder(source.length());
		final char[] sourceChars = source.toCharArray();
		for (int i = 0; i < sourceChars.length; i++) {
			char c = sourceChars[i];
			if (
					c == '\n'
							&& (i - 1 >= 0 && !Character.isWhitespace(sourceChars[i - 1]))
							&& (i + 1 < sourceChars.length && !Character.isWhitespace(sourceChars[i + 1]))
			) {
				c = ' ';
			}
			sb.append(c);
		}
		return sb.toString();
	}

	public static CleanSourceAction getInstance() {
		return instance;
	}

	public void setSourceTextPanel(SourceTextPanel sourceTextPanel) {
		this.sourceTextPanel = sourceTextPanel;
	}
}

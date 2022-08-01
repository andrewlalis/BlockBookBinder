package nl.andrewlalis.blockbookbinder.control.source;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ImportSourceAction extends AbstractAction {
	private static final ImportSourceAction instance = new ImportSourceAction();

	public ImportSourceAction() {
		super("Import Source");
		this.putValue(SHORT_DESCRIPTION, "Import source text from a file.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	public static ImportSourceAction getInstance() {
		return instance;
	}
}

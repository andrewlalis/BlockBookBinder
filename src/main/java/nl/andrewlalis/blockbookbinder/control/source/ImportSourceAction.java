package nl.andrewlalis.blockbookbinder.control.source;

import lombok.Getter;
import lombok.Setter;
import nl.andrewlalis.blockbookbinder.BlockBookBinder;
import nl.andrewlalis.blockbookbinder.view.SourceTextPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

public class ImportSourceAction extends AbstractAction {
	@Getter
	private static final ImportSourceAction instance = new ImportSourceAction();

	@Setter
	private SourceTextPanel sourceTextPanel;

	public ImportSourceAction() {
		super("Import Source");
		this.putValue(SHORT_DESCRIPTION, "Import source text from a file.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Preferences prefs = Preferences.userNodeForPackage(BlockBookBinder.class);
		String dir = prefs.get("source-import-dir", ".");
		JFileChooser fileChooser = new JFileChooser(dir);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", ".txt"));
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setMultiSelectionEnabled(false);
		final Component parent = SwingUtilities.getWindowAncestor((Component) e.getSource());
		int result = fileChooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Path filePath = file.toPath();
				sourceTextPanel.setSourceText(Files.readString(filePath));
				prefs.put("source-import-dir", filePath.getParent().toAbsolutePath().toString());
			} catch (IOException exc) {
				exc.printStackTrace();
				JOptionPane.showMessageDialog(parent, "Failed to read file:\n" + exc.getMessage(), "Read Failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}

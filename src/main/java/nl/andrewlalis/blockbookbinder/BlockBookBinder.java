package nl.andrewlalis.blockbookbinder;

import com.formdev.flatlaf.FlatDarkLaf;
import nl.andrewlalis.blockbookbinder.util.VersionReader;
import nl.andrewlalis.blockbookbinder.view.MainFrame;

import javax.swing.*;

/**
 * The main class for the application.
 */
public class BlockBookBinder {
	public static final String VERSION = VersionReader.getVersion();

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			FlatDarkLaf.setup();
			var mainFrame = new MainFrame();
			mainFrame.setupAndShow();
		});
	}
}

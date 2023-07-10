package nl.andrewlalis.blockbookbinder.view;

import nl.andrewlalis.blockbookbinder.BlockBookBinder;
import nl.andrewlalis.blockbookbinder.control.export.ExportBookToMinecraftAction;
import nl.andrewlalis.blockbookbinder.control.source.CleanSourceAction;
import nl.andrewlalis.blockbookbinder.control.source.CompileFromSourceAction;
import nl.andrewlalis.blockbookbinder.control.source.ImportSourceAction;
import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;
import nl.andrewlalis.blockbookbinder.view.about.AboutDialog;
import nl.andrewlalis.blockbookbinder.view.book.BookPreviewPanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * The main window of the application.
 */
public class MainFrame extends JFrame {
	public void setupAndShow() {
		this.setPreferredSize(new Dimension(
				ApplicationProperties.getIntProp("frame.default_width"),
				ApplicationProperties.getIntProp("frame.default_height")
		));
		this.setTitle(ApplicationProperties.getProp("frame.title") + " Version " + BlockBookBinder.VERSION);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		final URL iconUrl = this.getClass().getClassLoader().getResource("images/book_and_quill.png");
		if (iconUrl != null) {
			this.setIconImage(new ImageIcon(iconUrl).getImage());
		}

		this.setContentPane(this.buildContentPane());
		this.setJMenuBar(this.buildMenuBar());

		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private Container buildContentPane() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel doublePanel = new JPanel(new GridLayout(1, 2));

		BookPreviewPanel bookPreviewPanel = new BookPreviewPanel();
		doublePanel.add(bookPreviewPanel);
		CompileFromSourceAction.getInstance().setBookPreviewPanel(bookPreviewPanel);
		ExportBookToMinecraftAction.getInstance().setBookPreviewPanel(bookPreviewPanel);

		SourceTextPanel sourceTextPanel = new SourceTextPanel();
		doublePanel.add(sourceTextPanel);
		CompileFromSourceAction.getInstance().setSourceTextPanel(sourceTextPanel);
		CleanSourceAction.getInstance().setSourceTextPanel(sourceTextPanel);
		ImportSourceAction.getInstance().setSourceTextPanel(sourceTextPanel);

		mainPanel.add(doublePanel, BorderLayout.CENTER);

		return mainPanel;
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.add(ImportSourceAction.getInstance());
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(e -> this.dispose());
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		JMenu bookMenu = new JMenu("Book");
		bookMenu.add(CompileFromSourceAction.getInstance());
		bookMenu.add(CleanSourceAction.getInstance());
		bookMenu.add(ExportBookToMinecraftAction.getInstance());
		menuBar.add(bookMenu);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(e -> {
			AboutDialog dialog = new AboutDialog(this);
			dialog.setupAndShow();
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);

		return menuBar;
	}

	@Override
	public void dispose() {
		super.dispose();
		System.exit(0);
	}
}

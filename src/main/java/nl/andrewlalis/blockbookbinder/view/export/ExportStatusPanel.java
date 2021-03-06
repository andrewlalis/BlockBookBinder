package nl.andrewlalis.blockbookbinder.view.export;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * A panel with some components for displaying the current status of an export
 * job.
 */
public class ExportStatusPanel extends JPanel {
	@Getter
	private final JLabel statusLabel;
	@Getter
	private final JTextArea outputTextArea;
	@Getter
	private final JProgressBar exportProgressBar;

	public ExportStatusPanel() {
		this.setLayout(new BorderLayout());

		this.statusLabel = new JLabel("Exporting...");
		this.add(this.statusLabel, BorderLayout.NORTH);

		this.outputTextArea = new JTextArea();
		this.outputTextArea.setEditable(false);
		this.outputTextArea.setLineWrap(true);
		this.outputTextArea.setWrapStyleWord(true);
		this.outputTextArea.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(this.outputTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setAutoscrolls(true);
		this.add(scrollPane, BorderLayout.CENTER);

		this.exportProgressBar = new JProgressBar();
		this.add(this.exportProgressBar, BorderLayout.SOUTH);
	}
}

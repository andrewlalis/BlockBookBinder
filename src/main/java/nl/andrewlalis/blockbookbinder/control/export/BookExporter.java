package nl.andrewlalis.blockbookbinder.control.export;

import lombok.Setter;
import nl.andrewlalis.blockbookbinder.model.Book;
import nl.andrewlalis.blockbookbinder.util.ApplicationProperties;
import nl.andrewlalis.blockbookbinder.view.export.ExportStatusPanel;
import nl.andrewlalis.blockbookbinder.view.export.ExportToBookDialog;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A separate runnable process which handles exporting a book, page by page,
 * into one's clipboard and pasting the pages.
 */
public class BookExporter implements Runnable {
	private final static int START_DELAY = 5;
	private final static int CLIPBOARD_RETRY_DELAY_MS = 100;

	private final Book book;
	private final boolean autoPaste;
	private final int autoPasteDelay;

	@Setter
	private volatile boolean running;

	@Setter
	private volatile boolean nextPageRequested;

	private final ExporterKeyListener exporterKeyListener;
	private final Clipboard clipboard;
	private Robot robot;

	private final ExportStatusPanel statusPanel;
	private final ExportToBookDialog dialog;

	// Some sound clips to play as user feedback.
	private final Clip beepClip;
//	private final Clip beginningExportClip;
//	private final Clip finishClip;

	public BookExporter(ExportToBookDialog dialog, ExportStatusPanel exportStatusPanel, Book book, boolean autoPaste, int autoPasteDelay) {
		this.dialog = dialog;
		this.statusPanel = exportStatusPanel;
		this.book = book;
		this.autoPaste = autoPaste;
		this.autoPasteDelay = autoPasteDelay;
		this.beepClip = this.loadAudioClip(ApplicationProperties.getProp("export_dialog.beep_sound"));
//		this.beginningExportClip = this.loadAudioClip(ApplicationProperties.getProp("export_dialog.beginning_export"));
//		this.finishClip = this.loadAudioClip(ApplicationProperties.getProp("export_dialog.finish_sound"));
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		this.exporterKeyListener = new ExporterKeyListener(this);
		if (this.autoPaste) { // Only initialize the robot if we'll need it.
			try {
				this.robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		this.running = true;
		this.nextPageRequested = true;
		long startTime = System.currentTimeMillis();
		long lastAudioPlayedAt = 0;
		long lastPageExportedAt = 0;
		int nextPageToExport = 0;
		while (this.running) {
			long currentTime = System.currentTimeMillis();
			// Check if we're still in the first few seconds of runtime.
			boolean inStartPhase = (currentTime - startTime < (START_DELAY * 1000));
			if (inStartPhase && currentTime - lastAudioPlayedAt > 1000) {
				this.playAudioClip(this.beepClip);
				lastAudioPlayedAt = currentTime;
				int secondsLeft = (START_DELAY) - (int) (currentTime - startTime) / 1000;
				this.updateStatusLabel("Starting in " + secondsLeft + " seconds.");
			}
			// Otherwise, export one page.
			if (!inStartPhase && this.nextPageRequested) {
				this.nextPageRequested = false; // Reset the flag so that some other process has to set it before the next page is exported.
				// If this is the first time we're exporting, play a sound.
				if (lastPageExportedAt == 0) {
					this.initStatusPanel();
					this.updateStatusLabel("Exporting.");
					this.initNativeListener();
//					this.playAudioClip(this.beginningExportClip);
				}
				this.exportPageToClipboard(nextPageToExport);
				if (this.autoPaste) {
					this.pasteAndTurnPage();
				} else {
					this.addStatusMessage("Waiting to detect a CTRL+V keypress...");
				}
				nextPageToExport++;
				this.updateStatusProgressBar(nextPageToExport);
				// If we've reached the end of the book, stop the exporter.
				if (nextPageToExport >= this.book.getPageCount()) {
//					this.playAudioClip(this.finishClip);
					this.addStatusMessage("Export finished: " + this.book.getPageCount() + " pages exported.");
					if (!this.autoPaste) {
						this.stopNativeListener();
					}
					this.running = false;
					this.updateStatusLabel("Export finished");
					SwingUtilities.invokeLater(dialog::onExportFinished);
					break;
				}
				// Since there may be significant delay, get a fresh timestamp.
				lastPageExportedAt = System.currentTimeMillis();
			}
		}
	}

	/**
	 * Loads the given page onto the system clipboard so either a user or this
	 * program can paste it into a minecraft book.
	 * @param page The index of the page to export.
	 */
	private void exportPageToClipboard(int page) {
		boolean clipboardSuccess = false;
		int attempts = 0;
		while (!clipboardSuccess) {
			try {
				attempts++;
				clipboard.setContents(new StringSelection(book.getPages().get(page).toString()), null);
				clipboardSuccess = true;
			} catch (IllegalStateException e) {
				System.err.println("Could not open and set contents of system clipboard.");
			}
			if (!clipboardSuccess) {
				try {
					Thread.sleep(CLIPBOARD_RETRY_DELAY_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (attempts > 10) {
				throw new RuntimeException("Could not insert page into clipboard after " + attempts + " attempts.");
			}
		}
		this.addStatusMessage("Exported page " + (page + 1) + " to clipboard.");
	}

	/**
	 * Automatically pastes and turns the page of the minecraft book, so that
	 * the next page can be pasted in.
	 */
	private void pasteAndTurnPage() {
		this.robot.keyPress(KeyEvent.VK_CONTROL);
		this.robot.keyPress(KeyEvent.VK_V);
		try {
			Thread.sleep(this.autoPasteDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.robot.keyRelease(KeyEvent.VK_V);
		this.robot.keyRelease(KeyEvent.VK_CONTROL);
		this.addStatusMessage("Pasted page into book.");
		this.robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
		try {
			Thread.sleep(this.autoPasteDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
		try { // Wait for minecraft to turn the page.
			Thread.sleep(this.autoPasteDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.addStatusMessage("Clicked to turn the page.");
		this.nextPageRequested = true;
	}

	private void initNativeListener() {
		try {
			// For catching native events, set logging here.
			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);
			logger.setUseParentHandlers(false);
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this.exporterKeyListener);
		} catch (NativeHookException nativeHookException) {
			System.err.println("Could not register native hook.");
			nativeHookException.printStackTrace();
		}
	}

	private void stopNativeListener() {
		try {
			GlobalScreen.removeNativeKeyListener(this.exporterKeyListener);
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException nativeHookException) {
			System.err.println("Could not unregister a native hook.");
			nativeHookException.printStackTrace();
		}
	}

	private void playAudioClip(Clip clip) {
		clip.setFramePosition(0);
		clip.start();
	}

	private Clip loadAudioClip(String path) {
		try {
			Clip clip = AudioSystem.getClip();
			InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream(path);
			if (fileInputStream == null) {
				return null;
			}
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(fileInputStream));
			clip.open(ais);
			return clip;
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.err.println("Could not load audio clip.");
			e.printStackTrace();
			return null;
		}
	}

	private void initStatusPanel() {
		SwingUtilities.invokeLater(() -> {
			JProgressBar bar = this.statusPanel.getExportProgressBar();
			bar.setMinimum(0);
			bar.setMaximum(this.book.getPageCount());
			bar.setStringPainted(true);
			this.updateStatusProgressBar(0);
		});
	}

	private void updateStatusLabel(String text) {
		SwingUtilities.invokeLater(() -> this.statusPanel.getStatusLabel().setText(text));
	}

	private void updateStatusProgressBar(int nextPage) {
		SwingUtilities.invokeLater(() -> {
			JProgressBar bar = this.statusPanel.getExportProgressBar();
			bar.setValue(nextPage);
			bar.setString(String.format("%d of %d pages exported", nextPage, this.book.getPageCount()));
		});
	}

	private void addStatusMessage(String message) {
		SwingUtilities.invokeLater(() -> this.statusPanel.getOutputTextArea().append(message + "\n"));
	}
}

package nl.andrewlalis.blockbookbinder.control.export;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Native key listener that's used during the export process, to detect when the
 * user performs certain key actions outside the focus of this program.
 */
public class ExporterKeyListener implements NativeKeyListener {
	private final BookExporter exporterRunnable;

	public ExporterKeyListener(BookExporter exporterRunnable) {
		this.exporterRunnable = exporterRunnable;
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		if (nativeKeyEvent.getKeyCode() == NativeKeyEvent.VC_V && (nativeKeyEvent.getModifiers() & NativeKeyEvent.CTRL_MASK) > 0) {
			// Wait a little bit so that we can let the system do whatever it was planning to do with the original paste action.
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.exporterRunnable.setNextPageRequested(true);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {}
}

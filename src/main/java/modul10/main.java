package modul10;

import modul10.view.MahasiswaApp;
import javax.swing.SwingUtilities;

public class main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MahasiswaApp().setVisible(true));
	}
}
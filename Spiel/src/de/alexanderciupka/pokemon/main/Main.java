package de.alexanderciupka.pokemon.main;

import java.io.File;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;

public class Main {

	public static final double FPS = 60;

	public static boolean SONG_PAUSE = false;
	public static boolean FORCE_REPAINT = false;

	public static JOptionPane getOptionPane(JComponent parent) {
		JOptionPane pane = null;
		if (!(parent instanceof JOptionPane)) {
			pane = getOptionPane((JComponent) parent.getParent());
		} else {
			pane = (JOptionPane) parent;
		}
		return pane;
	}

	public static String arrayToString(Object[] array) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				result += "+";
			}
			result += array[i];
		}
		return result;
	}

	public static void main(String[] args) throws Exception {

		MenuController.getInstance();

		Thread repainter = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = 0;
				GameController gController = null;
				GameFrame frame = null;
				while ((gController = GameController.getInstance()) == null) {
					Thread.yield();
				}
				while ((frame = gController.getGameFrame()) == null) {
					Thread.yield();
				}
				while (true) {
					startTime = System.currentTimeMillis();
					if (!frame.getDialogue().isVisible()
							|| (gController.isFighting() && !frame.getFightPanel().getTextLabel().isVisible())
							|| FORCE_REPAINT) {
						frame.repaint();
						if (FORCE_REPAINT) {
							FORCE_REPAINT = false;
						}
					} else {
						if (frame.getDialogue().isVisible()) {
							frame.getDialogue().repaint();
						} else if (frame.getFightPanel().getTextLabel().isVisible()) {
							frame.getFightPanel().getTextLabel().repaint();
						}
					}
					try {
						Thread.sleep((long) Math.max(1000.0 / Main.FPS - (System.currentTimeMillis() - startTime), 0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		repainter.setDaemon(true);
		repainter.setName("REPAINTER");
		repainter.start();

		Thread songChooser = new Thread(new Runnable() {
			@Override
			public void run() {
				SoundController sc = SoundController.getInstance();
				Random rng = new Random();
				File[] songs = new File(Main.class.getResource("/music/songs/").getFile()).listFiles();
				while (true) {
					if (!SONG_PAUSE && !sc.isSongRunning()) {
						File song = songs[rng.nextInt(songs.length)];
						sc.playSong(song);
					}
					Thread.yield();
				}
			}
		});
		songChooser.setDaemon(true);
		songChooser.setName("SONGCHOOSER");
		songChooser.start();
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * HAPPY BIRTHDAY NACHTRÄGLICH!
	 * HOFFE DIR GEFÄLLT DIE KLEINIGKEIT,
	 * DIE ICH HIER GEMACHT HABE :)
	 * 
	 * DEIN ALEX!
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
}

package de.alexanderciupka.pokemon.painting;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PaintingListener implements WindowListener {

	private PaintingController pController;
	
	public PaintingListener() {
		pController = PaintingController.getInstance();
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {
		pController.updateColorLabel(new Color(254,254,254));
		pController.closeOptionFrame();
	}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

}

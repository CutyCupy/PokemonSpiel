package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import de.alexanderciupka.pokemon.map.GameController;

public class BackgroundLabel extends JLabel {

	
	private ArrayList<Overlay> overlay;
	private GameController gController;

	public BackgroundLabel(int x, int y) {
		super();
		gController = GameController.getInstance();
		this.overlay = new ArrayList<Overlay>();
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		double x = (gController.getMainCharacter().getExactX() - 4);
		double y = (gController.getMainCharacter().getExactY() - 4);
		double w = 9;
		double h = 9;

		double xOffset = 0;
		double yOffset = 0;

		if(x < 0) {
			xOffset = -x;
			x = 0;
		}
		if(y < 0) {
			yOffset = -y;
			y = 0;
		}

		if(x + w > gController.getCurrentBackground().getCurrentRoute().getWidth()) {
			w = (gController.getCurrentBackground().getCurrentRoute().getWidth() - x);
		}
		if(y + h > gController.getCurrentBackground().getCurrentRoute().getHeight()) {
			h = (gController.getCurrentBackground().getCurrentRoute().getHeight() - y);
		}
		
		g.drawImage(gController.getCurrentBackground().getCurrentRoute().getMap().getSubimage((int) (x * GameFrame.GRID_SIZE), (int) (y * GameFrame.GRID_SIZE), (int) (w * GameFrame.GRID_SIZE),
				(int) (h * GameFrame.GRID_SIZE)),
				(int) (xOffset * GameFrame.GRID_SIZE), (int) (yOffset * GameFrame.GRID_SIZE), null);
		g.drawImage(gController.getMainCharacter().getCharacterImage(), (int) ((gController.getMainCharacter().getExactX() - x + xOffset) * GameFrame.GRID_SIZE),
				(int) ((gController.getMainCharacter().getExactY() - y + yOffset) * GameFrame.GRID_SIZE), null);
		
		for(Overlay o : overlay) {
			if(o.created) {
				g.drawImage(o.getOverlay(), 0, 0, null);
			}
		}
		for(int i = 0; i < overlay.size(); i++) {
			if(overlay.get(i).isFinshed()) {
				overlay.remove(i);
				i--;
			}
		}
	}
	
	public void addOverlay(Overlay ov) {
		this.overlay.add(ov);
	}

	public void startEncounter() {
		Form f = Form.values()[new Random().nextInt(Form.values().length)];
		CollapseOverlay c = new CollapseOverlay(this, this.getSize(), f);
		final int DEFAULT_STEP_SIZE = 25;
		switch(f) {
		case CIRCLE:
			c.startAnimation((int) (DEFAULT_STEP_SIZE * ((Math.sqrt(Math.pow(this.getWidth(), 2) + Math.pow(this.getHeight(), 2))) / this.getWidth())), 0);
			break;
		case RECTANGLE:
			c.startAnimation(DEFAULT_STEP_SIZE, 0);
			break;
		}
		this.addOverlay(c);
		while(!c.isFinshed()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

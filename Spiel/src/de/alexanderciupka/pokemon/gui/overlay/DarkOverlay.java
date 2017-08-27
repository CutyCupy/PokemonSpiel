package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.GameController;

public class DarkOverlay extends Overlay {

	public int visionSize;
	
	public DarkOverlay(BackgroundLabel parent, Dimension size, int visionSize) {
		super(parent, size);
		this.visionSize = visionSize;
	}

	@Override
	public void createOverlay() {
		BufferedImage foo =  new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		if(this.overlay == null) {
			this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		}
		Graphics g = foo.getGraphics();

		g.setColor(Color.RED);
		g.fillOval(this.size.width / 2 - (visionSize / 2), this.size.height / 2 - (visionSize / 2), visionSize,
				visionSize);
		
		int transparentRGB = new Color(255, 255, 255, 0).getRGB();
		int blackRGB = Color.BLACK.getRGB();

		for (int x = 0; x < foo.getWidth(); x++) {
			for (int y = 0; y < foo.getHeight(); y++) {
				if (foo.getRGB(x, y) != Color.RED.getRGB()) {
					foo.setRGB(x, y, blackRGB);
				} else {
					foo.setRGB(x, y, transparentRGB);
				}
			}
		}
		this.overlay = foo;
		created = true;
	}

	public void update(int visionSize) {
		this.visionSize = visionSize;
		createOverlay();
	}

	public int getVisionSize() {
		return this.visionSize;
	}

	public void flash() {
		if(this.visionSize != GameFrame.FRAME_SIZE) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (visionSize != GameFrame.FRAME_SIZE) {
						visionSize = visionSize + 30 < GameFrame.FRAME_SIZE ? visionSize + 30 : GameFrame.FRAME_SIZE;
						createOverlay();
						parent.repaint();
					}
					parent.repaint();				
				}
			}).start();
			GameController.getInstance().getGameFrame().addDialogue("Die Höhle wurde hell erleuchtet!");
			GameController.getInstance().waitDialogue();
		}
	}
}

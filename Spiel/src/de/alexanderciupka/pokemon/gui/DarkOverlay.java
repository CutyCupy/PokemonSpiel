package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class DarkOverlay extends Overlay {
	
	public int visionSize;

	public DarkOverlay(BackgroundLabel parent, Dimension size, int visionSize) {
		super(parent, size);
		this.visionSize = visionSize;
	}

	@Override
	public void createOverlay() {
		this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = overlay.getGraphics();
		
		g.setColor(Color.RED);
		g.fillOval(this.size.width / 2 - (visionSize / 2), this.size.height / 2 - (visionSize / 2), visionSize, visionSize);
		
		for(int x = 0; x < overlay.getWidth(); x++) {
			for(int y = 0; y < overlay.getHeight(); y++) {
				if(overlay.getRGB(x, y) != Color.RED.getRGB()) {
					overlay.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					overlay.setRGB(x, y, new Color(255, 255, 255, 0).getRGB());
				}
			}
		}
		created = true;
	}
	
	public void update(int visionSize) {
		this.visionSize = visionSize;
		createOverlay();
		parent.repaint();
	}
	
	public int getVisionSize() {
		return this.visionSize;
	}
}

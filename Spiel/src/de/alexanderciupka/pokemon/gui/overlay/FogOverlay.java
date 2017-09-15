package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;

public class FogOverlay extends Overlay {

	
	private Color fogColor;
	
	public FogOverlay(BackgroundLabel parent, Dimension size, FogType fog) {
		super(parent, size);
		fogColor = new Color(255, 255, 255, fog.getAlpha());
	}

	@Override
	public void createOverlay() {
		this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		for(int x = 0; x < this.overlay.getWidth(); x++) {
			for(int y = 0; y < this.overlay.getHeight(); y++) {
				overlay.setRGB(x, y, fogColor.getRGB());
			}
		}
		created = true;
		parent.repaint();
	}

}

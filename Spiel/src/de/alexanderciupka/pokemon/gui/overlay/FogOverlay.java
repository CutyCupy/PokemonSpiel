package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
		Graphics g = this.overlay.getGraphics();
		g.setColor(fogColor);
		g.fillRect(0, 0, this.size.width, this.size.height);
		created = true;
	}

}

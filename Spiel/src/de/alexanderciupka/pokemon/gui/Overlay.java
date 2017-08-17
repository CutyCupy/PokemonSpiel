package de.alexanderciupka.pokemon.gui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public abstract class Overlay {
	
	public Dimension size;
	public BufferedImage overlay;
	public BackgroundLabel parent;
	
	public boolean finished;
	public boolean created;
	
	public Overlay(BackgroundLabel parent, Dimension size) {
		this.size = size;
		this.parent = parent;
	}
	
	
	public abstract void createOverlay();
	
	public BufferedImage getOverlay() {
		return this.overlay;
	}
	
	public boolean isFinshed() {
		return this.finished;
	}
	
	public void setFinished(boolean b) {
		this.finished = b;
	}

}

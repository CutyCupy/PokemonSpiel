package de.alexanderciupka.sarahspiel.painting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import de.alexanderciupka.sarahspiel.menu.MenuController;

public class PaintingController {

	private static PaintingController instance;
	
	private PaintingFrame pFrame;
	private OptionFrame oFrame;
	private Painting currentPainting;
	private boolean floodMode;
	private Dimension screenResolution;
	
	private PaintingController() {}
	
	public static PaintingController getInstance() {
		if(instance == null) {
			instance = new PaintingController();
			instance.start();
		}
		return instance;
	}
	
	private void start() {
		pFrame = new PaintingFrame();
		oFrame = new OptionFrame();
		screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	public void setPaintingColor(Color c) {
		pFrame.setColor(c);
	}
	
	public void updateColorLabel(Color c) {
		oFrame.updateColorLabel(c);
	}

	public void savePainting() {
		currentPainting.save();
	}

	public void startNewImage(String name, int width, int height) {
		currentPainting = new Painting(name, width, height);
		pFrame.setBounds(MenuController.getToCenter(width, height));
		pFrame.start(name);
		oFrame.setLocation(getOptionFramePosition());
		oFrame.start();
	}

	private void startLoadedImage() {
		pFrame.loadImage(currentPainting.getImage(), currentPainting.getName());
		oFrame.setLocation(getOptionFramePosition());
		oFrame.start();
	}
	
	public Painting getCurrentPainting() {
		return currentPainting;
	}
	
	public int getCircleWidth() {
		return oFrame.getCircleWidth();
	}

	public boolean findImage() {
		currentPainting = new Painting("", 1, 1);
		if(currentPainting.load()) {
			startLoadedImage();
			return true;
		}
		return false;
	}

	public void floodImage(int startX, int startY, Color newColor) {
		currentPainting.flood(startX, startY, newColor);
	}
	
	public void setFloodMode(boolean fillEnabled) {
		floodMode = fillEnabled;
	}
	
	public boolean getFloodMode() {
		return floodMode;
	}
	
	public Dimension getScreenResolution() {
		return screenResolution;
	}
	
	private Point getOptionFramePosition() {
		int pWidth = pFrame.getWidth();
		int oWidth = oFrame.getWidth();
		Point position = new Point();
		if(pFrame.getX() + pWidth + oWidth > (int) screenResolution.getWidth()) {
			position.x = (int) (screenResolution.getWidth() - oWidth);
		} else {
			position.x = pFrame.getX() + pWidth;
		}
		position.y = pFrame.getY();
		return position;
	}

	public void closeOptionFrame() {
		oFrame.dispose();
	}
	
	public void backup() {
		currentPainting.backup();	
	}
	
	public void loadBackup() {
		pFrame.drawImg(currentPainting.loadBackup());
	}
}

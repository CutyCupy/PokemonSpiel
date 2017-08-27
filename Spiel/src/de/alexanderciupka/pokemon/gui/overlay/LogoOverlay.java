package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.map.GameController;

public class LogoOverlay extends Overlay {

	private GameController gController;
	private String logo;
	private Image logoImage;
	
	public LogoOverlay(String logoName, BackgroundLabel parent, Dimension size) {
		super(parent, size);
		this.gController = GameController.getInstance();
		this.logo = logoName;
		this.logoImage = gController.getRouteAnalyzer().getLogoByName(logo);
	}


	@Override
	public void createOverlay() {
		this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = overlay.getGraphics();
		g.drawImage(this.logoImage, 0, 0, null);
		created = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finished = true;
				parent.repaint();
			}
		}).start();
		parent.repaint();
	}
	
	public String getLogo() {
		return logo;
	}


	public void setLogo(String logo) {
		this.logo = logo;
	}


	public Image getLogoImage() {
		return logoImage;
	}

}

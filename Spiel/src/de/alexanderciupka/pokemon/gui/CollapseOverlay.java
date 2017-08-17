package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CollapseOverlay extends Overlay {

	public int visionSize;
	public Form type;
	
	public CollapseOverlay(BackgroundLabel parent, Dimension size, Form type) {
		super(parent, size);
		this.type = type;
	}
	
	@Override
	public void createOverlay() {
		BufferedImage foo = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		if(this.overlay == null) {
			this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		}
		Graphics g = foo.getGraphics();
		
		g.setColor(Color.RED);
		switch(type) {
		case CIRCLE:
			g.fillOval(this.size.width / 2 - (visionSize / 2), this.size.height / 2 - (visionSize / 2), visionSize, visionSize);
			break;
		case RECTANGLE:
			g.fillRect(this.size.width / 2 - (visionSize / 2), this.size.height / 2 - (visionSize / 2), visionSize, visionSize);
			break;
		}
		
		for(int x = 0; x < foo.getWidth(); x++) {
			for(int y = 0; y < foo.getHeight(); y++) {
				if(foo.getRGB(x, y) != Color.RED.getRGB()) {
					foo.setRGB(x, y, Color.BLACK.getRGB());
				} else {
					foo.setRGB(x, y, new Color(255, 255, 255, 0).getRGB());
				}
			}
		}
		this.overlay = foo;
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
	
	public void startAnimation(int stepSize, int pause) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				visionSize = Math.max(size.height, size.width);
				switch(type) {
				case CIRCLE:
					visionSize = (int) (Math.sqrt(Math.pow(size.width, 2) + Math.pow(size.height, 2)));
					break;
				case RECTANGLE:
					visionSize = Math.max(size.width, size.height);
					break;
				}
				while(visionSize != 0) {
					visionSize = visionSize - stepSize > 0 ? visionSize - stepSize : 0;
					update(visionSize);
					parent.repaint();
					try {
						Thread.sleep(pause);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				finished = true;
				parent.repaint();
			}
		}).start();
	}
	

}

package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.map.GameController;

public class SnowOverlay extends Overlay {
	
	private ArrayList<Snowflake> snowflakes;

	private FogOverlay fog;

	public SnowOverlay(BackgroundLabel parent, Dimension size, SnowType type) {
		super(parent, size);
		snowflakes = new ArrayList<>();
		for (int i = 0; i < type.getSnowflakes(); i++) {
			snowflakes.add(new Snowflake(type));
		}
		switch(type) {
		case BLIZZARD:
			this.fog = new FogOverlay(parent, size, FogType.MIST);
			this.fog.createOverlay();
			break;
		}
	}

	@Override
	public void createOverlay() {
		if(this.fog != null) {
			this.fog.createOverlay();
			this.overlay = this.fog.getOverlay();
		} else {
			this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		}
		Graphics g = overlay.getGraphics();
		g.setColor(Snowflake.COLOR);
		for (Snowflake s : snowflakes) {
//			Dimension size = s.getSize();
//			int xPoints[] = {
//					(int) (s.getX() + size.width / 2.0), 
//					(int) (s.getX() + s.getSize().width),
//					(int) (s.getX() + size.width / 2.0),
//					(int) (s.getX())
//			};
//			int yPoints[] = {
//					(int) (s.getY()),
//					(int) (s.getY() + size.height / 2.0),
//					(int) (s.getY() + size.height),
//					(int) (s.getY() + size.height / 2.0)
//			};
//			g.fillPolygon(xPoints, yPoints, 4);
			g.fillOval((int) s.getX(), (int) s.getY(), s.getSize().width, s.getSize().height);
		}

		created = true;
		while (GameController.getInstance().getGameFrame().getDialogue().isVisible()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
//		parent.repaint();
	}

	public void startAnimation() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					for (Snowflake s : snowflakes) {
						s.fall();
					}
					createOverlay();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		;
	}
	

	public ArrayList<Snowflake> getSnowflakes() {
		return snowflakes;
	}
	
	

}

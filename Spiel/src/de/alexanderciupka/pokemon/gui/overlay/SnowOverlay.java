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

	private Thread animation;

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
		default:
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
			g.fillOval((int) s.getX(), (int) s.getY(), s.getSize().width, s.getSize().height);
		}

		created = true;
		while (GameController.getInstance().getGameFrame().getDialogue().isVisible()) {
			try {
				Thread.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void startAnimation() {
		if(animation == null) {
			animation = new Thread(new Runnable() {
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
							return;
						}
					}
				}
			});
			animation.start();
		}
	}


	public ArrayList<Snowflake> getSnowflakes() {
		return snowflakes;
	}


	@Override
	public void onRemove() {
		animation.interrupt();
	}


}

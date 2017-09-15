package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.map.GameController;

public class RainOverlay extends Overlay {

	private ArrayList<Raindrop> raindrops;

	private RainType type;

	private Random rng;
	private int thunderFrames;

	public RainOverlay(BackgroundLabel parent, Dimension size, RainType type) {
		super(parent, size);
		this.type = type;
		this.rng = new Random();
		raindrops = new ArrayList<Raindrop>();
		for (int i = 0; i < type.getRaindrops(); i++) {
			raindrops.add(new Raindrop());
		}
	}

	@Override
	public void createOverlay() {
		this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = overlay.getGraphics();
		g.setColor(Raindrop.COLOR);
		for (Raindrop r : raindrops) {
			g.fillRect((int) r.getX(), (int) r.getY(), r.getSize().width, r.getSize().height);
		}

		if (this.type == RainType.STORM && (thunderFrames > 0 || rng.nextFloat() < 0.001)) {
			if (thunderFrames == 0) {
				thunderFrames = rng.nextInt(10) + 50;
			}
			int alpha = 0;
			if (thunderFrames > 15 && thunderFrames < 35) {
				alpha = 200;
			} else if (thunderFrames <= 15) {
				alpha = thunderFrames * 13;
			} else {
				alpha = Math.min((60 - thunderFrames) * 13, 200);
			}
			
			alpha = Math.min(alpha, 200);
			
			g.setColor(new Color(255, 255, 255, alpha));
			g.fillRect(0, 0, this.size.width, this.size.height);
			thunderFrames--;
		}
		created = true;
		while (GameController.getInstance().getGameFrame().getDialogue().isVisible()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		parent.repaint();
	}

	public void startAnimation() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					for (Raindrop r : raindrops) {
						r.fall();
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
	}
	
	public ArrayList<Raindrop> getRaindrops() {
		return raindrops;
	}

}

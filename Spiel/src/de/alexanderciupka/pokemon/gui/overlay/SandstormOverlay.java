package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.menu.SoundController;

public class SandstormOverlay extends Overlay implements IAnimated {
	private ArrayList<Sand> sandcorns;

	private Thread animation;

	public SandstormOverlay(BackgroundLabel parent, Dimension size) {
		super(parent, size);
		sandcorns = new ArrayList<>();
		for (int i = 0; i < 1500; i++) {
			sandcorns.add(new Sand());
		}
	}

	@Override
	public void createOverlay() {
		BufferedImage temp = null;
		temp = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = temp.getGraphics();
		for (Sand r : sandcorns) {
			g.setColor(r.getColor());
			g.fillRect((int) r.getX(), (int) r.getY(), r.getSize().width, r.getSize().height);
		}
		this.overlay = temp;
		created = true;
	}

	@Override
	public void startAnimation() {
		if (animation == null) {
			animation = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						for (Sand s : sandcorns) {
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

	public ArrayList<Sand> getSandcorns() {
		return sandcorns;
	}

	@Override
	public void onRemove() {
		animation.interrupt();
		SoundController.getInstance().stopRain();
	}

}

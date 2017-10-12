package de.alexanderciupka.pokemon.gui;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.alexanderciupka.pokemon.map.GameController;

public class AnimationLabel extends JLabel {


	private JLabel animationLabel;

	private boolean isPlayer;

	public AnimationLabel(boolean isPlayer) {
		setLayout(null);
		this.isPlayer = isPlayer;
		animationLabel = new JLabel();
		animationLabel.setOpaque(false);
		this.add(animationLabel);
	}

	public void playAnimation(String animations) {
		animationLabel.setBounds(0, 0, this.getWidth(), this.getHeight());
		for(String animation : animations.split("\\+")) {
			BufferedImage bi = GameController.getInstance().getRouteAnalyzer().getAnimationImage(animation);
			if(bi == null) {
				continue;
			}
			long currentTime = 0;
			for(int y = 0; y < bi.getHeight(); y += 192) {
				for(int x = 0; x < bi.getWidth(); x += 192) {
					currentTime = System.currentTimeMillis();
					BufferedImage image = bi.getSubimage(x, y, 192, 192);
					if(isPlayer) {
						AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
						tx.translate(-image.getWidth(null), 0);
						image = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(image, null);
					}
					animationLabel.setIcon(new ImageIcon(image.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_SMOOTH)));
					repaint();
					try {
						Thread.sleep(Math.max(50 - (System.currentTimeMillis() - currentTime), 0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		animationLabel.setIcon(null);
	}

}

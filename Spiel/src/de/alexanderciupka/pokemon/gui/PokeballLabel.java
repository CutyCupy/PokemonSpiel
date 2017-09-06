package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JLabel;

import de.alexanderciupka.pokemon.gui.panels.FightPanel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Item;

public class PokeballLabel extends JLabel {
	
	private Item ball;
	private BufferedImage image;
	private GameController gController;
	
	private static HashMap<Integer, Integer> coordinates;
	
	private int x;
	private int y;
	
	private double degree;
	
	public PokeballLabel() {
		gController = GameController.getInstance();
		setBounds(0, 0, GameFrame.FRAME_SIZE, GameFrame.FRAME_SIZE);
		
		if(coordinates == null) {
			coordinates = new HashMap<Integer, Integer>();
			for (int x = 150; x <= 475; x++) {
				coordinates.put(x, nextPokeballCoordinate(x));
			}
		}
	}
	
	public void setBall(Item ball) {
		this.ball = ball;
		this.image = FightPanel.pokeballImages.get(this.ball);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		double rotationRequired = Math.toRadians(degree);
		double locationX = image.getWidth() / 2;
		double locationY = image.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		g2d.drawImage(op.filter(image, null), x - image.getWidth() / 2, y - image.getHeight() / 2, null);
	}
	
	public void throwBall() {
		this.degree = 0;
		for (int x = 150; x <= 475; x++) {
			this.x = x;
			this.y = coordinates.get(x);
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
			degree += 1080 / 326.0;
			degree %= 360;
		}
		this.degree = 0;
		this.image = FightPanel.openPokeballImages.get(this.ball);
		repaint();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void drop() {
		this.image = FightPanel.pokeballImages.get(this.ball);
		for(int bounce = 50; bounce > 0; bounce /= 2) {
			for(int y = 0; y < bounce; y++) {
				this.y += 1;
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
			}
			for(int y = 0; y < bounce / 2; y++) {
				this.y -= 1;
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				repaint();
			}
		}
	}
	
	public void shake(int times)  {
		for(int shakes = 0; shakes < Math.min(3, times); shakes++) {
			try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.x = 475;
			int degree = 0;
			for(int i : new int[]{45, -45, 0}) {
				for(; i < 0 ? degree >= i : degree <= i; degree += (i < 0 ? -5 : 5)) {
					this.degree = degree;
					this.degree %= 360;
					this.x = 475 + (degree / 5);
					repaint();
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(times == 4) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			this.image = FightPanel.openPokeballImages.get(this.ball);
			repaint();
		}
	}
	
	private int nextPokeballCoordinate(int x) {
		return (int) (0.0038825965 * Math.pow(x, 2) - 2.5518694434 * x + 516.1271465108);
	}
	
}

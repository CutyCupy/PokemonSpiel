package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.Camera;
import de.alexanderciupka.pokemon.map.GameController;

public class SpottedOverlay extends Overlay {

	private Character spotter;
	private static Image SPOT_SIGN;

	private GameController gController;

	public SpottedOverlay(BackgroundLabel parent, Character spotter) {
		super(parent, new Dimension(GameFrame.FRAME_SIZE, GameFrame.FRAME_SIZE));
		if (SPOT_SIGN == null) {
			try {
				SPOT_SIGN = ImageIO.read(new File(Main.class.getResource("/icons/spotsign.png").getFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.gController = GameController.getInstance();
		this.spotter = spotter;
	}

	@Override
	public void createOverlay() {
		Camera cam = GameController.getInstance().getCurrentBackground().getCamera();
		double x = (cam.getX() - (GameFrame.FRAME_SIZE / GameFrame.GRID_SIZE) / 2.0);
		double y = (cam.getY() - (GameFrame.FRAME_SIZE / GameFrame.GRID_SIZE) / 2.0);
		
		double w = 9;
		double h = 9;

		double xOffset = 0;
		double yOffset = 0;

		if(x < 0) {
			xOffset = -x;
			x = 0;
		}
		if(y < 0) {
			yOffset = -y;
			y = 0;
		}

		if(x + w > gController.getCurrentBackground().getCurrentRoute().getWidth()) {
			w = (gController.getCurrentBackground().getCurrentRoute().getWidth() - x);
		}
		if(y + h > gController.getCurrentBackground().getCurrentRoute().getHeight()) {
			h = (gController.getCurrentBackground().getCurrentRoute().getHeight() - y);
		}
		
		double xPos = (spotter.getExactX() - x + xOffset) * GameFrame.GRID_SIZE
				- (spotter.getCharacterImage().getWidth(null) - GameFrame.GRID_SIZE) / 2.0;
		double yPos = (spotter.getExactY() - y + yOffset) * GameFrame.GRID_SIZE
				- (spotter.getCharacterImage().getHeight(null) - GameFrame.GRID_SIZE);
		
		this.overlay = new BufferedImage(this.size.width, this.size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = this.overlay.getGraphics();
//		g.drawImage(SPOT_SIGN, (int) ((spotter.getExactX() - x + xOffset) * GameFrame.GRID_SIZE + 10), 
//						(int) ((spotter.getExactY() - y + yOffset) * GameFrame.GRID_SIZE - 50), null);
		g.drawImage(SPOT_SIGN, (int) (xPos + 10), 
				(int) (yPos - 50), null);
		
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

}

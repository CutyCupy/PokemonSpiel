package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;

import javax.swing.JLabel;

import de.alexanderciupka.pokemon.map.GameController;

public class BackgroundLabel extends JLabel {

	private int characterX;
	private int characterY;
	private GameController gController;

	public BackgroundLabel(int x, int y) {
		super();
		characterX = x;
		characterY = y;
		gController = GameController.getInstance();
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		double x = (gController.getMainCharacter().getExactX() - 4);
		double y = (gController.getMainCharacter().getExactY() - 4);
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


		g.drawImage(gController.getCurrentBackground().getCurrentRoute().getMap().getSubimage((int) (x * GameFrame.GRID_SIZE), (int) (y * GameFrame.GRID_SIZE), (int) (w * GameFrame.GRID_SIZE),
				(int) (h * GameFrame.GRID_SIZE)),
				(int) (xOffset * GameFrame.GRID_SIZE), (int) (yOffset * GameFrame.GRID_SIZE), null);
		g.drawImage(gController.getMainCharacter().getCharacterImage(), (int) ((gController.getMainCharacter().getExactX() - x + xOffset) * GameFrame.GRID_SIZE),
				(int) ((gController.getMainCharacter().getExactY() - y + yOffset) * GameFrame.GRID_SIZE), null);
	}

	public void setCharacterX(int characterX) {
		this.characterX = characterX;
	}

	public void setCharacterY(int characterY) {
		this.characterY = characterY;
	}

}

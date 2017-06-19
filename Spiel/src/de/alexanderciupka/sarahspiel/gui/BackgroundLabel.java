package de.alexanderciupka.sarahspiel.gui;

import java.awt.Graphics;

import javax.swing.JLabel;

import de.alexanderciupka.sarahspiel.map.GameController;

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
//		g.drawImage(gController.getCurrentBackground().getBackground(),
//				characterX - (int) gController.getMainCharacter().getCurrentPosition().getX() * 70,
//				characterY - (int) gController.getMainCharacter().getCurrentPosition().getY() * 70, null);
		g.drawImage(gController.getCurrentBackground().getBackground(),
				characterX - (int) (gController.getMainCharacter().getExactX() * 70),
				characterY - (int) (gController.getMainCharacter().getExactY() * 70), null);
		g.drawImage(gController.getMainCharacter().getCharacterImage(), characterX, characterY, null);
	}
	
	public void setCharacterX(int characterX) {
		this.characterX = characterX;
	}
	
	public void setCharacterY(int characterY) {
		this.characterY = characterY;
	}

}

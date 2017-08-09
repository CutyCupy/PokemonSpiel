package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Type;

public class MoveButton extends JButton {
	
	private Move move;
	
	public MoveButton() {
		setMove(null);
	}
	
	public void setMove(Move m) {
		this.move = m;
		if (this.move != null) {
			setBackground(Color.WHITE);
			setText("<html> <div style='text-align: center;'>" + this.move.getName() + "<br>" + this.move.getCurrentPP() + "/"
					+  this.move.getPp() + " </div></html>");
			setName(this.move.getName());
			setBorder(BorderFactory.createLineBorder(Type.getColor(this.move.getMoveType()), 5));
			if(this.move.getCurrentPP() == 0) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
		} else {
			setBackground(Color.LIGHT_GRAY);
			setEnabled(false);
		}
	}
	
	public Move getMove() {
		return this.move;
	}
	

}

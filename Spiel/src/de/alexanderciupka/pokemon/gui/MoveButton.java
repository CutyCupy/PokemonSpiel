package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Type;

public class MoveButton extends JButton {
	
	private Move move;
	private TypeLabel type;
	private JLabel damageType;
	
	public MoveButton(boolean showType) {
		this.setLayout(null);
		type = new TypeLabel();
		type.setLocation(10, 12);
		damageType = new JLabel();
		damageType.setSize(type.getSize());
		if(showType) {
			this.add(type);
			this.add(damageType);
		}
		setMove(null);
	}
	
	public void setMove(Move m) {
		this.move = m;
		if (this.move != null) {
			setBackground(Color.WHITE);
			setText("<html> <div style='text-align: center;'>" + this.move.getName() + "<br>" + "AP: " + this.move.getCurrentPP() + "/"
					+  this.move.getPp() + " </div></html>");
			setName(this.move.getName());
			setBorder(BorderFactory.createLineBorder(Type.getColor(this.move.getMoveType()), 5));
			if(this.move.getCurrentPP() == 0) {
				setEnabled(false);
			} else {
				setEnabled(true);
			}
			type.setType(m.getMoveType());
			
			damageType.setLocation(this.getWidth() - type.getWidth() - 10, 12);
			
			switch(m.getDamageClass()) {
			case PHYSICAL:
				damageType.setIcon(new ImageIcon(DamageClass.PHYSICAL_IMAGE));
				break;
			case SPECIAL:
				damageType.setIcon(new ImageIcon(DamageClass.SPECIAL_IMAGE));
				break;
			case NO_DAMAGE:
				damageType.setIcon(new ImageIcon(DamageClass.NO_DAMAGE_IMAGE));
				break;
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

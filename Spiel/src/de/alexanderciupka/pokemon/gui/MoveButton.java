package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Type;

public class MoveButton extends JButton {

	private Move move;
	private Pokemon source;
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
		setMove(null, null);
	}

	public void setMove(Pokemon source, Move m) {
		this.move = m;
		this.source = source;
		if (this.move != null) {
			setBackground(Color.WHITE);
			setText("<html> <div style='text-align: center;'>" + this.move.getName() + "<br>" + "AP: " + this.move.getCurrentPP() + "/"
					+  this.move.getPp() + " </div></html>");
			setName(this.move.getName());
			setBorder(BorderFactory.createLineBorder(Type.getColor(this.move.getMoveType()), 5));
			if(this.move.getCurrentPP() == 0 ||
					(GameController.getInstance().isFighting() &&
							GameController.getInstance().getFight().getCurrentFightOption().equals(FightOption.FIGHT) &&
							!move.equals(GameController.getInstance().getFight().canUse(source, move)))) {
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
			setText("");
			setBackground(Color.LIGHT_GRAY);
			setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
			damageType.setIcon(null);
			type.setType(null);
			setEnabled(false);
		}
	}

	public Move getMove() {
		return this.move;
	}
	
	public Pokemon getSource() {
		return this.source;
	}


}

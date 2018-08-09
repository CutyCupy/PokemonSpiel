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
		this.type = new TypeLabel();
		this.type.setLocation(10, 12);
		this.damageType = new JLabel();
		this.damageType.setSize(this.type.getSize());
		if (showType) {
			this.add(this.type);
			this.add(this.damageType);
		}
		this.setMove(null, null);

		this.setBackground(Color.WHITE);
		this.setFocusable(false);
	}

	public void setMove(Pokemon source, Move m) {
		this.move = m;
		this.source = source;
		if (this.move != null) {
			this.setBackground(Color.WHITE);
			this.setText("<html> <div style='text-align: center;'>" + this.move.getName() + "<br>" + "AP: "
					+ this.move.getCurrentPP() + "/" + this.move.getPp() + " </div></html>");
			this.setName(this.move.getName());
			this.setBorder(BorderFactory.createLineBorder(Type.getColor(this.move.getMoveType()), 5));
			if (this.move.getCurrentPP() == 0 || (GameController.getInstance().isFighting()
					&& GameController.getInstance().getFight().getCurrentFightOption().equals(FightOption.FIGHT)
					&& !this.move.equals(GameController.getInstance().getFight().canUse(source, this.move)))) {
				this.setEnabled(false);
			} else {
				this.setEnabled(true);
			}
			this.type.setType(m.getMoveType());

			this.damageType.setLocation(this.getWidth() - this.type.getWidth() - 10, 12);

			switch (m.getDamageClass()) {
			case PHYSICAL:
				this.damageType.setIcon(new ImageIcon(DamageClass.PHYSICAL_IMAGE));
				break;
			case SPECIAL:
				this.damageType.setIcon(new ImageIcon(DamageClass.SPECIAL_IMAGE));
				break;
			case NO_DAMAGE:
				this.damageType.setIcon(new ImageIcon(DamageClass.NO_DAMAGE_IMAGE));
				break;
			}
		} else {
			this.setText("");
			this.setBackground(Color.LIGHT_GRAY);
			this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 5));
			this.damageType.setIcon(null);
			this.type.setType(null);
			this.setEnabled(false);
		}
	}

	public Move getMove() {
		return this.move;
	}

	public Pokemon getSource() {
		return this.source;
	}

}

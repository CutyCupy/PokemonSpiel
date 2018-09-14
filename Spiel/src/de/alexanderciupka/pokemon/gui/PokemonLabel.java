/**
 * @author CutyCupy
 */
package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

/**
 * The Label holds an ID between 1 and 4, which will be used to determine the
 * Pokemon that will be displayed. contains the AnimationLabel for any kind of
 * animation for this Pokemon.
 * 
 * @author CutyCupy
 */
public class PokemonLabel extends JLabel {

	private AnimationLabel animations;
	private Pokemon pokemon;

	public PokemonLabel() {
		this.animations = new AnimationLabel();

		this.setSize(160, 160);
		this.animations.setBounds(0, 0, 160, 160);

		this.add(this.animations);
	}

	public AnimationLabel getAnimationLabel() {
		return this.animations;
	}

	public void setPokemon(Pokemon p) {
		this.pokemon = p;
		this.animations.setIsPlayer(GameController.getInstance().getFight().isPlayer(p));
	}

	public Pokemon getPokemon() {
		return this.pokemon;
	}

	@Override
	protected void paintComponent(Graphics arg0) {
		super.paintComponent(arg0);
		if (this.pokemon != null) {
			this.setIcon(new ImageIcon(
					GameController.getInstance().getFight().isPlayer(this.pokemon) ? this.pokemon.getSpriteBack() : this.pokemon.getSpriteFront()));
		}
		this.setVisible(GameController.getInstance().getFight().isVisible(this.pokemon));
	}

}

package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

@SuppressWarnings("serial")
public class PokemonButton extends JButton {

	private Pokemon pokemon;
	private int index;
	private GameController gController;
	
	public PokemonButton(Pokemon pokemon, int index) {
		super();
		gController = GameController.getInstance();
		this.pokemon = pokemon;
		this.setIndex(index);
		update();
	}
	
	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
		update();
	}
	
	public Pokemon getPokemon() {
		return this.pokemon;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void update() {
		if(gController.isFighting()) {
			this.pokemon = gController.getFight().getPokemon(this.index);			
		} else {
			this.pokemon = gController.getMainCharacter().getTeam().getTeam()[this.index];
		}
		if(pokemon != null) {
			setIcon(new ImageIcon(pokemon.getSpriteFront()));
			setText("<html>" + pokemon.getName() + "<br>" + pokemon.getStats().getCurrentHP() + "/"
					+ pokemon.getStats().getStats()[0] + " KP" + "<br>Level: " + pokemon.getStats().getLevel() + "</html>");
			setEnabled(true);
		} else {
			setEnabled(false);
		}
		this.setBackground(Color.WHITE);
		this.setFocusable(false);
	}
}

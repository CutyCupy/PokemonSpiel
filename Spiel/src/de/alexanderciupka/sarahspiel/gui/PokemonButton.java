package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JButton;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

@SuppressWarnings("serial")
public class PokemonButton extends JButton {

	private Pokemon pokemon;
	private int index;
	private GameController gController;
	
	private Image sprite;
	
	private HPBar hpBar;
	private TypeLabel typeOne;
	private TypeLabel typeTwo;
	private AilmentLabel ailment;
	
	public PokemonButton(Pokemon pokemon, int index) {
		super();
		this.setLayout(null);
		gController = GameController.getInstance();
		this.pokemon = pokemon;
		this.setIndex(index);
		hpBar = new HPBar();
		typeOne = new TypeLabel();
		typeTwo = new TypeLabel();
		ailment = new AilmentLabel();
		update();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(pokemon != null) {
			g.drawImage(sprite, 0, 0, null);
			g.setColor(Color.BLACK);
			g.drawString(pokemon.getName() + " - Lvl: " + pokemon.getStats().getLevel(), sprite.getWidth(null) + 5, 25);
			repaint();
		}
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
		for(Component c : this.getComponents()) {
			this.remove(c);
		}
		if(gController.isFighting()) {
			this.pokemon = gController.getFight().getPokemon(this.index);			
		} else {
			this.pokemon = gController.getMainCharacter().getTeam().getTeam()[this.index];
		}
		if(pokemon != null) {
			setEnabled(true);
			this.sprite = this.pokemon.getSpriteFront().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
			hpBar.setMaximum(pokemon.getStats().getStats()[0]);
			hpBar.setValue(pokemon.getStats().getCurrentHP());
			hpBar.setBounds(80, 32, 150, 10);
			
			typeOne.setType(this.pokemon.getTypes()[0]);
			typeOne.setLocation(80, 45);
			
			typeTwo.setType(this.pokemon.getTypes()[1]);
			typeTwo.setLocation(80 + typeOne.getWidth() + 5, 45);
			
			ailment.setAilment(this.pokemon.getAilment());
			ailment.setLocation(hpBar.getWidth() + hpBar.getX() - ailment.getWidth(), hpBar.getY() - ailment.getHeight() - 5);
			
			this.add(hpBar);
			this.add(typeOne);
			this.add(typeTwo);
			this.add(ailment);
			repaint();
		} else {
			setEnabled(false);
		}
		this.setBackground(Color.WHITE);
		this.setFocusable(false);
	}
}

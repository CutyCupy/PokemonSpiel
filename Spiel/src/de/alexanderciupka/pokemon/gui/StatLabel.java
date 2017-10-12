package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class StatLabel extends JLabel {

	private Pokemon pokemon;

	private static final Font NAME_FONT = new Font(Font.DIALOG, Font.BOLD, 12);//MenuController.importFont("/fonts/pokemon_text.ttf").deriveFont(12f).deriveFont(Font.BOLD);

	public StatLabel() {
		setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(2, 10, 2, 10)));
	}

	public void setPokemon(Pokemon p) {
		this.pokemon = p;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(pokemon == null) {
			return;
		}
		g.setFont(NAME_FONT);
		FontMetrics fm = getFontMetrics(NAME_FONT);
		int h = 15;
		g.drawString(pokemon.getName(), 10, h);
		Image img = GameController.getInstance().getInformation().getGenderImage(this.pokemon.getGender());
		if(img != null) {
			g.drawImage(img.getScaledInstance(10, 10, Image.SCALE_SMOOTH), 10 + fm.stringWidth(pokemon.getName()),
					h - 10, null);
		}
		fm = g.getFontMetrics();
		g.drawString("Lv" + this.pokemon.getStats().getLevel(), 125 - fm.stringWidth("Lv100"), h);
	}

	public Pokemon getPokemon() {
		return this.pokemon;
	}

}


package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class StatLabel extends JLabel {

	private HPBar hp;
	private AilmentLabel ailment;
	private Pokemon pokemon;

	private static final Font NAME_FONT = new Font(Font.DIALOG, Font.BOLD, 12);

	public StatLabel() {
		this.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(2, 10, 2, 10)));
		this.setLayout(null);

		this.hp = new HPBar();
		this.ailment = new AilmentLabel();

		this.setBackground(Color.WHITE);
		this.setVerticalAlignment(SwingConstants.TOP);
		this.setOpaque(false);

		this.setSize(180, 40);

		this.ailment.setLocation(this.getWidth() - this.ailment.getWidth() - 10, 5);
		this.hp.setSize(this.getWidth() - 20, 10);
		this.hp.setLocation(10, this.getHeight() - this.hp.getHeight() - 7);

		this.add(this.ailment);
		this.add(this.hp);

		this.setVisible(false);
	}

	public void setPokemon(Pokemon p) {
		this.pokemon = p;
	}

	public Pokemon getPokemon() {
		return this.pokemon;
	}

	@Override
	public boolean isVisible() {
		return GameController.getInstance().getFight().isVisible(this.pokemon);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (this.pokemon == null) {
			return;
		}
		g.setFont(NAME_FONT);
		FontMetrics fm = this.getFontMetrics(NAME_FONT);
		int h = 15;
		g.drawString(this.pokemon.getName(), 10, h);
		Image img = GameController.getInstance().getInformation().getGenderImage(this.pokemon.getGender());
		if (img != null) {
			g.drawImage(img.getScaledInstance(10, 10, Image.SCALE_SMOOTH), 10 + fm.stringWidth(this.pokemon.getName()),
					h - 10, null);
		}
		fm = g.getFontMetrics();
		g.drawString("Lv" + this.pokemon.getStats().getLevel(), 125 - fm.stringWidth("Lv100"), h);
		super.paintComponent(g);
	}

	public HPBar getHPBar() {
		return this.hp;
	}

}

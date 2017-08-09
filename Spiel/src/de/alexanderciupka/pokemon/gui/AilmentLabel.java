package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.alexanderciupka.pokemon.pokemon.Ailment;

public class AilmentLabel extends JLabel {


	private Ailment ailment;

	public AilmentLabel() 	{
		this.setVisible(false);
		this.setSize(40, 15);
		this.setForeground(Color.BLACK);
		this.setOpaque(true);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
		this.ailment = Ailment.NONE;
	}

	public void setAilment(Ailment a) {
		this.ailment = a;
		if(this.ailment != null && !this.ailment.equals(Ailment.NONE)) {
			this.setText(Ailment.getShorttext(this.ailment));
			this.setBackground(Ailment.getColor(this.ailment));
			this.setVisible(true);
		} else {
			this.setVisible(false);
		}
	}





}

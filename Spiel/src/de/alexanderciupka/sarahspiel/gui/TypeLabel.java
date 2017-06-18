package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import de.alexanderciupka.sarahspiel.pokemon.Type;

public class TypeLabel extends JLabel {
	
	private Type currentType;
	
	public TypeLabel() 	{
		this.setVisible(false);
		this.setSize(60, 25);
		this.setForeground(Color.white);
		this.setOpaque(true);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
	}
	
	public void setType(Type t) {
		this.currentType = t;
		if(this.currentType != null) {
			this.setText(this.currentType.toString());
			this.setBackground(Type.getColor(this.currentType));
			this.setVisible(true);
		} else {
			this.setVisible(false);
		}
	}
}

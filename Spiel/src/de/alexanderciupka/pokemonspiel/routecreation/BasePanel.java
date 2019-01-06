package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JPanel;

import com.google.gson.JsonObject;

public abstract class BasePanel extends JPanel {
	

	int x;
	int y;
	
	public BasePanel(int x, int y, JsonObject currentJson) {
		this.x = x;
		this.y = y;
		this.setLayout(null);
	}
	
	public abstract JsonObject getJSON();
	
}

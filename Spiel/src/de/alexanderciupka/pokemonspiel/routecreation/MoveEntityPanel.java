package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.google.gson.JsonObject;
import com.sun.javafx.scene.traversal.Direction;

public class MoveEntityPanel extends EntityPanel {

	protected JComboBox<String> directionBox;

	public MoveEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setSize(this.getWidth(), this.getHeight() + 31);

		JLabel itemLabel = new JLabel("Direction");
		itemLabel.setBounds(10, 515, 115, 20);
		this.add(itemLabel);

		directionBox = new JComboBox<>();
		directionBox.setBounds(178, 515, 226, 20);
		
		for(Direction dir : Direction.values()) {
			this.directionBox.addItem(dir.name());
		}
		
		this.directionBox.setSelectedItem(currentJson.has("direction") ? currentJson.get("direction").getAsString().toUpperCase() : Direction.values()[0].name());
		
		this.add(this.directionBox);
		
		for(int i = 0; i < this.spriteBox.getItemCount(); i++) {
			if(!this.spriteBox.getItemAt(i).contains("move")) {
				this.spriteBox.removeItemAt(i);
				i--;
			}
		}
		
	}

	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = super.getJSON();
		currentJson.addProperty("direction", this.directionBox.getSelectedItem().toString());
		return currentJson;
	}

}

package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.gson.JsonObject;

public class WaterEntityPanel extends EntityPanel {

	private JCheckBox diveableBox;

	public WaterEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setSize(this.getWidth(), this.getHeight() + 31);

		JLabel itemLabel = new JLabel("Diveable?");
		itemLabel.setBounds(10, 515, 115, 20);
		this.add(itemLabel);
		diveableBox = new JCheckBox("");
		diveableBox.setHorizontalAlignment(SwingConstants.CENTER);
		diveableBox.setBounds(178, 515, 115, 20);
		this.add(diveableBox);
		
		this.terrainBox.setSelectedItem("see");
		this.terrainBox.setEnabled(false);
	}

	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = super.getJSON();
		currentJson.addProperty("is_open", this.diveableBox.isSelected());
		return currentJson;
	}


}

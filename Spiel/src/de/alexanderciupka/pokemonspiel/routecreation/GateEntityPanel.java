package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.gson.JsonObject;

public class GateEntityPanel extends EntityPanel {

	protected JCheckBox openBox;

	public GateEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setSize(this.getWidth(), this.getHeight() + 31);

		JLabel itemLabel = new JLabel("Open?");
		itemLabel.setBounds(10, 515, 115, 20);
		this.add(itemLabel);
		openBox = new JCheckBox("");
		openBox.setHorizontalAlignment(SwingConstants.CENTER);
		openBox.setBounds(178, 515, 115, 20);
		openBox.setSelected(currentJson.has("is_open") ? currentJson.get("is_open").getAsBoolean() : false);
		this.add(openBox);
	}

	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = super.getJSON();
		currentJson.addProperty("is_open", this.openBox.isSelected());
		return currentJson;
	}

}

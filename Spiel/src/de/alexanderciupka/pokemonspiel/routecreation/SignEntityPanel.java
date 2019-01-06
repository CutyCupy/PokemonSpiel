package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.google.gson.JsonObject;

public class SignEntityPanel extends EntityPanel {

	protected JTextField signField;

	public SignEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setSize(this.getWidth(), this.getHeight() + 31);

		JLabel itemLabel = new JLabel("Information");
		itemLabel.setBounds(10, 515, 115, 20);
		this.add(itemLabel);

		signField = new JTextField();
		signField.setBounds(178, 515, 226, 20);
		signField.setText(currentJson.has("information") ? currentJson.get("information").getAsString() : "");
		this.add(this.signField);
	}

	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = super.getJSON();
		currentJson.addProperty("information", this.signField.getText());
		return currentJson;
	}

}

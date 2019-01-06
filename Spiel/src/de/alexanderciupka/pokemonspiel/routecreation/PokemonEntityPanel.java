package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.PokemonInformation;

public class PokemonEntityPanel extends EntityPanel {

	protected JTextField itemIDsField;
	protected JTextField badgesField;
	protected JComboBox<String> pokemonIDBox;
	protected JComboBox<Integer> pokemonLevelBox;
	protected JTextField beforeFightField;
	protected JTextField noFightField;

	public PokemonEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);

		this.setSize(this.getWidth(), this.getHeight() + 8 * 31);

		JLabel requierementsLabel = new JLabel("Requirements");
		requierementsLabel.setBounds(10, 515, 115, 20);
		this.add(requierementsLabel);

		JLabel itemsLabel = new JLabel("Items");
		itemsLabel.setBounds(40, 546, 85, 20);
		this.add(itemsLabel);

		this.itemIDsField = new JTextField();
		this.itemIDsField.setBounds(178, 546, 226, 20);
		if(currentJson.has("requirements") && currentJson.getAsJsonObject("requirements").has("items")) {
			for(JsonElement e : currentJson.getAsJsonObject("requirements").getAsJsonArray("items")) {
				itemIDsField.setText(e.getAsJsonObject().get("id").getAsInt() + ",");
			}
		}
		this.add(this.itemIDsField);

		JLabel badgesLabel = new JLabel("Badges");
		badgesLabel.setBounds(40, 577, 85, 20);
		this.add(badgesLabel);

		this.badgesField = new JTextField();
		this.badgesField.setBounds(178, 577, 226, 20);
		if(currentJson.has("requirements") && currentJson.getAsJsonObject("requirements").has("badges")) {
			this.badgesField.setText(String.valueOf(currentJson.getAsJsonObject("requirements").get("badges").getAsInt()));
		}
		this.add(this.badgesField);

		JLabel pokemonLabel = new JLabel("Pokemon");
		pokemonLabel.setBounds(10, 608, 115, 20);
		this.add(pokemonLabel);

		JLabel pokemonIDLabel = new JLabel("ID");
		pokemonIDLabel.setBounds(40, 639, 85, 20);
		this.add(pokemonIDLabel);

		this.pokemonIDBox = new JComboBox<>();
		PokemonInformation info = GameController.getInstance().getInformation();
		for (int i = 1; info.getName(i) != null; i++) {
			pokemonIDBox.addItem(info.getName(i));
		}
		this.pokemonIDBox.setBounds(178, 639, 226, 20);
		this.pokemonIDBox
				.setSelectedItem(currentJson.has("pokemon") && currentJson.get("pokemon").getAsJsonObject().has("id")
						? info.getName(currentJson.get("pokemon").getAsJsonObject().get("id").getAsInt())
						: info.getName(1));
		this.add(this.pokemonIDBox);

		JLabel pokemonLevelLabel = new JLabel("Level");
		pokemonLevelLabel.setBounds(40, 670, 85, 20);
		this.add(pokemonLevelLabel);

		this.pokemonLevelBox = new JComboBox<>();
		for (int i = 1; i <= 100; i++) {
			this.pokemonLevelBox.addItem(i);
		}
		this.pokemonLevelBox.setBounds(178, 670, 226, 20);
		this.pokemonLevelBox.setSelectedItem(currentJson.has("pokemon")
				&& currentJson.get("pokemon").getAsJsonObject().has("stats")
				&& currentJson.get("pokemon").getAsJsonObject().get("stats").getAsJsonObject().has("level")
						? currentJson.getAsJsonObject("pokemon").getAsJsonObject("stats").get("level").getAsInt() - 1
						: 4);
		this.add(this.pokemonLevelBox);

		JLabel beforeFightLabel = new JLabel("Before Fight");
		beforeFightLabel.setBounds(10, 701, 115, 20);
		this.add(beforeFightLabel);

		this.beforeFightField = new JTextField();
		this.beforeFightField.setBounds(178, 701, 226, 20);
		if (currentJson.has("before_fight")) {
			this.beforeFightField.setText(currentJson.get("before_fight").getAsString());
		}
		this.add(this.beforeFightField);

		JLabel noFightLabel = new JLabel("No Fight");
		noFightLabel.setBounds(10, 732, 115, 20);
		this.add(noFightLabel);

		this.noFightField = new JTextField();
		this.noFightField.setBounds(178, 732, 226, 20);
		if (currentJson.has("no_fight")) {
			this.noFightField.setText(currentJson.get("no_fight").getAsString());
		}
		this.add(this.noFightField);
	}

	@Override
	public JsonObject getJSON() {
		JsonObject json = super.getJSON();

		JsonObject requirements = new JsonObject();
		JsonArray items = new JsonArray();
		for (String s : this.itemIDsField.getText().split(",")) {
			if (s.isEmpty()) {
				continue;
			}
			JsonObject item = new JsonObject();
			item.addProperty("id", s);
			items.add(item);
		}
		requirements.add("items", items);
		requirements.addProperty("badges", this.badgesField.getText());
		json.add("requirements", requirements);

		JsonObject pokemon = new JsonObject();
		pokemon.addProperty("id",
				GameController.getInstance().getInformation().getID(this.pokemonIDBox.getSelectedItem().toString()));
		JsonObject stats = new JsonObject();
		stats.addProperty("level", this.pokemonLevelBox.getSelectedItem().toString());
		stats.add("dvev", new JsonArray());
		pokemon.add("stats", stats);

		json.add("pokemon", pokemon);

		json.addProperty("before_fight", this.beforeFightField.getText());
		json.addProperty("no_fight", this.noFightField.getText());

		return json;
	}

}

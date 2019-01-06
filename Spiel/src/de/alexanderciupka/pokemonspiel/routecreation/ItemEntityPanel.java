package de.alexanderciupka.pokemonspiel.routecreation;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Items;


public class ItemEntityPanel extends EntityPanel {
	
	protected JComboBox<String> itemBox;
	protected JCheckBox hiddenCheck;
	

	public ItemEntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setSize(this.getWidth(), this.getHeight() + 62);
		
		JLabel itemLabel = new JLabel("Item");
		itemLabel.setBounds(10, 515, 115, 20);
		this.add(itemLabel);

		itemBox = new JComboBox<>();
		itemBox.setBounds(178, 515, 226, 20);
		
		boolean add = false;
		for(java.lang.reflect.Field f : Items.class.getDeclaredFields()) {
			if(!add) {
				add = f.toString().contains("KEINS");
				continue;
			}
			itemBox.addItem(f.getName());
		}
		this.add(this.itemBox);
		
		JLabel hiddenLabel = new JLabel("hidden");
		hiddenLabel.setBounds(10, 546, 115, 20);
		this.add(hiddenLabel);
		
		hiddenCheck = new JCheckBox("");
		hiddenCheck.setHorizontalAlignment(SwingConstants.CENTER);
		hiddenCheck.setBounds(178, 546, 115, 20);
		hiddenCheck.setSelected(currentJson.has("hidden") ? currentJson.get("hidden").getAsBoolean() : false);
		this.add(hiddenCheck);
		
	}
	
	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = super.getJSON();
		for(java.lang.reflect.Field f : Items.class.getDeclaredFields()) {
			if(f.getName().equals(this.itemBox.getSelectedItem().toString())) {
				try {
					currentJson.addProperty("item", f.getInt(null));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				};
				break;
			}
		}
		currentJson.addProperty("hidden", this.hiddenCheck.isSelected());
		return currentJson;
	}

	
}



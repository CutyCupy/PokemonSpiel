package de.alexanderciupka.pokemon.characters.types;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Items;

public class Salesman extends NPC {
	
	private HashMap<Integer, Integer> itemPrices;
	
	
	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty(DIALOGUE_ON_EXIT, this.dialogues.get(DIALOGUE_ON_EXIT));
		saveData.addProperty(DIALOGUE_ON_ACTION, this.dialogues.get(DIALOGUE_ON_ACTION));
		
		JsonArray items = new JsonArray();
		for(Integer i : itemPrices.keySet()) {
			JsonObject item = new JsonObject();
			item.addProperty("item", i);
			item.addProperty("price", this.itemPrices.get(i));
			
			items.add(item);
		}
		saveData.add("items", items);
		
		return saveData;
	}
	
	@Override
	public boolean importSaveData(JsonObject saveData) {
		if(super.importSaveData(saveData)) {
			this.dialogues.put(DIALOGUE_ON_EXIT, saveData.get(DIALOGUE_ON_EXIT).getAsString());
			this.dialogues.put(DIALOGUE_ON_ACTION, saveData.get(DIALOGUE_ON_ACTION).getAsString());
			
			if(this.itemPrices == null) {
				this.itemPrices = new HashMap<>();
			}
			for(JsonElement e : saveData.get("items").getAsJsonArray()) {
				JsonObject currentItem = e.getAsJsonObject();
				int item = currentItem.get("item").getAsInt();
				int cost = this.gController.getInformation().getItemData(Items.ITEM_COST, item).getAsInt();
				if(currentItem.has("price")) {
					cost = currentItem.get("price").getAsInt();
				}
				this.itemPrices.put(item, cost);
			}
			
			return true;
		}
		return false;
	}
	
	
	
	
}

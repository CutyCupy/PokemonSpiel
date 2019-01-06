package de.alexanderciupka.pokemon.characters.types;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class RewardNPC extends NPC {

	private HashMap<Integer, Integer> itemRewards;
	private Pokemon pokemonReward;

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty(DIALOGUE_ON_ACTION, this.dialogues.get(DIALOGUE_ON_ACTION));

		JsonObject rewards = new JsonObject();
		JsonArray items = new JsonArray();
		for (Integer i : this.itemRewards.keySet()) {
			int amount = this.itemRewards.get(i);
			if (amount == 0) {
				continue;
			}
			JsonObject currentItem = new JsonObject();
			currentItem.addProperty("id", i);
			currentItem.addProperty("amount", amount);
			items.add(currentItem);
		}
		rewards.add("items", items);
		if(this.pokemonReward != null) {
			rewards.add("pokemon", this.pokemonReward.getSaveData());
		}
		
		saveData.add("rewards", rewards);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.dialogues.put(DIALOGUE_ON_ACTION, saveData.get(DIALOGUE_ON_ACTION).getAsString());
			this.itemRewards = new HashMap<>();
			this.pokemonReward = null;
			if (saveData.has("rewards")) {
				saveData = saveData.get("rewards").getAsJsonObject();
				for (JsonElement j : saveData.get("items").getAsJsonArray()) {
					if (j.getAsJsonObject().get("amount") != null) {
						this.itemRewards.put(j.getAsJsonObject().get("id").getAsInt(),
								j.getAsJsonObject().get("amount").getAsInt());
					}
				}
				this.pokemonReward = saveData.has("pokemon")
						? Pokemon.importSaveData(saveData.get("pokemon").getAsJsonObject()) : null;
			}
			return true;
		}
		return false;
	}

}

package de.alexanderciupka.pokemon.characters.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class QuizNPC extends RewardNPC {
	
	
	private JsonArray questions;

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty(NPC.DIALOGUE_BEFORE_ACTION, this.dialogues.get(NPC.DIALOGUE_BEFORE_ACTION));
		saveData.addProperty(NPC.DIALOGUE_AFTER_ACTION, this.dialogues.get(NPC.DIALOGUE_AFTER_ACTION));
		saveData.add("questions", questions);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.dialogues.put(NPC.DIALOGUE_BEFORE_ACTION, saveData.get(NPC.DIALOGUE_BEFORE_ACTION).getAsString());
			this.dialogues.put(NPC.DIALOGUE_AFTER_ACTION, saveData.get(NPC.DIALOGUE_AFTER_ACTION).getAsString());
			this.questions = saveData.get("questions").getAsJsonArray();
			return true;
		}
		return false;
	}

}

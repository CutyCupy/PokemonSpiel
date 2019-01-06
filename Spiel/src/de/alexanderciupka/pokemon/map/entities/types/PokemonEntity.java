package de.alexanderciupka.pokemon.map.entities.types;

import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class PokemonEntity extends Entity {

	private ArrayList<Integer> requiredItems;
	private int requiredBadges;

	private String noInteractionMessage;
	private String interactionMessage;
	private Pokemon pokemon;

	public PokemonEntity(Route parent, String terrainName) {
		super(parent, false, "25", 0, terrainName);
	}

	public void setRequiredItems(Integer... items) {
		this.requiredItems = new ArrayList<Integer>(items.length);
		for (Integer i : items) {
			this.addRequiredItem(i);
		}
	}

	public void addRequiredItem(Integer item) {
		if (this.requiredItems == null) {
			this.requiredItems = new ArrayList<Integer>();
		}
		this.requiredItems.add(item);
	}

	public void clearRequiredItems() {
		this.requiredItems = new ArrayList<>();
	}

	public int getRequiredBadges() {
		return this.requiredBadges;
	}

	public void setRequiredBadges(int badges) {
		this.requiredBadges = badges;
	}

	public ArrayList<Integer> getRequiredItems() {
		return this.requiredItems;
	}

	public String getNoInteractionMessage() {
		return this.noInteractionMessage;
	}

	public void setNoInteractionMessage(String noInteractionMessage) {
		this.noInteractionMessage = noInteractionMessage;
	}

	public String getInteractionMessage() {
		return this.interactionMessage;
	}

	public void setInteractionMessage(String interactionMessage) {
		this.interactionMessage = interactionMessage;
	}

	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
		if (pokemon != null) {
			this.setSprite(String.valueOf(pokemon.getId()));
		} else {
			super.setSprite("free");
			this.setAccessible(true);
		}
	}

	@Override
	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		try {
			this.sprite = ImageIO
					.read(this.getClass().getResourceAsStream("/characters/pokemon/" + spriteName + "/front_0.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Pokemon getPokemon() {
		return this.pokemon;
	}

	@Override
	public void onInteraction(Player c) {
		boolean isInteractable = true;
		if (this.requiredItems != null) {
			for (Integer i : this.requiredItems) {
				if (!c.hasItem(i)) {
					isInteractable = false;
					break;
				}
			}
		}
		if (isInteractable) {
			if (!this.interactionMessage.isEmpty()) {
				this.gController.getGameFrame().addDialogue(this.interactionMessage);
				this.gController.waitDialogue();
			}
			this.gController.startFight(this.pokemon);
			this.setPokemon(null);
		} else {
			if (!this.noInteractionMessage.isEmpty()) {
				this.gController.getGameFrame().addDialogue(this.noInteractionMessage);
				this.gController.waitDialogue();
			}
		}
	}

	public void importRequiredItems(JsonElement je) {
		this.requiredItems = new ArrayList<Integer>();
		if (je != null) {
			JsonArray items = je.getAsJsonArray();
			for (JsonElement i : items) {
				try {
					this.addRequiredItem(i.getAsJsonObject().get("item").getAsInt());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj) && obj instanceof PokemonEntity) {
			PokemonEntity other = (PokemonEntity) obj;
			return this.interactionMessage.equals(other.interactionMessage)
					&& this.noInteractionMessage.equals(other.noInteractionMessage)
					&& this.requiredItems.equals(other.requiredItems)
					&& (this.pokemon == null ? other.pokemon == null : this.pokemon.equals(other.pokemon))
					&& this.requiredBadges == other.requiredBadges && this.requiredItems.equals(other.requiredItems);
		}
		return false;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		
		JsonObject requirements = new JsonObject();
		JsonArray items = new JsonArray();
		for (Integer item : this.requiredItems) {
			JsonObject current = new JsonObject();
			current.addProperty("id", item);
			items.add(current);
		}
		requirements.add("items", items);
		requirements.addProperty("badges", this.requiredBadges);
		
		saveData.add("requirements", requirements);
		
		if (this.pokemon != null) {
			saveData.add("pokemon", this.pokemon.getSaveData());
		}
		
		saveData.addProperty("before_fight", this.interactionMessage);
		saveData.addProperty("no_fight", this.noInteractionMessage);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		if (super.importSaveData(saveData)) {
			this.requiredItems = new ArrayList<>();
			this.requiredBadges = 0;
			JsonObject requirements = saveData.get("requirements").getAsJsonObject();
			if (requirements.has("items")) {
				for (JsonElement e : requirements.get("items").getAsJsonArray()) {
					JsonObject item = e.getAsJsonObject();
					for (int i = 0; i < item.get("amount").getAsInt(); i++) {
						this.requiredItems.add(item.get("id").getAsInt());
					}
				}
			}
			if (requirements.has("badges")) {
				this.requiredBadges = requirements.get("badges").getAsInt();
			}
			if(saveData.has("pokemon")) {
				this.pokemon = Pokemon.importSaveData(saveData.get("pokemon").getAsJsonObject());
			}
			this.interactionMessage = saveData.get("before_fight").getAsString();
			this.noInteractionMessage = saveData.get("no_fight").getAsString();
			return true;
		}
		return false;
	}
}

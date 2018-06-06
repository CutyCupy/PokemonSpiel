package de.alexanderciupka.pokemon.map.entities;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class PokemonEntity extends Entity {

	private ArrayList<Item> requiredItems;

	private String noInteractionMessage;
	private String interactionMessage;
	private Pokemon pokemon;

	public PokemonEntity(Route parent, String terrainName) {
		super(parent, false, "25", 0, terrainName);
	}

	public void setRequiredItems(Item... items) {
		this.requiredItems = new ArrayList<Item>(items.length);
		for (Item i : items) {
			this.addRequiredItem(i);
		}
	}

	public void addRequiredItem(Item item) {
		if (item == null) {
			return;
		}
		if (this.requiredItems == null) {
			this.requiredItems = new ArrayList<Item>();
		}
		this.requiredItems.add(item);
	}

	public ArrayList<Item> getRequiredItems() {
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
			this.sprite = ImageIO.read(
					new File(Main.class.getResource("/characters/pokemon/" + spriteName + "/front_0.png").getFile()));
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
			for (Item i : this.requiredItems) {
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
			c.getCurrentRoute().updateMap(new Point(this.getX(), this.getY()));
		} else {
			if (!this.noInteractionMessage.isEmpty()) {
				this.gController.getGameFrame().addDialogue(this.noInteractionMessage);
				this.gController.waitDialogue();
			}
		}
	}

	public void importRequiredItems(JsonElement je) {
		this.requiredItems = new ArrayList<Item>();
		if (je != null) {
			JsonArray items = je.getAsJsonArray();
			for (JsonElement i : items) {
				try {
					this.addRequiredItem(Item.valueOf(i.getAsJsonObject().get("item").getAsString().toUpperCase()));
				} catch (Exception e) {
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
					&& (this.pokemon == null ? other.pokemon == null : this.pokemon.equals(other.pokemon));
		}
		return false;
	}

	@Override
	public JsonObject getSaveData(Entity entity) {
		JsonObject saveData = super.getSaveData(entity);
		PokemonEntity other = (PokemonEntity) entity;
		if (!other.interactionMessage.equals(this.interactionMessage)) {
			saveData.addProperty("interaction_message", this.interactionMessage);
		}
		if (!other.noInteractionMessage.equals(this.noInteractionMessage)) {
			saveData.addProperty("no_interaction_message", this.noInteractionMessage);
		}
		if (this.pokemon != null) {
			saveData.add("pokemon", this.pokemon.getSaveData());
		}
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if (super.importSaveData(saveData, entity)) {
			PokemonEntity other = (PokemonEntity) entity;
			if (saveData.get("interaction_message") != null) {
				this.interactionMessage = saveData.get("interaction_message").getAsString();
			} else {
				this.interactionMessage = other.interactionMessage;
			}
			if (saveData.get("no_interaction_message") != null) {
				this.noInteractionMessage = saveData.get("no_interaction_message").getAsString();
			} else {
				this.noInteractionMessage = other.noInteractionMessage;
			}
			if (saveData.get("pokemon") != null) {
				this.pokemon = Pokemon.importSaveData(saveData.get("pokemon").getAsJsonObject());
			} else {
				this.pokemon = null;
			}
			return true;
		}
		return false;
	}

	public static PokemonEntity convert(Entity entity) {
		PokemonEntity result = new PokemonEntity(entity.getRoute(), entity.getTerrainName());
		if (entity.getWarp() != null) {
			result.addWarp(entity.getWarp().clone());
		}
		result.setAccessible(false);
		result.setSprite(entity.getSpriteName());
		result.setEncounterRate(entity.getEncounterRate());
		result.setWater(entity.isWater());
		result.setEvent(entity.getEvent() == null ? null : entity.getEvent().clone());
		result.setX(entity.getX());
		result.setY(entity.getY());
		return result;
	}
}

package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import de.alexanderciupka.sarahspiel.pokemon.Item;
import de.alexanderciupka.sarahspiel.pokemon.Player;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

public class PokemonEntity extends Entity {
	
	private ArrayList<Item> requiredItems;
	private GameController gController;
	
	private String noInteractionMessage;
	private String interactionMessage;
	private Pokemon pokemon;
	private String id;

	public PokemonEntity(String terrainName, String id) {
		super(false, "free", 0, terrainName);
		gController = GameController.getInstance();
		this.setId(id);
	}

	public void setRequiredItems(Item... items) {
		requiredItems = new ArrayList<Item>(items.length);
		for(Item i : items) {
			requiredItems.add(i);
		}
	}
	
	public void addRequiredItem(Item item) {
		if(requiredItems == null) {
			requiredItems = new ArrayList<Item>();
		}
		requiredItems.add(item);
	}
	
	public ArrayList<Item> getRequiredItems() {
		return this.requiredItems;
	}
	
	
	public String getNoInteractionMessage() {
		return noInteractionMessage;
	}

	public void setNoInteractionMessage(String noInteractionMessage) {
		this.noInteractionMessage = noInteractionMessage;
	}
	
	public String getInteractionMessage() {
		return interactionMessage;
	}

	public void setInteractionMessage(String interactionMessage) {
		this.interactionMessage = interactionMessage;
	}

	public void setPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
		if(pokemon != null) {
			this.setSprite(pokemon.getName().toLowerCase());
		} else {
			this.setSprite("free");
			this.setAccessible(true);
		}
	}
	
	public Pokemon getPokemon() {
		return this.pokemon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void onInteraction(Player c) {
		boolean isInteractable = true;
		if(requiredItems != null) {
			for(Item i : this.requiredItems) {
				if(!c.hasItem(i)) {
					isInteractable = false;
				}
			}
		}
		if(isInteractable) {
			if(!interactionMessage.isEmpty()) {
				gController.getGameFrame().addDialogue(interactionMessage);
				gController.waitDialogue();
			}
			gController.startFight(this.pokemon);
			setPokemon(null);
			c.getCurrentRoute().updateMap(new Point(getX(), getY()));
		} else {
			if(!noInteractionMessage.isEmpty()) {
				gController.getGameFrame().addDialogue(noInteractionMessage);
				gController.waitDialogue();
			}
		}
	}

	public void importRequiredItems(JsonElement je) {
		this.requiredItems = new ArrayList<Item>();
		if(je != null && je.isJsonArray()) {
			JsonArray items = je.getAsJsonArray();
		}
	}
}

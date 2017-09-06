package de.alexanderciupka.pokemon.map.entities;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
	private String id;

	public PokemonEntity(Route parent, String terrainName, String id) {
		super(parent, false, "25", 0, terrainName);
		this.setId(id);
	}

	public void setRequiredItems(Item... items) {
		requiredItems = new ArrayList<Item>(items.length);
		for(Item i : items) {
			addRequiredItem(i);
		}
	}

	public void addRequiredItem(Item item) {
		if(item == null) {return;}
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
			this.sprite = ImageIO.read(new File(Main.class.getResource("/characters/pokemon/" + spriteName + "/front_0.png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
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
					break;
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
		gController.getGameFrame().repaint();
	}

	public void importRequiredItems(JsonElement je) {
		this.requiredItems = new ArrayList<Item>();
		if(je != null) {
			JsonArray items = je.getAsJsonArray();
			for(JsonElement i : items) {
				try {
					this.addRequiredItem(Item.valueOf(i.getAsJsonObject().get("item").getAsString().toUpperCase()));
				} catch(Exception e) {}
			}
		}
	}
}

package de.alexanderciupka.pokemon.map;

import java.awt.Image;
import java.awt.Point;

import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.pokemon.Item;

public class ItemEntity extends Entity {

	private String id;
	private Item item;
	
	public ItemEntity(Route parent, String terrainName, String id) {
		super(parent, false, "pokeball", 0, terrainName);
		this.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
		if(item != null) {
			this.setSprite(item.name().toLowerCase());
		} else {
			super.setSprite("free");
			this.setAccessible(true);
		}
	}
	
	@Override
	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		this.sprite = gController.getRouteAnalyzer().getItemImage(Item.valueOf(spriteName.toUpperCase())).getScaledInstance(70, 70, Image.SCALE_SMOOTH);
	}
	
	@Override
	public void onInteraction(Player c) {
		if(item != null) {
			gController.getGameFrame().addDialogue("Du findest einen " + this.item.getName() + "!");
			gController.waitDialogue();
			setItem(null);
			c.getCurrentRoute().updateMap(new Point(getX(), getY()));
		} else {
			super.onInteraction(c);
		}
		gController.getGameFrame().repaint();
	}
	
}

package de.alexanderciupka.pokemon.map.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.pokemon.Item;

public class ItemEntity extends Entity {

	private String id;
	private Item item;
	private boolean hidden;
	
	public ItemEntity(Route parent, String terrainName, String id, boolean hidden) {
		super(parent, hidden, "warp", 0, terrainName);
		this.hidden = hidden;
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
			if(this.hidden) {
				this.setSprite(Item.NONE, this.spriteName);
			} else {
				this.setSprite(item, this.spriteName);
			}
			this.setAccessible(this.hidden);
		} else {
			this.setSprite(Item.NONE, this.spriteName);
			this.setAccessible(true);
		}
	}
	
	@Override
	public void setSprite(String spriteName) {
		if(this.item != null) {
			setSprite(this.item, spriteName);
		} else {
			super.setSprite(spriteName);
		}
	}
	
	private void setSprite(Item item, String spriteName) {
		super.setSprite(spriteName);
		if(!hidden) {
			Image temp = new BufferedImage(this.sprite.getWidth(null), this.sprite.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = temp.getGraphics();
			g.drawImage(this.sprite, 0, 0, null);
			g.drawImage(gController.getRouteAnalyzer().getItemImage(item).getScaledInstance(70, 70, Image.SCALE_SMOOTH), 0, 0, null);
			this.sprite = temp;
		}
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		if(this.item != null) {
			this.setAccessible(this.hidden);
		} else {
			this.setAccessible(true);
		}
		setSprite(item, spriteName);
	}

	@Override
	public void onInteraction(Player c) {
		if(item != null) {
			c.addItem(this.item);
			gController.getGameFrame().addDialogue("Du hast einen " + this.item.getName() + " gefunden!");
			gController.waitDialogue();
			setItem(null);
			c.getCurrentRoute().updateMap(new Point(getX(), getY()));
		} else {
			super.onInteraction(c);
		}
		gController.getGameFrame().repaint();
	}
	
}

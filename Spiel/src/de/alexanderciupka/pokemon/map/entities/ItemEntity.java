package de.alexanderciupka.pokemon.map.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

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
//		gController.getGameFrame().repaint();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemEntity) {
			ItemEntity other = (ItemEntity) obj;
			return ((this.item == null && other.item == null) || (this.item != null && this.item.equals(other.item))) &&
					(this.hidden == other.hidden) && super.equals(obj);
		}
		return false;
	}

	@Override
	public JsonObject getSaveData(Entity entity) {
		ItemEntity origin = (ItemEntity) entity;
		JsonObject saveData = super.getSaveData(entity);
		saveData.addProperty("id", id);
		if((this.item != null || origin.item != null) || (this.item != null && !this.item.equals(origin.item))) {
			saveData.addProperty("item", this.item != null ? this.item.name() : null);
		}
		if(this.hidden != origin.hidden) {
			saveData.addProperty("hidden", this.hidden);
		}
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if(super.importSaveData(saveData, entity) && entity instanceof ItemEntity) {
			ItemEntity other = (ItemEntity) entity;
			if(saveData.get("id") != null) {
				this.id = saveData.get("id").getAsString();
			} else {
				this.id = other.id;
			}
			if(saveData.get("item") != null) {
				setItem(saveData.get("item") instanceof JsonNull ? null :
					Item.valueOf(saveData.get("item").getAsString().toUpperCase()));
			} else {
				setItem(other.item);
			}
			if(saveData.get("hidden") != null) {
				this.hidden = saveData.get("hidden").getAsBoolean();
			} else {
				this.hidden = other.hidden;
			}
			return true;
		}
		return false;
	}

}

package de.alexanderciupka.pokemon.map.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.Route;

public class ItemEntity extends Entity {

	private Integer item;
	private boolean hidden;

	public ItemEntity(Route parent, String terrainName, boolean hidden) {
		super(parent, hidden, "free", 0, terrainName);
		this.hidden = hidden;
	}

	public Integer getItem() {
		return this.item;
	}

	public void setItem(Integer item) {
		this.item = item;
		if (item != null) {
			if (this.hidden) {
				this.setSprite(Items.KEINS, this.spriteName);
			} else {
				this.setSprite(item, this.spriteName);
			}
			this.setAccessible(this.hidden);
		} else {
			this.setSprite(Items.KEINS, this.spriteName);
			this.setAccessible(true);
		}
	}

	@Override
	public void setSprite(String spriteName) {
		if (this.item != null) {
			this.setSprite(this.item, spriteName);
		} else {
			super.setSprite(spriteName);
		}
	}

	private void setSprite(Integer item, String spriteName) {
		super.setSprite(spriteName);
		if (!this.hidden) {
			Image temp = new BufferedImage(this.sprite.getWidth(null), this.sprite.getHeight(null),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = temp.getGraphics();
			g.drawImage(this.sprite, 0, 0, null);
			g.drawImage(this.gController.getRouteAnalyzer().getItemImage(item).getScaledInstance(GameFrame.GRID_SIZE,
					GameFrame.GRID_SIZE, Image.SCALE_SMOOTH), 0, 0, null);
			this.sprite = temp;
		}
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		if (this.item != null) {
			this.setAccessible(this.hidden);
		} else {
			this.setAccessible(true);
		}
		this.setSprite(this.item, this.spriteName);
	}

	@Override
	public void onInteraction(Player c) {
		if (this.item != null) {
			c.addItem(this.item);
			this.gController.getGameFrame().addDialogue("Du hast einen " + this.gController.getInformation().getItemData(Items.ITEM_NAME, this.item).toString() + " gefunden!");
			this.gController.waitDialogue();
			this.setItem(null);
			c.getCurrentRoute().updateMap(new Point(this.getX(), this.getY()));
		} else {
			super.onInteraction(c);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemEntity) {
			ItemEntity other = (ItemEntity) obj;
			return ((this.item == null && other.item == null) || (this.item != null && this.item.equals(other.item)))
					&& (this.hidden == other.hidden) && super.equals(obj);
		}
		return false;
	}

	@Override
	public JsonObject getSaveData(Entity entity) {
		ItemEntity origin = (ItemEntity) entity;
		JsonObject saveData = super.getSaveData(entity);
		if ((this.item != null || origin.item != null) || (this.item != null && !this.item.equals(origin.item))) {
			saveData.addProperty("item", this.item != null ? this.item : 0);
		}
		if (this.hidden != origin.hidden) {
			saveData.addProperty("hidden", this.hidden);
		}
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if (super.importSaveData(saveData, entity) && entity instanceof ItemEntity) {
			ItemEntity other = (ItemEntity) entity;
			if (saveData.get("item") != null) {
				this.setItem(saveData.get("item") instanceof JsonNull ? Items.KEINS
						: saveData.get("item").getAsInt());
			} else {
				this.setItem(other.item);
			}
			if (saveData.get("hidden") != null) {
				this.hidden = saveData.get("hidden").getAsBoolean();
			} else {
				this.hidden = other.hidden;
			}
			return true;
		}
		return false;
	}

	public static ItemEntity convert(Entity entity) {
		ItemEntity result = new ItemEntity(entity.getRoute(), entity.getTerrainName(), false);
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

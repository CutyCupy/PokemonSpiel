package de.alexanderciupka.pokemon.map.entities.types;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.Route;

public class ItemEntity extends Entity {

	private int item = Items.KEINS;
	private boolean hidden;
	
	public ItemEntity() {
		super();
	}

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
		} else {
			this.setSprite(Items.KEINS, this.spriteName);
		}
	}
	
	@Override
	public boolean isAccessible(Character c) {
		return super.isAccessible(c) && (this.item == Items.KEINS || this.hidden);
	}

	@Override
	public void setSprite(String spriteName) {
		if (this.item != Items.KEINS) {
			this.setSprite(this.item, spriteName);
		} else {
			super.setSprite(spriteName);
		}
	}

	private void setSprite(Integer item, String spriteName) {
		super.setSprite(spriteName);
		if (!this.hidden && item != Items.KEINS) {
			System.out.println(item);
			Image temp = new BufferedImage(this.sprite.getWidth(null), this.sprite.getHeight(null),
					BufferedImage.TYPE_4BYTE_ABGR);
			Graphics g = temp.getGraphics();
			g.drawImage(this.sprite, 0, 0, null);
			g.drawImage(this.gController.getRouteAnalyzer().getItemImage(item).getScaledInstance(GameFrame.GRID_SIZE / 2,
					GameFrame.GRID_SIZE / 2, Image.SCALE_SMOOTH), GameFrame.GRID_SIZE / 4, GameFrame.GRID_SIZE / 4, null);
			this.sprite = temp;
		}
	}
	
	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		this.setSprite(this.item, this.spriteName);
	}

	@Override
	public void onInteraction(Player c) {
		if (this.item != Items.KEINS) {
			c.addItem(this.item);
			this.gController.getGameFrame()
					.addDialogue("Du hast einen "
							+ this.gController.getInformation().getItemData(Items.ITEM_NAME, this.item).toString().replace("\"", "")
							+ " gefunden!");
			this.gController.waitDialogue();
			this.setItem(Items.KEINS);
		} else {
			super.onInteraction(c);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemEntity) {
			ItemEntity other = (ItemEntity) obj;
			return (this.item == other.item) && (this.hidden == other.hidden) && super.equals(obj);
		}
		return false;
	}

	@Override
	public JsonObject getSaveData() {
		JsonObject saveData = super.getSaveData();
		saveData.addProperty("item", this.item);
		saveData.addProperty("hidden", this.hidden);
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData) {
		System.out.println(saveData);
		if (super.importSaveData(saveData)) {
			if (saveData.get("item") != null) {
				this.setItem(!saveData.has("item") ? Items.KEINS : saveData.get("item").getAsInt());
			}
			if (saveData.get("hidden") != null) {
				this.setHidden(saveData.get("hidden").getAsBoolean());
			}
			return true;
		}
		return false;
	}
}

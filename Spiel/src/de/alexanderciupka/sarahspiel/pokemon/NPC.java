package de.alexanderciupka.sarahspiel.pokemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NPC extends Character {


	private File teamFile;
	private File dialogueFile;
	private String beforeFight;
	private String noFight;
	private String onDefeat;
	private Item reward;

	public NPC() {
		super();
	}

	public NPC(String currentString) {
		super(currentString);
	}

	public void resetPosition() {
		if (!currentPosition.equals(originalPosition)) {
			currentRoute.getEntities()[currentPosition.y][currentPosition.x].removeCharacter();
			currentRoute.getEntities()[originalPosition.y][originalPosition.x].addCharacter(this);
			currentRoute.updateMap(originalPosition, currentPosition);
			setCurrentPosition(originalPosition);
		}
		if(currentDirection != originalDirection) {
			setCurrentDirection(originalDirection);
			currentRoute.updateMap(currentPosition);
		}
	}

	public void faceTowardsMainCharacter() {
		switch (gController.getMainCharacter().getCurrentDirection()) {
		case UP:
			setCurrentDirection(de.alexanderciupka.sarahspiel.pokemon.Direction.DOWN);
			break;
		case RIGHT:
			setCurrentDirection(de.alexanderciupka.sarahspiel.pokemon.Direction.LEFT);
			break;
		case DOWN:
			setCurrentDirection(de.alexanderciupka.sarahspiel.pokemon.Direction.UP);
			break;
		case LEFT:
			setCurrentDirection(de.alexanderciupka.sarahspiel.pokemon.Direction.RIGHT);
			break;
		}
		currentRoute.updateMap(currentPosition);
		gController.repaint();
	}

	public String getBeforeFightDialogue() {
		return this.beforeFight;
	}

	public String getNoFightDialogue() {
		return this.noFight;
	}

	public boolean moveTowardsMainCharacter() {
		int mainX = gController.getMainCharacter().getCurrentPosition().x;
		int mainY = gController.getMainCharacter().getCurrentPosition().y;
		if (currentPosition.x != mainX ^ currentPosition.y != mainY) {
			int x = 0;
			int y = 0;
			switch (currentDirection) {
			case DOWN:
				if (mainX != currentPosition.x || currentPosition.y > mainY) {
					return false;
				}
				y = 1;
				break;
			case LEFT:
				if (mainY != currentPosition.y || currentPosition.x < mainX) {
					return false;
				}
				x = -1;
				break;
			case RIGHT:
				if (mainY != currentPosition.y || currentPosition.x > mainX) {
					return false;
				}
				x = 1;
				break;
			case UP:
				if (mainX != currentPosition.x || currentPosition.y < mainY) {
					return false;
				}
				y = -1;
				break;
			}
			for(int i = 1; i < 8; i++) {
				if(currentPosition.x + (i*x) == mainX && currentPosition.y + (i*y) == mainY) {
					break;
				} else if(!currentRoute.getEntities()[currentPosition.y + (i*y)][currentPosition.x + (i*x)].isAccessible(this)) {
					return false;
				}
			}
			switch (this.getCurrentDirection()) {
			case DOWN:
				gController.getMainCharacter().setCurrentDirection(Direction.UP);
				break;
			case LEFT:
				gController.getMainCharacter().setCurrentDirection(Direction.RIGHT);
				break;
			case RIGHT:
				gController.getMainCharacter().setCurrentDirection(Direction.LEFT);
				break;
			case UP:
				gController.getMainCharacter().setCurrentDirection(Direction.DOWN);
				break;
			}
			gController.getGameFrame().repaint();
			while (!(currentPosition.x + x == mainX && currentPosition.y + y == mainY)) {
				currentRoute.getEntities()[currentPosition.y][currentPosition.x].removeCharacter();
				currentRoute.updateMap(currentPosition);
				currentRoute.getEntities()[currentPosition.y + y][currentPosition.x + x].addCharacter(this);
				this.changePosition(this.getCurrentDirection(), true);
				currentRoute.updateMap(currentPosition);
			}
			System.err.println("finished");
			return true;
		}
		return false;
	}

	@Override
	public void setCurrentDirection(Direction direction) {
		super.setCurrentDirection(direction);
		if(currentRoute != null) {
			this.currentRoute.updateMap(this.currentPosition);
		}
	}

	public void importTeam() {
		try {
			teamFile = new File(
					this.getClass().getResource("/characters/teams/" + this.currentRoute.getId() + "/" + getFileName() + ".txt").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(teamFile));
			String currentLine;
			while ((currentLine = reader.readLine()) != null) {
				String[] rowElements = currentLine.split(",");
				Pokemon currentPokemon = new Pokemon(Integer.parseInt(rowElements[0]));
				currentPokemon.getStats().generateStats(Short.parseShort(rowElements[1]));
				this.team.addPokemon(currentPokemon);
			}
			trainer = true;
		} catch (Exception e) {
			trainer = false;
		}
	}

	public void importDialogue() {
		try {
			dialogueFile = new File(
					this.getClass().getResource("/characters/dialoge/" + this.currentRoute.getId() + "/" +  getFileName() + ".char").getFile());
			JsonObject dialogue = new JsonParser().parse(new BufferedReader(new FileReader(dialogueFile))).getAsJsonObject();
			this.beforeFight = dialogue.get("before") instanceof JsonNull ? null : dialogue.get("before").getAsString();
			this.noFight = dialogue.get("no") instanceof JsonNull ? null : dialogue.get("no").getAsString();
			this.onDefeat = dialogue.get("on_defeat") instanceof JsonNull ? null : dialogue.get("on_defeat").getAsString();
			if(!(dialogue.get("reward") instanceof JsonNull)) {
				for(Item i : Item.values()) {
					if(i.name().toLowerCase().equals(dialogue.get("reward").getAsString().toLowerCase())) {
						this.reward = i;
						break;
					}
				}
			}
			if(this.reward == null) {
				this.reward = Item.NONE;
			}
		} catch (Exception e) {
			System.err.println("/characters/dialoge/" + this.currentRoute.getId() + "/" +  getFileName() + ".txt");
			e.printStackTrace();
		}
	}


	private String getFileName() {
		return this.name.toLowerCase().replace(" ", "_");
	}

	public String getOnDefeatDialogue() {
		return onDefeat;
	}

	public Item getReward() {
		return reward;
	}
}

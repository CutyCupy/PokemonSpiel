package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class NPC extends Character {


	private File teamFile;
	private File dialogueFile;
	private String beforeFight;
	private String noFight;

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
			currentPosition = new Point(originalPosition);
		} if(currentDirection != originalDirection) {
			this.currentDirection = originalDirection;
		}
		currentRoute.updateMap(currentPosition, originalPosition);
	}

	public void faceTowardsMainCharacter() {
		switch (gController.getMainCharacter().getCurrentDirection()) {
		case UP:
			currentDirection = Direction.DOWN;
			break;
		case RIGHT:
			currentDirection = Direction.LEFT;
			break;
		case DOWN:
			currentDirection = Direction.UP;
			break;
		case LEFT:
			currentDirection = Direction.RIGHT;
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
			while (!(currentPosition.x + x == mainX && currentPosition.y + y == mainY)) {
//				gController.sleep(500);
				currentRoute.getEntities()[currentPosition.y][currentPosition.x].removeCharacter();
				currentRoute.updateMap(currentPosition);
//				currentPosition.x += x;
//				currentPosition.y += y;
				currentRoute.getEntities()[currentPosition.y + y][currentPosition.x + x].addCharacter(this);
				this.changePosition(this.getCurrentDirection());
//				waiting();
				currentRoute.updateMap(currentPosition);
			}
			System.err.println("finished");
			return true;
		}
		return false;
	}

	public void importTeam() {
		try {
			System.out.println("IMPORT TEAM: " + getFileName() + " - " + this.currentRoute.getId());
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
			hasTeam = true;
		} catch (Exception e) {
			hasTeam = false;
		}

	}

	public void importDialogue() {
		try {
			System.out.println("IMPORT DIALOGUE: " + getFileName() + " - " + this.currentRoute.getId());
			dialogueFile = new File(
					this.getClass().getResource("/characters/dialoge/" + this.currentRoute.getId() + "/" +  getFileName() + ".txt").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(dialogueFile));
			String currentLine;
			while((currentLine = reader.readLine()) != null) {
				currentLine = currentLine.toLowerCase();
				if(currentLine.equals("before")) {
					beforeFight = reader.readLine();
				} else if((currentLine.equals("no"))) {
					noFight = reader.readLine();
				}
			}
		} catch (Exception e) {
			System.err.println("/characters/dialoge/" + this.currentRoute.getId() + "/" +  getFileName() + ".txt");
			e.printStackTrace();
		}
	}


	private String getFileName() {
		return this.name.toLowerCase().replace(" ", "_");
	}


}

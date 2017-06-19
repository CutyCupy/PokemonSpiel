package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.google.gson.JsonObject;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.map.Route;

public class Character {

	private Image front;
	private Image left;
	private Image right;
	private Image back;
	private String name;
	private Route currentRoute;
	private Point currentPosition;
	private Point originalPosition;
	private Direction originalDirection;
	private Team team;
	private String id;
	private long money;
	private Direction currentDirection;
	private boolean defeated;
	private File teamFile;
	private File dialogueFile;
	private String beforeFight;
	private String noFight;
	private boolean hasTeam;
	private boolean trainer;

	private Route lastPokemonCenter;

	private GameController gController;

	private ArrayList<String> routeHistory;

	//TODO: Add History to check where the last Pokemon center was.

	public Character() {
		gController = GameController.getInstance();
		routeHistory = new ArrayList<String>();
		team = new Team();
		currentPosition = new Point(0, 0);
		originalPosition = new Point(0, 0);
	}

	public Character(String id) {
		gController = GameController.getInstance();
		routeHistory = new ArrayList<String>();
		team = new Team();
		this.id = id;
		currentPosition = new Point(0, 0);
		originalPosition = new Point(0, 0);
	}

	public void setCurrentRoute(Route currentRoute) {
		routeHistory.add(currentRoute.getId());
		this.currentRoute = currentRoute;
	}

	public Route getCurrentRoute() {
		return this.currentRoute;
	}

	public ArrayList<String> getRouteHistory() {
		return this.routeHistory;
	}

	public Route getLastPokemonCenter() {
		String routeID = "zuhause";
		int x = 2;
		int y = 0;
		Direction d = Direction.DOWN;
		for(int centerIndex = this.routeHistory.size() - 1; centerIndex >= 0; centerIndex--) {
			if(this.routeHistory.get(centerIndex).equals("pokemon_center")) {
				routeID = this.routeHistory.get(centerIndex);
				x = 2;
				y = 1;
				d = Direction.UP;
				break;
			}
		}
		setCurrentPosition(x, y);
		setCurrentDirection(d);
		return GameController.getInstance().getRouteAnalyzer().getRouteById(routeID);
	}

	public void setCurrentPosition(int x, int y) {
		currentPosition.setLocation(x, y);
		originalPosition.setLocation(x, y);
	}

	public void setCurrentPosition(Point newPosition) {
		currentPosition.setLocation(newPosition);
		originalPosition.setLocation(newPosition);
	}

	public Point getCurrentPosition() {
		return this.currentPosition;
	}

	public void changePosition(Direction direction) {
		currentDirection = direction;
		switch (direction) {
		case UP:
			currentPosition.y -= 1;
			break;
		case DOWN:
			currentPosition.y += 1;
			break;
		case LEFT:
			currentPosition.x -= 1;
			break;
		case RIGHT:
			currentPosition.x += 1;
			break;
		}
	}

	public Team getTeam() {
		return this.team;
	}

	public Image getCharacterImage() {
		switch (currentDirection) {
		case UP:
			return back;
		case DOWN:
			return front;
		case LEFT:
			return left;
		case RIGHT:
			return right;
		}
		return null;
	}

	public void setLastPokemonCenter(Route center) {
		this.lastPokemonCenter = center;
	}

	public void warpToPokemonCenter() {
		this.setCurrentRoute(this.getLastPokemonCenter());
		gController.setCurrentRoute(this.getCurrentRoute());
		team.restoreTeam();
//		this.setCurrentDirection(Direction.UP);
//		if (lastPokemonCenter != null) {
//			this.setCurrentPosition(2, 1);
//			this.setCurrentRoute(this.lastPokemonCenter);
//		} else {
//			this.setCurrentPosition(2, 1);
//			gController.setCurrentRoute(gController.getRouteAnalyzer().getRouteById("pokemon_center"));
//		}
	}

	public void setCharacterImage(String characterImageName, String direction) {
		this.front = new ImageIcon(
				this.getClass().getResource("/characters/" + characterImageName + "_front.png").getFile()).getImage();
		this.back = new ImageIcon(
				this.getClass().getResource("/characters/" + characterImageName + "_back.png").getFile()).getImage();
		this.left = new ImageIcon(
				this.getClass().getResource("/characters/" + characterImageName + "_left.png").getFile()).getImage();
		this.right = new ImageIcon(
				this.getClass().getResource("/characters/" + characterImageName + "_right.png")	.getFile()).getImage();
		switch (direction) {
		case "front":
			currentDirection = Direction.DOWN;
			this.originalDirection = currentDirection;
			return;
		case "back":
			currentDirection = Direction.UP;
			this.originalDirection = currentDirection;
			return;
		case "left":
			currentDirection = Direction.LEFT;
			this.originalDirection = currentDirection;
			return;
		case "right":
			currentDirection = Direction.RIGHT;
			this.originalDirection = currentDirection;
			return;
		}
		currentDirection = Direction.DOWN;
		originalDirection = currentDirection;
	}

	public String getID() {
		return this.id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public long getMoney() {
		return this.money;
	}

	public void increaseMoney(long ammount) {
		this.money += ammount;
	}

	public void decreaseMoney(long ammount) {
		this.money -= ammount;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Direction getCurrentDirection() {
		return this.currentDirection;
	}

	public void setCurrentDirection(String direction) {
		switch (direction) {
		case "front":
			currentDirection = Direction.DOWN;
			this.originalDirection = currentDirection;
			return;
		case "back":
			currentDirection = Direction.UP;
			this.originalDirection = currentDirection;
			return;
		case "left":
			currentDirection = Direction.LEFT;
			this.originalDirection = currentDirection;
			return;
		case "right":
			currentDirection = Direction.RIGHT;
			this.originalDirection = currentDirection;
			return;
		}
		currentDirection = Direction.DOWN;
		this.originalDirection = currentDirection;
	}

	public void setCurrentDirection(Direction direction) {
		this.currentDirection = direction;
	}

	public Point getInteractionPoint() {
		switch (currentDirection) {
		case DOWN:
			return new Point(currentPosition.x, currentPosition.y + 1);
		case UP:
			return new Point(currentPosition.x, currentPosition.y - 1);
		case LEFT:
			return new Point(currentPosition.x - 1, currentPosition.y);
		case RIGHT:
			return new Point(currentPosition.x + 1, currentPosition.y);
		}
		return null;
	}

	public void defeated(boolean defeat) {
		this.defeated = defeat;
	}

	public boolean isDefeated() {
		return this.defeated;
	}

	public boolean isHasTeam() {
		return hasTeam;
	}

	public boolean isTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = Boolean.parseBoolean(trainer);
	}

	public int checkStartFight() {
		if (this.hasTeam) {
			if (!this.defeated) {
				int mainX = gController.getMainCharacter().getCurrentPosition().x;
				int mainY = gController.getMainCharacter().getCurrentPosition().y;
				if (mainX == currentPosition.x || mainY == currentPosition.y) {
					int x = 0;
					int y = 0;
					switch (currentDirection) {
					case DOWN:
						if(mainY - currentPosition.y < 7 && mainY - currentPosition.y > 0) {
							y = 1;
							break;
						}
						return -1;
					case LEFT:
						if(currentPosition.x - mainX < 7 && currentPosition.x - mainX > 0) {
							x = -1;
							break;
						}
						return -1;
					case RIGHT:
						if(mainX - currentPosition.x < 7 && mainX - currentPosition.x > 0) {
							x = 1;
							break;
						}
						return -1;
					case UP:
						if(currentPosition.y - mainY < 7 && currentPosition.y - mainY > 0) {
							y = -1;
							break;
						}
						return -1;
					}
					for (int i = 1; i <= 7; i++) {
						if (checkAccessible(currentPosition.x + (i * x), currentPosition.y + (i * y))) {
							if (mainX == currentPosition.x + (i * x) && mainY == currentPosition.y + (i * y)) {
								return i;
							}
						}
					}
				}
			}
		}
		return -1;
	}

	public boolean checkAccessible(int x, int y) {
		return gController.getCurrentBackground().getCurrentRoute().getEntities()[y][x].isAccessible();
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
				} else if(!currentRoute.getEntities()[currentPosition.y + (i*y)][currentPosition.x + (i*x)].isAccessible()) {
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
				currentPosition.x += x;
				currentPosition.y += y;
				currentRoute.getEntities()[currentPosition.y][currentPosition.x].addCharacter(this);
				currentRoute.updateMap(new Point(currentPosition.x - x, currentPosition.y - y), currentPosition);
				gController.repaint();
			}
			return true;
		}
		return false;
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
			System.exit(0);
		}
	}

	public String getBeforeFightDialogue() {
		return this.beforeFight;
	}

	public String getNoFightDialogue() {
		return this.noFight;
	}

	private String getFileName() {
		return this.name.toLowerCase().replace(" ", "_");
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("name", this.name);
		data.addProperty("id", this.id);
		data.addProperty("money", this.money);
		data.addProperty("route", this.currentRoute.getId());
		data.addProperty("current_position.x", this.currentPosition.x);
		data.addProperty("current_position.y", this.currentPosition.y);
		data.addProperty("current_direction", this.currentDirection.name());
		data.add("team", this.getTeam().getSaveData());
		data.addProperty("defeated", this.defeated);
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		if(this.id == null || this.id.equals(saveData.get("id").getAsString())) {
			this.setName(saveData.get("name").getAsString());
			this.money = saveData.get("money").getAsLong();
			this.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(saveData.get("route").getAsString()));
			this.setCurrentPosition(saveData.get("current_position.x").getAsInt(), saveData.get("current_position.y").getAsInt());
			switch(saveData.get("current_direction").getAsString()) {
			case "DOWN":
				setCurrentDirection(Direction.DOWN);
				break;
			case "LEFT":
				setCurrentDirection(Direction.LEFT);
				break;
			case "UP":
				setCurrentDirection(Direction.UP);
				break;
			case "RIGHT":
				setCurrentDirection(Direction.RIGHT);
				break;
			default:
				setCurrentDirection(Direction.DOWN);
				break;
			}
			this.team.importSaveData(saveData.get("team").getAsJsonArray());
			this.defeated = saveData.get("defeated").equals("true") ? true : false;
			return true;
		}
		return false;
	}

}
package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;

import com.google.gson.JsonObject;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.map.Route;

public class Character implements Runnable {

	private Image front;
	private Image left;
	private Image right;
	private Image back;
	protected String name;
	protected Route currentRoute;
	protected Point currentPosition;
	protected Point oldPosition;
	protected Point originalPosition;
	protected Direction originalDirection;
	protected Team team;
	protected String id;
	protected long money;
	protected Direction currentDirection;
	protected boolean defeated;
	protected boolean hasTeam;
	protected boolean trainer;

	protected double exactX;
	protected double exactY;

	protected boolean moving;

	protected GameController gController;


	protected int speed;

	public static final int FAST = 10;
	public static final int SLOW = 30;

	private boolean surfing;


	//TODO: Add History to check where the last Pokemon center was.

	public Character() {
		gController = GameController.getInstance();
		team = new Team();
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
	}

	public Character(String id) {
		gController = GameController.getInstance();
		team = new Team();
		this.id = id;
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
	}

	public void setCurrentRoute(Route currentRoute) {
		this.currentRoute = currentRoute;
	}

	public Route getCurrentRoute() {
		return this.currentRoute;
	}

	public void setCurrentPosition(int x, int y) {
		currentPosition.setLocation(x, y);
		originalPosition.setLocation(x, y);
		this.exactX = x;
		this.exactY = y;
	}

	public void setCurrentPosition(Point newPosition) {
		currentPosition.setLocation(newPosition);
		originalPosition.setLocation(newPosition);
		this.exactX = newPosition.x;
		this.exactY = newPosition.y;
	}

	public Point getCurrentPosition() {
		return this.currentPosition;
	}

	public void changePosition(Direction direction) {
		currentDirection = direction;
		oldPosition = new Point(currentPosition);
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
		new Thread(this).start();
		waiting();
		setSurfing(this.getCurrentRoute().getEntities()[currentPosition.y][currentPosition.x].isWater());
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
						if (this.getCurrentRoute().getEntities()[currentPosition.y + (i * y)][currentPosition.x + (i * x)].isAccessible(this)) {
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

//	public boolean checkAccessible(int x, int y) {
//		return gController.getCurrentBackground().getCurrentRoute().getEntities()[y][x].isAccessible(this);
//	}

	private void waiting() {
		this.moving = true;
		while(moving) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
		data.addProperty("surfing", this.isSurfing());
		data.add("team", this.getTeam().getSaveData());
		data.addProperty("defeated", this.defeated);
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		if(this.id == null || this.id.equals(saveData.get("id").getAsString())) {
			this.setID(saveData.get("id").getAsString());
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
			this.setSurfing("true".equals(saveData.get("surfing").getAsString()));
			this.team.importSaveData(saveData.get("team").getAsJsonArray());
			this.defeated = saveData.get("defeated").equals("true");
			return true;
		}
		return false;
	}

	public double getExactY() {
		return this.exactY;
	}

	public double getExactX() {
		return this.exactX;
	}

	@Override
	public void run() {
		switch (this.currentDirection) {
		case UP:
			for(int i = 0; i < 10; i++) {
				this.exactY -= 0.1;
				if(this instanceof NPC) {
					currentRoute.updateMap(oldPosition, currentPosition);
				}
				gController.getGameFrame().repaint();
				try {
					Thread.sleep(this.speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case DOWN:
			for(int i = 0; i < 10; i++) {
				this.exactY += 0.1;
				if(this instanceof NPC) {
					currentRoute.updateMap(oldPosition, currentPosition);
				}
				gController.getGameFrame().repaint();
				try {
					Thread.sleep(this.speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case LEFT:
			for(int i = 0; i < 10; i++) {
				this.exactX -= 0.1;
				if(this instanceof NPC) {
					currentRoute.updateMap(oldPosition, currentPosition);
				}
				gController.getGameFrame().repaint();
				try {
					Thread.sleep(this.speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		case RIGHT:
			for(int i = 0; i < 10; i++) {
				this.exactX += 0.1;
				if(this instanceof NPC) {
					currentRoute.updateMap(oldPosition, currentPosition);
				}
				gController.getGameFrame().repaint();
				try {
					Thread.sleep(this.speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		this.moving = false;
	}

	public void toggleWalkingSpeed() {
		this.speed = this.speed == SLOW ? FAST : SLOW;
	}

	public void setSurfing(boolean surfing) {
		this.surfing = surfing;
	}

	public boolean isSurfing() {
		return this.surfing;
	}
}
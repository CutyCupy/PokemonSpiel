package de.alexanderciupka.pokemon.characters;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.map.entities.Entity;

public class Character implements Runnable {

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
	protected boolean trainer;
	protected boolean aggro;

	protected double exactX;
	protected double exactY;

	protected boolean moving;

	protected GameController gController;
	private Direction uncontrollableDir;

	protected int speed;
	protected int originalSpeed;

	public static final int FAST = 20;
	public static final int SLOW = 40;
	public static final int VERY_SLOW = 50;

	private boolean surfing;
	private boolean controllable = true;
	private boolean spinning;
	private boolean event;

	private Thread uncontrollable;

	private HashMap<String, Image[]> sprites;
	private String spriteName;
	private int currentWalking;

	public boolean ignoreCollisions;

	public Character() {
		gController = GameController.getInstance();
		team = new Team(this);
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
		controllable = true;

		sprites = new HashMap<String, Image[]>();

	}

	public Character(String id) {
		gController = GameController.getInstance();
		team = new Team(this);
		this.id = id;
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
		controllable = true;

		sprites = new HashMap<String, Image[]>();

	}

	public void setCurrentRoute(Route currentRoute) {
		this.oldPosition = new Point(this.currentPosition);
		String oldRoute = currentRoute.getName();
		if (this.currentRoute != null) {
			oldRoute = this.currentRoute.getId();
		}
		this.currentRoute = currentRoute;
		Route old = this.gController.getRouteAnalyzer().getRouteById(oldRoute);
		if (old != null) {
			old.updateMap(this.currentPosition);
		}
	}

	public Route getCurrentRoute() {
		return this.currentRoute;
	}

	public void setCurrentPosition(int x, int y) {
		setCurrentPosition(new Point(x, y));
	}

	public void setCurrentPosition(Point newPosition) {
		this.oldPosition.setLocation(currentPosition.x, currentPosition.y);
		currentPosition.setLocation(newPosition);
		originalPosition.setLocation(newPosition);
		this.exactX = newPosition.x;
		this.exactY = newPosition.y;
		if (this.currentRoute != null) {
			this.currentRoute.updateMap(this.currentPosition);
		}
	}

	public Point getCurrentPosition() {
		return this.currentPosition;
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
		default:
			return new Point(currentPosition.x, currentPosition.y);
		}
	}

	public void changePosition(Direction direction, boolean waiting) {
		if (controllable) {
			setCurrentDirection(direction);
			oldPosition.setLocation(currentPosition.x, currentPosition.y);
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
			default:
				return;
			}
			if (waiting) {
				run();
			} else {
				new Thread(this).start();
				this.moving = true;
			}
			setSurfing(this.getCurrentRoute().getEntities()[currentPosition.y][currentPosition.x].isWater());
			this.currentRoute.updateMap(this.currentPosition);
		}
	}

	public void slide(Direction dir) {
		setCurrentDirection(dir);
		oldPosition = new Point(currentPosition);
		switch (dir) {
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
		default:
			return;
		}
		run();
	}

	public Team getTeam() {
		return this.team;
	}

	public Image getCharacterImage() {
		switch (currentDirection) {
		case UP:
			return sprites.get("back")[currentWalking];
		case DOWN:
			return sprites.get("front")[currentWalking];
		case LEFT:
			return sprites.get("left")[currentWalking];
		case RIGHT:
			return sprites.get("right")[currentWalking];
		default:
			return sprites.get("front")[0];
		}
	}

	public void setCharacterImage(String characterImageName) {
		switch (currentDirection) {
		case DOWN:
			setCharacterImage(characterImageName, "front");
			break;
		case LEFT:
			setCharacterImage(characterImageName, "left");
			break;
		case RIGHT:
			setCharacterImage(characterImageName, "right");
			break;
		case UP:
			setCharacterImage(characterImageName, "back");
			break;
		default:
			break;
		}
		;
	}

	public void setCharacterImage(String characterImageName, String direction) {
		this.spriteName = characterImageName;
		for (String s : new String[] { "front", "back", "left", "right" }) {
			Image[] currentImages = new Image[4];
			for (int i = 0; i <= 3; i++) {
				try {
					currentImages[i] = new ImageIcon(this.getClass()
							.getResource("/characters/" + this.spriteName + "_" + s + "_" + i + ".png").getFile())
									.getImage();
				} catch (Exception e) {
					currentImages[i] = new ImageIcon(this.getClass()
							.getResource("/characters/team_marco" + "_" + s + "_" + i + ".png").getFile()).getImage();
				}
			}
			this.sprites.put(s, currentImages);
		}
		switch (direction) {
		case "front":
			setCurrentDirection(Direction.DOWN);
			break;
		case "back":
			setCurrentDirection(Direction.UP);
			break;
		case "left":
			setCurrentDirection(Direction.LEFT);
			break;
		case "right":
			setCurrentDirection(Direction.RIGHT);
			break;
		default:
			setCurrentDirection(Direction.DOWN);
			break;
		}
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

	public de.alexanderciupka.pokemon.characters.Direction getCurrentDirection() {
		return this.currentDirection;
	}

	public void setCurrentDirection(Direction direction) {
		if (controllable && direction != Direction.NONE) {
			this.currentDirection = direction;
			if (this.currentRoute != null) {
				this.currentRoute.updateMap(this.currentPosition);
				Main.FORCE_REPAINT = true;
			}
		}
	}

	public void defeated(boolean defeat) {
		this.defeated = defeat;
	}

	public boolean isDefeated() {
		return this.defeated;
	}

	public boolean isTrainer() {
		return trainer;
	}

	public void setTrainer(String trainer) {
		this.trainer = Boolean.parseBoolean(trainer);
	}

	public void setTrainer(boolean trainer) {
		this.trainer = trainer;
	}

	public int checkStartFight() {
		if (this.trainer && this.aggro) {
			if (!this.defeated) {
				int mainX = gController.getMainCharacter().getCurrentPosition().x;
				int mainY = gController.getMainCharacter().getCurrentPosition().y;
				if (mainX == currentPosition.x || mainY == currentPosition.y) {
					int x = 0;
					int y = 0;
					switch (currentDirection) {
					case DOWN:
						if (mainY - currentPosition.y < 7 && mainY - currentPosition.y > 0) {
							y = 1;
							break;
						}
						return -1;
					case LEFT:
						if (currentPosition.x - mainX < 7 && currentPosition.x - mainX > 0) {
							x = -1;
							break;
						}
						return -1;
					case RIGHT:
						if (mainX - currentPosition.x < 7 && mainX - currentPosition.x > 0) {
							x = 1;
							break;
						}
						return -1;
					case UP:
						if (currentPosition.y - mainY < 7 && currentPosition.y - mainY > 0) {
							y = -1;
							break;
						}
						return -1;
					default:
						return -1;
					}
					for (int i = 1; i <= 4; i++) {
						if (this.getCurrentRoute().getEntities()[currentPosition.y + (i * y)][currentPosition.x
								+ (i * x)].isAccessible(this)) {
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

	public void waiting(boolean waiting) {
		while (moving && waiting) {
			Thread.yield();
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
		data.addProperty("original_position.x", this.originalPosition.x);
		data.addProperty("original_position.y", this.originalPosition.y);
		data.addProperty("current_direction", this.currentDirection.name());
		data.addProperty("original_direction", this.originalDirection.name());
		data.addProperty("surfing", this.isSurfing());
		data.addProperty("aggro", this.isAggro());
		data.addProperty("trainer", this.trainer);
		data.addProperty("aggro", this.aggro);
		data.addProperty("defeated", this.defeated);
		data.addProperty("spriteName", this.spriteName);

		data.add("team", this.getTeam().getSaveData());
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		if (this.id == null || this.id.equals(saveData.get("id").getAsString())) {
			this.setID(saveData.get("id").getAsString());
			this.setName(!(saveData.get("name") instanceof JsonNull) ? saveData.get("name").getAsString() : null);
			this.money = saveData.get("money").getAsLong();
			this.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(saveData.get("route").getAsString()));
			this.setCurrentPosition(saveData.get("current_position.x").getAsInt(),
					saveData.get("current_position.y").getAsInt());
			this.setOriginalPosition(new Point(saveData.get("original_position.x").getAsInt(),
					saveData.get("original_position.y").getAsInt()));
			try {
				setCurrentDirection(Direction.valueOf(saveData.get("current_direction").getAsString()));
			} catch (Exception e) {
				setCurrentDirection(Direction.DOWN);
			}
			try {
				setOriginalDirection(Direction.valueOf(saveData.get("original_direction").getAsString()));
			} catch (Exception e) {
				setOriginalDirection(Direction.DOWN);
			}
			this.setSurfing(false);
			if (saveData.get("surfing") != null) {
				this.setSurfing(saveData.get("surfing").getAsBoolean());
				if (isSurfing()) {
					currentRoute.getEntities()[this.currentPosition.y][this.currentPosition.y].setTerrain("see");
					currentRoute.updateMap(this.currentPosition);
				}
			}
			this.team.importSaveData(saveData.get("team").getAsJsonArray());
			this.trainer = saveData.get("trainer").getAsBoolean();
			this.setCharacterImage(saveData.get("spriteName").getAsString());
			this.defeated = saveData.get("defeated").getAsBoolean();
			this.aggro = saveData.get("aggro") != null ? saveData.get("aggro").getAsBoolean() : true;
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
		this.exactX = oldPosition.x;
		this.exactY = oldPosition.y;
		double xChange = 0;
		double yChange = 0;
		switch (this.currentDirection) {
		case UP:
			yChange = -0.1;
			break;
		case DOWN:
			yChange = 0.1;
			break;
		case LEFT:
			xChange = -0.1;
			break;
		case RIGHT:
			xChange = 0.1;
			break;
		case NONE:
			break;
		default:
			break;
		}
		for (int i = 0; i < 10; i++) {
			this.exactY += yChange;
			this.exactX += xChange;
			if (!this.isControllable() && spinning) {
				if (i % 2 == 0 && i != 0) {
					this.currentDirection = next();
				}
			} else if (this.isControllable() && i % 2 == 0 && i != 0) {
				currentWalking = (currentWalking + 1) % 4;
			}
			currentRoute.updateMap(oldPosition, currentPosition);
			try {
				if (i != 9) {
					Thread.sleep(this.speed);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.exactX = currentPosition.x;
		this.exactY = currentPosition.y;
		currentWalking = 0;
		this.currentRoute.getEntities()[currentPosition.y][currentPosition.x].onStep(this);
		this.currentRoute.updateMap(currentPosition);
		this.moving = false;
	}

	private Direction next() {
		for (int i = 0; i < Direction.values().length; i++) {
			if (Direction.values()[i].equals(currentDirection)) {
				Direction result = Direction.values()[(i + 1) % Direction.values().length];
				return result == Direction.NONE ? Direction.values()[(i + 2) % Direction.values().length] : result;
			}
		}
		return null;
	}

	public void toggleWalkingSpeed() {
		if (this.isControllable()) {
			this.speed = this.speed == SLOW ? FAST : SLOW;
		}
	}

	public void setSurfing(boolean surfing) {
		this.surfing = surfing;
	}

	public boolean isSurfing() {
		return this.surfing;
	}

	public boolean isControllable() {
		return controllable;
	}

	public void setControllable(boolean controllable) {
		this.controllable = controllable;
	}

	public void startUncontrollableMove(Direction dir, boolean spinning, int newSpeed) {
		this.setControllable(false);
		this.spinning = spinning;
		if (this.uncontrollableDir == null) {
			this.originalSpeed = this.speed;
		}
		if (this.uncontrollableDir == dir) {
			return;
		}
		uncontrollableDir = dir;
		uncontrollable = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isControllable() && uncontrollableDir == dir) {
					speed = newSpeed;
					currentDirection = dir;
					gController.slide(currentDirection);
				}
				if (isControllable()) {
					uncontrollableDir = null;
				}
				speed = originalSpeed;
			}
		});
		uncontrollable.start();
	}

	public Direction[] moveTowards(int x, int y) {
		this.speed = SLOW;
		int start = this.currentPosition.y * this.currentRoute.getWidth() + this.currentPosition.x;
		int[] distance = new int[this.currentRoute.getHeight() * this.currentRoute.getWidth()];
		int[] predecessor = new int[distance.length];
		HashMap<Integer, Entity> nodes = new HashMap<Integer, Entity>();
		for (int i = 0; i < distance.length; i++) {
			distance[i] = Integer.MAX_VALUE;
			predecessor[i] = -1;
			nodes.put(i, this.currentRoute.getEntities()[i / this.currentRoute.getWidth()][i
					% this.currentRoute.getWidth()]);
		}
		distance[start] = 0;
		int curX = this.currentPosition.x;
		int curY = this.currentPosition.y;
		Direction[] neighborDirection = { Direction.RIGHT, Direction.LEFT, Direction.DOWN, Direction.UP };
		while (!nodes.isEmpty()) {
			Entity smallestNode = findSmallest(distance, nodes);
			curX = smallestNode.getX();
			curY = smallestNode.getY();
			if (curX == x && curY == y) {
				break;
			}
			Entity[] neighbors = { nodes.get(curY * currentRoute.getWidth() + curX + 1),
					nodes.get(curY * currentRoute.getWidth() + curX - 1),
					nodes.get((curY + 1) * currentRoute.getWidth() + curX),
					nodes.get((curY - 1) * currentRoute.getWidth() + curX) };
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != null && neighbors[i].isAccessible(neighborDirection[i])) {
					distanceUpdate(curY * currentRoute.getWidth() + curX,
							neighbors[i].getY() * currentRoute.getWidth() + neighbors[i].getX(), distance, predecessor,
							start);
				}
			}

		}

		ArrayList<Point> path = new ArrayList<Point>();
		path.add(new Point(x, y));
		int u = y * currentRoute.getWidth() + x;
		while (predecessor[u] != -1) {
			u = predecessor[u];
			path.add(0, new Point(u % this.currentRoute.getWidth(), u / this.currentRoute.getWidth()));
		}

		ArrayList<Direction> directions = new ArrayList<Direction>();
		Point last = currentPosition;
		for (Point p : path) {
			if (p.x < last.x) {
				directions.add(Direction.LEFT);
			} else if (p.x > last.x) {
				directions.add(Direction.RIGHT);
			} else if (p.y < last.y) {
				directions.add(Direction.UP);
			} else if (p.y > last.y) {
				directions.add(Direction.DOWN);
			}
			last = p;
		}
		return directions.toArray(new Direction[directions.size()]);
	}

	private Entity findSmallest(int[] distance, HashMap<Integer, Entity> nodes) {
		int index = 0;
		int value = Integer.MAX_VALUE;
		for (int i = 0; i < distance.length; i++) {
			if (distance[i] < value && nodes.get(i) != null) {
				index = i;
				value = distance[i];
			}
		}
		return nodes.remove(index);
	}

	private void distanceUpdate(int u, int v, int[] distance, int[] predecessor, int start) {
		int alternativ = distance[u] + 1;
		if (alternativ < distance[v]) {
			distance[v] = alternativ;
			predecessor[v] = u;
		}
	}

	public void setOriginalPosition(Point currentPosition) {
		this.originalPosition = currentPosition;
	}

	public void setOriginalDirection(Direction direction) {
		this.originalDirection = direction;
	}

	public boolean isAggro() {
		return aggro;
	}

	public void setAggro(boolean aggro) {
		this.aggro = aggro;
	}

	public Point getOldPosition() {
		return this.oldPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Character) {
			Character c = (Character) obj;
			return this.getID().equals(c.getID()) && this.getName().equals(c.getName())
					&& this.getCurrentPosition().equals(c.getCurrentPosition())
					&& (this.getCurrentRoute() == null ? c.getCurrentRoute() == null
							: this.getCurrentRoute().getId().equals(c.getCurrentRoute().getId()));
		}
		return false;
	}

	public String getSpriteName() {
		return this.spriteName;
	}

	public boolean isSpinning() {
		return this.spinning;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}
}
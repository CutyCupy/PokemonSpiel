package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.ImageIcon;

import com.google.gson.JsonObject;

import de.alexanderciupka.sarahspiel.map.Entity;
import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.map.Route;

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

	protected double exactX;
	protected double exactY;

	protected boolean moving;

	protected GameController gController;
	private Direction uncontrollableDir;

	protected int speed;
	protected int originalSpeed;

	public static final int FAST = 10;
	public static final int SLOW = 30;
	public static final int VERY_SLOW = 50;

	private boolean surfing;
	private boolean controllable = true;

	private PC pc;

	private Thread uncontrollable;

	private HashMap<String, Image[]> sprites;
	private int currentWalking;

	private Stack<Point> stack;
	private Stack<Integer> steps;

	// TODO: Add History to check where the last Pokemon center was.

	public Character() {
		gController = GameController.getInstance();
		team = new Team();
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
		controllable = true;

		sprites = new HashMap<String, Image[]>();

		pc = new PC(this);
	}

	public Character(String id) {
		gController = GameController.getInstance();
		team = new Team();
		this.id = id;
		currentPosition = new Point(0, 0);
		oldPosition = new Point(currentPosition);
		originalPosition = new Point(0, 0);
		this.speed = SLOW;
		controllable = true;

		sprites = new HashMap<String, Image[]>();

		pc = new PC(this);
	}

	public void setCurrentRoute(Route currentRoute) {
		this.oldPosition = new Point(this.currentPosition);
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

	public void changePosition(Direction direction, boolean waiting) {
		if (controllable) {
			setCurrentDirection(direction);
			oldPosition = new Point(currentPosition);
			System.out.println(currentPosition);
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
			System.out.println(currentPosition);
			System.out.println(exactX);
			System.out.println(exactY);
			if (this instanceof NPC) {
				currentRoute.getEntities()[oldPosition.y][oldPosition.x].removeCharacter();
				currentRoute.getEntities()[currentPosition.y][currentPosition.x].addCharacter((NPC) this);
			}
			new Thread(this).start();
			this.moving = true;
			if (waiting) {
				waiting();
			}
			setSurfing(this.getCurrentRoute().getEntities()[currentPosition.y][currentPosition.x].isWater());
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
		}
		new Thread(this).start();
		waiting();
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
		}
		return null;
	}

	public void setCharacterImage(String characterImageName, String direction) {
		characterImageName = "team_marco";
		for (String s : new String[] { "front", "back", "left", "right" }) {
			Image[] currentImages = new Image[4];
			for (int i = 0; i <= 3; i++) {
				currentImages[i] = new ImageIcon(this.getClass()
						.getResource("/characters/" + characterImageName + "_" + s + "_" + i + ".png").getFile())
								.getImage();
			}
			this.sprites.put(s, currentImages);
		}
		// this.front = new ImageIcon(
		// this.getClass().getResource("/characters/" + characterImageName +
		// "_front.png").getFile()).getImage();
		// this.back = new ImageIcon(
		// this.getClass().getResource("/characters/" + characterImageName +
		// "_back.png").getFile()).getImage();
		// this.left = new ImageIcon(
		// this.getClass().getResource("/characters/" + characterImageName +
		// "_left.png").getFile()).getImage();
		// this.right = new ImageIcon(
		// this.getClass().getResource("/characters/" + characterImageName +
		// "_right.png").getFile()).getImage();
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

	public de.alexanderciupka.sarahspiel.pokemon.Direction getCurrentDirection() {
		return this.currentDirection;
	}

	// public void setCurrentDirection(String direction) {
	// switch (direction) {
	// case "front":
	// currentDirection = Direction.DOWN;
	// this.originalDirection = currentDirection;
	// return;
	// case "back":
	// currentDirection = Direction.UP;
	// this.originalDirection = currentDirection;
	// return;
	// case "left":
	// currentDirection = Direction.LEFT;
	// this.originalDirection = currentDirection;
	// return;
	// case "right":
	// currentDirection = Direction.RIGHT;
	// this.originalDirection = currentDirection;
	// return;
	// }
	// currentDirection = Direction.DOWN;
	// this.originalDirection = currentDirection;
	// }

	public void setCurrentDirection(Direction direction) {
		if (controllable) {
			this.currentDirection = direction;
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

	public int checkStartFight() {
		if (this.trainer && false) {
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
					}
					for (int i = 1; i <= 7; i++) {
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

	public void waiting() {
		while (moving) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.currentRoute.getEntities()[currentPosition.y][currentPosition.x].onStep(this);
		this.currentRoute.updateMap(currentPosition);
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
		data.add("pc", pc.getSaveData());
		data.addProperty("defeated", this.defeated);
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		if (this.id == null || this.id.equals(saveData.get("id").getAsString())) {
			this.setID(saveData.get("id").getAsString());
			this.setName(saveData.get("name").getAsString());
			this.money = saveData.get("money").getAsLong();
			this.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(saveData.get("route").getAsString()));
			this.setCurrentPosition(saveData.get("current_position.x").getAsInt(),
					saveData.get("current_position.y").getAsInt());
			switch (saveData.get("current_direction").getAsString().toLowerCase()) {
			case "down":
				setCurrentDirection(Direction.DOWN);
				break;
			case "left":
				setCurrentDirection(Direction.LEFT);
				break;
			case "up":
				setCurrentDirection(Direction.UP);
				break;
			case "right":
				setCurrentDirection(Direction.RIGHT);
				break;
			default:
				setCurrentDirection(Direction.DOWN);
				break;
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
			this.pc.importSaveData(saveData.get("pc").getAsJsonArray());
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
		this.exactX = oldPosition.x;
		this.exactY = oldPosition.y;
		switch (this.currentDirection) {
		case UP:
			for (int i = 0; i < 10; i++) {
				this.exactY -= 0.1;
				if (!this.isControllable() && i % 3 == 0 && i != 0) {
					this.currentDirection = next();
				} else if (i % 2 == 0 && i != 0) {
					currentWalking = (currentWalking + 1) % 4;
				}
				if (this instanceof NPC) {
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
			for (int i = 0; i < 10; i++) {
				this.exactY += 0.1;

				if (!this.isControllable() && i % 3 == 0 && i != 0) {
					this.currentDirection = next();
				} else if (i % 2 == 0 && i != 0) {
					currentWalking = (currentWalking + 1) % 4;
				}
				if (this instanceof NPC) {
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
			for (int i = 0; i < 10; i++) {
				this.exactX -= 0.1;
				if (this instanceof NPC) {
					currentRoute.updateMap(oldPosition, currentPosition);
				}
				if (!this.isControllable() && i % 3 == 0 && i != 0) {
					this.currentDirection = next();
				} else if (i % 2 == 0 && i != 0) {
					currentWalking = (currentWalking + 1) % 4;
				}
				if (this instanceof NPC) {
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
			for (int i = 0; i < 10; i++) {
				this.exactX += 0.1;

				if (!this.isControllable() && i % 3 == 0 && i != 0) {
					this.currentDirection = next();
				} else if (i % 2 == 0 && i != 0) {
					currentWalking = (currentWalking + 1) % 4;
				}
				if (this instanceof NPC) {
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
		currentWalking = 0;
		gController.getGameFrame().repaint();
		this.moving = false;
	}

	private Direction next() {
		for (int i = 0; i < Direction.values().length; i++) {
			if (Direction.values()[i].equals(currentDirection)) {
				return Direction.values()[(i + 1) % Direction.values().length];
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

	public PC getPC() {
		return this.pc;
	}

	public boolean isControllable() {
		return controllable;
	}

	public void setControllable(boolean controllable) {
		this.controllable = controllable;
	}

	public void startUncontrollableMove(Direction dir) {
		this.setControllable(false);
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
					speed = VERY_SLOW;
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
		int start = ((int) this.getExactY()) * this.currentRoute.getWidth() + (int) this.getExactX();
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

		while (!nodes.isEmpty()) {
			Entity smallestNode = findSmallest(distance, nodes);
			int curX = smallestNode.getX();
			int curY = smallestNode.getY();
			if (curX == x && curY == y) {
				break;
			}
			Entity[] neighbors = { nodes.get(curY * currentRoute.getWidth() + curX + 1),
					nodes.get(curY * currentRoute.getWidth() + curX - 1),
					nodes.get((curY + 1) * currentRoute.getWidth() + curX),
					nodes.get((curY - 1) * currentRoute.getWidth() + curX) };
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != null && neighbors[i].isAccessible(this)) {
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
			if (distance[i] <= value && nodes.get(i) != null) {
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
}
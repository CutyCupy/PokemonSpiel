package de.alexanderciupka.pokemon.characters.types;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.map.entities.Entity;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.PokemonInformation;

public class Character implements Runnable {

	protected String id;
	protected String name;

	protected Route currentRoute;
	protected Point currentPosition;
	protected double exactX;
	protected double exactY;
	protected Point oldPosition;
	protected Point originalPosition;

	protected Direction originalDirection;
	protected Direction currentDirection;

	protected long money;
	protected Team team;
	protected int range; // Describes the Aggro Range

	// protected boolean aggro;
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

	private int protectedSteps;
	private int stepCounter;

	private NPC follower;

	private HashMap<Integer, Integer> items;

	public static final int NOT_IN_RANGE = -1;
	public static final int SEARCH_FOR_DOUBLE = 0;
	public static final int SEARCH_FINISHED = 1;

	public NPC getFollower() {
		return this.follower;
	}

	public void setFollower(NPC follower) {
		this.follower = follower;
	}

	public Character() {
		this.gController = GameController.getInstance();
		this.currentPosition = new Point(0, 0);
		this.oldPosition = new Point(this.currentPosition);
		this.originalPosition = new Point(0, 0);
		this.speed = SLOW;
		this.controllable = true;

		this.sprites = new HashMap<String, Image[]>();
		this.items = new HashMap<Integer, Integer>();
		this.team = new Team(this);

		this.id = "";

	}

	public Character(String id) {
		this();
		this.id = id;

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
		this.setCurrentPosition(new Point(x, y));
	}

	public void setCurrentPosition(Point newPosition) {
		this.oldPosition.setLocation(this.currentPosition.x, this.currentPosition.y);
		this.currentPosition.setLocation(newPosition);
		this.originalPosition.setLocation(newPosition);
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
		switch (this.currentDirection) {
		case DOWN:
			return new Point(this.currentPosition.x, this.currentPosition.y + 1);
		case UP:
			return new Point(this.currentPosition.x, this.currentPosition.y - 1);
		case LEFT:
			return new Point(this.currentPosition.x - 1, this.currentPosition.y);
		case RIGHT:
			return new Point(this.currentPosition.x + 1, this.currentPosition.y);
		default:
			return new Point(this.currentPosition.x, this.currentPosition.y);
		}
	}

	public void changePosition(Direction direction, boolean waiting) {
		if (this.controllable) {
			if (this.follower != null && this.currentRoute.equals(this.follower.currentRoute, false)) {
				if (this.currentPosition.distance(this.follower.currentPosition) > 1) {
					this.follower.setCurrentPosition(this.oldPosition.getLocation());
				}
			}
			this.setCurrentDirection(direction);
			if (this.follower != null) {
				if (this.currentPosition.x < this.oldPosition.x) {
					this.follower.changePosition(Direction.LEFT, false);
				} else if (this.currentPosition.x > this.oldPosition.x) {
					this.follower.changePosition(Direction.RIGHT, false);
				} else {
					if (this.currentPosition.y < this.oldPosition.y) {
						this.follower.changePosition(Direction.UP, false);
					} else if (this.currentPosition.y > this.oldPosition.y) {
						this.follower.changePosition(Direction.DOWN, false);
					}
				}
			}
			this.oldPosition.setLocation(this.currentPosition.x, this.currentPosition.y);
			switch (direction) {
			case UP:
				this.currentPosition.y -= 1;
				break;
			case DOWN:
				this.currentPosition.y += 1;
				break;
			case LEFT:
				this.currentPosition.x -= 1;
				break;
			case RIGHT:
				this.currentPosition.x += 1;
				break;
			default:
				return;
			}

			if (waiting) {
				this.run();
			} else {
				new Thread(this).start();
				this.moving = true;
			}
			this.setSurfing(
					this.getCurrentRoute().getEntities()[this.currentPosition.y][this.currentPosition.x].isWater());
			this.currentRoute.updateMap(this.currentPosition);
			this.onMovement();
		}
	}

	public void slide(Direction dir) {
		this.setCurrentDirection(dir);
		this.oldPosition = new Point(this.currentPosition);
		switch (dir) {
		case UP:
			this.currentPosition.y -= 1;
			break;
		case DOWN:
			this.currentPosition.y += 1;
			break;
		case LEFT:
			this.currentPosition.x -= 1;
			break;
		case RIGHT:
			this.currentPosition.x += 1;
			break;
		default:
			return;
		}
		this.run();
	}

	public Team getTeam() {
		return this.team;
	}

	public Image getCharacterImage() {
		switch (this.currentDirection) {
		case UP:
			return this.sprites.get("back")[this.currentWalking];
		case DOWN:
			return this.sprites.get("front")[this.currentWalking];
		case LEFT:
			return this.sprites.get("left")[this.currentWalking];
		case RIGHT:
			return this.sprites.get("right")[this.currentWalking];
		default:
			return this.sprites.get("front")[0];
		}
	}

	public void setCharacterImage(String characterImageName) {
		switch (this.currentDirection) {
		case DOWN:
			this.setCharacterImage(characterImageName, "front");
			break;
		case LEFT:
			this.setCharacterImage(characterImageName, "left");
			break;
		case RIGHT:
			this.setCharacterImage(characterImageName, "right");
			break;
		case UP:
			this.setCharacterImage(characterImageName, "back");
			break;
		default:
			break;
		}
	}

	public void setCharacterImage(String characterImageName, String direction) {
		if (characterImageName.equals(this.spriteName)) {
			return;
		}
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
			this.setCurrentDirection(Direction.DOWN);
			break;
		case "back":
			this.setCurrentDirection(Direction.UP);
			break;
		case "left":
			this.setCurrentDirection(Direction.LEFT);
			break;
		case "right":
			this.setCurrentDirection(Direction.RIGHT);
			break;
		default:
			this.setCurrentDirection(Direction.DOWN);
			break;
		}
		this.originalDirection = this.currentDirection;
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
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public de.alexanderciupka.pokemon.characters.Direction getCurrentDirection() {
		return this.currentDirection;
	}

	public void setCurrentDirection(Direction direction) {
		if (this.controllable && direction != Direction.NONE) {
			this.currentDirection = direction;
			if (this.currentRoute != null) {
				this.currentRoute.updateMap(this.currentPosition);
				Main.FORCE_REPAINT = true;
			}
		}
	}

	public boolean isDefeated() {
		if (this.isTrainer()) {
			return this.team.getFirstFightPokemon() == null;
		}
		return true;
	}

	public boolean isTrainer() {
		return this.team != null;
	}

	public void waiting(boolean waiting) {
		while (this.moving && waiting) {
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
		// data.addProperty("aggro", this.isAggro());
		// data.addProperty("trainer", this.trainer);
		data.addProperty("range", this.range);
		// data.addProperty("defeated", this.isDefeated());
		data.addProperty("spriteName", this.spriteName);
		data.addProperty("step_counter", this.stepCounter);

		JsonArray items = new JsonArray();
		for (Integer i : this.items.keySet()) {
			int amount = this.items.get(i);
			if (amount == 0) {
				continue;
			}
			JsonObject currentItem = new JsonObject();
			currentItem.addProperty("id", i);
			currentItem.addProperty("amount", amount);
			items.add(currentItem);
		}
		data.add("items", items);

		data.add("team", this.getTeam().getSaveData());
		return data;
	}

	public boolean importSaveData(JsonObject saveData) {
		if (this.id == null || this.id.equals(saveData.get("id").getAsString())) {
			this.setID(saveData.get("id").getAsString());
			this.setName(!(saveData.get("name") instanceof JsonNull) ? saveData.get("name").getAsString() : null);
			this.money = saveData.get("money").getAsLong();
			this.setCurrentRoute(this.gController.getRouteAnalyzer().getRouteById(saveData.get("route").getAsString()));
			this.setCurrentPosition(saveData.get("current_position.x").getAsInt(),
					saveData.get("current_position.y").getAsInt());
			this.setOriginalPosition(new Point(saveData.get("original_position.x").getAsInt(),
					saveData.get("original_position.y").getAsInt()));
			try {
				this.setCurrentDirection(Direction.valueOf(saveData.get("current_direction").getAsString()));
			} catch (Exception e) {
				this.setCurrentDirection(Direction.DOWN);
			}
			try {
				this.setOriginalDirection(Direction.valueOf(saveData.get("original_direction").getAsString()));
			} catch (Exception e) {
				this.setOriginalDirection(Direction.DOWN);
			}
			this.setSurfing(false);
			if (saveData.get("surfing") != null) {
				this.setSurfing(saveData.get("surfing").getAsBoolean());
				if (this.isSurfing()) {
					this.currentRoute.getEntities()[this.currentPosition.y][this.currentPosition.y].setTerrain("see");
					this.currentRoute.updateMap(this.currentPosition);
				}
			}
			this.team.importSaveData(saveData.get("team").getAsJsonArray());
			// this.trainer = saveData.get("trainer").getAsBoolean();
			this.setCharacterImage(saveData.get("spriteName").getAsString());
			// this.defeated = saveData.get("defeated").getAsBoolean();
			// this.aggro = saveData.get("aggro") != null ?
			// saveData.get("aggro").getAsBoolean() : true;
			this.range = saveData.get("range") != null ? saveData.get("range").getAsInt() : 4;
			for (JsonElement j : saveData.get("items").getAsJsonArray()) {
				if (j.getAsJsonObject().get("amount") != null) {
					this.items.put(j.getAsJsonObject().get("id").getAsInt(),
							j.getAsJsonObject().get("amount").getAsInt());
				}
			}
			this.stepCounter = saveData.get("step_counter").getAsInt();

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
		this.exactX = this.oldPosition.x;
		this.exactY = this.oldPosition.y;
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
			if (!this.isControllable() && this.spinning) {
				if (i % 2 == 0 && i != 0) {
					this.currentDirection = this.next();
				}
			} else if (this.isControllable() && i % 2 == 0 && i != 0) {
				this.currentWalking = (this.currentWalking + 1) % 4;
			}
			this.currentRoute.updateMap(this.oldPosition, this.currentPosition);
			try {
				if (i != 9) {
					Thread.sleep(this.speed);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.exactX = this.currentPosition.x;
		this.exactY = this.currentPosition.y;
		this.currentWalking = 0;
		this.currentRoute.getEntities()[this.currentPosition.y][this.currentPosition.x].onStep(this);
		this.currentRoute.updateMap(this.currentPosition);
		this.moving = false;
	}

	private Direction next() {
		for (int i = 0; i < Direction.values().length; i++) {
			if (Direction.values()[i].equals(this.currentDirection)) {
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
		return this.controllable;
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
		this.uncontrollableDir = dir;
		this.uncontrollable = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Character.this.isControllable() && Character.this.uncontrollableDir == dir) {
					Character.this.speed = newSpeed;
					Character.this.currentDirection = dir;
					Character.this.gController.slide(Character.this.currentDirection);
				}
				if (Character.this.isControllable()) {
					Character.this.uncontrollableDir = null;
				}
				Character.this.speed = Character.this.originalSpeed;
			}
		});
		this.uncontrollable.start();
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
			Entity smallestNode = this.findSmallest(distance, nodes);
			curX = smallestNode.getX();
			curY = smallestNode.getY();
			if (curX == x && curY == y) {
				break;
			}
			Entity[] neighbors = { nodes.get(curY * this.currentRoute.getWidth() + curX + 1),
					nodes.get(curY * this.currentRoute.getWidth() + curX - 1),
					nodes.get((curY + 1) * this.currentRoute.getWidth() + curX),
					nodes.get((curY - 1) * this.currentRoute.getWidth() + curX) };
			for (int i = 0; i < neighbors.length; i++) {
				if (neighbors[i] != null && neighbors[i].isAccessible(neighborDirection[i])) {
					this.distanceUpdate(curY * this.currentRoute.getWidth() + curX,
							neighbors[i].getY() * this.currentRoute.getWidth() + neighbors[i].getX(), distance,
							predecessor, start);
				}
			}

		}

		ArrayList<Point> path = new ArrayList<Point>();
		path.add(new Point(x, y));
		int u = y * this.currentRoute.getWidth() + x;
		while (predecessor[u] != -1) {
			u = predecessor[u];
			path.add(0, new Point(u % this.currentRoute.getWidth(), u / this.currentRoute.getWidth()));
		}

		ArrayList<Direction> directions = new ArrayList<Direction>();
		Point last = this.currentPosition;
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
		return this.range > 0;
	}

	public void setRange(int range) {
		this.range = range;
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
		return this.event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public void addItem(Integer reward) {
		this.addItem(reward, 1);
	}

	public void addItem(Integer reward, int amount) {
		if (amount > 0) {
			SoundController.getInstance().playSound(SoundController.GET_ITEM);
			Integer previous = this.items.get(reward);
			if(previous == null) {
				previous = 0;
			}
			this.items.put(reward, previous + amount);
		}
	}

	public boolean hasItem(Integer i) {
		if(this.items.get(i) != null) {
			return this.items.get(i) > 0;
		}
		return false;
	}

	public boolean removeItem(Integer i) {
		Integer value = this.items.get(i);
		if(value != null && this.gController.getInformation().hasAttribute(Items.ATTR_CONSUMABLE, i)) {
			if (value > 1) {
				this.items.put(i, value - 1);
				return true;
			} else {
				this.items.remove(i);
			}
		}
		return this.gController.getInformation().hasAttribute(Items.ATTR_CONSUMABLE, i);
	}

	public HashMap<Integer, Integer> getItems() {
		return this.items;
	}

	public boolean isProtected() {
		return this.protectedSteps > 0;
	}

	//TODO: Use Item
	public String useItem(Integer item) {
		PokemonInformation info = gController.getInformation();
		if(this.gController.isFighting()) {
			if(info.getItemData(Items.ITEM_POCKET, item).equals(Items.POKEBALLS)) {
				this.gController.getGameFrame().getFightPanel().throwBall(item);
				return null;
			}
		}
		return Items.USELESS;
	}
//		this.gController.setInteractionPause(true);
//		boolean result = false;
//		switch (i) {
//		case CUT:
//		case FLASH:
//		case ROCKSMASH:
//		case STRENGTH:
//		case SURF:
//			if (this instanceof Player) {
//				this.currentRoute.getEntities()[this.getInteractionPoint().y][this.getInteractionPoint().x]
//						.useVM((Player) this, i);
//			}
//			break;
//		case REPEL:
//			if (this.isProtected()) {
//				this.gController.getGameFrame().addDialogue("Es wurde bereits ein Schutz eingesetzt!");
//			} else {
//				this.protectedSteps = this.stepCounter + i.getValue();
//				this.gController.getGameFrame()
//						.addDialogue("Du bist jetzt für " + i.getValue() + " Schritte vor wilden Pokemon geschützt!");
//				result = true;
//			}
//		case HYPERBALL:
//		case MASTERBALL:
//		case POKEBALL:
//		case SUPERBALL:
//		case HEALBALL:
//		case PREMIERBALL:
//			if (this.gController.isFighting()) {
//				result = true;
//				this.gController.getGameFrame().getFightPanel().throwBall(i);
//			} else {
//				this.gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
//			}
//			break;
//		default:
//			this.gController.getGameFrame().addDialogue("Es wird keine Wirkung haben.");
//			result = false;
//		}
//		if (result) {
//			this.removeItem(i);
//			this.gController.getGameFrame().setCurrentPanel(null);
//		}
//		this.gController.waitDialogue();
//		this.gController.setInteractionPause(false);
//		return result;
//	}

	public void onMovement() {
		this.stepCounter++;
		if (this.stepCounter % 8 == 0) {
			for (int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].afterWalkingDamage();
			}
		}
		if (this.stepCounter % 128 == 0) {
			for (int i = 0; i < this.team.getAmmount(); i++) {
				this.team.getTeam()[i].changeHappiness(1);
			}
		}
		if (this.protectedSteps > 0) {
			this.protectedSteps--;
			if (this.protectedSteps == 0) {
				this.gController.getGameFrame().addDialogue("Der Schutz ist ausgelaufen!");
				this.gController.waitDialogue();
			}
		}
	}
}
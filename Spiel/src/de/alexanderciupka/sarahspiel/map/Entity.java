package de.alexanderciupka.sarahspiel.map;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;

import de.alexanderciupka.sarahspiel.pokemon.Character;
import de.alexanderciupka.sarahspiel.pokemon.Direction;
import de.alexanderciupka.sarahspiel.pokemon.NPC;
import de.alexanderciupka.sarahspiel.pokemon.Player;

public class Entity {

	private boolean left;
	private boolean right;
	private boolean top;
	private boolean bottom;

	private Image sprite;
	private float pokemonRate;
	private Warp warp;
	private ArrayList<NPC> characters;
	private Image terrain;

	private String terrainName;
	private String spriteName;

	private boolean water;

	public static final float POKEMON_GRASS_RATE = 0.0f;

	private int x;
	private int y;

	private double exactX;
	private double exactY;

	private Random rng;
	private GameController gController;

	private TriggeredEvent event;


	public Entity(boolean accessible, String spriteName, float pokemonRate, String terrainName) {
		this.left = accessible;
		this.right = accessible;
		this.top = accessible;
		this.bottom = accessible;
		gController = GameController.getInstance();
		try {
			setSprite(spriteName);
			setTerrain(terrainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.characters = new ArrayList<NPC>();
		this.pokemonRate = pokemonRate;
		rng = new Random();
	}

	public Entity(boolean left, boolean right, boolean top, boolean bottom, String spriteName, float pokemonRate, String terrainName) {
	this.left = left;
	this.right = right;
	this.top = top;
	this.bottom = bottom;
	gController = GameController.getInstance();
	try {
		setSprite(spriteName);
		setTerrain(terrainName);
	} catch (Exception e) {
		e.printStackTrace();
	}
	this.characters = new ArrayList<NPC>();
	this.pokemonRate = pokemonRate;
	rng = new Random();
	}

	public boolean isAccessible(Character c) {
		boolean accessible = false;
		switch(c.getCurrentDirection()) {
		case DOWN:
			accessible = this.top;
			break;
		case LEFT:
			accessible = this.right;
			break;
		case RIGHT:
			accessible = this.left;
			break;
		case UP:
			accessible = this.bottom;
			break;
		}
		return ((accessible && !this.isWater()) || (this.isWater() && c.isSurfing() && accessible)) && !this.hasCharacter();
	}

	public void setSprite(String spriteName) {
		this.spriteName = spriteName;
		if(spriteName.equals("grass")) {
			this.terrain = new ImageIcon(this.getClass().getResource("/routes/terrain/grassy.png")).getImage();
		}
		this.sprite = new ImageIcon(this.getClass().getResource("/routes/entities/" + spriteName + ".png")).getImage();
	}

	public void setTerrain(String terrainName) {
		this.terrainName = terrainName;
		try {
			this.terrain = new ImageIcon(this.getClass().getResource("/routes/terrain/" + terrainName + ".png"))
					.getImage();
			this.setWater(terrainName.equals("see"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Image getSprite() {
		return this.sprite;
	}

	public String getSpriteName() {
		return this.spriteName;
	}

	public Image[] getCharacterSprites() {
		Image[] result = new Image[this.characters.size()];
		for(int i = 0; i < this.characters.size(); i++) {
			result[i] = this.characters.get(i).getCharacterImage();
		}
		return result;
	}

	public Image getTerrain() {
		return this.terrain;
	}

	public void addWarp(Warp warp) {
		this.warp = warp;
	}

	public void addCharacter(NPC character) {
		this.characters.add(character);
	}

	public boolean removeCharacter() {
		if (hasCharacter()) {
			this.characters.remove(0);
			return true;
		}
		return false;
	}

	public boolean removeCharacter(String characterId) {
		int i = 0;
		while(i < this.characters.size()) {
			if(this.characters.get(i).getID().equals(characterId)) {
				this.characters.remove(i);
				return true;
			}
			i++;
		}
		return false;
	}

	public ArrayList<NPC> getCharacters() {
		return this.characters;
	}

	public boolean checkPokemon() {
		if (pokemonRate > 0 && gController.getCurrentBackground().getCurrentRoute().getPokemonPool().size() != 0) {
			if (rng.nextFloat() <= pokemonRate) {
				return true;
			}
		}
		return false;
	}

	public boolean startWarp(Character c) {
		if (warp != null) {
			if(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()) == null) {
				return false;
			}
			if(warp.getNewRoute().toLowerCase().equals("pokemon_center")) {
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp().setNewPosition(c.getCurrentPosition().getLocation());
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[4][2].getWarp().setNewRoute(c.getCurrentRoute().getId());
			}
			gController.resetCharacterPositions();
			c.setCurrentPosition(warp.getNewPosition());
			c.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
			if(this.spriteName.contains("stair")) {
				switch(c.getCurrentDirection()) {
				case LEFT:
					c.setCurrentDirection(Direction.RIGHT);
					break;
				case RIGHT:
					c.setCurrentDirection(Direction.LEFT);
					break;
				default:
					break;
				}
			}
			if(c instanceof NPC) {
				gController.getRouteAnalyzer().getRouteById(warp.getOldRoute()).removeCharacter(c.getID());
				gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).addCharacterToEntity(c.getCurrentPosition().x, c.getCurrentPosition().y, (NPC) c);
			}
			if(c instanceof Player) {
				gController.setCurrentRoute(gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()));
			}
			gController.repaint();
			if(c instanceof Player) {
				if(c.getCurrentRoute().getId().equals("haus_von_alex")) {
					Entity house = gController.getRouteAnalyzer().getRouteById("bruchkoebel").getEntities()[7][42];
					if(house.getWarp() == null) {
						Warp houseWarp = new Warp("W100", "bruchkoebel");
						houseWarp.setNewPosition(new Point(3, 4));
						houseWarp.setNewRoute("verlassenes_haus");
						house.addWarp(houseWarp);
						house.setAccessible(true);
						gController.getGameFrame().addDialogue("Irgendwie scheint Alex nicht hier zu sein... Wo steckt er denn?! "
								+ "Huch - was liegt denn da? Eine Notiz: WICHTIG! KOMME SOFORT NACH BRUCHKOEBEL ZUM VERLASSENEN HAUS!");
						gController.waitDialogue();
					}
				} else if(c.getCurrentRoute().getId().equals("verlassenes_haus")) {
					for(Character character : gController.getMainCharacter().getCurrentRoute().getCharacters()) {
						if(character.getName().equals("Alex")) {
							if(!character.isDefeated()) {
								gController.getGameFrame().addDialogue("Alle: Happy Birthday to You! "
										+ "Happy Birthday to You! Happy Birthday liebe SARAH! Happy Birthday to You!");
								gController.getGameFrame().addDialogue("Alex: Alles Gute zum Geburtstag Schatz!");
								gController.waitDialogue();
							}
							break;
						}
					}
				}
			}
			gController.getRouteAnalyzer().getRouteById(warp.getNewRoute()).getEntities()[c.getCurrentPosition().y][c.getCurrentPosition().x].onStepNoWarp(c);
			return true;
		}
		return false;
	}

	public boolean hasCharacter() {
		return !this.characters.isEmpty();
	}

	public Warp getWarp() {
		return this.warp;
	}

	public String getTerrainName() {
		return terrainName;
	}

	public void setAccessible(boolean accessible) {
		this.left = accessible;
		this.right = accessible;
		this.top = accessible;
		this.bottom = accessible;
	}

	public void setAccessible(boolean left, boolean right, boolean top, boolean bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	private void setWater(boolean water) {
		this.water = water;
	}

	public boolean isWater() {
		return this.water;
	}

	public boolean isPC() {
		return spriteName.equals("pc");
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		this.exactX = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		this.exactY = y;
	}

	public double getExactX() {
		return exactX;
	}

	public double getExactY() {
		return exactY;
	}

	public void onStep(Character c) {
		if(!startWarp(c)) {
			onStepNoWarp(c);
		}
	}

	public void onStepNoWarp(Character c) {
		if(this.event != null && c instanceof Player) {
			event.startEvent((Player) c);
			return;
		}
		int characterIndex = gController.checkStartFight();
		if (characterIndex >= 0) {
			gController.startFight(gController.getCurrentBackground().getCurrentRoute().getCharacters().get(characterIndex));
		} else if (checkPokemon()) {
			gController.startFight(gController.getCurrentBackground().chooseEncounter());
		} else {
			if(this.spriteName.startsWith("move")) {
				switch(spriteName) {
				case "moveleft":
					c.startUncontrollableMove(Direction.LEFT);
					break;
				case "moveright":
					c.startUncontrollableMove(Direction.RIGHT);
					break;
				case "moveup":
					c.startUncontrollableMove(Direction.UP);
					break;
				case "movedown":
					c.startUncontrollableMove(Direction.DOWN);
					break;
				case "movestop":
					c.setControllable(true);
				}
			}
		}
	}

	public void onInteraction(Player c) {
		boolean flag = false;
		if (hasCharacter()) {
			for(NPC character : getCharacters()) {
				if(character.getID().equals("strength")) {
					gController.getGameFrame().addDialogue("Dieser Felsen kann bewegt werden!");
					gController.waitDialogue();
					return;
				}
				if (character.isTrainer()) {
					if (!character.isDefeated()) {
						character.faceTowardsMainCharacter();
						gController.startFight(character);
						flag = true;
					}
				}
				if(!flag) {
					character.faceTowardsMainCharacter();
					gController.getGameFrame().addDialogue(character.getName() + ": "
							+ character.getNoFightDialogue());
					gController.waitDialogue();
					if(character.getName().equals("Joy")) {
						c.getTeam().restoreTeam();
						for(int i = 1; i <= c.getTeam().getAmmount() + 1; i++) {
							gController.getCurrentBackground().getCurrentRoute().getEntities()[0][1].setSprite("joyhealing" + (i % (c.getTeam().getAmmount() + 1)));
							gController.getCurrentBackground().getCurrentRoute().updateMap(new Point(1, 0));
							gController.getGameFrame().repaint();
							gController.sleep(i == c.getTeam().getAmmount() ? 1500 : 750);
						}
						gController.getGameFrame().addDialogue("Deine Pokemon sind nun wieder topfit!");
						gController.waitDialogue();
					}
				}
			}
		} else if (getSpriteName().equals("free") && !isAccessible(c)) {
			if(y - 1 >= 0) {
				if(c.getCurrentRoute().getEntities()[y-1][x].hasCharacter() && c.getCurrentRoute().getEntities()[y-1][x].getCharacters().get(0).getName().equals("Joy")) {
					c.getCurrentRoute().getEntities()[y-1][x].onInteraction(c);
				}
			}
		} else if(isWater() && !c.isSurfing()) {
			gController.getGameFrame().addDialogue("Hier k�nnte man surfen!");
			gController.waitDialogue();
			if(c.canSurf()) {
				gController.getGameFrame().addDialogue("Du f�ngst an zu surfen!");
				c.setSurfing(true);
				gController.waitDialogue();
				c.changePosition(c.getCurrentDirection(), true);
			}
		} else if(isPC()) {
			gController.getGameFrame().displayPC(c);
		} else if(this.spriteName.equals("rock")) {
			gController.getGameFrame().addDialogue("Dieser Felsen könnte zertr�mmert werden!");
			gController.waitDialogue();
			if(c.canRocksmash()) {
				gController.getGameFrame().addDialogue("Du hast den Felsen zertrümmert!");
				this.setSprite("free");
				this.setAccessible(true);
				c.getCurrentRoute().updateMap(c.getInteractionPoint());
				gController.waitDialogue();
			}
			gController.getGameFrame().repaint();
		} else if(this.spriteName.equals("treecut")) {
			if(c.canCut()) {
				gController.getGameFrame().addDialogue("Du zerschneidest den Baum!");
				this.setAccessible(true);
				gController.waitDialogue();

				new Thread(new Runnable() {
					@Override
					public void run() {
						for(int i = 1; i <= 3; i++) {
							setSprite("treecut" + i);
							c.getCurrentRoute().updateMap(c.getInteractionPoint());
							gController.getGameFrame().repaint();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			} else {
				gController.getGameFrame().addDialogue("Dieser Baum k�nnte zerschnitten werden!");
				gController.waitDialogue();
			}
			gController.getGameFrame().repaint();
		}
	}

	public void setExactX(double x) {
		this.exactX = x;
	}

	public void setExactY(double y) {
		this.exactY = y;
	}

	public TriggeredEvent getEvent() {
		return event;
	}

	public void setEvent(TriggeredEvent event) {
		this.event = event;
	}
}

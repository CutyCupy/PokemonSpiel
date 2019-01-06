package de.alexanderciupka.pokemon.map;

import java.awt.Point;
import java.util.AbstractMap.SimpleEntry;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.characters.types.Walkable;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.TextLabel;
import de.alexanderciupka.pokemon.gui.panels.ReportPanel;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.PokemonInformation;
import de.alexanderciupka.pokemon.pokemon.Stat;

public class GameController {

	private Player mainCharacter;
	private Background currentBackground;
	private PokemonInformation information;
	private RouteAnalyzer routeAnalyzer;
	private GameFrame gameFrame;
	private boolean fighting;
	private Fighting fight;
	private boolean interactionPause;
	private MenuController mController;

	private static final Point START = new Point(5, 4);

	private static GameController instance;

	private GameController() {
	}

	public static GameController getInstance() {
		if (instance == null) {
			instance = new GameController();
			instance.start();
		}
		return instance;
	}

	private void start() {
		this.information = new PokemonInformation();
		this.routeAnalyzer = new RouteAnalyzer();
		this.routeAnalyzer.init();
		this.mController = MenuController.getInstance();
	}

	public boolean move(Direction moveDirection) {
		boolean result = false;
		if (!this.interactionPause) {
//			this.setInteractionPause(true);
			this.gameFrame.setDelay(TextLabel.SLOW);
			Point possiblePoint = this.mainCharacter.getCurrentPosition();
			if (this.mainCharacter.getCurrentDirection() == moveDirection) {
				switch (moveDirection) {
				case UP:
					result = this.updatePosition(possiblePoint.x, possiblePoint.y - 1);
					break;
				case DOWN:
					result = this.updatePosition(possiblePoint.x, possiblePoint.y + 1);
					break;
				case LEFT:
					result = this.updatePosition(possiblePoint.x - 1, possiblePoint.y);
					break;
				case RIGHT:
					result = this.updatePosition(possiblePoint.x + 1, possiblePoint.y);
					break;
				case NONE:
					break;
				}
			} else {
				result = true;
				this.mainCharacter.setCurrentDirection(moveDirection);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
//			this.setInteractionPause(false);
		}
		return result;
	}

	public boolean slide(Direction slideDirection) {
		boolean result = false;
		if (!this.interactionPause) {
			this.setInteractionPause(true);
			Point possiblePoint = this.mainCharacter.getCurrentPosition();
			switch (slideDirection) {
			case UP:
				result = this.updatePosition(possiblePoint.x, possiblePoint.y - 1);
				break;
			case DOWN:
				result = this.updatePosition(possiblePoint.x, possiblePoint.y + 1);
				break;
			case LEFT:
				result = this.updatePosition(possiblePoint.x - 1, possiblePoint.y);
				break;
			case RIGHT:
				result = this.updatePosition(possiblePoint.x + 1, possiblePoint.y);
				break;
			case NONE:
				break;
			}
			this.setInteractionPause(false);
		}
		return result;
	}

	private boolean updatePosition(int x, int y) {
		try {
			if (x >= this.mainCharacter.getCurrentRoute().getWidth() || x < 0
					|| y >= this.mainCharacter.getCurrentRoute().getHeight() || y < 0) {
				this.mainCharacter.setControllable(true);
				return false;
			}
			boolean changed = false;
			for (NPC stone : this.mainCharacter.getCurrentRoute().getEntity(x, y).getCharacters()) {
				if (stone != null && "strength".equals(stone.getID())
						&& this.mainCharacter.getTeam().canUseVM((Items.STRENGTH))) {
					stone.setCurrentDirection(this.mainCharacter.getCurrentDirection());
					if (this.mainCharacter.getCurrentRoute()
							.getEntity(stone.getInteractionPoint().x, stone.getInteractionPoint().y).isAccessible(stone)
							&& (this.currentBackground.getCurrentRoute()
									.getEntity(stone.getInteractionPoint().x, stone.getInteractionPoint().y)
									.getSpriteName().equals("free"))) {
						stone.changePosition(stone.getCurrentDirection(), true);
					}
					changed = true;
					break;
				}
			}
			if (!changed) {
				if (this.mainCharacter.getCurrentRoute().getEntity(x, y).isAccessible(this.mainCharacter)) {
					if (this.mainCharacter.isControllable()) {
						this.mainCharacter.changePosition(this.mainCharacter.getCurrentDirection(), true);
					} else {
						this.mainCharacter.slide(this.mainCharacter.getCurrentDirection());
					}
					return true;
				} else {
					this.mainCharacter.setControllable(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Pokemon getEncounterPokemon() {
		return this.currentBackground.chooseEncounter();
	}

	public Background getCurrentBackground() {
		return this.currentBackground;
	}

	public Player getMainCharacter() {
		return this.mainCharacter;
	}

	public void setMainCharacter(Player character) {
		this.mainCharacter = character;
	}

	public RouteAnalyzer getRouteAnalyzer() {
		return this.routeAnalyzer;
	}

	public PokemonInformation getPokemonInformation() {
		return this.information;
	}

	public void setCurrentRoute(Route newRoute) {
		if (this.currentBackground == null) {
			this.currentBackground = new Background(newRoute);
		} else {
			this.currentBackground.setCurrentRoute(newRoute);
		}
	}

	public GameFrame getGameFrame() {
		return this.gameFrame;
	}

	public boolean isFighting() {
		return this.fighting;
	}

	public void startFight(NPC enemy) {
		// this.gameFrame.addDialogue(enemy.getBeforeFightDialogue(), enemy);
		// TODO: Copy this after the opponent is next to the player
		// this.waitDialogue();
		this.fight = new Fighting(enemy);
		startFight();
	}

	public void startFight(NPC enemy1, NPC enemy2) {
		this.fight = new Fighting(enemy1, enemy2);
		startFight();
	}

	public void startFight(Pokemon enemy) {
		this.fight = new Fighting(enemy, true);
		startFight();
	}

	public void startFight(Pokemon enemy1, Pokemon enemy2) {
		this.fight = new Fighting(enemy1, enemy2, true);
		startFight();
	}

	private void startFight() {
		this.fighting = true;
		SoundController.getInstance().stopRain();
		SoundController.getInstance().playBattleSong(null);
		if (this.fight.canEscape()) {
			this.getGameFrame().getBackgroundLabel().startEncounter();
		} else {
			this.getGameFrame().getBackgroundLabel()
					.startFight(((NPC) this.fight.getCharacter(Fighting.LEFT_OPPONENT)).getLogo());
		}
		this.gameFrame.startFight();
		this.gameFrame.getFightPanel().showMenu();
	}

	public void escape() {
		if (this.fight.canEscape()) {
			SoundController.getInstance().playSound(SoundController.ESCAPE);
			this.fight.won = false;
			this.endFight();
		}
	}

	public void endFight() {
		SoundController.getInstance().updatePokemonLow();
		this.fighting = false;
		this.gameFrame.stopFight();
		if (this.fight.won) {
			if (!this.gameFrame.getEvolutionPanel().getPokemon().isEmpty()) {
				this.gameFrame.setCurrentPanel(this.gameFrame.getEvolutionPanel());
				this.gameFrame.getEvolutionPanel().start();
			}
		} else {
			for (int i : new int[] { Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT, Fighting.RIGHT_OPPONENT }) {
				Team t = this.fight.getTeam(i);
				if (t != null) {
					t.restoreTeam();
				}
			}
		}
		SoundController.getInstance().startRain(this.mainCharacter.getCurrentRoute().getRain());
	}

	// TODO: Update Attacks when one enemy is completely defeated
	public boolean winFight(Pokemon dead) {
		if (fight.isPlayer(dead)) {
			return false;
		}
		boolean left = fight.getIndex(dead) == Fighting.LEFT_OPPONENT;
		for (Pokemon p : this.fight.getParticipants(left)) {
			int XPGain = this.fight.calculateXP(dead, p);
			if (p.getStats().getLevel() < 100) {
				int xp = XPGain / this.fight.getParticipants(left).size();
				this.gameFrame.getFightPanel().addText(p.getName() + " erhält " + xp + " Erfahrungspunkte!");
				p.gainXP(xp);
			}
		}
		this.fight.won = this.fight.enemyDead();
		this.fight.remove(dead);
		if (this.fight.won) {
			if (this.fight.getCharacter(Fighting.LEFT_OPPONENT) instanceof NPC) {
				NPC opponent = ((NPC) this.fight.getCharacter(Fighting.LEFT_OPPONENT));
				opponent.onDefeat((Player) this.fight.getCharacter(Fighting.LEFT_PLAYER));
			}
			if (this.fight.getCharacter(Fighting.RIGHT_OPPONENT) instanceof NPC) {
				NPC opponent = ((NPC) this.fight.getCharacter(Fighting.RIGHT_OPPONENT));
				opponent.onDefeat((Player) this.fight.getCharacter(Fighting.LEFT_PLAYER));
			}
			this.endFight();
			return true;
		}
		return false;
	}

	public boolean loseFight() {
		if (!this.mainCharacter.getTeam().isAnyPokemonAlive()) {
			this.fight.won = false;
			this.gameFrame.getFightPanel().addText("Du wurdest besiegt!");
			this.getMainCharacter().decreaseMoney((long) (this.getMainCharacter().getMoney() * 0.1));
			this.gameFrame.getFightPanel().pause();
			this.mainCharacter.getCurrentRoute().reset();
			this.mainCharacter.warpToPokemonCenter();
			this.endFight();
			return true;
		}
		return false;
	}

	public Fighting getFight() {
		return this.fight;
	}

	public JPanel getCurrentFightPanel() {
		switch (this.fight.getCurrentFightOption()) {
		case FIGHT:
			return this.gameFrame.getFightPanel();
		case BAG:
			return this.gameFrame.getInventoryPanel();
		case POKEMON:
			return this.gameFrame.getPokemonPanel();
		case NEW_ATTACK:
			return this.gameFrame.getNewAttackPanel();
		case REPORT:
			return this.gameFrame.getReportPanel();
		default:
			return null;
		}
	}

	public void updateFight() {
		if (this.gameFrame.getFightPanel() != null) {
			this.gameFrame.getFightPanel().updateFight();
			this.gameFrame.getFightPanel().updatePanels();
		}
	}

	public void checkInteraction() {
		Point interactionPoint = this.mainCharacter.getInteractionPoint();
		if (!this.interactionPause
				&& this.mainCharacter.getCurrentRoute().getEntity(interactionPoint.x, interactionPoint.y) != null) {
			this.setInteractionPause(true);
			this.currentBackground.getCurrentRoute().getEntity(interactionPoint.x, interactionPoint.y)
					.onInteraction(this.mainCharacter);
			this.setInteractionPause(false);
		}
	}

	public SimpleEntry<NPC, NPC> checkStartFight(de.alexanderciupka.pokemon.characters.Character c) {
		if (!(c instanceof Player)) {
			return null;
		}
		NPC left = null;
		NPC right = null;
		for (NPC npc : this.currentBackground.getCurrentRoute().getCharacters()) {
			if (npc.checkStartFight((Player) c)) {
				if(npc instanceof Walkable) {
					((Walkable) npc).lock();
				}
				switch (npc.getFightingStyle()) {
				case NPC.DOUBLE:
					if (left == null) {
						left = npc;
						right = npc;
					}
					break;
				case NPC.FOLLOWER_DOUBLE:
					if (left == null) {
						left = npc;
						right = npc.getFollower();
					}
					break;
				case NPC.NO_DOUBLE:
					if (left != null) {
						right = npc;
					} else {
						left = npc;
					}
					break;
				}
			}
		}
		if (left != null) {
			return new SimpleEntry<NPC, NPC>(left, right);
		}
		return null;
	}

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public PokemonInformation getInformation() {
		return this.information;
	}

	public void startNewGame() {
		this.mainCharacter = new Player();
		this.mainCharacter.setCharacterImage("talih", "front");
		this.mainCharacter.setName("Talih");
		this.mainCharacter.setID("999");

		this.mainCharacter.setCurrentRoute(this.routeAnalyzer.getRouteById("pokemon_center"));
		this.mainCharacter.setCurrentPosition(1, 0);

		this.mainCharacter.getItems().put(Items.POKEBALL, 5 * 99);
		this.mainCharacter.getItems().put(Items.TRANK, 10);

		// mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("eigenes_zimmer"));
		// mainCharacter.setCurrentPosition(START.x, START.y);

		this.currentBackground = new Background(this.mainCharacter.getCurrentRoute());

		Pokemon player = new Pokemon(25);
		player.getStats().generateStats((short) 5);

		for (Stat s : Stat.values()) {
			switch (s) {
			case ACCURACY:
			case EVASION:
				continue;
			default:
				player.getStats().setDV(s, 31);
			}
		}

		this.mainCharacter.getTeam().addPokemon(player);

		player = new Pokemon(6);
		player.getStats().generateStats((short) 100);
		this.mainCharacter.getTeam().addPokemon(player);

		if (this.gameFrame == null) {
			this.gameFrame = new GameFrame();
		}

		this.gameFrame.getBackgroundLabel().changeRoute(this.getCurrentBackground().getCurrentRoute());

		this.currentBackground.getCamera().setCharacter(this.getMainCharacter(), false);

		this.mainCharacter.getCurrentRoute()
				.getEntity(this.mainCharacter.getCurrentPosition().x, this.mainCharacter.getCurrentPosition().y)
				.onStep(this.mainCharacter);
	}

	public boolean loadGame(String path) {
		this.mainCharacter = new Player();
		this.mainCharacter.setCharacterImage("talih", "front");
		if (this.routeAnalyzer.loadGame(path)) {
			if (this.gameFrame == null) {
				this.gameFrame = new GameFrame();
			}
			this.gameFrame.getBackgroundLabel().changeRoute(this.currentBackground.getCurrentRoute());
			return true;
		}
		return false;
	}

	public void waitDialogue() {
		this.gameFrame.getDialogue().waitText();
	}

	public void saveGame() {
//		if (!this.interactionPause) {
			this.interactionPause = true;
			if (!this.routeAnalyzer.saveGame(this.mController.saveGame())) {
				JOptionPane.showMessageDialog(this.gameFrame, "Das Speichern ist fehlgeschlagen!",
						"Speichern fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
			}
			this.interactionPause = false;
//		}
	}

	public boolean getInteractionPause() {
		return this.interactionPause;
	}

	public void returnToMenu() {
		int choice = this.mController.returnToMenu();
		switch (choice) {
		case 0:
			this.saveGame();
			break;
		case 1:
			this.saveGame();
		case 3:
			this.gameFrame.setVisible(false);
			SoundController.getInstance().stopRain();
			this.mController.showMenu();
			break;
		case 2:
			SoundController.getInstance().stopRain();
			this.mController.loadGame();
			break;
		default:
			break;
		}

	}

	public void displayReport(Pokemon pokemon, Pokemon[] others) {
		if (this.isFighting()) {
			this.fight.setCurrentFightOption(FightOption.REPORT);
		}
		this.gameFrame.setCurrentPanel(this.gameFrame.getReportPanel());
		((ReportPanel) this.gameFrame.getReportPanel()).setPokemon(pokemon, others, this.gameFrame.getPokemonPanel());
	}

	public void setInteractionPause(boolean b) {
		this.interactionPause = b;
	}

	public TextLabel getCurrentTextLabel() {
		if (this.isFighting()) {
			return this.getGameFrame().getFightPanel().getTextLabel();
		} else {
			return this.getGameFrame().getDialogue();
		}
	}

}

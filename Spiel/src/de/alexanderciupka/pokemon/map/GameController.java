package de.alexanderciupka.pokemon.map;

import java.awt.Point;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.characters.RandomWalker;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.TextLabel;
import de.alexanderciupka.pokemon.gui.panels.ReportPanel;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Item;
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
			this.setInteractionPause(true);
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
			this.setInteractionPause(false);
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
			for (NPC stone : this.mainCharacter.getCurrentRoute().getEntities()[y][x].getCharacters()) {
				if (stone != null && stone.getID().equals("strength") && this.mainCharacter.hasItem(Item.STRENGTH)) {
					this.mainCharacter.getCurrentRoute().updateMap(new Point(x, y));
					stone.setCurrentDirection(this.mainCharacter.getCurrentDirection());
					if (this.mainCharacter.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone
							.getInteractionPoint().x].isAccessible(stone)
							&& (this.currentBackground.getCurrentRoute()
									.getEntities()[stone.getInteractionPoint().y][stone.getInteractionPoint().x]
											.getSpriteName().equals("free"))) {
						this.mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
						stone.changePosition(stone.getCurrentDirection(), true);
						this.mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
					}
					changed = true;
					break;
				}
			}
			if (!changed) {
				if (this.mainCharacter.getCurrentRoute().getEntities()[y][x].isAccessible(this.mainCharacter)) {
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
		this.gameFrame.addDialogue(enemy.getBeforeFightDialogue(), enemy);
		this.waitDialogue();
		this.fighting = true;
		SoundController.getInstance().stopRain();
		SoundController.getInstance().playBattleSong(enemy.getName());
		this.gameFrame.getBackgroundLabel().startFight(enemy.getLogo());
		this.fight = new Fighting(enemy);
		this.gameFrame.startFight(this.fight.getPlayer(), this.fight.getEnemy());
		this.gameFrame.getFightPanel().showMenu();
	}

	public void startFight(Pokemon enemy) {
		this.fight = new Fighting(enemy);
		this.fighting = true;
		SoundController.getInstance().stopRain();
		SoundController.getInstance().playBattleSong(null);
		this.getGameFrame().getBackgroundLabel().startEncounter();
		this.gameFrame.startFight(this.fight.getPlayer(), this.fight.getEnemy());
		this.gameFrame.getFightPanel().showMenu();
	}

	public boolean checkDead(Pokemon p) {
		if (p.getStats().getCurrentHP() == 0) {
			p.changeHappiness(-1);
			return true;
		}
		return false;
	}

	public void escape() {
		if (this.fight.canEscape()) {
			SoundController.getInstance().playSound(SoundController.ESCAPE);
			this.fight.won = false;
			this.endFight();
		}
	}

	public void endFight() {
		SoundController.getInstance().updatePokemonLow(null);
		this.fighting = false;
		this.gameFrame.stopFight();
		if (this.fight.won) {
			if (!this.gameFrame.getEvolutionPanel().getPokemon().isEmpty()) {
				this.gameFrame.setCurrentPanel(this.gameFrame.getEvolutionPanel());
				this.gameFrame.getEvolutionPanel().start();
			}
			if (this.fight.getEnemyCharacter() != null && this.fight.getEnemyCharacter().hasRewards()) {
				this.mainCharacter.earnRewards(this.fight.getEnemyCharacter().getRewards(), true);
				this.fight.getEnemyCharacter().getRewards().clear();
			}
		}
		SoundController.getInstance().startRain(this.mainCharacter.getCurrentRoute().getRain());
	}

	public boolean winFight() {
		for (Pokemon p : this.fight.getParticipants()) {
			int XPGain = this.fight.calculateXP(p);
			if (p.getStats().getLevel() < 100) {
				int xp = XPGain / this.fight.getParticipants().size();
				this.gameFrame.getFightPanel().addText(p.getName() + " erhält " + xp + " Erfahrungspunkte!");
				p.gainXP(xp);
			}
		}
		if (this.fight.enemyDead()) {
			this.fight.won = true;
			if (this.fight.getEnemyCharacter() != null) {
				this.gameFrame.getFightPanel().addText(this.fight.getEnemyCharacter().getOnDefeatDialogue());
				this.getGameFrame().getFightPanel()
						.addText("Du erhälst " + this.fight.getEnemyCharacter().getMoney() + " Cupydollar!");
				this.getMainCharacter().increaseMoney(this.fight.getEnemyCharacter().getMoney());
				this.gameFrame.getFightPanel().pause();
			}
			this.endFight();
			return true;
		}
		return false;
	}

	public boolean loseFight() {
		if (this.fight.playerDead()) {
			this.fight.won = false;
			this.gameFrame.getFightPanel().addText("Du wurdest besiegt!");
			this.getMainCharacter().decreaseMoney((long) (this.getMainCharacter().getMoney() * 0.1));
			this.gameFrame.getFightPanel().pause();
			this.resetCharacterPositions();
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
			this.gameFrame.getFightPanel().setPlayer();
			this.gameFrame.getFightPanel().setEnemy();
			this.gameFrame.getFightPanel().updatePanels();
		}
	}

	public void checkInteraction() {
		Point interactionPoint = this.mainCharacter.getInteractionPoint();
		if (!this.interactionPause && interactionPoint.y >= 0
				&& interactionPoint.y < this.mainCharacter.getCurrentRoute().getHeight() && interactionPoint.x >= 0
				&& interactionPoint.x < this.mainCharacter.getCurrentRoute().getWidth()) {
			this.setInteractionPause(true);
			this.currentBackground.getCurrentRoute().getEntities()[interactionPoint.y][interactionPoint.x]
					.onInteraction(this.mainCharacter);
			this.setInteractionPause(false);
		}
	}

	public int checkStartFight() {
		for (int i = 0; i < this.currentBackground.getCurrentRoute().getCharacters().size(); i++) {
			int distance = this.currentBackground.getCurrentRoute().getCharacters().get(i).checkStartFight();
			if (distance > 0) {
				return i;
			}
		}
		return -1;
	}

	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void resetCharacterPositions() {
		for (NPC c : this.currentBackground.getCurrentRoute().getCharacters()) {
			c.resetPosition();
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
		this.mainCharacter.setCurrentPosition(0, 0);

		this.mainCharacter.getItems().put(Item.POKEBALL, 5 * 99);
		this.mainCharacter.getItems().put(Item.POTION, 10);

		// mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("eigenes_zimmer"));
		// mainCharacter.setCurrentPosition(START.x, START.y);

		RandomWalker spin = new RandomWalker();

		spin.setCharacterImage("cutycupy", "front");

		spin.setCurrentPosition(new Point(2, 1));
		spin.setCurrentRoute(this.mainCharacter.getCurrentRoute());

		this.mainCharacter.getCurrentRoute().addCharacter(spin);

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

		System.out.println(player.getAbility().getName());

		this.mainCharacter.getTeam().addPokemon(player);

		if (this.gameFrame == null) {
			this.gameFrame = new GameFrame();
		}

		this.routeAnalyzer.updateHatches(this.mainCharacter.getCurrentRoute());

		this.gameFrame.getBackgroundLabel().changeRoute(this.getCurrentBackground().getCurrentRoute());

		this.currentBackground.getCamera().setCharacter(this.getMainCharacter(), false);

		this.mainCharacter.getCurrentRoute().getEntities()[this.mainCharacter.getCurrentPosition().y][this.mainCharacter
				.getCurrentPosition().x].onStep(this.mainCharacter);

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
		if (!this.interactionPause) {
			this.interactionPause = true;
			if (!this.mainCharacter.getCurrentRoute().getId().equals("pokemon_center")) {
				if (!this.routeAnalyzer.saveGame(this.mController.saveGame())) {
					JOptionPane.showMessageDialog(this.gameFrame, "Das Speichern ist fehlgeschlagen!",
							"Speichern fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this.gameFrame, "Du kannst nicht im Pokemon Center speichern!",
						"Speichern fehlgeschlagen!", JOptionPane.ERROR_MESSAGE);
			}
			this.interactionPause = false;
		}
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

}

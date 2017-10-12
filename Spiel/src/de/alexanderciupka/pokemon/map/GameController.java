package de.alexanderciupka.pokemon.map;

import java.awt.Point;

import javax.swing.JPanel;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
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
		information = new PokemonInformation();
		routeAnalyzer = new RouteAnalyzer();
		routeAnalyzer.init();
		mController = MenuController.getInstance();
	}

	public boolean move(Direction moveDirection) {
		boolean result = false;
		if (!interactionPause) {
			setInteractionPause(true);
			gameFrame.setDelay(TextLabel.SLOW);
			Point possiblePoint = mainCharacter.getCurrentPosition();
			if (mainCharacter.getCurrentDirection() == moveDirection) {
				switch (moveDirection) {
				case UP:
					result = updatePosition(possiblePoint.x, possiblePoint.y - 1);
					break;
				case DOWN:
					result = updatePosition(possiblePoint.x, possiblePoint.y + 1);
					break;
				case LEFT:
					result = updatePosition(possiblePoint.x - 1, possiblePoint.y);
					break;
				case RIGHT:
					result = updatePosition(possiblePoint.x + 1, possiblePoint.y);
					break;
				}
			} else {
				result = true;
				mainCharacter.setCurrentDirection(moveDirection);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setInteractionPause(false);
		}
		return result;
	}

	public boolean slide(Direction slideDirection) {
		boolean result = false;
		if (!interactionPause) {
			setInteractionPause(true);
			Point possiblePoint = mainCharacter.getCurrentPosition();
			switch (slideDirection) {
			case UP:
				result = updatePosition(possiblePoint.x, possiblePoint.y - 1);
				break;
			case DOWN:
				result = updatePosition(possiblePoint.x, possiblePoint.y + 1);
				break;
			case LEFT:
				result = updatePosition(possiblePoint.x - 1, possiblePoint.y);
				break;
			case RIGHT:
				result = updatePosition(possiblePoint.x + 1, possiblePoint.y);
				break;
			}
			setInteractionPause(false);
		}
		return result;
	}

	private boolean updatePosition(int x, int y) {
		try {
			if (x >= mainCharacter.getCurrentRoute().getWidth() || x < 0
					|| y >= mainCharacter.getCurrentRoute().getHeight() || y < 0) {
				mainCharacter.setControllable(true);
				return false;
			}
			boolean changed = false;
			for (NPC stone : mainCharacter.getCurrentRoute().getEntities()[y][x].getCharacters()) {
				if (stone != null && stone.getID().equals("strength") && mainCharacter.hasItem(Item.STRENGTH)) {
					mainCharacter.getCurrentRoute().updateMap(new Point(x, y));
					stone.setCurrentDirection(mainCharacter.getCurrentDirection());
					if (mainCharacter.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone
							.getInteractionPoint().x].isAccessible(stone)
							&& (currentBackground.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone
									.getInteractionPoint().x].getSpriteName().equals("free"))) {
						mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
						stone.changePosition(stone.getCurrentDirection(), true);
						mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
					}
					changed = true;
					break;
				}
			}
			if (!changed) {
				if (mainCharacter.getCurrentRoute().getEntities()[y][x].isAccessible(mainCharacter)) {
					if (mainCharacter.isControllable()) {
						mainCharacter.changePosition(mainCharacter.getCurrentDirection(), true);
					} else {
						mainCharacter.slide(mainCharacter.getCurrentDirection());
					}
					return true;
				} else {
					mainCharacter.setControllable(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Pokemon getEncounterPokemon() {
		return currentBackground.chooseEncounter();
	}

	public Background getCurrentBackground() {
		return currentBackground;
	}

	public Player getMainCharacter() {
		return mainCharacter;
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
		if (currentBackground == null) {
			this.currentBackground = new Background(newRoute);
		} else {
			currentBackground.setCurrentRoute(newRoute);
		}
	}

	public GameFrame getGameFrame() {
		return this.gameFrame;
	}

	public boolean isFighting() {
		return this.fighting;
	}

	public void startFight(NPC enemy) {
		gameFrame.addDialogue(enemy.getName() + ": " + enemy.getBeforeFightDialogue());
		waitDialogue();
		this.fighting = true;
		SoundController.getInstance().playBattleSong(enemy.getName());
		this.gameFrame.getBackgroundLabel().startFight(enemy.getLogo());
		this.fight = new Fighting(enemy);
		gameFrame.startFight(fight.getPlayer(), fight.getEnemy());
		gameFrame.getFightPanel().showMenu();
	}

	public void startFight(Pokemon enemy) {
		this.fight = new Fighting(enemy);
		this.fighting = true;
		SoundController.getInstance().playBattleSong(null);
		getGameFrame().getBackgroundLabel().startEncounter();
		gameFrame.startFight(fight.getPlayer(), fight.getEnemy());
		gameFrame.getFightPanel().showMenu();
	}

//	public boolean playerAttack(Move move) {
//		return fight.playerAttack(move);
//	}
//
//	public boolean enemyAttack() {
//		return fight.enemyAttack();
//	}
//
//	public boolean enemyAttack(Move move) {
//		return fight.enemyAttack(move);
//	}
	public boolean checkDead(Pokemon p) {
		if (p.getStats().getCurrentHP() == 0) {
			p.changeHappiness(-1);
			return true;
		}
		return false;
	}

	public void escape() {
		if (fight.canEscape()) {
			SoundController.getInstance().playSound(SoundController.ESCAPE);
			this.fight.won = false;
			endFight();
		}
	}

	public void endFight() {
		SoundController.getInstance().updatePokemonLow(null);
		this.fighting = false;
//		for (int i = 0; i < this.mainCharacter.getTeam().getAmmount(); i++) {
//			this.mainCharacter.getTeam().getTeam()[i].
//		}
		gameFrame.stopFight();
		if (this.fight.won) {
			if (!gameFrame.getEvolutionPanel().getPokemon().isEmpty()) {
				gameFrame.setCurrentPanel(gameFrame.getEvolutionPanel());
				gameFrame.getEvolutionPanel().start();
			}
			if (fight.getEnemyCharacter() != null && fight.getEnemyCharacter().getReward() != Item.NONE) {
				this.getGameFrame().addDialogue(fight.getEnemyCharacter().getName() + ": " + "Nehme das als ein Geschenk von mir!");
				this.getGameFrame().getDialogue().waitText();
				this.getGameFrame().addDialogue("Du hast " + fight.getEnemyCharacter().getReward().getName() + " erhalten!");
				this.mainCharacter.addItem(fight.getEnemyCharacter().getReward());
			}
		}
	}

	public boolean winFight() {
		for (Pokemon p : fight.getParticipants()) {
			int XPGain = fight.calculateXP(p);
			if (p.getStats().getLevel() < 100) {
				int xp = XPGain / fight.getParticipants().size();
				gameFrame.getFightPanel().addText(p.getName() + " erhält " + xp + " Erfahrungspunkte!");
				p.gainXP(xp);
			}
		}
		if (fight.enemyDead()) {
			this.fight.won = true;
			if (fight.getEnemyCharacter() != null) {
				gameFrame.getFightPanel().addText(fight.getEnemyCharacter().getOnDefeatDialogue());
				this.getGameFrame().getFightPanel()
						.addText("Du erhälst " + fight.getEnemyCharacter().getMoney() + " Cupydollar!");
				this.getMainCharacter().increaseMoney(fight.getEnemyCharacter().getMoney());
				gameFrame.getFightPanel().pause();
			}
			endFight();
			return true;
		}
		return false;
	}

	public boolean loseFight() {
		if (fight.playerDead()) {
			this.fight.won = false;
			gameFrame.getFightPanel().addText("Du wurdest besiegt!");
			this.getMainCharacter().decreaseMoney((long) (this.getMainCharacter().getMoney() * 0.1));
			gameFrame.getFightPanel().pause();
			resetCharacterPositions();
			mainCharacter.warpToPokemonCenter();
			endFight();
			return true;
		}
		return false;
	}

	public Fighting getFight() {
		return this.fight;
	}

	public JPanel getCurrentFightPanel() {
		switch (fight.getCurrentFightOption()) {
		case FIGHT:
			return gameFrame.getFightPanel();
		case BAG:
			return gameFrame.getInventoryPanel();
		case POKEMON:
			return gameFrame.getPokemonPanel();
		case NEW_ATTACK:
			return gameFrame.getNewAttackPanel();
		case REPORT:
			return gameFrame.getReportPanel();
		default:
			return null;
		}
	}

	public void updateFight() {
		if(gameFrame.getFightPanel() != null) {
			gameFrame.getFightPanel().setPlayer();
			gameFrame.getFightPanel().setEnemy();
			gameFrame.getFightPanel().updatePanels();
		}
	}

	public void checkInteraction() {
		Point interactionPoint = mainCharacter.getInteractionPoint();
		if (!interactionPause && interactionPoint.y >= 0
				&& interactionPoint.y < mainCharacter.getCurrentRoute().getHeight() && interactionPoint.x >= 0
				&& interactionPoint.x < mainCharacter.getCurrentRoute().getWidth()) {
			setInteractionPause(true);
			currentBackground.getCurrentRoute().getEntities()[interactionPoint.y][interactionPoint.x]
					.onInteraction(mainCharacter);
			setInteractionPause(false);
		}
	}

	public int checkStartFight() {
		for (int i = 0; i < currentBackground.getCurrentRoute().getCharacters().size(); i++) {
			int distance = currentBackground.getCurrentRoute().getCharacters().get(i).checkStartFight();
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

	public void repaint() {
		// gameFrame.paint(gameFrame.getGraphics());
	}

	public void resetCharacterPositions() {
		for (NPC c : currentBackground.getCurrentRoute().getCharacters()) {
			c.resetPosition();
		}
	}

	public PokemonInformation getInformation() {
		return this.information;
	}

	public void startNewGame() {
		mainCharacter = new Player();
		mainCharacter.setCharacterImage("talih", "front");
		mainCharacter.setName("Talih");
		mainCharacter.setID("999");
		mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("haddonfield"));
		mainCharacter.setCurrentPosition(32, 9);

		// for (int i = 0; i < 5; i++) {
		mainCharacter.getItems().put(Item.POKEBALL, 5);
		mainCharacter.getItems().put(Item.RARECANDY, 1);
		mainCharacter.getItems().put(Item.THUNDERSTONE, 1);
//		mainCharacter.getItems().put(Item.RARECANDY, 1);
		// mainCharacter.addItem(Item.POKEBALL);
		// }

//		for (Item i : Item.values()) {
			// if(i.name().contains("BALL")) {
			// mainCharacter.addItem(i);
//			mainCharacter.getItems().put(i, 5);
			// mainCharacter.addItem(i);
//			 mainCharacter.addItem(i);
//			 }
//		}

//		 mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("eigenes_zimmer"));
//		 mainCharacter.setCurrentPosition(START.x, START.y);
		currentBackground = new Background(mainCharacter.getCurrentRoute());

		Pokemon player = new Pokemon(150);
		player.getStats().generateStats((short) 100);

		player.setName("Mandarine");

//		player.addMove(player.getMoves()[0].getName(), information.getMoveByName("Teleport"));

//		player.setMoves(new Move[4]);
//		player.addMove("Heuler");
//		player.addMove("Jauler");
//		player.addMove("Fliegen");
//		player.addMove("Taucher");
//		player.addMove("Schemenkraft");

		mainCharacter.getTeam().addPokemon(player);
//		for (int i = 0; i < 5; i++) {
//			Pokemon p = new Pokemon(i + 1);
//			p.getStats().generateStats((short) 10);
//			mainCharacter.getTeam().addPokemon(p);
//		}

		// for(int i = 0; i < 3; i++) {
		// Pokemon foo = new Pokemon(new Random().nextInt(649) + 1);
		// foo.getStats().generateStats((short) 100);
		// mainCharacter.getPC().getBoxes()[0].addPokemon(foo, i * 10);
		// }

		// Random rng = new Random();
		// for(int i = 0; i < 5; i++) {
		// Pokemon current = new Pokemon(rng.nextInt(649) + 1);
		// current.getStats().generateStats((short) (rng.nextInt(100) + 1));
		// mainCharacter.getTeam().addPokemon(current);
		// }
		if(gameFrame == null) {
			gameFrame = new GameFrame();
		}

		gameFrame.getBackgroundLabel().changeRoute(getCurrentBackground().getCurrentRoute());

		currentBackground.getCamera().setCharacter(getMainCharacter(), false);


		mainCharacter.getCurrentRoute().getEntities()[mainCharacter.getCurrentPosition().y][mainCharacter.getCurrentPosition().x].onStep(mainCharacter);

		// SnowOverlay s = new SnowOverlay(gameFrame.getBackgroundLabel(), new
		// Dimension(630, 630), SnowType.BLIZZARD);
		// s.createOverlay();
		// gameFrame.getBackgroundLabel().addOverlay(s);
		// s.startAnimation();

		// information.getGender(300);
	}

	public boolean loadGame(String path) {
		mainCharacter = new Player();
		mainCharacter.setCharacterImage("talih", "front");
		if (routeAnalyzer.loadGame(path)) {
			if(gameFrame == null) {
				gameFrame = new GameFrame();
			}
			gameFrame.getBackgroundLabel().changeRoute(currentBackground.getCurrentRoute());
			this.repaint();
			return true;
		}
		return false;
	}

	public void waitDialogue() {
//		gameFrame.setActive();
		gameFrame.getDialogue().waitText();
	}

	public void saveGame() {
		if (!interactionPause) {
			interactionPause = true;
			if (!routeAnalyzer.saveGame(mController.saveGame())) {
				System.err.println("ERROR");
			}
			interactionPause = false;
		}
	}

	public boolean getInteractionPause() {
		return this.interactionPause;
	}

	public void returnToMenu() {
		int choice = mController.returnToMenu();
		switch (choice) {
		case 0:
			this.saveGame();
			break;
		case 1:
			this.saveGame();
		case 3:
			gameFrame.setVisible(false);
			this.mController.showMenu();
			break;
		case 2:
			this.mController.loadGame();
			break;
		default:
			break;
		}

	}

	public void displayReport(Pokemon pokemon, Pokemon[] others) {
		if (isFighting()) {
			this.fight.setCurrentFightOption(FightOption.REPORT);
		}
		this.gameFrame.setCurrentPanel(this.gameFrame.getReportPanel());
		((ReportPanel) this.gameFrame.getReportPanel()).setPokemon(pokemon, others, this.gameFrame.getPokemonPanel());
		this.repaint();
	}

	public void setInteractionPause(boolean b) {
		this.interactionPause = b;
	}

}

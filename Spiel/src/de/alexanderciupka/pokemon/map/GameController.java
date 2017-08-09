package de.alexanderciupka.pokemon.map;

import java.awt.Point;

import javax.swing.JPanel;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.gui.ReportPanel;
import de.alexanderciupka.pokemon.gui.TextLabel;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Direction;
import de.alexanderciupka.pokemon.pokemon.FightOption;
import de.alexanderciupka.pokemon.pokemon.Fighting;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.NPC;
import de.alexanderciupka.pokemon.pokemon.Player;
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
		mController = MenuController.getInstance();
	}

	public void move(Direction moveDirection) {
		System.out.println("Move");
		if (!interactionPause) {
			setInteractionPause(true);
			gameFrame.setDelay(TextLabel.SLOW);
			Point possiblePoint = mainCharacter.getCurrentPosition();
			if (mainCharacter.getCurrentDirection() == moveDirection) {
				switch (moveDirection) {
				case UP:
					updatePosition(possiblePoint.x, possiblePoint.y - 1);
					break;
				case DOWN:
					updatePosition(possiblePoint.x, possiblePoint.y + 1);
					break;
				case LEFT:
					updatePosition(possiblePoint.x - 1, possiblePoint.y);
					break;
				case RIGHT:
					updatePosition(possiblePoint.x + 1, possiblePoint.y);
					break;
				}
			} else {
				mainCharacter.setCurrentDirection(moveDirection);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setInteractionPause(false);
		}
	}

	public void slide(Direction slideDirection) {
		System.out.println("Slide");
		if (!interactionPause) {
			setInteractionPause(true);
			Point possiblePoint = mainCharacter.getCurrentPosition();
			switch (slideDirection) {
			case UP:
				updatePosition(possiblePoint.x, possiblePoint.y - 1);
				break;
			case DOWN:
				updatePosition(possiblePoint.x, possiblePoint.y + 1);
				break;
			case LEFT:
				updatePosition(possiblePoint.x - 1, possiblePoint.y);
				break;
			case RIGHT:
				updatePosition(possiblePoint.x + 1, possiblePoint.y);
				break;
			}
			setInteractionPause(false);
		}
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
				if (stone != null && stone.getID().equals("strength") && mainCharacter.canStrength()) {
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

	public boolean checkForBattle() {
		return currentBackground.checkEncounter(mainCharacter.getCurrentPosition());
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
		mainCharacter.setCurrentRoute(newRoute);
		currentBackground.setCurrentRoute(newRoute);
	}

	public GameFrame getGameFrame() {
		return this.gameFrame;
	}

	public boolean isFighting() {
		return this.fighting;
	}

	public void startFight(NPC enemy) {
		if (enemy.moveTowardsMainCharacter()) {
			// repaint();
			gameFrame.addDialogue(enemy.getName() + ": " + enemy.getBeforeFightDialogue());
			waitDialogue();
			this.fighting = true;
			this.fight = new Fighting(enemy);
			gameFrame.startFight(fight.getPlayer(), fight.getEnemy());
		}
	}

	public void startFight(Pokemon enemy) {
		this.fighting = true;
		this.fight = new Fighting(enemy);
		gameFrame.startFight(fight.getPlayer(), fight.getEnemy());
		gameFrame.getFightPanel().addText("Ein wildes " + enemy.getName() + " erscheint!");
		gameFrame.getFightPanel().showMenu();
	}

	public boolean playerAttack(Move move) {
		return fight.playerAttack(move);
	}

	public boolean enemyAttack() {
		return fight.enemyAttack();
	}

	public boolean enemyAttack(Move move) {
		return fight.enemyAttack(move);
	}

	public boolean checkEnemyDead() {
		if (fight.getEnemy().getStats().getCurrentHP() == 0) {
			return true;
		}
		return false;
	}

	public boolean checkPlayerDead() {
		if (fight.getPlayer().getStats().getCurrentHP() == 0) {
			return true;
		}
		return false;
	}

	public void escape() {
		if (fight.canEscape()) {
			endFight();
		}
	}

	public void endFight() {
		this.fighting = false;
		for (int i = 0; i < this.mainCharacter.getTeam().getAmmount(); i++) {
			switch (this.mainCharacter.getTeam().getTeam()[i].getAilment()) {
			case CONFUSION:
				this.mainCharacter.getTeam().getTeam()[i].setAilment(Ailment.NONE);
				break;
			default:
				break;

			}
		}
		gameFrame.stopFight();
	}

	public boolean winFight() {
		for (Pokemon p : fight.getParticipants()) {
			int XPGain = fight.calculateXP(p);
			if (p.gainXP(XPGain / fight.getParticipants().size())) {
				gameFrame.getFightPanel().addText(p.getName() + " erh�lt " + XPGain + " Erfahrungspunkte!");
			}
		}
		if (fight.enemyDead()) {
			if (fight.getEnemyCharacter() != null) {
				this.getMainCharacter().addItem(fight.getEnemyCharacter().getReward());
				gameFrame.getFightPanel().addText(fight.getEnemyCharacter().getOnDefeatDialogue());
				gameFrame.getFightPanel().pause();
			}
			endFight();
			return true;
		}
		return false;
	}

	public boolean loseFight() {
		if (fight.playerDead()) {
			gameFrame.getFightPanel().addText(
					"Du hast keine kampff�higen Pokemon mehr ... Dir wird schwarz vor Augen und rennst so schnell wie m�glich zu einem Pokemon Center!");
			gameFrame.getFightPanel().pause();
			endFight();
			resetCharacterPositions();
			mainCharacter.warpToPokemonCenter();
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
			return gameFrame.getBagPanel();
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
		gameFrame.getFightPanel().setPlayer();
	}

	public void checkInteraction() {
		System.out.println("Check Interaction");
		if (!interactionPause) {
			setInteractionPause(true);
			Point interactionPoint = mainCharacter.getInteractionPoint();
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
		gameFrame.paint(gameFrame.getGraphics());
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
		mainCharacter.setCharacterImage("main", "front");
		mainCharacter.setName("Talih");
		mainCharacter.setID("999");
//		mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("eigenes_zimmer"));
//		mainCharacter.setCurrentPosition(START.x, START.y);
		mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("winterhude"));
		mainCharacter.setCurrentPosition(25, 15);
		currentBackground = new Background(mainCharacter.getCurrentRoute());
		Pokemon player = new Pokemon(152);
		player.setName("Mandarine");
		player.getStats().generateStats((short) 5);
		mainCharacter.getTeam().addPokemon(player);
		gameFrame = new GameFrame();
	}

	public boolean loadGame(String path) {
		mainCharacter = new Player();
		mainCharacter.setCharacterImage("main", "front");
		if (routeAnalyzer.loadGame(path)) {
			currentBackground = new Background(mainCharacter.getCurrentRoute());
			gameFrame = new GameFrame();
			this.repaint();
			return true;
		}
		return false;
	}

	public void waitDialogue() {
		gameFrame.setActive();
		while (!gameFrame.isDialogueEmpty()) {
			sleep(50);
			this.gameFrame.getDialogue().repaint();
		}
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

	public void displayReport(Pokemon pokemon) {
		if (isFighting()) {
			this.fight.setCurrentFightOption(FightOption.REPORT);
		}
		((ReportPanel) this.gameFrame.getReportPanel()).setPokemon(pokemon, this.gameFrame.getPokemonPanel());
		this.gameFrame.setCurrentPanel(this.gameFrame.getReportPanel());
	}

	public void setInteractionPause(boolean b) {
		System.out.println("Interaction Pause: " + b);
		this.interactionPause = b;
	}

}
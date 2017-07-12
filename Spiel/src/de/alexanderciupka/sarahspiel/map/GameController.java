package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;

import javax.swing.JPanel;

import de.alexanderciupka.sarahspiel.gui.GameFrame;
import de.alexanderciupka.sarahspiel.gui.ReportPanel;
import de.alexanderciupka.sarahspiel.gui.TextLabel;
import de.alexanderciupka.sarahspiel.menu.MenuController;
import de.alexanderciupka.sarahspiel.pokemon.Ailment;
import de.alexanderciupka.sarahspiel.pokemon.Direction;
import de.alexanderciupka.sarahspiel.pokemon.FightOption;
import de.alexanderciupka.sarahspiel.pokemon.Fighting;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.NPC;
import de.alexanderciupka.sarahspiel.pokemon.Player;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;
import de.alexanderciupka.sarahspiel.pokemon.PokemonInformation;

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
		if (!interactionPause && mainCharacter.isControllable()) {
			interactionPause = true;
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
			}
			interactionPause = false;
		}
	}

	public void slide(Direction slideDirection) {
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
	}

	private boolean updatePosition(int x, int y) {
		try {
			System.out.println(x);
			System.out.println(y);
			System.out.println(currentBackground.getCurrentRoute().getName());
			if(x >= mainCharacter.getCurrentRoute().getWidth() || x < 0 || y >= mainCharacter.getCurrentRoute().getHeight() || y < 0) {
				mainCharacter.setControllable(true);
				return false;
			}
			boolean changed = false;
			for(NPC stone : mainCharacter.getCurrentRoute().getEntities()[y][x].getCharacters()) {
				if(stone != null && stone.getID().equals("strength") && mainCharacter.canStrength()) {
					mainCharacter.getCurrentRoute().updateMap(new Point(x, y));
					stone.setCurrentDirection(mainCharacter.getCurrentDirection());
					if(mainCharacter.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone.getInteractionPoint().x].isAccessible(stone) && (currentBackground.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone.getInteractionPoint().x].getSpriteName().equals("free"))){
						mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
						mainCharacter.getCurrentRoute().getEntities()[y][x].removeCharacter();
						mainCharacter.getCurrentRoute().getEntities()[stone.getInteractionPoint().y][stone.getInteractionPoint().x].addCharacter(stone);
						stone.changePosition(stone.getCurrentDirection(), true);
						mainCharacter.getCurrentRoute().updateMap(stone.getCurrentPosition());
//					currentBackground.getCurrentRoute().getEntities()[y][x].onStep(mainCharacter);
					}
					changed = true;
					break;
				}
			}
			if(!changed) {
				if (mainCharacter.getCurrentRoute().getEntities()[y][x].isAccessible(mainCharacter)) {
					if(mainCharacter.isControllable()) {
						mainCharacter.changePosition(mainCharacter.getCurrentDirection(), true);
					} else {
						mainCharacter.slide(mainCharacter.getCurrentDirection());
					}
//					mainCharacter.getCurrentRoute().getEntities()[mainCharacter.getCurrentPosition().y][mainCharacter.getCurrentPosition().x].onStep(mainCharacter);
					interactionPause = false;
					return true;
				} else {
					mainCharacter.setControllable(true);
				}
			}
		} catch(Exception e) {
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
		if(enemy.moveTowardsMainCharacter()) {
//			repaint();
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
		for(int i = 0; i < this.mainCharacter.getTeam().getAmmount(); i++) {
			switch(this.mainCharacter.getTeam().getTeam()[i].getAilment()) {
			case CONFUSION:
				this.mainCharacter.getTeam().getTeam()[i].setAilment(Ailment.NONE);
				break;
			default:
				break;

			}
		}
		gameFrame.stopFight();
	}

	public boolean winFight(int XPGain) {
		for (Pokemon p : fight.getParticipants()) {
			if(p.gainXP(XPGain / fight.getParticipants().size())) {
				gameFrame.getFightPanel().addText(p.getName() + " erh�lt " + XPGain + " Erfahrungspunkte!");
			}
		}
		if (fight.enemyDead()) {
			if(fight.getEnemyCharacter() != null) {
				this.getMainCharacter().addItem(fight.getEnemyCharacter().getReward());
			}
			gameFrame.getFightPanel().pause();
			endFight();
			return true;
		}
		return false;
	}

	public boolean loseFight() {
		if (fight.playerDead()) {
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
		if (!interactionPause) {
			interactionPause = true;
			Point interactionPoint = mainCharacter.getInteractionPoint();
			 currentBackground.getCurrentRoute()
				.getEntities()[interactionPoint.y][interactionPoint.x].onInteraction(mainCharacter);

 			interactionPause = false;
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
		mainCharacter.setName("Sarah");
		mainCharacter.setID("999");
		mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("eigenes_zimmer"));
		mainCharacter.setCurrentPosition(5, 4);
		currentBackground = new Background(mainCharacter.getCurrentRoute());
		Pokemon player = new Pokemon(54);
		player.setName("Sarah");
		player.getStats().generateStats((short) 100);
		mainCharacter.getTeam().addPokemon(player);
		gameFrame = new GameFrame();
	}

	public boolean loadGame(String path) {
		mainCharacter = new Player();
		mainCharacter.setCharacterImage("main", "front");
		if(routeAnalyzer.loadGame(path)) {
			currentBackground = new Background(mainCharacter.getCurrentRoute());
			gameFrame = new GameFrame();
			this.repaint();
			return true;
		}
		return false;
	}

	public void waitDialogue() {
		gameFrame.setActive();
		this.interactionPause = true;
		while (!gameFrame.isDialogueEmpty()) {
			sleep(50);
			this.gameFrame.getDialogue().repaint();
		}
		this.interactionPause = false;
	}

	public void saveGame() {
		if(!interactionPause) {
			interactionPause = true;
			if(!routeAnalyzer.saveGame(mController.saveGame())) {
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
		switch(choice) {
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
		if(isFighting()) {
			this.fight.setCurrentFightOption(FightOption.REPORT);
		}
		((ReportPanel) this.gameFrame.getReportPanel()).setPokemon(pokemon, this.gameFrame.getPokemonPanel());
		this.gameFrame.setCurrentPanel(this.gameFrame.getReportPanel());
	}


}

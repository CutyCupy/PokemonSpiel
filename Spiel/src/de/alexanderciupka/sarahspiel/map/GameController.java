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
		if (!interactionPause) {
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

	private boolean updatePosition(int x, int y) {
		if(x >= currentBackground.getCurrentRoute().getWidth() || x < 0 || y >= currentBackground.getCurrentRoute().getHeight() || y < 0) {
			return false;
		}
		if (currentBackground.getCurrentRoute().getEntities()[y][x].isAccessible(mainCharacter)) {
			mainCharacter.changePosition(mainCharacter.getCurrentDirection());
			if (currentBackground.getCurrentRoute().getEntities()[y][x].startWarp()) {
				x = mainCharacter.getCurrentPosition().x;
				y = mainCharacter.getCurrentPosition().y;
			}
			int characterIndex = checkStartFight();
			if (characterIndex >= 0) {
				startFight(getCurrentBackground().getCurrentRoute().getCharacters().get(characterIndex));
			} else if (currentBackground.getCurrentRoute().getEntities()[y][x].checkPokemon()) {
				startFight(currentBackground.chooseEncounter());
			}
			interactionPause = false;
			return true;
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
			gameFrame.getFightPanel().addText(p.getName() + " erhï¿½lt " + XPGain + " Erfahrungspunkte!");
			p.gainXP(XPGain / fight.getParticipants().size());
		}
		if (fight.enemyDead()) {
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
			boolean flag = false;
			Point interactionPoint = mainCharacter.getInteractionPoint();
			Entity currentEntity = currentBackground.getCurrentRoute()
					.getEntities()[interactionPoint.y][interactionPoint.x];
			if (currentEntity.hasCharacter()) {
				if (currentEntity.getCharacter().isTrainer()) {
					if (!currentEntity.getCharacter().isDefeated()) {
						currentEntity.getCharacter().faceTowardsMainCharacter();
						startFight(currentEntity.getCharacter());
						flag = true;
					}
				}
				if(!flag) {
					currentEntity.getCharacter().faceTowardsMainCharacter();
					gameFrame.addDialogue(currentEntity.getCharacter().getName() + ": "
							+ currentEntity.getCharacter().getNoFightDialogue());
					waitDialogue();
					if(currentEntity.getCharacter().getName().equals("Joy")) {
						mainCharacter.getTeam().restoreTeam();
						for(int i = 1; i <= mainCharacter.getTeam().getAmmount() + 1; i++) {
							System.out.println("test");
							getCurrentBackground().getCurrentRoute().getEntities()[0][1].setSprite("joyhealing" + (i % (mainCharacter.getTeam().getAmmount() + 1)));
							getCurrentBackground().getCurrentRoute().updateMap(new Point(1, 0));
							gameFrame.repaint();
							sleep(i == mainCharacter.getTeam().getAmmount() ? 1500 : 750);
						}
						gameFrame.addDialogue("Deine Pokemon sind nun wieder topfit!");
						waitDialogue();
					}
				}
			} else if(currentEntity.isWater() && !mainCharacter.isSurfing()) {
				gameFrame.addDialogue("Hier könnte man surfen!");
				boolean breaking = false;
				for(Pokemon p : mainCharacter.getTeam().getTeam()) {
					for(Move m : p.getMoves()) {
						if(m != null) {
							if(m.getName().equals("Surfer")) {
								gameFrame.addDialogue("Du fängst an auf " + p.getName() + " zu surfen!");
								mainCharacter.setSurfing(true);
								breaking = true;
								break;
							}
						}
					}
					if(breaking) break;
				}
				waitDialogue();
				if(breaking) {
					mainCharacter.changePosition(mainCharacter.getCurrentDirection());
				}
			} else if(currentEntity.isPC()) {
				gameFrame.displayPC(mainCharacter);
			}
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
		mainCharacter.setCurrentRoute(routeAnalyzer.getRouteById("zuhause"));
		mainCharacter.setCurrentPosition(2, 0);
		currentBackground = new Background(mainCharacter.getCurrentRoute());
		Pokemon player = new Pokemon(246);
		player.getStats().generateStats((short) 99);
		player.addMove(player.getMoves()[0].getName(), information.getMoveByName("Surfer"));
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
		while (!gameFrame.isDialogueEmpty()) {
			sleep(50);
			this.gameFrame.getDialogue().repaint();
		}
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

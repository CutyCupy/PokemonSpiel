package de.alexanderciupka.pokemon.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.gui.AilmentLabel;
import de.alexanderciupka.pokemon.gui.AnimationLabel;
import de.alexanderciupka.pokemon.gui.HPBar;
import de.alexanderciupka.pokemon.gui.MoveButton;
import de.alexanderciupka.pokemon.gui.PokeballLabel;
import de.alexanderciupka.pokemon.gui.StatLabel;
import de.alexanderciupka.pokemon.gui.TextLabel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.RouteType;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.painting.Painting;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;

@SuppressWarnings("serial")
public class FightPanel extends JPanel {

	private JLabel enemyPokemon;
	private JLabel ownPokemon;

	private AnimationLabel enemyAnimations;
	private AnimationLabel ownAnimations;

	private JLabel background;
	private JButton attack;
	private JButton bag;
	private JButton pokemon;
	private JButton escape;
	private MoveButton firstMove;
	private MoveButton secondMove;
	private MoveButton thirdMove;
	private MoveButton fourthMove;
	private JButton back;
	private JButton[] menu;
	private MoveButton[] moves;
	private StatLabel playerStats;
	private StatLabel enemyStats;
	private JPanel playerStatPanel;
	private JPanel enemyStatPanel;
	private boolean enemyAttack;
	private boolean attacked;
	private boolean throwPokeball;
	private TextLabel textLabel;
	private PokeballLabel pokeball;

	private HPBar playerHPBar;
	private HPBar enemyHPBar;

	private AilmentLabel playerAilmentLabel;
	private AilmentLabel enemyAilmentLabel;

	private Pokemon enemy;
	private Pokemon mine;

	private GameController gController;

	private JLabel[] playerPokemons;
	private JLabel[] enemyPokemons;

	private Image coloredPokeball;
	private Image grayPokeball;

	public static HashMap<Item, BufferedImage> pokeballImages;
	public static HashMap<Item, BufferedImage> openPokeballImages;

	private final static Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);

	public FightPanel(Pokemon mine, Pokemon enemy) {
		super();
		setLayout(new BorderLayout());
		gController = GameController.getInstance();
//		this.enemy = enemy;
//		this.mine = mine;
		playerHPBar = new HPBar();
		enemyHPBar = new HPBar();
		menu = new JButton[4];
		moves = new MoveButton[4];
		pokeball = new PokeballLabel();
		textLabel = new TextLabel();
		textLabel.setBounds(5, 480, 600, 110);
		textLabel.setOpaque(true);
		textLabel.setVisible(false);
		textLabel.setBackground(Color.WHITE);
		textLabel.setDelay(TextLabel.SLOW);
		textLabel.setAutoMove(true);
		setBounds(0, 0, 630, 630);
		enemyPokemon = new JLabel();
		enemyStats = new StatLabel();
		playerStatPanel = new JPanel(null);
		enemyStatPanel = new JPanel(null);

		playerAilmentLabel = new AilmentLabel();
		enemyAilmentLabel = new AilmentLabel();

		enemyStats.setBackground(Color.WHITE);
		enemyStats.setOpaque(false);
		ownPokemon = new JLabel();

		enemyAnimations = new AnimationLabel(false);
		ownAnimations = new AnimationLabel(true);

		playerStats = new StatLabel();
		playerStats.setVerticalAlignment(SwingConstants.TOP);
		enemyStats.setVerticalAlignment(SwingConstants.TOP);
		background = new JLabel(new ImageIcon(gController.getMainCharacter().getCurrentRoute()
				.getEntities()[gController.getMainCharacter().getCurrentPosition().y][gController.getMainCharacter()
						.getCurrentPosition().x].isWater() ? RouteType.WATER.getBattleBackground()
								: gController.getMainCharacter().getCurrentRoute().getType().getBattleBackground()));
		// playerStats.setBorder(
		// BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new
		// EmptyBorder(2, 10, 2, 10)));
		// enemyStats.setBorder(
		// BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new
		// EmptyBorder(2, 10, 2, 10)));

		pokeball.setVisible(false);
		pokeball.setOpaque(false);
		attack = new JButton("KAMPF");

		attack.setBackground(Color.WHITE);
		attack.setFocusable(false);
		bag = new JButton("BEUTEL");

		bag.setBackground(Color.WHITE);
		bag.setFocusable(false);
		pokemon = new JButton("POKÉMON");
		pokemon.setBackground(Color.WHITE);
		pokemon.setFocusable(false);
		escape = new JButton("FLUCHT");
		escape.setBackground(Color.WHITE);
		escape.setFocusable(false);
		firstMove = new MoveButton(true);
		firstMove.setBackground(Color.WHITE);
		firstMove.setVisible(false);
		firstMove.setFocusable(false);
		secondMove = new MoveButton(true);
		secondMove.setBackground(Color.WHITE);
		secondMove.setVisible(false);
		secondMove.setFocusable(false);
		thirdMove = new MoveButton(true);
		thirdMove.setBackground(Color.WHITE);
		thirdMove.setVisible(false);
		thirdMove.setFocusable(false);
		fourthMove = new MoveButton(true);
		fourthMove.setBackground(Color.WHITE);
		fourthMove.setVisible(false);
		fourthMove.setFocusable(false);
		back = new JButton("Zurück");
		back.setBackground(Color.WHITE);
		back.setVisible(false);
		back.setFocusable(false);
		menu[0] = attack;
		menu[1] = bag;
		menu[2] = pokemon;
		menu[3] = escape;
		moves[0] = firstMove;
		moves[1] = secondMove;
		moves[2] = thirdMove;
		moves[3] = fourthMove;

		playerStatPanel.setVisible(false);
		enemyStatPanel.setVisible(false);

		playerPokemons = new JLabel[6];
		enemyPokemons = new JLabel[6];

		try {
			double ratio = gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL).getHeight()
					/ (gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL).getWidth() * 1.0);
			coloredPokeball = gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL).getScaledInstance(15,
					(int) (15 * ratio), Image.SCALE_SMOOTH);
			grayPokeball = ImageIO.read(new File(this.getClass().getResource("/pokeballs/gray_pokeball.png").getFile()))
					.getScaledInstance(15, (int) (15 * ratio), Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < playerPokemons.length; i++) {
			JLabel currentPlayer = new JLabel();
			JLabel currentEnemy = new JLabel();

			currentPlayer.setVisible(false);
			currentEnemy.setVisible(false);

			currentPlayer.setOpaque(false);
			currentEnemy.setOpaque(false);

			currentPlayer.setBounds(375 + (16 * i), 257, 15, 15);
			currentEnemy.setBounds(45 + (16 * i), 132, 15, 15);

			playerPokemons[i] = currentPlayer;
			enemyPokemons[i] = currentEnemy;

			add(currentPlayer);
			add(currentEnemy);
		}

		if (pokeballImages == null || openPokeballImages == null) {
			pokeballImages = new HashMap<>();
			openPokeballImages = new HashMap<>();
			for (Item i : Item.values()) {
				try {
					Image img = gController.getRouteAnalyzer().getPokeballImage(i);
					if (img != null) {
						Image openImg = ImageIO.read(new File(Main.class
								.getResource("/pokeballs/" + i.name().toLowerCase() + "_open.png").getFile()));
						double ratio = img.getHeight(null) / (img.getWidth(null) * 1.0);
						pokeballImages.put(i, Painting
								.toBufferedImage(img.getScaledInstance(20, (int) (20 * ratio), Image.SCALE_SMOOTH)));
						openPokeballImages.put(i, Painting.toBufferedImage(
								openImg.getScaledInstance(20, (int) (20 * ratio), Image.SCALE_SMOOTH)));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		background.setBounds(0, 0, 630, 420);

		enemyPokemon.setBounds(395, 100, 160, 160); // 475, 180 mitte
		enemyStatPanel.setBounds(45, 150, 180, 40);
		ownPokemon.setBounds(50, 260, 160, 160); // 130, 250
		playerStatPanel.setBounds(375, 275, 180, 40);

		enemyAnimations.setBounds(enemyPokemon.getBounds());//enemyPokemon.getX() - 16, enemyPokemon.getY() - 16, 192, 192);
		ownAnimations.setBounds(ownPokemon.getBounds());//ownPokemon.getX() - 16, ownPokemon.getY() - 16, 192, 192);

		enemyStats.setLocation(0, 0);
		enemyStats.setSize(enemyStatPanel.getSize());

		playerStats.setLocation(0, 0);
		playerStats.setSize(playerStatPanel.getSize());

		attack.setFont(FONT);
		bag.setFont(FONT);
		pokemon.setFont(FONT);
		escape.setFont(FONT);
		attack.setBounds(65, 450, 500, 100);
		bag.setBounds(25, 575, 175, 50);
		pokemon.setBounds(431, 575, 175, 50);
		escape.setBounds(228, 580, 175, 50);

		firstMove.setFont(FONT);
		secondMove.setFont(FONT);
		thirdMove.setFont(FONT);
		fourthMove.setFont(FONT);

		firstMove.setBounds(25, 440, 280, 50);
		secondMove.setBounds(325, 440, 280, 50);
		thirdMove.setBounds(25, 500, 280, 50);
		fourthMove.setBounds(325, 500, 280, 50);

		back.setBounds(50, 570, 530, 60);

		addActionListener();
		addComponents();
	}

	private void addActionListener() {
		attack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateMoves();
				boolean struggle = true;
				for (MoveButton mb : moves) {
					if (mb.isEnabled()) {
						struggle = false;
						break;
					}
				}
				if (struggle) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							showText();
							textLabel.setActive();
							gController.getFight().startRound(null, enemy.getMove(mine));
							updateMoves();
							showMenu();
						}
					}).start();
				} else {
					for (int i = 0; i < menu.length; i++) {
						menu[i].setVisible(false);
						moves[i].setVisible(true);
					}
					back.setVisible(true);
				}
//				repaint();
			}
		});
		escape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gController.escape();
			}
		});
		pokemon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gController.getFight().setCurrentFightOption(FightOption.POKEMON);
				gController.getGameFrame().getPokemonPanel().update();
				gController.repaint();
				enemyAttack = true;
			}
		});
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < menu.length; i++) {
					menu[i].setVisible(true);
					moves[i].setVisible(false);
				}
				back.setVisible(false);
				repaint();
			}
		});
		for (int i = 0; i < moves.length; i++) {
			moves[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// if (!attacked && mine.getMoveByName(((JButton)
					// e.getSource()).getName()).getCurrentPP() > 0) {
					attacked = true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							do {
								showText();
								textLabel.setActive();
								Move playerMove = mine.getMoveByName(((JButton) e.getSource()).getName());
								if (!playerMove.equals(gController.getFight().canUse(mine, playerMove))) {
									addText(mine.getName() + " kann " + playerMove.getName() + " nicht einsetzen!");
									if (gController.getFight().canUse(mine, playerMove) == null) {
										showMenu();
										return;
									}
								}
								gController.getFight().startRound(playerMove, gController.getInformation().getMoveByName("Donnerwelle"));//enemy.getMove(mine));
//								Move playerMove = mine.getMoveByName(((JButton) e.getSource()).getName());
//								if (!playerMove.equals(gController.getFight().canUse(mine, playerMove))) {
//									addText(mine.getName() + " kann " + playerMove.getName() + " nicht einsetzen!");
//									if (gController.getFight().canUse(mine, playerMove) == null) {
//										showMenu();
//										return;
//									}
//								}
//								Move enemyMove = enemy.getMove(mine);
//								boolean playerStarts = gController.getFight().isPlayerStart(playerMove, enemyMove);
//								if (playerStarts) {
//									if (gController.getFight().attack(mine, enemy, playerMove)) {
//										gController.getFight().attack(enemy, mine, enemyMove);
//									} else {
//									}
//								} else {
//									if (gController.getFight().attack(enemy, mine, enemyMove)) {
//										gController.getFight().attack(mine, enemy, playerMove);
//									} else {
//										gController.getFight().setCurrentFightOption(FightOption.POKEMON);
//										gController.repaint();
//									}
//								}
//								mine.afterTurnDamage();
//								enemy.afterTurnDamage();
//								gController.getFight().increaseTurn();
								updateMoves();
								showMenu();
								// attacked = false;
							} while (!gController.getFight().canChooseAction());
						}
					}).start();
					// }
				}
			});
		}
		bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gController.getGameFrame().getInventoryPanel().update(gController.getMainCharacter());
				gController.getFight().setCurrentFightOption(FightOption.BAG);
				// gController.getGameFrame().repaint();
				enemyAttack = true;
			}
		});
	}

	public void throwBall(Item ball) {
		if (!throwPokeball) {
			throwPokeball = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					setComponentZOrder(pokeball, 1);
					pokeball.setVisible(true);
					pokeball.setBall(ball);
					pokeball.throwBall();
					if (gController.getFight().canEscape()) {
						enemyPokemon.setVisible(false);
						pokeball.drop();
						if (enemy.isCatched(ball)) {
							pokeball.shake(4);
							SoundController.getInstance().playSound(SoundController.POKEMON_CAUGHT);
							addText(enemy.getName() + " wurde gefangen!");
							if (!gController.getMainCharacter().getTeam().addPokemon(enemy)) {
								addText("Dein Team ist voll!");
								addText(enemy.getName() + " wurde auf deinem PC in "
										+ gController.getMainCharacter().getPC().addPokemon(enemy).getName()
										+ " gespeichert!");
							}
							gController.endFight();
							pokeball.setVisible(false);
							return;
						} else {
							pokeball.shake(enemy.getShakes(ball));
							enemyPokemon.setVisible(true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										Thread.sleep(150);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									pokeball.setVisible(false);
								}
							}).start();
							addText(enemy.getName() + " hat sich befreit!");
						}
					} else {
						pokeball.setVisible(false);
						addText("Sei kein Dieb!");
					}
					if (!gController.getFight().attack(enemy, mine)) {
						gController.getFight().setCurrentFightOption(FightOption.POKEMON);
						gController.repaint();
					}
					throwPokeball = false;
					showMenu();
				}
			}).start();
		}
	}

	private void addComponents() {
		for (int i = 0; i < 4; i++) {
			add(menu[i]);
			add(moves[i]);
		}
		playerStatPanel.add(playerHPBar);
		enemyStatPanel.add(enemyHPBar);
		playerStatPanel.add(playerStats);
		enemyStatPanel.add(enemyStats);
		playerStatPanel.add(playerAilmentLabel);
		enemyStatPanel.add(enemyAilmentLabel);
		add(textLabel);
		add(ownAnimations);
		add(enemyAnimations);
		add(playerStatPanel);
		add(enemyStatPanel);
		add(enemyPokemon);
		add(ownPokemon);
		add(pokeball);
		add(back);
		add(background);

		repaint();
	}

	public void setPlayer() {
		boolean playSound = !gController.getFight().getPlayer().equals(this.mine);
		ownPokemon.setVisible(false);
		this.mine = gController.getFight().getPlayer();
		if(playSound) {
			addText("Los " + mine.getName() + "!", false);
		}
		ownPokemon.setIcon(new ImageIcon(mine.getSpriteBack()));
		updateMoves();

		ownPokemon.setVisible(gController.getFight().isVisible(this.mine));

		if(playSound) {
			SoundController.getInstance().playBattlecry(mine.getId());
		}
	}

	public void updateMoves() {
		for (int i = 0; i < moves.length; i++) {
			moves[i].setMove(mine, mine.getMoves()[i]);
		}
	}

	public void setEnemy() {
		boolean playSound = !gController.getFight().getEnemy().equals(this.enemy);
		this.enemy = gController.getFight().getEnemy();
		this.enemyPokemon.setVisible(false);
		if (!gController.getFight().canEscape() && !gController.getFight().getEnemy().equals(this.enemy)) {
			this.addText(
					gController.getFight().getEnemyCharacter().getName() + " setzt " + this.enemy.getName() + " ein!");
		}
		enemyPokemon.setIcon(new ImageIcon(enemy.getSpriteFront()));
//		gController.getFight().setVisible(this.enemy, true);
		this.enemyPokemon.setVisible(gController.getFight().isVisible(this.enemy));

		if(playSound) {
			SoundController.getInstance().playBattlecry(enemy.getId());
		}
	}

	public void updatePanels() {
		Pokemon oldPlayer = playerStats.getPokemon();
		Pokemon oldEnemy = enemyStats.getPokemon();
		playerStats.setPokemon(mine);
		enemyStats.setPokemon(enemy);

		playerHPBar.setMaximum(mine.getStats().getStats().get(Stat.HP));
		enemyHPBar.setMaximum(enemy.getStats().getStats().get(Stat.HP));

		playerStatPanel.setVisible(true);
		enemyStatPanel.setVisible(true);

		if (playerHPBar.getValue() == 0 || !mine.equals(oldPlayer)) {
			playerHPBar.setValue(mine.getStats().getCurrentHP());
		} else {
			if (playerHPBar.getMaximum() != mine.getStats().getStats().get(Stat.HP)) {
				playerHPBar.setValue(mine.getStats().getCurrentHP());
			} else {
				playerHPBar.updateValue(mine.getStats().getCurrentHP());
			}
		}
		if (enemyHPBar.getValue() == 0 || !enemy.equals(oldEnemy)) {
			enemyHPBar.setValue(enemy.getStats().getCurrentHP());
		} else {
			enemyHPBar.updateValue(enemy.getStats().getCurrentHP());
		}

		int counter = 0;

		while (!playerHPBar.isFinished() || !enemyHPBar.isFinished()) {
			SoundController.getInstance().updatePokemonLow(playerHPBar);
			if (playerHPBar.isFinished()) {
				ownPokemon.setVisible(true);
			}
			if (enemyHPBar.isFinished()) {
				enemyPokemon.setVisible(true);
			}
			if (counter % 10 == 0) {
				if (playerHPBar.isFalling()) {
					ownPokemon.setVisible(!ownPokemon.isVisible());
				}
				if (enemyHPBar.isFalling()) {
					enemyPokemon.setVisible(!enemyPokemon.isVisible());
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}

		SoundController.getInstance().updatePokemonLow(playerHPBar);

		ownPokemon.setVisible(gController.getFight().isVisible(mine));
		enemyPokemon.setVisible(gController.getFight().isVisible(enemy));

		stopWaiting();

		playerAilmentLabel.setAilment(mine.getAilment());
		enemyAilmentLabel.setAilment(enemy.getAilment());

		playerAilmentLabel.setLocation(playerStats.getWidth() - playerAilmentLabel.getWidth() - 10, 5);
		enemyAilmentLabel.setLocation(enemyStats.getWidth() - enemyAilmentLabel.getWidth() - 10, 5);

		playerHPBar.setSize(playerStats.getWidth() - 20, 10);
		playerHPBar.setLocation(10, playerStats.getHeight() - playerHPBar.getHeight() - 7);

		enemyHPBar.setSize(enemyStats.getWidth() - 20, 10);
		enemyHPBar.setLocation(10, enemyStats.getHeight() - enemyHPBar.getHeight() - 7);

		for (int i = 0; i < playerPokemons.length; i++) {
			playerPokemons[i].setVisible(true);
			if (gController.getMainCharacter().getTeam().getTeam()[i] != null) {
				if (gController.getMainCharacter().getTeam().getTeam()[i].getStats().getCurrentHP() > 0) {
					playerPokemons[i].setIcon(new ImageIcon(coloredPokeball));
				} else {
					playerPokemons[i].setIcon(new ImageIcon(grayPokeball));
				}
			} else {
				playerPokemons[i].setVisible(false);
			}
		}

		for (int i = 0; i < enemyPokemons.length; i++) {
			enemyPokemons[i].setVisible(true);
			if (gController.getFight().getEnemyCharacter() != null) {
				if (gController.getFight().getEnemyCharacter().getTeam().getTeam()[i] != null) {
					if (gController.getFight().getEnemyCharacter().getTeam().getTeam()[i].getStats()
							.getCurrentHP() > 0) {
						enemyPokemons[i].setIcon(new ImageIcon(coloredPokeball));
					} else {
						enemyPokemons[i].setIcon(new ImageIcon(grayPokeball));
					}
				} else {
					enemyPokemons[i].setVisible(false);
				}
			} else {
				enemyPokemons[i].setVisible(false);
			}
		}
	}

	public void showMenu() {
		for (int i = 0; i < menu.length; i++) {
			menu[i].setVisible(true);
			moves[i].setVisible(false);
		}
		back.setVisible(false);
		textLabel.setVisible(false);
		repaint();
	}

	public void checkEnemyAttack() {
		if (enemyAttack) {
			gController.getFight().attack(enemy, mine);
			enemyAttack = false;
			gController.getFight().endTurn();
		}
	}

	public void pause() {
		while (!textLabel.isEmpty() && !textLabel.isWaiting()) {
			gController.sleep(50);
			textLabel.repaint();
		}
	}

	public void showText() {
		for (int i = 0; i < menu.length; i++) {
			menu[i].setVisible(false);
			moves[i].setVisible(false);
		}
		back.setVisible(false);
		textLabel.setVisible(true);
		repaint();
	}

	public void addText(String text) {
		this.addText(text, false);
	}

	public void addText(String text, boolean wait) {
		this.textLabel.setWaiting(wait);
		if (!textLabel.isVisible()) {
			showText();
		}
		this.textLabel.addText(text);
		this.textLabel.setActive();
		pause();
		textLabel.setAfter(After.NOTHING);
	}

	public void stopWaiting() {
		this.textLabel.setWaiting(false);
		pause();
	}

	public TextLabel getTextLabel() {
		return this.textLabel;
	}

	public void removeEnemy() {
		this.enemyStatPanel.setVisible(false);
		this.enemyPokemon.setVisible(false);
		for (int i = 0; i < enemyPokemons.length; i++) {
			enemyPokemons[i].setVisible(false);
		}
	}

	public AnimationLabel getEnemyAnimation() {
		return this.enemyAnimations;
	}

	public AnimationLabel getPlayerAnimation() {
		return this.ownAnimations;
	}

}

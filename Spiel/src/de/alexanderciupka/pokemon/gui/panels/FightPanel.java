package de.alexanderciupka.pokemon.gui.panels;

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
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.gui.AnimationLabel;
import de.alexanderciupka.pokemon.gui.MoveButton;
import de.alexanderciupka.pokemon.gui.PokeballLabel;
import de.alexanderciupka.pokemon.gui.PokemonLabel;
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

	// private JLabel enemyPokemon;
	// private JLabel ownPokemon;

	// private PokemonLabel leftPlayer;
	// private PokemonLabel rightPlayer;
	//
	// private PokemonLabel leftOpponent;
	// private PokemonLabel rightOpponent;

	private PokemonLabel[] pokemons;

	// private AnimationLabel enemyAnimations;
	// private AnimationLabel ownAnimations;

	private JLabel background;
	// private JButton attack;
	// private JButton bag;
	// private JButton pokemon;
	// private JButton escape;
	// private MoveButton firstMove;
	// private MoveButton secondMove;
	// private MoveButton thirdMove;
	// private MoveButton fourthMove;
	private JButton back;
	private JButton[] menu;
	private MoveButton[] moves;

	// private StatLabel leftPlayerStats;
	// private StatLabel rightPlayerStats;
	//
	// private StatLabel leftOpponentStats;
	// private StatLabel rightOpponentStats;

	private StatLabel[] stats;
	// private JPanel playerStatPanel;
	// private JPanel enemyStatPanel;
	private boolean enemyAttack;
	private boolean attacked;
	private boolean throwPokeball;
	private TextLabel textLabel;
	private PokeballLabel pokeball;

	// private HPBar playerHPBar;
	// private HPBar enemyHPBar;
	//
	// private AilmentLabel playerAilmentLabel;
	// private AilmentLabel enemyAilmentLabel;

	// private Pokemon enemy;
	// private Pokemon mine;

	private GameController gController;

	private JLabel[] playerPokemons;
	private JLabel[] enemyPokemons;

	private Image coloredPokeball;
	private Image grayPokeball;

	public static HashMap<Item, BufferedImage> pokeballImages;
	public static HashMap<Item, BufferedImage> openPokeballImages;

	private final static Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);

	public FightPanel() {
		super();
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		this.gController = GameController.getInstance();
		this.menu = new JButton[4];
		this.moves = new MoveButton[4];
		this.pokeball = new PokeballLabel();
		this.textLabel = new TextLabel();
		this.textLabel.setBounds(5, 480, 600, 110);
		this.textLabel.setOpaque(true);
		this.textLabel.setVisible(false);
		this.textLabel.setBackground(Color.WHITE);
		this.textLabel.setDelay(TextLabel.SLOW);
		this.textLabel.setAutoMove(true);
		this.setBounds(0, 0, 630, 630);

		this.pokemons = new PokemonLabel[4];
		this.stats = new StatLabel[4];

		for (int i : new int[] { Fighting.LEFT_OPPONENT, Fighting.LEFT_PLAYER, Fighting.RIGHT_OPPONENT,
				Fighting.RIGHT_PLAYER }) {
			this.pokemons[i - 1] = new PokemonLabel();
			this.stats[i - 1] = new StatLabel();
		}

		this.background = new JLabel(new ImageIcon(this.gController.getMainCharacter().getCurrentRoute()
				.getEntities()[this.gController.getMainCharacter().getCurrentPosition().y][this.gController
						.getMainCharacter().getCurrentPosition().x].isWater() ? RouteType.WATER.getBattleBackground()
								: this.gController.getMainCharacter().getCurrentRoute().getType()
										.getBattleBackground()));

		this.pokeball.setVisible(false);
		this.pokeball.setOpaque(false);

		String[] texts = new String[] { "KAMPF", "BEUTEL", "POKÉMON", "FLUCHT" };

		this.menu = new JButton[4];
		this.moves = new MoveButton[4];
		for (int i = 0; i < 4; i++) {
			JButton b = new JButton(texts[i]);
			b.setBackground(Color.WHITE);
			b.setFocusable(false);
			b.setFont(FONT);

			MoveButton m = new MoveButton(true);
			m.setVisible(false);

			this.menu[i] = b;
			this.moves[i] = m;
		}

		this.back = new JButton("Zurück");
		this.back.setBackground(Color.WHITE);
		this.back.setVisible(false);
		this.back.setFocusable(false);

		this.playerPokemons = new JLabel[6];
		this.enemyPokemons = new JLabel[6];

		try {
			double ratio = this.gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL).getHeight()
					/ (this.gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL).getWidth() * 1.0);
			this.coloredPokeball = this.gController.getRouteAnalyzer().getPokeballImage(Item.POKEBALL)
					.getScaledInstance(15, (int) (15 * ratio), Image.SCALE_SMOOTH);
			this.grayPokeball = ImageIO
					.read(new File(this.getClass().getResource("/pokeballs/gray_pokeball.png").getFile()))
					.getScaledInstance(15, (int) (15 * ratio), Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < this.playerPokemons.length; i++) {
			JLabel currentPlayer = new JLabel();
			JLabel currentEnemy = new JLabel();

			currentPlayer.setVisible(false);
			currentEnemy.setVisible(false);

			currentPlayer.setOpaque(false);
			currentEnemy.setOpaque(false);

			currentPlayer.setBounds(375 + (16 * i), 257, 15, 15);
			currentEnemy.setBounds(45 + (16 * i), 132, 15, 15);

			this.playerPokemons[i] = currentPlayer;
			this.enemyPokemons[i] = currentEnemy;

			this.add(currentPlayer);
			this.add(currentEnemy);
		}

		if (pokeballImages == null || openPokeballImages == null) {
			pokeballImages = new HashMap<>();
			openPokeballImages = new HashMap<>();
			for (Item i : Item.values()) {
				try {
					Image img = this.gController.getRouteAnalyzer().getPokeballImage(i);
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

		this.background.setBounds(0, 0, 630, 420);

		// this.enemyPokemon.setBounds();
		// this.enemyStatPanel.setBounds();
		// this.ownPokemon.setBounds();
		// this.playerStatPanel.setBounds();
		//
		// this.enemyAnimations.setBounds(this.enemyPokemon.getBounds());
		// this.ownAnimations.setBounds(this.ownPokemon.getBounds());
		//
		// this.enemyStats.setLocation(0, 0);
		// this.enemyStats.setSize(this.enemyStatPanel.getSize());
		//
		// this.playerStats.setLocation(0, 0);
		// this.playerStats.setSize(this.playerStatPanel.getSize());
		//
		// this.attack.setFont(FONT);
		// this.bag.setFont(FONT);
		// this.pokemon.setFont(FONT);
		// this.escape.setFont(FONT);
		this.menu[0].setBounds(65, 450, 500, 100);
		this.menu[1].setBounds(431, 575, 175, 50);
		this.menu[2].setBounds(25, 575, 175, 50);
		this.menu[3].setBounds(228, 580, 175, 50);
		//
		// this.firstMove.setFont(FONT);
		// this.secondMove.setFont(FONT);
		// this.thirdMove.setFont(FONT);
		// this.fourthMove.setFont(FONT);
		//
		this.moves[0].setBounds(25, 440, 280, 50);
		this.moves[1].setBounds(325, 440, 280, 50);
		this.moves[2].setBounds(25, 500, 280, 50);
		this.moves[3].setBounds(325, 500, 280, 50);

		this.back.setBounds(50, 570, 530, 60);

		this.addActionListener();
		this.addComponents();
	}

	public void setUpSingle() {
		this.pokemons[Fighting.LEFT_PLAYER - 1].setBounds(50, 260, 160, 160);
		this.playerPokemons[Fighting.LEFT_PLAYER - 1].setVisible(true);
		this.playerPokemons[Fighting.RIGHT_PLAYER - 1].setVisible(false);

		this.enemyPokemons[Fighting.LEFT_OPPONENT - 1].setBounds(395, 100, 160, 160);
		this.enemyPokemons[Fighting.LEFT_OPPONENT - 1].setVisible(true);
		this.enemyPokemons[Fighting.RIGHT_OPPONENT - 1].setVisible(false);

		this.stats[Fighting.LEFT_PLAYER - 1].setBounds(375, 275, 180, 40);
		this.stats[Fighting.LEFT_PLAYER - 1].setVisible(true);
		this.stats[Fighting.RIGHT_PLAYER - 1].setVisible(false);

		this.stats[Fighting.LEFT_OPPONENT - 1].setBounds(45, 150, 180, 40);
		this.stats[Fighting.LEFT_OPPONENT - 1].setVisible(true);
		this.stats[Fighting.RIGHT_OPPONENT - 1].setVisible(false);
		this.updateFight();
	}

	public void setUpDouble() {
		this.pokemons[Fighting.LEFT_PLAYER - 1].setBounds(0, 260, 160, 160);
		this.pokemons[Fighting.LEFT_PLAYER - 1].setVisible(true);
		this.pokemons[Fighting.LEFT_PLAYER - 1].setBounds(100, 260, 160, 160);
		this.pokemons[Fighting.RIGHT_PLAYER - 1].setVisible(true);

		this.pokemons[Fighting.LEFT_OPPONENT - 1].setBounds(345, 100, 160, 160);
		this.pokemons[Fighting.LEFT_OPPONENT - 1].setVisible(true);
		this.pokemons[Fighting.RIGHT_OPPONENT - 1].setBounds(435, 100, 160, 160);
		this.pokemons[Fighting.RIGHT_OPPONENT - 1].setVisible(true);

		this.stats[Fighting.LEFT_PLAYER - 1].setBounds(375, 255, 180, 40);
		this.stats[Fighting.LEFT_PLAYER - 1].setVisible(true);
		this.stats[Fighting.RIGHT_PLAYER - 1].setBounds(375, 295, 180, 40);
		this.stats[Fighting.RIGHT_PLAYER - 1].setVisible(true);

		this.stats[Fighting.LEFT_OPPONENT - 1].setBounds(45, 110, 180, 40);
		this.stats[Fighting.LEFT_OPPONENT - 1].setVisible(true);
		this.stats[Fighting.RIGHT_OPPONENT - 1].setBounds(45, 190, 180, 40);
		this.stats[Fighting.RIGHT_OPPONENT - 1].setVisible(true);
		this.updateFight();
	}

	public void updateFight() {

	}

	private void addActionListener() {
		this.menu[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.updateMoves();
				boolean struggle = true;
				for (MoveButton mb : FightPanel.this.moves) {
					if (mb.isEnabled()) {
						struggle = false;
						break;
					}
				}
				if (struggle) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							FightPanel.this.showText();
							FightPanel.this.textLabel.setActive();
							FightPanel.this.gController.getFight().startRound(null,
									FightPanel.this.enemy.getMove(FightPanel.this.mine));
							FightPanel.this.updateMoves();
							FightPanel.this.showMenu();
						}
					}).start();
				} else {
					for (int i = 0; i < FightPanel.this.menu.length; i++) {
						FightPanel.this.menu[i].setVisible(false);
						FightPanel.this.moves[i].setVisible(true);
					}
					FightPanel.this.back.setVisible(true);
				}
			}
		});
		this.menu[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.escape();
			}
		});
		this.menu[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
				FightPanel.this.gController.getGameFrame().getPokemonPanel().update();
				FightPanel.this.enemyAttack = true;
			}
		});
		this.back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < FightPanel.this.menu.length; i++) {
					FightPanel.this.menu[i].setVisible(true);
					FightPanel.this.moves[i].setVisible(false);
				}
				FightPanel.this.back.setVisible(false);
				FightPanel.this.repaint();
			}
		});
		for (MoveButton move : this.moves) {
			move.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FightPanel.this.attacked = true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							do {
								FightPanel.this.showText();
								FightPanel.this.textLabel.setActive();
								Move playerMove = FightPanel.this.mine
										.getMoveByName(((JButton) e.getSource()).getName());
								if (!playerMove.equals(FightPanel.this.gController.getFight()
										.canUse(FightPanel.this.mine, playerMove))) {
									FightPanel.this.addText(FightPanel.this.mine.getName() + " kann "
											+ playerMove.getName() + " nicht einsetzen!");
									if (FightPanel.this.gController.getFight().canUse(FightPanel.this.mine,
											playerMove) == null) {
										FightPanel.this.showMenu();
										return;
									}
								}
								FightPanel.this.gController.getFight().startRound(playerMove,
										FightPanel.this.enemy.getMove(FightPanel.this.mine));
								FightPanel.this.updateMoves();
								FightPanel.this.showMenu();
							} while (!FightPanel.this.gController.getFight().canChooseAction());
						}
					}).start();
				}
			});
		}
		this.menu[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.getGameFrame().getInventoryPanel()
						.update(FightPanel.this.gController.getMainCharacter());
				FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.BAG);
				FightPanel.this.enemyAttack = true;
			}
		});
	}

	public void throwBall(Item ball) {
		if (!this.throwPokeball) {
			this.throwPokeball = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					FightPanel.this.setComponentZOrder(FightPanel.this.pokeball, 1);
					FightPanel.this.pokeball.setVisible(true);
					FightPanel.this.pokeball.setBall(ball);
					FightPanel.this.pokeball.throwBall();
					if (FightPanel.this.gController.getFight().canEscape()) {
						FightPanel.this.enemyPokemon.setVisible(false);
						FightPanel.this.pokeball.drop();
						if (FightPanel.this.enemy.isCatched(ball)) {
							FightPanel.this.pokeball.shake(4);
							SoundController.getInstance().playSound(SoundController.POKEMON_CAUGHT);
							FightPanel.this.addText(FightPanel.this.enemy.getName() + " wurde gefangen!");
							if (!FightPanel.this.gController.getMainCharacter().getTeam()
									.addPokemon(FightPanel.this.enemy)) {
								FightPanel.this.addText("Dein Team ist voll!");
								FightPanel.this
										.addText(
												FightPanel.this.enemy.getName() + " wurde auf deinem PC in "
														+ FightPanel.this.gController.getMainCharacter().getPC()
																.addPokemon(FightPanel.this.enemy).getName()
														+ " gespeichert!");
							}
							FightPanel.this.gController.endFight();
							FightPanel.this.pokeball.setVisible(false);
							return;
						} else {
							FightPanel.this.pokeball.shake(FightPanel.this.enemy.getShakes(ball));
							FightPanel.this.enemyPokemon.setVisible(true);
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										Thread.sleep(150);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									FightPanel.this.pokeball.setVisible(false);
								}
							}).start();
							FightPanel.this.addText(FightPanel.this.enemy.getName() + " hat sich befreit!");
						}
					} else {
						FightPanel.this.pokeball.setVisible(false);
						FightPanel.this.addText("Sei kein Dieb!");
					}
					if (!FightPanel.this.gController.getFight().attack(FightPanel.this.enemy, FightPanel.this.mine)) {
						FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
					}
					FightPanel.this.throwPokeball = false;
					FightPanel.this.showMenu();
				}
			}).start();
		}
	}

	private void addComponents() {
		for (int i = 0; i < 4; i++) {
			this.add(this.menu[i]);
			this.add(this.moves[i]);
		}
		// this.playerStatPanel.add(this.playerHPBar);
		// this.enemyStatPanel.add(this.enemyHPBar);
		// this.playerStatPanel.add(this.playerStats);
		// this.enemyStatPanel.add(this.enemyStats);
		// this.playerStatPanel.add(this.playerAilmentLabel);
		// this.enemyStatPanel.add(this.enemyAilmentLabel);
		// this.add(this.textLabel);
		// this.add(this.ownAnimations);
		// this.add(this.enemyAnimations);
		// this.add(this.playerStatPanel);
		// this.add(this.enemyStatPanel);
		// this.add(this.enemyPokemon);
		// this.add(this.ownPokemon);
		this.add(this.pokeball);
		this.add(this.back);
		this.add(this.background);

		this.repaint();
	}

	public void setPokemon(int index) {
		boolean playSound = !this.gController.getFight().getPokemon(index).equals(this.pokemons[index - 1]);
		this.pokemons[index - 1].setPokemon(this.gController.getFight().getPokemon(index));
		this.updateMoves();
		if (playSound) {
			this.addText("Los " + this.pokemons[index - 1].getName() + "!", true);
			// this.textLabel.waitText();
			SoundController.getInstance().playBattlecry(this.pokemons[index - 1].getPokemon().getId());
		}
	}

	// public void setPlayer() {
	// boolean playSound =
	// !this.gController.getFight().getPlayer().equals(this.mine);
	// this.ownPokemon.setVisible(false);
	// this.mine = this.gController.getFight().getPlayer();
	// if (playSound) {
	// this.addText("Los " + this.mine.getName() + "!", false);
	// this.textLabel.waitText();
	// }
	// this.ownPokemon.setIcon(new ImageIcon(this.mine.getSpriteBack()));
	// this.updateMoves();
	//
	// this.ownPokemon.setVisible(this.gController.getFight().isVisible(this.mine));
	//
	// if (playSound) {
	// SoundController.getInstance().playBattlecry(this.mine.getId());
	// }
	// }

	public void updateMoves() {
		for (int i = 0; i < this.moves.length; i++) {
			this.moves[i].setMove(this.pokemons[this.gController.getFight().getActivePlayer() - 1].getPokemon(),
					this.pokemons[this.gController.getFight().getActivePlayer() - 1].getPokemon().getMoves()[i]);
		}
	}

	// public void setEnemy() {
	// boolean playSound =
	// !this.gController.getFight().getEnemy().equals(this.enemy);
	// this.enemyPokemon.setVisible(false);
	// this.enemy = this.gController.getFight().getEnemy();
	// if (!this.gController.getFight().canEscape() && playSound) {
	// this.addText(this.gController.getFight().getEnemyCharacter().getName() + "
	// setzt " + this.enemy.getName()
	// + " ein!");
	// this.textLabel.waitText();
	// }
	// this.enemyPokemon.setIcon(new ImageIcon(this.enemy.getSpriteFront()));
	// this.enemyPokemon.setVisible(this.gController.getFight().isVisible(this.enemy));
	//
	// if (playSound) {
	// SoundController.getInstance().playBattlecry(this.enemy.getId());
	// }
	// }

	public void updatePanels() {
		for (int i = 0; i < this.pokemons.length; i++) {
			Pokemon old = this.stats[i].getPokemon();

			this.stats[i].setPokemon(this.gController.getFight().getPokemon(i + 1));
			this.stats[i].setVisible(this.gController.getFight().isVisible(this.stats[i].getPokemon()));

			if (!this.stats[i].getPokemon().equals(old)) {
				this.stats[i].getHPBar().setValue(this.stats[i].getPokemon().getStats().getStats().get(Stat.HP));
			} else {
				this.stats[i].getHPBar().updateValue(this.stats[i].getPokemon().getStats().getCurrentHP());
			}
		}
		Pokemon oldPlayer = this.playerStats.getPokemon();
		Pokemon oldEnemy = this.enemyStats.getPokemon();
		this.playerStats.setPokemon(this.mine);
		this.enemyStats.setPokemon(this.enemy);

		this.playerHPBar.setMaximum(this.mine.getStats().getStats().get(Stat.HP));
		this.enemyHPBar.setMaximum(this.enemy.getStats().getStats().get(Stat.HP));

		this.playerStatPanel.setVisible(true);
		this.enemyStatPanel.setVisible(true);

		if (this.playerHPBar.getValue() == 0 || !this.mine.equals(oldPlayer)) {
			this.playerHPBar.setValue(this.mine.getStats().getCurrentHP());
		} else {
			if (this.playerHPBar.getMaximum() != this.mine.getStats().getStats().get(Stat.HP)) {
				this.playerHPBar.setValue(this.mine.getStats().getCurrentHP());
			} else {
				this.playerHPBar.updateValue(this.mine.getStats().getCurrentHP());
			}
		}
		if (this.enemyHPBar.getValue() == 0 || !this.enemy.equals(oldEnemy)) {
			this.enemyHPBar.setValue(this.enemy.getStats().getCurrentHP());
		} else {
			this.enemyHPBar.updateValue(this.enemy.getStats().getCurrentHP());
		}

		int counter = 0;

		while (!this.playerHPBar.isFinished() || !this.enemyHPBar.isFinished()) {
			SoundController.getInstance().updatePokemonLow(this.playerHPBar);
			if (this.playerHPBar.isFinished()) {
				this.ownPokemon.setVisible(true);
			}
			if (this.enemyHPBar.isFinished()) {
				this.enemyPokemon.setVisible(true);
			}
			if (counter % 10 == 0) {
				if (this.playerHPBar.isFalling()) {
					this.ownPokemon.setVisible(!this.ownPokemon.isVisible());
				}
				if (this.enemyHPBar.isFalling()) {
					this.enemyPokemon.setVisible(!this.enemyPokemon.isVisible());
				}
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			counter++;
		}

		SoundController.getInstance().updatePokemonLow(this.playerHPBar);

		this.ownPokemon.setVisible(this.gController.getFight().isVisible(this.mine));
		this.enemyPokemon.setVisible(this.gController.getFight().isVisible(this.enemy));

		this.stopWaiting();

		this.playerAilmentLabel.setAilment(this.mine.getAilment());
		this.enemyAilmentLabel.setAilment(this.enemy.getAilment());

		this.playerAilmentLabel.setLocation(this.playerStats.getWidth() - this.playerAilmentLabel.getWidth() - 10, 5);
		this.enemyAilmentLabel.setLocation(this.enemyStats.getWidth() - this.enemyAilmentLabel.getWidth() - 10, 5);

		this.playerHPBar.setSize(this.playerStats.getWidth() - 20, 10);
		this.playerHPBar.setLocation(10, this.playerStats.getHeight() - this.playerHPBar.getHeight() - 7);

		this.enemyHPBar.setSize(this.enemyStats.getWidth() - 20, 10);
		this.enemyHPBar.setLocation(10, this.enemyStats.getHeight() - this.enemyHPBar.getHeight() - 7);

		for (int i = 0; i < this.playerPokemons.length; i++) {
			this.playerPokemons[i].setVisible(true);
			if (this.gController.getMainCharacter().getTeam().getTeam()[i] != null) {
				if (this.gController.getMainCharacter().getTeam().getTeam()[i].getStats().getCurrentHP() > 0) {
					this.playerPokemons[i].setIcon(new ImageIcon(this.coloredPokeball));
				} else {
					this.playerPokemons[i].setIcon(new ImageIcon(this.grayPokeball));
				}
			} else {
				this.playerPokemons[i].setVisible(false);
			}
		}

		for (int i = 0; i < this.enemyPokemons.length; i++) {
			this.enemyPokemons[i].setVisible(true);
			if (this.gController.getFight().getEnemyCharacter() != null) {
				if (this.gController.getFight().getEnemyCharacter().getTeam().getTeam()[i] != null) {
					if (this.gController.getFight().getEnemyCharacter().getTeam().getTeam()[i].getStats()
							.getCurrentHP() > 0) {
						this.enemyPokemons[i].setIcon(new ImageIcon(this.coloredPokeball));
					} else {
						this.enemyPokemons[i].setIcon(new ImageIcon(this.grayPokeball));
					}
				} else {
					this.enemyPokemons[i].setVisible(false);
				}
			} else {
				this.enemyPokemons[i].setVisible(false);
			}
		}
	}

	private boolean isHPFinished() {
		for (int i = 0; i < this.stats.length; i++) {
			if (!this.stats[i].getHPBar().isFinished()) {
				return false;
			}
		}
		return true;
	}

	public void showMenu() {
		for (int i = 0; i < this.menu.length; i++) {
			this.menu[i].setVisible(true);
			this.moves[i].setVisible(false);
		}
		this.back.setVisible(false);
		this.textLabel.setVisible(false);
		this.repaint();
	}

	public void checkEnemyAttack() {
		if (this.enemyAttack) {
			this.gController.getFight().attack(this.enemy, this.mine);
			this.enemyAttack = false;
			this.gController.getFight().endTurn();
		}
	}

	public void pause() {
		while (!this.textLabel.isEmpty() && !this.textLabel.isWaiting()) {
			this.gController.sleep(50);
			this.textLabel.repaint();
		}
	}

	public void showText() {
		for (int i = 0; i < this.menu.length; i++) {
			this.menu[i].setVisible(false);
			this.moves[i].setVisible(false);
		}
		this.back.setVisible(false);
		this.textLabel.setVisible(true);
		this.repaint();
	}

	public void addText(String text) {
		this.addText(text, false);
	}

	public void addText(String text, boolean wait) {
		if (text == null) {
			return;
		}
		this.textLabel.setWaiting(wait);
		if (!this.textLabel.isVisible()) {
			this.showText();
		}
		this.textLabel.addText(text);
		this.textLabel.setActive();
		this.pause();
		this.textLabel.setAfter(After.NOTHING);
	}

	public void stopWaiting() {
		this.textLabel.setWaiting(false);
		this.pause();
	}

	public TextLabel getTextLabel() {
		return this.textLabel;
	}

	public void removeEnemy() {
		this.enemyStatPanel.setVisible(false);
		this.enemyPokemon.setVisible(false);
		for (JLabel enemyPokemon2 : this.enemyPokemons) {
			enemyPokemon2.setVisible(false);
		}
	}

	public AnimationLabel getEnemyAnimation() {
		return this.enemyAnimations;
	}

	public AnimationLabel getPlayerAnimation() {
		return this.ownAnimations;
	}

	public PokemonLabel getPokemonLabel(Pokemon pokemon) {
		for (PokemonLabel p : this.pokemons) {
			if (p != null && p.getPokemon().equals(pokemon)) {
				return p;
			}
		}
		return null;
	}

}

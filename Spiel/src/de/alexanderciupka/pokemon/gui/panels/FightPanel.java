package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.fighting.Weather;
import de.alexanderciupka.pokemon.gui.After;
import de.alexanderciupka.pokemon.gui.HPBar;
import de.alexanderciupka.pokemon.gui.MoveButton;
import de.alexanderciupka.pokemon.gui.PokeballLabel;
import de.alexanderciupka.pokemon.gui.PokemonLabel;
import de.alexanderciupka.pokemon.gui.StatLabel;
import de.alexanderciupka.pokemon.gui.TextLabel;
import de.alexanderciupka.pokemon.gui.overlay.FogOverlay;
import de.alexanderciupka.pokemon.gui.overlay.FogType;
import de.alexanderciupka.pokemon.gui.overlay.IAnimated;
import de.alexanderciupka.pokemon.gui.overlay.Overlay;
import de.alexanderciupka.pokemon.gui.overlay.RainOverlay;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.gui.overlay.SandstormOverlay;
import de.alexanderciupka.pokemon.gui.overlay.SnowOverlay;
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.RouteType;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.painting.Painting;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Type;

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
	private JButton[] targets;

	// private StatLabel leftPlayerStats;
	// private StatLabel rightPlayerStats;
	//
	// private StatLabel leftOpponentStats;
	// private StatLabel rightOpponentStats;

	private StatLabel[] stats;
	// private JPanel playerStatPanel;
	// private JPanel enemyStatPanel;
	private boolean enemyAttack;
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

	private JLabel weather;
	private Overlay weatherOverlay;

	public static HashMap<Integer, BufferedImage> pokeballImages;
	public static HashMap<Integer, BufferedImage> openPokeballImages;

	private final static Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 18);

	private Attack currentAttack;

	private final ActionListener MOVE_BACK = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			showMenu();
		}
	};

	private final ActionListener TARGET_BACK = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			showMoves();
		};
	};
	
	private final Thread weatherThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while(GameController.getInstance().isFighting()) {
				while(weather == null) {
					Thread.yield();
				}
				if(weatherOverlay != null && weatherOverlay.getOverlay() != null) {
					weather.setIcon(new ImageIcon(weatherOverlay.getOverlay()));
				} else if(weatherOverlay == null) {
					weather.setIcon(null);
				}
				try {
					Thread.sleep((long) (1000 / Main.FPS));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	public FightPanel() {
		super();

		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);
		this.gController = GameController.getInstance();
		this.menu = new JButton[4];
		this.moves = new MoveButton[4];
		this.targets = new JButton[4];
		this.pokeball = new PokeballLabel();
		this.textLabel = new TextLabel();
		this.textLabel.setBounds(5, 480, 600, 110);
		this.textLabel.setOpaque(true);
		this.textLabel.setVisible(false);
		this.textLabel.setBackground(Color.WHITE);
		this.textLabel.setDelay(TextLabel.SLOW);
		this.textLabel.setAutoMove(true);
		this.setBounds(0, 0, 630, 630);

		this.weather = new JLabel();
		this.weather.setBounds(0, 0, 630, 420);
		this.weather.setOpaque(false);
		
		this.add(this.weather);

		this.pokemons = new PokemonLabel[4];
		this.stats = new StatLabel[4];

		for (int i : new int[] { Fighting.LEFT_OPPONENT, Fighting.LEFT_PLAYER, Fighting.RIGHT_OPPONENT,
				Fighting.RIGHT_PLAYER }) {
			this.pokemons[i] = new PokemonLabel();
			this.stats[i] = new StatLabel();
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

			JButton target = new JButton();
			target.setBackground(Color.WHITE);
			target.setVisible(false);
			target.setFocusable(false);
			target.setFont(FONT);

			this.menu[i] = b;
			this.moves[i] = m;
			this.targets[i] = target;
		}

		this.back = new JButton("Zurück");
		this.back.setBackground(Color.WHITE);
		this.back.setVisible(false);
		this.back.setFocusable(false);

		try {
			this.coloredPokeball = ImageIO.read(this.getClass().getResourceAsStream("/pokeballs/pokeball.png"))
					.getScaledInstance(15, 30, Image.SCALE_SMOOTH);
			this.grayPokeball = ImageIO.read(this.getClass().getResourceAsStream("/pokeballs/gray_pokeball.png"))
					.getScaledInstance(15, 30, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (pokeballImages == null || openPokeballImages == null) {
			pokeballImages = new HashMap<>();
			openPokeballImages = new HashMap<>();
			for (int i = 1; i < 682; i++) {
				try {
					Object data = this.gController.getInformation().getItemData(Items.ITEM_POCKET, i);
					if (data != null && data.equals(Items.POKEBALLS)) {
						Image img = this.gController.getRouteAnalyzer().getItemImage(i);
						if (img != null) {
							Image openImg = ImageIO
									.read(this.getClass().getResourceAsStream("/pokeballs/" + i + "_open.png"));
							double ratio = img.getHeight(null) / (img.getWidth(null) * 1.0);
							pokeballImages.put(i, Painting.toBufferedImage(
									img.getScaledInstance(20, (int) (20 * ratio), Image.SCALE_SMOOTH)));
							openPokeballImages.put(i, Painting.toBufferedImage(
									openImg.getScaledInstance(20, (int) (20 * ratio), Image.SCALE_SMOOTH)));
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		this.background.setBounds(0, 0, 630, 420);

		this.menu[0].setBounds(65, 450, 500, 100);
		this.menu[1].setBounds(431, 575, 175, 50);
		this.menu[2].setBounds(25, 575, 175, 50);
		this.menu[3].setBounds(228, 580, 175, 50);

		this.moves[0].setBounds(25, 440, 280, 50);
		this.moves[1].setBounds(325, 440, 280, 50);
		this.moves[2].setBounds(25, 500, 280, 50);
		this.moves[3].setBounds(325, 500, 280, 50);

		this.targets[Fighting.LEFT_OPPONENT].setBounds(25, 440, 280, 50);
		this.targets[Fighting.RIGHT_OPPONENT].setBounds(325, 440, 280, 50);
		this.targets[Fighting.LEFT_PLAYER].setBounds(25, 500, 280, 50);
		this.targets[Fighting.RIGHT_PLAYER].setBounds(325, 500, 280, 50);

		this.back.setBounds(50, 570, 530, 60);

		this.addActionListener();
		this.addComponents();

		if (this.gController.getFight().isDouble()) {
			setUpDouble();
		} else {
			setUpSingle();
		}
	}

	public void setUpSingle() {
		this.pokemons[Fighting.LEFT_PLAYER].setBounds(50, 260, 160, 160);
		this.pokemons[Fighting.LEFT_PLAYER].setVisible(true);
		this.pokemons[Fighting.RIGHT_PLAYER].setVisible(false);

		this.pokemons[Fighting.LEFT_OPPONENT].setBounds(395, 100, 160, 160);
		this.pokemons[Fighting.LEFT_OPPONENT].setVisible(true);
		this.pokemons[Fighting.RIGHT_OPPONENT].setVisible(false);

		this.stats[Fighting.LEFT_PLAYER].setBounds(375, 275, 180, 40);
		this.stats[Fighting.LEFT_PLAYER].setVisible(true);
		this.stats[Fighting.RIGHT_PLAYER].setVisible(false);

		this.stats[Fighting.LEFT_OPPONENT].setBounds(45, 150, 180, 40);
		this.stats[Fighting.LEFT_OPPONENT].setVisible(true);
		this.stats[Fighting.RIGHT_OPPONENT].setVisible(false);

		this.playerPokemons = new JLabel[6];
		this.enemyPokemons = new JLabel[6];

		for (int i = 0; i < this.playerPokemons.length; i++) {
			JLabel currentPlayer = new JLabel();
			JLabel currentEnemy = new JLabel();

			currentPlayer.setVisible(false);
			currentEnemy.setVisible(false);

			currentPlayer.setOpaque(false);
			currentEnemy.setOpaque(false);

			currentPlayer.setBounds(375 + (16 * (i % 6)), 304, 15, 15);
			currentEnemy.setBounds(45 + (16 * (i % 6)), 159, 15, 15);

			this.playerPokemons[i] = currentPlayer;
			this.enemyPokemons[i] = currentEnemy;

			this.add(currentPlayer);
			this.add(currentEnemy);
		}
	}

	public void setUpDouble() {
		this.pokemons[Fighting.LEFT_PLAYER].setBounds(0, 260, 160, 160);
		this.pokemons[Fighting.LEFT_PLAYER].setVisible(true);
		this.pokemons[Fighting.RIGHT_PLAYER].setBounds(100, 260, 160, 160);
		this.pokemons[Fighting.RIGHT_PLAYER].setVisible(true);

		this.pokemons[Fighting.LEFT_OPPONENT].setBounds(345, 100, 160, 160);
		this.pokemons[Fighting.LEFT_OPPONENT].setVisible(true);
		this.pokemons[Fighting.RIGHT_OPPONENT].setBounds(435, 100, 160, 160);
		this.pokemons[Fighting.RIGHT_OPPONENT].setVisible(true);

		this.stats[Fighting.LEFT_PLAYER].setBounds(375, 255, 180, 40);
		this.stats[Fighting.LEFT_PLAYER].setVisible(true);
		this.stats[Fighting.RIGHT_PLAYER].setBounds(375, 325, 180, 40);
		this.stats[Fighting.RIGHT_PLAYER].setVisible(true);

		this.stats[Fighting.LEFT_OPPONENT].setBounds(45, 110, 180, 40);
		this.stats[Fighting.LEFT_OPPONENT].setVisible(true);
		this.stats[Fighting.RIGHT_OPPONENT].setBounds(45, 180, 180, 40);
		this.stats[Fighting.RIGHT_OPPONENT].setVisible(true);

		if (this.gController.getFight().getCharacter(Fighting.LEFT_PLAYER)
				.equals(this.gController.getFight().getCharacter(Fighting.RIGHT_PLAYER))) {
			this.playerPokemons = new JLabel[6];
			for (int i = 0; i < this.playerPokemons.length; i++) {
				JLabel currentPlayer = new JLabel();
				currentPlayer.setVisible(false);
				currentPlayer.setOpaque(false);
				currentPlayer.setBounds(375 + (16 * (i % 6)), 304, 15, 15);
				this.playerPokemons[i] = currentPlayer;
				this.add(currentPlayer);
			}
		} else {
			this.playerPokemons = new JLabel[12];
			for (int i = 0; i < this.playerPokemons.length; i++) {
				JLabel currentPlayer = new JLabel();
				currentPlayer.setVisible(false);
				currentPlayer.setOpaque(false);
				currentPlayer.setBounds(375 + (16 * (i % 6)), 234 + (70 * (i / 6)), 15, 15);
				this.playerPokemons[i] = currentPlayer;
				this.add(currentPlayer);
			}
		}

		if (this.gController.getFight().getCharacter(Fighting.LEFT_OPPONENT) != null
				&& this.gController.getFight().getCharacter(Fighting.LEFT_OPPONENT)
						.equals(this.gController.getFight().getCharacter(Fighting.RIGHT_OPPONENT))) {
			this.enemyPokemons = new JLabel[6];

			for (int i = 0; i < this.enemyPokemons.length; i++) {
				JLabel currentEnemy = new JLabel();
				currentEnemy.setVisible(false);
				currentEnemy.setOpaque(false);
				currentEnemy.setBounds(45 + (16 * (i % 6)), 159, 15, 15);
				this.enemyPokemons[i] = currentEnemy;
				this.add(currentEnemy);
			}
		} else {
			this.enemyPokemons = new JLabel[12];

			for (int i = 0; i < this.enemyPokemons.length; i++) {
				JLabel currentEnemy = new JLabel();
				currentEnemy.setVisible(false);
				currentEnemy.setOpaque(false);
				currentEnemy.setBounds(45 + (16 * (i % 6)), 89 + (70 * (i / 6)), 15, 15);
				this.enemyPokemons[i] = currentEnemy;
				this.add(currentEnemy);
			}
		}
	}

	public void updateFight() {
		Fighting fight = this.gController.getFight();
		ArrayList<Integer> newPokemons = new ArrayList<>();
		for (int i : new int[] { Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT,
				Fighting.RIGHT_OPPONENT }) {
			if ((fight.isDouble() || (i == Fighting.LEFT_OPPONENT || i == Fighting.LEFT_PLAYER))
					&& (this.pokemons[i].getPokemon() == null ? fight.getPokemon(i) != null
							: !this.pokemons[i].getPokemon().equals(fight.getPokemon(i)))) {
				this.pokemons[i].setVisible(false);
				this.pokemons[i].setPokemon(fight.getPokemon(i));
				if (fight.getPokemon(i) != null) {
					if (fight.isPlayer(i)) {
						this.addText("Los " + fight.getPokemon(i).getName() + "!");
					} else {
						if (fight.getCharacter(i) != null) {
							this.addText(fight.getCharacter(i).getName() + " setzt " + fight.getPokemon(i).getName()
									+ " ein!");
						} else {
							this.addText("Ein wildes " + fight.getPokemon(i).getName() + " erscheint!");
						}
					}
					this.textLabel.waitText();
					this.pokemons[i].setVisible(fight.isVisible(fight.getPokemon(i)));
					SoundController.getInstance().playBattlecry(fight.getPokemon(i).getId());
					newPokemons.add(i);
				}
			}
			this.pokemons[i].setVisible(fight.isVisible(fight.getPokemon(i)));
		}
		Pokemon[] speed = gController.getFight().getSpeedOrder();
		for (Pokemon p : speed) {
			if (p != null) {
				for (int i : newPokemons) {
					if (gController.getFight().getIndex(p) == i) {
						switch (p.getAbility().getId()) {
						case Abilities.DÜRRE:
							this.addText(gController.getFight().getField().setWeather(Weather.SUN, p));
							break;
						case Abilities.NIESEL:
							this.addText(gController.getFight().getField().setWeather(Weather.RAIN, p));
							break;
						case Abilities.SANDSTURM:
							this.addText(gController.getFight().getField().setWeather(Weather.SANDSTORM, p));
							break;
						case Abilities.HAGELALARM:
							this.addText(gController.getFight().getField().setWeather(Weather.HAIL, p));
							break;
						case Abilities.BEDROHER:
							for (int j : new int[] { Fighting.LEFT_OPPONENT, Fighting.LEFT_PLAYER,
									Fighting.RIGHT_OPPONENT, Fighting.RIGHT_PLAYER }) {
								if (fight.getPokemon(j) != null && fight.isPlayer(j) != fight.isPlayer(i)) {
									fight.getPokemon(j).getStats().decreaseStat(Stat.ATTACK, 1);
								}
							}
							break;
						case Abilities.FÄHRTE:
							if (fight.isPlayer(i)) {
								if (fight.getPokemon(Fighting.LEFT_OPPONENT) != null) {
									fight.getPokemon(i)
											.setFightingAbility(fight.getPokemon(Fighting.LEFT_OPPONENT).getAbility());
								} else {
									fight.getPokemon(i)
											.setFightingAbility(fight.getPokemon(Fighting.RIGHT_OPPONENT).getAbility());
								}
							} else {
								if (fight.getPokemon(Fighting.LEFT_PLAYER) != null) {
									fight.getPokemon(i)
											.setFightingAbility(fight.getPokemon(Fighting.LEFT_PLAYER).getAbility());
								} else {
									fight.getPokemon(i)
											.setFightingAbility(fight.getPokemon(Fighting.RIGHT_PLAYER).getAbility());
								}
							}
							this.addText(fight.getPokemon(i).getName() + " übernimmt die Fähigkeit "
									+ fight.getPokemon(i).getAbility().getName());
							break;
						case Abilities.DOWNLOAD:
							Pokemon target = null;
							if (fight.isPlayer(i)) {
								if (fight.getPokemon(Fighting.LEFT_OPPONENT) != null) {
									target = fight.getPokemon(Fighting.LEFT_OPPONENT);
								} else {
									target = fight.getPokemon(Fighting.RIGHT_OPPONENT);
								}
							} else {
								if (fight.getPokemon(Fighting.LEFT_PLAYER) != null) {
									target = fight.getPokemon(Fighting.LEFT_PLAYER);
								} else {
									target = fight.getPokemon(Fighting.RIGHT_PLAYER);
								}
							}
							if (target.getStats().getStats().get(Stat.DEFENSE) < target.getStats().getStats()
									.get(Stat.SPECIALDEFENSE)) {
								this.addText(
										"Download von " + fight.getPokemon(i).getName() + " erhöht seinen Angriff!");
								fight.getPokemon(i).getStats().increaseStat(Stat.ATTACK, 1);
							} else {
								this.addText("Download von " + fight.getPokemon(i).getName()
										+ " erhöht seinen Spezialangriff!");
								fight.getPokemon(i).getStats().increaseStat(Stat.SPECIALATTACK, 1);
							}
							break;
						case Abilities.VORAHNUNG:
							boolean scared = false;
							for (int j = 0; j < 4 && !scared; j++) {
								Pokemon check = fight.getPokemon(j);
								if (check != null && fight.isPlayer(check) != fight.isPlayer(i)) {
									for (Move m : check.getMoves()) {
										if (m != null) {
											if (Type.getEffectiveness(m.getMoveType(check),
													fight.getPokemon(i)) > Type.DEFAULT
													|| m.getCategory().contains("ohko")) {
												scared = true;
												break;
											}
										}
									}
								}
							}
							if (scared) {
								this.addText(fight.getPokemon(i).getName() + " erschaudert aufgrund von Vorahnung!");
							}
							break;
						case Abilities.ERZWINGER:
							this.addText(fight.getPokemon(i).getName() + " übt Druck auf die Pokemon aus!");
							break;
						case Abilities.ÜBERBRÜCKUNG:
							this.addText(fight.getPokemon(i).getName()
									+ " gelingt es, gegnerische Fähigkeiten zu überbrücken!");
							break;
						case Abilities.TERAVOLT:
							this.addText(fight.getPokemon(i).getName() + " strahlt eine knisternde Aura aus!");
							break;
						case Abilities.TURBOBRAND:
							this.addText(fight.getPokemon(i).getName() + " strahlt eine lodernde Aura aus!");
							break;
						case Abilities.VORWARNUNG:
							Move max = null;
							int maxPower = -1;
							Pokemon owner = null;
							for (int j = 0; j < 4; j++) {
								Pokemon check = fight.getPokemon(j);
								if (check != null && fight.isPlayer(check) != fight.isPlayer(i)) {
									for (Move m : check.getMoves()) {
										if (m != null) {
											int power = m.getPower();
											if (m.getCategory().contains("ohko")) {
												power = 160;
											}
											switch (m.getId()) {
											case Moves.ERUPTION:
											case Moves.FONTRÄNEN:
												power = 150;
												break;
											case Moves.KONTER:
											case Moves.SPIEGELCAPE:
											case Moves.METALLSTOSS:
												power = 120;
												break;
											case Moves.AUSWRINGEN:
											case Moves.BEERENKRÄFTE:
											case Moves.DRACHENWUT:
											case Moves.DRESCHFLEGEL:
											case Moves.FRUSTRATION:
											case Moves.FUSSKICK:
											case Moves.GEGENSCHLAG:
											case Moves.GEOWURF:
											case Moves.GYROBALL:
											case Moves.KRAFTRESERVE:
											case Moves.NACHTNEBEL:
											case Moves.NOTSITUATION:
											case Moves.PSYWELLE:
											case Moves.RÜCKKEHR:
											case Moves.STRAUCHLER:
											case Moves.TRUMPFKARTE:
											case Moves.ULTRASCHALL:
											case Moves.QUETSCHGRIFF:
												power = 80;
												break;
											}
											if (max == null || (power > maxPower)) {
												max = m;
												maxPower = power;
												owner = check;
											}
										}
									}
								}
							}
							this.addText(max.getName() + " von " + owner.getName() + " wurde durschaut!");
							break;
						}
					}
					this.updateFight();
				}
			}
		}


		if(!weatherThread.isAlive()) {
			weatherThread.start();
		}
		try {
			switch (this.gController.getFight().getField().getWeather()) {
			case FOG:
				if(!(weatherOverlay instanceof FogOverlay)) {
					if(weatherOverlay != null) {
						weatherOverlay.onRemove();
					}
					weatherOverlay = new FogOverlay(null, weather.getSize(), FogType.MIST);
				}
				break;
			case HAIL:
				if(!(weatherOverlay instanceof SnowOverlay)) {
					if(weatherOverlay != null) {
						weatherOverlay.onRemove();
					}
					this.weatherOverlay = new SnowOverlay(null, this.weather.getSize(), SnowType.BLIZZARD);
					((IAnimated) weatherOverlay).startAnimation();
				}
				break;
			case NONE:
				if(!(weatherOverlay == null)) {
					weatherOverlay.onRemove();
				}
				weatherOverlay = null;
				break;
			case RAIN:
				if(!(weatherOverlay instanceof RainOverlay)) {
					if(weatherOverlay != null) {
						weatherOverlay.onRemove();
					}
					this.weatherOverlay = new RainOverlay(null, this.weather.getSize(), RainType.HEAVY);
					((IAnimated) weatherOverlay).startAnimation();
				}
				break;
			case SANDSTORM:
				if(!(weatherOverlay instanceof SandstormOverlay)) {
					if(weatherOverlay != null) {
						weatherOverlay.onRemove();
					}
					this.weatherOverlay = new SandstormOverlay(null, this.weather.getSize());
					((IAnimated) weatherOverlay).startAnimation();
				}
				break;
			case SUN:
				if(weatherOverlay != null) {
					weatherOverlay.onRemove();
				}
				weatherOverlay = null;
				break;
			default:
				break;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		updatePanels();
	}

	private void addActionListener() {
		this.menu[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Fighting fight = gController.getFight();

				FightPanel.this.updateMoves();
				boolean hasMoves = false;
				for (MoveButton mb : FightPanel.this.moves) {
					if (mb.isEnabled()) {
						hasMoves = true;
						break;
					}
				}
				if (hasMoves) {
					showMoves();
				} else {
					int struggleTarget = Main.RNG.nextBoolean() ? Fighting.LEFT_OPPONENT : Fighting.RIGHT_OPPONENT;
					;
					// if (fight.isDouble()) {
					// if (fight.getPokemon(Fighting.LEFT_OPPONENT) != null
					// && fight.getPokemon(Fighting.RIGHT_OPPONENT) != null) {
					// struggleTarget =
					//
					// } else if (fight.getPokemon(Fighting.LEFT_OPPONENT) !=
					// null) {
					// struggleTarget = (Fighting.LEFT_OPPONENT);
					// } else {
					// struggleTarget = (Fighting.RIGHT_OPPONENT);
					// }
					// } else {
					// struggleTarget = (Fighting.LEFT_OPPONENT);
					// }
					fight.registerAttack(new Attack(fight.getCurrentPokemon(),
							gController.getInformation().getMoveById(Moves.VERZWEIFLER), struggleTarget));
				}

			}
		});
		this.menu[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.escape();
			}
		});
		this.menu[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
				FightPanel.this.gController.getGameFrame().getPokemonPanel().update();
				FightPanel.this.enemyAttack = true;
			}
		});
		for (MoveButton move : this.moves) {
			move.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentAttack = new Attack();

					currentAttack.setSource(gController.getFight().getCurrentPokemon());

					currentAttack.setMove(((MoveButton) e.getSource()).getMove());

					Fighting fight = gController.getFight();

					if (gController.getFight().isDouble()) {
						switch (currentAttack.getMove().getTarget()) {
						case ALLY:
							currentAttack.setTargets((fight.getActivePlayer() == Fighting.LEFT_PLAYER
									? Fighting.RIGHT_PLAYER : Fighting.LEFT_PLAYER));
							break;
						case ALL_OPPONENTS:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT), (Fighting.RIGHT_OPPONENT));
							break;
						case ALL_OTHER_POKEMON:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT), (Fighting.RIGHT_OPPONENT),
									(fight.getActivePlayer() == Fighting.LEFT_PLAYER ? Fighting.RIGHT_PLAYER
											: Fighting.LEFT_PLAYER));
							break;
						case ALL_POKEMON:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT), (Fighting.RIGHT_OPPONENT),
									(Fighting.LEFT_PLAYER), (Fighting.RIGHT_PLAYER));
							break;
						case RANDOM_OPPONENT:
							if (fight.getPokemon(Fighting.LEFT_OPPONENT) != null
									&& fight.getPokemon(Fighting.RIGHT_OPPONENT) != null) {
								currentAttack.setTargets(
										Main.RNG.nextBoolean() ? (Fighting.LEFT_OPPONENT) : (Fighting.RIGHT_OPPONENT));
							} else if (fight.getPokemon(Fighting.LEFT_OPPONENT) != null) {
								currentAttack.setTargets((Fighting.LEFT_OPPONENT));
							} else {
								currentAttack.setTargets((Fighting.RIGHT_OPPONENT));
							}
							break;
						case USER:
							currentAttack.setTargets(fight.getActivePlayer());
							break;
						case USER_AND_ALLIES:
							currentAttack.setTargets((Fighting.LEFT_PLAYER), (Fighting.RIGHT_PLAYER));
							break;
						default:
							FightPanel.this.showTargets();
							return;
						}
						fight.registerAttack(currentAttack);
					} else {
						switch (currentAttack.getMove().getTarget()) {
						case ALL_OPPONENTS:
						case ALL_OTHER_POKEMON:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT));
							break;
						case ALL_POKEMON:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT),
									gController.getFight().getActivePlayer());
							break;
						case RANDOM_OPPONENT:
						case SELECTED_POKEMON:
						case SELECTED_POKEMON_ME_FIRST:
							currentAttack.setTargets((Fighting.LEFT_OPPONENT));
							break;
						case USER_AND_ALLIES:
						case USER_OR_ALLY:
						case USER:
							currentAttack.setTargets(gController.getFight().getActivePlayer());
							break;
						default:
							break;
						}
						gController.getFight().registerAttack(currentAttack);
					}

					// new Thread(new Runnable() {
					// @Override
					// public void run() {
					// do {
					// FightPanel.this.showText();
					// FightPanel.this.textLabel.setActive();
					// Move playerMove = ((MoveButton) e.getSource()).getMove();
					// if
					// (!playerMove.equals(FightPanel.this.gController.getFight()
					// .canUse(gController.getFight().getCurrentPokemon(),
					// playerMove))) {
					// FightPanel.this.addText(gController.getFight().getCurrentPokemon()
					// + " kann "
					// + playerMove.getName() + " nicht einsetzen!");
					// if (FightPanel.this.gController.getFight()
					// .canUse(gController.getFight().getCurrentPokemon(),
					// playerMove) == null) {
					// FightPanel.this.showMenu();
					// return;
					// }
					// }
					// } while (!FightPanel.this.gController.getFight()
					// .canChooseAction(gController.getFight().getCurrentPokemon()));
					// }
					// }).start();
				}
			});
		}
		for (int i = 0; i < targets.length; i++) {
			targets[i].setName(String.valueOf(i));
			targets[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					currentAttack.setTargets((int) (Integer.valueOf(((JComponent) e.getSource()).getName())));
					gController.getFight().registerAttack(currentAttack);
				}
			});
		}
		this.menu[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FightPanel.this.gController.getGameFrame().getInventoryPanel()
						.update(FightPanel.this.gController.getMainCharacter());
				FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.BAG);
				FightPanel.this.enemyAttack = true;
			}
		});
	}

	public void startRound() {
		this.gController.getFight().createAIAttacks();
		// if (gController.getFight().isDouble()) {
		// // TODO: Get Opponent attacks
		// } else {
		// // TODO: Get Opponent attacks
		// }
		new Thread(new Runnable() {
			@Override
			public void run() {
				FightPanel.this.showText();
				FightPanel.this.textLabel.setActive();
				FightPanel.this.gController.getFight().startRound();
				FightPanel.this.updateMoves();
				FightPanel.this.showMenu();
			}
		}).start();
	}

	public void throwBall(Integer ball) {
		if (!this.throwPokeball) {
			this.throwPokeball = true;
			new Thread(new Runnable() {

				private boolean caught = false;

				@Override
				public void run() {
					FightPanel.this.setComponentZOrder(FightPanel.this.pokeball, 1);
					FightPanel.this.pokeball.setVisible(true);
					FightPanel.this.pokeball.setBall(ball);
					FightPanel.this.pokeball.throwBall();
					Fighting fight = FightPanel.this.gController.getFight();
					if (fight.canEscape() && (!fight.isDouble() || (fight.getPokemon(Fighting.LEFT_OPPONENT) == null
							|| fight.getPokemon(Fighting.RIGHT_OPPONENT) == null))) {
						if (fight.getPokemon(Fighting.LEFT_OPPONENT) == null) {
							catching(FightPanel.this.pokemons[Fighting.RIGHT_OPPONENT],
									FightPanel.this.gController.getFight().getPokemon(Fighting.RIGHT_OPPONENT));
						} else {
							catching(FightPanel.this.pokemons[Fighting.LEFT_OPPONENT],
									FightPanel.this.gController.getFight().getPokemon(Fighting.LEFT_OPPONENT));
						}
						if (caught) {
							return;
						}
					} else {
						FightPanel.this.pokeball.drop();
						FightPanel.this.pokeball.setVisible(false);
						FightPanel.this.addText("Sei kein Dieb!");
					}
					// if (!caught &&
					// !FightPanel.this.gController.getFight().attack(FightPanel.this.enemy,
					// FightPanel.this.mine)) {
					// FightPanel.this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
					// }
					FightPanel.this.throwPokeball = false;
					FightPanel.this.gController.getFight().startRound();
				}

				private void catching(JLabel label, Pokemon pokemon) {
					label.setVisible(false);
					FightPanel.this.pokeball.drop();
					int shakes = pokemon.isCatched(ball);
					if (shakes == 4) {
						FightPanel.this.pokeball.shake(4);
						SoundController.getInstance().playSound(SoundController.POKEMON_CAUGHT);
						FightPanel.this.addText(pokemon.getName() + " wurde gefangen!");
						Player p = (Player) gController.getFight()
								.getCharacter(gController.getFight().getActivePlayer());
						if (p.getPokedex().addToCaught(pokemon.getId())) {
							FightPanel.this.addText(
									"Für " + pokemon.getName() + " wurde ein Eintrag in dein Pokedex erstellt!");
						}
						if (!FightPanel.this.gController.getMainCharacter().getTeam().addPokemon(pokemon)) {
							FightPanel.this.addText("Dein Team ist voll!");
							FightPanel.this
									.addText(pokemon.getName()
											+ " wurde auf deinem PC in " + FightPanel.this.gController
													.getMainCharacter().getPC().addPokemon(pokemon).getName()
											+ " gespeichert!");
						}
						FightPanel.this.gController.endFight();
						FightPanel.this.pokeball.setVisible(false);
						return;
					} else {
						FightPanel.this.pokeball.shake(shakes);
						label.setVisible(true);
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
						FightPanel.this.addText(pokemon.getName() + " hat sich befreit!");
					}
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
		// this.add(this.ownAnimations);
		// this.add(this.enemyAnimations);
		// this.add(this.playerStatPanel);
		// this.add(this.enemyStatPanel);
		// this.add(this.enemyPokemon);
		// this.add(this.ownPokemon);

		this.add(this.textLabel);
		for (int i = this.pokemons.length - 1; i >= 0; i--) {
			this.add(this.pokemons[i]);
			this.add(this.stats[i]);
			this.add(this.targets[i]);
		}
		this.add(this.pokeball);
		this.add(this.back);
		this.add(this.background);

		this.repaint();
	}

	public void setPokemon(int index) {
		boolean playSound = !this.gController.getFight().getPokemon(index).equals(this.pokemons[index]);
		this.pokemons[index].setPokemon(this.gController.getFight().getPokemon(index));
		this.updateMoves();
		if (playSound) {
			this.addText("Los " + this.pokemons[index].getName() + "!", true);
			// this.textLabel.waitText();
			SoundController.getInstance().playBattlecry(this.pokemons[index].getPokemon().getId());
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
			Pokemon p = this.gController.getFight().getCurrentPokemon();

			this.moves[i].setMove(p, p.getMoves()[i]);

			if (p.getMoves()[i] == null
					|| !p.getMoves()[i].equals(this.gController.getFight().canUse(p, p.getMoves()[i]))) {
				this.moves[i].setEnabled(false);
			}

		}
	}

	// public void setEnemy() {
	// boolean playSound =
	// !this.gController.getFight().getEnemy().equals(this.enemy);
	// this.enemyPokemon.setVisible(false);
	// this.enemy = this.gController.getFight().getEnemy();
	// if (!this.gController.getFight().canEscape() && playSound) {
	// this.addText(this.gController.getFight().getEnemyCharacter().getName() +
	// "
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
		Fighting fight = this.gController.getFight();

		Pokemon[] oldPokemon = new Pokemon[4];

		for (int i : new int[] { Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT,
				Fighting.RIGHT_OPPONENT }) {
			oldPokemon[i] = this.stats[i].getPokemon();

			this.stats[i].setPokemon(fight.getPokemon(i));

			if (fight.getPokemon(i) != null) {
				if (this.stats[i].getHPBar().getValue() == 0
						|| (fight.getPokemon(i) != null && !fight.getPokemon(i).equals(oldPokemon[i]))) {
					this.stats[i].getHPBar().setMaximum(fight.getPokemon(i).getStats().getStats().get(Stat.HP));
					this.stats[i].getHPBar().setValue(fight.getPokemon(i).getStats().getCurrentHP());
				} else {
					this.stats[i].getHPBar().setMaximum(fight.getPokemon(i).getStats().getStats().get(Stat.HP));
					this.stats[i].getHPBar().updateValue(fight.getPokemon(i).getStats().getCurrentHP());
				}
			} else {
				this.stats[i].setVisible(false);
			}
		}

		int counter = 0;

		while (!isHPFinished()) {
			for (int i = 0; i < stats.length; i++) {
				HPBar hp = this.stats[i].getHPBar();
				if (!hp.isFinished()) {
					if (counter % 10 == 0) {
						if (hp.isFalling()) {
							this.pokemons[i].setVisible(!this.pokemons[i].isVisible());
						}
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					this.pokemons[i].setVisible(this.gController.getFight().isVisible(this.pokemons[i].getPokemon()));
				}
			}
			if (fight.isDouble()) {
				SoundController.getInstance().updatePokemonLow(this.stats[Fighting.LEFT_PLAYER].getHPBar(),
						this.stats[Fighting.RIGHT_PLAYER].getHPBar());
			} else {
				SoundController.getInstance().updatePokemonLow(this.stats[Fighting.LEFT_PLAYER].getHPBar());
			}
			counter++;
		}

		for (int i : new int[] { Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT,
				Fighting.RIGHT_OPPONENT }) {
			this.pokemons[i].setVisible(fight.isVisible(fight.getPokemon(i)));
			this.stats[i].updateAilment();
			this.stopWaiting();
		}

		for (int i : new int[] { Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT,
				Fighting.RIGHT_OPPONENT }) {
			Team team = fight.getTeam(i);
			int offset = 0;
			JLabel[] labels = null;
			switch (i) {
			case Fighting.RIGHT_PLAYER:
				offset = 6;
			case Fighting.LEFT_PLAYER:
				labels = this.playerPokemons;
				break;
			case Fighting.RIGHT_OPPONENT:
				offset = 6;
			case Fighting.LEFT_OPPONENT:
				labels = this.enemyPokemons;
				break;
			default:
				continue;
			}
			for (counter = 0; counter < Team.MAX_SIZE && (counter + offset) < labels.length; counter++) {
				if (team == null || team.getPokemon(counter) == null) {
					labels[counter + offset].setVisible(false);
				} else if (team.getPokemon(counter).getAilment() == Ailment.FAINTED) {
					labels[counter + offset].setIcon(new ImageIcon(this.grayPokeball));
					labels[counter + offset].setVisible(true);
				} else {
					labels[counter + offset].setIcon(new ImageIcon(this.coloredPokeball));
					labels[counter + offset].setVisible(true);
				}
			}
		}
		Main.FORCE_REPAINT = true;
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
			this.targets[i].setVisible(false);
		}
		this.back.setVisible(false);
		this.textLabel.setVisible(false);
		this.repaint();
	}

	public void showMoves() {
		for (int i = 0; i < FightPanel.this.menu.length; i++) {
			FightPanel.this.menu[i].setVisible(false);
			FightPanel.this.moves[i].setVisible(true);
			this.targets[i].setVisible(false);
		}
		FightPanel.this.back.setVisible(true);
		this.back.removeActionListener(MOVE_BACK);
		this.back.removeActionListener(TARGET_BACK);
		this.back.addActionListener(MOVE_BACK);
	}

	public void showTargets() {
		for (int i : new int[] { Fighting.LEFT_OPPONENT, Fighting.LEFT_PLAYER, Fighting.RIGHT_OPPONENT,
				Fighting.RIGHT_PLAYER }) {
			// TODO: Check if the move can target current the Pokemon
			if (this.gController.getFight().getPokemon(i) != null) {
				targets[i].setText(this.gController.getFight().getPokemon(i).getName());
				switch (currentAttack.getMove().getTarget()) {
				case SELECTED_POKEMON:
					targets[i].setEnabled(i != this.gController.getFight().getActivePlayer());
					break;
				case SELECTED_POKEMON_ME_FIRST:
					// TODO
					break;
				case SPECIFIC_MOVE:
					// TODO
					break;
				case USER_OR_ALLY:
					targets[i].setEnabled(this.gController.getFight().isPlayer(i));
					break;
				default:
					targets[i].setEnabled(false);
				}
			} else {
				targets[i].setText("");
				targets[i].setEnabled(false);
			}
		}
		for (int i = 0; i < this.menu.length; i++) {
			this.menu[i].setVisible(false);
			this.moves[i].setVisible(false);
			this.targets[i].setVisible(true);
		}
		this.back.setVisible(true);
		this.textLabel.setVisible(false);

		this.back.removeActionListener(MOVE_BACK);
		this.back.removeActionListener(TARGET_BACK);
		this.back.addActionListener(TARGET_BACK);
		this.repaint();
	}

	public void showText() {
		for (int i = 0; i < this.menu.length; i++) {
			this.menu[i].setVisible(false);
			this.moves[i].setVisible(false);
			this.targets[i].setVisible(false);
		}
		this.back.setVisible(false);
		this.textLabel.setVisible(true);
		this.repaint();
	}

	public void getEnemyMoves() {
		Fighting fight = this.gController.getFight();

		if (fight.isDouble()) {

		} else {

		}
	}

	// public void checkEnemyAttack() {
	// if (this.enemyAttack) {
	// this.gController.getFight().attack(this.enemy, this.mine);
	// this.enemyAttack = false;
	// this.gController.getFight().endTurn();
	// }
	// }

	public void pause() {
		while (!this.textLabel.isEmpty() && !this.textLabel.isWaiting()) {
			this.gController.sleep(50);
			this.textLabel.repaint();
		}
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
		this.stats[Fighting.LEFT_OPPONENT].setVisible(false);
		this.stats[Fighting.RIGHT_OPPONENT].setVisible(false);

		this.pokemons[Fighting.LEFT_OPPONENT].setVisible(false);
		this.pokemons[Fighting.RIGHT_OPPONENT].setVisible(false);

		for (JLabel pokemon : this.enemyPokemons) {
			pokemon.setVisible(false);
		}
	}

	// public AnimationLabel getEnemyAnimation() {
	// return this.enemyAnimations;
	// }
	//
	// public AnimationLabel getPlayerAnimation() {
	// return this.ownAnimations;
	// }

	public PokemonLabel getPokemonLabel(Pokemon pokemon) {
		for (PokemonLabel p : this.pokemons) {
			if (p.getPokemon() != null && p.getPokemon().equals(pokemon)) {
				return p;
			}
		}
		return null;
	}

}

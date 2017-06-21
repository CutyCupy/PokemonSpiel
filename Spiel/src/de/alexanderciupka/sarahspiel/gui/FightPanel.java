package de.alexanderciupka.sarahspiel.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.FightOption;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

@SuppressWarnings("serial")
public class FightPanel extends JPanel {

	private JLabel enemyPokemon;
	private JLabel ownPokemon;
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
	private JLabel playerStats;
	private JLabel enemyStats;
	private JPanel playerStatPanel;
	private JPanel enemyStatPanel;
	private boolean enemyAttack;
	private boolean attacked;
	private boolean throwPokeball;
	private TextLabel textLabel;
	private JLabel pokeball;

	private HPBar playerHPBar;
	private HPBar enemyHPBar;

	private AilmentLabel playerAilmentLabel;
	private AilmentLabel enemyAilmentLabel;

	private Pokemon enemy;
	private Pokemon mine;

	private GameController gController;

	public FightPanel(Pokemon mine, Pokemon enemy) {
		super();
		setLayout(new BorderLayout());
		gController = GameController.getInstance();
		this.enemy = enemy;
		this.mine = mine;
		playerHPBar = new HPBar();
		enemyHPBar = new HPBar();
		menu = new JButton[4];
		moves = new MoveButton[4];
		pokeball = new JLabel();
		pokeball.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/pokeballs/pokeball.png")).getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH)));
		textLabel = new TextLabel();
		textLabel.setBounds(5, 480, 600, 110);
		textLabel.setOpaque(true);
		textLabel.setVisible(false);
		textLabel.setBackground(Color.WHITE);
		textLabel.setDelay(TextLabel.SLOW);
		textLabel.setAutoMove(true);
		setBounds(0, 0, 630, 630);
		enemyPokemon = new JLabel();
		enemyStats = new JLabel();
		playerStatPanel = new JPanel(null);
		enemyStatPanel = new JPanel(null);

		playerAilmentLabel = new AilmentLabel();
		enemyAilmentLabel = new AilmentLabel();

		enemyStats.setBackground(Color.WHITE);
		enemyStats.setOpaque(false);
		ownPokemon = new JLabel();
		playerStats = new JLabel();
		playerStats.setVerticalAlignment(SwingConstants.TOP);
		enemyStats.setVerticalAlignment(SwingConstants.TOP);
		background = new JLabel(new ImageIcon(this.getClass().getResource("/battles/battlebgGrassy.png")));
		playerStats.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(2, 10, 2, 10)));
		enemyStats.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(2, 10, 2, 10)));
		enemyPokemon.setBounds(430, 150, 100, 100);
		enemyStatPanel.setBounds(45, 150, 180, 40);
		enemyStats.setLocation(0, 0);
		enemyStats.setSize(enemyStatPanel.getSize());
		ownPokemon.setBounds(100, 275, 100, 100);
		playerStatPanel.setBounds(375, 275, 180, 40);
		playerStats.setLocation(0, 0);
		playerStats.setSize(playerStatPanel.getSize());
		background.setBounds(0, 0, 630, 430);
		pokeball.setVisible(false);
		pokeball.setOpaque(false);
		attack = new JButton("Kampf");
		attack.setBounds(360, 480, 100, 40);
		attack.setBackground(Color.WHITE);
		attack.setFocusable(false);
		bag = new JButton("Pokeball");
		bag.setBounds(470, 480, 100, 40);
		bag.setBackground(Color.WHITE);
		bag.setFocusable(false);
		pokemon = new JButton("Pokemon");
		pokemon.setBounds(360, 530, 100, 40);
		pokemon.setBackground(Color.WHITE);
		pokemon.setFocusable(false);
		escape = new JButton("Flucht");
		escape.setBounds(470, 530, 100, 40);
		escape.setBackground(Color.WHITE);
		escape.setFocusable(false);
		firstMove = new MoveButton();
		firstMove.setBounds(40, 480, 150, 40);
		firstMove.setBackground(Color.WHITE);
		firstMove.setVisible(false);
		firstMove.setFocusable(false);
		secondMove = new MoveButton();
		secondMove.setBounds(200, 480, 150, 40);
		secondMove.setBackground(Color.WHITE);
		secondMove.setVisible(false);
		secondMove.setFocusable(false);
		thirdMove = new MoveButton();
		thirdMove.setBounds(40, 530, 150, 40);
		thirdMove.setBackground(Color.WHITE);
		thirdMove.setVisible(false);
		thirdMove.setFocusable(false);
		fourthMove = new MoveButton();
		fourthMove.setBounds(200, 530, 150, 40);
		fourthMove.setBackground(Color.WHITE);
		fourthMove.setVisible(false);
		fourthMove.setFocusable(false);
		back = new JButton("Zurück");
		back.setBounds(360, 530, 75, 40);
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
		setPlayer();
		setEnemy();
		addActionListener();
		addComponents();
	}

	private void addActionListener() {
		attack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < menu.length; i++) {
					updateMoves();
					menu[i].setVisible(false);
					moves[i].setVisible(true);
				}
				back.setVisible(true);
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
			}
		});
		for (int i = 0; i < moves.length; i++) {
			moves[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!attacked && mine.getMoveByName(((JButton) e.getSource()).getName()).getCurrentPP() > 0) {
						attacked = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								showText();
								textLabel.setActive();
								Move playerMove = mine.getMoveByName(((JButton) e.getSource()).getName());
								Move enemyMove = enemy.getMove(mine);
								boolean playerStarts = gController.getFight().isPlayerStart(playerMove, enemyMove);
								boolean wholeTurn = true;
								if (playerStarts) {
									if(gController.playerAttack(playerMove)) {
										gController.enemyAttack(enemyMove);

									} else {
										wholeTurn = false;
										if(!gController.getFight().enemyDead()) {
											setEnemy();
										}
									}
								} else {
									if(gController.enemyAttack(enemyMove)) {
										gController.playerAttack(playerMove);
									} else {
										wholeTurn = false;
										gController.getFight().setCurrentFightOption(FightOption.POKEMON);
										gController.repaint();
									}
								}
								if(wholeTurn) {
									mine.afterTurnDamage();
									enemy.afterTurnDamage();
								}
								updateMoves();
								showMenu();
								attacked = false;
							}
						}).start();
					}
				}
			});
		}
		bag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!throwPokeball) {
					throwPokeball = true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							pokeball.setVisible(true);
							for(int x = 150; x <= 480; x++) {
								try {
									Thread.sleep(2);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								pokeball.setBounds(x, nextPokeballCoordinate(x), 50, 50);
								revalidate();
								repaint();
							}
							
							if(gController.getFight().canEscape()) {
								System.out.println((1f / enemy.getStats().getLevel()) * (enemy.getStats().getCurrentHP() / enemy.getStats().getStats()[0]));
								if(new Random().nextFloat() < (1f / enemy.getStats().getLevel()) * (enemy.getStats().getCurrentHP() / enemy.getStats().getStats()[0])) {
									enemyPokemon.setVisible(false);
									addText(enemy.getName() + " wurde gefangen!");
									if(gController.getMainCharacter().getTeam().getAmmount() < 6) {
										gController.getMainCharacter().getTeam().addPokemon(enemy);
									} else {
										addText("Dein Team ist voll!");
										addText(enemy.getName() + " wurde auf deinem PC in " + gController.getMainCharacter().getPC().addPokemon(enemy).getName() + " gespeichert!");
										
									}
									gController.endFight();
									pokeball.setVisible(false);
									return;
								} else {
									pokeball.setVisible(false);
									addText(enemy.getName() + " hat sich befreit!");
								}
							} else {
								pokeball.setVisible(false);
								addText("Sei kein Dieb!");
							}
							if(!gController.enemyAttack()) {
								gController.getFight().setCurrentFightOption(FightOption.POKEMON);
								gController.repaint();
							}
							throwPokeball = false;
							showMenu();
						}
					}).start();
				}
			}
		});
	}

	private int nextPokeballCoordinate(int x) {
		return (int) ((0.0038170163 * Math.pow(x, 2) - 2.7835081585 * x + 656.6433566434) - 40);
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
		this.mine = gController.getFight().getPlayer();
		ownPokemon.setIcon(new ImageIcon(this.getClass().getResource("/pokemon/back/" + this.mine.getId() + ".png")));
		updateMoves();
		updatePanels();
	}

	public void updateMoves() {
		for (int i = 0; i < moves.length; i++) {
			moves[i].setMove(mine.getMoves()[i]);
		}
	}

	public void setEnemy() {
		this.enemy = gController.getFight().getEnemy();
		enemyPokemon.setIcon(new ImageIcon(this.getClass().getResource("/pokemon/front/" + this.enemy.getId() + ".png")));
		updatePanels();
	}

	public void updatePanels() {
//		playerStats.setHorizontalAlignment(SwingConstants.CENTER);
//		enemyStats.setHorizontalAlignment(SwingConstants.CENTER);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {

				playerStats.setText(mine.getName() + " Lv.: " + mine.getStats().getLevel());
				enemyStats.setText(enemy.getName() + " Lv.: " + enemy.getStats().getLevel());

				playerHPBar.setMaximum(mine.getStats().getStats()[0]);
				enemyHPBar.setMaximum(enemy.getStats().getStats()[0]);

				playerHPBar.updateValue(mine.getStats().getCurrentHP());
				enemyHPBar.updateValue(enemy.getStats().getCurrentHP());

				int counter = 0;

				while(!playerHPBar.isFinished() || !enemyHPBar.isFinished()) {
					if(playerHPBar.isFinished()) {
						ownPokemon.setVisible(true);
					} else if(enemyHPBar.isFinished()){
						ownPokemon.setVisible(true);
					}
					if(counter % 10 == 0) {
						if(!playerHPBar.isFinished()) {
							ownPokemon.setVisible(!ownPokemon.isVisible());
						} else if (!enemyHPBar.isFinished()) {
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

				ownPokemon.setVisible(true);
				enemyPokemon.setVisible(true);

				stopWaiting();

				playerAilmentLabel.setAilment(mine.getAilment());
				enemyAilmentLabel.setAilment(enemy.getAilment());

				playerAilmentLabel.setLocation(playerStats.getWidth() - playerAilmentLabel.getWidth() - 10, 5);
				enemyAilmentLabel.setLocation(enemyStats.getWidth() - enemyAilmentLabel.getWidth() - 10, 5);

				playerHPBar.setSize(playerStats.getWidth() - 20, 10);
				playerHPBar.setLocation(10, playerStats.getHeight() - playerHPBar.getHeight() - 7);

				enemyHPBar.setSize(enemyStats.getWidth() - 20, 10);
				enemyHPBar.setLocation(10, enemyStats.getHeight() - enemyHPBar.getHeight() - 7);
//			}
//		}).start();


//		playerHPBar.setForeground(this.mine.getStats().getHPColor());
//		enemyHPBar.setForeground(this.enemy.getStats().getHPColor());
	}


	public void showMenu() {
		for (int i = 0; i < menu.length; i++) {
			menu[i].setVisible(true);
			moves[i].setVisible(false);
		}
		back.setVisible(false);
		textLabel.setVisible(false);
	}

	public void checkEnemyAttack() {
		if(enemyAttack) {
			gController.enemyAttack();
			enemyAttack = false;
		}
	}

	public void pause() {
		while(!textLabel.isEmpty() && !textLabel.isWaiting()) {
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
	}

	public void addText(String text) {
		this.addText(text, false);
	}

	public void addText(String text, boolean wait) {
		this.textLabel.setWaiting(wait);
		if(!textLabel.isVisible()) {
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

}

package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.menu.MenuController;
import de.alexanderciupka.sarahspiel.pokemon.Direction;
import de.alexanderciupka.sarahspiel.pokemon.FightOption;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.Player;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {

	private int characterX;
	private int characterY;
	private FightPanel fight;
	private JPanel bag;
	private PokemonPanel pokemon;
	private JPanel map;
	private NewAttackPanel newMove;
	private PCPanel pc;
	private ReportPanel report;
	private BackgroundLabel imageHolder;
	private TextLabel dialogue;
	private GameController gController;
	private boolean active;
	private JPanel currentPanel;
	private static final long COOLDOWN = 0;

	public GameFrame() {
		map = new JPanel(null);
		setContentPane(map);
		gController = GameController.getInstance();
		setUndecorated(true);
		setVisible(true);
		setResizable(false);
		setBounds(MenuController.getToCenter(630, 630));
		characterX = 315 - gController.getMainCharacter().getCharacterImage().getWidth(null) / 2;
		characterY = 315 - gController.getMainCharacter().getCharacterImage().getHeight(null) / 2;
		imageHolder = new BackgroundLabel(characterX, characterY);
		imageHolder.setBounds(0, 0, 630, 630);
		dialogue = new TextLabel();
		dialogue.setBounds(5, 480, 600, 110);
		dialogue.setOpaque(true);
		dialogue.setVisible(false);
		dialogue.setBackground(Color.WHITE);
		map.add(imageHolder);
		map.add(dialogue);
		addActions();
		this.paint(getGraphics());
	}

	@Override
	public void paint(Graphics g) {
		if (!gController.isFighting()) {
			if(currentPanel != null) {
				setContentPane(currentPanel);
			} else {
				setContentPane(map);
				map.paint(g);
				map.repaint();
			}
		} else {
			setContentPane(gController.getCurrentFightPanel());
		}
	}

	public void startFight(Pokemon player, Pokemon enemy) {
		fight = new FightPanel(player, enemy);
		fight.setBorder(new EmptyBorder(5, 5, 5, 5));
		fight.setLayout(null);
		bag = new BagPanel();
		bag.setBorder(new EmptyBorder(5, 5, 5, 5));
		bag.setLayout(null);
		pokemon = new PokemonPanel();
		pokemon.setBorder(new EmptyBorder(5, 5, 5, 5));
		pokemon.setLayout(null);
		report = new ReportPanel();
		repaint();
	}

	public void stopFight() {
		setContentPane(map);
		repaint();
	}

	public FightPanel getFightPanel() {
		return fight;
	}

	public JPanel getBagPanel() {
		return bag;
	}

	public PokemonPanel getPokemonPanel() {
		pokemon.update();
		return pokemon;
	}

	public void addDialogue(String text) {
		dialogue.addText(text);
		dialogue.setActive();
	}

	public boolean isDialogueEmpty() {
		return dialogue.isEmpty();
	}

	public TextLabel getDialogue() {
		return dialogue;
	}

	public void setDelay(long slow) {
		this.dialogue.setDelay(slow);
	}

	public void displayNewMove(Pokemon pokemon, Move newMove) {
		if(this.newMove == null) {
			this.newMove = new NewAttackPanel();
			this.newMove.setBorder(new EmptyBorder(5, 5, 5, 5));
		}
		this.newMove.set(pokemon, newMove);
		this.gController.getFight().setCurrentFightOption(FightOption.NEW_ATTACK);
		gController.repaint();
	}

	public NewAttackPanel getNewAttackPanel() {
		return newMove;
	}

	public void setActive() {
		this.active = false;
	}

	public void setCurrentPanel(JPanel currentPanel) {
		this.currentPanel = currentPanel;
		if(this.gController.isFighting() && currentPanel instanceof PokemonPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
		} else if(this.gController.isFighting() && currentPanel instanceof ReportPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.REPORT);
		}
		this.repaint();
	}

	private void addActions() {
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "up");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		map.getActionMap().put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					if(!active) {
						active = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								gController.move(Direction.UP);
								try {
									Thread.sleep(COOLDOWN);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								active = false;
								repaint();
							}
						}).start();
					}
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "left");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		map.getActionMap().put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					if(!active) {
						active = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								gController.move(Direction.LEFT);
								try {
									Thread.sleep(COOLDOWN);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								active = false;
								repaint();
							}
						}).start();
					}
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "right");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		map.getActionMap().put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					if(!active) {
						active = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								gController.move(Direction.RIGHT);
								try {
									Thread.sleep(COOLDOWN);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								active = false;
								repaint();
							}
						}).start();
					}
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "run");
		map.getActionMap().put("run", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gController.getMainCharacter().toggleWalkingSpeed();
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "down");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		map.getActionMap().put("down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					if(!active) {
						active = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								gController.move(Direction.DOWN);
								try {
									Thread.sleep(COOLDOWN);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								active = false;
								repaint();
							}
						}).start();
					}
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		map.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.FAST);
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							gController.checkInteraction();
						}
					}).start();
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"), "released space");
		map.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		map.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!gController.isFighting() && gController.getInteractionPause()) {
					dialogue.setActive();
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		map.getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!gController.getInteractionPause() && !gController.isFighting()) {
					gController.returnToMenu();
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "pokemon");
		map.getActionMap().put("pokemon", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!gController.isFighting() && !gController.getInteractionPause()) {
					pokemon = new PokemonPanel();
					pokemon.setBorder(new EmptyBorder(5, 5, 5, 5));
					pokemon.setLayout(null);
					report = new ReportPanel();
					currentPanel = pokemon;
					repaint();
				}
			}
		});
	}

	public void displayPC(Player owner) {
		if(this.pc == null) {
			this.pc = new PCPanel();
		}
		this.pc.setPC(owner.getPC());
		this.setCurrentPanel(this.pc.getContentPane());
	}

	public JPanel getReportPanel() {
		return report;
	}
}

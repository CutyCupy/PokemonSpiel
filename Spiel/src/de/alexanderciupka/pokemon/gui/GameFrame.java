package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.gui.overlay.DarkOverlay;
import de.alexanderciupka.pokemon.gui.panels.EvolutionPanel;
import de.alexanderciupka.pokemon.gui.panels.FightPanel;
import de.alexanderciupka.pokemon.gui.panels.GeneratorPanel;
import de.alexanderciupka.pokemon.gui.panels.InventoryPanel;
import de.alexanderciupka.pokemon.gui.panels.NewAttackPanel;
import de.alexanderciupka.pokemon.gui.panels.PCPanel;
import de.alexanderciupka.pokemon.gui.panels.PokemonPanel;
import de.alexanderciupka.pokemon.gui.panels.ReportPanel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Item;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {

	private int characterX;
	private int characterY;
	private FightPanel fight;
	private PokemonPanel pokemon;
	private JPanel map;
	private NewAttackPanel newMove;
	private PCPanel pc;
	private ReportPanel report;
	private InventoryPanel inventory;
	private EvolutionPanel evolution;
	private BackgroundLabel imageHolder;
	private TextLabel dialogue;
	private GameController gController;
	private boolean active;
	private JPanel currentPanel;
	private static final long COOLDOWN = 250;

	public static final int GRID_SIZE = 70;
	public static final int FRAME_SIZE = GRID_SIZE * 9;

	private Direction currentDirection;
	private Stack<JPanel> panelHistory;
	private boolean back;

	private boolean fighting;

	private ArrayList<Direction> directions;

	@Override
	public void repaint() {
		super.repaint();
	}

	public GameFrame() {
		directions = new ArrayList<>();
		directions.add(Direction.NONE);
		currentDirection = Direction.NONE;
		map = new JPanel(null);
		setContentPane(map);
		map.setBackground(Color.black);
		gController = GameController.getInstance();
		setUndecorated(true);
		setVisible(true);
		setResizable(false);
		setBounds(MenuController.getToCenter(FRAME_SIZE, FRAME_SIZE));
		characterX = 315 - gController.getMainCharacter().getCharacterImage().getWidth(null) / 2;
		characterY = 315 - gController.getMainCharacter().getCharacterImage().getHeight(null) / 2;
		imageHolder = new BackgroundLabel(characterX, characterY);
		imageHolder.setBounds(0, 0, FRAME_SIZE, FRAME_SIZE);
		dialogue = new TextLabel();
		dialogue.setBounds(5, 480, 600, 110);
		dialogue.setOpaque(true);
		dialogue.setVisible(false);
		dialogue.setBackground(Color.WHITE);
		inventory = new InventoryPanel();
		pokemon = new PokemonPanel();
		pokemon.setBorder(new EmptyBorder(5, 5, 5, 5));
		pokemon.setLayout(null);
		report = new ReportPanel();
		evolution = new EvolutionPanel();
		map.add(imageHolder);
		map.add(dialogue);

		panelHistory = new Stack<JPanel>();

		addActions();
		this.paint(getGraphics());

	}

	@Override
	public void paint(Graphics g) {
		if (!gController.isFighting()) {
			if (this.fighting) {
				this.fighting = false;
			}
			if (currentPanel != null) {
				if (!currentPanel.equals(getContentPane())) {
					setContentPane(currentPanel);
				}
			} else {
				if (!map.equals(getContentPane())) {
					setContentPane(map);
				}
				map.repaint();
			}
		} else if (this.fighting) {
			if (gController.getCurrentFightPanel() != null
					&& !gController.getCurrentFightPanel().equals(getContentPane())) {
				setContentPane(gController.getCurrentFightPanel());
			}
		}
		getContentPane().repaint();
	}

	@Override
	public void setContentPane(Container contentPane) {
		if (dialogue != null) {
			dialogue.setParent((JPanel) contentPane);
		}
		super.setContentPane(contentPane);
	}

	public JPanel getCurrentPanel() {
		return this.currentPanel;
	}

	public void startFight(Pokemon player, Pokemon enemy) {
		fight = new FightPanel(player, enemy);
		fight.setBorder(new EmptyBorder(5, 5, 5, 5));
		fight.setLayout(null);
		this.fighting = true;
		pokemon.update();
		// repaint();
		if (!gController.getFight().canEscape()) {
			this.getFightPanel()
					.addText("Eine Herausforderung von " + gController.getFight().getEnemyCharacter().getName() + "!");
		} else {
			this.getFightPanel().addText("Ein wildes " + enemy.getName() + " erscheint!");
		}
		fight.setEnemy();
		fight.setPlayer();
		fight.updatePanels();
	}

	public void stopFight() {
		gController.waitDialogue();
		setCurrentPanel(null);
		// repaint();
	}

	public FightPanel getFightPanel() {
		return fight;
	}

	public PokemonPanel getPokemonPanel() {
		return pokemon;
	}

	public void addDialogue(String text) {
		if (text != null) {
			dialogue.addText(text);
			dialogue.setActive();
		}
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
		if (this.newMove == null) {
			this.newMove = new NewAttackPanel();
			this.newMove.setBorder(new EmptyBorder(5, 5, 5, 5));
		}
		this.newMove.set(pokemon, newMove);
		if (gController.isFighting()) {
			this.gController.getFight().setCurrentFightOption(FightOption.NEW_ATTACK);
		} else {
			this.setCurrentPanel(this.newMove);
		}
		gController.repaint();
	}

	public NewAttackPanel getNewAttackPanel() {
		return newMove;
	}

	public void setActive() {
		this.active = false;
	}

	public void setCurrentPanel(JPanel currentPanel) {
		if (!this.back && (currentPanel != null || this.currentPanel != null)
				&& ((currentPanel != null && !currentPanel.getClass().isInstance(this.currentPanel))
						|| (this.currentPanel != null && !this.currentPanel.getClass().isInstance(currentPanel)))) {
			this.panelHistory.push(this.currentPanel);
		}
		if(this.currentPanel instanceof GeneratorPanel) {
			map.add(imageHolder);
		}
		if (this.gController.isFighting() && currentPanel instanceof PokemonPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
		} else if (this.gController.isFighting() && currentPanel instanceof ReportPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.REPORT);
		} else if (this.gController.isFighting() && currentPanel instanceof InventoryPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.BAG);
		} else if(currentPanel instanceof GeneratorPanel) {
			currentPanel.add(imageHolder);
		}
		this.currentPanel = currentPanel;
		this.back = false;
	}

	private void addActions() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!gController.isFighting() && !gController.getInteractionPause()) {
						if (!active) {
							active = true;
							currentDirection = directions.get(0);
							if (currentDirection != Direction.NONE) {
								if (!gController.move(currentDirection)) {
									SoundController.getInstance().playSound(SoundController.BUMP);
									try {
										Thread.sleep(COOLDOWN);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							active = false;
						}
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "up");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "stopup");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "stopup");

		map.getActionMap().put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!directions.contains(Direction.UP)) {
					directions.add(0, Direction.UP);
				}
			}
		});

		map.getActionMap().put("stopup", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				directions.remove(Direction.UP);
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "left");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "stopleft");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "stopleft");

		map.getActionMap().put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!directions.contains(Direction.LEFT)) {
					directions.add(0, Direction.LEFT);
				}
			}
		});

		map.getActionMap().put("stopleft", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				directions.remove(Direction.LEFT);
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "right");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "stopright");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "stopright");

		map.getActionMap().put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!directions.contains(Direction.RIGHT)) {
					directions.add(0, Direction.RIGHT);
				}
			}
		});

		map.getActionMap().put("stopright", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				directions.remove(Direction.RIGHT);
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "down");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "stopdown");
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), "stopdown");

		map.getActionMap().put("down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!directions.contains(Direction.DOWN)) {
					directions.add(0, Direction.DOWN);
				}
			}
		});

		map.getActionMap().put("stopdown", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				directions.remove(Direction.DOWN);
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "run");
		map.getActionMap().put("run", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gController.getMainCharacter().toggleWalkingSpeed();
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		map.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
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
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		map.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		pokemon.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
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
		pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		pokemon.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		map.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && gController.getInteractionPause()) {
					dialogue.setActive();
				}
			}
		});

		pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		pokemon.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogue.setActive();
			}
		});

		evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		evolution.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.FAST);
				}
			}
		});
		evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		evolution.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		evolution.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && gController.getInteractionPause()) {
					dialogue.setActive();
				}
			}
		});

		inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		inventory.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.FAST);
				}
			}
		});
		inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		inventory.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.getInteractionPause()) {
					dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		inventory.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && gController.getInteractionPause()) {
					dialogue.setActive();
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		map.getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.getInteractionPause() && !gController.isFighting()) {
					gController.returnToMenu();
				}
			}
		});
		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "pokemon");
		map.getActionMap().put("pokemon", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					pokemon.update();
					currentPanel = pokemon;
					// repaint();
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("I"), "inventory");
		map.getActionMap().put("inventory", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!gController.isFighting() && !gController.getInteractionPause()) {
					inventory.update(gController.getMainCharacter());
					currentPanel = inventory;
					// repaint();
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "flash");
		map.getActionMap().put("flash", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (gController.getMainCharacter().hasItem(Item.FLASH)
						&& gController.getMainCharacter().getCurrentRoute().isDark()) {
					((DarkOverlay) (getBackgroundLabel().getOverlay(DarkOverlay.class))).flash();
				}
			}
		});

		map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("C"), "cheat");
		map.getActionMap().put("cheat", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				gController.getMainCharacter().ignoreCollisions = !gController.getMainCharacter().ignoreCollisions;
			}
		});

	}

	public void displayPC(Player owner) {
		if (this.pc == null) {
			this.pc = new PCPanel();
		}
		this.pc.setPC(owner.getPC());
		SoundController.getInstance().playSound(SoundController.PC_BOOT, true);
		this.setCurrentPanel(this.pc.getContentPane());
		// this.repaint();
	}

	public JPanel getReportPanel() {
		return report;
	}

	public BackgroundLabel getBackgroundLabel() {
		return imageHolder;
	}

	public InventoryPanel getInventoryPanel() {
		return this.inventory;
	}

	public EvolutionPanel getEvolutionPanel() {
		return this.evolution;
	}

	public JPanel getLastPanel() {
		return getLastPanel(false);
	}

	public JPanel getLastPanel(boolean withEvolution) {
		this.back = true;
		if (panelHistory.isEmpty()) {
			return null;
		} else {
			JPanel p = panelHistory.pop();
			if (this.currentPanel.getClass().isInstance(p)) {
				return getLastPanel(withEvolution);
			}
			if (!withEvolution) {
				if (p instanceof EvolutionPanel) {
					return getLastPanel(withEvolution);
				}
			}
			return p;
		}
	}
}

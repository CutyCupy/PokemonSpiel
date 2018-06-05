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
import de.alexanderciupka.pokemon.characters.NPC;
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

	public static final int GRID_SIZE = 42;
	public static final int FRAME_SIZE = GRID_SIZE * 15;

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
		this.directions = new ArrayList<>();
		this.directions.add(Direction.NONE);
		this.currentDirection = Direction.NONE;
		this.map = new JPanel(null);
		this.setContentPane(this.map);
		this.map.setBackground(Color.black);
		this.gController = GameController.getInstance();
		this.setUndecorated(true);
		this.setVisible(true);
		this.setResizable(false);
		this.setBounds(MenuController.getToCenter(FRAME_SIZE, FRAME_SIZE));
		this.characterX = 315 - this.gController.getMainCharacter().getCharacterImage().getWidth(null) / 2;
		this.characterY = 315 - this.gController.getMainCharacter().getCharacterImage().getHeight(null) / 2;
		this.imageHolder = new BackgroundLabel(this.characterX, this.characterY);
		this.imageHolder.setBounds(0, 0, FRAME_SIZE, FRAME_SIZE);
		this.dialogue = new TextLabel();
		this.dialogue.setBounds(5, 480, 600, 110);
		this.dialogue.setOpaque(true);
		this.dialogue.setVisible(false);
		this.dialogue.setBackground(Color.WHITE);
		this.inventory = new InventoryPanel();
		this.pokemon = new PokemonPanel();
		this.pokemon.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.pokemon.setLayout(null);
		this.report = new ReportPanel();
		this.evolution = new EvolutionPanel();
		this.map.add(this.imageHolder);
		this.map.add(this.dialogue);

		this.panelHistory = new Stack<JPanel>();

		this.addActions();
		this.paint(this.getGraphics());

	}

	@Override
	public void paint(Graphics g) {
		if (!this.gController.isFighting()) {
			if (this.fighting) {
				this.fighting = false;
			}
			if (this.currentPanel != null) {
				if (!this.currentPanel.equals(this.getContentPane())) {
					this.setContentPane(this.currentPanel);
				}
			} else {
				if (!this.map.equals(this.getContentPane())) {
					this.setContentPane(this.map);
				}
				this.map.repaint();
			}
		} else if (this.fighting) {
			if (this.gController.getCurrentFightPanel() != null
					&& !this.gController.getCurrentFightPanel().equals(this.getContentPane())) {
				this.setContentPane(this.gController.getCurrentFightPanel());
			}
		}
		this.getContentPane().repaint();
	}

	@Override
	public void setContentPane(Container contentPane) {
		if (this.dialogue != null) {
			this.dialogue.setParent((JPanel) contentPane);
		}
		super.setContentPane(contentPane);
	}

	public JPanel getCurrentPanel() {
		return this.currentPanel;
	}

	public void startFight(Pokemon player, Pokemon enemy) {
		this.fight = new FightPanel(player, enemy);
		this.fight.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.fight.setLayout(null);
		this.fighting = true;
		this.pokemon.update();
		if (!this.gController.getFight().canEscape()) {
			this.getFightPanel().addText(
					"Eine Herausforderung von " + this.gController.getFight().getEnemyCharacter().getName() + "!");
		} else {
			this.getFightPanel().addText("Ein wildes " + enemy.getName() + " erscheint!");
		}
		this.gController.updateFight();
	}

	public void stopFight() {
		this.gController.waitDialogue();
		this.setCurrentPanel(null);
	}

	public FightPanel getFightPanel() {
		return this.fight;
	}

	public PokemonPanel getPokemonPanel() {
		return this.pokemon;
	}

	public void addDialogue(String text) {
		if (text != null) {
			this.dialogue.addText(text);
			this.dialogue.setActive();
		}
	}

	public void addDialogue(String text, NPC character) {
		if (text != null) {
			if (character.showName()) {
				text = character.getName() + ": " + text;
			}
			this.dialogue.addText(text, character.getTextColor());
			this.dialogue.setActive();
		}
	}

	public boolean isDialogueEmpty() {
		return this.dialogue.isEmpty();
	}

	public TextLabel getDialogue() {
		return this.dialogue;
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
		if (this.gController.isFighting()) {
			this.gController.getFight().setCurrentFightOption(FightOption.NEW_ATTACK);
		} else {
			this.setCurrentPanel(this.newMove);
		}
	}

	public NewAttackPanel getNewAttackPanel() {
		return this.newMove;
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
		if (this.currentPanel instanceof GeneratorPanel) {
			this.map.add(this.imageHolder);
		}
		if (this.gController.isFighting() && currentPanel instanceof PokemonPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.POKEMON);
		} else if (this.gController.isFighting() && currentPanel instanceof ReportPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.REPORT);
		} else if (this.gController.isFighting() && currentPanel instanceof InventoryPanel) {
			this.gController.getFight().setCurrentFightOption(FightOption.BAG);
		} else if (currentPanel instanceof GeneratorPanel) {
			currentPanel.add(this.imageHolder);
		}
		this.currentPanel = currentPanel;
		this.back = false;
	}

	private void addActions() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!GameFrame.this.gController.isFighting() && !GameFrame.this.gController.getInteractionPause()) {
						if (!GameFrame.this.active) {
							GameFrame.this.active = true;
							GameFrame.this.currentDirection = GameFrame.this.directions.get(0);
							if (GameFrame.this.currentDirection != Direction.NONE) {
								if (!GameFrame.this.gController.move(GameFrame.this.currentDirection)) {
									SoundController.getInstance().playSound(SoundController.BUMP);
									try {
										Thread.sleep(COOLDOWN);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							GameFrame.this.active = false;
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

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "up");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released W"), "stopup");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), "stopup");

		this.map.getActionMap().put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.directions.contains(Direction.UP)) {
					GameFrame.this.directions.add(0, Direction.UP);
				}
			}
		});

		this.map.getActionMap().put("stopup", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameFrame.this.directions.remove(Direction.UP);
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "left");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "left");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released A"), "stopleft");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"),
				"stopleft");

		this.map.getActionMap().put("left", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.directions.contains(Direction.LEFT)) {
					GameFrame.this.directions.add(0, Direction.LEFT);
				}
			}
		});

		this.map.getActionMap().put("stopleft", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameFrame.this.directions.remove(Direction.LEFT);
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "right");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "right");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released D"), "stopright");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"),
				"stopright");

		this.map.getActionMap().put("right", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.directions.contains(Direction.RIGHT)) {
					GameFrame.this.directions.add(0, Direction.RIGHT);
				}
			}
		});

		this.map.getActionMap().put("stopright", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameFrame.this.directions.remove(Direction.RIGHT);
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "down");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released S"), "stopdown");
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"),
				"stopdown");

		this.map.getActionMap().put("down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.directions.contains(Direction.DOWN)) {
					GameFrame.this.directions.add(0, Direction.DOWN);
				}
			}
		});

		this.map.getActionMap().put("stopdown", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameFrame.this.directions.remove(Direction.DOWN);
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "run");
		this.map.getActionMap().put("run", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame.this.gController.getMainCharacter().toggleWalkingSpeed();
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.map.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.FAST);
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							GameFrame.this.gController.checkInteraction();
						}
					}).start();
				}
			}
		});
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		this.map.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		this.pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.pokemon.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.FAST);
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							GameFrame.this.gController.checkInteraction();
						}
					}).start();
				}
			}
		});
		this.pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		this.pokemon.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		this.map.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.isFighting() && GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setActive();
				}
			}
		});

		this.pokemon.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		this.pokemon.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameFrame.this.dialogue.setActive();
			}
		});

		this.evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.evolution.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.FAST);
				}
			}
		});
		this.evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		this.evolution.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		this.evolution.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		this.evolution.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.isFighting() && GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setActive();
				}
			}
		});

		this.inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
		this.inventory.getActionMap().put("space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.FAST);
				}
			}
		});
		this.inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released SPACE"),
				"released space");
		this.inventory.getActionMap().put("released space", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setDelay(TextLabel.SLOW);
				}
			}
		});

		this.inventory.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
		this.inventory.getActionMap().put("enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.isFighting() && GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.dialogue.setActive();
				}
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		this.map.getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.getInteractionPause() && !GameFrame.this.gController.isFighting()) {
					GameFrame.this.gController.returnToMenu();
				}
			}
		});
		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "pokemon");
		this.map.getActionMap().put("pokemon", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.isFighting() && !GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.pokemon.update();
					GameFrame.this.currentPanel = GameFrame.this.pokemon;
				}
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("I"), "inventory");
		this.map.getActionMap().put("inventory", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameFrame.this.gController.isFighting() && !GameFrame.this.gController.getInteractionPause()) {
					GameFrame.this.inventory.update(GameFrame.this.gController.getMainCharacter());
					GameFrame.this.currentPanel = GameFrame.this.inventory;
				}
			}
		});

		this.map.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "flash");
		this.map.getActionMap().put("flash", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (GameFrame.this.gController.getMainCharacter().hasItem(Item.FLASH)
						&& GameFrame.this.gController.getMainCharacter().getCurrentRoute().isDark()) {
					((DarkOverlay) (GameFrame.this.getBackgroundLabel().getOverlay(DarkOverlay.class))).flash();
				}
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
	}

	public JPanel getReportPanel() {
		return this.report;
	}

	public BackgroundLabel getBackgroundLabel() {
		return this.imageHolder;
	}

	public InventoryPanel getInventoryPanel() {
		return this.inventory;
	}

	public EvolutionPanel getEvolutionPanel() {
		return this.evolution;
	}

	public JPanel getLastPanel() {
		return this.getLastPanel(false);
	}

	public JPanel getLastPanel(boolean withEvolution) {
		this.back = true;
		if (this.panelHistory.isEmpty()) {
			return null;
		} else {
			JPanel p = this.panelHistory.pop();
			if (this.currentPanel.getClass().isInstance(p)) {
				return this.getLastPanel(withEvolution);
			}
			if (!withEvolution) {
				if (p instanceof EvolutionPanel) {
					return this.getLastPanel(withEvolution);
				}
			}
			return p;
		}
	}
}

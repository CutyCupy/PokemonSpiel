package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.Attack;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.PokemonButton;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Ailment;

@SuppressWarnings("serial")
public class PokemonPanel extends JPanel {

	private PokemonButton[] pokemonButtons;
	private GameController gController;
	private JButton backButton;
	private int firstOne;

	private Integer currentItem;

	private int index;

	public PokemonPanel() {
		super();
		this.firstOne = -1;
		this.setBounds(0, 0, 630, 630);
		this.pokemonButtons = new PokemonButton[6];
		this.gController = GameController.getInstance();
		for (int i = 0; i < 6; i++) {
			PokemonButton pokemonButton = new PokemonButton(this.gController.getMainCharacter().getTeam().getTeam()[i],
					i);
			pokemonButton.setSize(300, 96);
			if (i % 2 == 0) {
				pokemonButton.setLocation(10, 100 + i * 62);
			} else {
				pokemonButton.setLocation(325, 100 + (i - 1) * 62 + 25);
			}
			pokemonButton.update(false);
			pokemonButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			pokemonButton.setBackground(Color.WHITE);
			pokemonButton.setFocusable(false);
			pokemonButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PokemonButton source = (PokemonButton) e.getSource();
					if (PokemonPanel.this.currentItem == null) {
						int result = 1;
						if (PokemonPanel.this.firstOne == -1) {
							result = JOptionPane.showOptionDialog(null, "Was möchtest du tun?", "Pokemon",
									JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION,
									new ImageIcon(source.getPokemon().getSpriteFront()),
									new String[] { "Bericht", "Tauschen" }, null);
						}
						switch (result) {
						case 0:
							
							if (PokemonPanel.this.gController.isFighting()) {
								PokemonPanel.this.gController.displayReport(source.getPokemon(),
										PokemonPanel.this.gController.getFight().getTeam(Fighting.LEFT_PLAYER).getTeam());
							} else {
								PokemonPanel.this.gController.displayReport(source.getPokemon(),
										PokemonPanel.this.gController.getMainCharacter().getTeam().getTeam());
							}
							break;
						case 1:
							if (PokemonPanel.this.gController.isFighting()) {
								if (source.getIndex() != 0 && source.getIndex() != PokemonPanel.this.gController.getFight().getActivePlayer()) {
									if (PokemonPanel.this.gController.getFight().canBeSendOut(source.getPokemon())) {
										new Thread(new Runnable() {
											@Override
											public void run() {
												PokemonPanel.this.gController.getFight()
												.setCurrentFightOption(FightOption.FIGHT);
												if(gController.getFight().isActiveTurn()) {
													Fighting.waiting = true;
													PokemonPanel.this.gController.getFight().swap(
															gController.getFight().getCurrentPokemon(), source.getPokemon());
//												PokemonPanel.this.gController.getGameFrame().getFightPanel()
//														.checkEnemyAttack();
//												PokemonPanel.this.gController.getGameFrame().getFightPanel().showMenu();
													Fighting.waiting = false;
												} else {
													System.err.println("no active turn");
													gController.getFight().registerAttack(new Attack(gController.getFight().getCurrentPokemon(), source.getPokemon()));
												}
											}
										}).start();
									}
								}
							} else {
								if (source.getIndex() != PokemonPanel.this.firstOne) {
									if (PokemonPanel.this.firstOne != -1) {
										PokemonPanel.this.gController.getMainCharacter().getTeam()
												.swapPokemon(PokemonPanel.this.firstOne, source.getIndex());
										PokemonPanel.this.firstOne = -1;
										PokemonPanel.this.update();
									} else {
										PokemonPanel.this.firstOne = source.getIndex();
									}
								} else {
									PokemonPanel.this.firstOne = -1;
								}
							}
							break;
						}
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								PokemonPanel.this.gController.setInteractionPause(true);
								for (PokemonButton p : PokemonPanel.this.pokemonButtons) {
									p.setEnabled(false);
								}
								PokemonPanel.this.backButton.setEnabled(false);
								boolean result = source.getPokemon().useItem(
										PokemonPanel.this.gController.getMainCharacter(),
										PokemonPanel.this.currentItem);
								source.update(true);
								source.setEnabled(false);
								PokemonPanel.this.gController.waitDialogue();
								if (PokemonPanel.this.gController.isFighting()) {
									PokemonPanel.this.gController.getGameFrame().getFightPanel().updatePanels();
									PokemonPanel.this.gController.getFight().registerAttack(new Attack(PokemonPanel.this.gController.getFight().getCurrentPokemon(), 
											PokemonPanel.this.currentItem, source.getPokemon()));
									PokemonPanel.this.gController.getFight().setCurrentFightOption(FightOption.FIGHT);
								} else {
									if (!PokemonPanel.this.gController.getGameFrame().getEvolutionPanel().getPokemon()
											.isEmpty()) {
										PokemonPanel.this.gController.getGameFrame().setCurrentPanel(
												PokemonPanel.this.gController.getGameFrame().getEvolutionPanel());
										PokemonPanel.this.gController.getGameFrame().getEvolutionPanel().start();
									}
								}
								PokemonPanel.this.gController.getGameFrame().getInventoryPanel()
										.update(PokemonPanel.this.gController.getGameFrame().getInventoryPanel()
												.getCurrentPlayer());
								PokemonPanel.this.update();
								if (PokemonPanel.this.currentItem != null) {
									PokemonPanel.this.gController.getGameFrame().setCurrentPanel(
											PokemonPanel.this.gController.getGameFrame().getPokemonPanel());
								} else {
									PokemonPanel.this.gController.getGameFrame().setCurrentPanel(
											PokemonPanel.this.gController.getGameFrame().getLastPanel(false));
								}
								for (PokemonButton p : PokemonPanel.this.pokemonButtons) {
									if (p.getPokemon() != null) {
										p.setEnabled(true);
									}
								}
								PokemonPanel.this.backButton.setEnabled(true);
								PokemonPanel.this.gController.setInteractionPause(false);

//								if (result && PokemonPanel.this.gController.isFighting()) {
//									PokemonPanel.this.gController.getGameFrame().getFightPanel().checkEnemyAttack();
//									PokemonPanel.this.gController.getGameFrame().getFightPanel().showMenu();
//								}
								gController.getFight().nextPokemon();
								source.repaint();
							}
						}).start();
					}
				}
			});
			this.pokemonButtons[i] = pokemonButton;
			this.add(pokemonButton);
		}
		this.backButton = new JButton("Zurück");
		this.backButton.setBounds(90, 510, 450, 100);
		this.backButton.setFont(new Font(this.backButton.getFont().getFontName(), Font.PLAIN, 30));
		this.backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.backButton.setBackground(Color.WHITE);
		this.backButton.setOpaque(false);
		this.add(this.backButton);

		this.addActionListeners();

		JLabel background = new JLabel(new ImageIcon(Main.class.getResource("/backgrounds/team_pink.png")));
		background.setBounds(this.getBounds());
		this.add(background);

		this.setComponentZOrder(background, this.getComponentCount() - 1);
	}

	private void addActionListeners() {
		this.backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PokemonPanel.this.currentItem = null;
				if (PokemonPanel.this.gController.isFighting()) {
					PokemonPanel.this.gController.getFight().setCurrentFightOption(FightOption.FIGHT);
				} else {
					PokemonPanel.this.gController.getGameFrame()
							.setCurrentPanel(PokemonPanel.this.gController.getGameFrame().getLastPanel());
				}
			}
		});
	}

	public void update() {
		if (this.currentItem != null && this.gController.getGameFrame().getInventoryPanel().getCurrentPlayer()
				.getItems().get(this.currentItem) == 0 && !this.gController.isFighting()) {
			while (!this.getClass().isInstance(this.gController.getGameFrame().getCurrentPanel())) {
				Thread.yield();
			}
			this.gController.getGameFrame()
					.addDialogue("Du hast deinen letzten " + this.gController.getInformation().getItemData(Items.ITEM_NAME, this.currentItem).toString() + " benutzt!");
			this.currentItem = null;
			this.gController.waitDialogue();
		}
		for (PokemonButton pokemonButton : this.pokemonButtons) {
			pokemonButton.update(false);
		}
		if (this.gController.isFighting() && this.gController.getFight().getPokemon(index).getAilment() == Ailment.FAINTED) {
			this.backButton.setEnabled(false);
		} else {
			this.backButton.setEnabled(true);
		}
	}

	public void update(Integer i) {
		this.update();
		this.currentItem = i;
	}

	public void update(int index) {
		this.index = index;
		this.update();
		this.index = 0;
	}

	public int getIndex() {
		return this.index;
	}

}

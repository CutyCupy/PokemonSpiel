package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.gui.PokemonButton;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.Item;

//630 * 630

@SuppressWarnings("serial")
public class PokemonPanel extends JPanel {

	private PokemonButton[] pokemonButtons;
	private GameController gController;
	private JButton backButton;
	private int firstOne;
	
	private Item currentItem;

	public PokemonPanel() {
		super();
		firstOne = -1;
		setBounds(0, 0, 630, 630);
		pokemonButtons = new PokemonButton[6];
		gController = GameController.getInstance();
		for (int i = 0; i < 6; i++) {
			PokemonButton pokemonButton = new PokemonButton(gController.getMainCharacter().getTeam().getTeam()[i], i);
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
					if(currentItem == null) {
						int result = 1;
						if(firstOne == -1) {
							result = JOptionPane.showOptionDialog(null, "Was möchtest du tun?", "Pokemon",
									JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION,
									new ImageIcon(source.getPokemon().getSpriteFront()), new String[] { "Bericht", "Tauschen" },
									null);
						}
						switch (result) {
						case 0:
							gController.displayReport(source.getPokemon());
							break;
						case 1:
							if (gController.isFighting()) {
								if (source.getIndex() != 0) {
									gController.getFight().setCurrentFightOption(FightOption.FIGHT);
									if (gController.getFight().newPlayerPokemon(source.getIndex())) {
										new Thread(new Runnable() {
											@Override
											public void run() {
												gController.repaint();
												gController.getCurrentFightPanel().repaint();
												gController.getGameFrame().getFightPanel().checkEnemyAttack();
												gController.getGameFrame().getFightPanel().showMenu();
											}
										}).start();;
									}
								}
							} else {
								if (source.getIndex() != firstOne) {
									source.setBackground(Color.LIGHT_GRAY);
									if (firstOne != -1) {
										gController.getMainCharacter().getTeam().swapPokemon(firstOne, source.getIndex());
										firstOne = -1;
										update();
									} else {
										firstOne = source.getIndex();
									}
								} else {
									source.setBackground(Color.WHITE);
									firstOne = -1;
								}
							}
							break;
						}
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								gController.setInteractionPause(true);
								for(PokemonButton p : pokemonButtons) {
									p.setEnabled(false);
								}
								backButton.setEnabled(false);
								boolean result = source.getPokemon().useItem(gController.getMainCharacter(), currentItem);
								source.update(true);
								gController.waitDialogue();
								if(gController.isFighting()) {
									gController.getGameFrame().getFightPanel().updatePanels();
									gController.getFight().setCurrentFightOption(FightOption.FIGHT);
								} else {
									if(!gController.getGameFrame().getEvolutionPanel().getPokemon().isEmpty()) {
										gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getEvolutionPanel());
										gController.getGameFrame().repaint();
										gController.getGameFrame().getEvolutionPanel().start();
									}
								}
								gController.getGameFrame().getInventoryPanel().update(gController.getGameFrame().getInventoryPanel().getCurrentPlayer());
								update();
								if(currentItem != null) {
									gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getPokemonPanel());
								} else {
									gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel(false));
								}
								gController.getGameFrame().repaint();
								for(PokemonButton p : pokemonButtons) {
									if(p.getPokemon() != null) {
										p.setEnabled(true);
									}
								}
								backButton.setEnabled(true);
								gController.setInteractionPause(false);
								
								if(result && gController.isFighting()) {
									gController.getGameFrame().getFightPanel().checkEnemyAttack();
									gController.getGameFrame().getFightPanel().showMenu();
								}
								source.repaint();
							}
						}).start();
					}
				}
			});
			pokemonButtons[i] = pokemonButton;
			add(pokemonButton);
		}
		backButton = new JButton("Zurück");
		backButton.setBounds(90, 510, 450, 100);
		backButton.setFont(new Font(backButton.getFont().getFontName(), Font.PLAIN, 30));
		backButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		backButton.setBackground(Color.WHITE);
		add(backButton);
		
		addActionListeners();
	}

	private void addActionListeners() {
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gController.isFighting()) {
					gController.getFight().setCurrentFightOption(FightOption.FIGHT);
				} else {
					gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel());
				}
				gController.getGameFrame().repaint();
			}
		});
	}

	public void update() {
		if(this.currentItem != null && this.gController.getGameFrame().getInventoryPanel().getCurrentPlayer().getItems().get(currentItem) == 0) {
			gController.getGameFrame().addDialogue("Du hast deinen letzten " + this.currentItem.getName() + " benutzt!");
			gController.waitDialogue();
			this.currentItem = null;
		}
		for (int i = 0; i < pokemonButtons.length; i++) {
			pokemonButtons[i].update(false);
		}
		if(gController.isFighting() && gController.getFight().getPlayer().getAilment() == Ailment.FAINTED) {
			this.backButton.setEnabled(false);
		} else {
			this.backButton.setEnabled(true);
		}
	}
	
	public void update(Item i) {
		update();
		this.currentItem = i;
	}

}

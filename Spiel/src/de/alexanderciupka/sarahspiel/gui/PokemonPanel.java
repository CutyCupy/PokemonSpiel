package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.FightOption;

//630 * 630

@SuppressWarnings("serial")
public class PokemonPanel extends JPanel {

	private PokemonButton[] pokemonButtons;
	private GameController gController;
	private JButton backButton;
	private int firstOne;

	public PokemonPanel() {
		super();
		firstOne = -1;
		setBounds(0, 0, 630, 630);
		pokemonButtons = new PokemonButton[6];
		gController = GameController.getInstance();
		for (int i = 0; i < 6; i++) {
			PokemonButton pokemonButton = new PokemonButton(gController.getMainCharacter().getTeam().getTeam()[i], i);
			pokemonButton.setSize(250, 75);
			if (i % 2 == 0) {
				pokemonButton.setLocation(50, 100 + i * 62);
			} else {
				pokemonButton.setLocation(330, 100 + (i - 1) * 62 + 25);
			}
			pokemonButton.update();
			pokemonButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			pokemonButton.setBackground(Color.WHITE);
			pokemonButton.setFocusable(false);
			pokemonButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PokemonButton source = (PokemonButton) e.getSource();
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
									gController.repaint();
									gController.getCurrentFightPanel().repaint();
									gController.getGameFrame().getFightPanel().pause();
									gController.getGameFrame().getFightPanel().checkEnemyAttack();
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
					gController.getGameFrame().setCurrentPanel(null);
					gController.getGameFrame().stopFight();
				}
				gController.repaint();
			}
		});
	}

	public void update() {
		for (int i = 0; i < pokemonButtons.length; i++) {
			pokemonButtons[i].update();
		}
	}

}

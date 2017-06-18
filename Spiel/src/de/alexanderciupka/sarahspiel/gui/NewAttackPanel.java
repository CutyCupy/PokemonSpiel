package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.FightOption;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;

public class NewAttackPanel extends JPanel {

	private GameController gController;
	private JLabel pokemonLabel;
	private Pokemon currentPokemon;
	private JButton[] currentMoves;
	private JButton currentFirstMove;
	private JButton currentSecondMove;
	private JButton currentThirdMove;
	private JButton currentFourthMove;
	private JButton newMove;
	private boolean isActive;
	
	public NewAttackPanel() {
		super();
		setLayout(null);
		setBounds(0, 0, 630, 630);
		gController = GameController.getInstance();
		pokemonLabel = new JLabel();
		pokemonLabel.setBounds(200, 100, 230, 230);
		currentFirstMove = new JButton();
		currentFirstMove.setBounds(110, 470, 200, 40);
		currentFirstMove.setBackground(Color.WHITE);
		currentSecondMove = new JButton();
		currentSecondMove.setBounds(320, 470, 200, 40);
		currentSecondMove.setBackground(Color.WHITE);
		currentThirdMove = new JButton();
		currentThirdMove.setBounds(110, 520, 200, 40);
		currentThirdMove.setBackground(Color.WHITE);
		currentFourthMove = new JButton();
		currentFourthMove.setBounds(320, 520, 200, 40);
		currentFourthMove.setBackground(Color.WHITE);
		newMove = new JButton();
		newMove.setBounds(190, 570, 200, 40);
		newMove.setBackground(Color.WHITE);
		currentMoves = new JButton[]{currentFirstMove, currentSecondMove, currentThirdMove, currentFourthMove};
		add(pokemonLabel);
		add(currentFirstMove);
		add(currentSecondMove);
		add(currentThirdMove);
		add(currentFourthMove);
		add(newMove);
		addActionListener();
	}
	
	private void addActionListener() {
		for (int i = 0; i < currentMoves.length; i++) {
			currentMoves[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!isActive) {
						isActive = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								currentPokemon.addMove(((JButton) e.getSource()).getText(), gController.getInformation().getMoveByName(newMove.getText()));
								gController.getGameFrame().getFightPanel().setPlayer();
								gController.getFight().setCurrentFightOption(FightOption.FIGHT);
								gController.repaint();
								gController.getGameFrame().getFightPanel().addText(currentPokemon.getName() + " hat " + ((JButton) e.getSource()).getText() + " vergessen und " + newMove.getText() + " erlernt!");
							}
						}).start();
						isActive = false;
					}
				}
			});
		}
		newMove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						gController.getFight().setCurrentFightOption(FightOption.FIGHT);
						gController.repaint();
						gController.getGameFrame().getFightPanel().addText(currentPokemon.getName() + " hat " + newMove.getText() + " nicht erlernt!");
					}
				}).start();
			}
		});
	}

	public void update(Pokemon pokemon, Move newMove) {
		currentPokemon = pokemon;
		for (int i = 0; i < currentMoves.length; i++) {
			if (currentPokemon.getMoves()[i] != null) {
				currentMoves[i].setText(currentPokemon.getMoves()[i].getName());
			} else {
				currentMoves[i].setEnabled(false);
			}
		}
		this.newMove.setText(newMove.getName());
		this.pokemonLabel.setIcon(new ImageIcon(pokemon.getSpriteFront().getScaledInstance(230, 230, Image.SCALE_SMOOTH)));
	}
	
}

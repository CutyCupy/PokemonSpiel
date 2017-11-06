package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.gui.AilmentLabel;
import de.alexanderciupka.pokemon.gui.HPBar;
import de.alexanderciupka.pokemon.gui.MoveButton;
import de.alexanderciupka.pokemon.gui.TypeLabel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.painting.Painting;
import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Stats;
import de.alexanderciupka.pokemon.pokemon.Type;

public class NewAttackPanel extends JPanel {


	private JPanel parent;

	private MoveButton newMoveButton;
	private JLabel pokemonLabel;
	private JProgressBar xpBar;
	private JLabel xpLabel;

	private MoveButton[] moves;

	private Pokemon pokemon;
	private Move newMove;

	private JLabel levelLabel;
	private HPBar hpBar;
	private JLabel kpLabel;
	private JLabel idLabel;
	private JLabel nameLabel;

	private JLabel[] stats;
	private TypeLabel firstTypeLabel;
	private TypeLabel secondTypeLabel;
	private TypeLabel moveTypeLabel;
	private JLabel strengthLabel;
	private JLabel accuracyLabel;
	private JLabel damageClassLabel;
	private AilmentLabel ailmentLabel;

	private GameController gController;

	private boolean isActive;

	public NewAttackPanel() {
		gController = GameController.getInstance();
		setBounds(0, 0, 630, 630);
		setLayout(null);

		pokemonLabel = new JLabel();
		pokemonLabel.setBounds(75, 115, 150, 150);
		add(pokemonLabel);

		xpBar = new JProgressBar();
		xpBar.setBounds(50, 300, 200, 14);
		add(xpBar);

		xpLabel = new JLabel("currentXP von nextLevelXP");
		xpLabel.setBounds(50, 315, 200, 14);
		xpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(xpLabel);

		newMoveButton = new MoveButton(false);
		newMoveButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if(gController.isFighting()) {
							gController.getFight().setCurrentFightOption(FightOption.FIGHT);
							gController.getGameFrame().getFightPanel().addText(pokemon.getName() + " hat " + newMove.getName() + " nicht erlernt!");
						} else {
							gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel());
							gController.getGameFrame().addDialogue(pokemon.getName() + " hat " + newMove.getName() + " nicht erlernt!");
							gController.waitDialogue();
						}
					}
				}).start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(e.getComponent().isEnabled()) {
					moveTypeLabel.setType(newMove.getMoveType());
					strengthLabel.setText("Stärke: " + newMove.getPower());
					accuracyLabel.setText("Genauigkeit: " + (newMove.getAccuracy() > 100 ? "---" : (int) newMove.getAccuracy()));

					switch(newMove.getDamageClass()) {
					case PHYSICAL:
						damageClassLabel.setIcon(new ImageIcon(DamageClass.PHYSICAL_IMAGE));
						break;
					case SPECIAL:
						damageClassLabel.setIcon(new ImageIcon(DamageClass.SPECIAL_IMAGE));
						break;
					case NO_DAMAGE:
						damageClassLabel.setIcon(new ImageIcon(DamageClass.NO_DAMAGE_IMAGE));
						break;
					}

					strengthLabel.setVisible(true);
					accuracyLabel.setVisible(true);
					damageClassLabel.setVisible(true);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				moveTypeLabel.setVisible(false);
				strengthLabel.setVisible(false);
				accuracyLabel.setVisible(false);
				damageClassLabel.setVisible(false);
			}
		});
		newMoveButton.setBounds(125, 530, 360, 40);
		newMoveButton.setBackground(Color.WHITE);
		newMoveButton.setVisible(true);
		newMoveButton.setFocusable(false);
		this.add(newMoveButton);

		levelLabel = new JLabel("Level: ");
		levelLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		levelLabel.setBounds(50, 261, 200, 25);
		add(levelLabel);

		hpBar = new HPBar();
		hpBar.setBounds(230, 47, 200, 25);
		add(hpBar);

		kpLabel = new JLabel("999 / 999");
		kpLabel.setBounds(440, 47, 65, 25);
		add(kpLabel);

		nameLabel = new JLabel("Name: ");
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		nameLabel.setBounds(50, 47, 170, 25);
		add(nameLabel);

		idLabel = new JLabel("ID: ");
		idLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		idLabel.setBounds(50, 83, 60, 25);
		add(idLabel);

		firstTypeLabel = new TypeLabel();
		firstTypeLabel.setLocation(120, 83);
		add(firstTypeLabel);

		secondTypeLabel = new TypeLabel();
		secondTypeLabel.setLocation(185, 83);
		add(secondTypeLabel);

		moveTypeLabel = new TypeLabel();
		moveTypeLabel.setType(Type.NORMAL);
		moveTypeLabel.setVisible(false);
		moveTypeLabel.setLocation(265, 350);
		add(moveTypeLabel);

		strengthLabel = new JLabel("Stärke: ");
		strengthLabel.setVisible(false);
		strengthLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		strengthLabel.setBounds(125, 348, 125, 25);
		add(strengthLabel);

		accuracyLabel = new JLabel("Genauigkeit: ");
		accuracyLabel.setVisible(false);
		accuracyLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		accuracyLabel.setBounds(125, 384, 125, 25);
		add(accuracyLabel);

		damageClassLabel = new JLabel("DC");
		damageClassLabel.setVisible(false);
		damageClassLabel.setBounds(265, 386, 60, 25);
		add(damageClassLabel);

		ailmentLabel = new AilmentLabel();
		ailmentLabel.setLocation(515, 52);
		add(ailmentLabel);

		moves = new MoveButton[5];


		for(int i = 0; i < moves.length; i++) {
			MoveButton currentMove = new MoveButton(false);
			currentMove.setBounds(125 + 185 * (i % 2), 430 + 50 * (i / 2), 175, 40);
			currentMove.setName(String.valueOf(i));

			currentMove.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(!isActive) {
						isActive = true;
						new Thread(new Runnable() {
							@Override
							public void run() {
								pokemon.addMove(pokemon.getMoves()[Integer.parseInt(e.getComponent().getName())].getName(), newMove);
								if(gController.isFighting()) {
									gController.getGameFrame().getFightPanel().setPlayer();
									gController.getGameFrame().getFightPanel().updatePanels();
									gController.getFight().setCurrentFightOption(FightOption.FIGHT);
									gController.getGameFrame().getFightPanel().addText(pokemon.getName() + " hat " + ((MoveButton) e.getComponent()).getMove().getName() + " vergessen und " + newMove.getName() + " erlernt!");
								} else {
									gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel(true));
									gController.getGameFrame().addDialogue(
											pokemon.getName() + " hat " + ((MoveButton) e.getComponent()).getMove().getName() +
											" vergessen und " + newMove.getName() + " erlernt!");
									gController.waitDialogue();
								}
							}
						}).start();
						isActive = false;
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					if(e.getComponent().isEnabled()) {
						Move move = pokemon.getMoves()[Integer.parseInt(e.getComponent().getName())];
						moveTypeLabel.setType(move.getMoveType());
						strengthLabel.setText("Stärke: " + move.getPower());
						accuracyLabel.setText("Genauigkeit: " + (move.getAccuracy() > 100 ? "---" : (int) move.getAccuracy()));

						switch(move.getDamageClass()) {
						case PHYSICAL:
							damageClassLabel.setIcon(new ImageIcon(DamageClass.PHYSICAL_IMAGE));
							break;
						case SPECIAL:
							damageClassLabel.setIcon(new ImageIcon(DamageClass.SPECIAL_IMAGE));
							break;
						case NO_DAMAGE:
							damageClassLabel.setIcon(new ImageIcon(DamageClass.NO_DAMAGE_IMAGE));
							break;
						}

						strengthLabel.setVisible(true);
						accuracyLabel.setVisible(true);
						damageClassLabel.setVisible(true);
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					moveTypeLabel.setVisible(false);
					strengthLabel.setVisible(false);
					accuracyLabel.setVisible(false);
					damageClassLabel.setVisible(false);
				}
			});
			this.add(currentMove);
			moves[i] = currentMove;
		}

		stats = new JLabel[5];
		for(int i = 0; i < stats.length; i++) {
			JLabel currentLabel = new JLabel(Stat.values()[i].getText() + ": ");
			currentLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			currentLabel.setBounds(290, 120 + 40 * i, 165, 25);
			add(currentLabel);
			stats[i] = currentLabel;
		}
	}

	public void set(Pokemon p, Move newMove) {
		this.pokemon = p;
		this.newMove = newMove;
		update();
	}

	private void updateMoves() {
		for(int i = 0; i < Math.min(this.pokemon.getMoves().length, this.moves.length); i++) {
			moves[i].setMove(this.pokemon, this.pokemon.getMoves()[i]);
			moves[i].setName(String.valueOf(i));
		}
		newMoveButton.setMove(this.pokemon, newMove);
	}

	private void update() {
		updateMoves();
		Stats stats = this.pokemon.getStats();

		this.pokemonLabel.setIcon(new ImageIcon(Painting.toBufferedImage(this.pokemon.getSpriteFront()).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
		this.nameLabel.setText("Name: " + this.pokemon.getName());
		this.idLabel.setText("ID: " + this.pokemon.getId());
		this.firstTypeLabel.setType(this.pokemon.getTypes()[0]);
		this.secondTypeLabel.setType(this.pokemon.getTypes()[1]);

		this.xpLabel.setText(stats.getCurrentXP() + " / " + stats.getLevelUpXP());
		this.xpBar.setMaximum(stats.getLevelUpXP());
		this.xpBar.setValue(stats.getCurrentXP());
		this.levelLabel.setText("Level: " + stats.getLevel());

		this.kpLabel.setText(stats.getCurrentHP() + " / " + stats.getStats().get(Stat.HP));
		this.hpBar.setMaximum(stats.getStats().get(Stat.HP));
		this.hpBar.setValue(stats.getCurrentHP());
		this.hpBar.setForeground(stats.getHPColor());
		this.ailmentLabel.setAilment(this.pokemon.getAilment());

		for(int i = 0; i < Stat.values().length - 3; i++) {
			this.stats[i].setText(Stat.values()[i].getText() + ": " + stats.getStats().get(Stat.values()[i]));
		}
		setVisible(true);
	}
}




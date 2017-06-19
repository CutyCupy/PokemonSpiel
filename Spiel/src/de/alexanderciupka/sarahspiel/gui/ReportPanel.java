package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.painting.Painting;
import de.alexanderciupka.sarahspiel.pokemon.DamageClass;
import de.alexanderciupka.sarahspiel.pokemon.Move;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;
import de.alexanderciupka.sarahspiel.pokemon.Stats;
import de.alexanderciupka.sarahspiel.pokemon.Type;

public class ReportPanel extends JPanel {

	
	private JPanel parent;
	
	private JButton back;
	private JLabel pokemonLabel;
	private JProgressBar xpBar;
	private JLabel xpLabel;
	
	private MoveButton[] moves;
	
	private Pokemon pokemon;
	
	private int first;
	private int second;
	
	private boolean swapping;
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

	public ReportPanel() {
		setBounds(0, 0, 630, 630);
		setLayout(null);
		
		try {
			pokemonLabel = new JLabel(new ImageIcon(ImageIO.read(new File(this.getClass().getResource("/pokemon/front/1.png").getFile())).getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pokemonLabel.setBounds(75, 115, 150, 150);
		add(pokemonLabel);
		
		xpBar = new JProgressBar();
		xpBar.setBounds(50, 300, 200, 14);
		add(xpBar);
		
		xpLabel = new JLabel("currentXP von nextLevelXP");
		xpLabel.setBounds(50, 315, 200, 14);
		xpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(xpLabel);
		
		back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GameController.getInstance().getGameFrame().setCurrentPanel(parent);
			}
		});
		back.setBounds(125, 530, 360, 40);
		back.setBackground(Color.WHITE);
		back.setVisible(true);
		back.setFocusable(false);
		this.add(back);
		
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
		
		//TODO: Description
		
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
		
		moves = new MoveButton[4];
		
		
		for(int i = 0; i < moves.length; i++) {
			MoveButton currentMove = new MoveButton();
			currentMove.setBounds(125 + 185 * (i % 2), 430 + 50 * (i / 2), 175, 40);
			currentMove.setName(String.valueOf(i));
			
			currentMove.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseClicked(MouseEvent e) {
					JButton source = (JButton) e.getSource();
					if(swapping) {
						second = Integer.parseInt(source.getName());
						pokemon.swapMoves(first, second);
						updateMoves();
						swapping = false;
						mouseEntered(new MouseEvent(moves[second], 0, 0, 0, 0, 0, 0, false));
					} else {
						first = Integer.parseInt(source.getName());
						moves[first].setBackground(Color.LIGHT_GRAY);
						swapping = true;
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
			JLabel currentLabel = new JLabel(Stats.STAT_NAMES[i] + ": ");
			currentLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			currentLabel.setBounds(290, 120 + 40 * i, 165, 25);
			add(currentLabel);
			stats[i] = currentLabel;
		}
	}

	public void setPokemon(Pokemon p, JPanel parent) {
		this.pokemon = p;
		this.parent = parent;
		update();
	}

	private void updateMoves() {
		for(int i = 0; i < Math.min(this.pokemon.getMoves().length, this.moves.length); i++) {
			moves[i].setMove(this.pokemon.getMoves()[i]);
			moves[i].setName(String.valueOf(i));
		}
	}
	
	private void update() {
		updateMoves();
		Stats stats = this.pokemon.getStats();
		
		//Pokemon Data
		this.pokemonLabel.setIcon(new ImageIcon(Painting.toBufferedImage(this.pokemon.getSpriteFront()).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
		this.nameLabel.setText("Name: " + this.pokemon.getName());
		this.idLabel.setText("ID: " + this.pokemon.getId());
		this.firstTypeLabel.setType(this.pokemon.getTypes()[0]);
		this.secondTypeLabel.setType(this.pokemon.getTypes()[1]);
		
		//XP
		this.xpLabel.setText(stats.getCurrentXP() + " / " + stats.getLevelUpXP());
		this.xpBar.setMaximum(stats.getLevelUpXP());
		this.xpBar.setValue(stats.getCurrentXP());
		this.levelLabel.setText("Level: " + stats.getLevel());
		//KP
		this.kpLabel.setText(stats.getCurrentHP() + " / " + stats.getStats()[0]);
		this.hpBar.setMaximum(stats.getStats()[0]);
		this.hpBar.setValue(stats.getCurrentHP());
		this.hpBar.setForeground(stats.getHPColor());
		this.ailmentLabel.setAilment(this.pokemon.getAilment());
		//Stats
		for(int i = 1; i < stats.getStats().length; i++) {
			this.stats[i-1].setText(Stats.STAT_NAMES[i-1] + ": " + stats.getStats()[i]);
		}
		setVisible(true);
	}
}

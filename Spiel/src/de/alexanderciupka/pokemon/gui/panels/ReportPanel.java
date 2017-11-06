package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.gui.AilmentLabel;
import de.alexanderciupka.pokemon.gui.HPBar;
import de.alexanderciupka.pokemon.gui.MoveButton;
import de.alexanderciupka.pokemon.gui.TypeLabel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.painting.Painting;
import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Nature;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Stats;
import de.alexanderciupka.pokemon.pokemon.Type;

public class ReportPanel extends JPanel {

	private JPanel parent;

	private JButton back;
	private JLabel pokemonLabel;
	private JProgressBar xpBar;
	private JLabel xpLabel;

	private MoveButton[] moves;

	private Pokemon pokemon;
	private Pokemon[] others;

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
	private JLabel descriptionLabel;
	private JLabel genderLabel;
	private AilmentLabel ailmentLabel;

	private JButton previousPokemonB;
	private JButton nextPokemonB;

	private int currentPokemon;

	private final static javax.swing.border.Border DEFAULT_BORDER = new CompoundBorder(
			new LineBorder(new Color(0, 0, 0), 1, true), new EmptyBorder(0, 5, 0, 0));

	private GameController gController;

	public ReportPanel() {
		setBounds(0, 0, 630, 630);
		setLayout(null);

		gController = GameController.getInstance();

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

		back = new JButton("Zurück");
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (gController.isFighting()) {
					gController.getGameFrame().setCurrentPanel(parent);
				} else {
					gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel());
				}
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
		nameLabel.setBorder(DEFAULT_BORDER);
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		nameLabel.setBounds(50, 47, 170, 25);
		add(nameLabel);

		idLabel = new JLabel("ID: ");
		idLabel.setBorder(DEFAULT_BORDER);
		idLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		idLabel.setBounds(50, 83, 60, 25);
		add(idLabel);

		genderLabel = new JLabel();
		genderLabel.setBorder(DEFAULT_BORDER);
		genderLabel.setBounds(120, 83, 25, 25);
		add(genderLabel);

		firstTypeLabel = new TypeLabel();
		firstTypeLabel.setLocation(155, 83);
		add(firstTypeLabel);

		secondTypeLabel = new TypeLabel();
		secondTypeLabel.setLocation(220, 83);
		add(secondTypeLabel);

		moveTypeLabel = new TypeLabel();
		moveTypeLabel.setType(Type.NORMAL);
		moveTypeLabel.setVisible(false);
		moveTypeLabel.setLocation(215, 350);
		add(moveTypeLabel);

		strengthLabel = new JLabel("Stärke: ");
		strengthLabel.setBorder(DEFAULT_BORDER);
		strengthLabel.setVisible(false);
		strengthLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		strengthLabel.setBounds(75, 348, 125, 25);
		add(strengthLabel);

		accuracyLabel = new JLabel("Genauigkeit: ");
		accuracyLabel.setBorder(DEFAULT_BORDER);
		accuracyLabel.setVisible(false);
		accuracyLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		accuracyLabel.setBounds(75, 384, 125, 25);
		add(accuracyLabel);

		damageClassLabel = new JLabel("DC");
		damageClassLabel.setVisible(false);
		damageClassLabel.setBounds(215, 386, 60, 25);
		add(damageClassLabel);

		ailmentLabel = new AilmentLabel();
		ailmentLabel.setLocation(515, 52);
		add(ailmentLabel);

		descriptionLabel = new JLabel("Description");
		descriptionLabel.setBorder(DEFAULT_BORDER);
		descriptionLabel.setVisible(false);
		descriptionLabel.setBounds(290, 350, 245, 59);
		add(descriptionLabel);

		moves = new MoveButton[4];

		for (int i = 0; i < moves.length; i++) {
			MoveButton currentMove = new MoveButton(false);
			currentMove.setBounds(125 + 185 * (i % 2), 430 + 50 * (i / 2), 175, 40);
			currentMove.setName(String.valueOf(i));

			currentMove.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					JButton source = (JButton) e.getSource();
					if (swapping) {
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
					if (e.getComponent().isEnabled()) {
						Move move = pokemon.getMoves()[Integer.parseInt(e.getComponent().getName())];
						moveTypeLabel.setType(move.getMoveType());
						strengthLabel.setText("Stärke: " + (move.getPower() <= 0 ? "---" : move.getPower()));
						accuracyLabel.setText(
								"Genauigkeit: " + (move.getAccuracy() > 100 ? "---" : (int) move.getAccuracy()));

						switch (move.getDamageClass()) {
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

						descriptionLabel.setText(formatText((int) (descriptionLabel.getWidth() * 0.9),
								move.getDescription(), getFontMetrics(descriptionLabel.getFont()), 3));

						descriptionLabel.setVisible(true);
						strengthLabel.setVisible(true);
						accuracyLabel.setVisible(true);
						damageClassLabel.setVisible(true);
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					descriptionLabel.setVisible(false);
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
		for (int i = 0; i < stats.length; i++) {
			JLabel currentLabel = new JLabel(Stat.values()[i].getText() + ": ");
			currentLabel.setOpaque(false);
			currentLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			currentLabel.setBounds(290, 120 + 40 * i, 165, 25);
			currentLabel.setBorder(DEFAULT_BORDER);
			add(currentLabel);
			stats[i] = currentLabel;
		}

		previousPokemonB = new JButton();
		previousPokemonB.setBounds(480, 130, 125, 70);
		previousPokemonB.setBorder(DEFAULT_BORDER);
		previousPokemonB.setBackground(new Color(238, 238, 238));
		this.add(previousPokemonB);

		nextPokemonB = new JButton();
		nextPokemonB.setBounds(480, 220, 125, 70);
		nextPokemonB.setBorder(DEFAULT_BORDER);
		nextPokemonB.setBackground(this.getBackground());
		this.add(nextPokemonB);

		AbstractAction previousPokemonAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (previousPokemonB.isEnabled()) {
					currentPokemon = Integer.parseInt(previousPokemonB.getName());
					setPokemon(others[currentPokemon], others, parent);
				}
			}
		};

		AbstractAction nextPokemonAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (nextPokemonB.isEnabled()) {
					currentPokemon = Integer.parseInt(nextPokemonB.getName());
					setPokemon(others[currentPokemon], others, parent);
				}
			}
		};

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "previous");
		getActionMap().put("previous", previousPokemonAction);
		previousPokemonB.addActionListener(previousPokemonAction);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "next");
		getActionMap().put("next", nextPokemonAction);
		nextPokemonB.addActionListener(nextPokemonAction);

		for (Component c : this.getComponents()) {
			c.setBackground(Color.WHITE);
			if (c instanceof JLabel) {
				JLabel current = (JLabel) c;
				if (DEFAULT_BORDER.equals(current.getBorder())) {
					current.setOpaque(true);
				}
			}
		}

		JLabel background = new JLabel(new ImageIcon(Main.class.getResource("/backgrounds/stat.png")));
		background.setBounds(getBounds());
		this.add(background);

		this.setComponentZOrder(background, getComponentCount() - 1);

	}

	public void setPokemon(Pokemon p, Pokemon[] others, JPanel parent) {
		this.pokemon = p;
		this.others = others;
		this.currentPokemon = getIndex();
		this.parent = parent;
		update();
		SoundController.getInstance().playBattlecry(p.getId(), false);
	}

	private int getIndex() {
		if (this.pokemon == null)
			return 0;
		for (int i = 0; i < others.length; i++) {
			if (this.pokemon.equals(others[i])) {
				return i;
			}
		}
		return 0;
	}

	private void updateMoves() {
		for (int i = 0; i < Math.min(this.pokemon.getMoves().length, this.moves.length); i++) {
			moves[i].setMove(this.pokemon, this.pokemon.getMoves()[i]);
			moves[i].setName(String.valueOf(i));
		}
	}

	private void update() {
		updateMoves();
		Stats stats = this.pokemon.getStats();

		this.pokemonLabel.setIcon(new ImageIcon(Painting.toBufferedImage(this.pokemon.getSpriteFront())
				.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
		this.nameLabel.setText("Name: " + this.pokemon.getName());
		this.idLabel.setText("ID: " + this.pokemon.getId());
		Image img = gController.getInformation().getGenderImage(this.pokemon.getGender());
		if (img != null) {
			img = img.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		}
		this.genderLabel.setIcon(new ImageIcon(img));
		this.firstTypeLabel.setType(this.pokemon.getTypes()[0]);
		this.secondTypeLabel.setType(this.pokemon.getTypes()[1]);

		this.xpLabel.setText(stats.getCurrentXP() + " / " + stats.getLevelUpXP());
		this.xpBar.setMaximum(stats.getLevelUpXP());
		this.xpBar.setValue(stats.getCurrentXP());
		this.levelLabel.setText("Level: " + stats.getLevel());

		this.kpLabel.setText(stats.getCurrentHP() + " / " + stats.getStats().get(Stat.HP));
		this.hpBar.setMaximum(stats.getStats().get(Stat.HP));
		this.hpBar.setValue(stats.getCurrentHP());
		this.ailmentLabel.setAilment(this.pokemon.getAilment());
		
		for (int i = 0; i < this.stats.length; i++) {
			this.stats[i].setText(Stat.values()[i].getText() + ": " + stats.getStats().get(Stat.values()[i]));
			if (Stat.values()[i].equals(stats.getNature().getIncrease())) {
				this.stats[i].setForeground(Nature.INCREASE_COLOR);
			} else if (Stat.values()[i].equals(stats.getNature().getDecrease())) {
				this.stats[i].setForeground(Nature.DECREASE_COLOR);
			} else {
				this.stats[i].setForeground(Color.BLACK);
			}
		}
		setVisible(true);

		Pokemon p = null;
		for (int i = currentPokemon - 1; i >= 0; i--) {
			p = others[i];
			if (p != null) {
				previousPokemonB.setName(String.valueOf(i));
				break;
			}
		}
		if (p != null) {
			previousPokemonB.setIcon(new ImageIcon(p.getSpriteFront().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
		} else {
			previousPokemonB.setIcon(null);
		}
		previousPokemonB.setEnabled(p != null);
		previousPokemonB.setBackground(p != null ? Color.WHITE : Color.WHITE.darker());
		p = null;
		for (int i = currentPokemon + 1; i < others.length; i++) {
			p = others[i];
			if (p != null) {
				nextPokemonB.setName(String.valueOf(i));
				break;
			}
		}
		nextPokemonB.setEnabled(p != null);
		nextPokemonB.setBackground(p != null ? Color.WHITE : Color.WHITE.darker());
		if (p != null) {
			nextPokemonB.setIcon(new ImageIcon(p.getSpriteFront().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));
		} else {
			nextPokemonB.setIcon(null);
		}
	}

	public String formatText(int width, String text, FontMetrics fm, int maxRows) {
		ArrayList<String> rows = new ArrayList<String>();
		String currentRow = "";
		for (String s : text.split(" ")) {
			if (fm.stringWidth(currentRow + " " + s) > width) {
				rows.add(currentRow);
				currentRow = s;
			} else {
				currentRow += " " + s;
			}
		}
		if (!currentRow.isEmpty()) {
			rows.add(currentRow);
		}
		if (rows.size() > maxRows) {
			this.descriptionLabel.setFont(
					this.descriptionLabel.getFont().deriveFont(this.descriptionLabel.getFont().getSize() - 1.0f));
			return formatText(width, text, getFontMetrics(this.descriptionLabel.getFont()), maxRows);
		}
		String result = "<html>";
		for (String s : rows) {
			result += s + "<br>";
		}
		return result;
	}
}

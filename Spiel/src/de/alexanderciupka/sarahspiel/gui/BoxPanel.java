package de.alexanderciupka.sarahspiel.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.pokemon.Box;
import de.alexanderciupka.sarahspiel.pokemon.Pokemon;
import de.alexanderciupka.sarahspiel.pokemon.Team;

public class BoxPanel extends JPanel {

	private Box box;
	private JButton left;
	private JButton right;
	private JLabel boxName;

	private BufferedImage rArrow;
	private BufferedImage lArrow;

	private PCPanel parent;

	private JLabel selected;
	private Box selectedBox;

	private static final int SIZE = 90;

	private static final Color teamColor = new Color(153, 255, 255);
	private static final Color boxColor = new Color(153, 255, 153);

	private Box next;
	private Box before;

	public BoxPanel(PCPanel parent) {
		this.setLayout(null);
		this.setBounds(0, 0, 630, 630);
		this.parent = parent;
		try {
			rArrow = ImageIO.read(new File(this.getClass().getResource("/icons/right.png").getFile()));
			lArrow = ImageIO.read(new File(this.getClass().getResource("/icons/left.png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.left = new JButton(new ImageIcon(lArrow.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
		this.left.setBounds(0, 0, SIZE, SIZE);

		this.right = new JButton(new ImageIcon(rArrow.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
		this.right.setBounds(540, 0, SIZE, SIZE);
		this.boxName = new JLabel();
		this.boxName.setBounds(100, 0, 430, SIZE);
		this.boxName.setBackground(Color.WHITE);
		this.boxName.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));

		this.left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.setBox(before);
			}
		});

		this.right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.setBox(next);
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
		this.getActionMap().put("escape", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (parent.getPC().getOwner().getTeam().getAmmount() > 0) {
					GameController.getInstance().getGameFrame().setCurrentPanel(null);
				} else {
					JOptionPane.showMessageDialog(GameController.getInstance().getGameFrame(),
							"Du brauchst mindestens ein Pokemon im Team!", "Team", JOptionPane.ERROR_MESSAGE, null);
				}
			}
		});
	}

	public void setBox(Box box) {
		for (Component c : this.getComponents()) {
			this.remove(c);
		}

		this.next = box.getNext();
		this.before = box.getBefore();

		this.add(left);
		this.add(right);
		this.add(boxName);

		boxName.setText(box.getName());

		Pokemon[] team = box.getPc().getOwner().getTeam().getTeam();
		Pokemon[] boxP = box.getPokemons();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				JLabel currentPokemon = new JLabel();
				currentPokemon.setName(j == 0 ? "t" + String.valueOf(i) : "b" + String.valueOf(i * 6 + (j - 1)));
				currentPokemon.setBackground(j == 0 ? teamColor : boxColor);
				if (j == 0 && team[i] != null) {
					currentPokemon.setIcon(
							new ImageIcon(team[i].getSpriteFront().getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
				} else if (j != 0 && boxP[i * 6 + (j - 1)] != null) {
					currentPokemon.setIcon(new ImageIcon(
							boxP[i * 6 + (j - 1)].getSpriteFront().getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
				}
				currentPokemon.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (selected == null) {
							selectedBox = box;
							selected = (JLabel) e.getComponent();
							selected.setBorder(BorderFactory.createLineBorder(Color.black, 1, true));
						} else {
							JLabel second = (JLabel) e.getComponent();
							boolean firstTeam = selected.getName().startsWith("t");
							boolean secondTeam = second.getName().startsWith("t");
							int firstIndex = Integer.parseInt(selected.getName().substring(1));
							int secondIndex = Integer.parseInt(second.getName().substring(1));

							Team t = box.getPc().getOwner().getTeam();

							if (firstTeam && secondTeam) {
								t.swapPokemon(firstIndex, secondIndex);
							} else if (firstTeam ^ secondTeam) {
								if (firstTeam) {
									t.replacePokemon(firstIndex,
											box.replacePokemon(secondIndex, t.getTeam()[firstIndex]));
								} else {
									t.replacePokemon(secondIndex,
											selectedBox.replacePokemon(firstIndex, t.getTeam()[secondIndex]));
								}
							} else {
								box.getPc().swap(selectedBox, firstIndex, box, secondIndex);
							}

							selected.setBorder(BorderFactory.createEmptyBorder());
							selected = null;
							selectedBox = null;
							parent.setBox(box);
						}
					}

					@Override
					public void mouseEntered(MouseEvent e) {
						e.getComponent().setBackground(e.getComponent().getBackground().darker());
					}

					@Override
					public void mouseExited(MouseEvent e) {
						e.getComponent()
								.setBackground(e.getComponent().getName().startsWith("t") ? teamColor : boxColor);
					}
				});
				currentPokemon.setBounds(SIZE * j, SIZE + SIZE * i, SIZE, SIZE);
				currentPokemon.setOpaque(true);
				this.add(currentPokemon);
			}
		}
	}

	public Box getBox() {
		return this.box;
	}
}

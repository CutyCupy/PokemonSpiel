package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.map.GameController;

public class InventoryPanel extends JPanel {

	private HashMap<Integer, JLabel> itemNameLabels;
	private HashMap<Integer, JLabel> itemAmountLabels;

	private JButton backB;
	private JButton pokemonB;
	private JLabel spriteL;
	private JLabel descriptionL;
	private JScrollPane itemSP;
	private JPanel panel;

	private Player currentPlayer;

	private GameController gController;

	private static final Font FONT = new Font("Monospace", Font.PLAIN, 35);
	private static final Color LABEL_BACKGROUND = new Color(-1118482);
	private static final Color HOVER_BACKGROUND = LABEL_BACKGROUND.darker();

	private HashMap<Integer, ImageIcon> itemSprites;

	public InventoryPanel() {

		this.gController = GameController.getInstance();

		this.itemNameLabels = new HashMap<>();
		this.itemAmountLabels = new HashMap<>();

		this.setBounds(0, 0, 630, 630);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(null);

		this.spriteL = new JLabel(" ");
		this.spriteL.setBounds(10, 11, 200, 200);
		this.add(this.spriteL);

		this.descriptionL = new JLabel(" ");
		this.descriptionL.setBounds(10, 222, 200, 200);
		this.add(this.descriptionL);

		this.pokemonB = new JButton("Pokemon");
		this.pokemonB.setFont(FONT);
		this.pokemonB.setBounds(10, 473, 200, 48);
		this.pokemonB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.pokemonB.setBackground(Color.WHITE);
		this.pokemonB.setFocusable(false);

		this.pokemonB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				InventoryPanel.this.gController.getGameFrame().getPokemonPanel().update();
				InventoryPanel.this.gController.getGameFrame()
						.setCurrentPanel(InventoryPanel.this.gController.getGameFrame().getPokemonPanel());
				e.getComponent().setBackground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton source = (JButton) e.getComponent();
				source.setBackground(Color.WHITE);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JButton source = (JButton) e.getComponent();
				source.setBackground(Color.WHITE.darker());
			}

		});

		this.add(this.pokemonB);

		this.backB = new JButton("Zurück");
		this.backB.setFont(FONT);
		this.backB.setBounds(10, 532, 200, 48);
		this.backB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.backB.setBackground(Color.WHITE);
		this.backB.setFocusable(false);

		this.backB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (InventoryPanel.this.gController.isFighting()) {
					InventoryPanel.this.gController.getFight().setCurrentFightOption(FightOption.FIGHT);
				} else {
					InventoryPanel.this.gController.getGameFrame()
							.setCurrentPanel(InventoryPanel.this.gController.getGameFrame().getLastPanel());
				}
				e.getComponent().setBackground(Color.WHITE);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JButton source = (JButton) e.getComponent();
				source.setBackground(Color.WHITE);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				JButton source = (JButton) e.getComponent();
				source.setBackground(Color.WHITE.darker());
			}

		});

		this.add(this.backB);

		this.itemSP = new JScrollPane();
		this.itemSP.setBounds(220, 11, 384, 569);
		this.add(this.itemSP);

		this.panel = new JPanel();
		this.itemSP.setViewportView(this.panel);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 250, 50 };
		this.panel.setLayout(layout);

		this.itemSP.getVerticalScrollBar().setUnitIncrement(10);

		this.readSprites();
	}

	private void readSprites() {
		this.itemSprites = new HashMap<>();
		for (int i = 1; i < 682; i++) {
			try {
				Image sprite = this.gController.getRouteAnalyzer().getItemImage(i);
				this.itemSprites.put(i, new ImageIcon(sprite.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
			} catch (Exception e) {
			}
		}
	}

	public void update(Player character) {
		this.currentPlayer = character;
		this.updateItems();
	}

	private void updateItems() {
		this.spriteL.setIcon(null);
		this.descriptionL.setText(" ");

		this.spriteL.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		this.descriptionL.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		
		this.descriptionL.setHorizontalAlignment(SwingConstants.CENTER);
		
		if (this.currentPlayer != null) {
			GridBagConstraints gbc = new GridBagConstraints();

			HashMap<Integer, Integer> currentItems = this.currentPlayer.getItems();

			this.panel.removeAll();
			this.itemNameLabels.clear();
			this.itemAmountLabels.clear();

			int row = 0;

			for (Integer i : currentItems.keySet()) {
				int amount = currentItems.get(i);
				if (amount > 0) {
					while (amount > 0) {
						JLabel itemL = new JLabel(
								String.valueOf(this.gController.getInformation().getItemData(Items.ITEM_NAME, i))
										.replace("\"", "").replace("\\n", " "));
						itemL.setOpaque(true);
						itemL.setFont(FONT);

						itemL.setName(String.valueOf(i));

						gbc.gridx = 0;
						gbc.gridy = row;

						gbc.fill = GridBagConstraints.BOTH;
						gbc.weightx = 0.5;
						gbc.weighty = 0;

						itemL.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseExited(MouseEvent e) {
								JLabel source = (JLabel) e.getComponent();
								source.setBackground(LABEL_BACKGROUND);
								// InventoryPanel.this.descriptionL.setText("");
								// InventoryPanel.this.spriteL.setIcon(null);
							}

							@Override
							public void mouseEntered(MouseEvent e) {
								for(Component c : InventoryPanel.this.panel.getComponents()) {
									c.setBackground(LABEL_BACKGROUND);
								}
								JLabel source = (JLabel) e.getComponent();
								source.setBackground(HOVER_BACKGROUND);
								Integer item = Integer.parseInt(source.getName());
								InventoryPanel.this.descriptionL.setFont(FONT.deriveFont(20f));
								InventoryPanel.this.descriptionL.setText(InventoryPanel.this.formatText(
										InventoryPanel.this.descriptionL.getWidth(),
										GameController.getInstance().getInformation().getItemData(Items.ITEM_DESC, item)
												.toString().replace("\"", "").replace("\\n", " "),
										InventoryPanel.this.getFontMetrics(InventoryPanel.this.descriptionL.getFont()),
										5));
								InventoryPanel.this.spriteL.setIcon(InventoryPanel.this.itemSprites.get(item));
							}

							@Override
							public void mouseClicked(MouseEvent e) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										// TODO: Item usage
										// JLabel source = (JLabel)
										// e.getComponent();
										// Item i =
										// Item.getItemByName(source.getText());
										// if (i.isUsableOnPokemon()) {
										// InventoryPanel.this.gController.getGameFrame().getPokemonPanel().update(i);
										// InventoryPanel.this.gController.getGameFrame().setCurrentPanel(
										// InventoryPanel.this.gController.getGameFrame().getPokemonPanel());
										// } else {
										// if
										// (InventoryPanel.this.currentPlayer.useItem(i))
										// {
										// if
										// (InventoryPanel.this.gController.isFighting())
										// {
										// InventoryPanel.this.gController.getFight()
										// .setCurrentFightOption(FightOption.FIGHT);
										// } else {
										// InventoryPanel.this.gController.getGameFrame()
										// .setCurrentPanel(null);
										// }
										// }
										// }
										// source.setBackground(LABEL_BACKGROUND);
									}
								}).start();
							}
						});

						if (spriteL.getIcon() == null) {
							itemL.getMouseListeners()[0].mouseEntered(new MouseEvent(itemL, 0, 0, 0, 0, 0, 0, false));
						}

						this.panel.add(itemL, gbc);
						JLabel amountL = new JLabel("x" + (amount > 99 ? 99 : amount));
						amount -= 99;
						amountL.setFont(FONT);

						amountL.setName(String.valueOf(row));

						gbc.gridx = 1;
						gbc.gridy = row;

						gbc.fill = GridBagConstraints.BOTH;
						gbc.weightx = 0.5;
						gbc.weighty = 0;

						this.panel.add(amountL, gbc);

						this.itemNameLabels.put(row, itemL);
						this.itemAmountLabels.put(row, amountL);

						row++;
					}
				}
			}

			this.itemSP.setBorder(null);

			this.itemSP.setSize(this.itemSP.getWidth(), Math.min(row * 50 + 5, 569));
			this.itemSP.repaint();
			this.repaint();
		}
	}

	public String formatText(int width, String text, FontMetrics fm, int maxRows) {
		ArrayList<String> rows = new ArrayList<String>();
		String currentRow = "";
		System.out.println(text);
		for (String s : text.split(" ")) {
			if (fm.stringWidth(currentRow + " " + s) > width - 10) {
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
			this.descriptionL
					.setFont(this.descriptionL.getFont().deriveFont(this.descriptionL.getFont().getSize() - 1.0f));
			return this.formatText(width, text, this.getFontMetrics(this.descriptionL.getFont()), maxRows);
		}
		String result = "<html>";
		for (String s : rows) {
			System.out.println(s);
			result += s + " <br>";
		}
		return result;
	}

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}
}

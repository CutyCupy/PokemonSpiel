package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.border.EmptyBorder;

import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.fighting.FightOption;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Item;

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
	
	private HashMap<Item, ImageIcon> itemSprites;   

	public InventoryPanel() {
		
		gController = GameController.getInstance();

		itemNameLabels = new HashMap<>();
		itemAmountLabels = new HashMap<>();

		setBounds(0, 0, 630, 630);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);

		spriteL = new JLabel(" ");
		spriteL.setBounds(10, 11, 200, 200);
		add(spriteL);

		descriptionL = new JLabel(" ");
		descriptionL.setBounds(10, 222, 200, 200);
		add(descriptionL);

		pokemonB = new JButton("Pokemon");
		pokemonB.setFont(FONT);
		pokemonB.setBounds(10, 473, 200, 48);
		pokemonB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pokemonB.setBackground(Color.WHITE);
		pokemonB.setFocusable(false);
		
		pokemonB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				gController.getGameFrame().getPokemonPanel().update();
				gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getPokemonPanel());
				gController.getGameFrame().repaint();
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
		
		add(pokemonB);

		backB = new JButton("Zurück");
		backB.setFont(FONT);
		backB.setBounds(10, 532, 200, 48);
		backB.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		backB.setBackground(Color.WHITE);
		backB.setFocusable(false);
		
		backB.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(gController.isFighting()) {
					gController.getFight().setCurrentFightOption(FightOption.FIGHT);
				} else {
					gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel());
				}
				e.getComponent().setBackground(Color.WHITE);
				gController.getGameFrame().repaint();
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
			
		
		add(backB);

		itemSP = new JScrollPane();
		itemSP.setBounds(220, 11, 384, 569);
		add(itemSP);

		panel = new JPanel();
		itemSP.setViewportView(panel);
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] { 250, 50 };
		panel.setLayout(layout);

		itemSP.getVerticalScrollBar().setUnitIncrement(10);
		
		readSprites();
	}
	
	private void readSprites() {
		this.itemSprites = new HashMap<>(Item.values().length);
		for(Item i : Item.values()) {
			try {
				this.itemSprites.put(i, new ImageIcon(gController.getRouteAnalyzer().getItemImage(i)));
			} catch(Exception e) {}
		}	
 	}

	public void update(Player p) {
		this.currentPlayer = p;
		updateItems();
	}

	private void updateItems() {
		spriteL.setIcon(null);
		descriptionL.setText(" ");
		if (this.currentPlayer != null) {
			GridBagConstraints gbc = new GridBagConstraints();

			HashMap<Item, Integer> currentItems = this.currentPlayer.getItems();

			panel.removeAll();
			itemNameLabels.clear();
			itemAmountLabels.clear();

			int row = 0;

			for (Item i : currentItems.keySet()) {
				int amount = currentItems.get(i);
				if (i != Item.NONE && amount > 0) {
					while(amount > 0) {
						JLabel itemL = new JLabel(i.getName());
						itemL.setOpaque(true);
						itemL.setFont(FONT);
						
						itemL.setName(String.valueOf(row));
						
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
								descriptionL.setText("");
								spriteL.setIcon(null);
							}
							
							@Override
							public void mouseEntered(MouseEvent e) {
								JLabel source = (JLabel) e.getComponent();
								source.setBackground(HOVER_BACKGROUND);
								Item item = Item.getItemByName(source.getText());
								descriptionL.setFont(FONT.deriveFont(20f));
								descriptionL.setText(formatText(descriptionL.getWidth(), item.getDescription(),
										getFontMetrics(descriptionL.getFont()), 5));
								spriteL.setIcon(itemSprites.get(item));
							}
							
							@Override
							public void mouseClicked(MouseEvent e) {
								new Thread(new Runnable() {
									@Override
									public void run() {
										JLabel source = (JLabel) e.getComponent();
										Item i = Item.getItemByName(source.getText());
										if(i.isUsableOnPokemon()) {
											gController.getGameFrame().getPokemonPanel().update(i);
											gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getPokemonPanel());
										} else {
											if(currentPlayer.useItem(i)) {
												if(gController.isFighting()) {
													gController.getFight().setCurrentFightOption(FightOption.FIGHT);
												} else {
													gController.getGameFrame().setCurrentPanel(null);
												}
											}
										}
										source.setBackground(LABEL_BACKGROUND);
										gController.getGameFrame().repaint();
									}
								}).start();
							}
						});
						
						panel.add(itemL, gbc);
						JLabel amountL = new JLabel("x" + (amount > 99 ? 99 : amount));
						amount -= 99;
						amountL.setFont(FONT);
						
						amountL.setName(String.valueOf(row));
						
						gbc.gridx = 1;
						gbc.gridy = row;
						
						gbc.fill = GridBagConstraints.BOTH;
						gbc.weightx = 0.5;
						gbc.weighty = 0;
						
						panel.add(amountL, gbc);
						
						itemNameLabels.put(row, itemL);
						itemAmountLabels.put(row, amountL);
						
						row++;
					}
				}
			}
			
			itemSP.setBorder(null);
			
			itemSP.setSize(itemSP.getWidth(), Math.min(row * 50 + 5, 569));
			itemSP.repaint();
			this.repaint();
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
			this.descriptionL
					.setFont(this.descriptionL.getFont().deriveFont(this.descriptionL.getFont().getSize() - 1.0f));
			return formatText(width, text, getFontMetrics(this.descriptionL.getFont()), maxRows);
		}
		String result = "<html>";
		for (String s : rows) {
			result += s + "<br>";
		}
		return result;
	}

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}
}

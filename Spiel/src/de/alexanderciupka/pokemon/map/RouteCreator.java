package de.alexanderciupka.pokemon.map;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RouteCreator extends JFrame {

	private JPanel contentPane;
	private JLabel[][] labels;
	private JButton[] vertical;
	private JButton[] horizontal;
	private int index;
	private int x;
	private int y;
	private JRadioButton[] buttons;
	private ButtonGroup group;
	private int warpCounter;
	private int characterCounter;
	private ArrayList<Warp> warps;
	private ArrayList<String> characters;
	private String routeName;
	private String routeID;
	private static Scanner scanner;
	private JButton save;
	private JComboBox<String> terrains;
	private boolean active;

	private RouteAnalyzer routeAnalyzer;

	private static final String[][] TYPES = {{"Out of Bounds", "OOB"}, {"Bett", "bed"}, {"Treppe hoch links", "WSUL"}, {"Treppe hoch rechts", "WSUR"}, {"Treppe runter links", "WSDL"}, {"Treppe runter rechts", "WSDR"}, {"Stärke", "ST"}, {"Zerschneider", "TC"}, {"Wand", "MW"}, {"Fenster", "MWW"}, {"Fenster+Gardine", "MWWC"}, {"Free", ""}, {"Tree", "T"}, {"Bookshelf", "BS"}, {"TV", "TV"}, {"SPÜLE", "spuele"}, {"Laptop", "LAPTOP"}, {"Table", "TA"}, {"Chair UP", "STUHLU"}, {"Chair DOWN", "STUHLD"}, {"Chair LEFT", "STUHLL"}, {"Chair RIGHT", "STUHLR"}, {"Settle UP", "SETTLEU"}, {"Settle DOWN", "SETTLED"}, {"Settle LEFT", "SETTLEL"}, {"Settle RIGHT", "SETTLER"}, {"Grass", "G"}, {"Grassy", "GR"}, {"Mauer", "M"}, {"Pokemon Center", "P"}, {"House Small", "HS"}, {"Gym", "A"}, {"Warp", "W"}, {"Character", "C"}, {"See","S"}, {"Sand", "SA"}, {"Bridge", "B"}, {"PC", "BC"}, {"JoyHealing", "JH"}, {"MoveDown", "MD"}, {"MoveUp", "MU"}, {"MoveLeft", "ML"}, {"MoveRight", "MR"}, {"MoveStop", "MS"}, {"RockBig", "RB"}, {"RockGroup", "RG"}, {"Rock", "R"}};
	private static final int SIZE = 25;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					scanner = new Scanner(System.in);
					System.out.print("Breite (max: 65): ");
					int width = Integer.parseInt(scanner.nextLine());
					System.out.print("Hï¿½he (max: 40): ");
					int height = Integer.parseInt(scanner.nextLine());
					System.out.print("Name der Route: ");
					String name = scanner.nextLine();
					System.out.println(name);
					RouteCreator frame = new RouteCreator(width, height, name);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RouteCreator(int width, int height, String name) {
		routeAnalyzer = new RouteAnalyzer();
		x = 0;
		y = 0;
		index = 0;
		warpCounter = 0;
		characterCounter = 0;
		routeName = name;
		routeID = routeName.toLowerCase().replace(" ", "_");
		warps = new ArrayList<Warp>();
		characters = new ArrayList<String>();
		this.setTitle(routeName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1920, 1080);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		terrains = new JComboBox<String>();
		terrains.addItem("grassy");
		terrains.addItem("stone");
		terrains.addItem("sandy");
		terrains.addItem("bright_laminat");
		terrains.addItem("dark_laminat");
		terrains.setBackground(Color.WHITE);
		save = new JButton("SAVE!");
		save.setBounds(1750, 600, 150, 40);
		save.setBackground(Color.WHITE);
		contentPane.add(save);
		contentPane.add(terrains);
		this.labels = new JLabel[height][width];
		this.horizontal = new JButton[width];
		this.vertical = new JButton[height];
		this.buttons = new JRadioButton[TYPES.length];
		for (int y = 0; y <= height; y++) {
			for (int x = 0; x <= width; x++) {
				if (y == 0 && x != 0) {
					horizontal[x - 1] = new JButton();
					horizontal[x - 1].setName(String.valueOf(x - 1));
					horizontal[x - 1].setBounds(SIZE + SIZE * x, SIZE, SIZE, SIZE);
					horizontal[x - 1].setBackground(Color.GRAY);
					horizontal[x - 1].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					contentPane.add(horizontal[x - 1]);
				} else if (y != 0 && x == 0) {
					vertical[y - 1] = new JButton();
					vertical[y - 1].setName(String.valueOf(y - 1));
					vertical[y - 1].setBounds(SIZE, SIZE + SIZE * y, SIZE, SIZE);
					vertical[y - 1].setBackground(Color.GRAY);
					vertical[y - 1].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					contentPane.add(vertical[y - 1]);
				} else if (y != 0 && x != 0) {
					labels[y - 1][x - 1] = new JLabel("");
					labels[y - 1][x - 1].setFont(new Font("monospaced", Font.PLAIN, 10));
					labels[y - 1][x - 1].setHorizontalAlignment(SwingConstants.CENTER);
					labels[y - 1][x - 1].setName(String.valueOf((x - 1) + " " + (y - 1)));
					labels[y - 1][x - 1].setOpaque(true);
					labels[y - 1][x - 1].setBounds(SIZE + SIZE * x, SIZE + SIZE * y, SIZE, SIZE);
					labels[y - 1][x - 1].setBackground(Color.WHITE);
					labels[y - 1][x - 1].setBorder(BorderFactory.createLineBorder(Color.BLACK));
					contentPane.add(labels[y - 1][x - 1]);
				}
			}
		}
		group = new ButtonGroup();
		for(int i = 0; i < TYPES.length; i++) {
			JRadioButton currentButton = new JRadioButton(RouteCreator.TYPES[i][0]);
			currentButton.setFont(currentButton.getFont().deriveFont(8.0f));
			currentButton.setBounds(1750, 40 + 15 * i, 150, 10);
			buttons[i] = currentButton;
			group.add(currentButton);
			add(currentButton);
		}
		terrains.setBounds(1750, buttons[TYPES.length - 1].getY() + 40, 150, 40);
		save.setBounds(1750, terrains.getY() + 40, 150, 40);
		buttons[0].setSelected(true);
		actionListeners();
	}

	public void actionListeners() {
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveRoute();
			}
		});
		for(index = 0; index < horizontal.length; index++) {
			horizontal[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					if(getSelectedButton().getText().equals("Free") || getSelectedButton().getText().equals("Tree") || getSelectedButton().getText().equals("Grass") || getSelectedButton().getText().equals("Sand") || getSelectedButton().getText().equals("See")) {
						for(int y = 0; y < labels.length; y++) {
							String text = "";
							for(int i = 0; i < TYPES.length; i++) {
								if(getSelectedButton().getText().equals(TYPES[i][0])) {
									text = TYPES[i][1];
								}
							}
							labels[y][Integer.parseInt(((JButton) e.getSource()).getName())].setText(text);
						}
//					}
				}
			});
		}
		for(index = 0; index < vertical.length; index++) {
			vertical[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					if(getSelectedButton().getText().equals("Free") || getSelectedButton().getText().equals("Tree") || getSelectedButton().getText().equals("Grass") || getSelectedButton().getText().equals("Sand") || getSelectedButton().getText().equals("See")) {
						for(int x = 0; x < labels[0].length; x++) {
							String text = "";
							for(int i = 0; i < TYPES.length; i++) {
								if(getSelectedButton().getText().equals(TYPES[i][0])) {
									text = TYPES[i][1];
								}
							}
							labels[Integer.parseInt(((JButton) e.getSource()).getName())][x].setText(text);
						}
//					}
				}
			});
		}
		for(y = 0; y < labels.length; y++) {
			for(x = 0; x < labels[y].length; x++) {
				labels[y][x].addMouseListener(new MouseListener() {
					@Override
				 	public void mouseReleased(MouseEvent e) {}

					@Override
					public void mousePressed(MouseEvent e) {}

					@Override
					public void mouseExited(MouseEvent e) {}

					@Override
					public void mouseEntered(MouseEvent e) {}

					@Override
					public void mouseClicked(MouseEvent e) {
						if(!active) {
							active = true;
							String text = "";
							String[] coordinates = ((JLabel) e.getSource()).getName().split(" ");
							int xCoord = Integer.parseInt(coordinates[0]);
							int yCoord = Integer.parseInt(coordinates[1]);
							for(int i = 0; i < TYPES.length; i++) {
								if(getSelectedButton().getText().equals(TYPES[i][0])) {
									text = TYPES[i][1];
									if(text.equals("P")) {
										if(!createBuilding(xCoord, yCoord, 5, 4, "P")) {
											text = "";
										}
									} else if(text.equals("HS")) {
										if(!createBuilding(xCoord, yCoord, 4, 4, "H")) {
											text = "";
										}
									} else if(text.equals("A")) {
										if(!createBuilding(xCoord, yCoord, 7, 4, "A")) {
											text = "";
										}
									} else if(text.startsWith("W")) {
										Warp newWarp = new Warp(text + warpCounter, routeID);
										System.out.print("New Route: ");
										newWarp.setNewRoute(scanner.nextLine().toLowerCase().replace(" ", "_"));
										warps.add(newWarp);
										System.out.println(xCoord + " / " + yCoord + " " + newWarp.getWarpString());
										warpCounter++;
										text = newWarp.getWarpString();
									} else if(text.equals("C")) {
										System.out.print("New Character name: ");
										characters.add(scanner.nextLine());
										text += characterCounter;
										characterCounter++;
									}
								}
							}
							labels[yCoord][xCoord].setText(text);
							active = false;
						}
					}
				});
			}
		}
	}

	public JRadioButton getSelectedButton() {
		for(int i = 0; i < buttons.length; i++) {
			if(buttons[i].isSelected()) {
				return buttons[i];
			}
		}
		return null;
	}

	public boolean createBuilding(int xStart, int yStart, int width, int height, String buildingID) {
		if(xStart + width < horizontal.length && yStart + height < vertical.length) {
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					if(y == height - 1 && x == (width - 1) / 2) {
						labels[yStart + y][xStart + x].setText("WD" + buildingID + warpCounter);
						Warp newWarp = new Warp("WD" + buildingID + warpCounter, this.routeID);
						System.out.print("New Route: ");
						newWarp.setNewRoute(scanner.nextLine().toLowerCase().replace(" ", "_"));
						System.out.println((xStart + x) + " / " + (yStart + y) + newWarp.getWarpString());
						warps.add(newWarp);
						warpCounter++;
					}
					else if(x != 0 || y != 0) {
						labels[yStart + y][xStart + x].setText("M");
					} else {
						System.out.println("ELSE: " + x + ", " + y);
					}
				}
			}
			return true;
		}
		return false;
	}

	public void saveRoute() {
		File newRoute = new File("./res/routes/" + routeID + ".route");
		try {
			JsonObject route = new JsonObject();
			JsonObject routeDetails = new JsonObject();
			routeDetails.addProperty("id", routeID);
			routeDetails.addProperty("name", routeName);
			routeDetails.addProperty("terrain", this.terrains.getItemAt(terrains.getSelectedIndex()));
			routeDetails.addProperty("height", vertical.length);
			routeDetails.addProperty("width", horizontal.length);

			for(int y = 0; y < vertical.length; y++) {
				for(int x = 0; x < horizontal.length; x++) {
					routeDetails.addProperty(x + "." + y, labels[y][x].getText());
				}
			}
			JsonArray warpDetails = new JsonArray();
			for(Warp currentWarp : warps) {
				JsonObject warp = new JsonObject();
				warp.addProperty("id", currentWarp.getWarpString());
				warp.addProperty("new_route", currentWarp.getNewRoute());
				warp.addProperty("new_x", "-1");
				warp.addProperty("new_y", "-1");
				warpDetails.add(warp);
			}
			JsonArray characterDetails = new JsonArray();
			for(int i = 0; i < characters.size(); i++) {
				JsonObject currentCharacter = new JsonObject();
				currentCharacter.addProperty("id", "C" + i);
				currentCharacter.addProperty("name", characters.get(i));
				currentCharacter.addProperty("char_sprite", "man");
				currentCharacter.addProperty("direction", "todo");
				currentCharacter.addProperty("is_trainer", "todo");
				currentCharacter.addProperty("sprite", "free");
				currentCharacter.addProperty("terrain", this.terrains.getItemAt(terrains.getSelectedIndex()));
				currentCharacter.addProperty("surfing", "todo");
				characterDetails.add(currentCharacter);
			}

			route.add("route", routeDetails);
			route.add("warps", warpDetails);
			route.add("characters", characterDetails);
			route.add("encounters", new JsonArray());
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newRoute));
			for(char c : route.toString().toCharArray()) {
				bWriter.write(c);
				bWriter.flush();
			}
//			bWriter.write(routeName);
//			bWriter.newLine();
//			bWriter.write(terrains.getItemAt(terrains.getSelectedIndex()));
//			bWriter.newLine();
//			bWriter.write(String.valueOf(vertical.length));
//			bWriter.newLine();
//			bWriter.write(String.valueOf(horizontal.length));
//			bWriter.newLine();
//			String line = "";
//			String labelText = "";
//			for(int y = 0; y < vertical.length; y++) {
//				for(int x = 0; x < horizontal.length; x++) {
//					labelText = labels[y][x].getText();
//					if(labelText.equals("")) {
//						labelText = "F";
//					}
//					if(x == 0) {
//						line = labelText;
//					} else {
//						line += "," + labelText;
//					}
//				}
//				bWriter.write(line);
//				bWriter.newLine();
//			}
//			for(Warp currentWarp : warps) {
//				line = currentWarp.getWarpString() + "," + currentWarp.getNewRoute() + ",";
//				bWriter.write(line);
//				bWriter.newLine();
//			}
//			for(int i = 0; i < characters.size(); i++) {
//				line = "C" + i + "," + characters.get(i) + ",";
//				bWriter.write(line);
//				bWriter.newLine();
//			}
//			bWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		routeAnalyzer.readRoute(newRoute);
	}
}

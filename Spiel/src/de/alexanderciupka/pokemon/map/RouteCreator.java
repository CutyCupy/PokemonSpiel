package de.alexanderciupka.pokemon.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
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
	private HashMap<String, Point> characters;
	private String routeName;
	private String routeID;
	private JButton save;
	private JComboBox<String> terrains;
	private boolean active;

	HashMap<String, ArrayList<Point>> buildings = new HashMap<>();

	private RouteAnalyzer routeAnalyzer;

	private static final String[][] TYPES = {{"Out of Bounds", "OOB"}, {"Bett", "bed"}, {"Leiter hoch", "WLU"}, {"Leiter runter", "WLD"},
			{"Treppe hoch links", "WSUL"}, {"Treppe hoch rechts", "WSUR"}, {"Treppe runter links", "WSDL"}, {"Treppe runter rechts", "WSDR"},
			{"Stärke", "ST"}, {"Zerschneider", "TC"}, {"Wand", "MW"}, {"Fenster", "MWW"}, {"Fenster+Gardine", "MWWC"}, {"Free", ""}, {"Tree", "T"}, {"Snow Tree", "TS"},
			{"Bookshelf", "BS"}, {"TV", "TV"}, {"SPÜLE", "spuele"}, {"Laptop", "LAPTOP"}, {"Table", "TA"},{"Kaffee", "KAFFEE"}, {"Chair UP", "STUHLU"},
			{"Chair DOWN", "STUHLD"}, {"Chair LEFT", "STUHLL"}, {"Chair RIGHT", "STUHLR"}, {"Settle UP", "SETTLEU"}, {"Settle DOWN", "SETTLED"},
			{"Settle LEFT", "SETTLEL"}, {"Settle RIGHT", "SETTLER"}, {"Grass", "GRASS"}, {"Grassy", "GR"}, {"Snow", "SN"}, {"Mauer", "M"}, {"Pokemon Center", "P"},
			{"House Small", "HS"}, {"Gym", "A"}, {"Warp", "W"}, {"Character", "C"}, {"See","S"}, {"Sand", "SA"}, {"Stone", "STO"}, {"Bridge", "B"}, {"PC", "BC"},
			{"JoyHealing", "JH"}, {"MoveDown", "MD"}, {"MoveUp", "MU"}, {"MoveLeft", "ML"}, {"MoveRight", "MR"}, {"MoveStop", "MS"}, {"Höhle vorne", "HWF"},
			{"Höhle hinten", "HWB"}, {"Höhle rechts", "HWR"}, {"Höhle links", "HWL"},
			{"Höhle Eingang Front", "WHEF"}, {"Höhle Eingang Back", "WHEB"}, {"Höhle Eingang Left", "WHEL"}, {"Höhle Eingang Right", "WHER"},
			{"Höhle linksvorne außen", "HWLF"}, {"Höhle linkshinten außen", "HWLB"}, {"Höhle rechtshinten außen", "HWRB"}, {"Höhle rechtsvorne außen", "HWRF"}, {"Höhle Mitte", "HM"},
			{"Höhle linksvorne innen", "HWLFI"}, {"Höhle linkshinten innen", "HWLBI"}, {"Höhle rechtshinten innen", "HWRBI"},
			{"Höhle rechtsvorne innen", "HWRFI"}, {"RockBig", "RB"}, {"RockGroup", "RG"}, {"Rock", "R"},
			{"Luke", "WH"}, {"Generator", "GENERATOR"},
			{"Zaun linksunten", "ZLU"},  {"Zaun linksoben", "ZLO"},  {"Zaun rechtsunten", "ZRU"},  {"Zaun rechtsoben", "ZRO"},
			{"Zaun links", "ZL"},  {"Zaun rechts", "ZR"},  {"Zaun front", "ZF"}};

	private static final int SIZE = 25;

	public RouteCreator() {
		int width = Integer.parseInt(JOptionPane.showInputDialog("Breite (max: 65): "));
		int height = Integer.parseInt(JOptionPane.showInputDialog("Höhe (max: 40): "));
		String name = JOptionPane.showInputDialog("Name der Route: ");

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


		routeAnalyzer = GameController.getInstance().getRouteAnalyzer();
		x = 0;
		y = 0;
		index = 0;
		warpCounter = 0;
		characterCounter = 0;
		routeName = name;
		routeID = routeName.toLowerCase().replace(" ", "_");
		warps = new ArrayList<Warp>();
		characters = new HashMap<String, Point>();
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
		terrains.addItem("cave");
		terrains.addItem("snow");
		terrains.addItem("ice");
		terrains.setBackground(Color.WHITE);
		save = new JButton("SAVE!");
		save.setBounds(1750, 600, 150, 40);
		save.setBackground(Color.WHITE);
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
		JPanel entities = new JPanel(new GridLayout(TYPES.length + 2, 1));
		group = new ButtonGroup();
		for(int i = 0; i < TYPES.length; i++) {
			JRadioButton currentButton = new JRadioButton(RouteCreator.TYPES[i][0]);
			currentButton.setFont(currentButton.getFont().deriveFont(10.0f));
			currentButton.setBounds(1750, 40 + 15 * i, 150, 10);
			buttons[i] = currentButton;
			group.add(currentButton);
			entities.add(currentButton);
		}
		terrains.setBounds(1750, 900, 150, 40);
		save.setBounds(1750, terrains.getY() + 40, 150, 40);
		add(terrains);
		add(save);
		entities.setBounds(0, 0, 220, terrains.getY());

		buttons[0].setSelected(true);

		JScrollPane pane = new JScrollPane();
		pane.setBounds(1700, 0, 200, terrains.getY());
		pane.setViewportView(entities);
		pane.createVerticalScrollBar();
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(pane);
		actionListeners();
		this.setVisible(true);
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
										newWarp.setNewRoute(JOptionPane.showInputDialog("New Route: ").toLowerCase().replace(" ", "_"));
										warps.add(newWarp);
										System.out.println(xCoord + " / " + yCoord + " " + newWarp.getWarpString());
										warpCounter++;
										text = newWarp.getWarpString();
									} else if(text.equals("C")) {
										characters.put(text + characterCounter, new Point(xCoord, yCoord));
										JOptionPane.showMessageDialog(null, text + characterCounter + " added!");
										text = "";
										characterCounter++;
									}
								}
							}
							if(e.getButton() == MouseEvent.BUTTON1) {
								labels[yCoord][xCoord].setText(text);
							} else {
								floodFill(text, new boolean[labels[0].length][labels.length], xCoord, yCoord, xCoord, yCoord);
							}
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
			String key = "";
			switch(buildingID) {
			case "H":
				key = "house_small";
				break;
			case "P":
				key = "house_center";
				break;
			case "A":
				key = "house_gym";
				break;
			default:
				key = null;
				break;
			}
			if(key != null) {
				ArrayList<Point> locations = this.buildings.get(key);
				if (locations == null) {
					locations = new ArrayList<Point>();
				}
				locations.add(new Point(xStart, yStart));
				this.buildings.put(key, locations);
			}
			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					if(y == height - 1 && x == (width - 1) / 2) {
						labels[yStart + y][xStart + x].setText("WD" + buildingID + warpCounter);
						Warp newWarp = new Warp("WD" + buildingID + warpCounter, this.routeID);
						System.out.print("New Route: ");
						newWarp.setNewRoute(JOptionPane.showInputDialog("New Route: ").toLowerCase().replace(" ", "_"));
						System.out.println((xStart + x) + " / " + (yStart + y) + newWarp.getWarpString());
						warps.add(newWarp);
						warpCounter++;
					}
					else if(x != 0 || y != 0) {
						if(y == 0) {
							labels[yStart + y][xStart + x].setText("F");
						} else {
							labels[yStart + y][xStart + x].setText("M");
						}
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
			for(String s : characters.keySet()) {

//			for(int i = 0; i < characters.size(); i++) {
				JsonObject currentCharacter = new JsonObject();
				currentCharacter.addProperty("id", s);
				currentCharacter.addProperty("x", characters.get(s).x);
				currentCharacter.addProperty("y", characters.get(s).y);
				currentCharacter.addProperty("name", "");
				currentCharacter.addProperty("char_sprite", "man");
				currentCharacter.addProperty("direction", "todo");
				currentCharacter.addProperty("is_trainer", "todo");
//				currentCharacter.addProperty("terrain", this.terrains.getItemAt(terrains.getSelectedIndex()));
				currentCharacter.addProperty("surfing", "todo");
				characterDetails.add(currentCharacter);
			}

			route.add("route", routeDetails);
			route.add("warps", warpDetails);
			route.add("characters", characterDetails);
			route.add("encounters", new JsonObject());
			route.add("events", new JsonObject());

			JsonArray buildings = new JsonArray();
			for(String key : this.buildings.keySet()) {
				for(Point p : this.buildings.get(key)) {
					JsonObject j = new JsonObject();
					j.addProperty("building", key);
					j.addProperty("x", p.x);
					j.addProperty("y", p.y);
					buildings.add(j);
				}
			}
			route.add("buildings", buildings);
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newRoute));
			for(char c : route.toString().toCharArray()) {
				bWriter.write(c);
				bWriter.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		routeAnalyzer.readRoute(newRoute);
		routeAnalyzer.getRouteById(routeID).saveMap();
	}

	private void floodFill(String text, boolean[][] visited, int x, int y, int startX, int startY) {
		visited[x][y] = true;
		if(x > 0 && !visited[x-1][y] && this.labels[y][x-1].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x-1, y, startX, startY);
		}
		if(y > 0 && !visited[x][y-1] && this.labels[y-1][x].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x, y-1, startX, startY);
		}
		if(x < this.labels[0].length - 1 && !visited[x+1][y] && this.labels[y][x+1].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x+1, y, startX, startY);
		}
		if(y < this.labels.length - 1 && !visited[x][y+1] && this.labels[y+1][x].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x, y+1, startX, startY);
		}
		this.labels[y][x].setText(text);
	}
}

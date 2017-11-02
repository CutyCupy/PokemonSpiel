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
import java.util.HashSet;

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

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.map.entities.QuestionEntity;
import de.alexanderciupka.pokemon.map.entities.QuestionType;
import de.alexanderciupka.pokemon.pokemon.Item;

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
	private int itemCounter;
	private ArrayList<Warp> warps;
	private HashMap<NPC, Point> characters;
	private HashMap<String, String> items;
	private String routeName;
	private String routeID;
	private JButton save;
	private JComboBox<String> terrains;
	private ArrayList<QuestionEntity> questions;
	private static HashSet<String> sprites;
	private boolean active;

	HashMap<String, ArrayList<Point>> buildings = new HashMap<>();

	private RouteAnalyzer routeAnalyzer;

	private static final String[][] TYPES = { { "Free", "" }, { "Out of Bounds", "OOB" }, { "Grassy", "GR" },
			{ "Snow", "SN" }, {"Eis", "ICE"}, { "See", "S" }, { "Sand", "SA" }, { "Stone", "STO" }, { "Leiter hoch", "WLU" },
			{ "Leiter runter", "WLD" }, { "Treppe hoch links", "WSUL" }, { "Treppe hoch rechts", "WSUR" },
			{ "Treppe runter links", "WSDL" }, { "Treppe runter rechts", "WSDR" }, { "Stärke", "ST" },
			{ "Zerschneider", "TC" }, { "Wand", "MW" }, { "Fenster", "MWW" }, { "Fenster+Gardine", "MWWC" },
			{ "Statue", "statue" }, { "Bett", "bed" }, { "Bookshelf", "BS" }, { "TV", "TV" }, { "Spüle", "spuele" },
			{ "Kühlschrank", "fr" }, { "Server", "SV" }, { "Vitrine", "V" }, { "Toilette", "TL" },
			{ "Badewanne", "BT" }, { "Item", "ITEM" }, { "Laptop", "LAPTOP" }, { "PC", "PC" }, { "Quiz", "QUIZ" },
			{ "Table", "TA" }, { "Kaffee", "KAFFEE" }, { "Chair UP", "STUHLU" }, { "Chair DOWN", "STUHLD" },
			{ "Chair LEFT", "STUHLL" }, { "Chair RIGHT", "STUHLR" }, { "Settle UP", "SETTLEU" },
			{ "Settle DOWN", "SETTLED" }, { "Settle LEFT", "SETTLEL" }, { "Settle RIGH T", "SETTLER" }, { "Tree", "T" },
			{ "Snow Tree", "TS" }, { "Grass", "GRASS" }, {"Schneegrass", "GRASSSNOW"}, { "Mauer", "M" }, { "LKW Links", "LKWL" },
			{ "LKW Rechts", "LKWR" }, { "Pokemon Center", "P" }, { "House Small", "HS" }, { "Gym", "A" },
			{ "Warp", "W" }, { "Character", "C" }, { "Bridge", "B" }, { "JoyHealing", "JH" }, { "MoveDown", "MD" },
			{ "MoveUp", "MU" }, { "MoveLeft", "ML" }, { "MoveRight", "MR" }, { "MoveStop", "MS" },
			{ "Höhle vorne", "HWF" }, { "Höhle hinten", "HWB" }, { "Höhle rechts", "HWR" }, { "Höhle links", "HWL" },
			{ "Höhle Eingang Front", "WHEF" }, { "Höhle Eingang Back", "WHEB" }, { "Höhle Eingang Left", "WHEL" },
			{ "Höhle Eingang Right", "WHER" }, { "Höhle linksvorne außen", "HWLF" },
			{ "Höhle linkshinten außen", "HWLB" }, { "Höhle rechtshinten außen", "HWRB" },
			{ "Höhle rechtsvorne außen", "HWRF" }, { "Höhle Mitte", "HM" }, { "Höhle linksvorne innen", "HWLFI" },
			{ "Höhle linkshinten innen", "HWLBI" }, { "Höhle rechtshinten innen", "HWRBI" },
			{ "Höhle rechtsvorne innen", "HWRFI" }, { "Treppe vorne", "SCF"}, { "RockBig", "RB" }, { "RockGroup", "RG" },
			{ "Rock", "R" }, {"Eisstein", "IR"}, {"Eissäule", "IP"}, {"Eisfels", "IRB"}, { "Luke", "WH" },
			{ "Generator", "GENERATOR" }, { "Haken", "HOOK" }, { "Zaun linksunten", "ZLU" }, { "Zaun linksoben", "ZLO" },
			{ "Zaun rechtsunten", "ZRU" }, { "Zaun rechtsoben", "ZRO" }, { "Zaun links", "ZL" }, { "Zaun rechts", "ZR" },
			{ "Zaun front", "ZF" } };

	private static final int SIZE = 25;

	public RouteCreator() {

		if(sprites == null) {
			sprites = new HashSet<>();
			for(File f : new File(Main.class.getResource("/characters/").getFile()).listFiles()) {
				if(f.isDirectory()) {
					continue;
				}
				String[] name = f.getName().split("_");
				String currentName = "";
				for(int i = 0; i < name.length - 2; i++) {
					if(i != 0) {
						currentName += " ";
					}
					currentName += name[i];
				}
				sprites.add(currentName);
			}
		}

		int width = Integer.parseInt(JOptionPane.showInputDialog("Breite (max: 65): "));
		int height = Integer.parseInt(JOptionPane.showInputDialog("Höhe (max: 40): "));
		String name = JOptionPane.showInputDialog("Name der Route: ");

		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		questions = new ArrayList<>();

		routeAnalyzer = GameController.getInstance().getRouteAnalyzer();
		x = 0;
		y = 0;
		index = 0;
		warpCounter = 0;
		characterCounter = 0;
		routeName = name;
		routeID = routeName.toLowerCase().replace(" ", "_");
		warps = new ArrayList<Warp>();
		characters = new HashMap<>();
		items = new HashMap<>();
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
		for (int i = 0; i < TYPES.length; i++) {
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
		pane.getVerticalScrollBar().setUnitIncrement(20);
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
		for (index = 0; index < horizontal.length; index++) {
			horizontal[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// if(getSelectedButton().getText().equals("Free") ||
					// getSelectedButton().getText().equals("Tree") ||
					// getSelectedButton().getText().equals("Grass") ||
					// getSelectedButton().getText().equals("Sand") ||
					// getSelectedButton().getText().equals("See")) {
					for (int y = 0; y < labels.length; y++) {
						String text = "";
						for (int i = 0; i < TYPES.length; i++) {
							if (getSelectedButton().getText().equals(TYPES[i][0])) {
								text = TYPES[i][1];
							}
						}
						labels[y][Integer.parseInt(((JButton) e.getSource()).getName())].setText(text);
					}
					// }
				}
			});
		}
		for (index = 0; index < vertical.length; index++) {
			vertical[index].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (int x = 0; x < labels[0].length; x++) {
						String text = "";
						for (int i = 0; i < TYPES.length; i++) {
							if (getSelectedButton().getText().equals(TYPES[i][0])) {
								text = TYPES[i][1];
							}
						}
						labels[Integer.parseInt(((JButton) e.getSource()).getName())][x].setText(text);
					}
				}
			});
		}
		for (y = 0; y < labels.length; y++) {
			for (x = 0; x < labels[y].length; x++) {
				labels[y][x].addMouseListener(new MouseListener() {
					@Override
					public void mouseReleased(MouseEvent e) {
					}

					@Override
					public void mousePressed(MouseEvent e) {
					}

					@Override
					public void mouseExited(MouseEvent e) {
					}

					@Override
					public void mouseEntered(MouseEvent e) {
					}

					@Override
					public void mouseClicked(MouseEvent e) {
						if (!active) {
							active = true;
							String text = "";
							String[] coordinates = ((JLabel) e.getSource()).getName().split(" ");
							int xCoord = Integer.parseInt(coordinates[0]);
							int yCoord = Integer.parseInt(coordinates[1]);
							for (int i = 0; i < TYPES.length; i++) {
								if (getSelectedButton().getText().equals(TYPES[i][0])) {
									text = TYPES[i][1];
									if (text.equals("P")) {
										if (!createBuilding(xCoord, yCoord, 5, 4, "P")) {
											text = "";
										}
									} else if (text.equals("HS")) {
										if (!createBuilding(xCoord, yCoord, 4, 4, "H")) {
											text = "";
										}
									} else if (text.equals("A")) {
										if (!createBuilding(xCoord, yCoord, 7, 4, "A")) {
											text = "";
										}
									} else if (text.startsWith("W")) {
										Warp newWarp = new Warp(text + warpCounter, routeID);
										newWarp.setNewRoute(JOptionPane.showInputDialog("New Route: ").toLowerCase()
												.replace(" ", "_"));
										warps.add(newWarp);
										warpCounter++;
										text = newWarp.getWarpString();
									} else if (text.equals("C")) {
										NPC npc = new NPC(text + characterCounter);
										npc.setName(JOptionPane.showInputDialog("Wie soll der NPC heißen?"));
										npc.setTrainer(JOptionPane.showConfirmDialog(null, "Ist es ein Trainer?", null,
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ? true : false);
										npc.setAggro(JOptionPane.showConfirmDialog(null, "Ist er aggro?", null,
												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ? true : false);
										try {
											npc.setCurrentDirection((Direction) JOptionPane.showInputDialog(null, "Welche Richtung?",
													text, JOptionPane.QUESTION_MESSAGE, null, Direction.values(),
													Direction.values()[0]));
										} catch(Exception e1) {
											npc.setCurrentDirection(Direction.DOWN);
										}
										npc.setCharacterImage(JOptionPane.showInputDialog(null, "Welches Sprite?",
												text, JOptionPane.QUESTION_MESSAGE, null, sprites.toArray(),
												sprites.toArray()[0]).toString());
										characters.put(npc, new Point(xCoord, yCoord));
										JOptionPane.showMessageDialog(null, text + characterCounter + " added!");
										text = ((JLabel) e.getComponent()).getText();
										characterCounter++;
									} else if (text.startsWith("LKW")) {
										if (xCoord < labels[0].length - 1) {
											labels[yCoord][xCoord + 1].setText("M");
										}
									} else if (text.equals("ITEM")) {
										text += itemCounter;
										itemCounter++;
										String result = "";
										String[] possibleItems = new String[Item.values().length - 1];
										boolean none = false;
										for (int counter = 0; counter < Item.values().length; counter++) {
											if (Item.values()[counter] == Item.NONE) {
												none = true;
												continue;
											}
											possibleItems[none ? counter - 1 : counter] = Item.values()[counter]
													.getName();
										}
										result += Item.getItemByName(JOptionPane.showInputDialog(null, "Welches Item?",
												text, JOptionPane.QUESTION_MESSAGE, null, possibleItems,
												possibleItems[0]).toString()).name();
										result += "+";
										result += JOptionPane.showOptionDialog(null, "Versteckt?", text,
												JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
												new String[] { "Ja", "Nein" }, "Nein") == 0 ? "TRUE" : "FALSE";
										items.put(text, result);
									} else if (text.equals("QUIZ")) {
										text += questions.size();

										QuestionEntity q = new QuestionEntity(text, null, "stone");
										try {
											q.setType((QuestionType) JOptionPane.showInputDialog(null,
													"Welche Art von Quiz?", "",
													JOptionPane.QUESTION_MESSAGE, null, QuestionType.values(),
													QuestionType.values()[0]));
											q.setQuestion(
													JOptionPane.showInputDialog("Welche Frage soll gestellt werden?"));
											String s = "";
											while (!(s = JOptionPane.showInputDialog("Welche Antworten gibt es?"))
													.equals("")) {
												q.addOptions(s);
											}
											while (!(s = JOptionPane.showInputDialog("Welche Antworten sind richtig?"))
													.equals("")) {
												q.addSolutions(s);
											}
											q.setSource(JOptionPane.showInputDialog("Welche Datei wird benötigt?"));
											try {
												q.setNPC((NPC) JOptionPane.showInputDialog(null,
														"Welcher NPC?", "",
														JOptionPane.QUESTION_MESSAGE, null, characters.keySet().toArray(),
														null));
											} catch(Exception e1) {}
											questions.add(q);
										} catch (Exception e1) {
											e1.printStackTrace();
											text = ((JLabel) e.getSource()).getText();
										}
									}
								}
							}
							if (e.getButton() == MouseEvent.BUTTON1) {
								labels[yCoord][xCoord].setText(text);
							} else {
								floodFill(text, new boolean[labels[0].length][labels.length], xCoord, yCoord, xCoord,
										yCoord);
							}
							active = false;
						}
					}
				});
			}
		}
	}

	public JRadioButton getSelectedButton() {
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i].isSelected()) {
				return buttons[i];
			}
		}
		return null;
	}

	public boolean createBuilding(int xStart, int yStart, int width, int height, String buildingID) {
		if (xStart + width < horizontal.length && yStart + height < vertical.length) {
			String key = "";
			switch (buildingID) {
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
			if (key != null) {
				ArrayList<Point> locations = this.buildings.get(key);
				if (locations == null) {
					locations = new ArrayList<Point>();
				}
				locations.add(new Point(xStart, yStart));
				this.buildings.put(key, locations);
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (y == height - 1 && x == (width - 1) / 2) {
						labels[yStart + y][xStart + x].setText("WD" + buildingID + warpCounter);
						Warp newWarp = new Warp("WD" + buildingID + warpCounter, this.routeID);
						newWarp.setNewRoute(JOptionPane.showInputDialog("New Route: ").toLowerCase().replace(" ", "_"));
						warps.add(newWarp);
						warpCounter++;
					} else if (x != 0 || y != 0) {
						if (y == 0) {
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

			for (int y = 0; y < vertical.length; y++) {
				for (int x = 0; x < horizontal.length; x++) {
					routeDetails.addProperty(x + "." + y, labels[y][x].getText());
				}
			}
			JsonArray warpDetails = new JsonArray();
			for (Warp currentWarp : warps) {
				JsonObject warp = new JsonObject();
				warp.addProperty("id", currentWarp.getWarpString());
				warp.addProperty("new_route", currentWarp.getNewRoute());
				warp.addProperty("new_x", "-1");
				warp.addProperty("new_y", "-1");
				warpDetails.add(warp);
			}
			JsonArray characterDetails = new JsonArray();
			for (NPC n : characters.keySet()) {

				// for(int i = 0; i < characters.size(); i++) {
				JsonObject currentCharacter = new JsonObject();
				currentCharacter.addProperty("id", n.getID());
				currentCharacter.addProperty("x", characters.get(n).x);
				currentCharacter.addProperty("y", characters.get(n).y);
				currentCharacter.addProperty("name", n.getName());
				currentCharacter.addProperty("char_sprite", n.getSpriteName());
				switch(n.getCurrentDirection()) {
				case DOWN:
					currentCharacter.addProperty("direction", "front");
					break;
				case LEFT:
					currentCharacter.addProperty("direction", "left");
					break;
				case NONE:
					currentCharacter.addProperty("direction", "front");
					break;
				case RIGHT:
					currentCharacter.addProperty("direction", "right");
					break;
				case UP:
					currentCharacter.addProperty("direction", "back");
					break;
				default:
					currentCharacter.addProperty("direction", "front");
					break;

				}
				currentCharacter.addProperty("is_trainer", n.isTrainer());
				currentCharacter.addProperty("aggro", n.isAggro());
				characterDetails.add(currentCharacter);
			}

			JsonArray itemDetails = new JsonArray();
			for (String s : items.keySet()) {
				JsonObject currentItem = new JsonObject();
				currentItem.addProperty("entity_id", s);
				currentItem.addProperty("name", items.get(s).split("\\+")[0]);
				currentItem.addProperty("hidden", items.get(s).split("\\+")[1]);
				itemDetails.add(currentItem);
			}

			route.add("route", routeDetails);
			route.add("warps", warpDetails);
			route.add("characters", characterDetails);
			route.add("items", itemDetails);
			route.add("encounters", new JsonObject());
			route.add("events", new JsonObject());

			JsonArray buildings = new JsonArray();
			for (String key : this.buildings.keySet()) {
				for (Point p : this.buildings.get(key)) {
					JsonObject j = new JsonObject();
					j.addProperty("building", key);
					j.addProperty("x", p.x);
					j.addProperty("y", p.y);
					buildings.add(j);
				}
			}
			route.add("buildings", buildings);

			JsonObject quizzes = new JsonObject();
			for (QuestionEntity q : this.questions) {
				JsonObject curQ = new JsonObject();
				curQ.addProperty("question", q.getQuestion());
				String result = "";
				for (int i = 0; i < q.getOptions().size(); i++) {
					if (i != 0) {
						result += "+";
					}
					result += q.getOptions().get(i);
				}
				curQ.addProperty("options", result);
				result = "";
				for (int i = 0; i < q.getSolutions().size(); i++) {
					if (i != 0) {
						result += "+";
					}
					result += q.getSolutions().get(i);
				}
				curQ.addProperty("solutions", result);
				curQ.addProperty("source", q.getSource());
				curQ.addProperty("npc", q.getNpc() != null ? q.getNpc().getID() : "");
				curQ.add("entities", new JsonArray());
				curQ.addProperty("type", q.getType().name());
				quizzes.add(q.getID().toUpperCase(), curQ);
			}

			route.add("quizzes", quizzes);

			BufferedWriter bWriter = new BufferedWriter(new FileWriter(newRoute));
			String saveString = route.toString();
			for (char c : saveString.toCharArray()) {
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
		if (x > 0 && !visited[x - 1][y]
				&& this.labels[y][x - 1].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x - 1, y, startX, startY);
		}
		if (y > 0 && !visited[x][y - 1]
				&& this.labels[y - 1][x].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x, y - 1, startX, startY);
		}
		if (x < this.labels[0].length - 1 && !visited[x + 1][y]
				&& this.labels[y][x + 1].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x + 1, y, startX, startY);
		}
		if (y < this.labels.length - 1 && !visited[x][y + 1]
				&& this.labels[y + 1][x].getText().equals(this.labels[startY][startX].getText())) {
			floodFill(text, visited, x, y + 1, startX, startY);
		}
		this.labels[y][x].setText(text);
	}
}

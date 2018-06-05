package de.alexanderciupka.pokemon.map.entities;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.main.Main;
import de.alexanderciupka.pokemon.map.Route;
import de.alexanderciupka.pokemon.menu.SoundController;

public class QuestionEntity extends Entity {

	private QuestionType type;

	private String question;
	private ArrayList<String> options;
	private ArrayList<String> solutions;

	private NPC npc;
	private ArrayList<Entity> gates;

	private String source;

	private boolean answered;

	public QuestionEntity(Route parent, String terrainName) {
		super(parent, false, "sign", 0, terrainName);
		this.options = new ArrayList<>();
		this.solutions = new ArrayList<>();
		this.gates = new ArrayList<>();
	}

	public void addOptions(String... options) {
		for (String s : options) {
			addOption(s);
		}
	}

	private void addOption(String option) {
		this.options.add(option);
	}

	public void clearOptions() {
		this.options.clear();
	}

	public void addSolutions(String... solutions) {
		for (String s : solutions) {
			addSolution(s);
		}
	}

	private void addSolution(String solution) {
		this.solutions.add(solution);
	}

	public void clearSolutions() {
		this.solutions.clear();
	}

	public void addGates(Entity... gates) {
		for (Entity e : gates) {
			addGate(e);
		}
	}

	private void addGate(Entity gate) {
		this.gates.add(gate);
	}

	public void clearGates() {
		this.gates.clear();
	}

	public void setNPC(NPC npc) {
		this.npc = npc;
	}

	public void setType(QuestionType type) {
		this.type = type;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public QuestionType getType() {
		return type;
	}

	public String getSource() {
		return source;
	}

	public ArrayList<String> getOptions() {
		return options;
	}

	public ArrayList<String> getSolutions() {
		return solutions;
	}

	public NPC getNpc() {
		return npc;
	}

	public ArrayList<Entity> getGates() {
		return gates;
	}

	public String getQuestion() {
		return this.question;
	}

	@Override
	public void onInteraction(Player c) {
		GameFrame gf = gController.getGameFrame();
		if (!answered) {
			gf.addDialogue(
					"Beantwortest du die folgende Frage richtig, so öffnen sich die Tore. "
					+ (npc != null ? "Solltest du allerdings diese Frage falsch beantworten, so musst du "
							+ "eventuell gegen einen Trainer kämpfen!" : ""));
			gf.addDialogue(type.getInformation());
			gf.getDialogue().waitText();
			ArrayList<String> answers = new ArrayList<>();
			switch (type) {
			case MULTIPLE_CHOICE:
				JCheckBox[] checkBoxes = new JCheckBox[options.size()];
				Object[] message = new Object[options.size() + 1];
				message[0] = question;
				for (int i = 0; i < options.size(); i++) {
					checkBoxes[i] = new JCheckBox(options.get(i));
					message[i+1] = checkBoxes[i];
				}

				JOptionPane.showOptionDialog(null, message, "Multiple Choice!",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				for (int i = 0; i < checkBoxes.length; i++) {
					if (checkBoxes[i].isSelected()) {
						answers.add(checkBoxes[i].getText());
					}
				}

				break;
			case SINGLE_CHOICE:
				int result = JOptionPane.showOptionDialog(gf, question, "Wähle die Antwort aus!",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options.toArray(), null);
				if (result >= 0 && result < options.size()) {
					answers.add(options.get(result));
				}
				break;
			case SOUND_GAME:
				this.source = "quiz/" + this.source;
				question = "Na weißt du welcher Song gemeint ist?";
				ImageIcon icon = null;
				try {
					icon = new ImageIcon(ImageIO.read(new File(Main.class.getResource("/icons/note.png").getFile())));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Main.SONG_PAUSE = true;
				SoundController s = SoundController.getInstance();
				s.stopSong();
				JTextField group = new JTextField();
				JTextField song = new JTextField();
				message = new Object[] { question, "Gruppe / Künstler: ", group, "Song: ", song };

				int agains = 6;

				JButton[] buttons = {new JButton("Anhören"), new JButton("OK")};
				for(JButton b : buttons) {
					b.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							JOptionPane pane = Main.getOptionPane((JComponent) e.getSource());
							pane.setValue(e.getSource());
						}
					});
				}

				do {
					agains--;
					buttons[0].setText("Anhören (" + agains + ")");
					if (agains == 0) {
						buttons[0].setEnabled(false);
					} else {
						buttons[0].setEnabled(true);
					}
					s.playSound(this.source + "_quiz");
				} while ((result = JOptionPane.showOptionDialog(gf,
						message, "Erkenne den Song!",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, buttons, buttons[0])) == 0);
				if(result == 1) {
					answers.add(song.getText().toLowerCase());
					answers.add(group.getText().toLowerCase());
					s.playSound(this.source, true);
				}
			default:
				break;
			}
			boolean correct = answers.size() > 0;
			HashSet<String> solutions = new HashSet<>();
			for(String solution : this.solutions) {
				solutions.add(solution.toLowerCase());
			}
			for (String s : answers) {
				if (!solutions.contains(s.toLowerCase())) {
					correct = false;
					break;
				} else {
					solutions.remove(s.toLowerCase());
				}
			}
			if ((correct && solutions.isEmpty()) || answers.contains("alex ist der beste")) {
				gf.addDialogue("Das war richtig! Die Tore haben sich geöffnet!");
				SoundController.getInstance().playSound(SoundController.GREAT_SUCCESS);
				gf.getDialogue().waitText();
				open(c);
			} else {
				gf.addDialogue("Möööööp! Deine Antwort war falsch!");
				gf.getDialogue().waitText();
				if (npc != null && !npc.isDefeated()) {
					int deltaX = npc.getCurrentPosition().x - c.getCurrentPosition().x;
					int deltaY = npc.getCurrentPosition().y - c.getCurrentPosition().y;
					if(Math.abs(deltaX) > Math.abs(deltaY)) {
						if(deltaX > 0) {
							c.setCurrentDirection(Direction.RIGHT);
							npc.setCurrentDirection(Direction.LEFT);
						} else if(deltaX < 0) {
							c.setCurrentDirection(Direction.LEFT);
							npc.setCurrentDirection(Direction.RIGHT);
						}
					} else {
						if(deltaY > 0) {
							c.setCurrentDirection(Direction.DOWN);
							npc.setCurrentDirection(Direction.UP);
						} else if(deltaY < 0) {
							c.setCurrentDirection(Direction.UP);
							npc.setCurrentDirection(Direction.DOWN);
						}
					}
					npc.getCurrentRoute().updateMap(npc.getCurrentPosition());
					gf.addDialogue(npc.getName() + ": Das hast du wohl falsch beantwortet! Jetzt musst du gegen mich ran!");
					gf.getDialogue().waitText();
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gController.startFight(npc);
					while(gController.isFighting()) {
						Thread.yield();
					}
					if(npc.isDefeated()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						gf.addDialogue("Super! Die Tore haben sich geöffnet!");
						gf.getDialogue().waitText();
						open(c);
					}
				}
			}
		} else {
			gf.addDialogue("Diese Frage habe ich bereits beantwortet!");
			gf.getDialogue().waitText();
		}
	}

	private void open(Player c) {
		answered = true;
		ArrayList<Point> points = new ArrayList<>(this.gates.size());
		for (Entity e : this.gates) {
			e.setSprite("free");
			e.setAccessible(true);
			points.add(new Point(e.getX(), e.getY()));
		}
		c.getCurrentRoute().updateMap(points.toArray(new Point[points.size()]));
	}

	@Override
	public JsonObject getSaveData(Entity entity) {
		JsonObject saveData = super.getSaveData(entity);
		saveData.addProperty("question", this.question);
		saveData.addProperty("type", this.type.name());
		saveData.addProperty("options", Main.arrayToString(this.options.toArray()));
		saveData.addProperty("solutions", Main.arrayToString(this.solutions.toArray()));
		saveData.addProperty("source", this.source);
		saveData.addProperty("answered", this.answered);
		JsonArray entities = new JsonArray();
		for(Entity e : this.gates) {
			JsonObject cur = new JsonObject();
			cur.addProperty("x", e.getX());
			cur.addProperty("y", e.getY());
			entities.add(cur);
		}
		saveData.add("entities", entities);
		saveData.addProperty("npc", (this.npc == null) ? "" : this.npc.getID());
		return saveData;
	}

	@Override
	public boolean importSaveData(JsonObject saveData, Entity entity) {
		if(super.importSaveData(saveData, entity)) {
			QuestionEntity origin = (QuestionEntity) entity;
			if(saveData.get("question") != null) {
				this.question = saveData.get("question").getAsString();
			} else {
				this.question = origin.question;
			}
			if(saveData.get("type") != null) {
				this.type = QuestionType.valueOf(saveData.get("type").getAsString().toUpperCase());
			} else {
				this.type = origin.type;
			}
			if(saveData.get("options") != null) {
				addOptions(saveData.get("options").getAsString().split("\\+"));
			} else {
				this.options = new ArrayList<>(origin.options);
			}
			if(saveData.get("solutions") != null) {
				addSolutions(saveData.get("solutions").getAsString().split("\\+"));
			} else {
				this.solutions = new ArrayList<>(origin.solutions);
			}
			if(saveData.get("entities") != null) {
				for(JsonElement j : saveData.get("entities").getAsJsonArray()) {
					JsonObject e = j.getAsJsonObject();
					addGate(getRoute().getEntity(e.get("x").getAsInt(), e.get("y").getAsInt()));
				}
			} else {
				this.gates = new ArrayList<>(origin.gates);
			}
			if(saveData.get("npc") != null) {
				this.npc = this.getRoute().getNPC(saveData.get("npc").getAsString());
			} else {
				this.npc = origin.npc;
			}
			this.answered = saveData.get("answered") != null ? saveData.get("answered").getAsBoolean() : false;
		}
		return false;
	}

	public static QuestionEntity convert(Entity entity) {
		QuestionEntity result = new QuestionEntity(entity.getRoute(), entity.getTerrainName());
		if (entity.getWarp() != null) {
			result.addWarp(entity.getWarp().clone());
		}
		result.setAccessible(false);
		result.setSprite(entity.getSpriteName());
		result.setEncounterRate(entity.getEncounterRate());
		result.setWater(entity.isWater());
		result.setEvent(entity.getEvent() == null ? null : entity.getEvent().clone());
		result.setX(entity.getX());
		result.setY(entity.getY());
		return result;
	}
}

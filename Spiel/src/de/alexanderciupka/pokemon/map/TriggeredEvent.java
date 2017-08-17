package de.alexanderciupka.pokemon.map;

import java.awt.Point;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.pokemon.Character;
import de.alexanderciupka.pokemon.pokemon.Direction;
import de.alexanderciupka.pokemon.pokemon.NPC;
import de.alexanderciupka.pokemon.pokemon.Player;

public class TriggeredEvent {

	private ArrayList<Change[]> changes;

	private GameController gController;

	private String id;
	private boolean triggered;

	public TriggeredEvent(String id) {
		this.changes = new ArrayList<Change[]>();
		this.id = id;

		this.gController = GameController.getInstance();
	}
	
	public ArrayList<Change[]> getChanges() {
		return this.changes;
	}

	public void addChanges(Change[] changes) {
		this.changes.add(changes);
	}
	
	public String getId() {
		return id;
	}

	public void startEvent(Player source) {
		if(!triggered) {
			triggered = true;
			while(gController.getInteractionPause()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			gController.setInteractionPause(true);
			ArrayList<NPC> defeats = new ArrayList<NPC>();
			ArrayList<Character> allParticipants = new ArrayList<Character>();
			ArrayList<Point> allOriginalPoints = new ArrayList<Point>();
			ArrayList<Direction> allOriginalDirections = new ArrayList<Direction>();
			ArrayList<String> allOriginalBeforeDialoges = new ArrayList<String>();
			ArrayList<String> allOriginalAfterDialoges = new ArrayList<String>();
			ArrayList<String> allOriginalNoDialoges = new ArrayList<String>();
			ArrayList<String> allOriginalSprites = new ArrayList<String>();
			for(int i = 0; i < this.changes.size(); i++) {
				Change[] currentChanges = this.changes.get(i);
				int max = 0;
				for(int j = 0; j < currentChanges.length; j++) {
					currentChanges[j].initiate(source);
					Character c = currentChanges[j].getCharacter();
					if(c != null) {
						if(!allParticipants.contains(c)) {
							allParticipants.add(c);
							allOriginalPoints.add(new Point(c.getCurrentPosition()));
							allOriginalDirections.add(c.getCurrentDirection());
							if(c instanceof NPC) {
								allOriginalBeforeDialoges.add(((NPC) c).getBeforeFightDialogue());
								allOriginalAfterDialoges.add(((NPC) c).getOnDefeatDialogue());
								allOriginalNoDialoges.add(((NPC) c).getNoFightDialogue());
							}
							allOriginalSprites.add(c.getSpriteName());
						}
					} else {
						c = source;
					}
					max = currentChanges[j].getPath().length > max ? currentChanges[j].getPath().length : max;
				}
				Character currentChar = null;
				for(int j = 0; j < max; j++) {
					for(int c = 0; c < currentChanges.length; c++) {
						currentChar = currentChanges[c].getCharacter();
						if(j < currentChanges[c].getPath().length) {
							currentChar.changePosition(currentChanges[c].getPath()[j], false);
						}
					}
					for(int c = 0; c < currentChanges.length; c++) {
						currentChar = currentChanges[c].getCharacter();
						currentChar.waiting(true);
						if(j >= currentChanges[c].getPath().length - 1 && currentChanges[c].getDirection() != null) {
							currentChar.setCurrentDirection(currentChanges[c].getDirection());
						}
					}
					GameController.getInstance().getGameFrame().repaint();
				}
				if(max == 0) {
					for(int c = 0; c < currentChanges.length; c++) {
						currentChar = currentChanges[c].getCharacter();
						currentChar.waiting(true);
						if(c >= currentChanges[c].getPath().length - 1 && currentChanges[c].getDirection() != null) {
							currentChar.setCurrentDirection(currentChanges[c].getDirection());
						}
					}
					GameController.getInstance().getGameFrame().repaint();
				}
				for(int j = 0; j < currentChanges.length; j++) {
					currentChar = currentChanges[j].getCharacter();
					if(currentChanges[j].isPositionUpdate()) {
						currentChar.setOriginalPosition(currentChar.getCurrentPosition());
						currentChar.setOriginalDirection(currentChar.getCurrentDirection());
					}
					if(currentChar instanceof NPC) {
						NPC currentNPC = (NPC) currentChar;
						if(currentChanges[j].getAfterFightUpdate() != null) {
							currentNPC.setAfterFightDialog(currentChanges[j].getAfterFightUpdate());
						}
						if(currentChanges[j].getBeforeFightUpdate() != null) {
							currentNPC.setBeforeFightDialogue(currentChanges[j].getBeforeFightUpdate());
						}
						if(currentChanges[j].getNoFightUpdate() != null) {
							currentNPC.setNoFightDialogue(currentChanges[j].getNoFightUpdate());
						}
					}
					if(currentChanges[j].getSpriteUpdate() != null) {
						switch(currentChar.getCurrentDirection()) {
						case DOWN:
							currentChar.setCharacterImage(currentChanges[j].getSpriteUpdate(), "front");
							break;
						case LEFT:
							currentChar.setCharacterImage(currentChanges[j].getSpriteUpdate(), "left");
							break;
						case RIGHT:
							currentChar.setCharacterImage(currentChanges[j].getSpriteUpdate(), "right");
							break;
						case UP:
							currentChar.setCharacterImage(currentChanges[j].getSpriteUpdate(), "back");
							break;
						default:
							break;
						}
					}
					if(currentChanges[j].isHeal()) {
						currentChar.getTeam().restoreTeam();
					}
				}
				for(int j = 0; j < currentChanges.length; j++) {
					if(currentChanges[j].getDialog() != null) {
						currentChar = currentChanges[j].getCharacter();
						GameController.getInstance().getGameFrame().addDialogue((currentChar instanceof NPC ? (currentChar.getName() + ": ") : "") + currentChanges[j].getDialog());
						GameController.getInstance().waitDialogue();
					}
				}
				for(int j = 0; j < currentChanges.length; j++) {
					currentChar = currentChanges[j].getCharacter();
					if(currentChanges[j].isFight() && currentChar instanceof NPC && currentChar.isTrainer() && !currentChar.isDefeated()) {
						NPC enemy = (NPC) currentChar;
						gController.startFight(enemy);
						gController.getGameFrame().repaint();
						do {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} while(gController.isFighting());
						if(enemy.isDefeated()) {
							defeats.add(enemy);
						} else {
							for(NPC current : defeats) {
								current.defeated(false);
								current.getTeam().restoreTeam();
							}
							for(int k = 0; k < allParticipants.size(); k++) {
								Character c = allParticipants.get(k);
								Point p = allOriginalPoints.get(k);
								Direction d = allOriginalDirections.get(k);
								c.setCurrentPosition(p);
								c.setOriginalPosition(p);
								c.setCurrentDirection(d);
								c.setOriginalDirection(d);
								switch(d) {
								case DOWN:
									c.setCharacterImage(allOriginalSprites.get(k), "front");
									break;
								case LEFT:
									c.setCharacterImage(allOriginalSprites.get(k), "left");
									break;
								case RIGHT:
									c.setCharacterImage(allOriginalSprites.get(k), "right");
									break;
								case UP:
									c.setCharacterImage(allOriginalSprites.get(k), "back");
									break;
								default:
									break;
								}
								if(c instanceof NPC) {
									NPC n = (NPC) c;
									n.setAfterFightDialog(allOriginalAfterDialoges.get(k));
									n.setBeforeFightDialogue(allOriginalBeforeDialoges.get(k));
									n.setNoFightDialogue(allOriginalNoDialoges.get(k));
								}
							}
							triggered = false;
							gController.setInteractionPause(false);
							return;
						}
					}
				}
			}
			gController.setInteractionPause(false);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof TriggeredEvent) {
			TriggeredEvent other = (TriggeredEvent) obj;
			if(other.changes.size() != this.changes.size() || !this.id.equals(other.id) || this.triggered != other.triggered) {
				System.err.println("wrong triggeredevent");
				return false;
			}
			for(int i = 0; i < this.changes.size(); i++) {
				if(this.changes.get(i).length != other.changes.get(i).length) {
					System.err.println("wrong changesize");
					return false;
				} else {
					for(int j = 0; j < this.changes.get(i).length; j++) {
						if(!this.changes.get(i)[j].equals(other.changes.get(i)[j])) {
							System.err.println("unequal changes");
							return false;
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public JsonObject getSaveData(TriggeredEvent event) {
		JsonObject saveData = new JsonObject();
		saveData.addProperty("id", this.id);
		if(this.triggered != event.triggered) {
			saveData.addProperty("triggered", this.triggered);
		}
		return saveData;
	}
	
	public boolean importSaveData(JsonObject saveData, TriggeredEvent event) {
		if(saveData.get("id").getAsString().equals(this.id)) {
			if(saveData.get("triggered") != null) {
				this.triggered = saveData.get("triggered").getAsBoolean();
			} else {
				this.triggered = event.triggered;
			}
			return true;
		}
		return false;
	}

	@Override
	protected TriggeredEvent clone() {
		TriggeredEvent clone = new TriggeredEvent(this.id);
		clone.triggered = this.triggered;
		for(int i = 0; i < changes.size(); i++) {
			Change[] changes = new Change[this.changes.get(i).length];
			for(int j = 0; j < this.changes.get(i).length; i++) {
				changes[j] = this.changes.get(i)[j].clone();
			}
			clone.addChanges(changes);
		}
		return clone;
	}

}

package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;
import java.util.ArrayList;

import de.alexanderciupka.sarahspiel.pokemon.Character;
import de.alexanderciupka.sarahspiel.pokemon.Direction;
import de.alexanderciupka.sarahspiel.pokemon.Player;

public class TriggeredEvent {

	private ArrayList<String[]> participants;
	private ArrayList<String[]> routes;
	private ArrayList<Direction[]> directions;
	private ArrayList<Point[]> moves;
	private ArrayList<String[]> dialoges;

	private String id;
	private boolean triggered;


	public TriggeredEvent(String id) {
		this.participants = new ArrayList<String[]>();
		this.routes = new ArrayList<String[]>();
		this.moves = new ArrayList<Point[]>();
		this.dialoges = new ArrayList<String[]>();
		this.directions = new ArrayList<Direction[]>();
		this.id = id;
	}

	public ArrayList<String[]> getParticipants() {
		return participants;
	}

	public ArrayList<String[]> getRoutes() {
		return this.routes;
	}

	public ArrayList<Point[]> getMoves() {
		return moves;
	}

	public ArrayList<String[]> getDialoges() {
		return dialoges;
	}

	public ArrayList<Direction[]> getDirections() {
		return directions;
	}

	public void addCharacters(String[] chars) {
		this.participants.add(chars);
	}

	public void addRoutes(String[] routes) {
		this.routes.add(routes);
	}

	public void addMoves(Point[] moves) {
		this.moves.add(moves);
	}

	public void addDialoges(String[] dialoges) {
		this.dialoges.add(dialoges);
	}

	public void addDirections(String[] directions) {
		ArrayList<Direction> result = new ArrayList<Direction>();
		for(String s : directions) {
			for(Direction dir : Direction.values()) {
				if(dir.name().toLowerCase().equals(s.toLowerCase())) {
					result.add(dir);
					break;
				}
			}
		}
		this.directions.add(result.toArray(new Direction[result.size()]));
	}

	public String getId() {
		return id;
	}

	public void startEvent(Player source) {
		if(!triggered) {
			triggered = true;
			for(int i = 0; i < Math.min(routes.size(), Math.min(participants.size(), Math.min(moves.size(), dialoges.size()))); i++) {
				Character[] currentParticipants = new Character[Math.min(this.participants.get(i).length, this.routes.get(i).length)];
				for(int j = 0; j < currentParticipants.length; j++) {
					currentParticipants[j] = GameController.getInstance().getRouteAnalyzer().getRouteById(routes.get(i)[j]).getNPCByName(participants.get(i)[j]);
				}
				Point[] currentMoves = moves.get(i);
				String[] currentDialoges = dialoges.get(i);
				ArrayList<Direction[]> currentDirections = new ArrayList<Direction[]>();
				int max = -1;
				for(int j = 0; j < Math.min(currentParticipants.length, currentMoves.length); j++) {
					Character current = currentParticipants[j];
					if(current == null) {
						current = source;
					}
					Direction[] dir = current.moveTowards(currentMoves[j].x, currentMoves[j].y);
					currentDirections.add(dir);
					max = dir.length > max ? dir.length : max;
				}
				for(int j = 0; j < max; j++) {
					for(int c = 0; c < currentParticipants.length; c++) {
						if(j < currentDirections.get(c).length) {
							if(currentParticipants[c] != null) {
								currentParticipants[c].changePosition(currentDirections.get(c)[j], false);
							} else {
								source.changePosition(currentDirections.get(c)[j], false);
							}
						}
					}
					for(int c = 0; c  < currentParticipants.length; c++) {
						if(currentParticipants[c] != null) {
							currentParticipants[c].waiting();
							if(this.directions.get(i)[c] != null) {
								currentParticipants[c].setCurrentDirection(this.directions.get(i)[c]);
							}
						} else {
							System.out.println("source");
							source.waiting();
							if(this.directions.get(i)[c] != null) {
								source.setCurrentDirection(this.directions.get(i)[c]);
							}
						}
						GameController.getInstance().repaint();
					}
				}
				for(int j = 0; j < Math.min(currentDialoges.length, currentParticipants.length); j++) {
					if(currentDialoges[j] != null) {
						GameController.getInstance().getGameFrame().addDialogue((currentParticipants[j] != null ? (currentParticipants[j].getName() + ": ") : "") + currentDialoges[j]);
						GameController.getInstance().waitDialogue();
					}
				}
			}
		}
	}


}

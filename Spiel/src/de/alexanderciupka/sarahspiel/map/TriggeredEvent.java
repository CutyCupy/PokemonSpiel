package de.alexanderciupka.sarahspiel.map;

import java.awt.Point;
import java.util.ArrayList;

import de.alexanderciupka.sarahspiel.pokemon.Player;

public class TriggeredEvent {

	private ArrayList<Character[]> participants;
	private ArrayList<Point[]> moves;
	private ArrayList<String[]> dialoges;

	private int id;
	private boolean triggered;


	public TriggeredEvent(int id) {
		this.participants = new ArrayList<Character[]>();
		this.moves = new ArrayList<Point[]>();
		this.dialoges = new ArrayList<String[]>();

		this.id = id;
	}


	public ArrayList<Character[]> getParticipants() {
		return participants;
	}


	public ArrayList<Point[]> getMoves() {
		return moves;
	}


	public ArrayList<String[]> getDialoges() {
		return dialoges;
	}


	public void addCharacters(Character[] chars) {
		this.participants.add(chars);
	}

	public void addMoves(Point[] moves) {
		this.moves.add(moves);
	}

	public void addDialoges(String[] dialoges) {
		this.dialoges.add(dialoges);
	}

	public void startEvent(Player source) {
		if(!triggered) {
			triggered = true;
			for(int i = 0; i < Math.min(participants.size(), Math.min(moves.size(), dialoges.size())); i++) {
				if(participants.get(i) == null) {
					//source moving
				} else {
					//character moving
				}
			}
		}
	}


}

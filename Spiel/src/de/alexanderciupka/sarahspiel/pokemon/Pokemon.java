package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;

import de.alexanderciupka.sarahspiel.map.GameController;

public class Pokemon {

	private int id;
	private String name;
	private Image spriteFront;
	private Image spriteBack;
	private GameController gController;

	private Stats stats;

	private Move[] moves;
	
	private Type[] types;
	private Ailment ailment;

	public Pokemon(int id, String name) {
		gController = GameController.getInstance();
		this.id = id;
		this.name = name;
		this.stats = new Stats(this);
		this.spriteFront = new ImageIcon(this.getClass().getResource("/pokemon/front/" + id + ".png")).getImage();
		this.spriteBack = new ImageIcon(this.getClass().getResource("/pokemon/back/" + id + ".png")).getImage();
		this.types = gController.getInformation().getTypes(id);
		this.moves = new Move[4];
		this.ailment = Ailment.NONE;
	}

	public Pokemon(int id) {
		this.id = id;
		gController = GameController.getInstance();
		this.name = gController.getInformation().getName(id);
		this.types = gController.getInformation().getTypes(id);
		this.stats = new Stats(this);
		this.spriteFront = new ImageIcon(this.getClass().getResource("/pokemon/front/" + id + ".png")).getImage();
		this.spriteBack = new ImageIcon(this.getClass().getResource("/pokemon/back/" + id + ".png")).getImage();
		this.moves = new Move[4];
		this.ailment = Ailment.NONE;
	}
	
	public Stats getStats() {
		return this.stats;
	}

	public void gainXP(int gain) {
		stats.gainXP(gain);
	}

	public Move[] getMoves() {
		return moves;
	}

	public Move getMoveByName(String name) {
		for (Move m : moves) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
	public int getAmmountOfMoves() {
		for(int i = 0; i < moves.length; i++) {
			if(moves[i] == null) {
				return i;
			}
		}
		return 4;
	}

	public void setMoves(Move[] moves) {
		this.moves = moves;
	}
	
	public boolean swapMoves(int first, int second) {
		if(this.moves[first] == null || this.moves[second] == null || first == second) {
			return false;
		} else {
			Move m = this.moves[first].clone();
			this.moves[first] = this.moves[second];
			this.moves[second] = m;
			return true;
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Image getSpriteFront() {
		return spriteFront;
	}

	public Image getSpriteBack() {
		return spriteBack;
	}

	public void startFight() {
		stats.startFight();
	}

	public Move getRandomMove() {
		Random rng = new Random();
		int counter = 0;
		for (Move m : moves) {
			if (m != null && m.getPp() > 0) {
				counter++;
			}
		}
		if(counter == 0) {
			return gController.getPokemonInformation().getMoveByName("Verzweifler");
		}
		return moves[rng.nextInt(counter)];
	}

	public void addMove(String moveName) {
		//TODO: what happens when 4 moves already learned
		for(int i = 0; i < 4; i++) {
			if(moves[i] == null) {
				moves[i] = gController.getInformation().getMoveByName(moveName).clone();
				return;
			}
		}
	}
		
	public boolean addMove(String currentMove, Move replacementMove) {
		for(int i = 0; i < 4; i++) {
			if(moves[i].getName().equals(currentMove)) {
				moves[i] = replacementMove;
				return true;
			}
		}
		return false;
	}
	
	public void setTypes(Type... types) {
		this.types = new Type[2];
		for(int i = 0; i < Math.min(2, types.length); i++) {
			this.types[i] = types[i];
		}
	}
	
	
	//TODO: Change to return this.types
	public Type[] getTypes() {
		return this.types;
	}

	public boolean evolve(int newID) {
		if(newID != 0) {
			this.id = newID;
			update();
			return true;
		}
		return false;
	}
	
	private void update() {
		this.name = gController.getInformation().getName(id);
		this.spriteFront = new ImageIcon(this.getClass().getResource("/pokemon/front/" + id + ".png")).getImage();
		this.spriteBack = new ImageIcon(this.getClass().getResource("/pokemon/back/" + id + ".png")).getImage();
	}

	public void restoreMoves() {
		for (int i = 0; i < moves.length; i++) {
			if (moves[i] != null) {
				moves[i].setCurrentPP(moves[i].getCurrentPP());
			}
		}
	}

	public Move getMove(Pokemon player) {
		double highscore = -1;
		int index = -1;
		for(int i = 0; i < this.getAmmountOfMoves(); i++) {
				double current = Type.getEffectiveness(this.getMoves()[i].getMoveType(), player.getTypes());
				if(current > highscore) {
					index = i;
					highscore = current;
				} else if(current == highscore) {
					if(new Random().nextFloat() >= 0.5) {
						index = i;
						highscore = current;
					}
				}
		}
		return this.getMoves()[index];
	}
}

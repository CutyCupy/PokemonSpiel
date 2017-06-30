package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.util.Random;

import javax.swing.ImageIcon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
	private int since;

	private Random rng;

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
		this.rng = new Random();
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
		this.rng = new Random();
	}

	public Stats getStats() {
		return this.stats;
	}

	public boolean gainXP(int gain) {
		return stats.gainXP(gain);
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
		for (int i = 0; i < moves.length; i++) {
			if (moves[i] == null) {
				return i;
			}
		}
		return 4;
	}

	public void setMoves(Move[] moves) {
		this.moves = moves;
	}

	public boolean swapMoves(int first, int second) {
		if (this.moves[first] == null || this.moves[second] == null || first == second) {
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
		if (counter == 0) {
			return gController.getPokemonInformation().getMoveByName("Verzweifler");
		}
		return moves[rng.nextInt(counter)];
	}

	public void addMove(String moveName) {
		// TODO: what happens when 4 moves already learned
		for (int i = 0; i < 4; i++) {
			if (moves[i] == null) {
				moves[i] = gController.getInformation().getMoveByName(moveName).clone();
				return;
			}
		}
	}

	private void addMove(Move move) {
		this.addMove(move.getName());
	}

	public boolean addMove(String currentMove, Move replacementMove) {
		for (int i = 0; i < 4; i++) {
			if (moves[i].getName().equals(currentMove)) {
				moves[i] = replacementMove;
				return true;
			}
		}
		return false;
	}

	public void setTypes(Type... types) {
		this.types = new Type[2];
		for (int i = 0; i < Math.min(2, types.length); i++) {
			this.types[i] = types[i];
		}
	}

	// TODO: Change to return this.types
	public Type[] getTypes() {
		return this.types;
	}

	public boolean evolve(int newID) {
		if (newID != 0) {
			this.id = newID;
			update();
			if (gController.isFighting()) {
				gController.getGameFrame().getFightPanel().setPlayer();
			}
			return true;
		}
		return false;
	}

	private void update() {
		this.name = gController.getInformation().getName(id);
		this.spriteFront = new ImageIcon(this.getClass().getResource("/pokemon/front/" + id + ".png")).getImage();
		this.spriteBack = new ImageIcon(this.getClass().getResource("/pokemon/back/" + id + ".png")).getImage();
		this.types = gController.getPokemonInformation().getTypes(this.id);
	}

	public void restoreMoves() {
		for (int i = 0; i < moves.length; i++) {
			if (moves[i] != null) {
				moves[i].setCurrentPP(moves[i].getPp());
			}
		}
	}

	public Move getMove(Pokemon player) {
		double highscore = -1;
		int index = -1;
		for (int i = 0; i < this.getAmmountOfMoves(); i++) {
			double current = Type.getEffectiveness(this.getMoves()[i].getMoveType(), player.getTypes());
			if (current > highscore) {
				index = i;
				highscore = current;
			} else if (current == highscore) {
				if (new Random().nextBoolean()) {
					index = i;
					highscore = current;
				}
			}
		}
		return this.getMoves()[index];
	}

	public Ailment getAilment() {
		return ailment;
	}

	public boolean setAilment(Ailment ailment) {
		if (Ailment.NONE.equals(this.ailment) || ailment.equals(Ailment.NONE) || ailment.equals(Ailment.FAINTED)) {
			this.since = 1;
			this.ailment = ailment;
			return true;
		}
		return false;
	}

	public String canAttack() {
		switch (this.ailment) {
		case FREEZE:
			if (rng.nextFloat() < 0.2f) {
				this.ailment = Ailment.NONE;
				this.gController.getGameFrame().getFightPanel().updatePanels();
				return this.name + " ist wieder aufgetaut!";
			}
			return this.name + " kann sich nicht bewegen!";
		case PARALYSIS:
			if (rng.nextFloat() < (1 / 3.0)) {
				return this.name + " ist paralysiert und kann sich nicht bewegen!";
			}
			break;
		case SLEEP:
			if (rng.nextFloat() < 0.25f) {
				this.ailment = Ailment.NONE;
				gController.getGameFrame().getFightPanel().updatePanels();
				return this.name + " ist wieder aufgewacht!";
			}
			return this.name + " schl�ft tief und fest!";
		case CONFUSION:
			if (rng.nextFloat() < 0.2f * since) {
				this.ailment = Ailment.NONE;
				gController.getGameFrame().getFightPanel().updatePanels();
				this.gController.getGameFrame().getFightPanel().addText(this.name + " ist nicht mehr verwirrt!", false);
				return null;
			}
			this.gController.getGameFrame().getFightPanel().addText(this.name + " ist verwirrt!");
			if (rng.nextFloat() < (1 / 3.0)) {
				this.gController.getFight().selfAttack(this);
				return "Es hat sich vor Verwirrung selbst verletzt!";
			}
		default:
			break;
		}
		since++;
		return null;
	}

	public void afterTurnDamage() {
		switch (this.ailment) {
		case BURN:
			this.stats.loseHP((int) (this.stats.getStats()[0] * (1 / 16.0)));
			break;
		case POISON:
			this.stats.loseHP((int) (this.stats.getStats()[0] * (1 / 8.0)));
			break;
		default:
			break;
		}
		gController.getGameFrame().getFightPanel().updatePanels();
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("id", this.id);
		data.add("stats", this.stats.getSaveData());
		JsonArray moveData = new JsonArray();
		for (int i = 0; i < getAmmountOfMoves(); i++) {
			moveData.add(this.moves[i].getSaveData());
		}
		data.add("moves", moveData);
		data.addProperty("ailment", this.ailment.name());
		return data;
	}

	public static Pokemon importSaveData(JsonObject saveData) {
		Pokemon result = new Pokemon(saveData.get("id").getAsInt());
		result.getStats().importSaveData(saveData.get("stats").getAsJsonObject());
		result.setMoves(new Move[4]);
		for (int i = 0; i < Math.min(result.getMoves().length, saveData.get("moves").getAsJsonArray().size()); i++) {
			result.addMove(Move.importSaveData(saveData.get("moves").getAsJsonArray().get(i).getAsJsonObject()));
		}
		return result;
	}

	public void setName(String string) {
		this.name = string;
	}
}

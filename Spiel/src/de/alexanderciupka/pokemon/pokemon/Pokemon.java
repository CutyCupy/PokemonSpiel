package de.alexanderciupka.pokemon.pokemon;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import javax.swing.ImageIcon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.PokemonNames;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.AnimationLabel;
import de.alexanderciupka.pokemon.gui.panels.FightPanel;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;

public class Pokemon {

	private int id;
	private String name;
	private Image spriteFront;
	private Image spriteBack;
	private GameController gController;

	private Stats stats;

	private Move[] moves;

	private Type[] types;
	private Ability ability;
	private Ailment ailment;
	private ArrayList<SecondaryAilment> secondaryAilments;

	private int catchRate;
	private int weight;
	private int height;
	private int baseExperience;
	private HashMap<Stat, Short> evBonus;

	private Random rng;

	private boolean nameChanged;
	private int evolves;
	private int happiness;
	private String growthRate;

	private Gender gender;
	private boolean shiny;

	private String uniqueID = UUID.randomUUID().toString();

	public Pokemon(int id) {
		this.id = id;
		this.gController = GameController.getInstance();
		this.name = this.gController.getInformation().getName(id);
		this.types = this.gController.getInformation().getTypes(id);
		this.shiny = new Random().nextFloat() < 0.01;
		this.gender = this.gController.getInformation().getGender(this.id);
		this.moves = new Move[4];
		this.ailment = Ailment.NONE;
		this.happiness = this.gController.getInformation().getBaseHappiness(this.id);
		this.rng = new Random();
		this.weight = this.gController.getInformation().getWeight(id);
		this.height = this.gController.getInformation().getHeight(id);
		this.evBonus = this.gController.getInformation().getEvBonus(id);
		this.growthRate = this.gController.getInformation().getGrowthRate(id);
		this.catchRate = this.gController.getInformation().getCaptureRate(id);
		this.setBaseExperience(this.gController.getInformation().getBaseExperience(id));
		this.setAbility(this.gController.getInformation().getAbility(id, false));
		this.stats = new Stats(this);

		this.updateSprites();
	}

	public Pokemon(String name) {
		this.name = name;
		this.gController = GameController.getInstance();
		this.id = this.gController.getInformation().getID(name);
		this.name = this.gController.getInformation().getName(this.id);
		this.types = this.gController.getInformation().getTypes(this.id);
		this.shiny = new Random().nextFloat() < 0.01;
		this.gender = this.gController.getInformation().getGender(this.id);
		this.moves = new Move[4];
		this.ailment = Ailment.NONE;
		this.rng = new Random();
		this.weight = this.gController.getInformation().getWeight(this.id);
		this.height = this.gController.getInformation().getHeight(this.id);
		this.evBonus = this.gController.getInformation().getEvBonus(this.id);
		this.growthRate = this.gController.getInformation().getGrowthRate(this.id);
		this.catchRate = this.gController.getInformation().getCaptureRate(this.id);
		this.setBaseExperience(this.gController.getInformation().getBaseExperience(this.id));
		this.setAbility(this.gController.getInformation().getAbility(this.id, false));
		this.stats = new Stats(this);

		this.updateSprites();
	}

	public Stats getStats() {
		return this.stats;
	}

	public boolean gainXP(int gain) {
		return this.stats.gainXP(gain);
	}

	public Move[] getMoves() {
		return this.moves;
	}

	public Move getMoveByName(String name) {
		for (Move m : this.moves) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}

	public int getAmmountOfMoves() {
		for (int i = 0; i < this.moves.length; i++) {
			if (this.moves[i] == null) {
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
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Image getSpriteFront() {
		return this.spriteFront;
	}

	public Image getSpriteBack() {
		return this.spriteBack;
	}

	public void startFight() {
		this.stats.startFight();
		this.secondaryAilments = new ArrayList<>();
	}

	public Move getRandomMove() {
		Random rng = new Random();
		int counter = 0;
		for (Move m : this.moves) {
			if (m != null && m.getPp() > 0) {
				counter++;
			}
		}
		if (counter == 0) {
			return this.gController.getPokemonInformation().getMoveByName("Verzweifler");
		}
		return this.moves[rng.nextInt(counter)];
	}

	public void addMove(String moveName) {
		for (int i = 0; i < 4; i++) {
			if (this.moves[i] == null) {
				this.moves[i] = this.gController.getInformation().getMoveByName(moveName).clone();
				return;
			}
		}
	}

	private void addMove(Move move) {
		this.addMove(move.getName());
	}

	public boolean addMove(String currentMove, Move replacementMove) {
		for (int i = 0; i < 4; i++) {
			if (this.moves[i].getName().equals(currentMove)) {
				this.moves[i] = replacementMove;
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

	public Type[] getTypes() {
		if (this.gController.isFighting()) {
			if (PokemonNames.FORMEO == this.getId()) {
				switch (this.gController.getFight().getField().getWeather()) {
				case HAIL:
					return new Type[] { Type.ICE, null };
				case RAIN:
					return new Type[] { Type.WATER, null };
				case SANDSTORM:
					return new Type[] { Type.ROCK, null };
				case SUN:
					return new Type[] { Type.FIRE, null };
				default:
					break;
				}
			}
		}
		return this.types;
	}

	public boolean evolve(int newID) {
		if (newID != 0 && this.evolves == 0) {
			this.evolves = newID;
			this.gController.getGameFrame().getEvolutionPanel().addPokemon(this);
			return true;
		}
		return false;
	}

	private void update() {
		if (!this.nameChanged) {
			this.name = this.gController.getInformation().getName(this.id);
		}
		this.spriteFront = new ImageIcon(this.getClass().getResource("/pokemon/front/" + this.id + ".png")).getImage();
		this.spriteBack = new ImageIcon(this.getClass().getResource("/pokemon/back/" + this.id + ".png")).getImage();
		this.types = this.gController.getPokemonInformation().getTypes(this.id);
		this.stats.setBaseStats(this.gController.getInformation().getBaseStats(this.id));
	}

	public void restoreMoves() {
		for (Move move : this.moves) {
			if (move != null) {
				move.setCurrentPP(move.getPp());
			}
		}
	}

	public Move getMove(Pokemon player) {
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for (Move m : this.moves) {
			if (m != null && m.equals(this.gController.getFight().canUse(this, m))) {
				possibleMoves.add(m);
			}
		}
		if (possibleMoves.size() == 0) {
			return null;
		}
		if (this.gController.getFight().canEscape()) {
			return possibleMoves.get(this.rng.nextInt(possibleMoves.size()));
		} else {
			double highscore = -1;
			ArrayList<Integer> index = new ArrayList<Integer>();
			for (int i = 0; i < possibleMoves.size(); i++) {
				if (possibleMoves.get(i).getPower() <= 0) {
					if (highscore <= 0) {
						index.add(i);
					}
					continue;
				}
				double current = Type.getEffectiveness(possibleMoves.get(i).getMoveType(), player);
				if (current > highscore) {
					index.clear();
					index.add(i);
					highscore = current;
				} else if (current == highscore) {
					index.add(i);
				}
			}
			if (index.isEmpty()) {
				return possibleMoves.get(this.rng.nextInt(possibleMoves.size()));
			}
			return possibleMoves.get(index.get(this.rng.nextInt(index.size())));
		}
	}

	public Ailment getAilment() {
		return this.ailment;
	}

	public boolean setAilment(Ailment ailment) {
		if (Ailment.NONE.equals(this.ailment) || ailment.equals(Ailment.NONE) || ailment.equals(Ailment.FAINTED)) {
			switch (ailment) {
			case BURN:
			case FREEZE:
			case HEAVY_POISON:
			case PARALYSIS:
			case POISON:
			case SLEEP:
				if (this.gController.isFighting()) {
					switch (this.gController.getFight().getField().getWeather()) {
					case SUN:
						if (Abilities.FLORASCHILD == this.getAbility().getId()) {
							this.gController.getGameFrame().getFightPanel().addText(
									this.getName() + " wird durch " + Abilities.FLORASCHILD + " beschützt!", true);
							return false;
						}
					default:
						break;
					}
				}
				break;
			default:
				break;

			}
			this.ailment = ailment;
			return true;
		}
		return false;
	}

	public boolean addSecondaryAilment(SecondaryAilment ailment) {
		if (!this.secondaryAilments.contains(ailment)) {
			FightPanel fp = this.gController.getGameFrame().getFightPanel();
			boolean success = true;
			String message = ailment.getOnHit().replace("@pokemon", this.getName());
			switch (ailment) {
			case DISABLE:
				Move disabled = this.gController.getFight().getLastMove(this);
				if (disabled != null) {
					disabled.setDisabled(true);
					message = message.replace("@move", disabled.getName());
				}
				break;
			case INFATUATION:
				Pokemon source = this.equals(this.gController.getFight().getEnemy())
						? this.gController.getFight().getPlayer()
						: this.gController.getFight().getEnemy();
				message = message.replace("@enemy", source.getName());
				switch (source.getGender()) {
				case FEMALE:
					switch (this.gender) {
					case MALE:
						success = true;
						break;
					default:
						success = false;
						break;
					}
					break;
				case GENDERLESS:
					success = false;
					break;
				case MALE:
					switch (this.gender) {
					case FEMALE:
						success = true;
						break;
					default:
						success = false;
						break;
					}
					break;
				default:
					success = false;
					break;
				}
				break;
			case NIGHTMARE:
				success = this.ailment == Ailment.SLEEP;
				break;
			default:
				break;
			}
			if (success) {
				fp.addText(message);
				ailment.inflict();
				this.secondaryAilments.add(ailment);
				return true;
			} else {
				fp.addText("Es wird keine Wirkung haben!");
			}
		}
		return false;
	}

	public String canAttack() {
		AnimationLabel animation = this.equals(this.gController.getFight().getPlayer())
				? this.gController.getGameFrame().getFightPanel().getPlayerAnimation()
				: this.gController.getGameFrame().getFightPanel().getEnemyAnimation();
		switch (this.ailment) {
		case FREEZE:
			if (this.rng.nextFloat() < 0.2f) {
				this.ailment = Ailment.NONE;
				this.gController.getGameFrame().getFightPanel().updatePanels();
				return this.name + " ist wieder aufgetaut!";
			} else {
				animation.playAnimation("eingefroren");
			}
			return this.name + " kann sich nicht bewegen!";
		case PARALYSIS:
			if (this.rng.nextFloat() < (1 / 3.0)) {
				animation.playAnimation("paralyse");
				return this.name + " ist paralysiert und kann sich nicht bewegen!";
			}
			break;
		case SLEEP:
			if (this.rng.nextFloat() < 0.25f) {
				this.ailment = Ailment.NONE;
				this.gController.getGameFrame().getFightPanel().updatePanels();
				return this.name + " ist wieder aufgewacht!";
			} else {
				animation.playAnimation("schlafen");
			}
			return this.name + " schläft tief und fest!";
		default:
			break;
		}
		for (SecondaryAilment ailment : this.secondaryAilments) {
			switch (ailment) {
			case CONFUSION:
				if (this.rng.nextFloat() < 0.2f
						* (this.gController.getFight().getTurn() - ailment.getInflictedTurn())) {
					this.ailment = Ailment.NONE;
					this.gController.getGameFrame().getFightPanel().updatePanels();
					this.gController.getGameFrame().getFightPanel().addText(ailment.getHealed(), false);
					return null;
				}
				animation.playAnimation("verwirrt");
				this.gController.getGameFrame().getFightPanel().addText(this.name + " ist verwirrt!");
				if (this.rng.nextFloat() < (1 / 3.0)) {
					this.gController.getFight().selfAttack(this);
					return "Es hat sich vor Verwirrung selbst verletzt!";
				}
				break;
			case INFATUATION:
				if (this.rng.nextFloat() < 0.5) {
					animation.playAnimation("verliebt");
					return ailment.getAffected().replace("@pokemon", this.getName());
				}
				break;
			case FLINCH:
				return ailment.getAffected().replace("@pokemon", this.getName());
			default:
				break;
			}
		}
		return null;
	}

	public void afterTurnDamage() {
		FightPanel fightPanel = this.gController.getGameFrame().getFightPanel();
		AnimationLabel animation = this.equals(this.gController.getFight().getPlayer())
				? fightPanel.getPlayerAnimation()
				: fightPanel.getEnemyAnimation();
		switch (this.ailment) {
		case BURN:
			animation.playAnimation("verbrennung");
			this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 16.0)));
			fightPanel.addText(this.name + " wurde durch die Verbrennung verletzt!", true);
			break;
		case POISON:
			animation.playAnimation("gift");
			this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 8.0)));
			fightPanel.addText(this.name + " wurde durch die Vergiftung verletzt!", true);
			break;
		default:
			break;
		}
		int turn = this.gController.getFight().getTurn();
		for (int i = 0; i < this.secondaryAilments.size(); i++) {
			SecondaryAilment ailment = this.secondaryAilments.get(i);
			if (ailment.isWearOff()) {
				if (turn >= ailment.getInflictedTurn() + ailment.getMinTurns()) {
					if (turn >= ailment.getInflictedTurn() + ailment.getMaxTurns() || this.rng
							.nextFloat() > (ailment.getMinTurns() / (Math.max(ailment.getMaxTurns() * 1.0, 1)))
									* ((turn - ailment.getInflictedTurn()) - ailment.getMinTurns())) {
						if (ailment.getHealed() != null) {
							fightPanel.addText(ailment.getHealed().replace("@pokemon", this.getName()), true);
						}
						this.secondaryAilments.remove(i);
						i--;
						continue;
					}
				}
			}
			switch (ailment) {
			case INGRAIN:
				if (this.stats.restoreHP((int) (this.stats.getStats().get(Stat.HP) / 8.0)) > 0) {
					fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()), true);
				}
				break;
			case LEECHSEED:
				animation.playAnimation("absorber");
				int gain = this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) / 8.0));
				if (this.gController.getFight().getEnemy().equals(this)) {
					this.gController.getFight().getPlayer().getStats().restoreHP(gain);
				} else {
					this.gController.getFight().getEnemy().getStats().restoreHP(gain);
				}
				fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()), true);
				break;
			case NIGHTMARE:
				if (this.getAilment() == Ailment.SLEEP) {
					this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) / 4.0));
					fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()), true);
				} else {
					this.gController.getGameFrame().getFightPanel()
							.addText(ailment.getHealed().replace("@pokemon", this.getName()), true);
					this.secondaryAilments.remove(i);
					i--;
				}
				break;
			case TRAP:
				this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) / 8.0));
				fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()));
			case PERISHSONG:
				break;
			case YAWN:
				if (ailment.getInflictedTurn() + 1 <= turn) {
					if (this.setAilment(Ailment.SLEEP)) {
						fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()), true);
					}
				}
				break;
			default:
				break;
			}
		}
		fightPanel.updatePanels();
	}

	public void afterWalkingDamage() {
		switch (this.ailment) {
		case POISON:
			if (this.stats.getCurrentHP() > 1) {
				this.stats.loseHP(1);
			} else if (this.stats.getCurrentHP() == 1) {
				this.setAilment(Ailment.NONE);
				this.gController.getGameFrame().addDialogue(this.getName() + " hat sich von der Vergiftung erholt!");
			} else {
				this.setAilment(Ailment.FAINTED);
			}
			break;
		default:
			break;
		}
		this.gController.waitDialogue();
	}

	public JsonObject getSaveData() {
		JsonObject data = new JsonObject();
		data.addProperty("id", this.id);
		data.add("stats", this.stats.getSaveData());
		JsonArray moveData = new JsonArray();
		for (int i = 0; i < this.getAmmountOfMoves(); i++) {
			moveData.add(this.moves[i].getSaveData());
		}
		data.add("moves", moveData);
		data.addProperty("ailment", this.ailment.name());
		data.addProperty("name_changed", this.nameChanged);
		data.addProperty("gender", this.gender.name());
		data.addProperty("happiness", this.happiness);
		return data;
	}

	public static Pokemon importSaveData(JsonObject saveData) {
		Pokemon result = new Pokemon(saveData.get("id").getAsInt());
		result.getStats().importSaveData(saveData.get("stats").getAsJsonObject());
		result.setMoves(new Move[4]);
		for (int i = 0; i < Math.min(result.getMoves().length, saveData.get("moves").getAsJsonArray().size()); i++) {
			result.addMove(Move.importSaveData(saveData.get("moves").getAsJsonArray().get(i).getAsJsonObject()));
		}
		result.setNameChanged(
				saveData.get("name_changed") != null ? saveData.get("name_changed").getAsBoolean() : false);
		result.setAilment(Ailment.valueOf(saveData.get("ailment").getAsString()));
		result.setGender(Gender.valueOf(saveData.get("gender").getAsString().toUpperCase()));
		result.happiness = saveData.get("happiness").getAsInt();
		result.updateSprites();
		return result;
	}

	private void updateSprites() {
		this.spriteFront = this.gController.getInformation().getFrontSprite(this.id, this.gender, this.shiny);
		this.spriteBack = this.gController.getInformation().getBackSprite(this.id, this.gender, this.shiny);
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void setName(String string) {
		this.nameChanged = true;
		this.name = string;
	}

	public int getCatchRate() {
		return this.catchRate;
	}

	public boolean isCatched(Item usedBall) {
		if (usedBall.equals(Item.MASTERBALL)) {
			return true;
		}
		int Z = this.getStats().getRNG().nextInt(usedBall.getValue() + 1);
		int ailmentValue = 0;
		switch (this.getAilment()) {
		case BURN:
		case POISON:
		case PARALYSIS:
			ailmentValue = 12;
			break;
		case FREEZE:
		case SLEEP:
			ailmentValue = 25;
			break;
		default:
			break;
		}
		int F = (this.getStats().getStats().get(Stat.HP) * 255) / (usedBall == Item.SUPERBALL ? 8 : 12);
		if (this.getStats().getCurrentHP() / 4 > 0) {
			F = Math.min(255, F / (this.getStats().getCurrentHP() / 4));
		}
		if (Z - ailmentValue <= this.getCatchRate()) {
			int M = this.getStats().getRNG().nextInt(256);
			if (M <= F) {
				return true;
			}
		}
		return false;
	}

	public int getShakes(Item usedBall) {
		return this.rng.nextInt(4);
	}

	public int getBaseExperience() {
		return this.baseExperience;
	}

	public void setBaseExperience(int baseExperience) {
		this.baseExperience = baseExperience;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public HashMap<Stat, Short> getEVBonus() {
		return this.evBonus;
	}

	public void increaseEV(Pokemon enemy) {
		for (Stat s : Stat.values()) {
			if (s.equals(Stat.ACCURACY) || s.equals(Stat.EVASION)) {
				continue;
			}
			this.stats.increaseEV(s, enemy.getEVBonus().get(s));
		}
	}

	public boolean isNameChanged() {
		return this.nameChanged;
	}

	public void setNameChanged(boolean value) {
		this.nameChanged = value;
	}

	public String getGrowthRate() {
		return this.growthRate;
	}

	public boolean useItem(Player source, Item i) {
		if (this.secondaryAilments != null && this.secondaryAilments.contains(SecondaryAilment.EMBARGO)) {
			this.gController.getGameFrame()
					.addDialogue(SecondaryAilment.EMBARGO.getAffected().replace("@pokemon", this.getName()));
			return false;
		}
		if (i.isUsableOnPokemon()) {
			boolean effective = false;
			switch (i) {
			case REVIVE:
			case MAXREVIVE:
				if (this.getAilment() == Ailment.FAINTED) {
					effective = true;
					this.getStats().restoreHP((int) (this.getStats().getStats().get(Stat.HP) * (i.getValue() / 100.0)));
					this.setAilment(Ailment.NONE);
					this.gController.getGameFrame().addDialogue(this.getName() + " wurde wiederbelebt!");
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				}
				break;
			case FULLRESTORE:
				if (this.getAilment() != Ailment.NONE) {
					effective = true;
					this.gController.getGameFrame().addDialogue(
							this.getName() + " ist nicht mehr " + Ailment.getText(this.getAilment()) + "!");
					this.setAilment(Ailment.NONE);
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				}
			case POTION:
			case SUPERPOTION:
			case HYPERPOTION:
			case FULLHEAL:
				int restore = this.stats.restoreHP(i.getValue());
				if (restore > 0) {
					if (!effective) {
						SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
					}
					effective = true;
					this.gController.getGameFrame()
							.addDialogue(this.getName() + " wurde um " + restore + " KP geheilt!");
				}
				break;
			case BURNHEAL:
			case PARAHEAL:
			case FREEZEHEAL:
			case POISONHEAL:
			case SLEEPHEAL:
				if (this.getAilment() == i.getAilment()) {
					effective = true;
					this.gController.getGameFrame().addDialogue(
							this.getName() + " ist nicht mehr " + Ailment.getText(this.getAilment()) + "!");
					this.setAilment(Ailment.NONE);
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				}
				break;
			case HYPERHEAL:
			case ZWIEBACKNUTELLA:
				if (this.getAilment() != Ailment.NONE) {
					effective = true;
					this.gController.getGameFrame().addDialogue(
							this.getName() + " ist nicht mehr " + Ailment.getText(this.getAilment()) + "!");
					this.setAilment(Ailment.NONE);
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				}
				break;
			case RARECANDY:
				if (this.getStats().levelUP()) {
					effective = true;
					this.getStats().setCurrentXP(0);
				}
				break;

			case DAWNSTONE:
			case DUSKSTONE:
			case FIRESTONE:
			case LEAFSTONE:
			case MOONSTONE:
			case SHINYSTONE:
			case SUNSTONE:
			case THUNDERSTONE:
			case WATERSTONE:
				if (this.evolve(this.gController.getInformation().checkEvolution(this, i))) {
					effective = true;
					this.gController.getGameFrame()
							.addDialogue(this.getName() + " reagiert auf den " + i.getName() + "!");
				}
				break;
			case CALCIUM:
			case CARBON:
			case HPUP:
			case PROTEIN:
			case IRON:
			case ZINC:
				Stat s = i.getIncrease();
				if (this.getStats().getEv(s) < 100) {
					effective = true;
					this.gController.getGameFrame().addDialogue(i.getName() + " hat " + s.getArticle() + " "
							+ s.getText() + " von " + this.getName() + " erhöht!");
					this.stats.increaseEV(s, (short) 10);
					if (this.getHappiness() < 100) {
						this.changeHappiness(5);
					} else if (this.getHappiness() > 200) {
						this.changeHappiness(3);
					} else {
						this.changeHappiness(2);
					}
					SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				}
				break;
			default:
				break;
			}
			if (effective) {
				this.getStats().updateStats();
				source.removeItem(i);
				return true;
			}
			this.gController.getGameFrame().addDialogue("Es wird keine Wirkung haben!");
			return false;
		} else {
			throw new IllegalArgumentException("Given Item must be usable on Pokemon!");
		}
	}

	public int getEvolves() {
		return this.evolves;
	}

	public boolean isShiny() {
		return this.shiny;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender g) {
		this.gender = g;
	}

	public int getHappiness() {
		return this.happiness;
	}

	public void changeHappiness(int value) {
		this.happiness = Math.min(255, Math.max(0, this.happiness + value));
	}

	public void startEvolution() {
		if (this.evolves != 0) {
			this.id = this.evolves;
			this.evolves = 0;
			this.update();
			this.stats.newMoves();
		}
	}

	public boolean knowsMove(Move newMove) {
		for (int i = 0; i < this.getAmmountOfMoves(); i++) {
			if (newMove.equals(this.getMoves()[i])) {
				return true;
			}
		}
		return false;
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Pokemon ? ((Pokemon) obj).uniqueID.equals(this.uniqueID) : false;
	}

	public ArrayList<SecondaryAilment> getSecondaryAilments() {
		return this.secondaryAilments;
	}

	public Ability getAbility() {
		return this.ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	public boolean hasType(Type t) {
		return t.equals(this.types[0]) || t.equals(this.types[1]);
	}

	/**
	 * Ability stuff
	 */

	public void onBattlestart(Pokemon foe, Fighting fight) {

	}

}

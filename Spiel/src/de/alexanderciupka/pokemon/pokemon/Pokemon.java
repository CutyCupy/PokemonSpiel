package de.alexanderciupka.pokemon.pokemon;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import javax.swing.ImageIcon;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.Fighting;
import de.alexanderciupka.pokemon.gui.AnimationLabel;
import de.alexanderciupka.pokemon.gui.panels.FightPanel;
import de.alexanderciupka.pokemon.main.Main;
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
	private Ability fightingAbility;
	private int fightingSince;

	private Ailment ailment;
	private HashMap<SecondaryAilment, Integer> secondaryAilments;

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

	private Integer item = Items.KEINS;

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
		System.err.println("STARTFIGHT - " + this.getName());
		this.stats.startFight();
		this.secondaryAilments = new HashMap<>();
		this.fightingAbility = new Ability(Abilities.KEINE, "", "");
		try {
			this.fightingSince = this.gController.getFight().getTurn();
		} catch (Exception e) {
			this.fightingSince = 0;
		}
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
			if (this.getAbility().getId() == Abilities.PROGNOSE) {
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
				double current = Type.getEffectiveness(possibleMoves.get(i).getMoveType(this), player);
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
				switch (this.getAbility().getId()) {
				case Abilities.AQUAHÜLLE:
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " verhindert die Verbrennung!", true);
					return false;
				}
			case FREEZE:
				switch (this.getAbility().getId()) {
				case Abilities.MAGMAPANZER:
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " verhindert das Einfrieren!");
				}
				break;
			case PARALYSIS:
				switch (this.getAbility().getId()) {
				case Abilities.FEUCHTIGKEIT:
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " verhindert die Paralyse!", true);
					return false;
				}
				break;
			case HEAVY_POISON:
			case POISON:
				switch (this.getAbility().getId()) {
				case Abilities.IMMUNITÄT:
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getName() + " kann nicht vergiftet werden!");
					return false;
				}
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
					switch (this.getAbility().getId()) {
					case Abilities.INSOMNIA:
					case Abilities.MUNTERKEIT:
						this.gController.getGameFrame().getFightPanel()
								.addText(this.getName() + " kann nicht einschlafen!");
						return false;
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

	public int getFightingSince() {
		return this.fightingSince;
	}

	public boolean addSecondaryAilment(int sourceIndex, SecondaryAilment ailment) {
		if (!this.secondaryAilments.keySet().contains(ailment)) {
			FightPanel fp = this.gController.getGameFrame().getFightPanel();
			boolean success = true;
			String message = ailment.getOnHit().replace("@pokemon", this.getName());
			switch (ailment) {
			case DISABLE:
				System.err.println("disabled");
				Move disabled = this.gController.getFight().getLastMove(this);
				if (disabled != null) {
					disabled.setDisabled(true);
					message = message.replace("@move", disabled.getName());
				} else {
					success = false;
				}
				break;
			case INFATUATION:
				Pokemon source = this.gController.getFight().getPokemon(sourceIndex);
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
				switch (this.getAbility().getId()) {
				case Abilities.DÖSIGKEIT:
					success = false;
					break;
				}
				break;
			case NIGHTMARE:
				success = this.ailment == Ailment.SLEEP;
				break;
			case CONFUSION:
				if (this.getAbility().getId() == Abilities.TEMPOMACHER) {
					fp.addText("Tempomacher verhindert die Verwirrung von " + this.getName() + "!");
					return false;
				}
			case FLINCH:
				switch (this.getAbility().getId()) {
				case Abilities.KONZENTRATOR:
					fp.addText("Konzentrator verhindert das Zurückschrecken!");
					return false;
				}
			default:
				break;
			}
			if (success) {
				fp.addText(message);
				ailment.inflict();
				this.secondaryAilments.put(ailment, sourceIndex);
				return true;
			} else {
				fp.addText("Es wird keine Wirkung haben!");
			}
		}
		return false;
	}

	public String canAttack() {
		AnimationLabel animation = this.gController.getGameFrame().getFightPanel().getPokemonLabel(this)
				.getAnimationLabel();
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
			float p = Main.RNG.nextFloat();
			switch (this.getAbility().getId()) {
			case Abilities.FRÜHWECKER:
				p *= 2;
				break;
			}
			if (p < 0.25f) {
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
		for (SecondaryAilment ailment : this.secondaryAilments.keySet()) {
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
				switch (this.getAbility().getId()) {
				case Abilities.FELSENFEST:
					this.getStats().increaseStat(Stat.SPEED, 1);
					break;
				}
				return ailment.getAffected().replace("@pokemon", this.getName());
			default:
				break;
			}
		}
		switch (this.getAbility().getId()) {
		case Abilities.SCHNARCHNASE:
			if ((this.gController.getFight().getTurn() - fightingSince) % 2 == 0) {
				return this.getName() + " ruht sich aus!";
			}
		}
		return null;
	}

	public void afterTurnDamage() {
		FightPanel fightPanel = this.gController.getGameFrame().getFightPanel();
		AnimationLabel animation = fightPanel.getPokemonLabel(this).getAnimationLabel();

		switch (this.getAbility().getId()) {
		case Abilities.EXPIDERMIS:
			if (Main.RNG.nextFloat() < 0.3) {
				this.setAilment(Ailment.NONE);
				fightPanel.addText(this.getAbility().getName() + " heilt " + this.getName() + "!");
			}
			break;
		case Abilities.HYDRATION:
			if (this.getAilment() != Ailment.NONE) {
				this.setAilment(Ailment.NONE);
				SoundController.getInstance().playSound(SoundController.ITEM_HEAL);
				GameController.getInstance().getGameFrame().getFightPanel()
						.addText(this.getName() + " wurde durch den Regen geheilt!", true);
			}
		}

		fightPanel.updatePanels();

		if (this.getAbility().getId() != Abilities.MAGIESCHILD) {
			switch (this.ailment) {
			case BURN:
				switch (this.getAbility().getId()) {
				case Abilities.AQUAHÜLLE:
					this.setAilment(Ailment.NONE);
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " heilt die Verbrennung!", true);
					break;
				default:
					animation.playAnimation("verbrennung");
					if (this.getAbility().getId() == Abilities.HITZESCHUTZ) {
						this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 32.0)));
					} else {
						this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 16.0)));
					}
					fightPanel.addText(this.name + " wurde durch die Verbrennung verletzt!", true);
					break;
				}
				break;
			case FREEZE:
				switch (this.getAbility().getId()) {
				case Abilities.MAGMAPANZER:
					this.setAilment(Ailment.NONE);
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " schmilzt das Eis!");
					break;
				}
				break;
			case PARALYSIS:
				switch (this.getAbility().getId()) {
				case Abilities.FEUCHTIGKEIT:
					this.setAilment(Ailment.NONE);
					this.gController.getGameFrame().getFightPanel()
							.addText(this.getAbility().getName() + " heilt die Paralyse!", true);
					break;
				}
				break;
			case HEAVY_POISON:
			case POISON:
				switch (this.getAbility().getId()) {
				case Abilities.AUFHEBER:
					animation.playAnimation("heilung");
					this.stats.restoreHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 8.0)));
					fightPanel.addText(this.name + " heilt sich durch die Vergiftung!", true);
					break;
				default:
					animation.playAnimation("gift");
					double factor = 0;
					switch (this.ailment) {
					case HEAVY_POISON:
						factor = (this.gController.getFight().getTurn()
								- Math.max(this.ailment.getInflictedTurn(), this.fightingSince) + 1) * (1 / 16.0);
						break;
					default:
						factor = 1 / 8.0;
						break;
					}
					this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) * factor));
					fightPanel.addText(this.name + " wurde durch die Vergiftung verletzt!", true);
					break;
				}
				break;
			case SLEEP:
				if (gController.isFighting()) {
					Fighting fight = gController.getFight();
					for (Pokemon p : fight.getSpeedOrder()) {
						if (fight.isPlayer(p) != fight.isPlayer(this) && p.getAbility().getId() == Abilities.ALPTRAUM) {
							animation.playAnimation("gift");
							this.getStats().loseHP((int) (this.stats.getStats().get(Stat.HP) * (1 / 8.0)));
							fightPanel.addText(this.getName() + " erlebt einen Alptraum!", true);
						}
					}
				}
				break;
			default:
				break;
			}
		}
		int turn = this.gController.getFight().getTurn();
		ArrayList<SecondaryAilment> toRemove = new ArrayList<>();
		for (SecondaryAilment ailment : this.secondaryAilments.keySet()) {
			if (ailment.isWearOff()) {
				if (turn >= ailment.getInflictedTurn() + ailment.getMinTurns()) {
					if (turn >= ailment.getInflictedTurn() + ailment.getMaxTurns() || this.rng
							.nextFloat() > (ailment.getMinTurns() / (Math.max(ailment.getMaxTurns() * 1.0, 1)))
									* ((turn - ailment.getInflictedTurn()) - ailment.getMinTurns())) {
						if (ailment.getHealed() != null) {
							fightPanel.addText(ailment.getHealed().replace("@pokemon", this.getName()), true);
						}
						toRemove.add(ailment);
					}
				}
			} else if (this.getAbility().getId() != Abilities.MAGIESCHILD) {
				switch (ailment) {
				case INGRAIN:
					if (this.stats.restoreHP((int) (this.stats.getStats().get(Stat.HP) / 8.0)) > 0) {
						fightPanel.addText(ailment.getAffected().replace("@pokemon", this.getName()), true);
					}
					break;
				case LEECHSEED:
					animation.playAnimation("absorber");
					int gain = this.stats.loseHP((int) (this.stats.getStats().get(Stat.HP) / 8.0));
					if (this.gController.getFight().getPokemon(this.secondaryAilments.get(ailment)) != null) {
						switch (this.getAbility().getId()) {
						case Abilities.KLOAKENSOSSE:
							this.gController.getFight().getPokemon(this.secondaryAilments.get(ailment)).getStats()
									.loseHP(gain);
							break;
						default:
							this.gController.getFight().getPokemon(this.secondaryAilments.get(ailment)).getStats()
									.restoreHP(gain);
							break;
						}
					} else {
						if (this.gController.getFight().isPlayer(this.secondaryAilments.get(ailment))) {
							if (this.secondaryAilments.get(ailment) == Fighting.LEFT_PLAYER) {
								this.gController.getFight().getPokemon(Fighting.RIGHT_PLAYER).getStats()
										.restoreHP(gain);
							} else {
								this.gController.getFight().getPokemon(Fighting.LEFT_PLAYER).getStats().restoreHP(gain);
							}
						} else {
							if (this.secondaryAilments.get(ailment) == Fighting.LEFT_OPPONENT) {
								this.gController.getFight().getPokemon(Fighting.RIGHT_OPPONENT).getStats()
										.restoreHP(gain);
							} else {
								this.gController.getFight().getPokemon(Fighting.LEFT_OPPONENT).getStats()
										.restoreHP(gain);
							}
						}
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
						toRemove.add(SecondaryAilment.NIGHTMARE);
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
				case DISABLE:
					for (Move m : this.getMoves()) {
						if (m != null) {
							if (m.isDisabled() && m.getDisabledTurns() >= 3) {
								m.setDisabled(false);
								toRemove.add(SecondaryAilment.DISABLE);
								fightPanel.addText(ailment.getHealed().replace("@pokemon", this.getName())
										.replace("@move", m.getName()), true);
								break;
							}
						}
					}
					break;
				default:
					break;
				}
			}
			fightPanel.updatePanels();
		}

		for (SecondaryAilment a : toRemove) {
			this.secondaryAilments.remove(a);
		}

		switch (this.getAbility().getId()) {
		case Abilities.TEMPOSCHUB:
			this.stats.increaseStat(Stat.SPEED, 1);
			break;
		case Abilities.HEILHERZ:
			Pokemon partner = this.gController.getFight().getPokemon(this.gController.getFight().getPartner(this));
			if (Main.RNG.nextFloat() < .3 && partner != null) {
				switch (partner.getAilment()) {
				case FAINTED:
				case NONE:
					break;
				default:
					partner.setAilment(Ailment.NONE);
					fightPanel.addText(partner.getName() + " wurde durch " + this.getName()
							+ " von seinem Statusproblem geheilt!");
					break;
				}
			}
		case Abilities.GEFÜHLSWIPPE:
			ArrayList<Stat> increase = new ArrayList<>();
			ArrayList<Stat> decrease = new ArrayList<>();
			for (Stat s : this.getStats().getFightStats().keySet()) {
				if (this.getStats().getStatChange(s) < 6) {
					increase.add(s);
				}
				if (this.getStats().getStatChange(s) > -6) {
					decrease.add(s);
				}
			}
			if (!(increase.isEmpty() && decrease.isEmpty())) {
				this.gController.getGameFrame().getFightPanel()
						.addText(this.getAbility().getName() + " " + "von " + this.getName() + " aktiviert sich!");
				if (!increase.isEmpty()) {
					this.getStats().increaseStat(increase.get(Main.RNG.nextInt(increase.size())), 2);
				}
				if (!decrease.isEmpty()) {
					this.getStats().decreaseStat(decrease.get(Main.RNG.nextInt(decrease.size())), 1);
				}
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
		data.addProperty("item", this.getItem());
		data.addProperty("ability", this.ability.getId());
		return data;
	}

	public static Pokemon importSaveData(JsonObject saveData) {
		Pokemon result = new Pokemon(saveData.get("id").getAsInt());
		result.getStats().importSaveData(saveData.get("stats").getAsJsonObject());
		if(saveData.has("moves")) {
			result.setMoves(new Move[4]);
			for (int i = 0; i < Math.min(result.getMoves().length, saveData.get("moves").getAsJsonArray().size()); i++) {
				result.addMove(Move.importSaveData(saveData.get("moves").getAsJsonArray().get(i).getAsJsonObject()));
			}
		}
		if (saveData.has("name")) {
			result.setNameChanged(saveData.get("name").getAsJsonObject().get("changed").getAsBoolean());
		}
		result.setAilment(Ailment.valueOf(saveData.get("ailment").getAsString()));
		if(saveData.has("gender")) {
			result.setGender(Gender.valueOf(saveData.get("gender").getAsString().toUpperCase()));
		}
		if(saveData.has("happiness")) {
			result.happiness = saveData.get("happiness").getAsInt();
		}
		if(saveData.has("item")) {
			result.setItem(saveData.get("item").getAsInt());
		}
		result.updateSprites();
		return result;
	}

	public void setItem(int id) {
		this.item = id;
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

	public int isCatched(Integer usedBall) {

		double fangquote = 1;

		switch (usedBall) {
		case Items.MEISTERBALL:
			return 4;
		case Items.HYPERBALL:
			fangquote = 2;
			break;
		case Items.SUPERBALL:
			fangquote = 1.5;
			break;
		}

		double ailmentValue = 0;
		switch (this.getAilment()) {
		case BURN:
		case POISON:
		case PARALYSIS:
			ailmentValue = 1.5;
			break;
		case FREEZE:
		case SLEEP:
			ailmentValue = 2.5;
			break;
		default:
			break;
		}
		int x = (int) ((((3 * this.getStats().getStats().get(Stat.HP) - 2 * this.getStats().getCurrentHP())
				* this.getCatchRate() * fangquote) / 3 * this.getStats().getStats().get(Stat.HP)) * ailmentValue);

		int y = (int) (65536 / (Math.sqrt(Math.sqrt(255.0 / x))));

		for (int i = 0; i < 4; i++) {
			if (Main.RNG.nextInt(65535) >= y) {
				return i;
			}
		}
		return 4;
	}

	public int getBaseExperience() {
		return this.baseExperience;
	}

	public void setBaseExperience(int baseExperience) {
		this.baseExperience = baseExperience;
	}

	public int getWeight() {
		switch (this.getAbility().getId()) {
		case Abilities.SCHWERMETALL:
			return this.weight * 2;
		case Abilities.LEICHTMETALL:
			return this.weight / 2;
		default:
			return this.weight;
		}
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

	/**
	 * Checks if the given item can be used on this pokemon in the current
	 * Situation. If not an error message will be returned.
	 * 
	 * @param item
	 *            - the item id that needs to be checked.
	 * @return a String object that contains the possible error message, when
	 *         selecting the item with the given id. If the returned value is
	 *         null, this item can be used.
	 * 
	 */
	public String canBeUsed(Integer item) {
		if (this.getSecondaryAilments() != null && this.getSecondaryAilments().containsKey(SecondaryAilment.EMBARGO)) {
			return SecondaryAilment.EMBARGO.getAffected().replace("@pokemon", this.getName());
		}
		PokemonInformation info = this.gController.getInformation();
		if (gController.isFighting()) {
			if (!info.hasAttribute(Items.ATTR_USABLE_IN_BATTLE, item)) {
				return Items.CANNOT_BE_USED;
			}
		} else {
			if (!info.hasAttribute(Items.ATTR_USABLE_OVERWORLD, item)) {
				return Items.CANNOT_BE_USED;
			}
		}
		return null;
	}

	public boolean useItem(de.alexanderciupka.pokemon.characters.Character source, Integer i) {
		PokemonInformation info = this.gController.getPokemonInformation();

		int heal = 0;
		ArrayList<Ailment> ailmentHeal = new ArrayList<>();
		int revive = Items.NO_REVIVE;
		ArrayList<String> messages = new ArrayList<String>();

		switch (i) {
		case Items.TRANK:
			heal = 20;
			break;
		case Items.SUPERTRANK:
			heal = 50;
			break;
		case Items.HYPERTRANK:
			heal = (200);
			break;
		case Items.TOP_TRANK:
			heal = (this.getStats().getStats().get(Stat.HP));
			break;
		case Items.TAFELWASSER:
			heal = (30);
			break;
		case Items.SPRUDEL:
			heal = (50);
			break;
		case Items.LIMONADE:
			heal = (80);
			break;
		case Items.KUHMUH_MILCH:
			heal = (100);
			break;
		case Items.GEGENGIFT:
			ailmentHeal.add(Ailment.POISON);
			ailmentHeal.add(Ailment.HEAVY_POISON);
			break;
		case Items.FEUERHEILER:
			ailmentHeal.add(Ailment.BURN);
			break;
		case Items.EISHEILER:
			ailmentHeal.add(Ailment.FREEZE);
			break;
		case Items.AUFWECKER:
			ailmentHeal.add(Ailment.SLEEP);
			break;
		case Items.PARA_HEILER:
			ailmentHeal.add(Ailment.PARALYSIS);
			break;
		case Items.TOP_GENESUNG:
			heal = this.getStats().getStats().get(Stat.HP);
		case Items.HYPERHEILER:
			for (Ailment a : Ailment.values()) {
				if (a != Ailment.FAINTED && a != Ailment.NONE) {
					ailmentHeal.add(a);
				}
			}
			break;
		case Items.BELEBER:
			revive = Items.HALF_REVIVE;
			break;
		case Items.TOP_BELEBER:
			revive = Items.FULL_REVIVE;
			break;
		case Items.SONDERBONBON:
			if (this.getStats().levelUP()) {
				this.getStats().setCurrentXP(0);
				return true;
			}
			break;
		case Items.SONNENSTEIN:
		case Items.MONDSTEIN:
		case Items.FEUERSTEIN:
		case Items.DONNERSTEIN:
		case Items.WASSERSTEIN:
		case Items.BLATTSTEIN:
		case Items.LEUCHTSTEIN:
		case Items.FINSTERSTEIN:
		case Items.FUNKELSTEIN:
			if (!this.gController.isFighting() && this.evolve(info.checkEvolution(this, i))) {
				this.gController.getGameFrame().addDialogue(this.getName() + " reagiert auf den Stein!");
				this.gController.getGameFrame().setCurrentPanel(this.gController.getGameFrame().getEvolutionPanel());
				this.gController.getGameFrame().getEvolutionPanel().start();
				return true;
			}
			break;
		}
		if (this.getAilment() == Ailment.FAINTED) {
			switch (revive) {
			case Items.HALF_REVIVE:
				this.getStats().setCurrentHP((short) Math.ceil(this.getStats().getStats().get(Stat.HP) / 2.0));
				messages.add(this.getName() + " wurden einige KP aufgefrischt!");
				break;
			case Items.FULL_REVIVE:
				this.getStats().setCurrentHP(this.getStats().getStats().get(Stat.HP));
				messages.add(this.getName() + " wurden alle KP aufgefrischt!");
				break;
			}
		} else {
			heal = this.getStats().restoreHP(heal);
			if (heal > 0) {
				messages.add(this.getName() + " wurden " + heal + " KP aufgefrischt!");
			}
		}
		for (Ailment a : ailmentHeal) {
			if (this.getAilment() == a) {
				this.setAilment(Ailment.NONE);
				messages.add(this.getName() + " ist nicht mehr " + Ailment.getText(a));
				break;
			}
		}

		if (messages.isEmpty()) {
			gController.getCurrentTextLabel().addText(Items.USELESS);
			return false;
		}
		for (String msg : messages) {
			gController.getCurrentTextLabel().addText(msg);
		}
		return true;
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

	public HashMap<SecondaryAilment, Integer> getSecondaryAilments() {
		return this.secondaryAilments;
	}

	public Ability getAbility(boolean recursion) {
		if (this.gController.isFighting()) {
			if (this.fightingAbility == null || this.fightingAbility.getId() == Abilities.KEINE) {
				this.fightingAbility = ability;
			}
			if (recursion) {
				for (int i = 0; i < 4; i++) {
					if (this.gController.getFight().getPokemon(i) != null
							&& this.gController.getFight().isPlayer(this) != this.gController.getFight().isPlayer(i)) {
						switch (this.gController.getFight().getPokemon(i).getAbility(false).getId()) {
						case Abilities.ÜBERBRÜCKUNG:
						case Abilities.TERAVOLT:
						case Abilities.TURBOBRAND:
							this.fightingAbility = new Ability(Abilities.KEINE, "", "");
							break;
						}
					}
				}
			}
			return fightingAbility;
		}
		return this.ability;
	}

	public Ability getAbility() {
		return getAbility(true);
	}

	public void setFightingAbility(Ability ability) {
		this.fightingAbility = ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	public boolean hasType(Type t) {
		return t.equals(this.types[0]) || t.equals(this.types[1]);
	}

	public int getItem() {
		return this.item;
	}

	public int removeItem() {
		int result = this.item;
		this.item = Items.KEINS;
		return result;
	}

}

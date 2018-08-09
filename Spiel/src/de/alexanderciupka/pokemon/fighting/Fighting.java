package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Player;
import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
import de.alexanderciupka.pokemon.pokemon.SecondaryAilment;
import de.alexanderciupka.pokemon.pokemon.Stat;
import de.alexanderciupka.pokemon.pokemon.Stats;
import de.alexanderciupka.pokemon.pokemon.Type;

public class Fighting {

	/**
	 * Idea for double battles: Make left- and rightTeam array?
	 */

	private Team leftPlayerTeam;
	private Team rightPlayerTeam;
	private Team leftOpponentTeam;
	private Team rightOpponentTeam;

	private Pokemon leftPlayerPokemon;
	private Pokemon rightPlayerPokemon;
	private Pokemon leftOpponentPokemon;
	private Pokemon rightOpponentPokemon;

	// private Team playerTeam;
	// private Pokemon player;
	// private Team enemyTeam;
	// private Pokemon enemy;
	private FightOption currentFightOption;
	private Field field;

	private Character leftPlayer;
	private Character rightPlayer;
	private Character leftOpponent;
	private Character rightOpponent;

	private HashSet<Pokemon> leftParticipants;
	private HashSet<Pokemon> rightParticipants;
	// private Character enemyCharacter;
	private Random rng;
	private GameController gController;
	private boolean escapable;
	private int turn;

	private HashMap<Pokemon, Move> lastMoves;
	private HashMap<Pokemon, Move> chargeMoves;
	private HashMap<Pokemon, Boolean> needsRecharge;

	// private SimpleEntry<Boolean, Boolean> visible;

	private HashMap<Pokemon, Boolean> visible;

	private boolean isDouble;

	public boolean won;

	private int activePokemon;

	private HashMap<Pokemon, Attack> turnAttacks;

	public static final int LEFT_PLAYER = 1;
	public static final int RIGHT_PLAYER = 2;
	public static final int LEFT_OPPONENT = 3;
	public static final int RIGHT_OPPONENT = 4;

	/**
	 * Starts a wild 1v1 Battle.
	 * 
	 * @param pokemon
	 *            wild pokemon
	 * @param escapable
	 *            true when the player can run away from the battle. might not be
	 *            possible in some "story" wild pokemon fights. Can still be true
	 *            even if the opponent Pokemon's ability denies any attempt to run
	 *            away, because this will be checked later.
	 * @author CutyCupy
	 */
	public Fighting(Pokemon pokemon, boolean escapable) {
		this(pokemon, null, escapable);
	}

	/**
	 * Starts a wild 1v2 Battle.
	 * 
	 * @param left
	 *            "first" wild Pokemon
	 * @param right
	 *            "second" wild Pokemon
	 * @param escapable
	 *            true when the player can run away from the battle. might not be
	 *            possible in some "story" wild pokemon fights. Can still be true
	 *            even if one of the opponent Pokemons' ability denies any attempt
	 *            to run away, because this will be checked later.
	 * @author CutyCupy
	 */
	public Fighting(Pokemon left, Pokemon right, boolean escapable) {
		this(GameController.getInstance().getMainCharacter(), left, right, escapable);
	}

	/**
	 * Starts a wild 2v2 Battle.
	 * 
	 * @param teamMate
	 * @param left
	 *            "first" wild Pokemon
	 * @param right
	 *            "second" wild Pokemon
	 * @param escapable
	 *            true when the player can run away from the battle. might not be
	 *            possible in some "story" wild pokemon fights. Can still be true
	 *            even if one of the opponent Pokemons' ability denies any attempt
	 *            to run away, because this will be checked later.
	 * @author CutyCupy
	 */
	public Fighting(Character teamMate, Pokemon left, Pokemon right, boolean escapable) {
		this();
		this.leftPlayer = this.gController.getMainCharacter();
		this.rightPlayer = teamMate == null ? this.gController.getMainCharacter() : teamMate;

		this.leftPlayerTeam = this.leftPlayer.getTeam();
		this.rightPlayerTeam = this.rightPlayer.getTeam();

		this.leftOpponent = null;
		this.rightOpponent = null;

		this.leftOpponentTeam = null;
		this.rightOpponentTeam = null;

		this.leftPlayerPokemon = this.getFirstNonFightingPokemon(this.leftPlayerTeam);
		this.rightPlayerPokemon = this.getFirstNonFightingPokemon(this.rightPlayerTeam);

		this.leftOpponentPokemon = left;
		this.rightOpponentPokemon = right;

		this.escapable = escapable;

		this.isDouble = right != null;

	}

	/**
	 * Starts a fight against the Trainer opponent 1v1.
	 * 
	 * @param opponent
	 *            The trainer that wants to battle.
	 * @author CutyCupy
	 */
	public Fighting(Character opponent) {
		this(opponent, null);
	}

	/**
	 * Starts a 1v2 Double fight.
	 * 
	 * @param leftOpponent
	 *            "left" opponent on the field.
	 * @param rightOpponent
	 *            "right" opponent on the field. if left and right opponent are the
	 *            same its a 1v1 double battle
	 * @author CutyCupy
	 */
	public Fighting(Character leftOpponent, Character rightOpponent) {
		this(GameController.getInstance().getMainCharacter(), leftOpponent, rightOpponent);
	}

	/**
	 * Starts a 2v2 Double fight with one teammate against possible two opponents.
	 * 
	 * @param teamMate
	 *            "teammate" who will fight along the player.
	 * @param leftOpponent
	 *            "left" opponent on the field.
	 * @param rightOpponent
	 *            "right" opponent on the field. Might be null, which would mean
	 *            that a 1v1 fight will be created.
	 * @author CutyCupy
	 */
	public Fighting(Character teamMate, Character leftOpponent, Character rightOpponent) {
		this.leftPlayer = this.gController.getMainCharacter();
		this.leftPlayerTeam = this.leftPlayer.getTeam();
		this.leftOpponent = leftOpponent;
		this.leftOpponentTeam = this.leftOpponent.getTeam();
		this.leftPlayerPokemon = this.getFirstNonFightingPokemon(this.leftPlayerTeam);
		this.leftOpponentPokemon = this.getFirstNonFightingPokemon(this.leftOpponentTeam);
		this.escapable = false;

		if (rightOpponent != null) {
			this.rightPlayer = teamMate;
			this.rightPlayerTeam = this.rightPlayer.getTeam();
			this.rightOpponent = rightOpponent;
			this.rightOpponentTeam = this.rightOpponent.getTeam();
			this.rightPlayerPokemon = this.getFirstNonFightingPokemon(this.rightPlayerTeam);
			this.rightOpponentPokemon = this.getFirstNonFightingPokemon(this.leftOpponentTeam);
			this.isDouble = true;
		} else {
			this.isDouble = false;
		}

	}

	// public Fighting(Pokemon pokemonTwo) {
	// this.init();
	// this.playerTeam = new
	// Team(this.gController.getMainCharacter().getTeam().getTeam(),
	// this.gController.getMainCharacter());
	// this.player = this.playerTeam.getFirstFightPokemon();
	// this.enemyTeam = new Team(null);
	// this.enemyTeam.addPokemon(pokemonTwo);
	// this.enemy = pokemonTwo;
	// this.escape = true;
	// this.getStartPokemon();
	// this.player.startFight();
	// this.enemy.startFight();
	// this.field = new
	// Field(this.gController.getMainCharacter().getCurrentRoute().getWeather());
	// }
	//
	// public Fighting(Character enemyCharacter) {
	// this.init();
	// this.enemyCharacter = enemyCharacter;
	// this.playerTeam = new
	// Team(this.gController.getMainCharacter().getTeam().getTeam(),
	// this.gController.getMainCharacter());
	// this.player = this.playerTeam.getFirstFightPokemon();
	// this.enemyTeam = new Team(enemyCharacter.getTeam().getTeam(),
	// enemyCharacter);
	// this.enemy = this.enemyTeam.getFirstFightPokemon();
	// this.escape = false;
	// this.getStartPokemon();
	// this.player.startFight();
	// this.enemy.startFight();
	// this.field = new
	// Field(this.gController.getMainCharacter().getCurrentRoute().getWeather());
	// }

	private Fighting() {
		this.lastMoves = new HashMap<>();
		this.chargeMoves = new HashMap<>();
		this.needsRecharge = new HashMap<>();
		this.turn = 0;
		this.gController = GameController.getInstance();
		this.rng = new Random();
		this.currentFightOption = FightOption.FIGHT;
		this.leftParticipants = new HashSet<Pokemon>();
		this.rightParticipants = new HashSet<Pokemon>();
		// this.visible = new SimpleEntry<Boolean, Boolean>(true, true);
		this.visible = new HashMap<>();
	}

	private Pokemon getFirstNonFightingPokemon(Team team) {
		if (team == null) {
			return null;
		}
		for (int i = 0; i < team.getAmmount(); i++) {
			if (team.getPokemon(i) != null && team.getPokemon(i).getAilment() != Ailment.FAINTED
					&& this.notFighting(team.getPokemon(i))) {
				return team.getPokemon(i);
			}
		}
		return null;
	}

	private boolean notFighting(Pokemon p) {
		return !(p.equals(this.leftPlayerPokemon) || p.equals(this.rightPlayerPokemon)
				|| p.equals(this.leftOpponentPokemon) || p.equals(this.rightOpponentPokemon));
	}

	// private void getStartPokemon() {
	// this.player = this.playerTeam.getFirstFightPokemon();
	// this.sendOut(this.playerTeam.getIndex(this.player));
	// }

	/**
	 * Calculates and returns the order of attacks in a pokemon fight.
	 * 
	 * @param leftPlayer
	 *            Move chosen by the left Player pokemon
	 * @param rightPlayer
	 *            Move chosen by the right player pokemon
	 * @param leftOpponent
	 *            Move chosen by the left opponent pokemon
	 * @param rightOpponent
	 *            Move chosen by the right opponent pokemon
	 * @return Move order
	 * @author CutyCupy
	 */
	private Attack[] getOrder() {
		ArrayList<Attack> order = new ArrayList<>();
		Attack[] attacks = new Attack[] { this.turnAttacks.get(this.leftPlayerPokemon),
				this.turnAttacks.get(this.rightPlayerPokemon), this.turnAttacks.get(this.leftOpponentPokemon),
				this.turnAttacks.get(this.rightOpponentPokemon) };
		for (Attack attack : attacks) {
			if (attack != null) {
				order.add(this.getOrderIndex(order, attack), attack);
			}
		}
		return order.toArray(new Attack[order.size()]);
	}

	public Pokemon[] getSpeedOrder() {
		ArrayList<Pokemon> order = new ArrayList<>();
		for (Pokemon p : new Pokemon[] { this.leftPlayerPokemon, this.leftOpponentPokemon, this.rightOpponentPokemon,
				this.leftOpponentPokemon }) {
			if (p != null) {
				int index = 0;
				for (Pokemon cur : order) {
					if (this.field.updateFightStats(cur, this.isPlayer(cur)).get(Stat.SPEED) < this.field
							.updateFightStats(p, this.isPlayer(p)).get(Stat.SPEED)) {
						break;
					} else if (this.field.updateFightStats(cur, this.isPlayer(cur)).get(Stat.SPEED) == this.field
							.updateFightStats(p, this.isPlayer(p)).get(Stat.SPEED)) {
						if (this.rng.nextBoolean()) {
							break;
						}
					}
					index++;
				}
				order.add(index, p);
			}
		}
		return order.toArray(new Pokemon[order.size()]);
	}

	private int getOrderIndex(ArrayList<Attack> order, Attack attack) {
		for (int i = 0; i < order.size(); i++) {
			Attack compare = order.get(i);
			if (attack.getPriority() > compare.getPriority()) {
				return i;
			} else if (attack.getPriority() == compare.getPriority()) {
				double speedA = this.field.updateFightStats(attack.getSource(), this.isPlayer(attack.getSource()))
						.get(Stat.SPEED);
				double speedC = this.field.updateFightStats(attack.getSource(), this.isPlayer(compare.getSource()))
						.get(Stat.SPEED);
				if (speedA > speedC) {
					return i;
				} else if (speedA == speedC) {
					if (this.rng.nextBoolean()) {
						return i;
					}
				}
			}
		}
		return order.size();
	}

	public boolean isPlayer(Pokemon p) {
		if (p.equals(this.leftPlayerPokemon) || p.equals(this.rightPlayerPokemon)) {
			return true;
		}
		return false;
	}

	// public boolean isPlayerStart(Move playerMove, Move enemyMove) {
	// if (playerMove.getPriority() > enemyMove.getPriority()) {
	// return true;
	// } else if (playerMove.getPriority() == enemyMove.getPriority()) {
	// if (this.player.getStats().getFightStats().get(Stat.SPEED) >
	// this.enemy.getStats().getFightStats()
	// .get(Stat.SPEED)) {
	// return true;
	// } else if (this.player.getStats().getFightStats().get(Stat.SPEED) ==
	// this.enemy.getStats().getFightStats()
	// .get(Stat.SPEED)) {
	// return this.rng.nextBoolean();
	// } else {
	// return false;
	// }
	// } else {
	// return false;
	// }
	// }

	public void startRound() {
		for (Attack attack : this.getOrder()) {
			if (attack.getSource().getAilment() != Ailment.FAINTED) {
				if (attack.getTargets() != null) {
					for (Pokemon target : attack.getTargets()) {
						this.attack(attack.getSource(), target, attack.getMove());
					}
				} else if (attack.getItem() != null) {
					attack.getSource().useItem(null, attack.getItem()); // TODO: Trainers can have items too
				} else if (attack.getSwap() != null) {
					this.swap(attack.getSource(), attack.getSwap());
				}
			}
		}
		this.endTurn();
	}

	private void swap(Pokemon current, Pokemon next) {
		int position = 0;
		if (current.equals(this.leftPlayerPokemon)) {
			position = 1;
		} else if (current.equals(this.rightPlayerPokemon)) {
			position = 2;
		} else if (current.equals(this.leftOpponentPokemon)) {
			position = 3;
		} else {
			position = 4;
		}
		this.sendBack(current, position);
		this.sendOut(next, position);
	}

	public void sendBack(Pokemon current, int position) {
		Character c = null;
		switch (position) {
		case 1:
			c = this.leftPlayer;
			break;
		case 2:
			c = this.rightPlayer;
			break;
		case 3:
			c = this.leftOpponent;
			break;
		default:
			c = this.rightOpponent;
			break;
		}
		if (c == null) {
			return;
		}
		this.gController.getGameFrame().getFightPanel()
				.addText(c.getName() + " ruft " + current.getName() + " zurück!");
		SoundController.getInstance().playSound(SoundController.POKEBALL_CATCHING, true);
	}

	public void sendOut(Pokemon next, int position) {
		this.setVisible(next, false);
		Character c = null;
		switch (position) {
		case 1:
			c = this.leftPlayer;
			this.leftPlayerPokemon = next;
			break;
		case 2:
			c = this.rightPlayer;
			this.rightPlayerPokemon = next;
			break;
		case 3:
			c = this.leftOpponent;
			this.leftOpponentPokemon = next;
			break;
		default:
			c = this.rightOpponent;
			this.rightOpponentPokemon = next;
			break;
		}
		if (c == null) {
			return;
		}
		this.gController.getGameFrame().getFightPanel().addText(c.getName() + " setzt " + next.getName() + " ein!");
		SoundController.getInstance().playSound(SoundController.POKEBALL_OUT, true);
		this.setVisible(next, true);
	}

	private Character getTrainer(Pokemon p) {
		if (p.equals(this.leftPlayerPokemon)) {
			return this.leftPlayer;
		} else if (p.equals(this.rightPlayerPokemon)) {
			return this.rightPlayer;
		} else if (p.equals(this.leftOpponentPokemon)) {
			return this.leftOpponent;
		} else {
			return this.rightOpponent;
		}
	}

	// public void startRound(Move playerMove, Move enemyMove) {
	// if (playerMove == null) {
	// playerMove = this.gController.getInformation().getMoveByName("Verzweifler");
	// }
	// if (enemyMove == null) {
	// enemyMove = this.gController.getInformation().getMoveByName("Verzweifler");
	// }
	// boolean playerStarts = this.gController.getFight().isPlayerStart(playerMove,
	// enemyMove);
	// if (playerStarts) {
	// if (this.gController.getFight().attack(this.player, this.enemy, playerMove))
	// {
	// this.gController.getFight().attack(this.enemy, this.player, enemyMove);
	// }
	// } else {
	// if (this.gController.getFight().attack(this.enemy, this.player, enemyMove)) {
	// this.gController.getFight().attack(this.player, this.enemy, playerMove);
	// }
	// }
	// this.endTurn();
	// }

	public void endTurn() {
		// TODO: Check player / enemy win
		if (this.gController.isFighting()) {
			for (Pokemon p : this.getSpeedOrder()) {
				p.afterTurnDamage();
				if (this.checkDead(p)) {
					this.gController.getGameFrame().getFightPanel().addText(p.getName() + " wurde besiegt!");
					this.gController.getGameFrame().getFightPanel().updatePanels();
					if (this.isPlayer(p)) {
						this.gController.loseFight();
					} else {
						if (!this.gController.winFight()) {
							this.gController.getGameFrame().getFightPanel().setEnemy();
							this.gController.getGameFrame().getFightPanel().updatePanels();
						}
					}
				}
			}
			this.increaseTurn();
		}
	}

	private boolean checkDead(Pokemon p) {
		if (p != null && p.getAilment() == Ailment.FAINTED) {
			p.changeHappiness(-1);
			return true;
		}
		return false;
	}

	public boolean attack(Pokemon attacker, Pokemon defender) {
		return this.attack(attacker, defender, attacker.getMove(defender));
	}

	public boolean attack(Pokemon attacker, Pokemon defender, Move move) {
		if (!this.gController.isFighting()) {
			return true;
		}
		if (this.needsRecharge(attacker)) {
			this.gController.getGameFrame().getFightPanel().addText(attacker.getName() + " muss sich erholen.");
			this.setRecharge(attacker, false);
			return true;
		}
		String message = attacker.canAttack();
		if (message != null) {
			this.gController.getGameFrame().getFightPanel().addText(message);
			this.setVisible(attacker, true);
			this.chargeMoves.put(attacker, null);
		} else {
			this.lastMoves.put(attacker, move);
			this.gController.getGameFrame().getFightPanel()
					.addText(attacker.getName() + " setzt " + move.getName() + " ein!");
			if (!this.hit(attacker, defender, move)) {
				this.gController.getGameFrame().getFightPanel().addText("Die Attacke ging daneben");
				this.setRecharge(attacker, false);
				this.setVisible(attacker, true);
			}
		}
		this.gController.getGameFrame().getFightPanel().updatePanels();
		boolean dead = false;
		if (this.checkDead(defender)) {
			this.gController.getGameFrame().getFightPanel().addText(defender.getName() + " wurde besiegt!");
			this.gController.getGameFrame().getFightPanel().updatePanels();
			if (this.isPlayer(defender)) {
				this.gController.loseFight();
			} else {
				if (!this.gController.winFight()) {
					this.gController.getGameFrame().getFightPanel().setEnemy();
					this.gController.getGameFrame().getFightPanel().updatePanels();
				}
			}
			dead = true;
		}
		if (this.checkDead(attacker)) {
			this.gController.getGameFrame().getFightPanel().addText(attacker.getName() + " wurde besiegt!");
			if (!this.gController.winFight()) {
				this.gController.getGameFrame().getFightPanel().setEnemy();
				this.gController.getGameFrame().getFightPanel().updatePanels();
			}
			dead = true;
		}
		return !dead;
	}

	public boolean hit(Pokemon attacker, Pokemon defender, Move move) {
		boolean stop = false;
		for (String category : move.getCategory().split("\\+")) {
			if (category.equals("charge")) {
				if (this.chargeMoves.get(attacker) == null) {
					this.chargeMoves.put(attacker, move);
					this.setVisible(attacker, !move.getCategory().contains("disappear"));
					stop = true;
				} else {
					this.chargeMoves.put(attacker, null);
					this.setVisible(attacker, true);
				}
			}
		}

		if (stop) {
			return true;
		}

		double hitChance = move.getAccuracy() * (attacker.getStats().getFightStats().get(Stat.ACCURACY)
				/ defender.getStats().getFightStats().get(Stat.EVASION));
		move.reducePP();
		float p = this.rng.nextFloat() * 100;
		if (p < hitChance) {
			for (String category : move.getCategory().split("\\+")) {
				if (category.equals("recharge")) {
					this.setRecharge(attacker, true);
				} else if (category.equals("explosion")) {
					this.playAnimation(attacker, "explosion");
					attacker.getStats().loseHP(attacker.getStats().getCurrentHP());
				}
			}
			if (defender.getSecondaryAilments().contains(SecondaryAilment.PROTECTED)) {
				this.gController.getGameFrame().getFightPanel()
						.addText(SecondaryAilment.PROTECTED.getAffected().replace("@pokemon", defender.getName()));
				return true;
			}
			int hits = this.rng.nextInt(move.getMaxHits() - move.getMinHits() + 1) + move.getMinHits();
			switch (move.getTarget()) {
			case ALL:
				this.damageCalculation(attacker, defender, move, hits);
				this.damageCalculation(attacker, attacker, move, hits);
				break;
			case OPPONENT:
				this.damageCalculation(attacker, defender, move, hits);
				break;
			case USER:
				this.damageCalculation(attacker, attacker, move, hits);
				break;
			default:
				break;

			}
			this.gController.sleep(150);
			return true;
		}
		return false;
	}

	public void buff(Pokemon pokemon, Move move) {
		for (Stat s : Stat.values()) {
			int change = move.changeStat(s);
			if (change == 0) {
				continue;
			}
			if (this.gController.isFighting()) {
				this.playAnimation(pokemon, change < 0 ? "debuff" : "buff");
			}
			if (change < 0) {
				pokemon.getStats().decreaseStat(s, Math.abs(change));
			} else {
				pokemon.getStats().increaseStat(s, change);
			}
		}
	}

	private void playAnimation(Pokemon pokemon, String animation) {
		this.gController.getGameFrame().getFightPanel().getPokemonLabel(pokemon).getAnimationLabel()
				.playAnimation(animation);
	}

	public void selfAttack(Pokemon pokemon) {
		Stats stats = pokemon.getStats();
		double damage = 40;
		double def = stats.getFightStats().get(Stat.DEFENSE);
		double atk = stats.getFightStats().get(Stat.ATTACK);
		this.playAnimation(pokemon, "punch");
		SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
		stats.loseHP((int) (((stats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
				* ((this.rng.nextFloat() * 0.15f + 0.85) / 1)));
	}

	private void playAnimation(Pokemon attacker, Pokemon defense, Move usedMove) {
		this.playAnimation(attacker, usedMove.getUserAnimation());
		this.playAnimation(defense, usedMove.getTargetAnimation());
	}

	private void damageCalculation(Pokemon attacker, Pokemon defense, Move usedMove, int ammount) {
		Stats attackerStats = attacker.getStats();
		Stats defenderStats = defense.getStats();
		double weakness = Type.getEffectiveness(usedMove.getMoveType(), defense);
		if (!this.isVisible(defense)) {
			this.gController.getGameFrame().getFightPanel().addText("Es ist kein Gegner zu sehen!", true);
			return;
		}
		if (weakness == Type.USELESS) {
			this.gController.getGameFrame().getFightPanel().addText("Die Attacke zeigte keine Wirkung!", true);
			return;
		}
		double damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
		if (damage > 0 || usedMove.getDamageClass() != DamageClass.NO_DAMAGE) {
			double def = 0;
			double atk = 0;
			switch (usedMove.getDamageClass()) {
			case PHYSICAL:
				def = defenderStats.getFightStats().get(Stat.DEFENSE);
				atk = attackerStats.getFightStats().get(Stat.ATTACK);
				break;
			case SPECIAL:
				def = defenderStats.getFightStats().get(Stat.SPECIALDEFENSE);
				atk = attackerStats.getFightStats().get(Stat.SPECIALDEFENSE);
				break;
			default:
				break;
			}
			for (int i = 0; i < ammount; i++) {
				if (this.gController.isFighting()) {
					this.playAnimation(attacker, defense, usedMove);
				}
				int crit = 1;
				float pCrit = this.rng.nextFloat();
				switch (usedMove.getCrit() + 1) {
				case 1:
					crit = pCrit < 0.0625 ? 2 : 1;
					break;
				case 2:
					crit = pCrit < 0.125 ? 2 : 1;
					break;
				case 3:
					crit = pCrit < 0.5 ? 2 : 1;
					break;
				default:
					crit = 2;
					break;
				}
				if (crit == 2) {
					this.gController.getGameFrame().getFightPanel().addText("Ein Volltreffer!", true);
				}
				damage = (weakness * ((attackerStats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
						* crit * ((this.rng.nextFloat() * 0.15f + 0.85) / 1));
				damage = Math.max(damage, 1);
				if (usedMove.getCategory().contains("ohko")) {
					damage = defense.getStats().getCurrentHP();
				}
				defenderStats.loseHP((int) damage);
				if (weakness >= Type.STRONG) {
					SoundController.getInstance().playSound(SoundController.SUPER_EFFECTIVE);
				} else if (weakness <= Type.WEAK && weakness != Type.USELESS) {
					SoundController.getInstance().playSound(SoundController.NOT_EFFECTIVE);
				} else if (weakness == 1.0) {
					SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
				}
				this.gController.getGameFrame().getFightPanel().updatePanels();
				this.gController.sleep(150);

				if (usedMove.getDrain() > 0) {
					attackerStats.restoreHP((int) (damage * (usedMove.getDrain() / 100.0)));
					this.gController.getGameFrame().getFightPanel()
							.addText(defense.getName() + " wurde Energie abgesaugt!", true);
				} else if (usedMove.getDrain() < 0) {
					attackerStats.loseHP((int) Math.abs(damage * (usedMove.getDrain() / 100.0)));
					this.gController.getGameFrame().getFightPanel()
							.addText(attacker.getName() + " hat sich durch den Rückstoß verletzt!", true);
				}
				this.gController.getGameFrame().getFightPanel().updatePanels();
				damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
			}
			if (weakness >= Type.STRONG) {
				this.gController.getGameFrame().getFightPanel().addText("Die Attacke war sehr effektiv!", true);
			} else if (weakness <= Type.WEAK) {
				this.gController.getGameFrame().getFightPanel().addText("Die Attacke war nicht sehr effektiv!", true);
			}
		} else {
			this.playAnimation(attacker, defense, usedMove);
			if (!attacker.equals(defense)
					&& (usedMove.getAilment() != null || usedMove.getAilment() != Ailment.NONE
							|| usedMove.getSecondaryAilment() != null)
					&& defense.getSecondaryAilments().contains(SecondaryAilment.MAGICCOAT)) {
				this.gController.getGameFrame().getFightPanel()
						.addText(SecondaryAilment.MAGICCOAT.getAffected().replace("@pokemon", defense.getName()));
				defense = attacker;
			} else if (usedMove.getCategory().contains("teleport")) {
				if (this.canEscape()) {
					this.gController.getGameFrame().getFightPanel()
							.addText(attacker.getName() + " flieht aus dem Kampf!");
					this.gController.endFight();
				} else {
					this.gController.getGameFrame().getFightPanel().addText("Es schlägt fehl!");
				}
			}
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (usedMove.getHealing() > 0) {
			attackerStats.restoreHP((int) (attackerStats.getStats().get(Stat.HP) * (usedMove.getHealing() / 100)));
			this.gController.getGameFrame().getFightPanel()
					.addText("Die KP von " + attacker.getName() + " wurden aufgefrischt!", true);
			this.gController.getGameFrame().getFightPanel().updatePanels();
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (usedMove.checkStatChange()) {
			if (usedMove.checkUserBuff()) {
				this.buff(attacker, usedMove);
			}
			if (usedMove.checkEnemyBuff()) {
				this.buff(defense, usedMove);
			}
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (this.rng.nextFloat() * 100 < usedMove.getAilmentChance() || usedMove.getAilmentChance() == 0) {
			if (((usedMove.getAilment() != Ailment.NONE && usedMove.getAilment() != null)
					&& defense.setAilment(usedMove.getAilment()))) {
				this.gController.getGameFrame().getFightPanel()
						.addText(defense.getName() + " wurde " + Ailment.getText(usedMove.getAilment()) + "!", true);
			} else if ((usedMove.getSecondaryAilment() != null)) {
				defense.addSecondaryAilment(usedMove.getSecondaryAilment());
			}
			this.gController.getGameFrame().getFightPanel().updatePanels();
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();
	}

	public boolean canBeSendOut(Pokemon p) {
		return !this.checkDead(p) && this.notFighting(p);
	}

	public boolean sendOut(int index, Pokemon replacement) {
		if (!this.canBeSendOut(replacement)) {
			return false;
		}
		switch (index) {
		case LEFT_PLAYER:
			this.leftPlayerTeam.swapPokemon(0, this.leftPlayerTeam.getIndex(replacement));
			this.setPlayer(true);
			break;
		case RIGHT_PLAYER:
			if (this.leftPlayer.equals(this.rightPlayer)) {
				this.rightPlayerTeam.swapPokemon(1, this.rightPlayerTeam.getIndex(replacement));
			} else {
				this.rightPlayerTeam.swapPokemon(0, this.rightPlayerTeam.getIndex(replacement));
			}
			this.setPlayer(false);
			break;
		case LEFT_OPPONENT:
			this.leftOpponentTeam.swapPokemon(0, this.leftOpponentTeam.getIndex(replacement));
			this.setEnemy(true);
			break;
		case RIGHT_OPPONENT:
			if (this.leftOpponent.equals(this.rightOpponent)) {
				this.rightOpponentTeam.swapPokemon(1, this.rightPlayerTeam.getIndex(replacement));
			} else {
				this.rightOpponentTeam.swapPokemon(0, this.rightOpponentTeam.getIndex(replacement));
			}
			this.setEnemy(false);
			break;
		default:
			break;
		}
		this.addParticipants(true);
		this.addParticipants(false);
		return true;
	}

	public Pokemon getPokemon(int index) {
		switch (index) {
		case LEFT_PLAYER:
			return this.leftPlayerPokemon;
		case RIGHT_PLAYER:
			return this.rightPlayerPokemon;
		case LEFT_OPPONENT:
			return this.leftOpponentPokemon;
		case RIGHT_OPPONENT:
			return this.rightOpponentPokemon;
		}
		return null;
	}

	public Move canUse(Pokemon user, Move move) {
		if (this.chargeMoves.get(user) != null) {
			return this.chargeMoves.get(user);
		}
		if (user.getSecondaryAilments().contains(SecondaryAilment.TORMENT)) {
			if (move.equals(this.lastMoves.get(user))) {
				return null;
			}
		}
		return move.isDisabled() ? null : move;
	}

	private void setPlayer(boolean left) {
		if (left) {
			if (this.checkDead(this.leftPlayerPokemon)) {
				this.removeParticipant(this.leftPlayerPokemon);
			}
			this.leftPlayerPokemon = this.leftPlayerTeam.getTeam()[0];
			this.leftPlayerPokemon.startFight();
			this.setVisible(this.leftPlayerPokemon, true);
		} else {
			if (this.leftPlayerTeam.equals(this.rightPlayerTeam)) {
				if (this.checkDead(this.rightPlayerPokemon)) {
					this.removeParticipant(this.rightPlayerPokemon);
				}
				this.rightPlayerPokemon = this.leftPlayerTeam.getTeam()[1];
			} else {
				this.rightPlayerPokemon = this.rightPlayerTeam.getTeam()[0];
			}
			this.rightPlayerPokemon.startFight();
			this.setVisible(this.rightPlayerPokemon, true);
		}
		this.addParticipants(true);
		this.addParticipants(false);
		this.gController.updateFight();
	}

	private void setEnemy(boolean left) {
		if (left) {
			this.leftOpponentPokemon = this.leftOpponentTeam.getTeam()[0];
			this.leftOpponentPokemon.startFight();
			this.setVisible(this.leftOpponentPokemon, true);
		} else {
			if (this.leftOpponentTeam.equals(this.rightOpponentTeam)) {
				this.rightOpponentPokemon = this.leftOpponentTeam.getTeam()[1];
			} else {
				this.rightOpponentPokemon = this.rightOpponentTeam.getTeam()[0];
			}
			this.rightOpponentPokemon.startFight();
			this.setVisible(this.rightOpponentPokemon, true);
		}
		this.clearParticipants(left);
		this.addParticipants(left);
		this.gController.updateFight();
	}

	private void clearParticipants(boolean left) {
		if (left) {
			this.leftParticipants.clear();
		} else {
			this.rightParticipants.clear();
		}
	}

	private void addParticipants(boolean left) {
		if (left) {
			this.leftParticipants.add(this.leftPlayerPokemon);
			if (this.isDouble && this.gController.getMainCharacter().equals(this.rightPlayer)) {
				this.leftParticipants.add(this.rightPlayerPokemon);
			}
		} else {
			this.rightParticipants.add(this.leftPlayerPokemon);
			if (this.isDouble && this.gController.getMainCharacter().equals(this.rightPlayer)) {
				this.rightParticipants.add(this.rightPlayerPokemon);
			}
		}
	}

	public void removeParticipant(Pokemon p) {
		this.leftParticipants.remove(p);
		this.rightParticipants.remove(p);
	}

	public boolean didPlayerLose() {
		boolean replaceLeft = this.checkDead(this.leftPlayerPokemon);
		boolean replaceRight = this.checkDead(this.rightPlayerPokemon);
		if (this.getFirstNonFightingPokemon(this.leftPlayerTeam) == null) {
			if (!this.isDouble || !(this.rightPlayer instanceof Player)) {
				if (replaceLeft) {
					return true;
				}
			} else {
				if (replaceLeft && replaceRight) {
					return true;
				}
			}
		}
		if (replaceLeft) {
			this.setCurrentFightOption(FightOption.POKEMON);
			this.gController.getGameFrame().getPokemonPanel().update(LEFT_PLAYER);
			while (this.getCurrentFightOption().equals(FightOption.POKEMON)) {
				Thread.yield();
			}
		}
		if (replaceRight) {
			this.setCurrentFightOption(FightOption.POKEMON);
			this.gController.getGameFrame().getPokemonPanel().update(RIGHT_PLAYER);
			while (this.getCurrentFightOption().equals(FightOption.POKEMON)) {
				Thread.yield();
			}
		}
		return false;
	}

	public void onDefeat() {
		if (this.leftOpponent != null) {
			this.leftOpponent.getTeam().restoreTeam();
		}
		if (this.rightOpponent != null) {
			this.rightOpponent.getTeam().restoreTeam();
		}
	}

	public boolean enemyDead() {
		boolean replaceLeft = this.checkDead(this.leftOpponentPokemon);
		boolean replaceRight = this.checkDead(this.rightOpponentPokemon);
		this.gController.getGameFrame().getFightPanel().removeEnemy();
		if (this.getFirstNonFightingPokemon(this.leftOpponentTeam) == null
				&& this.getFirstNonFightingPokemon(this.rightOpponentTeam) == null) {
			if ((!this.isDouble && replaceLeft) || (replaceLeft && replaceRight)) {
				return true;
			}
		}
		if (replaceLeft) {
			this.sendOut(LEFT_OPPONENT, this.getFirstNonFightingPokemon(this.leftOpponentTeam));
		}
		if (replaceRight) {
			this.sendOut(RIGHT_OPPONENT, this.getFirstNonFightingPokemon(this.rightOpponentTeam));
		}
		return false;
	}

	public FightOption getCurrentFightOption() {
		return this.currentFightOption;
	}

	public void setCurrentFightOption(FightOption currentFightOption) {
		this.currentFightOption = currentFightOption;
	}

	public HashSet<Pokemon> getParticipants(boolean left) {
		return left ? this.leftParticipants : this.rightParticipants;
	}

	public boolean canEscape() {
		return this.escapable;
	}

	public int calculateXP(Pokemon defeated, Pokemon participant) {
		int base = defeated.getBaseExperience();
		double OTFactor = 1;
		double itemFactor = 1;
		double friendshipFactor = 1;
		double evolveFactor = participant.getEvolves() != 0 ? 1.2 : 1;
		int enemyLevel = defeated.getStats().getLevel();
		int playerLevel = participant.getStats().getLevel();
		int xp = (int) ((((base * enemyLevel) / 5.0)
				* (Math.pow(2 * enemyLevel + 10, 2.5) / Math.pow(enemyLevel + playerLevel + 10, 2.5)) + 1) * OTFactor
				* itemFactor * friendshipFactor * evolveFactor);
		participant.increaseEV(defeated);
		return xp;
	}

	public Character getEnemyCharacter(boolean left) {
		return left ? this.leftOpponent : this.rightOpponent;
	}

	public int getTurn() {
		return this.turn;
	}

	public void increaseTurn() {
		this.turn++;
	}

	public void setTurn(int newTurn) {
		this.turn = newTurn;
	}

	public Move getLastMove(Pokemon pokemon) {
		return this.lastMoves.get(pokemon);
	}

	public Character getCharacter(int index) {
		switch (index) {
		case LEFT_PLAYER:
			return this.leftPlayer;
		case RIGHT_PLAYER:
			return this.rightPlayer;
		case LEFT_OPPONENT:
			return this.leftOpponent;
		case RIGHT_OPPONENT:
			return this.rightOpponent;
		default:
			return null;
		}
	}

	public Team getTeam(int index) {
		switch (index) {
		case LEFT_PLAYER:
			return this.leftPlayerTeam;
		case RIGHT_PLAYER:
			return this.rightPlayerTeam;
		default:
			return null;
		}
	}

	public boolean isDouble() {
		return this.isDouble;
	}

	public void setVisible(Pokemon p, boolean v) {
		this.visible.put(p, v);
	}

	public boolean isVisible(Pokemon pokemon) {
		if (this.visible.get(pokemon) == null) {
			return false;
		}
		return this.visible.get(pokemon);

	}

	public void setRecharge(Pokemon p, boolean v) {
		this.needsRecharge.put(p, v);
	}

	public boolean needsRecharge(Pokemon pokemon) {
		if (this.needsRecharge.get(pokemon) == null) {
			this.needsRecharge.put(pokemon, false);
		}
		return this.needsRecharge.get(pokemon);
	}

	public boolean canChooseAction(Pokemon p) {
		if (this.chargeMoves.get(p) != null || this.needsRecharge(p)) {
			return false;
		}
		return true;
	}

	public Field getField() {
		return this.field;
	}

	public boolean registerAttack(Attack attack) {
		this.turnAttacks.put(attack.getSource(), attack);
		if (this.isDouble) {
			if (this.activePokemon == LEFT_PLAYER && this.gController.getMainCharacter().equals(this.rightPlayer)) {
				this.activePokemon = RIGHT_PLAYER;
				return true;
			}
		}
		return false;
	}

	/**
	 * @return A String object that represents the text that should be displayed at
	 *         the start of the fight.
	 * @author CutyCupy
	 */
	public String getEntryText() {
		if (this.escapable) {
			return "Ein wildes " + this.leftOpponentPokemon.getName() + " "
					+ (this.isDouble ? "und " + this.rightOpponentPokemon.getName() : "") + " erscheint!";
		} else {
			if (this.isDouble && !this.rightOpponent.equals(this.leftOpponent)) {
				return "Eine Herausforderung von " + this.leftOpponent.getName() + " und "
						+ this.rightOpponent.getName() + "!";
			} else {
				return "Eine Herausforderung von " + this.leftOpponent.getName() + "!";
			}
		}
	}

	/**
	 * @return
	 * @author CutyCupy
	 */
	public int getActivePlayer() {
		return this.activePokemon;
	}

}

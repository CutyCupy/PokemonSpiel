package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.characters.ai.IAI;
import de.alexanderciupka.pokemon.characters.ai.WildPokemonAI;
import de.alexanderciupka.pokemon.characters.types.NPC;
import de.alexanderciupka.pokemon.characters.types.Player;
import de.alexanderciupka.pokemon.constants.Abilities;
import de.alexanderciupka.pokemon.constants.Moves;
import de.alexanderciupka.pokemon.main.Main;
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

	private FightOption currentFightOption;
	private Field field;

	private Character leftPlayer;
	private Character rightPlayer;
	private Character leftOpponent;
	private Character rightOpponent;

	private HashSet<Pokemon> leftParticipants;
	private HashSet<Pokemon> rightParticipants;

	private Random rng;
	private GameController gController;
	private boolean escapable;
	private int turn;

	private HashMap<Pokemon, Move> lastMoves;
	private HashMap<Pokemon, Move> chargeMoves;
	private HashMap<Pokemon, Boolean> needsRecharge;

	private HashMap<Pokemon, Boolean> visible;

	private boolean isDouble;

	public boolean won;

	private int activePokemon;

	private HashMap<Pokemon, Attack> turnAttacks;

	public static final int LEFT_PLAYER = 0;
	public static final int RIGHT_PLAYER = 1;
	public static final int LEFT_OPPONENT = 2;
	public static final int RIGHT_OPPONENT = 3;

	private boolean activeTurn;

	public static boolean waiting = false;

	private ArrayList<Integer> alreadyActed;

	private ArrayList<IAI> ais;

	/**
	 * Starts a wild 1v1 Battle.
	 * 
	 * @param pokemon
	 *            wild pokemon
	 * @param escapable
	 *            true when the player can run away from the battle. might not
	 *            be possible in some "story" wild pokemon fights. Can still be
	 *            true even if the opponent Pokemon's ability denies any attempt
	 *            to run away, because this will be checked later.
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
	 *            true when the player can run away from the battle. might not
	 *            be possible in some "story" wild pokemon fights. Can still be
	 *            true even if one of the opponent Pokemons' ability denies any
	 *            attempt to run away, because this will be checked later.
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
	 *            true when the player can run away from the battle. might not
	 *            be possible in some "story" wild pokemon fights. Can still be
	 *            true even if one of the opponent Pokemons' ability denies any
	 *            attempt to run away, because this will be checked later.
	 * @author CutyCupy
	 */
	public Fighting(Character teamMate, Pokemon left, Pokemon right, boolean escapable) {
		this();
		this.leftPlayer = this.gController.getMainCharacter();

		this.leftPlayerTeam = this.leftPlayer.getTeam();

		this.leftOpponent = null;
		this.rightOpponent = null;

		this.leftOpponentTeam = null;
		this.rightOpponentTeam = null;

		this.leftPlayerPokemon = this.getFirstNonFightingPokemon(this.leftPlayerTeam);

		this.leftOpponentPokemon = left;
		this.rightOpponentPokemon = right;

		this.escapable = escapable;

		if (right != null) {
			this.rightPlayer = teamMate == null ? this.gController.getMainCharacter() : teamMate;
			this.rightPlayerTeam = this.rightPlayer.getTeam();
			this.rightPlayerPokemon = this.getFirstNonFightingPokemon(this.rightPlayerTeam);
			this.isDouble = true;
		} else {
			this.isDouble = false;
		}

		this.setPlayer(true);
		this.setPlayer(false);
		this.setEnemy(true);
		this.setEnemy(false);

		this.init();
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
	 *            "right" opponent on the field. if left and right opponent are
	 *            the same its a 1v1 double battle
	 * @author CutyCupy
	 */
	public Fighting(Character leftOpponent, Character rightOpponent) {
		this(GameController.getInstance().getMainCharacter(), leftOpponent, rightOpponent);
	}

	/**
	 * Starts a 2v2 Double fight with one teammate against possible two
	 * opponents.
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
		this();
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

		this.setPlayer(true);
		this.setPlayer(false);
		this.setEnemy(true);
		this.setEnemy(false);

		this.init();
	}

	public void init() {
		this.ais = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			if (this.getCharacter(i) instanceof NPC || this.getPokemon(i) != null) {
				IAI ai = new WildPokemonAI(this.getCharacter(i));
				ai.setPosition(i);
				this.ais.add(ai);
			}
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

		this.field = new Field(this.gController.getMainCharacter().getCurrentRoute().getWeather());

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
		System.out.println(order);
		return order.toArray(new Attack[order.size()]);
	}

	public Pokemon[] getSpeedOrder() {
		ArrayList<Pokemon> order = new ArrayList<>();
		for (Pokemon p : new Pokemon[] { this.leftPlayerPokemon, this.leftOpponentPokemon, this.rightOpponentPokemon,
				this.rightPlayerPokemon }) {
			if (p != null) {
				int index = 0;
				for (Pokemon cur : order) {
					if (this.field.updateFightStats(cur.getStats().getFightStats(), this.isPlayer(cur))
							.get(Stat.SPEED) < this.field
									.updateFightStats(p.getStats().getFightStats(), this.isPlayer(p)).get(Stat.SPEED)) {
						break;
					} else if (this.field.updateFightStats(cur.getStats().getFightStats(), this.isPlayer(cur))
							.get(Stat.SPEED) == this.field
									.updateFightStats(p.getStats().getFightStats(), this.isPlayer(p)).get(Stat.SPEED)) {
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
				if (attack.getSource().getAbility().getId() == Abilities.ZEITSPIEL) {
					return i;
				} else if (attack.getSource().getAbility().getId() == Abilities.ZEITSPIEL) {
					continue;
				}
				double speedA = this.field.updateFightStats(attack.getSource().getStats().getFightStats(),
						this.isPlayer(attack.getSource())).get(Stat.SPEED);
				double speedC = this.field.updateFightStats(compare.getSource().getStats().getFightStats(),
						this.isPlayer(compare.getSource())).get(Stat.SPEED);
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
		if (p != null && (p.equals(this.leftPlayerPokemon) || p.equals(this.rightPlayerPokemon))) {
			return true;
		}
		return false;
	}

	public boolean isPlayer(int i) {
		return i == LEFT_PLAYER || i == RIGHT_PLAYER;
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

		this.alreadyActed = new ArrayList<>();

		this.activeTurn = true;
		for (Attack attack : this.getOrder()) {
			if (attack.getSource().getAilment() != Ailment.FAINTED && getIndex(attack.getSource()) != -1) {
				if (attack.getTargets() != null && attack.getItem() == null) {
					int pp = 1;
					for (Pokemon target : attack.getTargets()) {
						if (target == null) {
							switch (attack.getMove().getTarget()) {
							case ALL_OPPONENTS:
							case ALL_OTHER_POKEMON:
							case ALL_POKEMON:
							case USER_AND_ALLIES:
								continue;
							case RANDOM_OPPONENT:
							case SELECTED_POKEMON:
							case SELECTED_POKEMON_ME_FIRST:
							case SPECIFIC_MOVE:
							case USER_OR_ALLY:
							case OPPONENTS_FIELD:
							case USERS_FIELD:
								target = getPokemon(getPartner(target));
								break;
							default:
								break;
							}
						}
						if (target != null) {
							switch (target.getAbility().getId()) {
							case Abilities.ERZWINGER:
								pp += 1;
								break;
							}
							this.attack(attack.getSource(), target, attack.getMove());
						}
					}
					for (int i = 0; i < pp; i++) {
						attack.getMove().reducePP();
					}
				} else if (attack.getSwap() != null) {
					this.swap(attack.getSource(), attack.getSwap());
				} else {
					System.out.println(attack.getMove());
				}
			}
			this.alreadyActed.add(this.getIndex(attack.getSource()));
			while (waiting) {
				Thread.yield();
			}
		}
		this.activeTurn = false;
		this.endTurn();
	}

	public int getPartner(int index) {
		switch (index) {
		case Fighting.LEFT_PLAYER:
			return Fighting.RIGHT_PLAYER;
		case Fighting.RIGHT_PLAYER:
			return Fighting.LEFT_PLAYER;
		case Fighting.LEFT_OPPONENT:
			return Fighting.RIGHT_OPPONENT;
		case Fighting.RIGHT_OPPONENT:
			return Fighting.LEFT_OPPONENT;
		default:
			return -1;
		}
	}

	public int getPartner(Pokemon pokemon) {
		return getPartner(this.getIndex(pokemon));
	}

	public void swap(Pokemon current, Pokemon next) {
		if (isActiveTurn()) {
			for (int i = 0; i < 4; i++) {
				if (getPokemon(i) != null && isPlayer(i) != isPlayer(current)) {
					switch (getPokemon(i).getAbility().getId()) {
					case Abilities.WEGSPERRE:
						if (!current.hasType(Type.GHOST)) {
							this.gController.getGameFrame().getFightPanel()
									.addText(current.getName() + " ist gefangen und kann nicht wechseln!", false);
							return;
						}
						break;
					case Abilities.AUSWEGLOS:
						if (Type.getEffectiveness(Type.GROUND, getPokemon(i)) > Type.USELESS) {
							this.gController.getGameFrame().getFightPanel()
									.addText(current.getName() + " ist gefangen und kann nicht wechseln!", false);
							return;
						}
						break;
					case Abilities.MAGNETFALLE:
						if (current.hasType(Type.STEEL)) {
							this.gController.getGameFrame().getFightPanel()
									.addText(current.getName() + " ist gefangen und kann nicht wechseln!", false);
							return;
						}
						break;
					}
				}
			}
			Character trainer = getTrainer(current);
			switch (getIndex(current)) {
			case LEFT_OPPONENT:
				clearParticipants(true);
				break;
			case RIGHT_OPPONENT:
				clearParticipants(false);
				break;
			default:
				break;
			}
			
			updateAttackTargets(current, next);

			trainer.getTeam().swapPokemon(trainer.getTeam().getIndex(current), trainer.getTeam().getIndex(next));

			this.gController.getGameFrame().getFightPanel()
					.addText(trainer.getName() + " ruft " + current.getName() + " zurück!");
			SoundController.getInstance().playSound(SoundController.POKEBALL_CATCHING, false);
			this.setVisible(current, false);
			this.setVisible(next, false);
			this.gController.getGameFrame().getFightPanel().updateFight();
			this.gController.getGameFrame().getFightPanel()
					.addText(trainer.getName() + " setzt " + next.getName() + " ein!");
			this.setVisible(next, true);
			switch (getIndex(current)) {
			case Fighting.LEFT_OPPONENT:
				this.setEnemy(true);
				break;
			case Fighting.LEFT_PLAYER:
				this.setPlayer(true);
				break;
			case Fighting.RIGHT_OPPONENT:
				this.setEnemy(false);
				break;
			case Fighting.RIGHT_PLAYER:
				this.setPlayer(false);
				break;
			}
			this.gController.getGameFrame().getFightPanel().updateFight();

			this.addParticipants(true);
			this.addParticipants(false);

			// for (Pokemon p : turnAttacks.keySet()) {
			// turnAttacks.get(p).updateTargets(current, next);
			// }
		} else {
			this.registerAttack(new Attack(current, next));
		}

	}

	public int getIndex(Pokemon p) {
		for (int i : new int[] { Fighting.LEFT_PLAYER, Fighting.RIGHT_PLAYER, Fighting.LEFT_OPPONENT,
				Fighting.RIGHT_OPPONENT }) {
			if (p != null && p.equals(this.getPokemon(i))) {
				return i;
			}
		}
		return -1;
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
	// playerMove =
	// this.gController.getInformation().getMoveByName("Verzweifler");
	// }
	// if (enemyMove == null) {
	// enemyMove =
	// this.gController.getInformation().getMoveByName("Verzweifler");
	// }
	// boolean playerStarts =
	// this.gController.getFight().isPlayerStart(playerMove,
	// enemyMove);
	// if (playerStarts) {
	// if (this.gController.getFight().attack(this.player, this.enemy,
	// playerMove))
	// {
	// this.gController.getFight().attack(this.enemy, this.player, enemyMove);
	// }
	// } else {
	// if (this.gController.getFight().attack(this.enemy, this.player,
	// enemyMove)) {
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
				this.getField().getWeather().onEndOfTurn(p);
				if (this.checkDead(p)) {
					this.onDeath(p);
				}
			}
			this.getField().endOfTurn();
			this.increaseTurn();
		}
		if(playerDead()) {
			
		}
	}

	private boolean checkDead(Pokemon p) {
		if (p != null && p.getAilment() == Ailment.FAINTED) {
			p.changeHappiness(-1);
			return true;
		}
		return p == null;
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
		this.gController.getGameFrame().getFightPanel().updateFight();
		this.gController.getGameFrame().getFightPanel().updatePanels();
		boolean dead = false;
		if (this.checkDead(defender)) {
			onDeath(defender);
			dead = true;
		}
		if (this.checkDead(attacker)) {
			onDeath(attacker);
			dead = true;
		}
		return !dead;
	}

	public void onDeath(Pokemon dead) {
		this.gController.getGameFrame().getFightPanel().addText(dead.getName() + " wurde besiegt!");
		SoundController.getInstance().playBattlecry(dead.getId());
		this.setVisible(dead, false);
		this.gController.getGameFrame().getFightPanel().updatePanels();
		if (this.isPlayer(dead)) {
			if(this.playerDead()) {
				this.gController.loseFight();
			}
		} else {
			this.gController.winFight(dead);
		}
	}

	public void remove(Pokemon p) {
		switch (getIndex(p)) {
		case Fighting.LEFT_PLAYER:
			this.leftOpponentPokemon = null;
			break;
		case Fighting.RIGHT_PLAYER:
			this.rightOpponentPokemon = null;
			break;
		case Fighting.LEFT_OPPONENT:
			this.leftOpponentPokemon = null;
			break;
		case Fighting.RIGHT_OPPONENT:
			this.rightOpponentPokemon = null;
			break;
		}
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

		double accuracy = move.getAccuracy();

		switch (defender.getAbility().getId()) {
		case Abilities.FUSSANGEL:
			if (defender.getSecondaryAilments().containsKey(SecondaryAilment.CONFUSION)) {
				accuracy *= .5;
			}
			break;
		case Abilities.STARTHILFE:
			if (move.getMoveType(attacker) == Type.ELECTRIC) {
				defender.getStats().increaseStat(Stat.SPEED, 1);
				this.gController.getGameFrame().getFightPanel().addText(defender.getAbility().getName() + " "
						+ "verhindert " + move.getName() + " und erhöht seine Initiative!");
				return true;
			}
			break;
		case Abilities.SCHILDLOS:
			accuracy = Double.MAX_VALUE;
			break;
		case Abilities.WUNDERHAUT:
			if (move.getCategory().contains("ailment") && accuracy > 50) {
				accuracy = 50;
			}
		}

		switch (attacker.getAbility().getId()) {
		case Abilities.FACETTENAUGE:
			accuracy *= 1.3;
			break;
		case Abilities.ÜBEREIFER:
			accuracy *= 0.8;
			break;
		case Abilities.SCHILDLOS:
			accuracy = Double.MAX_VALUE;
			break;
		case Abilities.TRIUMPHSTERN:
			accuracy *= 1.1;
			break;
		}

		if (this.getPokemon(this.getPartner(attacker)) != null) {
			switch (this.getPokemon(this.getPartner(attacker)).getAbility().getId()) {
			case Abilities.TRIUMPHSTERN:
				accuracy *= 1.1;
				break;
			}
		}

		double hitChance = accuracy * (attacker.getStats().getFightStats().get(Stat.ACCURACY)
				/ defender.getStats().getFightStats().get(Stat.EVASION));
		float p = this.rng.nextFloat() * 100;

		Pokemon[] pokemons = this.getSpeedOrder();
		for (int i = 0; i < pokemons.length; i++) {
			if (pokemons[i] != null && this.getIndex(pokemons[i]) != this.getIndex(attacker)) {
				switch (pokemons[i].getAbility().getId()) {
				case Abilities.BLITZFÄNGER:
					if (move.getMoveType(attacker) == Type.ELECTRIC) {
						this.gController.getGameFrame().getFightPanel()
								.addText("Blitzfänger von " + pokemons[i].getName() + " wirkt!");
						pokemons[i].getStats().increaseStat(Stat.SPECIALATTACK, 1);
						return true;
					}
					break;
				case Abilities.STURMSOG:
					if (move.getMoveType(attacker) == Type.WATER) {
						this.gController.getGameFrame().getFightPanel()
								.addText("Sturmsog von " + pokemons[i].getName() + " wirkt!");
						pokemons[i].getStats().increaseStat(Stat.SPECIALATTACK, 1);
						return true;
					}
					break;
				}
			}
		}

		if (p < hitChance) {
			for (String category : move.getCategory().split("\\+")) {
				if (category.equals("recharge")) {
					this.setRecharge(attacker, true);
				} else if (category.equals("explosion")) {
					this.playAnimation(attacker, "explosion");
					attacker.getStats().loseHP(attacker.getStats().getCurrentHP());
				}
			}
			if (defender.getSecondaryAilments().containsKey(SecondaryAilment.PROTECTED)) {
				this.gController.getGameFrame().getFightPanel()
						.addText(SecondaryAilment.PROTECTED.getAffected().replace("@pokemon", defender.getName()));
				return true;
			}

			int hits = this.rng.nextInt(move.getMaxHits() - move.getMinHits() + 1) + move.getMinHits();
			switch (attacker.getAbility().getId()) {
			case Abilities.WERTELINK:
				hits = move.getMaxHits();
				break;
			}
			this.damageCalculation(attacker, defender, move, hits);
			this.gController.sleep(150);
			if (move.getDamageClass() == DamageClass.PHYSICAL) {
				switch (defender.getAbility().getId()) {
				case Abilities.TASTFLUCH:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						move.setDisabled(true);
						attacker.addSecondaryAilment(this.getIndex(defender), SecondaryAilment.DISABLE);
						this.gController.getGameFrame().getFightPanel().addText(
								move.getName() + " wurd durch " + defender.getAbility().getName() + " blockiert!");
					}
					break;
				case Abilities.STATIK:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						attacker.setAilment(Ailment.PARALYSIS);
					}
					break;
				case Abilities.GIFTDORN:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						attacker.setAilment(Ailment.POISON);
					}
					break;
				case Abilities.FLAMMKÖRPER:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						attacker.setAilment(Ailment.BURN);
					}
					break;
				case Abilities.SPORENWIRT:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)
							&& attacker.getAbility().getId() != Abilities.WETTERFEST) {
						switch (Main.RNG.nextInt(3)) {
						case 0:
							attacker.setAilment(Ailment.PARALYSIS);
							break;
						case 1:
							attacker.setAilment(Ailment.POISON);
							break;
						case 2:
							attacker.setAilment(Ailment.SLEEP);
							break;
						}
					}
					break;
				case Abilities.CHARMEBOLZEN:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						attacker.addSecondaryAilment(getIndex(defender), SecondaryAilment.INFATUATION);
					}
					break;
				case Abilities.FINALSCHLAG:
					if (defender.getStats().getCurrentHP() == 0) {
						boolean boom = true;
						for (Pokemon poke : this.getSpeedOrder()) {
							if (poke != null && poke.getAbility().getId() == Abilities.FEUCHTIGKEIT) {
								boom = false;
							}
						}
						if (boom) {
							this.gController.getGameFrame().getFightPanel().addText(attacker.getName()
									+ " erleidet Schaden durch " + defender.getAbility().getName() + "!");
							attacker.getStats().loseHP((int) (attacker.getStats().getStats().get(Stat.HP) * 0.25));
						}
					}
					break;
				}
				switch (attacker.getAbility().getId()) {
				case Abilities.GIFTGRIFF:
					if (Main.RNG.nextFloat() < getP(hits, 0.3)) {
						this.gController.getGameFrame().getFightPanel()
								.addText(attacker.getAbility().getName() + " von " + attacker.getName() + " wirkt!");
						defender.setAilment(Ailment.POISON);
					}
					break;
				}
			}
			return true;
		}
		return false;
	}

	private double getP(int hits, double p) {
		return 1 - Math.pow(1 - p, hits);
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
			switch (pokemon.getAbility().getId()) {
			case Abilities.UMKEHRUNG:
				change *= -1;
				break;
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

		double weakness = Type.getEffectiveness(usedMove.getMoveType(attacker), defense);
		if (!this.isVisible(defense) || defense == null) {
			this.gController.getGameFrame().getFightPanel().addText("Es ist kein Gegner zu sehen!", true);
			return;
		}
		if (weakness == Type.USELESS) {
			this.gController.getGameFrame().getFightPanel()
					.addText("Die Attacke zeigt keine Wirkung auf " + defense.getName() + "!", true);
			return;
		}

		int power = this.field.getWeather().getPower(attacker, usedMove);

		switch (usedMove.getId()) {
		case Moves.STRAUCHLER:
		case Moves.FUSSKICK:
			if (defense.getWeight() <= 10) {
				power = 20;
			} else if (defense.getWeight() <= 25) {
				power = 40;
			} else if (defense.getWeight() <= 50) {
				power = 60;
			} else if (defense.getWeight() <= 100) {
				power = 80;
			} else if (defense.getWeight() <= 200) {
				power = 100;
			} else {
				power = 120;
			}
			break;
		case Moves.RAMMBOSS:
		case Moves.BRANDSTEMPEL:
			double ratio = (defense.getWeight() * 1.0) / (attacker.getWeight() * 1.0);
			if (ratio <= .2) {
				power = 120;
			} else if (ratio <= .25) {
				power = 100;
			} else if (ratio <= .33) {
				power = 80;
			} else if (ratio <= .5) {
				power = 60;
			} else {
				power = 40;
			}
			break;
		}

		switch (defense.getAbility().getId()) {
		case Abilities.WETTERFEST:
			switch (usedMove.getId()) {
			case Moves.BAUMWOLLSAAT:
			case Moves.GIFTPUDER:
			case Moves.PILZSPORE:
			case Moves.SCHLAFPUDER:
			case Moves.STACHELSPORE:
			case Moves.WUTPULVER:
				this.gController.getGameFrame().getFightPanel().addText(defense.getAbility().getName() + " "
						+ "schützt " + defense.getName() + " vor " + usedMove.getName());
				return;
			}
			break;
		case Abilities.LÄRMSCHUTZ:
			switch (usedMove.getId()) {
			case Moves.ABGESANG:
			case Moves.AUFRUHR:
			case Moves.BRÜLLER:
			case Moves.GESANG:
			case Moves.GESCHWÄTZ:
			case Moves.GRASFLÖTE:
			case Moves.HEULER:
			case Moves.KÄFERGEBRUMM:
			case Moves.KANON:
			case Moves.KREIDESCHREI:
			case Moves.METALLSOUND:
			case Moves.SÄUSELSTIMME:
			case Moves.SCHALLWELLE:
			case Moves.SCHNARCHER:
			case Moves.STANDPAUKE:
			case Moves.SUPERSCHALL:
			case Moves.ÜBERSCHALLKNALL:
			case Moves.VERTRAUENSSACHE:
			case Moves.WIDERHALL:
				this.gController.getGameFrame().getFightPanel().addText(defense.getAbility().getName() + " "
						+ "schützt " + defense.getName() + " vor " + usedMove.getName());
				return;
			}
		case Abilities.VEGETARIER:
			if (usedMove.getMoveType(attacker) == Type.GRASS) {
				this.gController.getGameFrame().getFightPanel()
						.addText(usedMove.getName() + " wird durch " + defense.getAbility().getName() + " verhindert!");
				defense.getStats().increaseStat(Stat.ATTACK, 1);
				return;
			}
			break;
		}

		double damage = power * Type.calcSTAB(attacker, usedMove);
		boolean critted = false;
		if (damage > 0 || usedMove.getDamageClass() != DamageClass.NO_DAMAGE) {
			switch (defense.getAbility().getId()) {
			case Abilities.TELEPATHIE:
				if (this.gController.getFight().isPlayer(attacker) == this.gController.getFight().isPlayer(defense)) {
					this.gController.getGameFrame().getFightPanel()
							.addText(defense.getName() + " weicht der Attacke seines Partners aus!");
					break;
				}
			default:
				for (int i = 0; i < ammount && attacker.getAilment() != Ailment.FAINTED; i++) {
					damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
					double def = 0;
					double atk = 0;
					switch (usedMove.getDamageClass()) {
					case PHYSICAL:
						if (attacker.getAbility().getId() == Abilities.UNKENNTNIS
								|| defense.getAbility().getId() == Abilities.UNKENNTNIS) {
							def = defenderStats.getFightStats().get(Stat.DEFENSE);
							atk = attackerStats.getFightStats().get(Stat.ATTACK);
						} else {
							def = defenderStats.getStats().get(Stat.DEFENSE);
							atk = attackerStats.getStats().get(Stat.ATTACK);
						}
						break;
					case SPECIAL:
						if (attacker.getAbility().getId() == Abilities.UNKENNTNIS
								|| defense.getAbility().getId() == Abilities.UNKENNTNIS) {
							def = defenderStats.getFightStats().get(Stat.SPECIALDEFENSE);
							atk = attackerStats.getFightStats().get(Stat.SPECIALATTACK);
						} else {
							def = defenderStats.getStats().get(Stat.SPECIALDEFENSE);
							atk = attackerStats.getStats().get(Stat.SPECIALATTACK);
						}
						break;
					default:
						break;
					}
					switch (attacker.getAbility().getId()) {
					case Abilities.ACHTLOS:
						if (usedMove.getCategory().contains("recoil")) {
							damage *= 1.2;
						}
						break;
					case Abilities.ANALYSE:
						if (this.alreadyActed.contains(this.getIndex(defense))) {
							damage *= 1.2;
						}
						break;
					case Abilities.DUFTNOTE:
						if (!this.alreadyActed.contains(this.getIndex(defense))) {
							if (Main.RNG.nextFloat() < 0.1) {
								defense.addSecondaryAilment(this.getIndex(attacker), SecondaryAilment.FLINCH);
							}
						}
						break;
					case Abilities.ÜBEREIFER:
						if (usedMove.getDamageClass() == DamageClass.PHYSICAL) {
							damage *= 1.5;
						}
						break;
					case Abilities.NOTDÜNGER:
						if (usedMove.getMoveType(attacker) == Type.GRASS && attacker.getStats()
								.getCurrentHP() <= attacker.getStats().getStats().get(Stat.HP) * (1.0 / 3.0)) {
							damage *= 1.5;
						}
						break;
					case Abilities.GROSSBRAND:
						if (usedMove.getMoveType(attacker) == Type.FIRE && attacker.getStats()
								.getCurrentHP() <= attacker.getStats().getStats().get(Stat.HP) * (1.0 / 3.0)) {
							damage *= 1.5;
						}
						break;
					case Abilities.STURZBACH:
						if (usedMove.getMoveType(attacker) == Type.WATER && attacker.getStats()
								.getCurrentHP() <= attacker.getStats().getStats().get(Stat.HP) * (1.0 / 3.0)) {
							damage *= 1.5;
						}
						break;
					case Abilities.HEXAPLAGA:
						if (usedMove.getMoveType(attacker) == Type.BUG && attacker.getStats()
								.getCurrentHP() <= attacker.getStats().getStats().get(Stat.HP) * (1.0 / 3.0)) {
							damage *= 1.5;
						}
						break;
					case Abilities.RIVALITÄT:
						switch (attacker.getGender()) {
						case FEMALE:
							switch (defense.getGender()) {
							case FEMALE:
								damage *= 1.25;
								break;
							case MALE:
								damage *= 0.75;
								break;
							default:
								break;
							}
							break;
						case MALE:
							switch (defense.getGender()) {
							case FEMALE:
								damage *= .75;
								break;
							case MALE:
								damage *= 1.25;
								break;
							default:
								break;
							}
							break;
						default:
							break;
						}
						break;
					case Abilities.TECHNIKER:
						if (usedMove.getPower() <= 60) {
							damage *= 1.5;
						}
						break;
					case Abilities.EISENFAUST:
						switch (usedMove.getId()) {
						case Moves.ABLEITHIEB:
						case Moves.DONNERSCHLAG:
						case Moves.EISHIEB:
						case Moves.FEUERSCHLAG:
						case Moves.FINSTERFAUST:
						case Moves.HAMMERARM:
						case Moves.HIMMELHIEB:
						case Moves.IRRSCHLAG:
						case Moves.KOMETENHIEB:
						case Moves.MEGAHIEB:
						case Moves.PATRONENHIEB:
						case Moves.POWER_PUNCH:
						case Moves.STERNENHIEB:
						case Moves.STEIGERUNGSHIEB:
						case Moves.TEMPOHIEB:
						case Moves.WUCHTSCHLAG:
							damage *= 1.2;
							break;
						}
						break;
					case Abilities.AUFWERTUNG:
						if (weakness < Type.DEFAULT) {
							weakness *= 2;
						}
						break;
					case Abilities.ROHE_GEWALT:
						if (usedMove.getStatChance() > 0 || usedMove.getAilment() != Ailment.NONE) {
							damage *= 1.3;
						}
					default:
						break;
					}
					if (this.gController.isFighting()) {
						this.playAnimation(attacker, defense, usedMove);
					}

					switch (defense.getAbility().getId()) {
					case Abilities.VOLTABSORBER:
						if (usedMove.getMoveType(attacker).equals(Type.ELECTRIC)) {
							defense.getStats().restoreHP((int) (defense.getStats().getStats().get(Stat.HP) * 0.25));
							continue;
						}
						break;
					case Abilities.H2O_ABSORBER:
						if (usedMove.getMoveType(attacker).equals(Type.WATER)) {
							defense.getStats().restoreHP((int) (defense.getStats().getStats().get(Stat.HP) * 0.25));
							continue;
						}
						break;
					case Abilities.SPECKSCHICHT:
						if (usedMove.getMoveType(attacker).equals(Type.FIRE)
								|| usedMove.getMoveType(attacker).equals(Type.ICE)) {
							damage *= .5;
						}
						break;
					case Abilities.HITZESCHUTZ:
						if (usedMove.getMoveType(attacker).equals(Type.FIRE)) {
							damage *= .5;
						}
						break;
					case Abilities.FILTER:
					case Abilities.FELSKERN:
						if (weakness > Type.DEFAULT) {
							weakness *= .75;
						}
						break;
					case Abilities.MULTISCHUPPE:
						if (defense.getStats().getCurrentHP() == defense.getStats().getStats().get(Stat.HP)) {
							damage *= .5;
						}
					}

					int crit = 1;
					switch (defense.getAbility().getId()) {
					case Abilities.KAMPFPANZER:
					case Abilities.PANZERHAUT:
						break;
					default:
						float pCrit = this.rng.nextFloat();
						switch (usedMove.getCrit(attacker) + 1) {
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
					}
					if (crit == 2) {
						this.gController.getGameFrame().getFightPanel().addText("Ein Volltreffer!", true);
						critted = true;
						switch (attacker.getAbility().getId()) {
						case Abilities.SUPERSCHÜTZE:
							crit *= 1.5;
						}
					}
					damage = (weakness
							* ((attackerStats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2) * crit
							* ((this.rng.nextFloat() * 0.15f + 0.85) / 1));
					damage = Math.max(damage, 1);

					if (usedMove.getCategory().contains("ohko")) {
						if (defense.getAbility().getId() == Abilities.ROBUSTHEIT) {
							this.gController.getGameFrame().getFightPanel()
									.addText("Die K.O. Attacke ist wirkungslos gegen " + defense.getName() + "!");
							return;
						}
						damage = defense.getStats().getCurrentHP() * 10000;
					}
					switch (defense.getAbility().getId()) {
					case Abilities.ROBUSTHEIT:
						if (defense.getStats().getCurrentHP() == defense.getStats().getStats().get(Stat.HP)
								&& damage >= defense.getStats().getCurrentHP()) {
							damage = defense.getStats().getCurrentHP() - 1;
						}
						break;
					}
					damage = defenderStats.loseHP((int) damage);
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
						switch (defense.getAbility().getId()) {
						case Abilities.KLOAKENSOSSE:
							attackerStats.loseHP((int) (damage * (usedMove.getDrain() / 100.0)));
							this.gController.getGameFrame()
									.getFightPanel().addText(attacker.getName()
											+ " verletzt sich durch die Fähigkeit von " + defense.getName() + "!",
											true);
							break;
						default:
							attackerStats.restoreHP((int) (damage * (usedMove.getDrain() / 100.0)));
							this.gController.getGameFrame().getFightPanel()
									.addText(defense.getName() + " wurde Energie abgesaugt!", true);
							break;
						}
					} else if (usedMove.getDrain() < 0) {
						switch (attacker.getAbility().getId()) {
						case Abilities.STEINHAUPT:
						case Abilities.MAGIESCHILD:
							break;
						default:
							if (attackerStats.loseHP((int) Math.abs(damage * (usedMove.getDrain() / 100.0))) > 0) {
								this.gController.getGameFrame().getFightPanel()
										.addText(attacker.getName() + " hat sich durch den Rückstoß verletzt!", true);
							}
							break;
						}
					}
					this.gController.getGameFrame().getFightPanel().updatePanels();
					switch (defense.getAbility().getId()) {
					case Abilities.ROBUSTHEIT:
						if (damage == defense.getStats().getStats().get(Stat.HP) - 1) {
							this.gController.getGameFrame().getFightPanel().addText(defense.getName()
									+ " hält mithilfe von " + defense.getAbility().getName() + " durch!", false);
						}
						break;
					case Abilities.REDLICHKEIT:
						if (usedMove.getMoveType(attacker) == Type.DARK) {
							defense.getStats().increaseStat(Stat.ATTACK, 1);
						}
						break;
					case Abilities.HASENFUSS:
						switch (usedMove.getMoveType(attacker)) {
						case GHOST:
						case BUG:
						case DARK:
							defense.getStats().increaseStat(Stat.SPEED, 1);
							break;
						default:
							break;
						}
					}
					switch (usedMove.getDamageClass()) {
					case PHYSICAL:
						switch (defense.getAbility().getId()) {
						case Abilities.RAUHAUT:
						case Abilities.EISENSTACHEL:
							attacker.getStats().loseHP((int) (attacker.getStats().getStats().get(Stat.HP) * 1.0 / 8));
							this.gController.getGameFrame().getFightPanel()
									.addText(attacker.getName() + " verletzt sich" + " durch "
											+ defense.getAbility().getName() + " von " + defense.getName());
							break;
						case Abilities.BRUCHRÜSTUNG:
							this.gController.getGameFrame().getFightPanel().addText(defense.getAbility().getName() + " "
									+ "von " + defense.getName() + " aktiviert sich!");
							defense.getStats().decreaseStat(Stat.DEFENSE, 1);
							defense.getStats().increaseStat(Stat.SPEED, 2);
							break;
						case Abilities.MUMIE:
							this.gController.getGameFrame().getFightPanel()
									.addText(defense.getAbility().getName() + " von " + defense.getName() + " wirkt!");
							attacker.setFightingAbility(defense.getAbility());
							break;
						}
						break;
					default:
						break;
					}

					// damage = usedMove.getPower() * Type.calcSTAB(attacker,
					// usedMove);
				}
				if (weakness >= Type.STRONG) {
					this.gController.getGameFrame().getFightPanel().addText("Die Attacke war sehr effektiv!", true);
				} else if (weakness <= Type.WEAK) {
					this.gController.getGameFrame().getFightPanel().addText("Die Attacke war nicht sehr effektiv!",
							true);
				}
			}

		} else {
			switch (defense.getAbility().getId()) {
			case Abilities.VOLTABSORBER:
				if (usedMove.getMoveType(attacker).equals(Type.ELECTRIC)) {
					defense.getStats().restoreHP((int) (defense.getStats().getStats().get(Stat.HP) * 0.25));
				}
				break;
			case Abilities.H2O_ABSORBER:
				if (usedMove.getMoveType(attacker).equals(Type.WATER)) {
					defense.getStats().restoreHP((int) (defense.getStats().getStats().get(Stat.HP) * 0.25));
				}
				break;
			default:
				this.playAnimation(attacker, defense, usedMove);
				if (!attacker.equals(defense)
						&& (usedMove.getAilment() != null || usedMove.getAilment() != Ailment.NONE
								|| usedMove.getSecondaryAilment() != null)
						&& defense.getSecondaryAilments().containsKey(SecondaryAilment.MAGICCOAT)) {
					this.gController.getGameFrame().getFightPanel()
							.addText(SecondaryAilment.MAGICCOAT.getAffected().replace("@pokemon", defense.getName()));
					defense = attacker;
				} else if (usedMove.getCategory().contains("teleport")) {
					if (this.canEscape()) {
						this.gController.getGameFrame().getFightPanel()
								.addText(attacker.getName() + " flieht aus dem Kampf!", true);
						this.gController.endFight();
					} else {
						this.gController.getGameFrame().getFightPanel().addText("Es schlägt fehl!", true);
					}
				}
				break;
			}

		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (critted) {
			switch (defense.getAbility().getId()) {
			case Abilities.KURZSCHLUSS:
				this.gController.getGameFrame().getFightPanel()
						.addText(defense.getName() + " maximiert seinen Angriff!");
				defense.getStats().increaseStat(Stat.ATTACK, 100);
				break;
			}
		}

		if (usedMove.getHealing() > 0) {
			attackerStats.restoreHP((int) (attackerStats.getStats().get(Stat.HP) * (usedMove.getHealing() / 100)));
			this.gController.getGameFrame().getFightPanel()
					.addText("Die KP von " + attacker.getName() + " wurden aufgefrischt!", true);
			this.gController.getGameFrame().getFightPanel().updatePanels();
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (defense.getAbility().getId() != Abilities.ROHE_GEWALT) {
			if (usedMove.checkStatChange(attacker)) {
				if (usedMove.checkUserBuff()) {
					this.buff(attacker, usedMove);
				}
				if (usedMove.checkEnemyBuff()) {
					this.buff(defense, usedMove);
				}
			}

			this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();

			if (this.rng.nextFloat() * 100 < usedMove.getAilmentChance(attacker)
					|| usedMove.getAilmentChance(attacker) == 0) {
				if (((usedMove.getAilment() != Ailment.NONE && usedMove.getAilment() != null)
						&& defense.setAilment(usedMove.getAilment()))
						&& (damage == 0 || defense.getAbility().getId() != Abilities.PUDERABWEHR)) {
					this.gController.getGameFrame().getFightPanel().addText(
							defense.getName() + " wurde " + Ailment.getText(usedMove.getAilment()) + "!", true);
					switch (defense.getAbility().getId()) {
					case Abilities.SYNCHRO:
						this.gController.getGameFrame().getFightPanel()
								.addText("Synchro von " + defense.getName() + " wirkt!", false);
						attacker.setAilment(usedMove.getAilment());
						break;
					}
				} else if ((usedMove.getSecondaryAilment() != null)) {
					defense.addSecondaryAilment(this.getIndex(attacker), usedMove.getSecondaryAilment());
				}
				this.gController.getGameFrame().getFightPanel().updatePanels();
			}
		}

		this.gController.getGameFrame().getFightPanel().getTextLabel().waitText();
	}

	public boolean canBeSendOut(Pokemon p) {
		return !this.checkDead(p) && this.notFighting(p);
	}

	public void updateAttackTargets(Pokemon oldTarget, Pokemon newTarget) {
		if(oldTarget != null) {
			for (Pokemon p : this.turnAttacks.keySet()) {
				Attack attack = this.turnAttacks.get(p);
				attack.updateTargets(oldTarget, newTarget);
				this.turnAttacks.put(p, attack);
			}
		}
	}

	public boolean sendOut(int index, Pokemon replacement) {
		if (!this.canBeSendOut(replacement)) {
			return false;
		}
		System.out.println("send out:" + this.getPokemon(index));
		updateAttackTargets(this.getPokemon(index), replacement);
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
		if (user.getSecondaryAilments().containsKey(SecondaryAilment.TORMENT)) {
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
			if (this.leftPlayerPokemon != null) {
				this.leftPlayerPokemon.getStats().stopFight();
			}
			this.leftPlayerPokemon = this.leftPlayerTeam.getTeam()[0];
			if (this.leftPlayerPokemon != null) {
				this.leftPlayerPokemon.startFight();
				this.setVisible(this.leftPlayerPokemon, true);
			}
		} else if (isDouble()) {
			if (this.rightPlayerPokemon != null) {
				this.rightPlayerPokemon.getStats().stopFight();
			}
			if (this.leftPlayerTeam.equals(this.rightPlayerTeam)) {
				if (this.checkDead(this.rightPlayerPokemon)) {
					this.removeParticipant(this.rightPlayerPokemon);
				}
				this.rightPlayerPokemon = this.leftPlayerTeam.getTeam()[1];
			} else {
				if (this.rightPlayerTeam != null) {
					this.rightPlayerPokemon = this.rightPlayerTeam.getTeam()[0];
				}
			}
			if (this.rightPlayerPokemon != null) {
				this.rightPlayerPokemon.startFight();
				this.setVisible(this.rightPlayerPokemon, true);
			}
		}
		this.addParticipants(true);
		this.addParticipants(false);
		this.gController.updateFight();
	}

	private void setEnemy(boolean left) {
		if (left) {
			if (this.leftOpponentPokemon != null) {
				this.leftOpponentPokemon.getStats().stopFight();
			}
			if (this.leftOpponentTeam != null) {
				this.leftOpponentPokemon = this.leftOpponentTeam.getTeam()[0];
			}
			if (this.leftOpponentPokemon != null) {
				this.leftOpponentPokemon.startFight();
				this.setVisible(this.leftOpponentPokemon, true);
			}
		} else {
			if (this.rightOpponentPokemon != null) {
				this.rightOpponentPokemon.getStats().stopFight();
			}
			if (this.rightOpponentTeam != null) {
				if (this.leftOpponentTeam.equals(this.rightOpponentTeam)) {
					this.rightOpponentPokemon = this.leftOpponentTeam.getTeam()[1];
				} else {
					this.rightOpponentPokemon = this.rightOpponentTeam.getTeam()[0];
				}
			}
			if (rightOpponentPokemon != null) {
				this.rightOpponentPokemon.startFight();
				this.setVisible(this.rightOpponentPokemon, true);
			}
		}
		this.clearParticipants(left);
		this.addParticipants(left);
		this.gController.updateFight();
	}

	private void clearParticipants(boolean left) {
		System.out.println("clear: " + left);
		if (left) {
			this.leftParticipants.clear();
		} else {
			this.rightParticipants.clear();
		}
	}

	private void addParticipants(boolean left) {
		System.out.println("add: " + left);
		if (left) {
			this.leftParticipants.add(this.leftPlayerPokemon);
			if (this.isDouble && this.rightPlayer instanceof Player) {
				this.leftParticipants.add(this.rightPlayerPokemon);
			}
		} else {
			this.rightParticipants.add(this.leftPlayerPokemon);
			if (this.isDouble && this.rightPlayer instanceof Player) {
				this.rightParticipants.add(this.rightPlayerPokemon);
			}
		}
	}

	public void removeParticipant(Pokemon p) {
		System.out.println("remove: " + p);
		this.leftParticipants.remove(p);
		this.rightParticipants.remove(p);
	}

	public boolean playerDead() {
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
		if (replaceLeft && !replaceRight) {
			this.updateAttackTargets(this.leftPlayerPokemon, this.rightPlayerPokemon);
		} else if (!replaceLeft && replaceRight) {
			this.updateAttackTargets(this.rightPlayerPokemon, this.leftPlayerPokemon);
		}
//		if (replaceLeft) {
//			this.setCurrentFightOption(FightOption.POKEMON);
//			this.gController.getGameFrame().getPokemonPanel().update(LEFT_PLAYER);
//			while (this.getCurrentFightOption().equals(FightOption.POKEMON)) {
//				Thread.yield();
//			}
//		}
//		if (replaceRight) {
//			this.setCurrentFightOption(FightOption.POKEMON);
//			this.gController.getGameFrame().getPokemonPanel().update(RIGHT_PLAYER);
//			while (this.getCurrentFightOption().equals(FightOption.POKEMON)) {
//				Thread.yield();
//			}
//		}
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
		if (replaceLeft && !replaceRight) {
			this.updateAttackTargets(this.leftOpponentPokemon, this.rightOpponentPokemon);
		} else if (!replaceLeft && replaceRight) {
			this.updateAttackTargets(this.rightOpponentPokemon, this.leftOpponentPokemon);
		}
//		if (replaceLeft) {
//			if (this.getFirstNonFightingPokemon(this.leftOpponentTeam) != null) {
//				this.sendOut(LEFT_OPPONENT, this.getFirstNonFightingPokemon(this.leftOpponentTeam));
//			}
//		}
//		if (replaceRight) {
//			if (this.getFirstNonFightingPokemon(this.rightOpponentTeam) != null) {
//				this.sendOut(RIGHT_OPPONENT, this.getFirstNonFightingPokemon(this.rightOpponentTeam));
//			}
//		}
		return false;
	}

	public FightOption getCurrentFightOption() {
		return this.currentFightOption;
	}

	public void setCurrentFightOption(FightOption currentFightOption) {
		this.currentFightOption = currentFightOption;
	}

	public HashSet<Pokemon> getParticipants(boolean left) {
		System.out.println("get: " + left + " - " + (left ? this.leftParticipants : this.rightParticipants));
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
		if (p != null) {
			this.visible.put(p, v);
		}
	}

	public boolean isVisible(Pokemon pokemon) {
		if (this.visible.get(pokemon) == null) {
			return false;
		}
		return this.visible.get(pokemon);

	}

	public Pokemon getCurrentPokemon() {
		return this.getPokemon(this.activePokemon);
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

	public void registerAttack(Attack attack) {
		if (this.activePokemon == LEFT_PLAYER) {
			this.turnAttacks = new HashMap<>();
		}
		this.turnAttacks.put(attack.getSource(), attack);
		if (this.isDouble) {
			nextPokemon();
			if (this.activePokemon == RIGHT_PLAYER) {
				gController.getGameFrame().getFightPanel().showMenu();
				return;
			}
		}
		gController.getGameFrame().getFightPanel().startRound();

	}

	public boolean cancelAttack() {
		if (this.activePokemon == RIGHT_PLAYER) {
			this.turnAttacks.put(this.getPokemon(LEFT_PLAYER), null);
			return true;
		}
		return false;
	}

	/**
	 * @return A String object that represents the text that should be displayed
	 *         at the start of the fight.
	 * @author CutyCupy
	 */
	public String getEntryText() {
		if (!this.escapable) {
			if (this.isDouble && !this.rightOpponent.equals(this.leftOpponent)) {
				return "Eine Herausforderung von " + this.leftOpponent.getName() + " und "
						+ this.rightOpponent.getName() + "!";
			} else {
				return "Eine Herausforderung von " + this.leftOpponent.getName() + "!";
			}
		}
		return null;
	}

	/**
	 * @return
	 * @author CutyCupy
	 */
	public int getActivePlayer() {
		return this.activePokemon;
	}

	public boolean isActiveTurn() {
		return this.activeTurn;
	}

	public void nextPokemon() {
		if (this.activePokemon == LEFT_PLAYER && this.gController.getMainCharacter().equals(this.rightPlayer)) {
			this.activePokemon = RIGHT_PLAYER;
			gController.getGameFrame().getFightPanel().showMenu();
		} else {
			this.activePokemon = LEFT_PLAYER;
		}
	}

	public void createAIAttacks() {
		for (IAI ai : this.ais) {
			if (this.getPokemon(ai.getPosition()) != null) {
				Attack a = ai.getAttack();
				if (!this.turnAttacks.containsKey(a.getSource())) {
					this.turnAttacks.put(a.getSource(), a);
				}
			}
		}
	}
}

package de.alexanderciupka.pokemon.fighting;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.NPC;
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

	private Team playerTeam;
	private Pokemon player;
	private Team enemyTeam;
	private Pokemon enemy;
	private FightOption currentFightOption;
	private Character enemyCharacter;
	private HashSet<Pokemon> participants;
	private Random rng;
	private GameController gController;
	private boolean escape;
	private int turn;

	private HashMap<Pokemon, Move> lastMoves;
	private HashMap<Pokemon, Move> chargeMoves;
	private HashMap<Pokemon, Boolean> needsRecharge;

	private SimpleEntry<Boolean, Boolean> visible;

	public boolean won;

	public Fighting(Pokemon pokemonTwo) {
		init();
		playerTeam = new Team(gController.getMainCharacter().getTeam().getTeam(), gController.getMainCharacter());
		this.player = playerTeam.getFirstFightPokemon();
		enemyTeam = new Team(null);
		enemyTeam.addPokemon(pokemonTwo);
		this.enemy = pokemonTwo;
		escape = true;
		getStartPokemon();
		player.startFight();
		enemy.startFight();
	}

	public Fighting(Character enemyCharacter) {
		init();
		this.enemyCharacter = enemyCharacter;
		playerTeam = new Team(gController.getMainCharacter().getTeam().getTeam(), gController.getMainCharacter());
		this.player = playerTeam.getFirstFightPokemon();
		enemyTeam = new Team(enemyCharacter.getTeam().getTeam(), enemyCharacter);
		this.enemy = enemyTeam.getFirstFightPokemon();
		escape = false;
		getStartPokemon();
		player.startFight();
		enemy.startFight();
	}

	private void init() {
		lastMoves = new HashMap<>();
		chargeMoves = new HashMap<>();
		needsRecharge = new HashMap<>();
		turn = 0;
		gController = GameController.getInstance();
		rng = new Random();
		currentFightOption = FightOption.FIGHT;
		participants = new HashSet<Pokemon>();
		this.visible = new SimpleEntry<Boolean, Boolean>(true, true);

	}

	private void getStartPokemon() {
		player = playerTeam.getFirstFightPokemon();
		sendOut(playerTeam.getIndex(player));
	}

	public boolean isPlayerStart(Move playerMove, Move enemyMove) {
		if (playerMove.getPriority() > enemyMove.getPriority()) {
			return true;
		} else if (playerMove.getPriority() == enemyMove.getPriority()) {
			if (player.getStats().getFightStats().get(Stat.SPEED) > enemy.getStats().getFightStats().get(Stat.SPEED)) {
				return true;
			} else if (player.getStats().getFightStats().get(Stat.SPEED) == enemy.getStats().getFightStats()
					.get(Stat.SPEED)) {
				return rng.nextBoolean();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void startRound(Move playerMove, Move enemyMove) {
		if (playerMove == null) {
			playerMove = gController.getInformation().getMoveByName("Verzweifler");
		}
		if (enemyMove == null) {
			enemyMove = gController.getInformation().getMoveByName("Verzweifler");
		}
		boolean playerStarts = gController.getFight().isPlayerStart(playerMove, enemyMove);
		if (playerStarts) {
			if (gController.getFight().attack(player, enemy, playerMove)) {
				gController.getFight().attack(enemy, player, enemyMove);
			}
		} else {
			if (gController.getFight().attack(enemy, player, enemyMove)) {
				gController.getFight().attack(player, enemy, playerMove);
			} else {
//				gController.getFight().setCurrentFightOption(FightOption.POKEMON);
				gController.repaint();
			}
		}
		endTurn();
	}

	public void endTurn() {
		if (gController.isFighting()) {
			player.afterTurnDamage();
			enemy.afterTurnDamage();
			if (gController.checkDead(player)) {
				gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde besiegt!");
				gController.getGameFrame().getFightPanel().updatePanels();
				if (!gController.loseFight()) {
					gController.repaint();
//					setPlayer();
				}
			}
			if (gController.checkDead(enemy)) {
				gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde besiegt!");
				if (!gController.winFight()) {
//					gController.getGameFrame().getFightPanel().setEnemy();
				}
			}
			increaseTurn();
		}
	}

	public boolean attack(Pokemon attacker, Pokemon defender) {
		return attack(attacker, defender, attacker.getMove(defender));
	}

	public boolean attack(Pokemon attacker, Pokemon defender, Move move) {
		if(!this.gController.isFighting()) {
			return true;
		}
		if (this.needsRecharge(attacker)) {
			gController.getGameFrame().getFightPanel().addText(attacker.getName() + " muss sich erholen.");
			this.setRecharge(attacker, false);
			return true;
		}
		String message = attacker.canAttack();
		if (message != null) {
			gController.getGameFrame().getFightPanel().addText(message);
			this.setVisible(attacker, true);
			this.chargeMoves.put(attacker, null);
		} else {
			this.lastMoves.put(attacker, move);
			gController.getGameFrame().getFightPanel()
					.addText(attacker.getName() + " setzt " + move.getName() + " ein!");
			if (!hit(attacker, defender, move)) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke ging daneben");
				setRecharge(attacker, false);
				setVisible(attacker, true);
				// gController.getGameFrame().getFightPanel().updatePanels();
			}
		}
		gController.getGameFrame().getFightPanel().updatePanels();
		boolean dead = false;
		if (gController.checkDead(player)) {
			gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde besiegt!");
			gController.getGameFrame().getFightPanel().updatePanels();
			if (!gController.loseFight()) {
				gController.repaint();
//				setPlayer();
			}
			dead = true;
		}
		if (gController.checkDead(enemy)) {
			gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde besiegt!");
			if (!gController.winFight()) {
				gController.getGameFrame().getFightPanel().setEnemy();
				gController.getGameFrame().getFightPanel().updatePanels();
			}
			dead = true;
		}
		return !dead;
	}

	// public boolean playerAttack(Move move) {
	// String message = player.canAttack();
	// if (message != null) {
	// gController.getGameFrame().getFightPanel().addText(message);
	// gController.getGameFrame().getFightPanel().updatePanels();
	// return true;
	// }
	// gController.getGameFrame().getFightPanel().addText(player.getName() + " setzt
	// " + move.getName() + " ein!");
	// if (!playerHit(move)) {
	// gController.getGameFrame().getFightPanel().addText("Die Attacke ging
	// daneben");
	// return true;
	// }
	// gController.getGameFrame().getFightPanel().updatePanels();
	// if (gController.checkEnemyDead()) {
	// gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde
	// besiegt!");
	// if (!gController.winFight()) {
	// gController.getGameFrame().getFightPanel().setEnemy();
	// }
	// return false;
	// } else if (gController.checkPlayerDead()) {
	// gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde
	// besiegt!");
	// gController.getGameFrame().getFightPanel().updatePanels();
	// if (!gController.loseFight()) {
	// gController.repaint();
	// setPlayer();
	// }
	// return false;
	// } else {
	// if (move.checkStatChange()) {
	// if (move.checkUserBuff()) {
	// buff(player, move);
	// }
	// if (move.checkEnemyBuff()) {
	// buff(enemy, move);
	// }
	// }
	// return true;
	// }
	// }
	//
	// public boolean enemyAttack() {
	// return enemyAttack(enemy.getMove(this.player));
	// }
	//
	// public boolean enemyAttack(Move move) {
	// String message = enemy.canAttack();
	// if (message != null) {
	// gController.getGameFrame().getFightPanel().addText(message);
	// gController.getGameFrame().getFightPanel().updatePanels();
	// return true;
	// }
	// gController.getGameFrame().getFightPanel().addText(enemy.getName() + " setzt
	// " + move.getName() + " ein!");
	// if (!enemyHit(move)) {
	// gController.getGameFrame().getFightPanel().addText("Die Attacke ging
	// daneben");
	// return true;
	// }
	// gController.getGameFrame().getFightPanel().updatePanels();
	// if (gController.checkPlayerDead()) {
	// gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde
	// besiegt!");
	// gController.getGameFrame().getFightPanel().updatePanels();
	// if (!gController.loseFight()) {
	// gController.repaint();
	// setPlayer();
	// }
	// return false;
	// } else if (gController.checkEnemyDead()) {
	// gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde
	// besiegt!");
	// gController.getGameFrame().getFightPanel()
	// .addText(player.getName() + " erhält " + (int) ((enemy.getStats().getLevel()
	// * 1.25 * 100) / 7));
	// gController.getGameFrame().getFightPanel().updatePanels();
	// if (!gController.winFight()) {
	// enemy = gController.getFight().getEnemy();
	// gController.getGameFrame().getFightPanel().setEnemy();
	// }
	// return false;
	// } else {
	// if (move.checkStatChange()) {
	// if (move.checkUserBuff()) {
	// buff(enemy, move);
	// }
	// if (move.checkEnemyBuff()) {
	// buff(player, move);
	// }
	// }
	//
	// return true;
	// }
	// }

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
		float p = rng.nextFloat() * 100;
		if (p < hitChance) {
			for (String category : move.getCategory().split("\\+")) {
				if (category.equals("recharge")) {
					this.setRecharge(attacker, true);
				} else if (category.equals("explosion")) {
					if (attacker.equals(this.player)) {
						this.gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation("explosion");
					} else {
						this.gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation("explosion");
					}
					attacker.getStats().loseHP(attacker.getStats().getCurrentHP());
				}
			}
			if(defender.getSecondaryAilments().contains(SecondaryAilment.PROTECTED)) {
				gController.getGameFrame().getFightPanel().addText(SecondaryAilment.PROTECTED.getAffected()
						.replace("@pokemon", defender.getName()));
				return true;
			}
			int hits = rng.nextInt(move.getMaxHits() - move.getMinHits() + 1) + move.getMinHits();
			switch (move.getTarget()) {
			case ALL:
				damageCalculation(attacker, defender, move, hits);
				damageCalculation(attacker, attacker, move, hits);
				break;
			case OPPONENT:
				damageCalculation(attacker, defender, move, hits);
				break;
			case USER:
				damageCalculation(attacker, attacker, move, hits);
				break;
			default:
				break;

			}
			gController.sleep(150);
			return true;
		}
		return false;
	}

	// public boolean playerHit(Move playerMove) {
	// double hitChance = playerMove.getAccuracy() *
	// (player.getStats().getFightStats().get(Stat.ACCURACY)
	// / enemy.getStats().getFightStats().get(Stat.EVASION));
	// playerMove.reducePP();
	// if (rng.nextFloat() * 100 < hitChance && playerMove.getCurrentPP() > 0) {
	// int hits = rng.nextInt(playerMove.getMaxHits() - playerMove.getMinHits() + 1)
	// + playerMove.getMinHits();
	// switch(playerMove.getTarget()) {
	// case ALL:
	// damageCalculation(player, enemy, playerMove, hits);
	// damageCalculation(player, player, playerMove, hits);
	// break;
	// case OPPONENT:
	// damageCalculation(player, enemy, playerMove, hits);
	// break;
	// case USER:
	// damageCalculation(player, player, playerMove, hits);
	// break;
	// default:
	// break;
	//
	// }
	// gController.sleep(150);
	// return true;
	// }
	// return false;
	// }
	//
	// public boolean enemyHit(Move enemyMove) {
	// double hitChance = enemyMove.getAccuracy() *
	// (enemy.getStats().getFightStats().get(Stat.ACCURACY)
	// / player.getStats().getFightStats().get(Stat.EVASION));
	// enemyMove.reducePP();
	// if (enemyMove.getAccuracy() > 100 || rng.nextFloat() * 100 < hitChance &&
	// enemyMove.getCurrentPP() > 0) {
	// int hits = rng.nextInt(enemyMove.getMaxHits() - enemyMove.getMinHits() + 1) +
	// enemyMove.getMinHits();
	// switch(enemyMove.getTarget()) {
	// case ALL:
	// damageCalculation(enemy, player, enemyMove, hits);
	// damageCalculation(enemy, enemy, enemyMove, hits);
	// break;
	// case OPPONENT:
	// damageCalculation(enemy, player, enemyMove, hits);
	// break;
	// case USER:
	// damageCalculation(enemy, enemy, enemyMove, hits);
	// break;
	// default:
	// break;
	//
	// }
	// return true;
	// }
	// return false;
	// }

	public void buff(Pokemon pokemon, Move move) {
		for (Stat s : Stat.values()) {
			int change = move.changeStat(s);
			if (change == 0) {
				continue;
			}
			if (gController.isFighting()) {
				if (pokemon.equals(gController.getFight().getPlayer())) {
					gController.getGameFrame().getFightPanel().getPlayerAnimation()
							.playAnimation(change < 0 ? "debuff" : "buff");
				} else {
					gController.getGameFrame().getFightPanel().getEnemyAnimation()
							.playAnimation(change < 0 ? "debuff" : "buff");
				}
			}
			if (change < 0) {
				pokemon.getStats().decreaseStat(s, Math.abs(change));
			} else if (change > 0) {
				pokemon.getStats().increaseStat(s, change);
			}
		}
	}

	public void selfAttack(Pokemon pokemon) {
		Stats stats = pokemon.getStats();
		double damage = 40;
		double def = stats.getFightStats().get(Stat.DEFENSE);
		double atk = stats.getFightStats().get(Stat.ATTACK);
		if (pokemon.equals(player)) {
			gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation("punch");
		} else {
			gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation("punch");
		}
		SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
		stats.loseHP((int) (((stats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
				* ((rng.nextFloat() * 0.15f + 0.85) / 1)));
	}

	private void playAnimation(Pokemon attacker, Pokemon defense, Move usedMove) {
		if (attacker.equals(gController.getFight().getPlayer())) {
			gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation(usedMove.getUserAnimation());
		} else {
			gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation(usedMove.getUserAnimation());
		}
		if (defense.equals(gController.getFight().getPlayer())) {
			gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation(usedMove.getTargetAnimation());
		} else {
			gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation(usedMove.getTargetAnimation());
		}
	}

	private void damageCalculation(Pokemon attacker, Pokemon defense, Move usedMove, int ammount) {
		Stats attackerStats = attacker.getStats();
		Stats defenderStats = defense.getStats();
		double weakness = Type.getEffectiveness(usedMove.getMoveType(), defense.getTypes());
		if (!isVisible(defense)) {
			gController.getGameFrame().getFightPanel().addText("Es ist kein Gegner zu sehen!", true);
			return;
		}
		if (weakness == Type.USELESS) {
			gController.getGameFrame().getFightPanel().addText("Die Attacke zeigte keine Wirkung!", true);
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
				if (gController.isFighting()) {
					playAnimation(attacker, defense, usedMove);
				}
				int crit = 1;
				float pCrit = rng.nextFloat();
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
					gController.getGameFrame().getFightPanel().addText("Ein Volltreffer!", true);
				}
				damage = (weakness * ((attackerStats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
						* crit * ((rng.nextFloat() * 0.15f + 0.85) / 1));
				damage = Math.max(damage, 1);
				if(usedMove.getCategory().contains("ohko")) {
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
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.sleep(150);

				if (usedMove.getDrain() > 0) {
					attackerStats.restoreHP((int) (damage * (usedMove.getDrain() / 100.0)));
					gController.getGameFrame().getFightPanel().addText(defense.getName() + " wurde Energie abgesaugt!",
							true);
				} else if (usedMove.getDrain() < 0) {
					attackerStats.loseHP((int) Math.abs(damage * (usedMove.getDrain() / 100.0)));
					gController.getGameFrame().getFightPanel()
							.addText(attacker.getName() + " hat sich durch den Rückstoß verletzt!", true);
				}
				gController.getGameFrame().getFightPanel().updatePanels();
				damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
			}
			if (weakness >= Type.STRONG) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war sehr effektiv!", true);
			} else if (weakness <= Type.WEAK) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war nicht sehr effektiv!", true);
			}
		} else {
			playAnimation(attacker, defense, usedMove);
			if(!attacker.equals(defense) && (usedMove.getAilment() != null || usedMove.getAilment() != Ailment.NONE ||
					usedMove.getSecondaryAilment() != null) &&
					defense.getSecondaryAilments().contains(SecondaryAilment.MAGICCOAT)) {
				gController.getGameFrame().getFightPanel().addText(
						SecondaryAilment.MAGICCOAT.getAffected().replace("@pokemon", defense.getName()));
				defense = attacker;
			} else if(usedMove.getCategory().contains("teleport")) {
				if(this.canEscape()) {
					gController.getGameFrame().getFightPanel().addText(
							attacker.getName() + " flieht aus dem Kampf!");
					gController.endFight();
				} else {
					gController.getGameFrame().getFightPanel().addText("Es schlägt fehl!");
				}
			}
		}

		gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (usedMove.getHealing() > 0) {
			attackerStats.restoreHP((int) (attackerStats.getStats().get(Stat.HP) * (usedMove.getHealing() / 100)));
			gController.getGameFrame().getFightPanel()
					.addText("Die KP von " + attacker.getName() + " wurden aufgefrischt!", true);
			gController.getGameFrame().getFightPanel().updatePanels();
		}

		gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (usedMove.checkStatChange()) {
			if (usedMove.checkUserBuff()) {
				buff(attacker, usedMove);
			}
			if (usedMove.checkEnemyBuff()) {
				buff(defense, usedMove);
			}
		}

		gController.getGameFrame().getFightPanel().getTextLabel().waitText();

		if (rng.nextFloat() * 100 < usedMove.getAilmentChance() || usedMove.getAilmentChance() == 0) {
			if (((usedMove.getAilment() != Ailment.NONE && usedMove.getAilment() != null)
					&& defense.setAilment(usedMove.getAilment()))) {
				gController.getGameFrame().getFightPanel()
						.addText(defense.getName() + " wurde " + Ailment.getText(usedMove.getAilment()) + "!", true);
			} else if ((usedMove.getSecondaryAilment() != null)) {
				defense.addSecondaryAilment(usedMove.getSecondaryAilment());
			}
			gController.getGameFrame().getFightPanel().updatePanels();
		}

		gController.getGameFrame().getFightPanel().getTextLabel().waitText();
	}

	public boolean canBeSendOut(int index) {
		if (playerTeam.getTeam()[index].getStats().getCurrentHP() > 0 && index != 0) {
			return true;
		}
		return false;
	}

	public void sendOut(int index) {
		playerTeam.swapPokemon(0, index);
		setPlayer();
		participants.add(player);
	}

	public Pokemon getPlayer() {
		return player;
	}

	public Move canUse(Pokemon user, Move move) {
		if (chargeMoves.get(user) != null) {
			return chargeMoves.get(user);
		}
		if (user.getSecondaryAilments().contains(SecondaryAilment.TORMENT)) {
			if (move.equals(lastMoves.get(user))) {
				return null;
			}
		}
		return move.isDisabled() ? null : move;
	}

	private void setPlayer() {
		this.player = playerTeam.getTeam()[0];
		this.player.startFight();
//		participants.add(player);
		// gController.getGameFrame().getFightPanel().addText("Du schaffst das "
		// + this.player.getName() + "!");
		this.visible = new SimpleEntry<Boolean, Boolean>(true, this.visible.getValue());
		gController.updateFight();
	}

	private void setEnemy() {
		this.enemy = enemyTeam.getFirstFightPokemon();
		enemy.startFight();
		if(participants == null) {
			participants = new HashSet<>();
		}
		participants.clear();
		participants.add(player);
		this.visible = new SimpleEntry<Boolean, Boolean>(this.visible.getKey(), true);
		gController.updateFight();
	}

	public Pokemon getEnemy() {
		return enemy;
	}

	/**
	 * @return false if player has another Pokemon
	 */
	public boolean playerDead() {
		participants.remove(player);
		if (playerTeam.getFirstFightPokemon() == null) {
			if (enemyCharacter != null) {
				enemyCharacter.getTeam().restoreTeam();
			}
			return true;
		}
		setCurrentFightOption(FightOption.POKEMON);
		gController.getGameFrame().getPokemonPanel().update();
		while(getCurrentFightOption().equals(FightOption.POKEMON)) {
			Thread.yield();
		}
		return false;
	}

	/**
	 * @return false if enemy has another Pokemon
	 */
	public boolean enemyDead() {
		gController.getGameFrame().getFightPanel().removeEnemy();
		if (enemyTeam.getFirstFightPokemon() == null) {
			if (enemyCharacter != null) {
				gController.getGameFrame().getFightPanel().addText(enemyCharacter.getName() + " wurde besiegt!");
				enemyCharacter.defeated(true);
			}
			return true;
		}
		setEnemy();
		return false;
	}


	public FightOption getCurrentFightOption() {
		return currentFightOption;
	}

	public void setCurrentFightOption(FightOption currentFightOption) {
		this.currentFightOption = currentFightOption;
	}

	public Pokemon getPokemon(int index) {
		return this.playerTeam.getTeam()[index];
	}

	public HashSet<Pokemon> getParticipants() {
		return this.participants;
	}

	public boolean canEscape() {
		return this.escape;
	}

	public int calculateXP(Pokemon player) {
		int base = enemy.getBaseExperience();
		double OTFactor = 1;
		double itemFactor = 1;
		double friendshipFactor = 1;
		double evolveFactor = player.getEvolves() != 0 ? 1.2 : 1;
		int enemyLevel = enemy.getStats().getLevel();
		int playerLevel = player.getStats().getLevel();
		int xp = (int) ((((base * enemyLevel) / 5.0)
				* (Math.pow(2 * enemyLevel + 10, 2.5) / Math.pow(enemyLevel + playerLevel + 10, 2.5)) + 1) * OTFactor
				* itemFactor * friendshipFactor * evolveFactor);
		player.increaseEV(enemy);
		return xp;
	}

	public NPC getEnemyCharacter() {
		return (NPC) this.enemyCharacter;
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

	public Team getPlayerTeam() {
		return playerTeam;
	}

	public void setVisible(boolean player, boolean enemy) {
		this.visible = new SimpleEntry<Boolean, Boolean>(player, enemy);
	}

	public void setVisible(Pokemon p, boolean v) {
		if (p.equals(player)) {
			this.visible = new SimpleEntry<Boolean, Boolean>(v, this.visible.getValue());
		} else {
			this.visible = new SimpleEntry<Boolean, Boolean>(this.visible.getKey(), v);
		}
	}

	public boolean isVisible(Pokemon pokemon) {
		if (pokemon.equals(player)) {
			return this.visible.getKey();
		} else {
			return this.visible.getValue();
		}
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

	public boolean canChooseAction() {
		if (chargeMoves.get(player) != null || needsRecharge(player)) {
			return false;
		}
		return true;
	}

}

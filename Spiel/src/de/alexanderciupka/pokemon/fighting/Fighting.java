package de.alexanderciupka.pokemon.fighting;

import java.util.ArrayList;
import java.util.Random;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.characters.Team;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.pokemon.Ailment;
import de.alexanderciupka.pokemon.pokemon.DamageClass;
import de.alexanderciupka.pokemon.pokemon.Move;
import de.alexanderciupka.pokemon.pokemon.Pokemon;
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
	private ArrayList<Pokemon> participants;
	private Random rng;
	private GameController gController;
	private boolean escape;

	public boolean won;

	public Fighting(Pokemon pokemonTwo) {
		gController = GameController.getInstance();
		playerTeam = new Team(gController.getMainCharacter().getTeam().getTeam(), gController.getMainCharacter());
		this.player = playerTeam.getFirstFightPokemon();
		enemyTeam = new Team(null);
		enemyTeam.addPokemon(pokemonTwo);
		this.enemy = pokemonTwo;
		player.startFight();
		enemy.startFight();
		rng = new Random();
		currentFightOption = FightOption.FIGHT;
		participants = new ArrayList<Pokemon>();
		escape = true;
		getStartPokemon();
	}

	public Fighting(Character enemyCharacter) {
		this.enemyCharacter = enemyCharacter;
		gController = GameController.getInstance();
		playerTeam = new Team(gController.getMainCharacter().getTeam().getTeam(), gController.getMainCharacter());
		this.player = playerTeam.getFirstFightPokemon();
		enemyTeam = new Team(enemyCharacter.getTeam().getTeam(), enemyCharacter);
		this.enemy = enemyTeam.getFirstFightPokemon();
		player.startFight();
		enemy.startFight();
		rng = new Random();
		currentFightOption = FightOption.FIGHT;
		participants = new ArrayList<Pokemon>();
		escape = false;
		getStartPokemon();
	}

	private void getStartPokemon() {
		player = playerTeam.getFirstFightPokemon();
		int index = playerTeam.getIndex(player);
		if (index > 0) {
			playerTeam.swapPokemon(0, index);
		}
		participants.add(player);
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

	public boolean playerAttack(Move move) {
		String message = player.canAttack();
		if (message != null) {
			gController.getGameFrame().getFightPanel().addText(message);
			gController.getGameFrame().getFightPanel().updatePanels();
			return true;
		}
		gController.getGameFrame().getFightPanel().addText(player.getName() + " setzt " + move.getName() + " ein!");
		if (!playerHit(move)) {
			gController.getGameFrame().getFightPanel().addText("Die Attacke ging daneben");
			return true;
		}
		gController.getGameFrame().getFightPanel().updatePanels();
		if (gController.checkEnemyDead()) {
			gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde besiegt!");
			if (!gController.winFight()) {
				gController.getGameFrame().getFightPanel().setEnemy();
			}
			return false;
		} else if (gController.checkPlayerDead()) {
			gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde besiegt!");
			gController.getGameFrame().getFightPanel().updatePanels();
			if (!gController.loseFight()) {
				gController.repaint();
				setPlayer();
			}
			return false;
		} else {
			if (move.checkStatChange()) {
				if (move.checkUserBuff()) {
					buff(player, move);
				}
				if (move.checkEnemyBuff()) {
					buff(enemy, move);
				}
			}
			return true;
		}
	}

	public boolean enemyAttack() {
		return enemyAttack(enemy.getMove(this.player));
	}

	public boolean enemyAttack(Move move) {
		String message = enemy.canAttack();
		if (message != null) {
			gController.getGameFrame().getFightPanel().addText(message);
			gController.getGameFrame().getFightPanel().updatePanels();
			return true;
		}
		gController.getGameFrame().getFightPanel().addText(enemy.getName() + " setzt " + move.getName() + " ein!");
		if (!enemyHit(move)) {
			gController.getGameFrame().getFightPanel().addText("Die Attacke ging daneben");
			return true;
		}
		gController.getGameFrame().getFightPanel().updatePanels();
		if (gController.checkPlayerDead()) {
			gController.getGameFrame().getFightPanel().addText(player.getName() + " wurde besiegt!");
			gController.getGameFrame().getFightPanel().updatePanels();
			if (!gController.loseFight()) {
				gController.repaint();
				setPlayer();
			}
			return false;
		} else if (gController.checkEnemyDead()) {
			gController.getGameFrame().getFightPanel().addText(enemy.getName() + " wurde besiegt!");
			gController.getGameFrame().getFightPanel()
					.addText(player.getName() + " erh�lt " + (int) ((enemy.getStats().getLevel() * 1.25 * 100) / 7));
			gController.getGameFrame().getFightPanel().updatePanels();
			if (!gController.winFight()) {
				enemy = gController.getFight().getEnemy();
				gController.getGameFrame().getFightPanel().setEnemy();
			}
			return false;
		} else {
			if (move.checkStatChange()) {
				if (move.checkUserBuff()) {
					buff(enemy, move);
				}
				if (move.checkEnemyBuff()) {
					buff(player, move);
				}
			}

			return true;
		}
	}

	public boolean playerHit(Move playerMove) {
		double hitChance = playerMove.getAccuracy() * (player.getStats().getFightStats().get(Stat.ACCURACY)
				/ enemy.getStats().getFightStats().get(Stat.EVASION));
		if (rng.nextFloat() * 100 < hitChance && playerMove.getCurrentPP() > 0) {
			playerMove.reducePP();
			damageCalculation(player, enemy, playerMove,
					rng.nextInt(playerMove.getMaxHits() - playerMove.getMinHits() + 1) + playerMove.getMinHits());
			gController.sleep(150);
			return true;
		}
		return false;
	}

	public boolean enemyHit(Move enemyMove) {
		double hitChance = enemyMove.getAccuracy() * (enemy.getStats().getFightStats().get(Stat.ACCURACY)
				/ player.getStats().getFightStats().get(Stat.EVASION));
		if (enemyMove.getAccuracy() > 100 || rng.nextFloat() * 100 < hitChance && enemyMove.getCurrentPP() > 0) {
			enemyMove.reducePP();
			damageCalculation(enemy, player, enemyMove,
					rng.nextInt(enemyMove.getMaxHits() - enemyMove.getMinHits() + 1) + enemyMove.getMinHits());
			return true;
		}
		return false;
	}

	public void buff(Pokemon pokemon, Move move) {
		for (Stat s : Stat.values()) {
			int change = move.changeStat(s);
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
		stats.loseHP((int) (((stats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
				* ((rng.nextFloat() * 0.15f + 0.85) / 1)));
	}

	private void damageCalculation(Pokemon attacker, Pokemon defense, Move usedMove, int ammount) {
		Stats attackerStats = attacker.getStats();
		Stats defenderStats = defense.getStats();
		double weakness = Type.getEffectiveness(usedMove.getMoveType(), defense.getTypes());
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
			System.out.println(ammount);
			for (int i = 0; i < ammount; i++) {
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
				System.out.println(damage);
				damage = Math.max(damage, 1);
				defenderStats.loseHP((int) damage);
				if (usedMove.getDrain() > 0) {
					attackerStats.restoreHP((int) (damage * (usedMove.getDrain() / 100)));
					gController.getGameFrame().getFightPanel().addText(defense.getName() + " wurde Energie abgesaugt!",
							true);
				} else if (usedMove.getDrain() < 0) {
					attackerStats.loseHP((int) (damage * (usedMove.getDrain() / 100)));
					gController.getGameFrame().getFightPanel()
							.addText(attacker.getName() + " hat sich durch den Rückstoß verletzt!", true);
				}
				damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
				gController.getGameFrame().getFightPanel().updatePanels();
				gController.sleep(150);
			}
			if (weakness == Type.STRONG) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war sehr effektiv!", true);
			} else if (weakness == Type.WEAK) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war nicht sehr effektiv!", true);
			} else if (weakness == Type.USELESS) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke zeigte keine Wirkung!", true);
			}
		}

		if (usedMove.getHealing() > 0) {
			attackerStats.restoreHP((int) (attackerStats.getStats().get(Stat.HP) * (usedMove.getHealing() / 100)));
			gController.getGameFrame().getFightPanel()
					.addText("Die KP von " + attacker.getName() + " wurden aufgefrischt!", true);
		}

		if (rng.nextFloat() * 100 < usedMove.getAilmentChance()) {
			if (usedMove.getAilment() != Ailment.NONE && defense.setAilment(usedMove.getAilment())) {
				gController.getGameFrame().getFightPanel()
						.addText(defense.getName() + " wurde " + Ailment.getText(usedMove.getAilment()) + "!", true);
			}
		}

	}

	public boolean newPlayerPokemon(int index) {
		if (playerTeam.getTeam()[index].getStats().getCurrentHP() > 0 && index != 0) {
			playerTeam.swapPokemon(0, index);
			setPlayer();
			return true;
		}
		return false;
	}

	public Pokemon getPlayer() {
		return player;
	}

	private void setPlayer() {
		this.player = playerTeam.getTeam()[0];
		this.player.startFight();
		if (!participants.contains(player)) {
			participants.add(player);
		}
		// gController.getGameFrame().getFightPanel().addText("Du schaffst das "
		// + this.player.getName() + "!");
		gController.updateFight();
	}

	public Pokemon getEnemy() {
		return enemy;
	}

	/**
	 * @return false if player has another Pokemon
	 */
	public boolean playerDead() {
		if (playerTeam.getFirstFightPokemon() == null) {
			if (enemyCharacter != null) {
				enemyCharacter.getTeam().restoreTeam();
			}
			return true;
		}
		participants.remove(player);
		gController.getFight().setCurrentFightOption(FightOption.POKEMON);
		gController.getGameFrame().getPokemonPanel().update();
		return false;
	}

	/**
	 * @return false if enemy has another Pokemon
	 */
	public boolean enemyDead() {
		gController.getGameFrame().getFightPanel().removeEnemy();
		enemy = enemyTeam.getFirstFightPokemon();
		if (enemy == null) {
			if (enemyCharacter != null) {
				gController.getGameFrame().getFightPanel().addText(enemyCharacter.getName() + " wurde besiegt!");
				enemyCharacter.defeated(true);
			}
			return true;
		}
		participants.clear();
		participants.add(player);
		enemy.startFight();
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

	public ArrayList<Pokemon> getParticipants() {
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

}

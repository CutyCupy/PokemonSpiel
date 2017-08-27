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

	public Pokemon[] getOrder() {
		Pokemon[] order = new Pokemon[2];
		if (player.getStats().getFightStats()[5] >= enemy.getStats().getFightStats()[5]) {
			order[0] = player;
			order[1] = enemy;
		} else {
			order[0] = enemy;
			order[1] = player;
		}
		return order;
	}

	public boolean isPlayerStart(Move playerMove, Move enemyMove) {
		if (playerMove.getPriority() > enemyMove.getPriority()) {
			return true;
		} else if (playerMove.getPriority() == enemyMove.getPriority()) {
			if (player.getStats().getFightStats()[5] > enemy.getStats().getFightStats()[5]) {
				return true;
			} else if (player.getStats().getFightStats()[5] == enemy.getStats().getFightStats()[5]) {
				return new Random().nextBoolean();
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
				if (move.checkEnemyBuff()){
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
		if (rng.nextFloat() * 100 < playerMove.getAccuracy() && playerMove.getCurrentPP() > 0) {
			playerMove.reducePP();
			int ammount = rng.nextInt(playerMove.getMaxHits() - playerMove.getMinHits() + 1);
			for (int i = 0; i < playerMove.getMinHits() + ammount; i++) {
				damageCalculation(player, enemy, playerMove);
				gController.sleep(150);
			}
			return true;
		}
		return false;
	}

	public boolean enemyHit(Move enemyMove) {
		if (rng.nextFloat() < enemyMove.getAccuracy() && enemyMove.getCurrentPP() > 0) {
			enemyMove.reducePP();
			int ammount = rng.nextInt(enemyMove.getMaxHits() - enemyMove.getMinHits() + 1);
			for (int i = 0; i < enemyMove.getMinHits() + ammount; i++) {
				damageCalculation(enemy, player, enemyMove);
				gController.sleep(150);
			}
			return true;
		}
		return false;
	}

	public void buff(Pokemon pokemon, Move move) {
		for (int i = 0; i < pokemon.getStats().getStats().length; i++) {
			int change = move.changeStat(i);
			if (change < 0) {
				if (pokemon.getStats().decreaseStat(i, Math.abs(change))) {
					if (change == -1) {
						gController.getGameFrame().getFightPanel()
								.addText(Stats.STAT_NAMES[i] + " von " + pokemon.getName() + " wurde gesenkt!");
					} else if (change <= -2) {
						gController.getGameFrame().getFightPanel().addText(
								Stats.STAT_NAMES[i] + " von " + pokemon.getName() + " wurde sehr stark gesenkt!");
					}
				}
			} else if (change > 0) {
				if (pokemon.getStats().increaseStat(i, change)) {
					if (change == 1) {
						gController.getGameFrame().getFightPanel()
								.addText(Stats.STAT_NAMES[i] + " von " + pokemon.getName() + " ist gestiegen!");
					} else if (change >= 2) {
						gController.getGameFrame().getFightPanel().addText(
								Stats.STAT_NAMES[i] + " von " + pokemon.getName() + " ist sehr stark gestiegen!");
					}
				}
			}
		}
	}

	public void selfAttack(Pokemon pokemon) {
		Stats stats = pokemon.getStats();
		double damage = 40;
		int def = stats.getFightStats()[2];
		int atk = stats.getFightStats()[1];
		stats.loseHP((int) (((stats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
				* ((rng.nextFloat() * 0.15f + 0.85) / 1)));
	}

	private void damageCalculation(Pokemon attacker, Pokemon defense, Move usedMove) {
		Stats attackerStats = attacker.getStats();
		Stats defenderStats = defense.getStats();
		double damage = usedMove.getPower() * Type.calcSTAB(attacker, usedMove);
		int crit = 1;
		double weakness = Type.getEffectiveness(usedMove.getMoveType(), defense.getTypes());
		if (damage > 0 || usedMove.getDamageClass() != DamageClass.NO_DAMAGE) {
			int def = 0;
			int atk = 0;
			switch (usedMove.getDamageClass()) {
			case PHYSICAL:
				def = defenderStats.getFightStats()[2];
				atk = attackerStats.getFightStats()[1];
				break;
			case SPECIAL:
				def = defenderStats.getFightStats()[4];
				atk = attackerStats.getFightStats()[3];
				break;
			default:
				break;
			}
			float p_crit = rng.nextFloat();
			switch(usedMove.getCrit() + 1) {
			case 1:
				crit = p_crit < 0.0625 ? 2 : 1;
				break;
			case 2:
				crit = p_crit < 0.125 ? 2 : 1;
				break;
			case 3:
				crit = p_crit < 0.5 ? 2 : 1;
				break;
			default:
				crit = 2;
				break;
			}
			if (crit == 2) {
				gController.getGameFrame().getFightPanel().addText("Ein Volltreffer!", true);
			}
			if (weakness == Type.STRONG) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war sehr effektiv!", true);
			} else if (weakness == Type.WEAK) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke war nicht sehr effektiv!", true);
			} else if (weakness == Type.USELESS) {
				gController.getGameFrame().getFightPanel().addText("Die Attacke zeigte keine Wirkung!", true);
			}
			damage = (weakness * ((attackerStats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
					* crit * ((rng.nextFloat() * 0.15f + 0.85) / 1));
			defenderStats.loseHP((int) damage);
			if(usedMove.getDrain() > 0) {
				attackerStats.restoreHP((int) (damage * (usedMove.getDrain() / 100)));
				gController.getGameFrame().getFightPanel().addText(defense.getName() + " wurde Energie abgesaugt!", true);
			} else if(usedMove.getDrain() < 0) {
				attackerStats.loseHP((int) (damage * (usedMove.getDrain() / 100)));
				gController.getGameFrame().getFightPanel().addText(attacker.getName() + " hat sich durch den Rückstoß verletzt!", true);
			}
		}
		
		if(usedMove.getHealing() > 0) {
			attackerStats.restoreHP((int) (attackerStats.getStats()[0] * (usedMove.getHealing() / 100)));
			gController.getGameFrame().getFightPanel().addText("Die KP von " + attacker.getName() + " wurden aufgefrischt!", true);
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
//		gController.getGameFrame().getFightPanel().addText("Du schaffst das " + this.player.getName() + "!");
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
		int enemyLevel = enemy.getStats().getLevel();
		int xp = (int) (((enemy.getBaseExperience() * enemyLevel) / 5.0) * (Math.pow(2 * enemyLevel + 10, 2.5) / Math.pow(enemyLevel + player.getStats().getLevel() + 10, 2.5)) + 1);
		if(!canEscape()) {
			xp *= 1.5;
		}
		player.increaseEV(enemy);
		return xp;
	}

	public NPC getEnemyCharacter() {
		return (NPC) this.enemyCharacter;
	}

}
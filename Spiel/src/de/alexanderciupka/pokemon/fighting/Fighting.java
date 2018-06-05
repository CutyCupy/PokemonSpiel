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
	private Field field;
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
		this.init();
		this.playerTeam = new Team(this.gController.getMainCharacter().getTeam().getTeam(),
				this.gController.getMainCharacter());
		this.player = this.playerTeam.getFirstFightPokemon();
		this.enemyTeam = new Team(null);
		this.enemyTeam.addPokemon(pokemonTwo);
		this.enemy = pokemonTwo;
		this.escape = true;
		this.getStartPokemon();
		this.player.startFight();
		this.enemy.startFight();
		this.field = new Field(this.gController.getMainCharacter().getCurrentRoute().getWeather());
	}

	public Fighting(Character enemyCharacter) {
		this.init();
		this.enemyCharacter = enemyCharacter;
		this.playerTeam = new Team(this.gController.getMainCharacter().getTeam().getTeam(),
				this.gController.getMainCharacter());
		this.player = this.playerTeam.getFirstFightPokemon();
		this.enemyTeam = new Team(enemyCharacter.getTeam().getTeam(), enemyCharacter);
		this.enemy = this.enemyTeam.getFirstFightPokemon();
		this.escape = false;
		this.getStartPokemon();
		this.player.startFight();
		this.enemy.startFight();
		this.field = new Field(this.gController.getMainCharacter().getCurrentRoute().getWeather());
	}

	private void init() {
		this.lastMoves = new HashMap<>();
		this.chargeMoves = new HashMap<>();
		this.needsRecharge = new HashMap<>();
		this.turn = 0;
		this.gController = GameController.getInstance();
		this.rng = new Random();
		this.currentFightOption = FightOption.FIGHT;
		this.participants = new HashSet<Pokemon>();
		this.visible = new SimpleEntry<Boolean, Boolean>(true, true);

	}

	private void getStartPokemon() {
		this.player = this.playerTeam.getFirstFightPokemon();
		this.sendOut(this.playerTeam.getIndex(this.player));
	}

	public boolean isPlayerStart(Move playerMove, Move enemyMove) {
		if (playerMove.getPriority() > enemyMove.getPriority()) {
			return true;
		} else if (playerMove.getPriority() == enemyMove.getPriority()) {
			if (this.player.getStats().getFightStats().get(Stat.SPEED) > this.enemy.getStats().getFightStats()
					.get(Stat.SPEED)) {
				return true;
			} else if (this.player.getStats().getFightStats().get(Stat.SPEED) == this.enemy.getStats().getFightStats()
					.get(Stat.SPEED)) {
				return this.rng.nextBoolean();
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void startRound(Move playerMove, Move enemyMove) {
		if (playerMove == null) {
			playerMove = this.gController.getInformation().getMoveByName("Verzweifler");
		}
		if (enemyMove == null) {
			enemyMove = this.gController.getInformation().getMoveByName("Verzweifler");
		}
		boolean playerStarts = this.gController.getFight().isPlayerStart(playerMove, enemyMove);
		if (playerStarts) {
			if (this.gController.getFight().attack(this.player, this.enemy, playerMove)) {
				this.gController.getFight().attack(this.enemy, this.player, enemyMove);
			}
		} else {
			if (this.gController.getFight().attack(this.enemy, this.player, enemyMove)) {
				this.gController.getFight().attack(this.player, this.enemy, playerMove);
			}
		}
		this.endTurn();
	}

	public void endTurn() {
		if (this.gController.isFighting()) {
			this.player.afterTurnDamage();
			this.enemy.afterTurnDamage();
			if (this.gController.checkDead(this.player)) {
				this.gController.getGameFrame().getFightPanel().addText(this.player.getName() + " wurde besiegt!");
				this.gController.getGameFrame().getFightPanel().updatePanels();
				this.gController.loseFight();
			}
			if (this.gController.checkDead(this.enemy)) {
				this.gController.getGameFrame().getFightPanel().addText(this.enemy.getName() + " wurde besiegt!");
				if (!this.gController.winFight()) {
					this.gController.getGameFrame().getFightPanel().setEnemy();
					this.gController.getGameFrame().getFightPanel().updatePanels();
				}
			}
			this.increaseTurn();
		}
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
		if (this.gController.checkDead(this.player)) {
			this.gController.getGameFrame().getFightPanel().addText(this.player.getName() + " wurde besiegt!");
			this.gController.getGameFrame().getFightPanel().updatePanels();
			this.gController.loseFight();
			dead = true;
		}
		if (this.gController.checkDead(this.enemy)) {
			this.gController.getGameFrame().getFightPanel().addText(this.enemy.getName() + " wurde besiegt!");
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
					if (attacker.equals(this.player)) {
						this.gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation("explosion");
					} else {
						this.gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation("explosion");
					}
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
				if (pokemon.equals(this.gController.getFight().getPlayer())) {
					this.gController.getGameFrame().getFightPanel().getPlayerAnimation()
							.playAnimation(change < 0 ? "debuff" : "buff");
				} else {
					this.gController.getGameFrame().getFightPanel().getEnemyAnimation()
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
		if (pokemon.equals(this.player)) {
			this.gController.getGameFrame().getFightPanel().getPlayerAnimation().playAnimation("punch");
		} else {
			this.gController.getGameFrame().getFightPanel().getEnemyAnimation().playAnimation("punch");
		}
		SoundController.getInstance().playSound(SoundController.NORMAL_EFFECTIVE);
		stats.loseHP((int) (((stats.getLevel() * (2 / 5.0) + 2) * damage * (atk / (50.0 * def)) + 2)
				* ((this.rng.nextFloat() * 0.15f + 0.85) / 1)));
	}

	private void playAnimation(Pokemon attacker, Pokemon defense, Move usedMove) {
		if (attacker.equals(this.gController.getFight().getPlayer())) {
			this.gController.getGameFrame().getFightPanel().getPlayerAnimation()
					.playAnimation(usedMove.getUserAnimation());
		} else {
			this.gController.getGameFrame().getFightPanel().getEnemyAnimation()
					.playAnimation(usedMove.getUserAnimation());
		}
		if (defense.equals(this.gController.getFight().getPlayer())) {
			this.gController.getGameFrame().getFightPanel().getPlayerAnimation()
					.playAnimation(usedMove.getTargetAnimation());
		} else {
			this.gController.getGameFrame().getFightPanel().getEnemyAnimation()
					.playAnimation(usedMove.getTargetAnimation());
		}
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

	public boolean canBeSendOut(int index) {
		if (this.playerTeam.getTeam()[index].getStats().getCurrentHP() > 0 && index != 0) {
			return true;
		}
		return false;
	}

	public void sendOut(int index) {
		this.playerTeam.swapPokemon(0, index);
		this.setPlayer();
		this.participants.add(this.player);
	}

	public Pokemon getPlayer() {
		return this.player;
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

	private void setPlayer() {
		this.player = this.playerTeam.getTeam()[0];
		this.player.startFight();
		this.visible = new SimpleEntry<Boolean, Boolean>(true, this.visible.getValue());
		this.gController.updateFight();
	}

	private void setEnemy() {
		this.enemy = this.enemyTeam.getFirstFightPokemon();
		this.enemy.startFight();
		if (this.participants == null) {
			this.participants = new HashSet<>();
		}
		this.participants.clear();
		this.participants.add(this.player);
		this.visible = new SimpleEntry<Boolean, Boolean>(this.visible.getKey(), true);
		this.gController.updateFight();
	}

	public Pokemon getEnemy() {
		return this.enemy;
	}

	public boolean playerDead() {
		this.participants.remove(this.player);
		if (this.playerTeam.getFirstFightPokemon() == null) {
			if (this.enemyCharacter != null) {
				this.enemyCharacter.getTeam().restoreTeam();
			}
			return true;
		}
		this.setCurrentFightOption(FightOption.POKEMON);
		this.gController.getGameFrame().getPokemonPanel().update();
		while (this.getCurrentFightOption().equals(FightOption.POKEMON)) {
			Thread.yield();
		}
		return false;
	}

	public boolean enemyDead() {
		this.gController.getGameFrame().getFightPanel().removeEnemy();
		if (this.enemyTeam.getFirstFightPokemon() == null) {
			if (this.enemyCharacter != null) {
				this.gController.getGameFrame().getFightPanel()
						.addText(this.enemyCharacter.getName() + " wurde besiegt!");
				this.enemyCharacter.defeated(true);
			}
			return true;
		}
		this.setEnemy();
		return false;
	}

	public FightOption getCurrentFightOption() {
		return this.currentFightOption;
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
		int base = this.enemy.getBaseExperience();
		double OTFactor = 1;
		double itemFactor = 1;
		double friendshipFactor = 1;
		double evolveFactor = player.getEvolves() != 0 ? 1.2 : 1;
		int enemyLevel = this.enemy.getStats().getLevel();
		int playerLevel = player.getStats().getLevel();
		int xp = (int) ((((base * enemyLevel) / 5.0)
				* (Math.pow(2 * enemyLevel + 10, 2.5) / Math.pow(enemyLevel + playerLevel + 10, 2.5)) + 1) * OTFactor
				* itemFactor * friendshipFactor * evolveFactor);
		player.increaseEV(this.enemy);
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
		return this.playerTeam;
	}

	public void setVisible(boolean player, boolean enemy) {
		this.visible = new SimpleEntry<Boolean, Boolean>(player, enemy);
	}

	public void setVisible(Pokemon p, boolean v) {
		if (p.equals(this.player)) {
			this.visible = new SimpleEntry<Boolean, Boolean>(v, this.visible.getValue());
		} else {
			this.visible = new SimpleEntry<Boolean, Boolean>(this.visible.getKey(), v);
		}
	}

	public boolean isVisible(Pokemon pokemon) {
		if (pokemon.equals(this.player)) {
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
		if (this.chargeMoves.get(this.player) != null || this.needsRecharge(this.player)) {
			return false;
		}
		return true;
	}

	public Field getField() {
		return this.field;
	}

}

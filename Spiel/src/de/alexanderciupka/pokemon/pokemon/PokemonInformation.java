package de.alexanderciupka.pokemon.pokemon;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.fighting.Target;
import de.alexanderciupka.pokemon.map.GameController;

public class PokemonInformation {

	private HashMap<Integer, String> names;
	private JsonArray allPokemonData;
	private ArrayList<Move> allMoves;
	private JsonArray allMoveData;
	private JsonArray allPokemonMoveData;
	private JsonArray abilityData;
	private JsonArray abilities;
	private JsonArray allItemData;

	private HashMap<String, JsonObject> allGrowthRates;
	private HashMap<Integer, HashMap<Integer, ArrayList<Move>>> allPokemonMoves;
	private HashMap<Integer, JsonArray> allEvolutions;
	private HashMap<Integer, Type[]> allPokemonTypes;	
	private HashMap<String, Image> frontSprites;
	private HashMap<String, Image> backSprites;
	private JsonParser parser;
	private BufferedReader pokemonData;
	private BufferedReader moveData;
	private BufferedReader pokemonMoveData;
	private BufferedReader growthRates;

	public PokemonInformation() {
		this.pokemonData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/pokemon.json")));
		this.moveData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/moves.json")));
		this.pokemonMoveData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/pokemonMoves.json")));
		this.growthRates = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/growth_rates.json")));
		this.frontSprites = new HashMap<>();
		this.backSprites = new HashMap<>();
		this.names = new HashMap<Integer, String>();
		this.allMoves = new ArrayList<Move>();
		this.allPokemonMoves = new HashMap<Integer, HashMap<Integer, ArrayList<Move>>>();
		this.allEvolutions = new HashMap<Integer, JsonArray>();
		this.allPokemonTypes = new HashMap<Integer, Type[]>();
		this.allGrowthRates = new HashMap<String, JsonObject>();
		this.parser = new JsonParser();
		this.readData();
	}

	private void readData() {
		try {
			this.allPokemonData = this.parser.parse(this.pokemonData.readLine()).getAsJsonArray();
			String data = "";
			String readLine = "";
			while ((readLine = this.moveData.readLine()) != null) {
				data += readLine;
			}
			this.allMoveData = this.parser.parse(data).getAsJsonArray();
			this.allPokemonMoveData = this.parser.parse(this.pokemonMoveData.readLine()).getAsJsonArray();
			JsonArray allGrowthRates = this.parser.parse(this.growthRates.readLine()).getAsJsonArray();
			for (JsonElement element : allGrowthRates) {
				this.allGrowthRates.put(element.getAsJsonObject().get("name").getAsString(), element.getAsJsonObject());
			}
			ArrayList<Move> foo = new ArrayList<Move>();
			HashSet<String> uniqueMoves = new HashSet<>();
			for (JsonElement element : this.allMoveData) {
				JsonObject currentJson = element.getAsJsonObject();
				Move currentMove = new Move(currentJson.get("id").getAsInt());
				currentMove.setMoveType(Type.get(currentJson.get("type").getAsString()));
				currentMove.setName(currentJson.get("name").getAsString());
				try {
					currentMove.setPp(currentJson.get("pp").getAsInt());
				} catch (Exception e) {
					currentMove.setPp(50);
				}
				try {
					currentMove.setAccuracy(currentJson.get("accuracy").getAsFloat());
				} catch (Exception e) {
					currentMove.setAccuracy(200.0f);
				}
				try {
					currentMove.setMinHits(currentJson.get("min_hits").getAsInt());
					currentMove.setMaxHits(currentJson.get("max_hits").getAsInt());
				} catch (Exception e) {
					currentMove.setMinHits(1);
					currentMove.setMaxHits(1);
				}
				currentMove.setCategory(currentJson.get("category").getAsString());
				currentMove.setTarget(currentJson.get("target") instanceof JsonNull ? Target.SELECTED_POKEMON
						: Target.valueOf(currentJson.get("target").getAsString()));
				currentMove.setCrit(currentJson.get("crit_rate").getAsInt());
				try {
					currentMove.setTargetAnimation(currentJson.get("target_animation").getAsString());
				} catch (Exception e) {
					currentMove.setTargetAnimation("none");
				}

				try {
					currentMove.setUserAnimation(currentJson.get("user_animation").getAsString());
				} catch (Exception e) {
					currentMove.setUserAnimation("none");
				}

				try {
					currentMove.setAilment(Ailment.valueOf(currentJson.get("ailment").getAsString().toUpperCase()));
				} catch (Exception e) {
					currentMove.setAilment(
							SecondaryAilment.valueOf(currentJson.get("ailment").getAsString().toUpperCase()));
				}

				try {
					currentMove.setPriority(currentJson.get("priority").getAsInt());
				} catch (Exception e) {
					currentMove.setPriority(0);
				}
				currentMove.setDescription(currentJson.get("desc").getAsString());
				switch (currentJson.get("damage_class").getAsString()) {
				case "physical":
					currentMove.setDamageClass(DamageClass.PHYSICAL);
					break;
				case "special":
					currentMove.setDamageClass(DamageClass.SPECIAL);
					break;
				default:
					currentMove.setDamageClass(DamageClass.NO_DAMAGE);
					break;
				}
				try {
					currentMove.setPower(currentJson.get("power").getAsInt());
				} catch (Exception e) {
					currentMove.setPower(0);
				}
				currentMove.setAilmentChance(currentJson.get("ailment_chance").getAsInt());
				if (currentJson.get("category").getAsString().equals("ailment")) {
					currentMove.setAilmentChance(100);
				}
				if (currentJson.get("category").getAsString().contains("unique")) {
					uniqueMoves.add(
							currentJson.get("name").getAsString() + " - " + currentJson.get("ailment").getAsString());
				}
				if (!currentJson.get("category").getAsString().contains("net-good-stats")) {
					currentMove.setStatChance(currentJson.get("stat_chance").getAsFloat());
				} else {
					currentMove.setStatChance(1.0f);
				}
				currentMove.setHealing(currentJson.get("healing").getAsFloat());
				currentMove.setDrain(currentJson.get("drain").getAsFloat());
				for (JsonElement currentStatChange : currentJson.get("stat_changes").getAsJsonArray()) {
					int change = currentStatChange.getAsJsonObject().get("change").getAsInt();
					try {
						currentMove.addStatChange(Stat.valueOf(currentStatChange.getAsJsonObject().get("stat")
								.getAsJsonObject().get("name").getAsString().toUpperCase()), change);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
				this.allMoves.add(currentMove);
			}
			for (JsonElement element : this.allPokemonData) {
				int key = element.getAsJsonObject().get("id").getAsInt();
				this.names.put(key, element.getAsJsonObject().get("name").getAsString());
				HashMap<Integer, ArrayList<Move>> moves = new HashMap<Integer, ArrayList<Move>>();
				JsonArray moveArray = null;
				try {
					moveArray = this.getMoves(key);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				for (JsonElement moveElement : moveArray) {
					if (moveElement.getAsJsonObject().get("id").getAsInt() - 1 < this.allMoves.size()) {
						int level = moveElement.getAsJsonObject().get("level_learned_at").getAsInt();
						ArrayList<Move> currentMoves = moves.get(level);
						if (currentMoves == null) {
							currentMoves = new ArrayList<Move>();
						}
						currentMoves.add(this.getMoveById(moveElement.getAsJsonObject().get("id").getAsInt()));
						if (foo.contains(this.getMoveById(moveElement.getAsJsonObject().get("id").getAsInt()))) {
							foo.remove(this.getMoveById(moveElement.getAsJsonObject().get("id").getAsInt()));
						}
						moves.put(level, currentMoves);
					}
				}
				this.allPokemonMoves.put(key, moves);
				this.allEvolutions.put(key, element.getAsJsonObject().get("evolution").getAsJsonArray());
			}

			for (JsonElement element : this.allPokemonData) {
				int id = element.getAsJsonObject().get("id").getAsInt();
				Type[] currentTypes = { Type.get(element.getAsJsonObject().get("firstType").getAsString()),
						Type.get(element.getAsJsonObject().get("secondType").getAsString()) };
				this.allPokemonTypes.put(id, currentTypes);
			}

			this.abilities = this.parser
					.parse(new BufferedReader(new InputStreamReader(
							this.getClass().getResourceAsStream("/pokemon/abilities.json"), "UTF8")).readLine())
					.getAsJsonArray();

			this.abilityData = this.parser
					.parse(new BufferedReader(new InputStreamReader(
							this.getClass().getResourceAsStream("/pokemon/pokemonAbilities.json"), "UTF8")).readLine())
					.getAsJsonArray();

			this.allItemData = this.parser
					.parse(new BufferedReader(
							new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/items.json"), "UTF8")))
					.getAsJsonArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JsonArray getMoves(int key) {
		for (JsonElement element : this.allPokemonMoveData) {
			if (element.getAsJsonObject().get("id").getAsInt() == key) {
				return element.getAsJsonObject().get("moves").getAsJsonArray();
			}
		}
		return null;
	}

	public String getName(int id) {
		return this.names.get(id);
	}

	public Move getMoveById(int id) {
		for (Move move : this.allMoves) {
			if (move.getId() == id) {
				return move.clone();
			}
		}
		return this.allMoves.get(id - 1).clone();
	}

	public String getMoveNameById(int id) {
		for (Move move : this.allMoves) {
			if (move.getId() == id) {
				return move.getName();
			}
		}
		return this.allMoves.get(id - 1).getName();
	}

	public Move getMoveByName(String name) {
		for (Move move : this.allMoves) {
			if (move.getName().equals(name)) {
				return move.clone();
			}
		}
		return null;
	}

	public ArrayList<Move> getNewMove(Pokemon p, int level) {
		ArrayList<Move> newMoves = new ArrayList<Move>();
		for (int i : this.allPokemonMoves.get(p.getId()).keySet()) {
			if (i == level) {
				for (int ammount = 0; ammount < this.allPokemonMoves.get(p.getId()).get(i).size(); ammount++) {
					Move newMove = this.allPokemonMoves.get(p.getId()).get(i).get(ammount).clone();
					if (!p.knowsMove(newMove)) {
						newMoves.add(newMove);
					}
				}
			}
		}
		return newMoves;
	}

	/**
	 * Checks the evolution of a Pokemon - if used is Item.NONE -> LEVELUP
	 *
	 * @param p
	 * @param used
	 * @return
	 */
	public int checkEvolution(Pokemon p, Integer used) {
		EvolveType type = used == Items.KEINS ? EvolveType.LEVELUP : EvolveType.USEITEM;
		for (JsonElement j : this.allEvolutions.get(p.getId())) {
			JsonArray jArray = j.getAsJsonObject().get("details").getAsJsonArray();
			for (JsonElement k : jArray) {
				JsonObject curJson = k.getAsJsonObject();
				if (type == EvolveType.valueOf(curJson.get("trigger").getAsString().toUpperCase())) {
					boolean evolve = true;
					if (!(curJson.get("min_level") instanceof JsonNull)) {
						evolve &= curJson.get("min_level").getAsInt() <= p.getStats().getLevel();
					}
					int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
					switch (curJson.get("time_of_day").getAsString()) {
					case "day":
						evolve &= hour >= 8 && hour < 20;
						break;
					case "night":
						evolve &= hour < 8 || hour >= 20;
						break;
					default:
						break;
					}
					if (!(curJson.get("relative_physical_stats") instanceof JsonNull)) {
						short attack = p.getStats().getStats().get(Stat.ATTACK);
						short defense = p.getStats().getStats().get(Stat.DEFENSE);
						switch (curJson.get("relative_physical_stats").getAsString()) {
						case "-1":
							evolve &= attack < defense;
							break;
						case "0":
							evolve &= attack == defense;
							break;
						case "1":
							evolve &= attack > defense;
							break;
						}
					}
					if (!(curJson.get("item") instanceof JsonNull)) {
						evolve &= used == curJson.get("item").getAsInt();
					}
					if(!(curJson.get("held_item") instanceof JsonNull)) {
						//TODO: Held Item
					}
					if (!(curJson.get("gender") instanceof JsonNull)) {
						switch (curJson.get("gender").getAsInt()) {
						case 1:
							evolve &= p.getGender() == Gender.FEMALE;
							break;
						case 2:
							evolve &= p.getGender() == Gender.MALE;
							break;
						case 3:
							evolve &= p.getGender() == Gender.GENDERLESS;
							break;
						}
					}
					if (!(curJson.get("known_move_type") instanceof JsonNull)) {
						Type t = Type.valueOf(curJson.get("known_move_type").getAsString().toUpperCase());
						boolean has = false;
						for (Move m : p.getMoves()) {
							if (m != null && m.getMoveType(p) == t) {
								has = true;
								break;
							}
						}
						evolve &= has;
					}
					if (!(curJson.get("party_type") instanceof JsonNull)) {
						Type t = Type.valueOf(curJson.get("known_move_type").getAsString().toUpperCase());
						boolean has = false;
						for (Pokemon poke : GameController.getInstance().getMainCharacter().getTeam().getTeam()) {
							if (poke != null && !p.equals(poke)) {
								for (Type ty : poke.getTypes()) {
									if (t.equals(ty)) {
										has = true;
										break;
									}
								}
							}
							if (has) {
								break;
							}
						}
						evolve &= has;
					}
					if (!(curJson.get("known_move") instanceof JsonNull)) {
						String move = curJson.get("known_move").getAsString().toLowerCase();
						boolean has = false;
						for (Move m : p.getMoves()) {
							if (m != null && m.getName().toLowerCase().equals(move)) {
								has = true;
								break;
							}
						}
						evolve &= has;
					}
					if (!(curJson.get("location") instanceof JsonNull)) {
					}
					if (evolve) {
						return j.getAsJsonObject().get("id").getAsInt();
					}
				}

			}
		}
		return 0;
	}
	
	public boolean canEvolveWith(int pokemonId, int itemId) {
		for (JsonElement j : this.allEvolutions.get(pokemonId)) {
			JsonArray jArray = j.getAsJsonObject().get("details").getAsJsonArray();
			for (JsonElement k : jArray) {
				JsonObject curJson = k.getAsJsonObject();
				if (EvolveType.USEITEM == EvolveType.valueOf(curJson.get("trigger").getAsString().toUpperCase())) {
					if (!(curJson.get("item") instanceof JsonNull)) {
						return curJson.get("item").getAsInt() == itemId;
					}
				}

			}
		}
		return false;
	}

	public JsonElement getItemData(String data, int id) {
		for (int i = 0; i < allItemData.size(); i++) {
			JsonObject item = allItemData.get(i).getAsJsonObject();
			if (item.get("id").getAsInt() == id) {
				if(item.has(data)) {
					return item.get(data);
				}
			}
		}
		return null;
	}
	
	public boolean hasAttribute(String attribute, int itemId) {
		for(int i = 0; i < allItemData.size(); i++) {
			JsonObject item = allItemData.get(i).getAsJsonObject();
			if (item.get("id").getAsInt() == itemId) {
				for(JsonElement e : item.get("attributes").getAsJsonArray()) {
					if(e.getAsJsonObject().get("name").getAsString().equals(attribute)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Type[] getTypes(int id) {
		try {
			return this.allPokemonTypes.get(id).clone();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getID(String name) {
		for (Integer id : this.names.keySet()) {
			if (this.names.get(id).equals(name)) {
				return id;
			}
		}
		return 0;
	}

	public HashMap<Stat, Short> getBaseStats(int id) {
		JsonObject pokemon = null;
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				pokemon = je.getAsJsonObject();
				break;
			}
		}
		if (pokemon != null) {
			HashMap<Stat, Short> stats = new HashMap<Stat, Short>();
			for (JsonElement cs : pokemon.get("stats").getAsJsonArray()) {
				JsonObject currentStat = cs.getAsJsonObject();
				stats.put(Stat.valueOf(currentStat.get("name").getAsString().toUpperCase()),
						currentStat.get("base_stat").getAsShort());
			}
			return stats;
		}
		return null;
	}

	public int getWeight(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("weight").getAsInt();
			}
		}
		return 0;
	}

	public int getHeight(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("height").getAsInt();
			}
		}
		return 0;
	}

	public int getBaseExperience(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("base_experience").getAsInt();
			}
		}
		return 0;
	}

	public HashMap<Stat, Short> getEvBonus(int id) {
		JsonObject pokemon = null;
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				pokemon = je.getAsJsonObject();
				break;
			}
		}
		if (pokemon != null) {
			HashMap<Stat, Short> stats = new HashMap<Stat, Short>();
			for (JsonElement cs : pokemon.get("stats").getAsJsonArray()) {
				JsonObject currentStat = cs.getAsJsonObject();
				stats.put(Stat.valueOf(currentStat.get("name").getAsString().toUpperCase()),
						currentStat.get("effort").getAsShort());
			}
			return stats;
		}
		return null;
	}

	public String getGrowthRate(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("growth_rate").getAsString();
			}
		}
		return "slow";
	}

	public int getCaptureRate(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("capture_rate").getAsInt();
			}
		}
		return 255;
	}

	public int getLevelUpXP(Pokemon p, int nextLevel) {
		int result = 0;
		for (JsonElement j : this.allGrowthRates.get(p.getGrowthRate()).get("levels").getAsJsonArray()) {
			if (j.getAsJsonObject().get("level").getAsInt() == nextLevel - 1) {
				result -= j.getAsJsonObject().get("experience").getAsInt();
			} else if (j.getAsJsonObject().get("level").getAsInt() == nextLevel) {
				result += j.getAsJsonObject().get("experience").getAsInt();
			}
		}
		return result;
	}

	public int getBaseHappiness(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("base_happiness").getAsInt();
			}
		}
		return 255;
	}

	public boolean isBaby(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("is_baby").getAsBoolean();
			}
		}
		return false;
	}

	public Gender getGender(int id) {
		for (JsonElement je : this.allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				double femaleChance = je.getAsJsonObject().get("female_rate").getAsDouble();
				if (femaleChance < 0) {
					return Gender.GENDERLESS;
				} else if (femaleChance == 0) {
					return Gender.MALE;
				} else if (femaleChance == 1) {
					return Gender.FEMALE;
				} else {
					Random rng = new Random();
					return rng.nextFloat() < femaleChance ? Gender.FEMALE : Gender.MALE;
				}
			}
		}
		return Gender.GENDERLESS;
	}

	public Image getFrontSprite(int id, Gender g, boolean shiny) {
		String s = id + g.getText() + (shiny ? "s" : "");
		Image img = this.frontSprites.get(s);
		if (img == null) {
			try {
				img = ImageIO.read(new File(this.getClass().getResource("/pokemon/front/" + s + ".png").getFile()));
				this.frontSprites.put(s, img);
			} catch (Exception e) {
				try {
					img = this.frontSprites.get(id + (shiny ? "s" : ""));
					if (img == null) {
						img = ImageIO.read(new File(this.getClass()
								.getResource("/pokemon/front/" + id + (shiny ? "s" : "") + ".png").getFile()));
						this.frontSprites.put(id + (shiny ? "s" : ""), img);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return img;
	}

	public Image getBackSprite(int id, Gender g, boolean shiny) {
		String s = id + g.getText() + (shiny ? "s" : "");
		Image img = this.backSprites.get(s);
		if (img == null) {
			try {
				img = ImageIO.read(new File(this.getClass().getResource("/pokemon/back/" + s + ".png").getFile()));
				this.backSprites.put(s, img);
			} catch (Exception e) {
				try {
					img = this.backSprites.get(id + (shiny ? "s" : ""));
					if (img == null) {
						img = ImageIO.read(new File(this.getClass()
								.getResource("/pokemon/back/" + id + (shiny ? "s" : "") + ".png").getFile()));
						this.backSprites.put(id + (shiny ? "s" : ""), img);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return img;
	}

	public Image getGenderImage(Gender g) {
		try {
			return ImageIO
					.read(new File(Main.class.getResource("/icons/" + g.name().toLowerCase() + ".png").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private JsonObject getAbilityData(int id) {
		for (JsonElement je : this.abilityData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject();
			}
		}
		return null;
	}

	public Ability getAbility(int id, boolean hidden) {
		JsonObject data = this.getAbilityData(id);
		ArrayList<Integer> possibilities = new ArrayList<>();
		for (JsonElement j : data.get("abilities").getAsJsonArray()) {
			JsonObject ability = j.getAsJsonObject();
			if (!(hidden ^ ability.get("is_hidden").getAsBoolean())) {
				possibilities.add(ability.get("id").getAsInt());
			}
		}

		ArrayList<Ability> abilities = new ArrayList<>();
		for (Integer currentID : possibilities) {
			for (JsonElement j : this.abilities) {
				JsonObject ability = j.getAsJsonObject();
				if (ability.get("id").getAsInt() == currentID) {
					abilities.add(new Ability(ability.get("id").getAsInt(), ability.get("name").getAsString(),
							ability.get("description").getAsString()));
				}
			}
		}

		return abilities.get(new Random().nextInt(abilities.size()));
	}
}

package de.alexanderciupka.pokemon.pokemon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PokemonInformation {

	private JsonArray allPokemon;
	private HashMap<Integer, String> names;
	private JsonArray allPokemonData;
	private ArrayList<Move> allMoves;
	private JsonArray allMoveData;
	private JsonArray allPokemonMoveData;
	private HashMap<String, JsonObject> allGrowthRates;
	private HashMap<Integer, HashMap<Integer, ArrayList<Move>>> allPokemonMoves;
	private HashMap<Integer, HashMap<Integer, Integer>> allEvolutions;
	private HashMap<Integer, Type[]> allPokemonTypes;
	private JsonParser parser;
	private BufferedReader pokemonNames;
	private BufferedReader pokemonData;
	private BufferedReader moveData;
	private BufferedReader pokemonMoveData;
	private BufferedReader growthRates;

	public PokemonInformation() {
		pokemonNames = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/names.json")));
		pokemonData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/pokemon.json")));
		moveData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/moves.json")));
		pokemonMoveData = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/pokemonMoves.json")));
		growthRates = new BufferedReader(
				new InputStreamReader(this.getClass().getResourceAsStream("/pokemon/growth_rates.json")));
		names = new HashMap<Integer, String>();
		allMoves = new ArrayList<Move>();
		allPokemonMoves = new HashMap<Integer, HashMap<Integer, ArrayList<Move>>>();
		allEvolutions = new HashMap<Integer, HashMap<Integer, Integer>>();
		allPokemonTypes = new HashMap<Integer, Type[]>();
		allGrowthRates = new HashMap<String, JsonObject>();
		parser = new JsonParser();
		readData();
	}

	private void readData() {
		try {
			allPokemon = parser.parse(pokemonNames.readLine()).getAsJsonArray();
			allPokemonData = parser.parse(pokemonData.readLine()).getAsJsonArray();
			allMoveData = parser.parse(moveData.readLine()).getAsJsonArray();
			allPokemonMoveData = parser.parse(pokemonMoveData.readLine()).getAsJsonArray();
			JsonArray allGrowthRates = parser.parse(growthRates.readLine()).getAsJsonArray();
			for(JsonElement element : allGrowthRates) {
				this.allGrowthRates.put(element.getAsJsonObject().get("name").getAsString(), element.getAsJsonObject());
			}
			for (JsonElement element : allPokemon) {
				names.put(element.getAsJsonObject().get("id").getAsInt(),
						element.getAsJsonObject().get("name").getAsString());
			}
			for (JsonElement element : allMoveData) {
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
				switch (currentJson.get("ailment").getAsString()) {
				case "poison":
					currentMove.setAilment(Ailment.POISON);
					break;
				case "burn":
					currentMove.setAilment(Ailment.BURN);
					break;
				case "sleep":
					currentMove.setAilment(Ailment.SLEEP);
					break;
				case "paralysis":
					currentMove.setAilment(Ailment.PARALYSIS);
					break;
				case "freeze":
					currentMove.setAilment(Ailment.FREEZE);
					break;
				case "confusion":
					currentMove.setAilment(Ailment.CONFUSION);
					break;
				default:
					currentMove.setAilment(Ailment.NONE);
					break;
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
					if (currentJson.get("category").getAsString().equals("ohko")) {
						currentMove.setPower(100);
					} else {
						currentMove.setPower(currentJson.get("power").getAsInt());
					}
				} catch (Exception e) {
					currentMove.setPower(0);
				}
				currentMove.setAilmentChance(currentJson.get("ailment_chance").getAsFloat());
				if (currentJson.get("category").getAsString().equals("ailment")) {
					currentMove.setAilmentChance(100);
				}
				if (!currentJson.get("category").getAsString().equals("net-good-stats")) {
					currentMove.setStatChance(currentJson.get("stat_chance").getAsFloat());
				} else {
					currentMove.setStatChance(1.0f);
				}
				currentMove.setHealing(currentJson.get("healing").getAsFloat());
				currentMove.setDrain(currentJson.get("drain").getAsFloat());
				for (JsonElement currentStatChange : currentJson.get("stat_changes").getAsJsonArray()) {
					int change = currentStatChange.getAsJsonObject().get("change").getAsInt();
					String[] url = currentStatChange.getAsJsonObject().get("stat").getAsJsonObject().get("url")
							.getAsString().split("/");
					int stat = Integer.parseInt(url[url.length - 1]);
					currentMove.addStatChange(stat - 2, change);
				}
				allMoves.add(currentMove);
			}
			for (JsonElement element : allPokemonData) {
				int key = element.getAsJsonObject().get("id").getAsInt();
				HashMap<Integer, ArrayList<Move>> moves = new HashMap<Integer, ArrayList<Move>>();
				HashMap<Integer, Integer> evolutions = new HashMap<Integer, Integer>(1);
				JsonArray moveArray = null;
				try {
					moveArray = getMoves(key);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				for (JsonElement moveElement : moveArray) {
					if (moveElement.getAsJsonObject().get("id").getAsInt() - 1 < allMoves.size()) {
						try {
							moves.get(moveElement.getAsJsonObject().get("level_learned_at").getAsInt())
									.add(allMoves.get(moveElement.getAsJsonObject().get("id").getAsInt() - 1).clone());
						} catch (Exception e) {
							moves.put(moveElement.getAsJsonObject().get("level_learned_at").getAsInt(),
									new ArrayList<Move>());
							moves.get(moveElement.getAsJsonObject().get("level_learned_at").getAsInt())
									.add(allMoves.get(moveElement.getAsJsonObject().get("id").getAsInt() - 1).clone());
						}
					}
				}
				JsonObject evolutionObject = element.getAsJsonObject().get("evolution").getAsJsonObject();
				try {
					evolutions.put(evolutionObject.get("min_level").getAsInt(), evolutionObject.get("id").getAsInt());
				} catch (Exception e) {
				}
				allPokemonMoves.put(key, moves);
				allEvolutions.put(key, evolutions);
			}
			for (JsonElement element : allPokemonData) {
				int id = element.getAsJsonObject().get("id").getAsInt();
				Type[] currentTypes = { Type.get(element.getAsJsonObject().get("firstType").getAsString()),
						Type.get(element.getAsJsonObject().get("secondType").getAsString()) };
				allPokemonTypes.put(id, currentTypes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JsonArray getMoves(int key) {
		for (JsonElement element : allPokemonMoveData) {
			if (element.getAsJsonObject().get("id").getAsInt() == key) {
				return element.getAsJsonObject().get("moves").getAsJsonArray();
			}
		}
		return null;
	}

	public String getName(int id) {
		return names.get(id);
	}

	public Move getMoveById(int id) {
		for (Move move : allMoves) {
			if (move.getId() == id) {
				return move.clone();
			}
		}
		return this.allMoves.get(id - 1).clone();
	}

	public Move getMoveByName(String name) {
		for (Move move : allMoves) {
			if (move.getName().equals(name)) {
				return move.clone();
			}
		}
		return null;
	}

	public ArrayList<Move> getNewMove(int id, int level) {
		ArrayList<Move> newMoves = new ArrayList<Move>();
		for (int i : allPokemonMoves.get(id).keySet()) {
			if (i == level) {
				for (int ammount = 0; ammount < allPokemonMoves.get(id).get(i).size(); ammount++) {
					newMoves.add(allPokemonMoves.get(id).get(i).get(ammount));
				}
			}
		}
		return newMoves;
	}

	public int checkEvolution(int id, int level) {
		try {
			return allEvolutions.get(id).get(level);
		} catch (Exception e) {
			return 0;
		}
	}

	public Type[] getTypes(int id) {
		try {
			return allPokemonTypes.get(id).clone();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getID(String name) {
		for (JsonElement e : allPokemon) {
			if (e.getAsJsonObject().get("name").getAsString().toLowerCase().equals(name)) {
				return e.getAsJsonObject().get("id").getAsInt();
			}
		}
		return 0;
	}

	public short[] getBaseStats(int id) {
		JsonObject pokemon = null;
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				pokemon = je.getAsJsonObject();
				break;
			}
		}
		if (pokemon != null) {
			short[] stats = new short[6];
			for (JsonElement cs : pokemon.get("stats").getAsJsonArray()) {
				JsonObject currentStat = cs.getAsJsonObject();
				for (int i = 0; i < Stats.STAT_SAVE_NAMES.length; i++) {
					if (currentStat.get("name").getAsString().equals(Stats.STAT_SAVE_NAMES[i])) {
						stats[i] = currentStat.get("base_stat").getAsShort();
						break;
					}
				}
			}
			return stats;
		}
		return new short[] { 0, 0, 0, 0, 0, 0 };
	}

	public int getWeight(int id) {
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("weight").getAsInt();
			}
		}
		return 0;
	}

	public int getHeight(int id) {
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("height").getAsInt();
			}
		}
		return 0;
	}

	public int getBaseExperience(int id) {
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("base_experience").getAsInt();
			}
		}
		return 0;
	}

	public HashMap<String, Short> getEvBonus(int id) {
		JsonObject pokemon = null;
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				pokemon = je.getAsJsonObject();
				break;
			}
		}
		if (pokemon != null) {
			HashMap<String, Short> stats = new HashMap<String, Short>();
			for (JsonElement cs : pokemon.get("stats").getAsJsonArray()) {
				JsonObject currentStat = cs.getAsJsonObject();
				stats.put(currentStat.get("name").getAsString(), currentStat.get("effort").getAsShort());
			}
			return stats;
		}
		return null;
	}

	public String getGrowthRate(int id) {
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("growth_rate").getAsString();
			}
		}
		return "slow";
	}

	public int getCaptureRate(int id) {
		for (JsonElement je : allPokemonData) {
			if (je.getAsJsonObject().get("id").getAsInt() == id) {
				return je.getAsJsonObject().get("capture_rate").getAsInt();
			}
		}
		return 255;
	}

	public int getLevelUpXP(Pokemon p, int nextLevel) {
		for(JsonElement j : this.allGrowthRates.get(p.getGrowthRate()).get("levels").getAsJsonArray()) {
			if(j.getAsJsonObject().get("level").getAsInt() == nextLevel) {
				return j.getAsJsonObject().get("experience").getAsInt();
			}
		}
		return 0;
	}
}

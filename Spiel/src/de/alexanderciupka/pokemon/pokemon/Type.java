package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;
import java.util.EnumMap;

public enum Type {

	WATER("Wasser"), GRASS("Pflanze"), FIRE("Feuer"), ROCK("Gestein"), GROUND("Boden"), STEEL("Stahl"), GHOST(
			"Geist"), DARK("Unlicht"), NORMAL("Normal"), FAIRY("Fee"), PSYCHO("Psycho"), FLYING("Flug"), BUG(
					"Käfer"), POISON("Gift"), ELECTRIC("Elektro"), ICE("Eis"), FIGHTING("Kampf"), DRAGON("Drache");

	private String name;

	public static final double STRONG = 2.0;
	public static final double WEAK = 0.5;
	public static final double USELESS = 0.0;
	public static final double DEFAULT = 1.0;
	public static final double STAB = 1.5;

	private static EnumMap<Type, Color> colorMapping = new EnumMap<Type, Color>(Type.class) {
		private static final long serialVersionUID = 1L;
		{
			this.put(Type.WATER, new Color(0, 0, 255));
			this.put(Type.GRASS, new Color(0, 204, 0));
			this.put(Type.FIRE, new Color(204, 0, 0));
			this.put(Type.ROCK, new Color(102, 51, 0));
			this.put(Type.GROUND, new Color(204, 102, 0));
			this.put(Type.STEEL, new Color(192, 192, 192));
			this.put(Type.GHOST, new Color(51, 0, 102));
			this.put(Type.DARK, new Color(0, 0, 0));
			this.put(Type.NORMAL, Color.LIGHT_GRAY);
			this.put(Type.FAIRY, new Color(255, 153, 204));
			this.put(Type.PSYCHO, new Color(255, 102, 255));
			this.put(Type.FLYING, new Color(204, 229, 255));
			this.put(Type.BUG, new Color(153, 153, 0));
			this.put(Type.POISON, new Color(153, 0, 153));
			this.put(Type.ELECTRIC, new Color(255, 255, 0));
			this.put(Type.ICE, new Color(153, 255, 255));
			this.put(Type.FIGHTING, new Color(255, 128, 0));
			this.put(Type.DRAGON, new Color(0, 0, 102));

		}
	};

	private static EnumMap<Type, Type[]> strongMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;
		{
			this.put(Type.WATER, new Type[] { GROUND, ROCK, FIRE });
			this.put(Type.GRASS, new Type[] { GROUND, ROCK, WATER });
			this.put(Type.FIRE, new Type[] { BUG, STEEL, GRASS, ICE });
			this.put(Type.ROCK, new Type[] { FLYING, BUG, FIRE, ICE });
			this.put(Type.GROUND, new Type[] { ELECTRIC, POISON, ROCK, FIRE, STEEL });
			this.put(Type.STEEL, new Type[] { ROCK, ICE, FAIRY });
			this.put(Type.GHOST, new Type[] { GHOST, PSYCHO });
			this.put(Type.DARK, new Type[] { GHOST, PSYCHO });
			this.put(Type.NORMAL, new Type[] {});
			this.put(Type.FAIRY, new Type[] { FIGHTING, DRAGON, DARK });
			this.put(Type.PSYCHO, new Type[] { FIGHTING, POISON });
			this.put(Type.FLYING, new Type[] { GRASS, BUG, FIGHTING });
			this.put(Type.BUG, new Type[] { GRASS, PSYCHO, DARK });
			this.put(Type.POISON, new Type[] { GRASS, FAIRY });
			this.put(Type.ELECTRIC, new Type[] { WATER, FLYING });
			this.put(Type.ICE, new Type[] { FLYING, GROUND, GRASS, DRAGON });
			this.put(Type.FIGHTING, new Type[] { NORMAL, ROCK, STEEL, ICE, DARK });
			this.put(Type.DRAGON, new Type[] { DRAGON });
		}
	};

	private static EnumMap<Type, Type[]> weakMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;

		{
			this.put(Type.WATER, new Type[] { WATER, GRASS, DRAGON });
			this.put(Type.GRASS, new Type[] { FLYING, POISON, BUG, STEEL, FIRE, GRASS, DRAGON });
			this.put(Type.FIRE, new Type[] { ROCK, FIRE, WATER, DRAGON });
			this.put(Type.ROCK, new Type[] { FIGHTING, GROUND, STEEL });
			this.put(Type.GROUND, new Type[] { BUG, GRASS });
			this.put(Type.STEEL, new Type[] { STEEL, FIRE, WATER, ELECTRIC });
			this.put(Type.GHOST, new Type[] { DARK });
			this.put(Type.DARK, new Type[] { FIGHTING, DARK, FAIRY });
			this.put(Type.NORMAL, new Type[] { ROCK, STEEL });
			this.put(Type.FAIRY, new Type[] { POISON, STEEL, FIRE });
			this.put(Type.PSYCHO, new Type[] { STEEL, PSYCHO });
			this.put(Type.FLYING, new Type[] { ROCK, STEEL, ELECTRIC });
			this.put(Type.BUG, new Type[] { FIGHTING, FLYING, POISON, GHOST, STEEL, FIRE, FAIRY });
			this.put(Type.POISON, new Type[] { POISON, GROUND, ROCK, GHOST });
			this.put(Type.ELECTRIC, new Type[] { GRASS, ELECTRIC, DRAGON });
			this.put(Type.ICE, new Type[] { STEEL, FIRE, WATER, ICE });
			this.put(Type.FIGHTING, new Type[] { FLYING, POISON, BUG, PSYCHO });
			this.put(Type.DRAGON, new Type[] { STEEL });
		}
	};

	private static EnumMap<Type, Type[]> uselessMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;

		{
			this.put(Type.WATER, new Type[] {});
			this.put(Type.GRASS, new Type[] {});
			this.put(Type.FIRE, new Type[] {});
			this.put(Type.ROCK, new Type[] {});
			this.put(Type.GROUND, new Type[] { FLYING });
			this.put(Type.STEEL, new Type[] {});
			this.put(Type.GHOST, new Type[] { NORMAL });
			this.put(Type.DARK, new Type[] {});
			this.put(Type.NORMAL, new Type[] { GHOST });
			this.put(Type.FAIRY, new Type[] {});
			this.put(Type.PSYCHO, new Type[] { DARK });
			this.put(Type.FLYING, new Type[] {});
			this.put(Type.BUG, new Type[] {});
			this.put(Type.POISON, new Type[] { STEEL });
			this.put(Type.ELECTRIC, new Type[] { GROUND });
			this.put(Type.ICE, new Type[] {});
			this.put(Type.FIGHTING, new Type[] { GHOST });
			this.put(Type.DRAGON, new Type[] { FAIRY });
		}
	};

	private Type(String name) {
		this.name = name;
	}

	public static double getEffectiveness(Type attackType, Pokemon defender) {
		Type[] targetTypes = defender.getTypes();
		double factor = DEFAULT;
		for (Type curType : strongMapping.get(attackType)) {
			for (Type curTarget : targetTypes) {
				if (curType.equals(curTarget)) {
					factor *= STRONG;
				}
			}
		}
		for (Type curType : weakMapping.get(attackType)) {
			for (Type curTarget : targetTypes) {
				if (curType.equals(curTarget)) {
					factor *= WEAK;
				}
			}
		}
		for (Type curType : uselessMapping.get(attackType)) {
			for (Type curTarget : targetTypes) {
				if (curType.equals(curTarget)) {
					factor = USELESS;
				}
			}
		}
		return factor;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static double calcSTAB(Pokemon attacker, Move usedMove) {
		for (Type t : attacker.getTypes()) {
			if (usedMove.getMoveType().equals(t)) {
				return STAB;
			}
		}
		return DEFAULT;
	}

	public static Color getColor(Type type) {
		if (colorMapping.get(type) != null) {
			return colorMapping.get(type);
		}
		return Color.MAGENTA;
	}

	public static Type get(String type) {
		switch (type) {
		case "water":
			return Type.WATER;
		case "grass":
			return Type.GRASS;
		case "fire":
			return Type.FIRE;
		case "rock":
			return Type.ROCK;
		case "ground":
			return Type.GROUND;
		case "steel":
			return Type.STEEL;
		case "ghost":
			return Type.GHOST;
		case "dark":
			return Type.DARK;
		case "normal":
			return Type.NORMAL;
		case "fairy":
			return Type.FAIRY;
		case "psycho":
			return Type.PSYCHO;
		case "flying":
			return Type.FLYING;
		case "bug":
			return Type.BUG;
		case "poison":
			return Type.POISON;
		case "electric":
			return Type.ELECTRIC;
		case "ice":
			return Type.ICE;
		case "fighting":
			return Type.FIGHTING;
		case "dragon":
			return Type.DRAGON;
		case "":
			return null;
		default:
		}
		return null;
	}
}

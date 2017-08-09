package de.alexanderciupka.pokemon.pokemon;

import java.awt.Color;
import java.util.EnumMap;

public enum Type {

	WATER("Wasser"), GRASS("Pflanze"), FIRE("Feuer"),
	ROCK("Gestein"), GROUND("Boden"), STEEL("Stahl"),
	GHOST("Geist"), DARK("Unlicht"), NORMAL("Normal"),
	FAIRY("Fee"), PSYCHO("Psycho"), FLYING("Flug"),
	BUG("Käfer"), POISON("Gift"), ELECTRIC("Elektro"),
	ICE("Eis"), FIGHTING("Kampf"), DRAGON("Drache");
	
	private String name;
	
	public static final double STRONG = 2.0;
	public static final double WEAK = 0.5;
	public static final double USELESS = 0.0;
	public static final double DEFAULT = 1.0;
	public static final double STAB = 1.5;
	
	private static EnumMap<Type, Color> colorMapping = new EnumMap<Type, Color>(Type.class) {
		private static final long serialVersionUID = 1L;
		{
			put(Type.WATER, new Color(0, 0, 255));
			put(Type.GRASS, new Color(0, 204, 0));
			put(Type.FIRE, new Color(204, 0, 0));
			put(Type.ROCK, new Color(102, 51, 0));
			put(Type.GROUND, new Color(204, 102, 0));
			put(Type.STEEL, new Color(192, 192, 192));
			put(Type.GHOST, new Color(51, 0, 102));
			put(Type.DARK, new Color(0, 0, 0));
			put(Type.NORMAL, Color.LIGHT_GRAY);
			put(Type.FAIRY, new Color(255, 153, 204));
			put(Type.PSYCHO, new Color(255, 102, 255));
			put(Type.FLYING, new Color(204, 229, 255));
			put(Type.BUG, new Color(153, 153, 0));
			put(Type.POISON, new Color(153, 0, 153));
			put(Type.ELECTRIC, new Color(255, 255, 0));
			put(Type.ICE, new Color(153, 255, 255));
			put(Type.FIGHTING, new Color(255, 128, 0));
			put(Type.DRAGON, new Color(0, 0, 102));
			
		}
	};
	
	//TODO
	private static EnumMap<Type, Type[]> strongMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;
		{
			put(Type.WATER, new Type[]{GROUND, ROCK, FIRE});
			put(Type.GRASS, new Type[]{GROUND, ROCK, WATER});
			put(Type.FIRE, new Type[]{BUG, STEEL, GRASS, ICE});
			put(Type.ROCK, new Type[]{FLYING, BUG, FIRE, ICE});
			put(Type.GROUND, new Type[]{ELECTRIC, POISON, ROCK, FIRE, STEEL});
			put(Type.STEEL, new Type[]{ROCK, ICE, FAIRY});
			put(Type.GHOST, new Type[]{GHOST, PSYCHO});
			put(Type.DARK, new Type[]{GHOST, PSYCHO});
			put(Type.NORMAL, new Type[]{});
			put(Type.FAIRY, new Type[]{FIGHTING, DRAGON, DARK});
			put(Type.PSYCHO, new Type[]{FIGHTING, POISON});
			put(Type.FLYING, new Type[]{GRASS, BUG, FIGHTING});
			put(Type.BUG, new Type[]{GRASS, PSYCHO, DARK});
			put(Type.POISON, new Type[]{GRASS, FAIRY});
			put(Type.ELECTRIC, new Type[]{WATER, FLYING});
			put(Type.ICE, new Type[]{FLYING, GROUND, GRASS, DRAGON});
			put(Type.FIGHTING, new Type[]{NORMAL, ROCK, STEEL, ICE, DARK});
			put(Type.DRAGON, new Type[]{DRAGON});
		}
	};
	
	private static EnumMap<Type, Type[]> weakMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;

		{
			put(Type.WATER, new Type[]{WATER, GRASS, DRAGON});
			put(Type.GRASS, new Type[]{FLYING, POISON, BUG, STEEL, FIRE, GRASS, DRAGON});
			put(Type.FIRE, new Type[]{ROCK, FIRE, WATER, DRAGON});
			put(Type.ROCK, new Type[]{FIGHTING, GROUND, STEEL});
			put(Type.GROUND, new Type[]{BUG, GRASS});
			put(Type.STEEL, new Type[]{STEEL, FIRE, WATER, ELECTRIC});
			put(Type.GHOST, new Type[]{DARK});
			put(Type.DARK, new Type[]{FIGHTING, DARK, FAIRY});
			put(Type.NORMAL, new Type[]{ROCK, STEEL});
			put(Type.FAIRY, new Type[]{POISON, STEEL, FIRE});
			put(Type.PSYCHO, new Type[]{STEEL, PSYCHO});
			put(Type.FLYING, new Type[]{ROCK, STEEL, ELECTRIC});
			put(Type.BUG, new Type[]{FIGHTING, FLYING, POISON, GHOST, STEEL, FIRE, FAIRY});
			put(Type.POISON, new Type[]{POISON, GROUND, ROCK, GHOST});
			put(Type.ELECTRIC, new Type[]{GRASS, ELECTRIC, DRAGON});
			put(Type.ICE, new Type[]{STEEL, FIRE, WATER, ICE});
			put(Type.FIGHTING, new Type[]{FLYING, POISON, BUG, PSYCHO});
			put(Type.DRAGON, new Type[]{STEEL});
		}
	};
	

	private static EnumMap<Type, Type[]> uselessMapping = new EnumMap<Type, Type[]>(Type.class) {
		private static final long serialVersionUID = 1L;

		{
			put(Type.WATER, new Type[]{});
			put(Type.GRASS, new Type[]{});
			put(Type.FIRE, new Type[]{});
			put(Type.ROCK, new Type[]{});
			put(Type.GROUND, new Type[]{FLYING});
			put(Type.STEEL, new Type[]{});
			put(Type.GHOST, new Type[]{NORMAL});
			put(Type.DARK, new Type[]{});
			put(Type.NORMAL, new Type[]{GHOST});
			put(Type.FAIRY, new Type[]{});
			put(Type.PSYCHO, new Type[]{DARK});
			put(Type.FLYING, new Type[]{});
			put(Type.BUG, new Type[]{});
			put(Type.POISON, new Type[]{STEEL});
			put(Type.ELECTRIC, new Type[]{GROUND});
			put(Type.ICE, new Type[]{});
			put(Type.FIGHTING, new Type[]{GHOST});
			put(Type.DRAGON, new Type[]{FAIRY});
		}
	};
	
	private Type(String name) {
		this.name = name;
	}
	
	public static double getEffectiveness(Type attackType, Type[] targetTypes) {
		double factor = DEFAULT;
		for(Type curType : strongMapping.get(attackType)) {
			for(Type curTarget : targetTypes) {
				if(curType.equals(curTarget)) {
					factor *= STRONG;
				}
			}
		}
		for(Type curType : weakMapping.get(attackType)) {
			for(Type curTarget : targetTypes) {
				if(curType.equals(curTarget)) {
					factor *= WEAK;
				}
			}
		}
		for(Type curType : uselessMapping.get(attackType)) {
			for(Type curTarget : targetTypes) {
				if(curType.equals(curTarget)) {
					factor *= USELESS;
				}
			}
		}
		return factor;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public static double calcSTAB(Pokemon attacker, Move usedMove) {
		for(Type t : attacker.getTypes()) {
			if(usedMove.getMoveType().equals(t)) {
				return STAB;
			}
		}
		return DEFAULT;
	}
	
	public static Color getColor(Type type) {
		if(colorMapping.get(type) != null) {
			return colorMapping.get(type);
		}
		return Color.MAGENTA;
	}

	public static Type get(String type) {
		switch(type) {
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
			System.err.println(type);
		}
		return null;
	}
}

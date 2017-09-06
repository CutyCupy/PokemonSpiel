package de.alexanderciupka.pokemon.map;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import de.alexanderciupka.pokemon.main.Main;

public enum RouteType {
	
	ARENA, CAVE, CITY,
	DESERT, FIELD, INDOOR,
	LIBRARY, SNOW, WATER;
	
	private HashMap<RouteType, Image> fightBackground;
	
	public Image getBattleBackground() {
		if(fightBackground == null) {
			fightBackground = new HashMap<>();
			for(RouteType r : RouteType.values()) {
				try {
					fightBackground.put(r, ImageIO.read(new File(
							Main.class.getResource("/battles/battlebg" + r.getSaveName() + ".png").getFile())));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return this.fightBackground.get(this);
	}
	
	public String getSaveName() {
		String name = this.name();
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}

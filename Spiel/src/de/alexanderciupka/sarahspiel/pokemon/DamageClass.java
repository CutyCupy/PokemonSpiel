package de.alexanderciupka.sarahspiel.pokemon;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.alexanderciupka.hoverbutton.Main;

public enum DamageClass {
	
	NO_DAMAGE,
	PHYSICAL,
	SPECIAL;
	
	public static final Image PHYSICAL_IMAGE = loadPhysical();
	public static final Image SPECIAL_IMAGE = loadSpecial();
	public static final Image NO_DAMAGE_IMAGE = loadNoDamage();

	private static Image loadPhysical() {
		try {
			return ImageIO.read(Main.class.getResource("/pokemon/physical.png")).getScaledInstance(60, 25, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Image loadSpecial() {
		try {
			return ImageIO.read(Main.class.getResource("/pokemon/special.png")).getScaledInstance(60, 25, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Image loadNoDamage() {
		try {
			return ImageIO.read(Main.class.getResource("/pokemon/no_damage.png")).getScaledInstance(60, 25, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}

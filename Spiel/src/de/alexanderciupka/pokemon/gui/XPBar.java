/**
 * @author CutyCupy
 */
package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

/**
 * 
 * @author CutyCupy
 */
public class XPBar extends HPBar {

	private static final Color BLUE = new Color(0, 102, 255);

	@Override
	public Color getForeground() {
		return BLUE;
	}

}

package de.alexanderciupka.pokemonspiel.routecreation.frames;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.alexanderciupka.pokemon.map.GameController;

public class OtherRoutesFrame extends JFrame {

	
	public OtherRoutesFrame() {
		setBounds(1280, 750, 640, 330);
		JPanel panel = new JPanel(new GridLayout(0, 1));
		
		for(String route : GameController.getInstance().getRouteAnalyzer().getOriginalRoutes().keySet()) {
			JButton button = new JButton(GameController.getInstance().getRouteAnalyzer().getRouteById(route, true).getName() + " - " + route);
			panel.add(button);
		}
		
		JScrollPane scroll = new JScrollPane(panel);
		this.getContentPane().add(scroll);
		
		this.setResizable(false);
		this.setUndecorated(true);
	}
}

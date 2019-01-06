package de.alexanderciupka.pokemonspiel.routecreation.frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.alexanderciupka.pokemon.map.GameController;

public class TerrainFrame extends JFrame {
	
	private String selected;
	
	public TerrainFrame() {
		setBounds(1280, 600, 640, 150);
		
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 10));
		
		ArrayList<String> sprites = new ArrayList<>( GameController.getInstance().getRouteAnalyzer().getTerrainNames());
		sprites.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		
		for(String sprite : sprites) {
			BufferedImage img = GameController.getInstance().getRouteAnalyzer().getTerrainByName(sprite);
			JLabel label = new JLabel(new ImageIcon(img.getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
			label.setPreferredSize(new Dimension(60, 60));
			label.setName(sprite);
			
			label.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					if(!e.getComponent().getName().equals(selected)) {
						label.setBorder(null);
					}
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					if(!e.getComponent().getName().equals(selected)) {
						label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
					}
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if(selected == null) {
						label.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
						selected = sprite;
					} else {
						if(sprite.equals(selected)) {
							selected = null;
							label.setBorder(null);
						} else {
							for(Component c : panel.getComponents()) {
								System.out.println(c.getName() + " - " + selected);
								if(selected.equals(c.getName())) {
									((JLabel) c).setBorder(null);
								}
							}
							selected = sprite;
							label.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
						}
					}
				}
			});
			
			panel.add(label);
		}
		
		JScrollPane pane = new JScrollPane(panel);
		pane.getVerticalScrollBar().setUnitIncrement(60);
		this.add(pane);
		
		setResizable(false);
		setUndecorated(true);
	}
	
	public String getSelectedSprite() {
		return this.selected;
	}

}


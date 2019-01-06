package de.alexanderciupka.pokemonspiel.routecreation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.characters.Direction;

public class EntityPanel extends BasePanel {

	protected JTextField encounterField;
	protected JTextField poolField;
	protected JTextField warpIDField;
	protected JTextField newRouteField;
	protected JTextField newXField;
	protected JTextField newYField;
	
	protected JComboBox<String> terrainBox;
	protected JComboBox<String> spriteBox;
	protected JComboBox<String> directionBox;

	protected JCheckBox leftCheck;
	protected JCheckBox rightCheck;
	protected JCheckBox topCheck;
	protected JCheckBox bottomCheck;
	protected JCheckBox warpCheck;
	protected JCheckBox accessibilityCheck;

	/**
	 * Create the frame.
	 */
	public EntityPanel(int x, int y, JsonObject currentJson) {
		super(x, y, currentJson);
		this.setBounds(10, 11, 414, 510);
		this.setLayout(null);
		
		JLabel terrainLabel = new JLabel("Terrain");
		terrainLabel.setBounds(10, 50, 115, 20);
		this.add(terrainLabel);
		
		JLabel spriteLabel = new JLabel("Sprite");
		spriteLabel.setBounds(10, 81, 115, 20);
		this.add(spriteLabel);
		
		JLabel encounterRateLabel = new JLabel("Encounter Rate");
		encounterRateLabel.setBounds(10, 112, 115, 20);
		this.add(encounterRateLabel);
		
		JLabel label_2 = new JLabel("Pool");
		label_2.setBounds(10, 143, 115, 20);
		this.add(label_2);
		
		JLabel accessibilityLabel = new JLabel("Accessibility");
		accessibilityLabel.setBounds(10, 174, 115, 20);
		this.add(accessibilityLabel);
		
		this.accessibilityCheck = new JCheckBox();
		this.accessibilityCheck.setHorizontalAlignment(SwingConstants.CENTER);
		this.accessibilityCheck.setBounds(178, 174, 226, 23);
		this.accessibilityCheck.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftCheck.setSelected(accessibilityCheck.isSelected());
				topCheck.setSelected(accessibilityCheck.isSelected());
				rightCheck.setSelected(accessibilityCheck.isSelected());
				bottomCheck.setSelected(accessibilityCheck.isSelected());
			}
		});
		this.add(accessibilityCheck);
		
		JLabel leftLabel = new JLabel("Left");
		leftLabel.setBounds(40, 205, 85, 20);
		this.add(leftLabel);
		
		JLabel rightLabel = new JLabel("Right");
		rightLabel.setBounds(40, 236, 85, 20);
		this.add(rightLabel);
		
		JLabel topLabel = new JLabel("Top");
		topLabel.setBounds(40, 267, 85, 20);
		this.add(topLabel);
		
		JLabel bottomLabel = new JLabel("Bottom");
		bottomLabel.setBounds(40, 298, 85, 20);
		this.add(bottomLabel);
		
		JLabel warpLabel = new JLabel("Warp");
		warpLabel.setBounds(10, 329, 115, 20);
		this.add(warpLabel);
		
		JLabel newYLabel = new JLabel("New Y");
		newYLabel.setBounds(40, 453, 85, 20);
		this.add(newYLabel);
		
		JLabel newXLabel = new JLabel("New X");
		newXLabel.setBounds(40, 422, 85, 20);
		this.add(newXLabel);
		
		JLabel newRouteLabel = new JLabel("New Route");
		newRouteLabel.setBounds(40, 391, 85, 20);
		this.add(newRouteLabel);
		
		JLabel warpIDLabel = new JLabel("ID");
		warpIDLabel.setBounds(40, 360, 85, 20);
		this.add(warpIDLabel);
		
		JLabel directionChangeLabel = new JLabel("Direction Change");
		directionChangeLabel.setBounds(40, 484, 85, 20);
		this.add(directionChangeLabel);
		
		terrainBox = new JComboBox<String>();
		terrainBox.setBounds(178, 50, 226, 20);
		
		for(File f : new File("D:/git/PokemonSpiel/Spiel/res/routes/terrain/").listFiles()) {
			terrainBox.addItem(f.getName().substring(0, f.getName().length() - 4));
		}
		
		if(RouteCreatorController.getInstance().getTerrains().getSelectedSprite() != null) {
			this.terrainBox.setSelectedItem(RouteCreatorController.getInstance().getTerrains().getSelectedSprite());
		} else if(currentJson.has("terrain")) {
			this.terrainBox.setSelectedItem(currentJson.get("terrain").getAsString());
		} else {
			this.terrainBox.setSelectedItem("free");
		}
		
		this.add(terrainBox);
		
		spriteBox = new JComboBox<String>();
		spriteBox.setBounds(178, 81, 226, 20);
		
		for(File f : new File("D:/git/PokemonSpiel/Spiel/res/routes/Entities/").listFiles()) {
			spriteBox.addItem(f.getName().substring(0, f.getName().length() - 4));
		}
		
		if(RouteCreatorController.getInstance().getSprites().getSelectedSprite() != null) {
			this.spriteBox.setSelectedItem(RouteCreatorController.getInstance().getSprites().getSelectedSprite());
		} else if(currentJson.has("sprite")) {
			this.spriteBox.setSelectedItem(currentJson.get("sprite").getAsString());
		} else {
			this.spriteBox.setSelectedItem("free");
		}
		
		this.add(spriteBox);
		
		encounterField = new JTextField();
		encounterField.setBounds(178, 112, 226, 20);
		if(currentJson.has("encounter_rate")) {
			this.encounterField.setText(currentJson.get("encounter_rate").getAsString());
		}
		this.add(encounterField);
		
		poolField = new JTextField();
		poolField.setColumns(10);
		poolField.setBounds(178, 143, 226, 20);
		if(currentJson.has("pool")) {
			poolField.setText(currentJson.get("pool").getAsString());
		}
		this.add(poolField);
		
		leftCheck = new JCheckBox("");
		leftCheck.setHorizontalAlignment(SwingConstants.CENTER);
		leftCheck.setBounds(178, 204, 226, 23);
		leftCheck.setSelected(currentJson.has("accessibility") ? currentJson.get("accessibility").getAsJsonObject().get("left").getAsBoolean() : false);
		this.add(leftCheck);
		
		warpIDField = new JTextField();
		warpIDField.setEnabled(false);
		warpIDField.setColumns(10);
		warpIDField.setBounds(178, 360, 226, 20);
		this.add(warpIDField);
		
		newRouteField = new JTextField();
		newRouteField.setEnabled(false);
		newRouteField.setColumns(10);
		newRouteField.setBounds(178, 391, 226, 20);
		this.add(newRouteField);
		
		newXField = new JTextField();
		newXField.setEnabled(false);
		newXField.setColumns(10);
		newXField.setBounds(178, 422, 226, 20);
		this.add(newXField);
		
		newYField = new JTextField();
		newYField.setEnabled(false);
		newYField.setColumns(10);
		newYField.setBounds(178, 453, 226, 20);
		this.add(newYField);
		
		directionBox = new JComboBox<String>();
		directionBox.setEnabled(false);
		directionBox.setBounds(178, 484, 226, 20);
		
		directionBox.addItem(Direction.NONE.name());
		directionBox.addItem(Direction.UP.name());
		directionBox.addItem(Direction.RIGHT.name());
		directionBox.addItem(Direction.DOWN.name());
		directionBox.addItem(Direction.LEFT.name());
		if(currentJson.has("warp")) {
			this.warpIDField.setText(currentJson.get("warp").getAsJsonObject().get("id").getAsString());
			this.newRouteField.setText(currentJson.get("new_route").getAsJsonObject().get("new_route").getAsString());
			this.newXField.setText(currentJson.get("new_x").getAsJsonObject().get("new_x").getAsString());
			this.newYField.setText(currentJson.get("new_y").getAsJsonObject().get("new_y").getAsString());
			this.directionBox.setSelectedItem(Direction.valueOf(currentJson.get("warp").getAsJsonObject().get("direction").getAsString().toUpperCase()));
		}
		
		this.add(directionBox);
		
		rightCheck = new JCheckBox("");
		rightCheck.setHorizontalAlignment(SwingConstants.CENTER);
		rightCheck.setBounds(178, 235, 226, 23);
		rightCheck.setSelected(currentJson.has("accessibility") ? currentJson.get("accessibility").getAsJsonObject().get("right").getAsBoolean() : false);
		this.add(rightCheck);
		
		topCheck = new JCheckBox("");
		topCheck.setHorizontalAlignment(SwingConstants.CENTER);
		topCheck.setBounds(178, 266, 226, 23);
		topCheck.setSelected(currentJson.has("accessibility") ? currentJson.get("accessibility").getAsJsonObject().get("top").getAsBoolean() : false);
		this.add(topCheck);
		
		bottomCheck = new JCheckBox("");
		bottomCheck.setHorizontalAlignment(SwingConstants.CENTER);
		bottomCheck.setBounds(178, 295, 226, 23);
		bottomCheck.setSelected(currentJson.has("accessibility") ? currentJson.get("accessibility").getAsJsonObject().get("bottom").getAsBoolean() : false);
		this.add(bottomCheck);
		
		warpCheck = new JCheckBox("");
		warpCheck.setHorizontalAlignment(SwingConstants.CENTER);
		warpCheck.setBounds(178, 328, 226, 23);
		
		warpCheck.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				directionBox.setEnabled(source.isSelected());
				newXField.setEnabled(source.isSelected());
				newYField.setEnabled(source.isSelected());
				newRouteField.setEnabled(source.isSelected());
				warpIDField.setEnabled(source.isSelected());
			}
		});
		this.add(warpCheck);
	}

	@Override
	public JsonObject getJSON() {
		JsonObject currentJson = new JsonObject();
		currentJson.addProperty("terrain", this.terrainBox.getSelectedItem().toString());
		currentJson.addProperty("sprite", this.spriteBox.getSelectedItem().toString());
		try {
			currentJson.addProperty("encounter_rate", Float.parseFloat(this.encounterField.getText()));
		} catch(Exception e) {
			currentJson.addProperty("encounter_rate", 0);
		}
		try {
			currentJson.addProperty("pool", Float.parseFloat(this.poolField.getText()));
		} catch(Exception e) {
			currentJson.addProperty("pool", 0);
		}
		
		JsonObject accessibility = new JsonObject();
		accessibility.addProperty("left", this.leftCheck.isSelected());
		accessibility.addProperty("right", this.rightCheck.isSelected());
		accessibility.addProperty("top", this.topCheck.isSelected());
		accessibility.addProperty("bottom", this.bottomCheck.isSelected());
		currentJson.add("accessibility", accessibility);
		
		if(this.warpCheck.isSelected()) {
			JsonObject warp = new JsonObject();
			warp.addProperty("id", this.warpIDField.getText());
			warp.addProperty("new_route", this.newRouteField.getText());
			warp.addProperty("new_x", this.newXField.getText());
			warp.addProperty("new_y", this.newYField.getText());
			warp.addProperty("direction_change", this.directionBox.getSelectedItem().toString());
			
			currentJson.add("warp", warp);
		}
		
		return currentJson;
	}

}

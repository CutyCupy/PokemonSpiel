package de.alexanderciupka.pokemonspiel.routecreation.frames;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.gui.overlay.FogType;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.gui.overlay.SnowType;
import de.alexanderciupka.pokemon.map.RouteType;
import de.alexanderciupka.pokemonspiel.routecreation.RouteCreatorController;

public class RouteDataFrame extends JFrame {

	private JComboBox<Integer> widthBox;
	private JComboBox<Integer> heightBox;
	private JComboBox<RouteType> routeTypeBox;
	private JComboBox<RainType> rainTypeBox;
	private JComboBox<SnowType> snowTypeBox;
	private JComboBox<FogType> fogTypeBox;

	private JTextField nameField;
	private JTextField idField;

	private JCheckBox darkBox;

	public RouteDataFrame(JsonObject data) {
		this.setBounds(0, 0, 320, 360);
		this.getContentPane().setLayout(null);

		JLabel nameLabel = new JLabel("Name: ");
		nameLabel.setBounds(10, 10, 150, 20);
		this.getContentPane().add(nameLabel);

		this.nameField = new JTextField("");
		this.nameField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateID();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateID();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateID();
			}

			public void updateID() {
				idField.setText(nameField.getText().toLowerCase().replace(" ", "_"));
			}
		});
		this.nameField.setBounds(160, 10, 150, 20);
		this.getContentPane().add(this.nameField);

		JLabel idLabel = new JLabel("ID: ");
		idLabel.setBounds(10, 40, 150, 20);
		this.getContentPane().add(idLabel);

		idField = new JTextField("");
		idField.setBounds(160, 40, 150, 20);
		idField.setEnabled(false);
		this.getContentPane().add(idField);

		JLabel widthLabel = new JLabel("Width: ");
		widthLabel.setBounds(10, 70, 150, 20);
		this.getContentPane().add(widthLabel);

		this.widthBox = new JComboBox<>();
		this.widthBox.setBounds(160, 70, 150, 20);
		this.getContentPane().add(this.widthBox);

		JLabel heightLabel = new JLabel("Height: ");
		heightLabel.setBounds(10, 100, 150, 20);
		this.getContentPane().add(heightLabel);

		this.heightBox = new JComboBox<>();
		this.heightBox.setBounds(160, 100, 150, 20);
		this.getContentPane().add(this.heightBox);

		for (int i = 1; i <= 200; i++) {
			this.widthBox.addItem(i);
			this.heightBox.addItem(i);
		}
		this.widthBox.setSelectedItem(100);
		this.heightBox.setSelectedItem(100);

		JLabel darkLabel = new JLabel("Dark? ");
		darkLabel.setBounds(10, 130, 150, 20);
		this.getContentPane().add(darkLabel);

		this.darkBox = new JCheckBox();
		this.darkBox.setHorizontalAlignment(SwingConstants.CENTER);
		this.darkBox.setBounds(160, 130, 150, 20);
		this.getContentPane().add(this.darkBox);

		JLabel rainLabel = new JLabel("Rain: ");
		rainLabel.setBounds(10, 160, 150, 20);
		this.getContentPane().add(rainLabel);

		this.rainTypeBox = new JComboBox<RainType>(RainType.values());
		this.rainTypeBox.setBounds(160, 160, 150, 20);
		this.getContentPane().add(this.rainTypeBox);

		JLabel fogLabel = new JLabel("Fog: ");
		fogLabel.setBounds(10, 190, 150, 20);
		this.getContentPane().add(fogLabel);

		this.fogTypeBox = new JComboBox<FogType>(FogType.values());
		this.fogTypeBox.setBounds(160, 190, 150, 20);
		this.getContentPane().add(this.fogTypeBox);

		JLabel snowLabel = new JLabel("Snow: ");
		snowLabel.setBounds(10, 220, 150, 20);
		this.getContentPane().add(snowLabel);

		this.snowTypeBox = new JComboBox<SnowType>(SnowType.values());
		this.snowTypeBox.setBounds(160, 220, 150, 20);
		this.getContentPane().add(this.snowTypeBox);

		JLabel routeLabel = new JLabel("Type: ");
		routeLabel.setBounds(10, 250, 150, 20);
		this.getContentPane().add(routeLabel);

		this.routeTypeBox = new JComboBox<RouteType>(RouteType.values());
		this.routeTypeBox.setBounds(160, 250, 150, 20);
		this.getContentPane().add(this.routeTypeBox);

		JButton confirmButton = new JButton("Confirm");
		confirmButton.setBounds(10, 280, 300, 50);
		this.getContentPane().add(confirmButton);

		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JsonObject data = RouteCreatorController.getInstance().getData();
				data.addProperty("id", idField.getText());
				data.getAsJsonObject("route").addProperty("name", nameField.getText());
				data.getAsJsonObject("route").getAsJsonObject("properties").addProperty("dark", darkBox.isSelected());
				data.getAsJsonObject("route").getAsJsonObject("properties").addProperty("type",
						routeTypeBox.getSelectedItem().toString());
				data.getAsJsonObject("route").getAsJsonObject("properties").addProperty("rain",
						rainTypeBox.getSelectedItem().toString());
				data.getAsJsonObject("route").getAsJsonObject("properties").addProperty("snow",
						snowTypeBox.getSelectedItem().toString());
				data.getAsJsonObject("route").getAsJsonObject("properties").addProperty("fog",
						fogTypeBox.getSelectedItem().toString());

				RouteCreatorController.getInstance().setData(data);
				RouteCreatorController.getInstance().getEntities().updateSize((int) widthBox.getSelectedItem(),
						(int) heightBox.getSelectedItem());
			}
		});

		this.setResizable(false);
		this.setUndecorated(true);
		
	}

	public Dimension getRouteSize() {
		return new Dimension((int) this.widthBox.getSelectedItem(), (int) this.heightBox.getSelectedItem());
	}

}

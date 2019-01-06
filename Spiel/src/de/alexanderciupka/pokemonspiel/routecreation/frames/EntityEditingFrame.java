package de.alexanderciupka.pokemonspiel.routecreation.frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemonspiel.routecreation.BasePanel;
import de.alexanderciupka.pokemonspiel.routecreation.EntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.GateEntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.ItemEntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.MoveEntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.PokemonEntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.RouteCreatorController;
import de.alexanderciupka.pokemonspiel.routecreation.SignEntityPanel;
import de.alexanderciupka.pokemonspiel.routecreation.WaterEntityPanel;

public class EntityEditingFrame extends JFrame {

	private JPanel contentPane;

	private BasePanel currentPanel;

	private JComboBox<String> entityTypeBox;

	private JButton cancel;
	private JButton ok;


	/**
	 * Create the frame.
	 */
	public EntityEditingFrame(int x, int y) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		setResizable(false);

		entityTypeBox = new JComboBox<String>();

		entityTypeBox.addItem("Entity");
		entityTypeBox.addItem("ButtonEntity");
		entityTypeBox.addItem("CrackedEntity");
		entityTypeBox.addItem("GateEntity");
		entityTypeBox.addItem("GeneratorEntity");
		entityTypeBox.addItem("HatchEntity");
		entityTypeBox.addItem("IceEntity");
		entityTypeBox.addItem("ItemEntity");
		entityTypeBox.addItem("MoveEntity");
		entityTypeBox.addItem("PokemonEntity");
		entityTypeBox.addItem("QuestionEntity");
		entityTypeBox.addItem("SignEntity");
		entityTypeBox.addItem("WaterEntity");

		entityTypeBox.setBounds(10, 11, 394, 28);

		entityTypeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> source = ((JComboBox<String>) e.getSource());
				JsonObject json = RouteCreatorController.getInstance().getEntity(x, y);
				if(json == null) {
					json = new JsonObject();
				}
				switch (source.getSelectedItem().toString()) {
				case "ItemEntity":
					EntityEditingFrame.this.updatePanel(new ItemEntityPanel(x, y, json));
					break;
				case "PokemonEntity":
					EntityEditingFrame.this.updatePanel(new PokemonEntityPanel(x, y, json));
					break;
				case "SignEntity":
					EntityEditingFrame.this.updatePanel(new SignEntityPanel(x, y, json));
					break;
				case "GateEntity":
					EntityEditingFrame.this.updatePanel(new GateEntityPanel(x, y, json));
					break;
				case "WaterEntity":
					EntityEditingFrame.this.updatePanel(new WaterEntityPanel(x, y, json));
					break;
				case "MoveEntity":
					EntityEditingFrame.this.updatePanel(new MoveEntityPanel(x, y, json));
					break;
				default:
					EntityEditingFrame.this.updatePanel(new EntityPanel(x, y, json));
					break;
				}
			}
		});

		this.add(entityTypeBox);

		cancel = new JButton("Cancel");
		ok = new JButton("OK");

		cancel.setBounds(240, 0, 80, 20);
		ok.setBounds(330, 0, 80, 20);

		this.cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EntityEditingFrame.this.dispose();
			}
		});
		
		this.ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JsonObject result = EntityEditingFrame.this.currentPanel.getJSON();
				result.addProperty("type", EntityEditingFrame.this.entityTypeBox.getSelectedItem().toString());
				EntityEditingFrame.this.dispose();
				RouteCreatorController.getInstance().setEntity(x, y, result);
			}
		});

		this.contentPane.add(cancel);
		this.contentPane.add(ok);
		
		if(RouteCreatorController.getInstance().getEntity(x, y).has("type")) {
			this.entityTypeBox.setSelectedItem(RouteCreatorController.getInstance().getEntity(x, y).get("type").getAsString());
		} else {
			this.entityTypeBox.setSelectedItem("Entity");
		}
		

		this.setVisible(true);
	}

	public void updatePanel(BasePanel newPanel) {
		for (Component c : this.contentPane.getComponents()) {
			if (c instanceof BasePanel) {
				this.contentPane.remove(c);
				break;
			}
		}
		this.contentPane.add(newPanel);
		this.currentPanel = newPanel;

		int y = 0;

		for (Component c : this.currentPanel.getComponents()) {
			y = Math.max(y, c.getY() + c.getHeight());
		}

		this.cancel.setLocation(this.cancel.getX(), y + 30);
		this.ok.setLocation(this.ok.getX(), y + 30);

		this.setSize(this.currentPanel.getWidth() + 25, this.currentPanel.getHeight() + 100);
		this.contentPane.setSize(this.getSize());
	}
	
}

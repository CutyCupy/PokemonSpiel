package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class DecisionPanel extends JPanel {

	public static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 15);

	private String result;

	public DecisionPanel(String... options) {
		int width = calculateButtonWidth(options);
		int height = (int) Math.floor(this.getFontMetrics(FONT)
				.getStringBounds("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzÄÖÜäöü", null).getHeight()) + 5;

		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setLayout(null);
		
		for (int i = 0; i < options.length; i++) {
			String s = options[i];
			JButton b = new JButton();
			b.setFont(FONT);
			b.setText(s);
			b.setSize(width, height);
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {
					((JButton) e.getSource()).setBackground(Color.WHITE);
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					((JButton) e.getSource()).setBackground(Color.LIGHT_GRAY);
				}
			});
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent a) {
					DecisionPanel.this.result = ((JButton) a.getSource()).getText();
				}
			});
			b.setBackground(Color.WHITE);
			b.setLocation(10, 10 + height * i);
			this.add(b);
		}

		this.setSize(width + 20, height * options.length + 20);

		this.setBackground(Color.BLACK);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
	}

	private int calculateButtonWidth(String... options) {
		FontMetrics f = this.getFontMetrics(FONT);
		int width = 0;
		for (String s : options) {
			width = Math.max(width, f.stringWidth(s));
		}
		return width + 50;
	}

	public String getResult() {
		while (this.result == null) {
			Thread.yield();
		}
		return this.result;
	}

}

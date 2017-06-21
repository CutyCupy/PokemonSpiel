package de.alexanderciupka.sarahspiel.gui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import de.alexanderciupka.sarahspiel.pokemon.Box;
import de.alexanderciupka.sarahspiel.pokemon.PC;

public class PCPanel extends JScrollPane {

	private JPanel contentPane;

	private GridBagConstraints gbc;
	private ArrayList<JLabel[]> labels;
	private int selected;
	private PC pc;

	private java.awt.Font font;

	private static final String ARROW = "-->";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					PCPanel frame = new PCPanel();
					frame.setVisible(true);
					frame.setPC(new PC());
					JFrame j = new JFrame();
					j.setContentPane(frame);
					j.setBounds(0, 0, 630, 630);
					j.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PCPanel() {
		font = getFont().deriveFont(45f);
		setBounds(0, 0, 630, 630);
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setBounds(0, 0, 420, 300);
		gbc = new GridBagConstraints();
		labels = new ArrayList<JLabel[]>();

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "up");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
		this.getActionMap().put("up", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelected(selected-1);
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("S"), "down");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
		this.getActionMap().put("down", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelected(selected+1);
			}
		});

		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "select");
		this.getActionMap().put("select", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayBox();
			}
		});

	}

	public void setPC(PC pc) {
		this.pc = pc;

		contentPane = new JPanel(new GridBagLayout());

		this.labels.clear();

		for(int i = 0; i < pc.getBoxes().length; i++) {
			JLabel arrow = new JLabel();
			arrow.setFont(font);
			arrow.setHorizontalAlignment(SwingConstants.CENTER);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.05;
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			contentPane.add(arrow, gbc);

			JLabel box = new JLabel(pc.getBoxes()[i].getName());
			box.setFont(font);
			box.setHorizontalAlignment(SwingConstants.LEFT);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.5;
			gbc.gridx = 1;
			gbc.gridy = i;
			gbc.gridwidth = 4;
			contentPane.add(box, gbc);

			JLabel ammount = new JLabel(pc.getBoxes()[i].getAmount() + " / " + Box.LIMIT);
			ammount.setFont(font);
			ammount.setHorizontalAlignment(SwingConstants.CENTER);
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 5;
			gbc.weightx = 0.1;
			gbc.gridy = i;
			gbc.gridwidth = 1;
			contentPane.add(ammount, gbc);
			JLabel[] currentLabels = new JLabel[]{arrow, box, ammount};
			for(int j = 0; j < currentLabels.length; j++) {
				currentLabels[j].setName(String.valueOf(i));
				currentLabels[j].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						setSelected(Integer.parseInt(e.getComponent().getName()));
					}
				});
			}
			this.labels.add(currentLabels);
		}

		this.setViewportView(contentPane);
	}

	private void setSelected(int selected) {
		this.selected = selected >= 0 ? selected % this.labels.size() : (this.labels.size() + selected) % this.labels.size();
		labels.get(this.selected)[0].setText(ARROW);
		for(int i = 0; i < labels.size(); i++) {
			if(i != this.selected) {
				labels.get(i)[0].setText("");
			}
		}

		JScrollBar vertical = this.getVerticalScrollBar();
		JLabel currentRow = labels.get(this.selected)[0];
		if(vertical.getValue() + this.getHeight() < currentRow.getY() + currentRow.getHeight()) {
			vertical.setValue(currentRow.getY() + currentRow.getHeight() - this.getHeight());
		} else if(vertical.getValue() > currentRow.getY()) {
			vertical.setValue(currentRow.getY());
		}
	}


	private void displayBox() {

	}

}

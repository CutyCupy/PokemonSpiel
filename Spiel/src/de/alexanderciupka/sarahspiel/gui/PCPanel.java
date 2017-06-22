package de.alexanderciupka.sarahspiel.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.alexanderciupka.sarahspiel.pokemon.Box;
import de.alexanderciupka.sarahspiel.pokemon.PC;

public class PCPanel extends JPanel {

	private BoxPanel contentPane;

	private int selected;
	private PC pc;

	private int currentBox;

	private java.awt.Font font;

	public BoxPanel getContentPane() {
		return contentPane;
	}

	public PCPanel() {
		font = getFont().deriveFont(45f);
		setVisible(true);
		setBounds(0, 0, 630, 630);

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

	public void setBox(int newBox) {
		this.currentBox = newBox >= 0 ? newBox % this.pc.getBoxes().length : (this.pc.getBoxes().length + newBox) %  this.pc.getBoxes().length;
		contentPane.setBox(pc.getBoxes()[currentBox]);
		contentPane.repaint();
	}

	public void setPC(PC pc) {
//		for(Component c : getComponents()) {
//			remove(c);
//		}
		this.pc = pc;

		contentPane = new BoxPanel(this);
		setBox(0);

//		this.labels.clear();
//
//		for(int i = 0; i < pc.getBoxes().length; i++) {
//			JLabel arrow = new JLabel();
//			arrow.setFont(font);
//			arrow.setHorizontalAlignment(SwingConstants.CENTER);
//			gbc.fill = GridBagConstraints.BOTH;
//			gbc.weightx = 0.05;
//			gbc.gridx = 0;
//			gbc.gridy = i;
//			gbc.gridwidth = 1;
//			contentPane.add(arrow, gbc);
//
//			JLabel box = new JLabel(pc.getBoxes()[i].getName());
//			box.setFont(font);
//			box.setHorizontalAlignment(SwingConstants.LEFT);
//			gbc.fill = GridBagConstraints.BOTH;
//			gbc.weightx = 0.5;
//			gbc.gridx = 1;
//			gbc.gridy = i;
//			gbc.gridwidth = 4;
//			contentPane.add(box, gbc);
//
//			JLabel ammount = new JLabel(pc.getBoxes()[i].getAmount() + " / " + Box.LIMIT);
//			ammount.setFont(font);
//			ammount.setHorizontalAlignment(SwingConstants.CENTER);
//			gbc.fill = GridBagConstraints.BOTH;
//			gbc.gridx = 5;
//			gbc.weightx = 0.1;
//			gbc.gridy = i;
//			gbc.gridwidth = 1;
//			contentPane.add(ammount, gbc);
//			JLabel[] currentLabels = new JLabel[]{arrow, box, ammount};
//			for(int j = 0; j < currentLabels.length; j++) {
//				currentLabels[j].setName(String.valueOf(i));
//				currentLabels[j].addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseEntered(MouseEvent e) {
//						setSelected(Integer.parseInt(e.getComponent().getName()));
//					}
//				});
//			}
//			this.labels.add(currentLabels);
//		}

	}

	private void setSelected(int selected) {
//		this.selected = selected >= 0 ? selected % this.labels.size() : (this.labels.size() + selected) % this.labels.size();
//		labels.get(this.selected)[0].setText(ARROW);
//		for(int i = 0; i < labels.size(); i++) {
//			if(i != this.selected) {
//				labels.get(i)[0].setText("");
//			}
//		}
//
//		JScrollBar vertical = this.getVerticalScrollBar();
//		JLabel currentRow = labels.get(this.selected)[0];
//		if(vertical.getValue() + this.getHeight() < currentRow.getY() + currentRow.getHeight()) {
//			vertical.setValue(currentRow.getY() + currentRow.getHeight() - this.getHeight());
//		} else if(vertical.getValue() > currentRow.getY()) {
//			vertical.setValue(currentRow.getY());
//		}
	}


	private void displayBox() {

	}

	public int getCurrentBox() {
		return currentBox;
	}

	public void setBox(Box next) {
		contentPane.setBox(next);
		contentPane.repaint();
	}

	public PC getPC() {
		return this.pc;
	}

}

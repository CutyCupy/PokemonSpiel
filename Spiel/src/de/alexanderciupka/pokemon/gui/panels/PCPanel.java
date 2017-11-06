package de.alexanderciupka.pokemon.gui.panels;

import javax.swing.JPanel;

import de.alexanderciupka.pokemon.characters.Box;
import de.alexanderciupka.pokemon.characters.PC;

public class PCPanel extends JPanel {

	private BoxPanel contentPane;

	private PC pc;

	private int currentBox;

	public BoxPanel getContentPane() {
		return contentPane;
	}

	public PCPanel() {
		setVisible(true);
		setBounds(0, 0, 630, 630);
	}

	public void setBox(int newBox) {
		this.currentBox = newBox >= 0 ? newBox % this.pc.getBoxes().length : (this.pc.getBoxes().length + newBox) %  this.pc.getBoxes().length;
		contentPane.setBox(pc.getBoxes()[currentBox]);
		contentPane.repaint();
	}

	public void setPC(PC pc) {
		this.pc = pc;

		contentPane = new BoxPanel(this);
		setBox(0);
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

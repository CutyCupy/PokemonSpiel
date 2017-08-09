package de.alexanderciupka.pokemon.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.alexanderciupka.pokemon.painting.PaintingController;

@SuppressWarnings("serial")
public class PaintingNameFrame extends JFrame {

	private JPanel contentPane;
	private JTextField nameField;
	private JButton startButton;
	private JButton loadButton;
	private JButton cancelButton;
	private JTextField width;
	private JTextField height;
	private Font font;
	
	private MenuController mController;
	private PaintingController pController;
	
	public PaintingNameFrame() {
		setTitle("Name des Bildes!");
		setResizable(false);
		setBounds(MenuController.getToCenter(300, 150));
		setVisible(true);
		mController = MenuController.getInstance();
		pController = PaintingController.getInstance();
		contentPane = new JPanel(null);
		setContentPane(contentPane);
		font = mController.getMenuFont().deriveFont(16F);
		addComponents();
	}
	
	private void addComponents() {
		nameField = new JTextField();
		nameField.setBounds(0, 0, 280, 35);
		nameField.setBackground(MainMenuFrame.BACKGROUND);
		nameField.setForeground(MainMenuFrame.FOREGROUND);
		nameField.setFont(font);
		width = new JTextField();
		width.setBounds(0, 40, 100, 35);
		width.setBackground(MainMenuFrame.BACKGROUND);
		width.setForeground(MainMenuFrame.FOREGROUND);
		width.setFont(font);
		JLabel xLabel = new JLabel("x");
		xLabel.setFont(font);
		xLabel.setBounds(100, 40, 80, 35);
		height = new JTextField();
		height.setBounds(180, 40, 100, 35);
		height.setBackground(MainMenuFrame.BACKGROUND);
		height.setForeground(MainMenuFrame.FOREGROUND);
		height.setFont(font);
		startButton = new JButton("OK");
		startButton.setForeground(MainMenuFrame.FOREGROUND);
		startButton.setBackground(MainMenuFrame.BACKGROUND);
		startButton.setBounds(0, 85, 81, 35);
		startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		startButton.setFont(font);
		loadButton = new JButton("Laden");
		loadButton.setForeground(MainMenuFrame.FOREGROUND);
		loadButton.setBackground(MainMenuFrame.BACKGROUND);
		loadButton.setBounds(100, 85, 81, 35);
		loadButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		loadButton.setFont(font);
		cancelButton = new JButton("Zurück");
		cancelButton.setForeground(MainMenuFrame.FOREGROUND);
		cancelButton.setBackground(MainMenuFrame.BACKGROUND);
		cancelButton.setBounds(200, 85, 81, 35);
		cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		cancelButton.setFont(font);
		
		nameField.setHorizontalAlignment(SwingConstants.CENTER);
		width.setHorizontalAlignment(SwingConstants.CENTER);
		height.setHorizontalAlignment(SwingConstants.CENTER);
		xLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		contentPane.add(nameField);
		contentPane.add(width);
		contentPane.add(xLabel);
		contentPane.add(height);
		contentPane.add(startButton);
		contentPane.add(loadButton);
		contentPane.add(cancelButton);
		addActionListeners();
	}
	
	private void addActionListeners() {
		nameField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				checkInput(((JTextField) e.getSource()).getText(), e.getKeyChar());
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				checkInput(((JTextField) e.getSource()).getText(), e.getKeyChar());
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				checkInput(((JTextField) e.getSource()).getText(), e.getKeyChar());
			}
		});
		width.addKeyListener(new KeyListener() {	
			@Override
			public void keyTyped(KeyEvent e) {
//				if(width.getText().length() > 0) {
//					widthCheck(e.getKeyChar());
//				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
					if(width.getText().length() > 0) {
						widthCheck(e.getKeyChar());
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
//				if(width.getText().length() > 0) {
//					widthCheck(e.getKeyChar());
//				}
			}
		});
		height.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
//				if(height.getText().length() > 0) {
//					heightCheck(e.getKeyChar());
//				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
					if(height.getText().length() > 0) {
						heightCheck(e.getKeyChar());
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
//				if(height.getText().length() > 0) {
//					heightCheck(e.getKeyChar());
//				}
			}
		});
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(width.getText().length() > 0 && width.getText().length() > 0) {
					if(checkName(nameField.getText())) {
						dispose();
						mController.openEditor(nameField.getText(), Integer.parseInt(width.getText()), Integer.parseInt(height.getText()));
					}
				}
			}
		});
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pController.findImage()) {
					dispose();
				}
			}
		});
	}
	
	private void widthCheck(char c) {
		if(isANumber(c)) {
			int value = Integer.parseInt(width.getText());
			if(value > (int) pController.getScreenResolution().getWidth()) {
				width.setText(String.valueOf((int) pController.getScreenResolution().getWidth()));
				return;
			}
			return;
		}
		width.setText(cutLastChar(width.getText()));
	}
	
	private void heightCheck(char c) {
		if(isANumber(c)) {
			int value = Integer.parseInt(height.getText());
			if(value > (int) pController.getScreenResolution().getHeight()) {
				height.setText(String.valueOf((int) pController.getScreenResolution().getHeight()));
				return;
			}
			return;
		}
		height.setText(cutLastChar(height.getText()));
	}
	
	private boolean checkName(String text) {
		if(text.length() > 0 && text.length() <= 16) {
			return true;
		}
		return false;
	}
	
	private void checkInput(String text, char inputChar) {
		if(!mController.checkName(text, inputChar)) {
			nameField.setText(cutLastChar(nameField.getText()));
		}
	}
	
	private String cutLastChar(String text) {
		return text.substring(0, text.length() - 1);
	}
	
	private boolean isANumber(char c) {
		try {
			Integer.parseInt(String.valueOf(c));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}

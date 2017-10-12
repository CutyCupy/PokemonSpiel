package de.alexanderciupka.pokemon.painting;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class OptionFrame extends JFrame {

	private JPanel contentPane;
	private JPanel colorPanel;
	private JTextField redValue;
	private JSlider redBar;
	private JTextField greenValue;
	private JSlider greenBar;
	private JTextField blueValue;
	private JSlider blueBar;
	private JTextField[] rgbValues;
	private JSlider[] rgbSlider;
	private JButton saveButton;
	private JButton floodButton;
	private JButton rubberButton;
	private JComboBox<Integer> circleSizeBox;
	private boolean rubberEnabled;
	private boolean fillEnabled;

	private PaintingController pController;


	public OptionFrame() {
		pController = PaintingController.getInstance();
		setResizable(false);
		setAlwaysOnTop(true);
		rgbValues = new JTextField[3];
		rgbSlider = new JSlider[3];
		setBounds(1000, 200, 200, 400);
		contentPane = new JPanel(null);
		setContentPane(contentPane);
		createComponents();
		repaint();
	}

	public void createComponents() {
		createColorComponents();
		createModeComponents();
		saveButton = new JButton();
		saveButton.setBounds(30, 300, 50, 50);
		saveButton.setIcon(new ImageIcon(getClass().getResource("/icons/save.png")));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				pController.loadBackup();
				pController.savePainting();
			}
		});
		floodButton = new JButton();
		floodButton.setBounds(120, 300, 50, 50);
		floodButton.setIcon(new ImageIcon(getClass().getResource("/icons/flood.png")));
		floodButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		floodButton.setBorderPainted(false);
		floodButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fillEnabled = !fillEnabled;
				pController.setFloodMode(fillEnabled);
				floodButton.setBorderPainted(fillEnabled);
			}
		});
		rubberButton = new JButton();
		rubberButton.setBounds(120, 235, 50, 50);
		rubberButton.setIcon(new ImageIcon(getClass().getResource("/icons/rubber.png")));
		rubberButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
		rubberButton.setBorderPainted(false);
		rubberButton.setBackground(Color.WHITE);
		rubberButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rubberEnabled = !rubberEnabled;
				if(rubberEnabled) {
					updateColorLabel(new Color(255, 255, 255));
					pController.setPaintingColor(colorPanel.getBackground());
					rubberButton.setBorderPainted(true);
				} else {
					updateColorLabel(Color.BLACK);
					pController.setPaintingColor(colorPanel.getBackground());
					rubberButton.setBorderPainted(false);
				}
			}
		});
		contentPane.add(floodButton);
		contentPane.add(saveButton);
		contentPane.add(rubberButton);
	}

	private void createModeComponents() {
		JLabel circleSizeBoxLabel = new JLabel("Größe");
		circleSizeBoxLabel.setBounds(35, 235, 40, 10);
		circleSizeBox = new JComboBox<Integer>();
		for(int i = 2; i <= 100; i += 2) {
			circleSizeBox.addItem(i);
		}
		circleSizeBox.setSelectedIndex(4);
		circleSizeBox.setBounds(30, 250, 50, 30);
		circleSizeBox.setEditable(true);
		circleSizeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int value = (int) circleSizeBox.getSelectedItem();
				if(value <= 0 || value > 100) {
					circleSizeBox.setSelectedIndex(4);
				}
			}
		});
		contentPane.add(circleSizeBoxLabel);
		contentPane.add(circleSizeBox);
	}

	private void createColorComponents() {
		colorPanel = new JPanel();
		colorPanel.setBackground(new Color(254, 254, 254));
		colorPanel.setBounds(80, 30, 40, 40);

		JLabel redPanel = new JLabel("Rot");
		redPanel.setBounds(30, 80, 30, 20);
		redValue = new JTextField("254");
		redValue.setBounds(140, 80, 30, 20);
		redBar = new JSlider(SwingConstants.HORIZONTAL, 0, 254, 254);
		redBar.setBounds(30, 100, 140, 25);
		rgbValues[0] = redValue;
		rgbSlider[0] = redBar;

		JLabel greenPanel = new JLabel("Grün");
		greenPanel.setBounds(30, 130, 30, 20);
		greenValue = new JTextField("254");
		greenValue.setBounds(140, 130, 30, 20);
		greenBar = new JSlider(SwingConstants.HORIZONTAL, 0, 254, 254);
		greenBar.setBounds(30, 150, 140, 25);
		rgbValues[1] = greenValue;
		rgbSlider[1] = greenBar;

		JLabel bluePanel = new JLabel("Blau");
		bluePanel.setBounds(30, 180, 30, 20);
		blueValue = new JTextField("254");
		blueValue.setBounds(140, 180, 30, 20);
		blueBar = new JSlider(SwingConstants.HORIZONTAL, 0, 254, 254);
		blueBar.setBounds(30, 200, 140, 25);
		rgbValues[2] = blueValue;
		rgbSlider[2] = blueBar;

		contentPane.add(colorPanel);
		contentPane.add(redPanel);
		contentPane.add(redValue);
		contentPane.add(redBar);
		contentPane.add(greenPanel);
		contentPane.add(greenValue);
		contentPane.add(greenBar);
		contentPane.add(bluePanel);
		contentPane.add(blueValue);
		contentPane.add(blueBar);
		addActionListeners();
	}

	private void addActionListeners() {
		redValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int value = Integer.parseInt(((JTextField) e.getSource()).getText());
				valueActionListener((JTextField) e.getSource(), value, 0);
			}
		});
		greenValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int value = Integer.parseInt(((JTextField) e.getSource()).getText());
				valueActionListener((JTextField) e.getSource(), value, 1);
			}
		});
		blueValue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int value = Integer.parseInt(((JTextField) e.getSource()).getText());
				valueActionListener((JTextField) e.getSource(), value, 2);
			}
		});
		redBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderActionListener(redValue, redBar.getValue());
			}
		});
		greenBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderActionListener(greenValue, greenBar.getValue());
			}
		});
		blueBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderActionListener(blueValue, blueBar.getValue());
			}
		});
	}

	public void valueActionListener(JTextField textField, int value, int index) {
		if(value > 0 && value < 256) {
			rgbSlider[index].setValue(value);
		} else {
			textField.setText(String.valueOf(rgbSlider[index].getValue()));
		}
	}

	public void sliderActionListener(JTextField textField, int value) {
		colorPanel.setBackground(new Color(redBar.getValue(), greenBar.getValue(), blueBar.getValue()));
		textField.setText(String.valueOf(value));
		pController.setPaintingColor(colorPanel.getBackground());
	}

	public void start() {
		setVisible(true);
		repaint();
	}

	public void updateColorLabel(Color c) {
		colorPanel.setBackground(c);
		redBar.setValue(c.getRed());
		redValue.setText(String.valueOf(c.getRed()));
		greenBar.setValue(c.getGreen());
		greenValue.setText(String.valueOf(c.getGreen()));
		blueBar.setValue(c.getBlue());
		blueValue.setText(String.valueOf(c.getBlue()));
		repaint();
	}

	public int getCircleWidth() {
		return (int) circleSizeBox.getSelectedItem();
	}
}

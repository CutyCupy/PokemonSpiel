package de.alexanderciupka.pokemon.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class MainMenuFrame extends JFrame {

	
	private JPanel contentPane;
	private JButton gameStart;
	private JButton newGame;
	private JButton editor;
	private JButton options;
	private Image pokeball;
	private Image superball;
	private Image hyperball;
	private Image masterball;
	private MenuController mController;
	private Font font;
	
	public static final Color FOREGROUND = Color.YELLOW;
	public static final Color BACKGROUND = Color.BLUE.darker().darker();
	
	public MainMenuFrame() {
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(MenuController.getToCenter(1360, 720));
		setTitle("Men�");
		contentPane = new JPanel(null);
		setContentPane(contentPane);
		mController = MenuController.getInstance();
		font = mController.getMenuFont().deriveFont(40F);
		createButtons();
		createBalls();
		createDesign();
	}
	
	public void createButtons() {
		gameStart = new JButton("Spiel fortsetzen");
		gameStart.setForeground(FOREGROUND);
		gameStart.setBackground(BACKGROUND);
		gameStart.setBounds(430, 200, 500, 75);
		gameStart.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		gameStart.setFont(font);
		newGame = new JButton("Neues Spiel");
		newGame.setForeground(FOREGROUND);
		newGame.setBackground(BACKGROUND);
		newGame.setBounds(430, 300, 500, 75);
		newGame.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		newGame.setFont(font);
		editor = new JButton("Editor");
		editor.setForeground(FOREGROUND);
		editor.setBackground(BACKGROUND);
		editor.setBounds(430, 400, 500, 75);
		editor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		editor.setFont(font);
		editor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mController.startEditor();
			}
		});
		options = new JButton("Optionen");
		options.setForeground(FOREGROUND);
		options.setBackground(BACKGROUND);
		options.setBounds(430, 500, 500, 75);
		options.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		options.setFont(font);
		
		gameStart.setVerticalAlignment(SwingConstants.CENTER);
		gameStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(mController.loadGame()) {
					setVisible(false);
				}
			}
		});
		newGame.setVerticalAlignment(SwingConstants.CENTER);
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				mController.startNewGame();
			}
		});
		editor.setVerticalAlignment(SwingConstants.CENTER);
		options.setVerticalAlignment(SwingConstants.CENTER);
		contentPane.add(gameStart);
		contentPane.add(newGame);
		contentPane.add(editor);
		contentPane.add(options);
	}
	
	public void createBalls() {
		pokeball = new ImageIcon(getClass().getResource("/pokeballs/pokeball.png")).getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		superball = new ImageIcon(getClass().getResource("/pokeballs/superball.png")).getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		hyperball = new ImageIcon(getClass().getResource("/pokeballs/hyperball.png")).getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		masterball = new ImageIcon(getClass().getResource("/pokeballs/masterball.png")).getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
		JLabel pokeLabel = new JLabel(new ImageIcon(pokeball));
		JLabel pokeLabel2 = new JLabel(new ImageIcon(pokeball));
		JLabel superLabel = new JLabel(new ImageIcon(superball));
		JLabel superLabel2 = new JLabel(new ImageIcon(superball));
		JLabel hyperLabel = new JLabel(new ImageIcon(hyperball));
		JLabel hyperLabel2 = new JLabel(new ImageIcon(hyperball));
		JLabel masterLabel = new JLabel(new ImageIcon(masterball));
		JLabel masterLabel2 = new JLabel(new ImageIcon(masterball));
		
		pokeLabel.setBounds(350, 200, 75, 75);
		pokeLabel2.setBounds(935, 200, 75, 75);
		superLabel.setBounds(350, 300, 75, 75);
		superLabel2.setBounds(935, 300, 75, 75);
		hyperLabel.setBounds(350, 400, 75, 75);
		hyperLabel2.setBounds(935, 400, 75, 75);
		masterLabel.setBounds(350, 500, 75, 75);
		masterLabel2.setBounds(935, 500, 75, 75);
		
		contentPane.add(pokeLabel);
		contentPane.add(pokeLabel2);
		contentPane.add(superLabel);
		contentPane.add(superLabel2);
		contentPane.add(hyperLabel);
		contentPane.add(hyperLabel2);
		contentPane.add(masterLabel);
		contentPane.add(masterLabel2);
	}
	
	public void createDesign() {
//		Image sarah = new ImageIcon(getClass().getResource("/avatars/character_sarah.png")).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
//		JLabel sarahLabel = new JLabel(new ImageIcon(sarah));
//		sarahLabel.setBounds(100, 450, 250, 250);
//		
//		Image alex = new ImageIcon(getClass().getResource("/avatars/character_alex.png")).getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
//		JLabel alexLabel = new JLabel(new ImageIcon(alex));
//		alexLabel.setBounds(1010, 450, 250, 250);
//		
//		contentPane.add(alexLabel);
//		contentPane.add(sarahLabel);
		repaint();
	}
}

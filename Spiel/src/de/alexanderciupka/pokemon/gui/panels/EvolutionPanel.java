package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.painting.Painting;
import de.alexanderciupka.pokemon.pokemon.Pokemon;

public class EvolutionPanel extends JPanel {

	private Queue<Pokemon> pokemon;

	private JLabel pokemonLabel;

	private GameController gController;

	public EvolutionPanel() {

		gController = GameController.getInstance();

		setBounds(100, 100, 630, 630);
		setBackground(Color.black);
		setLayout(null);

		pokemon = new LinkedList<>();

		pokemonLabel = new JLabel();
		pokemonLabel.setBounds(235, 235, 160, 160);
		add(pokemonLabel);
	}

	public void addPokemon(Pokemon p) {
		pokemon.offer(p);
	}

	public Queue<Pokemon> getPokemon() {
		return this.pokemon;
	}

	public void start() {
		gController.setInteractionPause(true);
		gController.getGameFrame().repaint();
		while (!pokemon.isEmpty()) {
			Pokemon currentPokemon = pokemon.poll();

			BufferedImage preEvolution = Painting.toBufferedImage(currentPokemon.getSpriteFront());
			BufferedImage postEvolution = Painting.toBufferedImage(gController.getInformation()
					.getFrontSprite(currentPokemon.getEvolves(), currentPokemon.getGender(), currentPokemon.isShiny()));

			pokemonLabel.setIcon(new ImageIcon(preEvolution));

			gController.getGameFrame().addDialogue("Nanu? " + currentPokemon.getName() + " entwickelt sich!");
			gController.waitDialogue();
			SoundController.getInstance().playSound(SoundController.EVOLUTION_START);

			BufferedImage whitePre = makeWhite(preEvolution);
			BufferedImage whitePost = makeWhite(postEvolution);

			ArrayList<ImageIcon> icons = new ArrayList<ImageIcon>();
			for (int sizeChange = 0; sizeChange < whitePre.getHeight(); sizeChange += 2) {
				if (sizeChange < whitePre.getHeight() / 2) {
					icons.add(new ImageIcon(whitePre.getScaledInstance(whitePre.getWidth() - sizeChange,
							whitePre.getHeight() - sizeChange, Image.SCALE_AREA_AVERAGING)));
				} else if (sizeChange >= whitePre.getHeight() / 2) {
					icons.add(new ImageIcon(whitePost.getScaledInstance(sizeChange % whitePost.getHeight(),
							sizeChange % whitePost.getHeight(), Image.SCALE_AREA_AVERAGING)));
				}
			}

			for (int rotations = 0; rotations < 10; rotations++) {
				int index = 0;
				for (int sizeChange = 0; sizeChange < whitePre.getHeight(); sizeChange += 2, index++) {
					if (sizeChange < whitePre.getHeight() / 2) {
						pokemonLabel.setIcon(icons.get(index));
						pokemonLabel.setLocation(pokemonLabel.getX() + 1, pokemonLabel.getY());
					} else if (sizeChange >= whitePre.getHeight() / 2) {
						pokemonLabel.setIcon(icons.get(index));
						pokemonLabel.setLocation(pokemonLabel.getX() - 1, pokemonLabel.getY());
					}
					try {
						Thread.sleep(16 - (rotations * 2) > 0 ? 16 - (rotations * 2) : 0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				pokemonLabel.setLocation(235, 235);
			}
			pokemonLabel.setIcon(new ImageIcon(postEvolution));
			SoundController.getInstance().playBattlecry(currentPokemon.getEvolves());
			gController.getGameFrame().addDialogue("Glückwunsch? Dein " + currentPokemon.getName() + " hat sich zu "
					+ gController.getInformation().getName(currentPokemon.getEvolves()) + " entwickelt!");
			gController.waitDialogue();
			currentPokemon.startEvolution();
			gController.waitDialogue();
		}
		gController.setInteractionPause(false);
		gController.getGameFrame().setCurrentPanel(gController.getGameFrame().getLastPanel(false));
		gController.getGameFrame().getPokemonPanel().update();
	}

	private BufferedImage makeWhite(BufferedImage original) {
		BufferedImage white = new BufferedImage(original.getWidth(null), original.getHeight(null),
				BufferedImage.TYPE_4BYTE_ABGR);
		for (int x = 0; x < original.getWidth(); x++) {
			for (int y = 0; y < original.getHeight(); y++) {
				Color current = new Color(original.getRGB(x, y));
				if ((current.getRed() != 0 || current.getBlue() != 0 || current.getGreen() != 0)
						&& (current.getRed() != 255 || current.getBlue() != 255 || current.getGreen() != 255)) {
					white.setRGB(x, y, new Color(255, 255, 255).getRGB());
				} else {
					white.setRGB(x, y, original.getRGB(x, y));
				}
			}
		}
		return white;
	}

}

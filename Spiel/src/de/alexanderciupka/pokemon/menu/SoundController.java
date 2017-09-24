package de.alexanderciupka.pokemon.menu;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.gui.overlay.RainType;
import de.alexanderciupka.pokemon.map.GameController;

public class SoundController {

	private Clip currentSong;
	private Clip rain;

	private static SoundController instance;

	public static final String GET_ITEM = "item_get"; //DONE
	public static final String EVOLUTION_START = "evolution"; //DONE
	public static final String LEVEL_UP = "levelup"; //DONE
	public static final String POKECENTER_HEAL = "pokecenter_heal"; //DONE
	public static final String POKEMON_CAUGHT = "pokemon_caught"; //DONE
	public static final String CUT = "hm_cut"; //DONE
	public static final String NORMAL_EFFECTIVE = "effective_normal";
	public static final String NOT_EFFECTIVE = "effective_not";
	public static final String SUPER_EFFECTIVE = "effective_super";
	public static final String ITEM_HEAL = "item_heal"; //DONE
	public static final String MONEY = "money";
	public static final String PC_BOOT = "pc_boot"; //DONE
	public static final String PC_SHUTDOWN = "pc_shutdown"; //DONE
	public static final String POKEBALL_DROP = "pokeball_drop"; //DONE
	public static final String POKEBALL_OUT = "pokeball_out"; //DONE
	public static final String POKEBALL_CATCHING = "pokeball_catching";
	public static final String POKEMON_LOW = "pokemon_low";
	public static final String ROCKSMASH = "hm_rocksmash"; //DONE
	public static final String ESCAPE = "escape_fight"; //DONE
	public static final String SAVE = "save_game"; //DONE
	public static final String BUMP = "bump"; //DONE
	public static final String THUNDER = "thunder"; //DONE



	private SoundController() {
	}

	public static SoundController getInstance() {
		if (instance == null) {
			instance = new SoundController();
		}
		return instance;
	}

	public void playSong(String songName) {
		playSong(new File(Main.class.getResource("/music/songs/" + songName + ".wav").getFile()));
	}

	public void playSong(File song) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song);
			if (currentSong != null) {
				currentSong.stop();
			}
			currentSong = AudioSystem.getClip();
			currentSong.open(audioInputStream);
			currentSong.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void stopSong() {
		if (currentSong != null) {
			currentSong.stop();
		}
		currentSong = null;
	}

	public boolean isSongRunning() {
		return currentSong == null ? false : currentSong.getMicrosecondLength() > currentSong.getMicrosecondPosition();
	}

	public void playBattlecry(int id) {
		playBattlecry(id, true);
	}

	public void playBattlecry(int id, boolean b) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(Main.class
					.getResource("/music/battlecries/" + (id < 10 ? "00" + id : (id < 100) ? "0" + id : id) + ".wav")
					.getFile()));
			Clip bc = AudioSystem.getClip();
			bc.open(audioInputStream);
			((FloatControl) bc.getControl(FloatControl.Type.MASTER_GAIN)).setValue(-40.0f);
			bc.start();
			while (b && bc.getMicrosecondLength() > bc.getMicrosecondPosition()) {
				Thread.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playSound(String name) {
		playSound(name, false);
	}

	public void playSound(String name, boolean b) {
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(Main.class.getResource("/music/sounds/" + name + ".wav").getFile()));
			Clip sound = AudioSystem.getClip();
			sound.open(audioInputStream);
			sound.start();
			while (b && (sound.getMicrosecondLength() > sound.getMicrosecondPosition())) {
				Thread.sleep(5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void playBattleSong(String characterName) {
		final long clipTime = this.currentSong != null ? this.currentSong.getMicrosecondPosition() : -1;
		if(this.currentSong != null) {
			this.currentSong.stop();
		}
		String fightTheme = null;
		if(characterName == null) {
			fightTheme = "wild_encounter";
		} else if(characterName.toLowerCase().contains("arenaleiter")) {
			fightTheme = "trainer_fight";
		} else if(characterName.toLowerCase().contains("team marco")) {
			fightTheme = "team_marco_fight";
		} else if(characterName.toLowerCase().contains("cutycupy")) {
			fightTheme = "rival_fight";
		} else if(characterName.toLowerCase().contains("kek(s)vorstand")) {
			fightTheme = "jan_fight";
		} else if(characterName.toLowerCase().contains("top vier")) {
			fightTheme = "elite_four_fight";
		} else {
			fightTheme = "trainer_fight";
		}
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(Main.class.getResource("/music/fight_songs/" + fightTheme + ".wav").getFile()));
			Clip sound = AudioSystem.getClip();
			sound.open(audioInputStream);
			sound.loop(Clip.LOOP_CONTINUOUSLY);
			sound.start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (GameController.getInstance().isFighting()) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sound.stop();
					if(clipTime != -1) {
						currentSong.setMicrosecondPosition(clipTime);
						FloatControl f = (FloatControl) currentSong.getControl(FloatControl.Type.MASTER_GAIN);
						f.setValue(-40.0f);
						currentSong.start();
						for(int i = 0; i < 40; i++) {
							f.setValue(1.0f);
							try {
								Thread.sleep(25);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startRain(RainType type) {
		if(rain != null) {
			rain.stop();
		}
		try {
			rain = AudioSystem.getClip();
			switch(type) {
			case NIZZLE:
				rain.open(AudioSystem.getAudioInputStream(
						new File(Main.class.getResource("/music/sounds/nizzle.wav").getFile())));
				break;
			case HEAVY:
			case STORM:
				rain.open(AudioSystem.getAudioInputStream(
						new File(Main.class.getResource("/music/sounds/heavy_rain.wav").getFile())));
				break;
			}
			rain.loop(Clip.LOOP_CONTINUOUSLY);
			rain.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRain() {
		if(rain != null) {
			rain.stop();
			rain = null;
		}
	}
}
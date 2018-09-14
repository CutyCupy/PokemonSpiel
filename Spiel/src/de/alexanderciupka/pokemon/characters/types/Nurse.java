package de.alexanderciupka.pokemon.characters.types;

import de.alexanderciupka.pokemon.menu.SoundController;

public class Nurse extends NPC {
	
	public Nurse() {
		super();
	}
	
	@Override
	public boolean isAggro() {
		return false;
	}
	
	@Override
	public boolean isTrainer() {
		return false;
	}
	
	@Override
	public void onInteraction(Player p) {
		super.onInteraction(p);
		p.getTeam().restoreTeam();
//		if (this.getCurrentRoute().getId().equals("pokemon_center")) {
		boolean searching = true;
		for(int x = 0; x < this.getCurrentRoute().getWidth() && searching; x++) {
			for(int y = 0; y < this.getCurrentRoute().getHeight() && searching; y++) {
				if(this.getCurrentRoute().getEntity(x, y) != null && this.getCurrentRoute().getEntity(x, y).getSpriteName().contains("joyhealing")) {
					searching = false;
					for (int i = 1; i <= p.getTeam().getAmmount() + 1 ; i++) {
						this.gController.getCurrentBackground().getCurrentRoute().getEntity(x, y)
						.setSprite("joyhealing" + (i % (p.getTeam().getAmmount() + 1)));
						if (i == p.getTeam().getAmmount()) {
							SoundController.getInstance().playSound(SoundController.POKECENTER_HEAL);
							this.gController.sleep(1500);
						} else {
							this.gController.sleep(750);
						}
					}
				}
			}
		}
		this.gController.getGameFrame().addDialogue("Deine Pokemon sind nun wieder topfit!");
		this.gController.waitDialogue();
	}
}

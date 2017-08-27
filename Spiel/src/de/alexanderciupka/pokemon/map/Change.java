package de.alexanderciupka.pokemon.map;

import java.awt.Point;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.characters.Direction;
import de.alexanderciupka.pokemon.characters.Player;

public class Change {
	
	private GameController gController;
	
	private String participant;
	private String routeID;
	private Direction direction;
	private Point move;
	private String dialog;
	private boolean positionUpdate;
	private boolean fight;
	private String beforeFightUpdate;
	private String noFightUpdate;
	private String afterFightUpdate;
	private String spriteUpdate;
	private boolean heal;
	
	private Character character;
	private Route route;
	private Direction[] path;
	
	
	
	public Change() {
		this.gController = GameController.getInstance();
	}
	
	public void initiate(Player source) {
		this.route = this.gController.getRouteAnalyzer().getRouteById(routeID);
		this.route = this.route == null ? source.getCurrentRoute() : this.route;
		this.character = this.route.getNPCByName(participant);
		this.character = this.character == null ? source : this.character;
		this.path = this.character.moveTowards(this.move.x == -1 ? this.character.getCurrentPosition().x : this.move.x, 
				this.move.y == -1 ? this.character.getCurrentPosition().y : this.move.y);
	}
	
	
	public Character getCharacter() {
		return this.character;
	}
	public Route getRoute() {
		return this.route;
	}
	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	public String getRouteID() {
		return routeID;
	}
	public void setRouteID(String route) {
		this.routeID = route;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public Point getMove() {
		return move;
	}
	public Direction[] getPath() {
		return path;
	}

	public void setMove(Point move) {
		this.move = move;
	}
	public String getDialog() {
		return dialog;
	}
	public void setDialog(String dialog) {
		this.dialog = dialog;
	}
	public boolean isPositionUpdate() {
		return positionUpdate;
	}
	public void setPositionUpdate(boolean positionUpdate) {
		this.positionUpdate = positionUpdate;
	}
	public boolean isFight() {
		return fight;
	}
	public void setFight(boolean fight) {
		this.fight = fight;
	}
	public String getBeforeFightUpdate() {
		return beforeFightUpdate;
	}
	public void setBeforeFightUpdate(String beforeFightUpdate) {
		this.beforeFightUpdate = beforeFightUpdate;
	}
	public String getNoFightUpdate() {
		return noFightUpdate;
	}
	public void setNoFightUpdate(String noFightUpdate) {
		this.noFightUpdate = noFightUpdate;
	}
	public String getAfterFightUpdate() {
		return afterFightUpdate;
	}
	public void setAfterFightUpdate(String afterFightUpdate) {
		this.afterFightUpdate = afterFightUpdate;
	}
	public String getSpriteUpdate() {
		return spriteUpdate;
	}
	public void setSpriteUpdate(String spriteUpdate) {
		this.spriteUpdate = spriteUpdate;
	}

	public boolean isHeal() {
		return heal;
	}

	public void setHeal(boolean heal) {
		this.heal = heal;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Change) {
			Change other = (Change) obj;
			return (this.participant.equals(other.participant) && this.routeID.equals(other.routeID) && this.move.equals(other.move) &&
					((this.direction == null && other.direction == null) || (this.direction != null && this.direction.equals(other.direction))) &&
					((this.dialog == null && other.dialog == null) || (this.dialog != null && this.dialog.equals(other.dialog))) &&
					this.positionUpdate == other.positionUpdate && this.fight == other.fight && this.heal == other.heal &&
					((this.beforeFightUpdate == null && other.beforeFightUpdate == null) || (this.beforeFightUpdate != null && this.beforeFightUpdate.equals(other.beforeFightUpdate))) &&
					((this.noFightUpdate == null && other.noFightUpdate == null) || (this.noFightUpdate != null && this.noFightUpdate.equals(other.noFightUpdate))) &&
					((this.afterFightUpdate == null && other.afterFightUpdate == null) || (this.afterFightUpdate != null && this.afterFightUpdate.equals(other.afterFightUpdate))) &&
					((this.spriteUpdate == null && other.spriteUpdate == null) || (this.spriteUpdate != null && this.spriteUpdate.equals(other.spriteUpdate))));
		}
		return false;
	}

	
	@Override
	protected Change clone() {
		Change clone = new Change();
		clone.setAfterFightUpdate(this.afterFightUpdate);
		clone.setBeforeFightUpdate(this.beforeFightUpdate);
		clone.setNoFightUpdate(this.noFightUpdate);
		clone.setDialog(this.dialog);
		clone.setDirection(this.direction);
		clone.setFight(this.fight);
		clone.setHeal(this.heal);
		clone.setMove(new Point(this.move));
		clone.setParticipant(this.participant);
		clone.setPositionUpdate(this.positionUpdate);
		clone.setRouteID(this.routeID);
		clone.setSpriteUpdate(this.spriteUpdate);
		return clone;
	}
}

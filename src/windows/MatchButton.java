package windows;

import javax.swing.JButton;
//Represents a button for a match edit on the panel
public class MatchButton extends JButton{
	private final int match;
	//Makes a new button with label 'Edit' and sets match to given match num
	public MatchButton(int match){
		super("Edit");
		this.match = match;
	}
	//returns which match this button represents
	public int getMatch(){
		return match;
	}
}

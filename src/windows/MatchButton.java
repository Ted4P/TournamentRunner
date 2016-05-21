package windows;

import javax.swing.JButton;

public class MatchButton extends JButton{
	private final int match;
	public MatchButton(int match){
		super("Edit");
		this.match = match;
	}
	public int getMatch(){
		return match;
	}
}

import java.awt.Container;
import java.awt.Panel;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;


public class TournamentView extends JFrame implements Observer{
	
	public TournamentView(TournamentModel model){		//Start popup with 
		Container pane = getContentPane();
		model.addObserver(this);
		
		
		//GUI FILLER
		pane.add(new JLabel("HELLO WORLD"));
		setTitle("Key printer");
		setSize(700,400);
		setVisible(true);
	}


	public void update(Observable arg0, Object arg1) {
		//UPDATE BRACKETS
	}
	
	public static void main(String[] args){
		TournamentModel model = new TournamentModel();
		TournamentView view = new TournamentView(model);
	}
}

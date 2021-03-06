package windows;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.awt.Color;
import tournamentRunner.*;
import javax.swing.JPanel;

/*
 * Acts as an extension of a panel which, given a TournamentView to reference and a bracket, it will display
 * that bracket within the panel.
 * 5.30.16
 * Ben Kelly and Ted Pyne
 */
public class BracketPanel extends JPanel implements ActionListener{
	private Bracket bracket;
	private String[][] info;
	private static int MATCH_WIDTH = 200, MATCH_HEIGHT = MATCH_WIDTH/5;
	private final static int MAX_WIDTH = 1000, MIN_WIDTH = 50;
	private static double FACTOR = 1.25;
	private MatchButton[] editButtons;
	private TournamentView view;
	private int level,shift,totShift;
	//Creates a new BracketPanel for a given bracket connected to view
	public BracketPanel(Bracket bracket, TournamentView view){
		this.view = view;
		this.bracket = bracket;
		info = new String[Brackets.getSize()][8];
		setLayout(null);
		String bracketInfo = bracket.getInfo();
		Scanner scan = new Scanner(bracketInfo);
		for(int i=0;i<Brackets.getSize() && scan.hasNext();i++){
			String line = scan.nextLine();
			String[] rawData = line.split(",");
			for(int index=0;index<3;index++){
				info[i][2*index] = rawData[index].substring(0, rawData[index].indexOf('/'));
				info[i][2*index+1] = rawData[index].substring(rawData[index].indexOf('/')+1);
			}
			info[i][6] = rawData[3];
			info[i][7] = rawData[4];
		}
		scan.close();
		setFocusable(true);
		editButtons = new MatchButton[Brackets.getSize()+1];
		level = 0;
		int num = 1;
		for(;num < Brackets.getSize();level++) num*=2;
		level--;
		shift = (int)(Math.pow(2, level)*(MATCH_WIDTH*6.0/100));
		totShift = (int)(2 * shift * (1-Math.pow(0.5, level)));
	}
	/*@Override
	 *Paints this panel with the correct information for the stored bracket*/
	public void paintComponent(Graphics g){
		removeAll();
		if(g instanceof Graphics2D)
			g = (Graphics2D)g;
		super.paintComponent(g);
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD,MATCH_HEIGHT/3));
		setPreferredSize(new Dimension(2*2*MATCH_WIDTH*level+3*MATCH_WIDTH,(totShift+MATCH_HEIGHT)*2));
		int startingX = 2*MATCH_WIDTH*level+(int)(1.5*MATCH_WIDTH);
		int startingY = totShift-MATCH_HEIGHT/2+MATCH_HEIGHT;
		g.drawRect(startingX, startingY-MATCH_HEIGHT*2, MATCH_WIDTH, MATCH_HEIGHT);
		g.drawLine(startingX,startingY-3*MATCH_HEIGHT/2,startingX+MATCH_WIDTH,startingY-3*MATCH_HEIGHT/2);
		g.drawLine(startingX + MATCH_WIDTH/2,startingY-MATCH_HEIGHT,startingX+MATCH_WIDTH/2,startingY+MATCH_HEIGHT/2);
		g.drawLine(startingX-3*MATCH_WIDTH/4,startingY+MATCH_HEIGHT/2,startingX+7*MATCH_WIDTH/4,startingY+MATCH_HEIGHT/2);
		MatchButton finalButton = new MatchButton(1);
		finalButton.setBounds(startingX+MATCH_WIDTH/3, startingY-MATCH_HEIGHT, MATCH_WIDTH/3, 3*MATCH_HEIGHT/5);
		finalButton.setFont(new Font(finalButton.getFont().getFontName(),Font.PLAIN,(int)(1.1*MATCH_HEIGHT/3)));
		finalButton.addActionListener(this);
		add(finalButton);
		editButtons[1] = finalButton;
		int index = getIndex(1);
		for(int i=0;i<2;i++){
			String winner = info[index][4]+info[index][5];
			String player = info[index][2*i]+info[index][2*i+1];
			if(winner.equals(Match.DEFAULT_WINNER_NAME + Match.DEFAULT_WINNER_SCHOOL))
				g.setColor(Color.BLACK);
			else if(winner.equals(player)){
				g.setColor(Color.GREEN);
			}
			else
				g.setColor(Color.RED);
			int maxLength = 8;
			if(info[index][2*i].length() < maxLength)
				maxLength = info[index][2*i].length();
			g.drawString(info[index][2*i].substring(0, maxLength), startingX+5, startingY+(5*i+4)*MATCH_HEIGHT/10-2*MATCH_HEIGHT);
			maxLength = 8;
			if(info[index][2*i+1].length() < maxLength)
				maxLength = info[index][2*i+1].length();
			if(!info[index][2*i+1].equals("NA"))
				g.drawString(info[index][2*i+1].substring(0, maxLength), startingX+MATCH_WIDTH/2, startingY+(5*i+4)*MATCH_HEIGHT/10-2*MATCH_HEIGHT);
			g.setColor(Color.BLACK);
			paintOneMatch(startingX-2*MATCH_WIDTH,startingY, shift, 0, 2, 1, 2, g);
			paintOneMatch(startingX+2*MATCH_WIDTH,startingY, shift, 0, 3, -1, 1, g);
		}
	}
	//Recursive method designed to paint a single match in the bracket, then to call its children (if they exist) to be painted
	private void paintOneMatch(int x, int y, int shift, int level, int match, int direction, int seed, Graphics g){
		g.setFont(new Font(g.getFont().getFontName(),Font.BOLD,MATCH_HEIGHT/3));
		int index = getIndex(match);
		int newX = x;
		for(int i=0;i<2;i++){
			g.drawRect(x, y, MATCH_WIDTH, MATCH_HEIGHT);
			String winner = info[index][4]+info[index][5];
			String player = info[index][2*i]+info[index][2*i+1];
			if(winner.equals(Match.DEFAULT_WINNER_NAME + Match.DEFAULT_WINNER_SCHOOL))
				g.setColor(Color.BLACK);
			else if(winner.equals(player)){
				g.setColor(Color.GREEN);
			}
			else
				g.setColor(Color.RED);
			g.drawString(info[index][2*i], x+5, y+(5*i+4)*MATCH_HEIGHT/10);
			if(!info[index][2*i+1].equals("NA"))
				g.drawString(info[index][2*i+1], x+MATCH_WIDTH/2, y+(5*i+4)*MATCH_HEIGHT/10);
			g.setColor(Color.BLACK);
			if(isFinalMatch(match)){
				g.drawString("" + Math.abs((seed-i*(Brackets.getSize()+1))), x+7*MATCH_WIDTH/8, y+(5*i+4)*MATCH_HEIGHT/10);
				g.drawLine(x+(int)(6.8*MATCH_WIDTH/8), y, x+(int)(6.8*MATCH_WIDTH/8), y+MATCH_HEIGHT);
			}
		}
		MatchButton button = new MatchButton(match);
		newX = x + MATCH_WIDTH/2*(direction+1);
		if(direction == -1)
			newX -= (int)(1.1*MATCH_WIDTH/3);
		button.setBounds(newX, y + MATCH_HEIGHT/5, (int)(1.1*MATCH_WIDTH/3),3*MATCH_HEIGHT/5);
		button.setFont(new Font(button.getFont().getFontName(), Font.PLAIN, MATCH_HEIGHT/3));
		button.addActionListener(this);
		add(button);
		editButtons[match] = button;
		if(direction == -1)
			newX = x + MATCH_WIDTH;
		else
			newX = x;
		if(!isFinalMatch(match)){
			g.drawLine(newX-direction*MATCH_HEIGHT/4,y+MATCH_HEIGHT/2,newX-(int)(direction*2.3*MATCH_WIDTH/6),y+MATCH_HEIGHT/2-shift);
			g.drawLine(newX-direction*MATCH_HEIGHT/4,y+MATCH_HEIGHT/2,newX-(int)(direction*2.3*MATCH_WIDTH/6),y+MATCH_HEIGHT/2+shift);
			g.drawLine(newX-direction*MATCH_HEIGHT/4,y+MATCH_HEIGHT/2,newX+direction*MATCH_WIDTH,y+MATCH_HEIGHT/2);
			paintOneMatch(x-direction*7*MATCH_WIDTH/4,y+shift,shift/2,level+1,match*2,direction,(int)(Math.pow(2, level+2)+1-seed),g);
			paintOneMatch(x-direction*7*MATCH_WIDTH/4,y-shift,shift/2,level+1,match*2+1,direction,seed,g);
		}
		else
			g.drawLine(newX,y+MATCH_HEIGHT/2,newX+direction*5*MATCH_WIDTH/4,y+MATCH_HEIGHT/2);
	}
	//Returns if this match is a final match in the bracket or if its children both contain BYEs
	private boolean isFinalMatch(int match){
		return match*2+1 > Brackets.getSize()
				|| (info[getIndex(match)][0].equals("BYE") && info[getIndex(match)][2].equals("BYE"))
				|| ((info[getIndex(match*2)][0].equals("BYE") && info[getIndex(match*2)][2].equals("BYE"))
				|| (info[getIndex(match*2+1)][0].equals("BYE") && info[getIndex(match*2+1)][2].equals("BYE")));
	}
	//Zooms in the panel by FACTOR
	public static void zoomIn(){
		if(MATCH_WIDTH*FACTOR <= MAX_WIDTH)
			MATCH_WIDTH *= FACTOR;
		else
			MATCH_WIDTH = MAX_WIDTH;
		MATCH_HEIGHT = MATCH_WIDTH/5;
	}
	//Zooms out the panel by FACTOR
	public static void zoomOut(){
		if(MATCH_WIDTH/FACTOR >= MIN_WIDTH)
			MATCH_WIDTH /= FACTOR;
		else
			MATCH_WIDTH = MIN_WIDTH;
		MATCH_HEIGHT = MATCH_WIDTH/5;
	}
	//Sets the match width a certain width
	public static void setMatchWidth(int width){
		if(width > MAX_WIDTH)
			MATCH_WIDTH = MAX_WIDTH;
		else if(width < MIN_WIDTH)
			MATCH_WIDTH = MIN_WIDTH;
		else
			MATCH_WIDTH = width;
		MATCH_HEIGHT = MATCH_WIDTH/5;
	}
	//Returns the number of levels
	public int getLevels(){
		return level;
	}
	//Returns the total shift in the y-axis
	public int getTotShift(){
		return totShift;
	}
	//Returns the width of each match
	public static int getMatchWidth(){
		return MATCH_WIDTH;
	}
	//Returns the height of each match
	public static int getMatchHeight(){
		return MATCH_HEIGHT;
	}
	//Sets the zoom factor
	public static void setFactor(double newFactor){
		FACTOR = newFactor;
	}
	//Given a match number, finds and returns the index where that match is in the information array
	private int getIndex(int match){
		int index = 0;
		while(index < Brackets.getSize() && !info[index][7].equals("" + match))
			index++;
		if(index == Brackets.getSize())
			throw new NoSuchElementException();
		return index;
	}
	@Override
	//If an edit button is pressed, finds which match it corresponds to and makes a new window for the editing
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() instanceof MatchButton){
			MatchButton match = (MatchButton)arg0.getSource();
			new MatchEditWindow(view, bracket, info[getIndex(match.getMatch())],match.getMatch(), isFinalMatch(match.getMatch()));
		}
	}
}

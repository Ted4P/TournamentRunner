import java.awt.Dimension;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.awt.Color;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BracketPanel extends JPanel implements ActionListener{
	private Bracket bracket;
	private String[][] info;
	private static int MATCH_WIDTH = 200, MATCH_HEIGHT = MATCH_WIDTH*1/5;
	private final static int MAX_WIDTH = 1000, MIN_WIDTH = 100;
	private static double FACTOR = 1.25;
	private MatchButton[] editButtons;
	private TournamentView view;
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
	}

	public void paintComponent(Graphics g){
		removeAll();
		if(g instanceof Graphics2D)
			g = (Graphics2D)g;
		super.paintComponent(g);
		int level = 0;
		for(;Math.pow(2, level) < Brackets.getSize();level++);
		int shift = (int)(Math.pow(2, level-1)*(MATCH_WIDTH*6.0/100));
		int totShift = (int)(2 * shift * (1-Math.pow(0.5, level)));
		setPreferredSize(new Dimension(2*MATCH_WIDTH*level,(totShift+MATCH_HEIGHT)*2));
		paintOneMatch(2*MATCH_WIDTH*level-(int)(MATCH_WIDTH*1.5),totShift-MATCH_HEIGHT/2+MATCH_HEIGHT,shift, 0, 1, g);
	}
	private void paintOneMatch(int x, int y, int shift, int level, int match, Graphics g){
		g.setFont(new Font(g.getFont().getFontName(),Font.BOLD,MATCH_HEIGHT/3));
		int index = getIndex(match);
		for(int i=0;i<2;i++){
			g.drawRect(x, y, MATCH_WIDTH, MATCH_HEIGHT);
			if((info[index][4]+info[index][5]).equals(Match.DEFAULT_WINNER_NAME + Match.DEFAULT_WINNER_SCHOOL))
				g.setColor(Color.BLACK);
			else if((info[index][2*i]+info[index][2*i+1]).equals(info[index][4]+info[index][5])){
				g.setColor(Color.GREEN);
			}
			else
				g.setColor(Color.RED);
			g.drawString(info[index][2*i], x+5, y+(5*i+4)*MATCH_HEIGHT/10);
			g.drawString(info[index][2*i+1], x+MATCH_WIDTH/2, y+(5*i+4)*MATCH_HEIGHT/10);
			g.setColor(Color.BLACK);
			MatchButton button = new MatchButton(match);
			button.setBounds(x + MATCH_WIDTH, y + MATCH_HEIGHT/5, MATCH_WIDTH/3,3*MATCH_HEIGHT/5);
			button.setFont(new Font(button.getFont().getFontName(), Font.PLAIN, MATCH_HEIGHT/3));
			button.addActionListener(this);
			add(button);
			editButtons[match] = button;
		}
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-3*MATCH_WIDTH/4,y+MATCH_HEIGHT/2-shift);
		g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x-3*MATCH_WIDTH/4,y+MATCH_HEIGHT/2+shift);
		if(Math.pow(2, level+1) + 1 < Brackets.getSize()-1){
			g.drawLine(x-MATCH_WIDTH/2,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
			paintOneMatch(x-MATCH_WIDTH*2,y+shift,shift/2,level+1,match*2,g);
			paintOneMatch(x-MATCH_WIDTH*2,y-shift,shift/2,level+1,match*2+1,g);
		}
		else{
			g.drawLine(x,y+MATCH_HEIGHT/2,x+MATCH_WIDTH,y+MATCH_HEIGHT/2);
		}
	}
	public static void zoomIn(){
		if(MATCH_WIDTH*FACTOR < MAX_WIDTH){
		MATCH_WIDTH *= FACTOR;
		MATCH_HEIGHT *= FACTOR;
		}
	}
	public static void zoomOut(){
		if(MATCH_WIDTH/FACTOR > MIN_WIDTH){
		MATCH_WIDTH /= FACTOR;
		MATCH_HEIGHT /= FACTOR;
		}
	}
	public static void setFactor(double newFactor){
		FACTOR = newFactor;
	}
	private int getIndex(int match){
		int index = 0;
		while(index < Brackets.getSize() && !info[index][7].equals("" + match))
			index++;
		if(index == Brackets.getSize())
			throw new NoSuchElementException();
		return index;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() instanceof MatchButton){
			MatchButton match = (MatchButton)arg0.getSource();
			new MatchEditWindow(view, bracket, info[getIndex(match.getMatch())],match.getMatch());
		}
	}
}

package tournamentRunner;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import windows.BracketPanel;


public class BracketPrinter implements Printable{
	BracketPanel panel;
	public BracketPrinter(BracketPanel panel){
		this.panel = panel;
	}
	@Override
	public int print(Graphics g, PageFormat pgFmt, int page) throws PrinterException {
		if(page > 0)
			return NO_SUCH_PAGE;
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(pgFmt.getImageableX(), pgFmt.getImageableY());
		int prevWidth = BracketPanel.getMatchWidth();
		BracketPanel.setMatchWidth(200);
		panel.paintComponent(g2);
		BracketPanel.setFactor(prevWidth);
		return PAGE_EXISTS;
	}
	
}

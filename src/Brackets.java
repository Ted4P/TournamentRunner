
public class Brackets {
	private Brackets(){}
	private static Bracket[] brackets;
	private static int bSize;
	public static void setBrackets(int num, int size) {
		bSize = size;
		if(brackets!=null) return;
		brackets = new Bracket[num];
		for(int i = 0; i < num; i++) brackets[i] = new Bracket(size,""+i);
	}
	public static Bracket getBracket(int num) {
		if(num>=brackets.length) return null;
		return brackets[num];
	}
	public static int getNum() {
		return brackets.length;
	}
	public static int getSize(){
		return bSize;
	}
	
}

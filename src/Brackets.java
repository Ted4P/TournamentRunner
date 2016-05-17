/*
 * Singleton class representing the Brackets of the tournament, with getters for size, number and brackets
 */
public class Brackets {
	private Brackets(){}
	private static Bracket[] brackets;
	private static int bSize;
	public static void setBrackets(int num, int size) {
		bSize = size;
		brackets = new Bracket[num];
		for(int i = 0; i < num; i++) brackets[i] = new Bracket(size,""+(i+1));
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

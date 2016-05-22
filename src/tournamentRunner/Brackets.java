package tournamentRunner;
/*
 * Singleton class representing the Brackets of the tournament, with getters for size, number and brackets
 */
public class Brackets {
	private Brackets(){}
	private static Bracket[] brackets;
	private static int bSize;
	public static void setBrackets(int num, int size) {		//Create [num] brackets of size [size], and add byes until the nearest power of 2
		int i =2;
		while(i<size) i*=2;
		
		bSize = i;
		brackets = new Bracket[num];
		for(int j = 0; j < num; j++) brackets[j] = new Bracket(size,""+(j+1));
	}
	//Return the bracket at num, or null if array out of bound
	public static Bracket getBracket(int num) {
		if(num>=brackets.length) return null;
		return brackets[num];
	}
	//Return an array of all brackets
	public static Bracket[] getAllBrackets(){
		return brackets;
	}
	//Return the number of brackets
	public static int getNum() {
		return brackets.length;
	}
	//Return the size of each bracket
	public static int getSize(){
		return bSize;
	}
	//Find the index of the bracket, with -1 if none match
	public static int indexOf(Bracket bracket){
		for(int i=0;i<bSize;i++)
			if(brackets[i] == bracket)
				return i;
		return -1;
	}
}


public class Bracket {//Represents a single bracket, with given size and elimination style
	private Match top;
	private String name;
	public Bracket(int size,  String name) {
		top = new Match(size, null);
		this.name = name;
	}
	public void setName(String nm){name = nm;}
	public boolean addPerson(Person per){return top.addPerson(per);}
	
	public void recordWin(Person winner, String notes){
		top.advancePerson(winner, notes);
	}
	/*Returns all names in this bracket in a specific order:
	 * Go to the left most match, then adds the names of both competitors (lPer, rPer) then adds the winner
	 * of the match, followed by the notes, then prints out the right child of the parent of the current match, with the same rules
	 * followed by the information for the parent itself.
	 */
	public String getInfo(){
		return top.getInfo();
	}
	
	public String getName(){
		return name;
	}
	public void restoreState(String[] data, int j) {
		System.out.println("CALLED");
		top.restoreState(data,j+1,Brackets.getSize()-1);
		
	}
}

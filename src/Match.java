
public class Match {
	Match left, right, parent;
	Person person;
	public Match(int size, Match parent){
		this.parent = parent;
		if(size==1) return;
		left = new Match(size/2, this);
		right = new Match(size/2, this);
	}
	public boolean addPerson(Person person){
		if(left!=null && left.addPerson(person)) return true;
		if(right!=null && right.addPerson(person)) return true;
		if(left==null&&right==null && this.person==null){
			System.out.println("ADDING PERSON: " + person);
			this.person = person;
			return true;
		}
		return false;
	}
	
	public boolean advancePerson(Person person){
		if(left!=null && left.advancePerson(person)) return true;
		if(right!=null && right.advancePerson(person)) return true;
		if(left==null && right == null) return false;
		if(left.getPerson()!=null && left.getPerson().equals(person)){this.person = left.getPerson(); return true;}
		else if(right.getPerson()!=null && right.getPerson().equals(person)){ this.person = right.getPerson(); return true;}
		return false;
	}
	public Person getPerson() {
		return person;
	}
}

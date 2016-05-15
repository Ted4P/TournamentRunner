public class Match {		//IF YOU NEED FUNCTIONALITY FROM THIS CLASS, ASK AND I'LL ADD A GETTER. THE CODE IS AWFUL AND WILL MAKE YOU HATE LIFE ITSELF
	private Match left, right, parent;
	private Person lPer, rPer, wPer;
	private String note;
	public Match(int size, Match parent){
		lPer = new Person("TBD", "");
		rPer = new Person("TBD", "");
		wPer = new Person("TBD", "");
		note = "No information availible yet";
		this.parent = parent;
		if(size<=2) return;
		left = new Match(size/2, this);
		right = new Match(size/2, this);
	}
	public boolean addPerson(Person person){
		if(left!=null && left.addPerson(person)) return true;
		else if(right!=null && right.addPerson(person)) return true;
		else if(left==null&&right==null){
			if(lPer.getName().equals("TBD")){lPer = person; return true;}
			if(rPer.getName().equals("TBD")){rPer = person; return true;}
		}
		return false;
	}
		
	public boolean advancePerson(Person person, String notes){
		if(left==null&&right==null){
			if(lPer!=null && lPer.equals(person)){ promoteWinner(lPer,notes); return true;}
			if(rPer!=null && rPer.equals(person)){ promoteWinner(rPer,notes); return true;}
			return false;
		}
		if(lPer!=null && lPer.equals(person)){
			return promoteWinner(lPer, notes);
		}
		if(rPer!=null && rPer.equals(person)){
				return promoteWinner(rPer, notes);
		}
		if(left.advancePerson(person, notes)) return true;
		if(right.advancePerson(person, notes)) return true;
		return false;
	}
	private boolean promoteWinner(Person per, String notes) {
		note = notes;
		wPer = per;
		if(parent!=null){
			parent.setPerson(per);
		}
		return true;
	}
	private void setPerson(Person per){
		if(lPer==null) lPer = per;
		else rPer = per;
	}
	public String getNotes(){return note;}
	public Person getWinner() {
		return wPer;
	}
	public String getInfo(){
		String result = "";
		if(left != null)
			result += left.getInfo() + "\n";
		if(right != null)
			result += right.getInfo() + "\n";
		result += lPer.getName() + "/" + lPer.getSchool() + ","
			+ rPer.getName() + "/" + rPer.getSchool() + ","
			+ wPer.getName() + "/" + wPer.getSchool() + "," + note;
		return result;
	}
}

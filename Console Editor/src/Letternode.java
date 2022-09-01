public class Letternode {
    private char letter;  
	private Letternode next;
	private Letternode previous;
	private boolean selected;
	private boolean found;
	 
	public Letternode(char dataToAdd) {
		 letter = dataToAdd;
	     next = null;
    }
	public void setFound(boolean isFound){
		found = isFound;
	}
	public boolean isFound(){
		return found;
	}
	public void setSelected(boolean isSelected){
		selected = isSelected;
	}
	public boolean isSelected(){
		return selected;
	}
	public Letternode getPrevious() {
		return previous;
	}
	public void setPrevious(Letternode previous) {
		this.previous = previous;
		if(previous != null)previous.next = this;
	}
	public char getLetter() {
		return letter;
	}
	public void setLetter(char letter) {
		this.letter = letter;
	}
	public Letternode getNext() {
		return next;
	}
	public void setNext(Letternode next) {
		this.next = next;
		if(next != null)next.previous = this;
	}
}
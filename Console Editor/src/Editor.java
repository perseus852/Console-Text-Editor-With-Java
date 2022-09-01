import enigma.core.Enigma;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class Editor {
	public enigma.console.Console cn = Enigma.getConsole("Ceng Editor", 100, 30, 16);
	public enigma.console.TextWindow cnt = cn.getTextWindow();
	public static TextAttributes att0 = new TextAttributes(Color.white, Color.black); // foreground, background color
	public static TextAttributes att1 = new TextAttributes(Color.black, Color.white);
	public static TextAttributes att2 = new TextAttributes(Color.black, Color.red);
	public TextMouseListener tmlis;
	public KeyListener klis;
	public int px = 1, py = 1;
	public int takipci=20;
	public MultiLinkedList MLL = new MultiLinkedList();
	public int maxsatir=1;
	// ------ Standard variables for mouse and keyboard ------
	public int mousepr; // mouse pressed?
	public int mousex, mousey; // mouse text coords.
	public int keypr; // key pressed?
	public int rkey; // key (for press/release)
	public int rkeymod; // key modifiers
	public int capslock = 0; // 0:off 1:on
	public boolean insert = true;
	boolean flag = false;
	Letternode firstLetter = null;
	Letternode lastLetter = null;
	int firstLetterx,firstLettery = 0;
	MultiLinkedList selection = new MultiLinkedList();
	MultiLinkedList replace = new MultiLinkedList();
	boolean justify = true;
	boolean copy = false;
	boolean cut = false;
	Letternode nextStart = null;
	boolean find = false;
	boolean replaceFlag = false;
	Letternode lastSelected = null;
	// ----------------------------------------------------

	Editor() throws Exception { // --- Contructor

		// ------ Standard code for mouse and keyboard ------ Do not change
		klis = new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (keypr == 0) {
					keypr = 1;
					rkey = e.getKeyCode();
					rkeymod = e.getModifiersEx();
					if (rkey == KeyEvent.VK_CAPS_LOCK) {
						if (capslock == 0)
							capslock = 1;
						else
							capslock = 0;
					}
				}
			}
			public void keyReleased(KeyEvent e) {}
		};
		cn.getTextWindow().addKeyListener(klis);
		// --------------------------------------------------------------------------
		int curtype = cnt.getCursorType(); // default:2 (invisible) 0-1:visible
		cnt.setCursorType(1);
		cn.setTextAttributes(att0);
		MLL.addLine(1);
		selection.addLine(1);
		replace.addLine(1);
		printScreen();
		cnt.setCursorPosition(px, py);
		while (true) {
			cnt.setCursorPosition(px, py);
			MLL.getHead();
			cnt.setCursorType(1);
			if (keypr == 1 ) { // if keyboard button pressed
				if (rkey == KeyEvent.VK_LEFT && px > 1)
					px--;
				if (rkey == KeyEvent.VK_RIGHT && px < 61)
					px++;
				if (rkey == KeyEvent.VK_UP && py > 1) 
					py--;		
				if (rkey == KeyEvent.VK_DOWN && py <= 20) {
					py++;
					if(py == 21){
						takipci++;
						if(takipci>maxsatir) {
							maxsatir++;
							MLL.addLine(maxsatir);
						}
						py--;
					}
					printMLL();
				}
				if (rkey == KeyEvent.VK_UP && py == 1 && takipci!=20) 
					takipci--;		
				Character rckey = (char) rkey;
				if (rkey == KeyEvent.VK_ENTER) {
					if(justify){
						justify();
						px = MLL.getlastletterPos(takipci-20+py);
					}else alignLeft();
					px=1;
					Linenode line = MLL.getLineNode(takipci-20+py);
					if(line == null){//entera basılan satır boşsa entera basılan satırdan sonraki satıra kadar satır ekle
						while(MLL.getLineCount() != takipci-20+py + 1) {
							MLL.addLine(MLL.getLineCount() + 1);
						}
						line = MLL.getLineNode(takipci-20+py);
					}
					Linenode nextLine = line.getDown();
					Linenode newLine = new Linenode(takipci-20+py+1);
					line.setDown(newLine);
					newLine.setDown(nextLine);
					fixLineNumbers();
					printMLL();
					if(py!=20) {
						py++;
					}
					else takipci++;
					
				}
				
				writeToScreen(rckey);				
				if (rkey == KeyEvent.VK_BACK_SPACE || rkey == KeyEvent.VK_F3){//iflere selection olup olmama kontrolu eklenecek
					Letternode charr = null;
					Linenode linee = null;
					if(selection != null && selection.getHead() != null)
						linee = selection.getHead();
					while(true) {
						if(linee != null && linee.getRight() != null)
							charr = linee.getRight();
						while(true) {
							if(!(py == 1 && px == 1)){//harf siliyor
								px--;
								if(!insert) MLL.setLetter('*', px, takipci-20+py);
								else MLL.insertModeDelete(px, takipci-20+py);
								if(MLL.isLineEmpty(takipci-20+py)){//satırda harf yoksa * ları siler
									MLL.getLineNode(takipci-20+py).setRight(null);
								}
							}
							if (px == 1 || px == 0) {//px == 1 satırı silmek için üst satıra geçiyor
								if(takipci-20+py != 1)if(MLL.isLineEmpty(takipci-20+py) ){//satır boşsa satırı sil
									Linenode lineToDelete = MLL.getLineNode(takipci-20+py);
									Linenode nextLine = lineToDelete.getDown();
									Linenode prevLine = lineToDelete.getUp();
									prevLine.setDown(nextLine);
									fixLineNumbers();
								}
								if(takipci-20+py != 1) px = MLL.getlastletterPos(takipci-20+py-1);//en ilk satırda çalışmicak
								if(py != 1) {
									py--;	
								}
								else if(takipci > 20)
									takipci--;
							}
							
							printMLL();
							
							if(rkey == KeyEvent.VK_BACK_SPACE) break;
							cut = true;
							if(charr != null && charr.getNext() != null)
								charr = charr.getNext();
							else break;		
						}
						if(linee != null && linee.getDown() != null)
							linee = linee.getDown();
						else break;
					}
				}
			
				
				if (rkey == KeyEvent.VK_F1) {
					clearSelected();
					clearFound();
					firstLetter = MLL.getLetterN(px, takipci-20+py);
					firstLetterx = px;
					firstLettery = takipci-20+py;
					copy = false;
					cut = false;
					nextStart = null;
					find = false;	
				}
				if (rkey == KeyEvent.VK_F2) {
					if(firstLetter != null && !(firstLetterx == px && firstLettery == takipci-20+py) ){
						selection = new MultiLinkedList();
						selection.addLine(1);
						lastLetter = MLL.getLetterN(px - 1, takipci-20+py);
						Linenode line = MLL.getLineNode(firstLettery);
						Linenode selectionLine = selection.getHead();
						int lastLetterx = px -1;						
						int lastLettery = takipci-20+py;
						Letternode letter = firstLetter; //first letter to add
						while(lastLettery + 1 != firstLettery) {
							selectionLine.setRight(new Letternode(' '));//yeni satıra ilk harfi ekle
							Letternode selectionNode = selectionLine.getRight();//yeni satırın ilk harfini seç
							if(line.getRight() != null)while (firstLetterx <= line.getLength()) {
								char letterToAdd = letter.getLetter();//letter to add on a different node
								selectionNode.setLetter(letterToAdd);//change current selection node to letter to add
								letter.setSelected(true);
								if(firstLetterx == lastLetterx && firstLettery == lastLettery) break;
								firstLetterx++;
								if(letter.getNext() != null)selectionNode.setNext(new Letternode(' '));//create next letter node if next letter exists
								selectionNode = selectionNode.getNext();
								letter = letter.getNext();
							}
							if(firstLetterx == lastLetterx && firstLettery == lastLettery) break;
							firstLettery++;
							firstLetterx = 1;
							selectionLine.setDown(new Linenode(selectionLine.getSatirnumber()+1));//selectiona yeni satır oluştur
							selectionLine = selectionLine.getDown();
							line = line.getDown();
							if(line != null)letter = line.getRight();
						}	
						printMLL();
						cut = false;
						find = false;
					}
				}
				if(rkey == KeyEvent.VK_F4) {
					copy = true;
				}
				if(rkey == KeyEvent.VK_F5 && (copy || cut) ) {
					Letternode charAt = null;
					Linenode lineAt = null;
					if(selection.getHead() != null)
						lineAt = selection.getHead();
					while(lineAt != null) {
						if(lineAt != null && lineAt.getRight() != null)
							charAt = lineAt.getRight();
						while(charAt != null) {
							if (Character.isUpperCase(charAt.getLetter()))
								capslock = 1;
							else
								capslock = 0;
							writeToScreen(Character.toUpperCase(charAt.getLetter()));
							charAt = charAt.getNext();
						}
						if(lineAt.getDown() != null){//son harfi yapıştırdıktan sonra yeni satıra geçmesin diye if
							py++;
							px = 1;
						}
						lineAt = lineAt.getDown();
					}
				}
				if(rkey == KeyEvent.VK_F6 && !find) {//find
					find();
					find = true;
				}
				if(rkey == KeyEvent.VK_F7){
					printMLL();
					px--;
					replaceFlag = true;
					cnt.setCursorPosition(71, 17);
					Scanner sc = new Scanner(cn.getInputStream());
					String replaceStr = sc.nextLine();
					replaceFlag = false;
					for (int i = 0; i < replaceStr.length(); i++) {
						replace.addLetter(replaceStr.charAt(i), i+1, 1);
					}
					Linenode line = MLL.getHead();
					while(line != null){
						Letternode letter = line.getRight();
						while(letter != null){
							if(letter.isFound() || letter.isSelected()){
								Letternode replaceFirst = replace.getHead().getRight();
								int x = MLL.getCoordinates(letter)[0];
								for (int i = 0; i < replace.getHead().getLength(); i++) {
									MLL.insertLetterToRight(replaceFirst.getLetter(), x + i, line.getSatirnumber());
									replaceFirst = replaceFirst.getNext();
									if(letter != null)letter = letter.getNext();
								}
							}
							if(letter != null)letter = letter.getNext();
						}
						line = line.getDown();
					}  
					cnt.setCursorPosition(71, 17);
					System.out.print("            ");
					deleteFound();
					replace = new MultiLinkedList();
					replace.addLine(1);
				}
				if(rkey == KeyEvent.VK_F8) {
					next();
				}
				if (rkey == KeyEvent.VK_F9){
					alignLeft();
					justify = false;
					cnt.setCursorPosition(73, 15);
					System.out.print("              ");
					cnt.setCursorPosition(73, 15);
					System.out.print("Align Left");
				}
				if (rkey == KeyEvent.VK_F10){
					justify();
					justify = true;
					cnt.setCursorPosition(73, 15);
					System.out.print("              ");
					cnt.setCursorPosition(73, 15);
					System.out.print("Justify       ");
				}
				if (rkey == KeyEvent.VK_F11) {
					load(takipci);
				}
				if (rkey == KeyEvent.VK_F12) {
					save(MLL.getHead());
				}
				if(rkey == KeyEvent.VK_INSERT) {
					if(insert) { 
						insert = false;
						cnt.setCursorPosition(68, 13);
						System.out.print("Overwrite");
					}
					else {
						insert = true;
						cnt.setCursorPosition(68, 13);
						System.out.print("          ");
						cnt.setCursorPosition(68, 13);
						System.out.print("Insert");
					}
				}
				if(rkey == KeyEvent.VK_END) {
					if(!MLL.isLineEmpty(takipci-20+py)) px = MLL.getlastletterPos(takipci-20+py);
				}
				if(rkey == KeyEvent.VK_HOME) {
					if(!MLL.isLineEmpty(takipci-20+py)) px = MLL.getFirstLetterPos(takipci-20+py);
				}
				if(rkey == KeyEvent.VK_PAGE_UP) {
					py = 1;
					px = 1;
					takipci = 20;
				}
				if(rkey == KeyEvent.VK_PAGE_DOWN) {
					py = 20;
					px = 1;
					takipci = maxsatir;
				}
				
				printMLL();
				keypr = 0; // last action
			}
			
			Thread.sleep(20);
		}
		
	}
	void alignLeft(){
		Linenode firstLine = MLL.getHead();
		while(firstLine != null){
			Letternode firstletter = firstLine.getRight();	
			while(firstletter != null){
				if(firstletter.getLetter() == '*'){
					MLL.insertModeDelete(MLL.getCoordinates(firstletter)[0], MLL.getCoordinates(firstletter)[1]);
				}
				if(firstletter.getLetter() != '*') break;
				firstletter = firstletter.getNext();
			}
			firstLine = firstLine.getDown();
		}
		px = 1;
	}
	void justify(){		
		Linenode firstLine = MLL.getHead();
		while(firstLine != null){
			if(firstLine.getRight() != null){
				int starCount = firstLine.getStarCount();//baştaki boşluk sayısı
				int spacesAtLast = 60-firstLine.getLength();
				if(spacesAtLast > starCount){
					while(spacesAtLast - starCount > 1) {
						MLL.addToLineStart('*', firstLine.getSatirnumber());
						spacesAtLast = 60-firstLine.getLength();
						starCount = firstLine.getStarCount();
					}
				}
				else if (spacesAtLast < starCount){
					while(spacesAtLast - starCount <= 1) {
						MLL.insertModeDelete(1, firstLine.getSatirnumber());
						spacesAtLast = 60-firstLine.getLength();
						starCount = firstLine.getStarCount();
					}
				}
			}
			firstLine = firstLine.getDown();
		}
	}
	void next(){
		Linenode line = MLL.getLineNode(MLL.getCoordinates(lastLetter)[1]);
		Letternode letter = lastLetter.getNext();
		while(line != null){                                     
			while(letter != null){
				if(letter.isFound()){
					clearSelected();
					for (int i = 0; i < selection.getHead().getLength(); i++) {
						letter.setSelected(true);
						lastLetter = letter;
						letter = letter.getNext();
					}
					return;
				}
				letter = letter.getNext();
			}
			line = line.getDown();
			if(line != null)letter = line.getRight();
		}
	}
	void find(){
		Linenode line = MLL.getHead();
		Linenode lineS = selection.getHead();
		Letternode letter = null;
		if(nextStart != null) {
			letter = nextStart;
			line = MLL.getLineNode(MLL.getCoordinates(letter)[1]);
		}
		while(line != null){
			if(letter == null)letter = line.getRight();
			while(letter != null){
				Letternode letterS = lineS.getRight();//seçimin ilk harfini al
				Letternode start = letter;//save start of letter
				int lettercount = 1;//seçili harf sayısı
				while(letter.getLetter() == letterS.getLetter()){//letter dan başla seçimle aynı mı diye sırayla kontrol et
					if(letterS.getNext() == null){
						for (int i = 0; i < lettercount; i++) {
							start.setFound(true);
							start = start.getNext();
						}
						nextStart = letter;
					}
					lettercount++;
					letter = letter.getNext();
					if(letter == null) break;
					if(letterS.getNext() != null)letterS = letterS.getNext();
				}
				letter = start;
				if(letter != null)letter = letter.getNext();
			}
			line = line.getDown();
		}
	}

	void clearSelected(){
		Linenode line = MLL.getHead();
		while(line != null){
			Letternode letter = line.getRight();
			while(letter != null){
				letter.setSelected(false);
				letter = letter.getNext();
			}
			line = line.getDown();
		}
	}
	void clearFound(){
		Linenode line = MLL.getHead();
		while(line != null){
			Letternode letter = line.getRight();
			while(letter != null){
				letter.setFound(false);
				letter = letter.getNext();
			}
			line = line.getDown();
		}
	}
	void deleteFound(){
		Linenode line = MLL.getHead();
		while(line != null){
			Letternode letter = line.getRight();
			while(letter != null){
				if(letter.isFound() || letter.isSelected()){
					MLL.insertModeDelete(MLL.getCoordinates(letter)[0], line.getSatirnumber());
				}
				letter = letter.getNext();
			}
			line = line.getDown();
		}
	}
	void printMLL(){
		Linenode temp = MLL.getHead();
		cnt.setCursorType(2);
		int x = 1;
		int y = 1;
		for (int i = 1; i < 21; i++) {
			cnt.setCursorPosition(1, i);
			System.out.print("                                                            ");
		}
		for(int i=0;i<takipci-20;i++) {
			temp = temp.getDown();
		}
		for (int i = 0; i < 20; i++) {		
			if(temp != null){
				Letternode temp2 = temp.getRight();
				while(temp2 != null){//kelimeler
					if(temp2.getLetter() != '*') {
						cn.getTextWindow().output(x,y,temp2.getLetter());
					}
					if(temp2.isSelected()){
						if(temp2.getLetter() == '*') cn.getTextWindow().output(x,y,' ',att1);
						else cn.getTextWindow().output(x,y,temp2.getLetter(),att1);
					}
					else if (temp2.isFound()){
						if(temp2.getLetter() == '*') cn.getTextWindow().output(x,y,' ',att2);
							else cn.getTextWindow().output(x,y,temp2.getLetter(),att2);
						
					}
					x++;
					temp2 = temp2.getNext();
				}
				x=1;
				y++;
				temp = temp.getDown();
			}
		}
	}
	void writeToScreen(Character rckey) {
		boolean isWritten = false;
		boolean isShiftPressed = false;
		cnt.setCursorPosition(px, py);
		if ((rkey == KeyEvent.VK_SPACE && (px < 60)) || rckey == ' ') {
			if(MLL.getLineCount() <= py){//aşağı satırlara yazılmaya çalışılırsa ve satır yoksa yazılmaya çalışan satıra kadar satırlar oluşturur
				for (int i = MLL.getLineCount() + 1; i <= py; i++) {
					MLL.addLine(i);
					maxsatir++;
				}
			}
			if(insert) MLL.insertLetterToRight('*', px, takipci-20+py);
			else MLL.setLetter('*', px, takipci-20+py);
			px++;
			printMLL();
		}
		if (rckey >= '0' && rckey <= '9') {
			isWritten = true;
		}
		if (rckey >= 'A' && rckey <= 'Z') {
			if (((rkeymod & KeyEvent.SHIFT_DOWN_MASK) > 0) || capslock == 1) {
				isWritten = true;
				isShiftPressed = true;
			} else {
				rckey = (char) (rckey + 32);
				isWritten = true;
			}
		}
		if ((rkeymod & KeyEvent.SHIFT_DOWN_MASK) == 0 && !(rckey == ';' || rckey == ':')) {
			if (rckey == '.' || rckey == ',' || rckey == '-') {
				isWritten = true;
			}
		 } 
		else {
			if (rckey == '.' || rckey == ':') {
				rckey = ':';
				isWritten = true;
			}
			if (rckey == ',' || rckey == ';') {
				rckey = ';';
				isWritten = true;
			}
		}
		if (isWritten) {
			if(MLL.getLineCount() <= takipci-20+py){//aşağı satırlara yazılmaya çalışılırsa ve satır yoksa yazılmaya çalışan satıra kadar satırlar oluşturur
				for (int i = MLL.getLineCount() + 1; i <= takipci-20+py; i++) {
					MLL.addLine(i);
					maxsatir++;
				}
			}
			if (capslock == 1 || isShiftPressed) {
				if(insert)
					MLL.insertLetterToRight(Character.toUpperCase(rckey), px, takipci-20+py);
				else MLL.setLetter(Character.toUpperCase(rckey), px, takipci-20+py);
			}
			else {
				if(insert)
					MLL.insertLetterToRight(Character.toLowerCase(rckey), px, takipci-20+py);
				else MLL.setLetter(Character.toLowerCase(rckey), px, takipci-20+py);
			}
			printMLL();
			px++;
			
		}
		if (px >= 61 && !replaceFlag) {// end of line
			if(justify){
				justify();
				px = MLL.getlastletterPos(takipci-20+py);
			}else alignLeft();
			if(py!=20) {
				py++;
			}
			else takipci++;
			px = 1;
			if(py==20 && takipci>maxsatir) {
				maxsatir++;
				MLL.addLine(maxsatir);
				printMLL();
			}
		}
	}
	void fixLineNumbers(){
		Linenode firstLine = MLL.getHead();
		int i = 1;
		while(firstLine != null){
			firstLine.setSatirnumber(i);
			i++;
			firstLine = firstLine.getDown();
		}
	}	
	void printScreen() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File("screen.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("File not found!!!");
		}
		while (sc.hasNextLine()) {
			char[] charArr = sc.nextLine().toCharArray();
			for (char c : charArr) {
				cn.getTextWindow().output(c);
			}
			System.out.println();
		}
	}
	void save(Linenode head) throws IOException {
		BufferedWriter wr = new BufferedWriter(new FileWriter("save.txt"));
		Linenode temp = head;
		while (temp != null) {
			Letternode temp2 = temp.getRight();
			if(!MLL.isLineEmpty(temp.getSatirnumber())){
				while (temp2 != null) {
					if (temp2.getLetter() == '*') {
						wr.write(' ');
					} else {
						if (Character.isUpperCase(temp2.getLetter()))
							wr.write(Character.toUpperCase(temp2.getLetter()));
						else
							wr.write(Character.toLowerCase(temp2.getLetter()));
					}
					temp2 = temp2.getNext();
				}	
			}
			wr.newLine();
			temp = temp.getDown();
		}
		wr.close();
	}
	void load(int takipci) throws FileNotFoundException {
		MLL = new MultiLinkedList();
		MLL.addLine(1);
		Scanner sc = new Scanner(new File("load.txt"));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			for (int i = 0; i < line.length(); i++) {
				if (Character.isUpperCase(line.charAt(i)))
					capslock = 1;
				else
					capslock = 0;
				writeToScreen(Character.toUpperCase(line.charAt(i)));
			}
			if(line.length() != 60)py++;
			if(py > 20){
				takipci++;
				maxsatir++;
				MLL.addLine(maxsatir);
			}
			px = 1;
		}
		py = 1;
	}
}
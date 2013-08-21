
public class work {
	
	boolean[][] x = new boolean[3][3];
	boolean[][] o = new boolean[3][3];
	
	
	public work(){
		
		init();
	}

	public void init(){
		for(int i=0;i < 3; i++){
			for(int j=0;j < 3;j++){
				x[i][j] = false;
				o[i][j] = false;
			}
		}
	}
	
	public void changevalue(int posx, int posy, boolean t){
		
		if(t == true){
			x[posx][posy] = true;
		}
		else{
			o[posx][posy] = true;
		}
	
			
	}
	
	public void iswinner(){
		
		int win = 5;
		if(x[0][0] == true & x[0][1] == true & x[0][2] == true) win = 1;
		else if (x[1][0] == true & x[1][1] == true & x[1][2] == true) win = 1;
		else if (x[2][0] == true & x[2][1] == true & x[2][2] == true) win = 1;
		else if (x[0][0] == true & x[1][0] == true & x[2][0] == true) win = 1;
		else if (x[0][1] == true & x[1][1] == true & x[2][1] == true) win = 1;
		else if (x[0][2] == true & x[1][2] == true & x[2][2] == true) win = 1;
		else if (x[0][0] == true & x[1][1] == true & x[2][2] == true) win = 1;
		else if (x[0][2] == true & x[1][1] == true & x[2][0] == true) win = 1;
		else if (o[0][0] == true & o[0][1] == true & o[0][2] == true) win = 2;
		else if (o[1][0] == true & o[1][1] == true & o[1][2] == true) win = 2;
		else if (o[2][0] == true & o[2][1] == true & o[2][2] == true) win = 2;
		else if (o[0][0] == true & o[1][0] == true & o[2][0] == true) win = 2;
		else if (o[0][1] == true & o[1][1] == true & o[2][1] == true) win = 2;
		else if (o[0][2] == true & o[1][2] == true & o[2][2] == true) win = 2;
		else if (o[0][0] == true & o[1][1] == true & o[2][2] == true) win = 2;
		else if (o[0][2] == true & o[1][1] == true & o[2][0] == true) win = 2;
		else win = 0;
		
		switch (win){		
		case(1):
			
			System.out.println("x is winner");
			break;
		
		case(2):
			
			System.out.println("o is winner");
			break;
		}
	}
	
	
	
	
	
}

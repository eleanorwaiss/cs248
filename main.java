import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class Dad extends WindowAdapter{
	public void windowClosing(WindowEvent e)
	{
		System.out.println("Program terminated.");
		System.exit(0); // quits the program
	}
}


public class main extends JFrame implements ActionListener{
	protected final boolean DEBUG = false; //flag for debug mode, to send addl messages to the terminal
	protected int gameStatus = 0;
	/* 0: game active
	 * 1: player 1 wins
	 * 2: player 2 wins
	 * -1: tie
	 **/
	protected int gameMode = 0; //0: player goes first 1: AI goes first

	private final int xLimit = 600; //width of game board
	private final int yLimit = 600; //height of game board, does not include bottom menu
	private final int nodeSize = 70; //diameter of each node
	private final int temp = nodeSize/2; //radius of each node

	private final int [] xtemp = {xLimit/10,3*xLimit/10, xLimit/2, 7*xLimit/10, 9*xLimit/10}; //array for centers of each node, by x position
	private final int [] ytemp = {yLimit/5, yLimit/2, 4*yLimit/5}; //array for centers of each node, by y position

	private Color p1 = Color.blue; // player 1 color
	private Color p2 = Color.red; // player 2 color

	private final int [] PRIMES = {2,3,5,7,11,13,17,19,23}; //first nine prime numbers
	private int p1Score = 1, p2Score = 1; //Score values, see WINNING_KEY desc
	public final int [] WINNING_KEY = {30, 266, 357, 506, 897, 935, 1001, 1235, 7429};
		/* How this works: each node gets assigned a unique prime number.
		* When a player selects a node, their score (which is initialized to 1) gets multiplied by the prime assigned to the node.
		* To check if a player has a winning line, check if their score mod any of the above keys is zero.
		* If yes, they must have all three of the primes that factor into the score, a.k.a. they have all three nodes.
		* I'm really happy that I thought of this, it is super efficient and uses some nice elementary number theory!
		**/
	public final int MAX_SCORE = 223092870; //product of all primes used, highest possible score
		
	
	public final int [][] nodePos = { //the centers of each node
		{xtemp[0],ytemp[0]}, //node 1
		{xtemp[2],ytemp[0]}, //node 2
		{xtemp[4],ytemp[0]}, //node 3
		{xtemp[1],ytemp[1]}, //node 4
		{xtemp[2],ytemp[1]}, //node 5
		{xtemp[3],ytemp[1]}, //node 6
		{xtemp[0],ytemp[2]}, //node 7
		{xtemp[2],ytemp[2]}, //node 8
		{xtemp[4],ytemp[2]} //node 9
	};

	class node{
		private int xPos, yPos;
		private int size = nodeSize;
		private Color color;
		private int status = 0; 
			/* 0: unclaimed
			* 1: player 1
			* 2: player 2
			**/

		protected int prime;

		public node(int x, int y, int size, int p, Color color)
		{
			this.xPos=x; this.yPos=y; this.size=size; this.prime=p; this.color=color; this.status = 0;
		}

		public void setStatus(int n) {
			this.status = n;
			if(n==1){
				p1Score = p1Score*this.prime;
				if(DEBUG) System.out.println("P1: "+p1Score);
			}else{
				p2Score = p2Score*this.prime;
				if(DEBUG) System.out.println("P2: "+p2Score);
			}
		}
		

		public void setColor(Color newCol) { color=newCol; }

		public void draw(Graphics g){
			if(status==0){ //if node is unclaimed
				g.setColor(Color.black);
				g.fillOval( xPos, yPos, size, size );
				g.setColor(Color.white);
				g.fillOval( xPos+10, yPos+10, size-20, size-20); 
			} else if(status==1){ //else if player 1 claimed
				g.setColor(p1);
				g.fillOval( xPos, yPos, size, size );
			} else{ //else player 2 claimed
				g.setColor(p2);
				g.fillOval( xPos, yPos, size, size );
			}
		}
	}

	public node [] board;

	// this part does the drawing
	class Ocean extends JPanel
	{

		public Ocean(){
			setSize(xLimit,yLimit);
			board=new node[nodePos.length];
			for(int i=0; i<board.length; i++) board[i]=new node(nodePos[i][0]-temp, nodePos[i][1]-temp, nodeSize, PRIMES[i], Color.black);
		}

		public void changeColor(int n){
			board[n].setColor(p1);
		}

		public void paintComponent(Graphics g){
			//board background
			g.setColor(Color.pink); 
			g.fillRect(0,0, xLimit,yLimit);

			//lines connecting nodes
			g.setColor(Color.black); 
			g.drawLine(xtemp[0],ytemp[0], xtemp[4],ytemp[0]);
			g.drawLine(xtemp[1],ytemp[1], xtemp[3],ytemp[1]); 
			g.drawLine(xtemp[0],ytemp[2], xtemp[4],ytemp[2]); 
			g.drawLine(xtemp[0],ytemp[0], xtemp[2],ytemp[2]); 
			g.drawLine(xtemp[0],ytemp[0], xtemp[4],ytemp[2]); 
			g.drawLine(xtemp[0],ytemp[2], xtemp[2],ytemp[0]); 
			g.drawLine(xtemp[0],ytemp[2], xtemp[4],ytemp[0]); 
			g.drawLine(xtemp[2],ytemp[2], xtemp[4],ytemp[0]); 
			g.drawLine(xtemp[2],ytemp[0], xtemp[4],ytemp[2]); 
			
			// nodes themselves
			for(int i=0; i<board.length; i++)
				board[i].draw(g);

		}
	}

	Ocean atlantic;

	JButton reseta, resetp, enter;
	JTextField nodeNum;

	public void checkOver(){
		for(int i=0; i<WINNING_KEY.length; i++){ //check if p1 wins
			if(p1Score%WINNING_KEY[i]==0) gameStatus=1;
		} 
		
		//did AI win?
		for(int i=0; i<WINNING_KEY.length; i++){
			if(p2Score%WINNING_KEY[i]==0) gameStatus=2;
		}

		//is game drawn?
		if(gameStatus==0){
			if(p1Score*p2Score==MAX_SCORE){
				gameStatus=-1;
			}
		}

		if(gameStatus!=0) {//if game over
			System.out.print("Game over. ");
			if(gameStatus==1) System.out.println("Player wins!");
			else if(gameStatus==2) System.out.println("AI wins!");
			else System.out.println("Draw!");
		}
	}

	public void actionPerformed(ActionEvent e){
		// which button was pressed?
		if(e.getSource()==reseta||e.getSource()==resetp) { //if reset
			gameStatus = 0; //flag game as in progress
			for(int i=0; i<9; i++){ //reset nodes
				board[i].status=0;
			}
			p1Score = p2Score = 1; //reset scores
			if(e.getSource()==resetp){//who goes first?
				gameMode=0; 
				System.out.println("Reset, player first");
			} else {
				gameMode=1; 
				System.out.println("Reset, AI first"); 
				//make AI move
				int r;
				do{
					r = (int)(Math.random()*9);
				} while(board[r].status!=0);
				board[r].setStatus(2);
			}
		} else // move entered
		{
			int n = Integer.parseInt(nodeNum.getText())-1; //read in node to update from text field
				//less one as game board visually is 1-9, game itself 0-8
			if((n<9)&&(n>-1)){ //check if value in range
				if(board[n].status==0){ //check if node unclaimed
					board[n].setStatus(1);
					atlantic.changeColor(n); 
					checkOver();

					if(gameStatus==0){ //if game still active
						//if adding AI, this is where the selection algo goes

						//board[i].status gives the status of each node
						//AI make rand move
						int r;
						do{
							r = (int)(Math.random()*9);
						} while(board[r].status!=0);
						board[r].setStatus(2);
						checkOver();
					}
				} else{ //invalid input
					if(DEBUG) System.out.println("Invalid Action: node already claimed.");
				}
			} else {
				if(DEBUG) System.out.println("Input out of bounds.");
			}
		}
		repaint();
	}

	public main(){
		setTitle("Jerry-Tac-Toe");
		addWindowListener( new Dad() );
		setSize(xLimit+10,yLimit+70);

		Container glass=getContentPane();
		glass.setLayout( new BorderLayout() );

		reseta=new JButton("Reset: AI goes first");
		reseta.addActionListener(this);
		resetp=new JButton("Reset: Player goes first");
		resetp.addActionListener(this);
		enter=new JButton("Enter");
		enter.addActionListener(this);
		nodeNum=new JTextField("0");
		nodeNum.setFont(new Font("Arial", Font.PLAIN, 24));

		JLabel playerCode = new JLabel("Player is blue.  Computer is red.");
		playerCode.setFont(new Font("Arial", Font.PLAIN, 24));

		atlantic=new Ocean();

		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());
		JPanel reset = new JPanel();
		reset.add(resetp, "West");
		reset.add(reseta, "East");

		bottom.add(reset,"West");
		bottom.add(nodeNum,"Center");
		bottom.add(enter,"East");

		glass.add(playerCode, "North");
		glass.add(atlantic,"Center");
		glass.add(bottom,"South");

		repaint();
		setVisible(true);
	}

	public static void main(String [] args){
		main syrup=new main();
	}
}
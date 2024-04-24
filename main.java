import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Line2D;

class Dad extends WindowAdapter{
	public void windowClosing(WindowEvent e)
	{
		System.out.println("Program terminated.");
		System.exit(0); // quits the program
	}
}


public class main extends JFrame implements ActionListener{
	protected final boolean DEBUG = true; //flag for debug mode, to send addl messages to the terminal
	protected int gameStatus = 0; //effectively a three-valued boolean to mark if (0) GAME_OPEN, (1)P1_WIN, (-1) P2_WIN

	private final int xLimit = 600; //width of game board
	private final int yLimit = 600; //height of game board, does not include bottom menu
	private final int nodeSize = 80; //diameter of each node
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
			}else{
				p2Score = p2Score*this.prime;
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

	JButton reset, enter;
	JTextField nodeNum;

	public void actionPerformed(ActionEvent e){
		// which button was pressed?
		if(e.getSource()==reset) {
			gameStatus = 0;
			for(int i=0; i<9; i++){
				board[i].status=0;
			}
			p1Score = p2Score = 1;
		} else // update gameboard
		{
			int n = Integer.parseInt(nodeNum.getText())-1; //read in node to update from text field
			if((n<9)&&(n>-1)){
				if(board[n].status==0){
					board[n].setStatus(1);
					atlantic.changeColor(n); //less one as game board visually is 1-9, game itself 0-8
					for(int i=0; i<WINNING_KEY.length; i++){
						if(p1Score%WINNING_KEY[i]==0) gameStatus=1;
					}

					if(gameStatus==0){
						int r;
						do{
							r = (int)(Math.random()*9);
						} while(board[r].status!=0);
						board[r].setStatus(2);
					}
					for(int i=0; i<WINNING_KEY.length; i++){
						if(p1Score%WINNING_KEY[i]==0) gameStatus=-1;
					}
					if(gameStatus!=0) System.out.println("Game Over.");
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
		setSize(xLimit,yLimit+50);

		Container glass=getContentPane();
		glass.setLayout( new BorderLayout() );

		reset=new JButton("Reset");
		reset.addActionListener(this);
		enter=new JButton("Enter");
		enter.addActionListener(this);
		nodeNum=new JTextField("0");
		nodeNum.setFont(new Font("Arial", Font.PLAIN, 24));

		atlantic=new Ocean();

		JPanel bottom=new JPanel();
		bottom.setLayout(new BorderLayout());

		bottom.add(reset,"West");
		bottom.add(nodeNum,"Center");
		bottom.add(enter,"East");

		glass.add(atlantic,"Center");
		glass.add(bottom,"South");

		repaint();
		setVisible(true);
	}

	public static void main(String [] args){
		main syrup=new main();
	}
}
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

	public static Color randomcolor() //can we delete this function? Obsolete
	{
		return new Color( 
			(int)(256*Math.random()),
			(int)(256*Math.random()),
			(int)(256*Math.random())
		);
	}
	

	private final int xLimit = 600; //width of game board
	private final int yLimit = 600; //height of game board, does not include bottom menu
	private final int nodeSize = 80; //diameter of each node
	private final int temp = nodeSize/2; //radius of each node
	private final int [] xtemp = {xLimit/10,3*xLimit/10, xLimit/2, 7*xLimit/10, 9*xLimit/10}; //array for centers of each node, by x position
	private final int [] ytemp = {yLimit/5, yLimit/2, 4*yLimit/5}; //array for centers of each node, by y position
	private Color p1 = Color.red; // player 1 color
	private Color p2 = Color.pink; // player 2 color
	
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

	class node
	{
		private int xPos, yPos;
		private int size = nodeSize;
		private Color color;
		private int status = 0; 
			/* 0: null
			 * 1: player 1
			 * 2: player 2
			 **/

		public node(int x, int y, int size, Color color)
		{
			this.xPos=x; this.yPos=y; this.size=size; this.color=color; this.status = 0;
		}

		public void setStatus(int n) {this.status = n;}
		

		public void setColor(Color newcolor) { color=newcolor; }

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

		public Ocean()
		{
			setSize(xLimit,yLimit);
			board=new node[nodePos.length];
			for(int i=0; i<board.length; i++) board[i]=new node(nodePos[i][0]-temp, nodePos[i][1]-temp, nodeSize, Color.black);
		}

		public void changeColor(int n)
		{
			board[n].setColor(p1);
		}

		public void paintComponent(Graphics g)
		{
			//board background
			g.setColor(Color.blue); 
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

	public void actionPerformed(ActionEvent e)
	{
		// which button was pressed?
		if(e.getSource()==reset) {
			for(int i=0; i<9; i++){
				board[i].status=0;
			}
		} else // update gameboard
		{
			int n = Integer.parseInt(nodeNum.getText())-1;
			if((n<9)&&(n>-1)){
				if(board[n].status==0){
					board[n].setStatus(1);
					atlantic.changeColor(n); //less one as game board visually is 1-9, game itself 0-8
				} else{
					System.out.println("Invalid Action: node already claimed.");
				}
			} else {
				//error handling for input out of bounds
			}
		}
		repaint();
	}

	public main()
	{
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

	public static void main(String [] args)
	{
		main syrup=new main();
	}
}
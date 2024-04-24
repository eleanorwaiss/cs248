import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.Line2D;

class Dad extends WindowAdapter
{
	public void windowClosing(WindowEvent e)
	{
		System.out.println("Dad likes pancakes.");
		System.exit(0); // quits the program
	}
}

public class main extends JFrame implements ActionListener{

	public static Color randomcolor()
	{
		return new Color( 
			(int)(256*Math.random()),
			(int)(256*Math.random()),
			(int)(256*Math.random())
		);
	}
	

	private final int xLimit = 600;
	private final int yLimit = 600;
	private final int nodeSize = 80;
	private final int temp = nodeSize/2;
	private final int [] xtemp = {xLimit/10,3*xLimit/10, xLimit/2, 7*xLimit/10, 9*xLimit/10};
	private final int [] ytemp = {yLimit/5, yLimit/2, 4*yLimit/5};
	private Color p1 = Color.red;
	private Color p2 = Color.pink;
	
	public final int [][] nodePos = {
		{xtemp[0],ytemp[0]},
		{xtemp[2],ytemp[0]},
		{xtemp[4],ytemp[0]},
		{xtemp[1],ytemp[1]},
		{xtemp[2],ytemp[1]},
		{xtemp[3],ytemp[1]},
		{xtemp[0],ytemp[2]},
		{xtemp[2],ytemp[2]},
		{xtemp[4],ytemp[2]}
	};

	class node
	{
		private int xPos, yPos;
		private int size;
		private Color color;
		private int status = 0;

		public node(int x, int y, int size, Color color)
		{
			this.xPos=x; this.yPos=y; this.size=size; this.color=color; this.status = 0;
		}

		public void setStatus(int n) {this.status = n;}
		public void swim(){};
		

		public void setColor(Color newcolor) { color=newcolor; }

		public void draw(Graphics g)
		{
			g.setColor(color);
			g.fillOval( xPos, yPos, size, size );
			if(status==0){
				g.setColor(Color.white);
				g.fillOval( xPos+10, yPos+10, size-20, size-20); 
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

		/*
		public void move()
		{
			for(int i=0; i<board.length; i++)
				board[i].swim();
		}
		**/

		public void changeColor(int n)
		{
			board[n].setColor(p1);
		}

		public void paintComponent(Graphics g)
		{
			g.setColor(Color.blue);
			g.fillRect(0,0, xLimit,yLimit);
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
		if(e.getSource()==reset) {}
			//atlantic.reset();
		else // update gameboard
		{
			int n = Integer.parseInt(nodeNum.getText())-1;
			board[n].setStatus(1);
			atlantic.changeColor(n); //less one as game board visually is 1-9, game itself 0-8
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




import java.util.*;

//Helper class to DotGame
public class DotBoard {

	boolean[][][] board;
	//[coordinate][coordinate][0->north, 1->south, 2->east, 3->west, claimed or not]
	
	int width;
	int height;
	boolean isEndgame;
	public ArrayList<String> moves;
	
	
	public DotBoard(int w, int h)
	{
		width = w;
		height = h;
		isEndgame = false;
		board = new boolean[w][h][5];
		moves = new ArrayList<String>();
		
	}

	public DotBoard copy()
	{
		DotBoard copy = new DotBoard(this.width, this.height);
		for (byte i = 0; i < width; i++)
		{
			for (byte j = 0; j < height; j++)
			{
				for (byte k = 0; k < 5; k++)
				{
					copy.board[i][j][k] = this.board[i][j][k];
				}
			}
		}
		return copy;
	}
	
	
	//method to help determine best spot to move if it is forced to create a 3square
	public String findShortestEnemyChain()
	{
		byte lowestWinnings = Byte.MAX_VALUE;
		byte x = 0;
		byte y = 0;
		byte d = 0;
		byte tempWinnings = Byte.MAX_VALUE;
		//starting at 0,0, going to w-1, h-1
		for (byte i = 0; i < width; i++)
		{
			for (byte j = 0; j < height; j++)
			{
				if (board[i][j][4])
				{
					continue;
				}
				for (byte k = 0; k < 4; k++)
				{
					if (!board[i][j][k])
					{
						DotBoard copy = this.copy();
						switch (k)
						{
						case 0:
							copy.mark(i,j,'n');
							break;
						case 1:
							copy.mark(i,j,'s');
							break;
						case 2:
							copy.mark(i,j,'e');
							break;
						case 3:
							copy.mark(i,j,'w');
							break;
						}
						tempWinnings = calculateEnemyWinnings(copy);

					}
					if (tempWinnings < lowestWinnings)
					{
						lowestWinnings = tempWinnings;
						x = i;
						y = j;
						d = k;
					}
				}
			}
		}

		
		//changing byte direction to char direction
		char w = 'r';
		
		switch (d)
		{
		case 0:
			w = 'n';
			break;
		case 1:
			w = 's';
			break;
		case 2:
			w = 'e';
			break;
		case 3:
			w = 'w';
			break;
		}
			
		
		String str = "" + x + y + w;
		return str;
	}
	
	
	public byte calculateEnemyWinnings(DotBoard test)
	{
		byte winnings = 0;

		//play as if you were the enemy, being greedy, gathering as much as possible
		
		//find first 3square
		//just keep making chains (if you can create a 3 square, do it... and keep doing it)
		
		
		for (byte i = 0; i < width; i++)
		{
			for (byte j = 0; j < height; j++)
			{
				if (test.is3square(i, j))
				{
					byte x = i;
					byte y = j;
					do
					{
						byte openside = test.make4square(x,y);
						winnings++;
						switch (openside)
						{
							case 0:
								if (y-1 >= 0)
									y = (byte)(y-1);
								break;
							case 1:
								if (y+1 < height)
									y = (byte)(y+1);
								break;
							case 2:
								if (x+1 < width)
									x = (byte)(x+1);
								break;
							case 3:
								if (x-1 >= 0)
									x = (byte)(x-1);
								break;
						}
					} while (test.is3square(x,y));
					i = -1;
					break;
				}
			}
		}
		return winnings;
	}
	
	//extra ability to tell us which side was open
	public byte make4square(byte x, byte y)
	{
		for (byte i = 0; i < 4; i++)
		{
			if (!board[x][y][i])
			{
				switch (i)
				{
				case 0:
					mark(x,y,'n');
					break;
				case 1:
					mark(x,y,'s');
					break;
				case 2:
					mark(x,y,'e');
					break;
				case 3:
					mark(x,y,'w');
					break;
				}
				board[x][y][4] = true;
				return i;
			}
		}
		return -1;
	}
	
	//returns true if it successfully marks the side
	public boolean mark(byte x, byte y, char d)
	{
		boolean works = true;
		if (board[x][y][4])
		{
			return false;
		}
		switch (d)
		{
			case 'n':
				if (board[x][y][0])
				{
					works = false;
					break;
				}
				board[x][y][0] = true;
				markRedundantSides(x, y, (byte)0);
				break;
			case 's':
				if (board[x][y][1])
				{
					works = false;
					break;
				}
				board[x][y][1] = true;
				markRedundantSides(x, y, (byte)1);
				break;
			case 'e':
				if (board[x][y][2])
				{
					works = false;
					break;
				}
				board[x][y][2] = true;
				markRedundantSides(x, y, (byte)2);
				break;
			case 'w':
				if (board[x][y][3])
				{
					works = false;
					break;
				}
				board[x][y][3] = true;
				markRedundantSides(x, y, (byte)3);
				break;
		}
		if (is4square(x,y))
			board[x][y][4] = true;

			
		moves.add("" + x + " " + y + " " + d);

		return works;
	}
	public void markRedundantSides(byte x, byte y, byte d)
	{
		if (d == 0 && y-1 >= 0 && !board[x][y-1][1]) 
		{
			board[x][y-1][1] = true;
			if (is4square(x,(byte)(y-1)))
			{
				board[x][y-1][4] = true;
			}
		}
		else if (d == 1 && y+1 < height && !board[x][y+1][0])
		{
			board[x][y+1][0] = true;
			if (is4square(x,(byte)(y+1)))
			{
				board[x][y+1][4] = true;
			}
		}
		else if (d == 2 && x+1 < width && !board[x+1][y][3])
		{
			board[x+1][y][3] = true;
			if (is4square((byte)(x+1),y))
			{
				board[x+1][y][4] = true;
			}
		}
		else if (d == 3 && x-1 >= 0 && !board[x-1][y][2])
		{
			board[x-1][y][2] = true;
			if (is4square((byte)(x-1),y))
			{
				board[x-1][y][4] = true;
			}
		}
	}
	

	//returns true if can move again
	public boolean findGoodMove()
	{

		moves.clear();
		


		for (byte i = 0; i < width; i++)
		{
			for (byte j = 0; j < height; j++)
			{
				
				if (is3square(i, j))
				{

					DotBoard copy = this.copy();

					//in the end will hold the end of chain values
					byte x = i;
					byte y = j;
					
					//trails behind x,y
					byte a = i;
					byte b = j;
					
					//find the end of the chain
					while(copy.is3square(x,y)|| copy.is4square(x,y))
					{
						if (copy.is4square(x,y))
						{
							a = x;
							b = y;
							break;
						}
					
						byte openside = copy.findOpenSide(x,y);

						switch (openside)
						{
							case 0:
								copy.mark(x,y,'n');
								if (y-1 >= 0)
								{
									b = y;
									y = (byte)(y-1);
								}
								break;
							case 1:
								copy.mark(x,y,'s');
								if (y+1 < height)
								{
									b = y;
									y = (byte)(y+1);
								}
								break;
							case 2:
								copy.mark(x,y,'e');
								if (x+1 < width)
								{
									a = x;
									x = (byte)(x+1);
								}
								break;
							case 3:
								copy.mark(x,y,'w');
								if (x-1 >= 0)
								{
									a = x;
									x = (byte)(x-1);
								}
								break;
						}
					}
					
					x = a;
					y = b;
					
					byte m = i;
					byte n = j;
					
					//complete squares along the chain
						//then when they are neighbors, doublecross

					while (!isNeighbor(m,n,x,y))
					{
						byte s = findOpenSide(m,n);
						switch (s)
						{
						case 0:
							mark(m,n,'n');
							if (n-1 >= 0)
								n = (byte)(n-1);
							break;
						case 1:
							mark(m,n,'s');
							if (n+1 < height)
								n = (byte)(n+1);
							break;
						case 2:
							mark(m,n,'e');
							if (m+1 < width)
								m = (byte)(m+1);
							break;
						case 3:
							mark(m,n,'w');
							if (m-1 >= 0)
								m = (byte)(m-1);
							break;
						}
					}

					if (is3square(x,y))
					{
						make4square(x,y);
						return true;
					}
					

					//if there are still 3squares, complete this chain and keep searching
							//don't doublecross


					//still3squares if there is a 3 square somewhere else on the board
						//and you can doublecross with that 3square
					boolean still3squares = false;
					
					//isEndOfGame is true if this boxes in this chain are the only ones left incomplete
					boolean isEndOfGame = true;
					for (byte u = 0; u < width; u++)
					{
						for (byte v = 0; v < height; v++)
						{
							if (still3squares)
								break;
							if (u == m && v == n)
								continue;
							if (is3square(u,v))
							{
								byte w = findOpenSide(u,v);
								switch (w)
								{
								case 0:
									if (v-1 >= 0)
									{
										if (is2square(u,(byte)(v-1)))
											still3squares = true;
									}
									break;
								case 1:
									if (v+1 < height)
									{
										if (is2square(u,(byte)(v+1)))
											still3squares = true;
									}
									break;
								case 2:
									if (u+1 < width)
									{
										if (is2square((byte)(u+1),v))
											still3squares = true;
									}
									break;
								case 3:
									if (u-1 >= 0)
									{
										if (is2square((byte)(u-1),v))
											still3squares = true;
									}
									break;
								}
								
							}
							if (u == x && v == y)
								continue;
							if (!board[u][v][4])
								isEndOfGame = false;
						}
					}

					if (!isEndgame || still3squares || isEndOfGame)
					{
						make4square(m,n);
						make4square(x,y);
						return true;
					}		
					

					//****DOUBLECROSS****
					if (is2square(x,y))
					{			
					
						byte s = findOpenSide(m,n);

						byte q = -1;
						char w = 'b';
						switch (s)
						{
						case 0:
							q = 1;
							break;
						case 1:
							q = 0;
							break;
						case 2:
							q = 3;
							break;
						case 3:
							q = 2;
							break;
						}
						for (byte z = 0; z < 4; z++)
						{
							if (z != q && !board[x][y][z])
							{
								switch (z)
								{
								case 0:
									w = 'n';
									break;
								case 1:
									w = 's';
									break;
								case 2:
									w = 'e';
									break;
								case 3:
									w = 'w';
									break;
								}
							}
						}
						mark(x,y,w);
						
						return false;
					}
					
				}

			}
		}
	
		if (!isEndgame)
		{
			byte x = (byte)(Math.random()*width);
			
			for (byte i = x; i < width; i++)
			{
				for (byte j = 0; j < height; j++)
				{
					if (board[i][j][4])
					{

						continue;
					}
					if (is2square(i,j))
					{

						continue;
					}
					
					
					if (!board[i][j][0])
					{
						if (j-1 >= 0)
						{
							if (is0or1square(i, (byte)(j-1)))
							{
								mark(i,j,'n');
								return false;
							}
						}
						else
						{
							mark(i,j,'n');
							return false;
						}
						
					}

					if (!board[i][j][1])
					{
						if (j+1 < height)
						{
							if (is0or1square(i,(byte)(j+1)))
							{
								mark(i,j,'s');
								return false;
							}
						}
						else
						{
							mark(i,j,'s');
							return false;
						}
						
					}
					if (!board[i][j][2])
					{
						if (i+1 < width)
						{
							if (is0or1square((byte)(i+1), j))
							{
								mark(i,j,'e');
								return false;
							}
						}
						else
						{
							mark (i,j,'e');
							return false;
						}
					}
					if (!board[i][j][3])
					{
						if (i-1 >= 0)
						{
							if (is0or1square((byte)(i-1), j))
							{
								mark(i,j,'w');
								return false;
							}
						}
						else
						{
							mark(i,j,'w');
							return false;
						}
					}
					
					
				}
				
			}
			for (byte i = 0; i < x; i++)
			{
				for (byte j = 0; j < height; j++)
				{
					if (board[i][j][4])
						continue;
					if (is2square(i,j))
						continue;
				if (!board[i][j][0])
					{
						if (j-1 >= 0)
						{
							if (is0or1square(i, (byte)(j-1)))
							{
								mark(i,j,'n');
								return false;
							}
						}
						else
						{
							mark(i,j,'n');
							return false;
						}
						
					}

					if (!board[i][j][1])
					{
						if (j+1 < height)
						{
							if (is0or1square(i,(byte)(j+1)))
							{
								mark(i,j,'s');
								return false;
							}
						}
						else
						{
							mark(i,j,'s');
							return false;
						}
						
					}
					if (!board[i][j][2])
					{
						if (i+1 < width)
						{
							if (is0or1square((byte)(i+1), j))
							{
								mark(i,j,'e');
								return false;
							}
						}
						else
						{
							mark (i,j,'e');
							return false;
						}
					}
					if (!board[i][j][3])
					{

						if (i-1 >= 0)
						{
							if (is0or1square((byte)(i-1), j))
							{
								mark(i,j,'w');
								return false;
							}
						}
						else
						{
							mark(i,j,'w');
							return false;
						}
					}
					
					
				}
			}

		}
		
		isEndgame = true;
		

		String str = findShortestEnemyChain();

		byte x = (byte)Integer.parseInt(str.substring(0,1));
		byte y = (byte)Integer.parseInt(str.substring(1,2));
		char d = str.charAt(2);
		mark(x,y,d);
		return false;
		
	}
	
	public boolean is3square(byte x, byte y)
	{
		//search, then return true
		byte sides = 0;
		for (byte i = 0; i < 4; i++)
		{
			if (board[x][y][i])
			{
				sides++;
			}
		}
	
		if (sides == 3)
		{
			return true;
		}
		
		return false;
	}
	public boolean is2square(byte x, byte y)
	{
		//search, then return true
		byte sides = 0;
		for (byte j = 0; j < 4; j++)
		{
			if (board[x][y][j])
			{
				sides++;
			}
		}
		if (sides == 2)
		{
			return true;
		}
		
		return false;
	}
	public boolean is0or1square(byte x, byte y)
	{
		byte sides = 0;
		for (byte k = 0; k < 4; k++)
		{
			if (board[x][y][k])
				sides++;
		}
		if (sides < 2)
		{
			return true;
		}
		return false;
	}
	

	public boolean isNeighbor(byte a, byte b, byte x, byte y)
	{
		if (a == x && b == y)
			return true;
		if (b == y)
		{
			//if x is east of a
			if ((x-a) == 1)
			{
				if (!board[a][b][2])
					return true;
			}
			//if x is west of a
			if ((x-a) == -1)
			{
				if (!board[a][b][3])
					return true;
			}
		}
		if (a == x)
		{
			//if y is north of b
			if ((y-b) == -1)
			{
				if (!board[a][b][0])
					return true;
			}
			//if y is south of b
			if ((y-b) == 1)
			{
				if (!board[a][b][1])
					return true;
			}
		}

		return false;
	}

	public byte findOpenSide(byte x, byte y)
	{
		for (byte i = 0; i < 4; i++)
		{
			if (!board[x][y][i])
			{
				return i;
			}
		}
		
		return -1;
	}
	
	
	public boolean is4square(byte x, byte y)
	{
		return (board[x][y][0] && board[x][y][1] && board[x][y][2] && board[x][y][3]);
	}
	
}

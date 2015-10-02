import java.util.*;

public class DotGame {

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		
		DotBoard game = new DotBoard(in.nextInt(), in.nextInt());
		in.nextLine();
		
		ArrayList<String> opponentMoves = new ArrayList<String>();
		

		while (true)
		{
			String str = in.nextLine();

			//must be the start of the game
			if (str.length() == 0)
			{

				game.findGoodMove();
				
				ListIterator<String> i = game.moves.listIterator();

				while (i.hasNext())
				{
					String s = i.next();

					System.out.println("" + s);

				}

				continue;
			}
			

			
			
			
			
			
			while(str.contains("|"))
			{
				int x = str.indexOf('|');
				opponentMoves.add(str.substring(0,x));
				str = str.substring(x+1);
			}
			opponentMoves.add(str);
			
			ListIterator<String> i = opponentMoves.listIterator();
			
			while (i.hasNext())
			{
				String s = i.next();
				
				byte x = (byte)Integer.parseInt("" + s.charAt(0));
				byte y = (byte)Integer.parseInt("" + s.charAt(2));
				char d = s.charAt(4);
				
				game.mark(x, y, d);
			}

			while (game.findGoodMove())
			{
				ListIterator<String> j = game.moves.listIterator();
				while (j.hasNext())
				{
					System.out.println(j.next());
					in.nextLine();
				}
			}
			
			ListIterator<String> j = game.moves.listIterator();

			String s = j.next();

			System.out.println("" + s);


			while (j.hasNext())
			{
				in.nextLine();
				s = j.next();
				System.out.println("" + s);

			}

			opponentMoves.clear();
			
		}
	}
}
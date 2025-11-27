// Nadia Iskandar
// TTT CP 1
public class TTT {
     public static Player FIRST = Player.X;

        public static void main(String[] args) {
            Board board = Board.valueOf(args[0]);

            Player turn = board.turn(FIRST);
            if (turn == Player.EMPTY) System.out.println("Invalid board");
            else System.out.println(turn + " turn");

            board.print();





        }



}

// Nadia Iskandar
// TTT CP 3
import java.util.Scanner;
public class Main {

	private static final Scanner scanner = new Scanner(System.in);
    private static final Board[] boards = new Board[32];
    private static int current = 0;

    private static class Redo extends Exception {}
    private static class Undo extends Exception {}
    public static class Done extends Exception {}

    private static boolean isValid(int coordinate) {
        return coordinate >= 0 && coordinate <= 3;
    }

    private static int getCoordinate(String prompt) throws Redo, Undo, Done {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            switch (s.toLowerCase()) {
                case "":
                case "redo": // Restart entering this move
                    throw new Redo();

                case "undo": // Go back to previous board position
                    throw new Undo();

                case "done":
                case "exit": // Quit the program
                    throw new Done();

                default:
                    try {
                        int value = Integer.parseInt(s);
                        if (isValid(value)) return value;
                    } catch (NumberFormatException e) {
                    }
                    System.err.println("Invalid coordinate: " + s);
            }
        }
    }

    public static Board getYourMove(Board board) throws Done {

        // Prompt the user to enter a move as XYZ coordinates
        // Check the entry for validity (retry if invalid)
        // Returns an updated board with the user's move.

        // Entering a blank line or "redo" for any coordinate
        // will restart the with entering the X coordinate.

        // Entering "undo" will back up the game state to
        // the previous move entered by the user.

        // Entering "done" will quit the game.

        boards[current++] = board;
        System.out.println("Your move");
        while (true) {
            try {
                int x = getCoordinate("Enter X: ");
                int y = getCoordinate("Enter Y: ");
                int z = getCoordinate("Enter Z: ");
                int position = Coordinate.position(x, y, z);
                if (board.isEmpty(position)) return board.next(position);
                System.err.println("Position is not empty");

            } catch (Redo e) {
                // Try again

            } catch (Undo e) {
                // Revert to previous board configuration
                if (current > 0) {
                    current = current -2;
                    board = boards[current];
                    board.print();

                } else {
                    System.err.println("Start of game:");
                    board.print();
                }
            }
        }
    }

private static int minimax(Board board, int plies, int alpha, int beta, Player maximizingPlayer, Player minimizingPlayer, boolean isMax) {
    if (plies == 0 || board.done()) {
        return board.evaluate(maximizingPlayer);
    }

    if (isMax) {
        int maxEval = Integer.MIN_VALUE;
        for (int move : board.getAvailableMoves()) {
            Board newBoard = board.makeMove(move, maximizingPlayer);
            int eval = minimax(newBoard, plies - 1, alpha, beta, maximizingPlayer, minimizingPlayer, false);
            maxEval = Math.max(maxEval, eval);
            alpha = Math.max(alpha, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return maxEval;
    } else {
        int minEval = Integer.MAX_VALUE;
        for (int move : board.getAvailableMoves()) {
            Board newBoard = board.makeMove(move, minimizingPlayer);
            int eval = minimax(newBoard, plies - 1, alpha, beta, maximizingPlayer, minimizingPlayer, true);
            minEval = Math.min(minEval, eval);
            beta = Math.min(beta, eval);
            if (beta <= alpha) {
                break;
            }
        }
        return minEval;
    }
}


    private static int getMyMove(Board board, int plies, Player computerPlayer, Player humanPlayer) {
        if(board.isEmpty(0)) return 0;
        // array thingy where you randomize the "best" moves
        int[] bestMoves = new int[64];
        int countArray = 0;
        int bestValue = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int move : board.getAvailableMoves()) {
            Board newBoard = board.makeMove(move, computerPlayer);
            int moveValue = minimax(newBoard, plies - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, computerPlayer, humanPlayer, false);
            if (moveValue > bestValue) {
                bestValue = moveValue;
               // bestMove = move;
                bestMoves = new int[64];
                countArray = 0;
                bestMoves[countArray++] = move;
            }else if(moveValue == bestValue){
                bestMoves[countArray++] = move;

            }
        }
        int random = (int) (Math.random() * countArray);
        return bestMoves[random];
    }

    public static void main(String[] args) {
        Board board = new Board();
		boolean first = true;
        int plies = 2;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-first":
                    first = true;
                    break;

                case "-second":
                    first = false;
                    break;

                case "-plies":
                    try {
                        arg = args[++i];
                        plies = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for -plies: " + arg);
                    } catch (IndexOutOfBoundsException e) {
                        System.err.println("No value for -plies");
                    }
                    break;

                default:
                    System.err.println("Invalid option: " + arg);
            }
        }

		boolean me = first;

        Player computerPlayer = first ? Player.X : Player.O;
        Player humanPlayer = computerPlayer.other();

        do {
            if (me) {
                int position = getMyMove(board, plies,  computerPlayer,  humanPlayer);
                board = board.next(position);
                System.out.println("My move: " + Coordinate.toString(position));
                board.print();
            } else {
                try {
                    board = getYourMove(board);
                    board.print();
                } catch (Done e) {
                    return; // Game abandonned
                }
            }
			me = !me;
        } while (!board.done());

        switch (board.winner()) {
            case X:
                System.out.println(first ? "I won" : "You won");
                System.out.println(board.winningLine(Player.X).name());
                break;

            case O:
                System.out.println(first ? "You won" : "I won");
                System.out.println(board.winningLine(Player.O).name());
                break;

            default:
                System.out.println("Tie");
                break;
        }
    }
}





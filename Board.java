// Nadia Iskandar
// TTT CP 3
import java.util.ArrayList;
import java.util.List;

public class Board {
    // bit.set(0, coordinate.position(x,y,x);
    // corners and centers
    // hard code
    // move ordering (alpha beta pruning)


    public static final int N = Coordinate.N;
    long boardX;
    long boardO;

    public Board() {
        this.boardX = 0L;
        this.boardO = 0L;
    }

    public Board(long boardX, long boardO){
        this.boardX = boardX;
        this.boardO = boardO;
    }

    public Player get(int position) {
        if (Bit.isSet(this.boardX, position)) return Player.X;
        if (Bit.isSet(this.boardO, position)) return Player.O;
        return Player.EMPTY;
    }

    public Player get(int x, int y, int z) {
        return this.get(Coordinate.position(x,y,z));
    }

    public void set(int position, Player player) {
        if (player == Player.X){
            boardX = Bit.set(boardX, position);
        } else {
            boardO = Bit.set(boardO, position);
        }
    }

    public boolean isEmpty(int position){
        if (this.get(position) == Player.EMPTY){
            return true;
        }
        return false;
    }

    public ArrayList<Integer> getAvailableMoves(){
        ArrayList<Integer> positions = new ArrayList<>();
        long combine = (boardX | boardO);
        for(int i = 0; i < 64; i++){
            if(!Bit.isSet(combine, i))  positions.add(i);
        }

        return positions;

    }

    public boolean done(){
        return this.winner() != null;
    }

    public Player turn(Player first){
        int numX = Bit.countOnes(boardX);
        int numO = Bit.countOnes(boardO);

        if(first.equals(Player.O)){
            numX = Bit.countOnes(boardO);
            numO = Bit.countOnes(boardX);
        }
        if(numO - numX == 0) return first;
        if(numX ==  numO + 1) return first.other();
        return Player.EMPTY;

    }



    public Board makeMove(int position, Player player) {
        Board newBoard = new Board(boardX, boardO);
        newBoard.set(position, player);
        return newBoard;

    }


    public Board next(int position){
        Player player_turn = this.turn();
        Board next = new Board(this.boardX, this.boardO);
        next.set(position, player_turn);
        return next;
    }


    public Player turn(){
        int difference = Bit.countOnes(boardX) - Bit.countOnes(boardO);
        if (difference == 0 ){
            return Player.X;
        }
        if (difference == 1){
            return Player.O;
        }

        return null;
    }

    public Player winner() {
        for (Line line : Line.lines) {
            if (Bit.containsLine(boardX, line)) {
                return Player.X;
            }
            if (Bit.containsLine(boardO, line)) {
                return Player.O;
            }
        }
        return null; //Returning this means that neither has won.
    }

    public Line winningLine(Player player){
        long board = 0L;
        if (player == Player.X) board = boardX;
        if (player == Player.O) board = boardO;
        for (Line line : Line.lines){
            if (Bit.containsLine(board, line)) return line;
        }
        return null;
    }
    
    // Construct a Board from a string representation.
    // Should be an inverse function of toString().

    public static Board valueOf(String s) {
        Board board = new Board();
        int position = 0;

        for (int i= 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case 'x':
                case 'X':
                    board.set(position++, Player.X);
                    break;

                case 'o':
                case 'O':
                    board.set(position++, Player.O);
                    break;

                case '.':
                    position++;
                    break;

                case ' ':
                case '|':
                    break;

                default:
                    throw new IllegalArgumentException("Invalid player: " + c);
            }
        }
        return board;
    }


    // Image & printing functions.

    @Override
    public String toString() {
        String result = "";
        String separator = "";

        for (int position = 0; position < 64; position++) {
            result += separator;
            result += this.get(position).toString();
            if (position % 16 == 0) {
                separator = " | ";
            } else if (position % 4 == 0) {
                separator = " ";
            } else {
                separator = "";
            }
        }
        return result;
    }


    public void print() {
        for (int y = N-1; y >= 0; y--) {
            for (int z = 0; z < N; z++) {
                for (int x = 0; x < N; x++) {
                    System.out.print(this.get(x, y, z));
                }
                System.out.print("    ");
            }
            System.out.println();
        }
    }

    public int evaluate(Player player) {
        // check if board is empty or only have a few moves then put a corner
        // move pick a random square among the ones with the highest
        // number of intersecting lines. corners and central squares.
        // seed it


        Player opponent = player.other();
        int score = 0;
        // check if a player has won

        //counting 2 in a rows
        // make sure 2 two-in-a-rows is less than 1 three in a row
        // if there is a bunch of three in a rows
        // if we have a three in a row and the other has 3 in a row
        // whose ever turn it is who has three in a row
        // check whose turn it in. instead of returning a single number, return a pair of numbers and the player (maxi max function)
        // wrap around. make sure max score value does not overload

        // checking forks
        // check if the two in a row lines intersect, it is a forced win

        // four in a row
        int opponentWin = countInARow(opponent, 4);
        if (opponentWin > 0) {
            return Integer.MIN_VALUE;
        }

        int playerWin = countInARow(opponent, 4);
        if (playerWin > 0) {
            return Integer.MAX_VALUE;
        }

        // count 2 in a row
        int opponentTwoMoveWins = countInARow(opponent, 2);
        if (opponentTwoMoveWins > 0) {
            score -= 10 * opponentTwoMoveWins;
        }

        int playerTwoMoveWins = countInARow(player, 2);
        if (playerTwoMoveWins > 0) {
            score += 10 * playerTwoMoveWins;
        }

        // count 3 in a rows --> make sure weighting is enough
        int opponentThreeMoveWins = countInARow(opponent, 3);
        if (opponentTwoMoveWins > 0) {
            score -= 100 * opponentThreeMoveWins;
        }

        int playerThreeMoveWins = countInARow(player, 3);
        if (playerTwoMoveWins > 0) {
            score += 100 * playerThreeMoveWins;
        }

        int countForks = countForks(player);
        if(countForks > 0){
            score += 1000 * countForks;
        }

        int countForksOther = countForks(opponent);
        if(countForks > 0){
            score -= 1000 * countForksOther;
        }

        return score;
    }

    public int countForks(Player player){
        int countForks = 0;
        for(int position: getAvailableMoves()){
            Board newBoard = this.makeMove(position, player);
            int winningLines = newBoard.countInARow(player, 3);

            if(winningLines > 1) countForks++;

        }

        return countForks;

    }







    public int countInARow(Player player, int num) { // unblocked 2 in a rows
        int countInARow = 0;
        boolean opponentMarker = false;

        for (Line line : Line.lines) {
            int playerMarkers = 0;
            int emptyMarkers = 0;

            // Iterate through positions in the line
            for (int position : Bit.ones(line.positions())) {
                Player current = get(position);

                if(current == player.other()){
                    opponentMarker = true;
                    break;
                }
                if (current == player) {
                    playerMarkers++;
                } else if (current == Player.EMPTY) {
                    emptyMarkers++;
                }
            }

            // Check if the line has exactly two markers for the player and the rest are empty
            //!opponentMarker &&
            if ((playerMarkers == num && emptyMarkers == Coordinate.N - num)) {
                countInARow++;
            }
        }

        return countInARow;
    }


}

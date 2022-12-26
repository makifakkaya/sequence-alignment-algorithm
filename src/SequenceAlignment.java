class ScoreNode {
    double i; char c; boolean isOpened;
    public ScoreNode(double i, char c, boolean isOpened) {
        this.i = i; this.c = c; this.isOpened = isOpened;
    }
}

public class SequenceAlignment {
    private static final double MATCH_SCORE = 3;
    private static final double MISMATCH_SCORE = -1;
    private static final double GAP_OPENING_PENALTY = -1;
    private static final double GAP_EXTENSION_PENALTY = -0.5;
    public static ScoreNode[][] align(char[] sequence1, char[] sequence2) {
        int m = sequence1.length;
        int n = sequence2.length;

        // Initialize the score matrix
        ScoreNode[][] score = new ScoreNode[m + 1][n + 1];
        score[0][0] = new ScoreNode(0.0, '-', false);
        score[0][1] = new ScoreNode(-1.0,'l', true);
        score[1][0] = new ScoreNode(-1.0, 'u', true);

        for (int i = 1; i < m; i++) {
            score[i+1][0] = new ScoreNode(score[i][0].i + GAP_EXTENSION_PENALTY, 'u', true);
        }
        for (int j = 1; j < n; j++) {
            score[0][j+1] = new ScoreNode(score[0][j].i + GAP_EXTENSION_PENALTY, 'l', false);
        }

        // Fill in the score matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                double match = (score[i - 1][j - 1].i + (sequence1[i - 1] == sequence2[j - 1] ? MATCH_SCORE : MISMATCH_SCORE));
                double delete = (!score[i - 1][j].isOpened) ? score[i-1][j].i + GAP_OPENING_PENALTY : score[i-1][j].i + GAP_EXTENSION_PENALTY;
                double insert = (!score[i][j - 1].isOpened) ? score[i][j-1].i + GAP_OPENING_PENALTY : score[i][j-1].i + GAP_EXTENSION_PENALTY;
                double nextScore = Math.max(Math.max(match, delete), insert);
                boolean isOpened = match != nextScore || score[i - 1][j - 1].isOpened;
                char nextMovement = (match == nextScore) ? 'x' : (delete == nextScore) ? 'u' : 'l';
                score[i][j] = new ScoreNode(nextScore, nextMovement, isOpened);
            }
        }



        return score;
    }

    public static void main(String[] args) {

        char[] sequence1 = "ATCGTCC".toCharArray();
        char[] sequence2 = "ATGTTATA".toCharArray();
        ScoreNode[][] score = align(sequence1, sequence2);
        String[] alignment = findAlignment(sequence1, sequence2, score);

        // Alignment Result
        System.out.println(alignment[0]);
        System.out.println(alignment[1]);

        // Print the score matrix
        /*for (ScoreNode[] scoreNodes : score) {
            for (ScoreNode scoreNode : scoreNodes) {
                System.out.print(scoreNode.i + " ");
            }
            System.out.println("");
        }*/

        // The optimal alignment score is the bottom-right element of the score matrix
        System.out.println("Optimal alignment score: " + score[sequence1.length][sequence2.length].i);
    }

    private static String[] findAlignment(char[] sequence1, char[] sequence2, ScoreNode[][] score) {

        int x = sequence1.length;
        int y = sequence2.length;
        ScoreNode node = score[x][y];

        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();

        while (node.c != '-'){
            switch (node.c) {
                case 'l' -> {
                    y--;
                    align1.insert(0, "-");
                    align2.insert(0, sequence2[y]);
                    node = score[x][y];
                }
                case 'u' -> {
                    x--;
                    align2.insert(0, "-");
                    align1.insert(0, sequence1[x]);
                    node = score[x][y];
                }
                case 'x' -> {
                    x--;
                    y--;
                    align1.insert(0, sequence1[x]);
                    align2.insert(0, sequence2[y]);
                    node = score[x][y];
                }
                default -> {
                }
            }
        }

        return new String[]{align1.toString(), align2.toString()};
    }
}
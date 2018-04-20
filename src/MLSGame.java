public class MLSGame {

    // Outcomes
    private int result;
    private int homeGoals;
    private int awayGoals;

    // Expected goals
    private double homeExpectGoalsTeam;
    private double homeExpectGoalsPlayers;
    private double awayExpectGoalsTeam;
    private double awayExpectGoalsPlayers;
    private double PH;
    private double PD;
    private double PA;
    private double MaxH;
    private double MaxD;
    private double MaxA;
    private double AvgH;
    private double AvgD;
    private double AvgA;

    public MLSGame( double[] metrics, int[] outcomes ) {

        // Assign metrics
        homeExpectGoalsTeam = metrics[0];
        homeExpectGoalsPlayers = metrics[1];
        awayExpectGoalsTeam = metrics[2];
        awayExpectGoalsPlayers = metrics[3];
        PH = metrics[4];
        PD = metrics[5];
        PA = metrics[6];
        MaxH = metrics[7];
        MaxD = metrics[8];
        MaxA = metrics[9];
        AvgH = metrics[10];
        AvgD = metrics[11];
        AvgA = metrics[12];

        // Assign outocomes
        result = outcomes[0];
        homeGoals = outcomes[1];
        awayGoals = outcomes[2];

    }

    public double[] getMetrics() {
        return new double[]{ homeExpectGoalsTeam,
                            homeExpectGoalsPlayers,
                            awayExpectGoalsTeam,
                            awayExpectGoalsPlayers,
                            PH,
                            PD,
                            PA,
                            MaxH,
                            MaxD,
                            MaxA,
                            AvgH,
                            AvgD,
                            AvgA };
    }
    public int getResult() {
        return result;
    }
    public int[] getOutcomes() {
        return new int[]{ result,
                           homeGoals,
                           awayGoals };

    }
    public double getAvgH() {
        return AvgH;
    }
    public double getAvgD() {
        return AvgD;
    }
    public double getAvgA() {
        return AvgA;
    }

}

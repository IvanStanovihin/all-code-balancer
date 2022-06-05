package nightcode.stanovihin.balancer.model;

import java.util.ArrayList;

public class AllStatistic {

    private ArrayList<IntervalStatistic> data = new ArrayList<>();

    public AllStatistic(){}

    public ArrayList<IntervalStatistic> getData() {
        return data;
    }

    public void setData(ArrayList<IntervalStatistic> data) {
        this.data = data;
    }

    public void addIntervalStatistic(IntervalStatistic intervalStatistic){

        Long start = intervalStatistic.getStart();
        Long end = intervalStatistic.getEnd();
        Long votes = intervalStatistic.getVotes();
        for (IntervalStatistic intervalStat : data){
            if (intervalStat.getStart().equals(start) && intervalStat.getEnd().equals(end)){
                Long intervalStatVotes = intervalStat.getVotes();
                intervalStat.setVotes(intervalStatVotes + votes);
            }
        }
    }
}


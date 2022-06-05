package nightcode.stanovihin.balancer.model;

import java.util.Arrays;

public class StatisticResponse {

   private IntervalStatistic[] data;

    public StatisticResponse(IntervalStatistic[] data) {
        this.data = data;
    }

    public IntervalStatistic[] getData() {
        return data;
    }

    public void setData(IntervalStatistic[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StatisticResponse{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}

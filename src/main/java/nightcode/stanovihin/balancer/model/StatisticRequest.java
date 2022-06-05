package nightcode.stanovihin.balancer.model;

public class StatisticRequest {

    private Long from;
    private Long to;
    private Long intervals;
    private String artists;

    public StatisticRequest(Long from, Long to, Long intervals, String artists) {
        this.from = from;
        this.to = to;
        this.intervals = intervals;
        this.artists = artists;
    }

    public String getPostUrl(){
        return "?from=" + from +
        "&to=" + to +
        "&intervals=" + intervals +
        "&artists=" + artists.replaceAll("\\s", "%20");
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public Long getIntervals() {
        return intervals;
    }

    public void setIntervals(Long intervals) {
        this.intervals = intervals;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }
}

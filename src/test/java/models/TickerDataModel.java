package models;

public class TickerDataModel {
    private String bestAsk;
    private String bestAskSize;
    private String bestBid;
    private String bestBidSize;
    private String price;
    private String sequence;
    private String size;
    private long time;

    public String getBestAsk() {
        return bestAsk;
    }

    public void setBestAsk(String bestAsk) {
        this.bestAsk = bestAsk;
    }

    public String getBestAskSize() {
        return bestAskSize;
    }

    public void setBestAskSize(String bestAskSize) {
        this.bestAskSize = bestAskSize;
    }

    public String getBestBid() {
        return bestBid;
    }

    public void setBestBid(String bestBid) {
        this.bestBid = bestBid;
    }

    public String getBestBidSize() {
        return bestBidSize;
    }

    public void setBestBidSize(String bestBidSize) {
        this.bestBidSize = bestBidSize;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public TickerDataModel() {
    }

    public TickerDataModel(String bestAsk, String bestAskSize, String bestBid, String bestBidSize, String price, String sequence, String size, long time) {
        this.bestAsk = bestAsk;
        this.bestAskSize = bestAskSize;
        this.bestBid = bestBid;
        this.bestBidSize = bestBidSize;
        this.price = price;
        this.sequence = sequence;
        this.size = size;
        this.time = time;
    }
}

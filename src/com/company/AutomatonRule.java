package com.company;

/**
 * Created by Michaela on 27.4.2018.
 */
public class AutomatonRule {
    private String from;
    private String to;
    private String on;

    public AutomatonRule() {
    }

    public AutomatonRule(String from, String to, String on) {
        this.from = from;
        this.to = to;
        this.on = on;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }
}

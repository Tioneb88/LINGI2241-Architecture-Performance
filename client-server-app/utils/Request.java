package utils;

import java.util.Date;
import java.io.Serializable;


public class Request implements Serializable {

    private String reqValue;
    private String respValue;
    private Date sentByClient;
    private Date receivedByClient;
    private Date startTreatTime;
    private Date endTreatTime;
    private Date startWaitTime;
    private Date endWaitTime;


    public Request(String value) {
        this.reqValue = value;
    }

    public String getReqValue() {
        return reqValue;
    }

    public void setResponseValue(String value) {
        this.respValue = value;
    }

    public void startWait(Date time) {
        this.startWaitTime = time;
    }

    public void endWait(Date time) {
        this.endWaitTime = time;
    }

    public void startTreat(Date time) {
        this.startTreatTime = time;
    }

    public void endTreat(Date time) {
        this.endTreatTime = time;
    }

    public void setSentByClient(Date sentByClientTime) {
        this.sentByClient = sentByClientTime;
    }

    public Date getSentByClient() {
        return sentByClient;
    }

    public Date getReceivedByClientTime() {
        return receivedByClient;
    }

    public void setReceivedByClientTime(Date receivedByClientTime) {
        this.receivedByClient = receivedByClientTime;
    }

    public long waitTime() {
        return this.endWaitTime.getTime() - this.startWaitTime.getTime();
    }

    public long treatTime() {
        return this.endTreatTime.getTime() - this.startTreatTime.getTime();
    }

}
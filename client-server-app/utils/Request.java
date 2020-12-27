package utils;

import java.util.Date;
import java.io.Serializable;

/*
 * Small class to manage the requests.
 */
public class Request implements Serializable {

    private String value;
    private Date startWaitTime;
    private Date endWaitTime;
    private Date startTreatTime;
    private Date endTreatTime;
    private Date sentByClient;

    /*
     * Constructs an object Request with a String value.
     * @param value : the String contained in the request
     * @return None
     */
    public Request(String value) {
        this.value = value;
    }

    /*
     * Returns the String value of the request.
     * @param None
     * @return value : the String contained in the request
     */
    public String getValue() {
        return value;
    }

    /*
     * Sets the time when the request started to wait in the queue.
     * @param time : the start time
     * @return None
     */
    public void startWait(Date time) {
        this.startWaitTime = time;
    }

    /*
     * Sets the time when the request stopped to wait in the queue.
     * @param time : the end time
     * @return None
     */
    public void endWait(Date time) {
        this.endWaitTime = time;
    }

    /*
     * Sets the time when the request started to be processed by the service station.
     * @param time : the start time
     * @return None
     */
    public void startTreat(Date time) {
        this.startTreatTime = time;
    }

    /*
     * Sets the time when the request stopped to be processed by the service station.
     * @param time : the end time
     * @return None
     */
    public void endTreat(Date time) {
        this.endTreatTime = time;
    }

    /*
     * Sets the time when the request has been sent by the client.
     * @param time : the time
     * @return None
     */
    public void setSentByClient(Date time) {
        this.sentByClient = time;
    }

    /*
     * Gets the time when the request has been sent by the client.
     * @param None
     * @return time : the time
     */
    public Date getSentByClient() {
        return sentByClient;
    }

    /*
     * Returns the time spent by the request in the queue.
     * @param None
     * @return time : the time spent in the queue
     */
    public long waitTime() {
        return this.endWaitTime.getTime() - this.startWaitTime.getTime();
    }

    /*
     * Returns the time spent by the request in the service station.
     * @param None
     * @return time : the time spent in the service station
     */
    public long treatTime() {
        return this.endTreatTime.getTime() - this.startTreatTime.getTime();
    }

}
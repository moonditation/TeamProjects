package com.example.myapplication.adapter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Promise {

    private Date promiseDate;
    private double promiseLatitude;
    private double promiseLongitude;
    private String promiseName;
    private String promiseUid;

    public  Promise(String promiseName, String promiseUid){
        this.promiseUid = promiseUid;
        this.promiseName = promiseName;
    }

    // 생성자
    public Promise(Date promiseDate, double promiseLatitude, double promiseLongitude, String promiseName, String promiseUid) {
        this.promiseDate = promiseDate;
        this.promiseLatitude = promiseLatitude;
        this.promiseLongitude = promiseLongitude;
        this.promiseName = promiseName;
        this.promiseUid = promiseUid;
    }

    // Getter 및 Setter 메서드
    public Date getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(Date promiseDate) {
        this.promiseDate = promiseDate;
    }

    public double getPromiseLatitude() {
        return promiseLatitude;
    }

    public void setPromiseLatitude(double promiseLatitude) {
        this.promiseLatitude = promiseLatitude;
    }

    public double getPromiseLongitude() {
        return promiseLongitude;
    }

    public void setPromiseLongitude(double promiseLongitude) {
        this.promiseLongitude = promiseLongitude;
    }

    public String getPromiseName() {
        return promiseName;
    }

    public void setPromiseName(String promiseName) {
        this.promiseName = promiseName;
    }
    public String getPromiseUid() {
        return promiseUid;
    }

    public void setPromiseUid(String promiseUid) {
        this.promiseUid = promiseUid;
    }

    @Override
    public String toString() {
        return "Promise{" +
                "promiseDate=" + promiseDate +
                ", promiseLatitude=" + promiseLatitude +
                ", promiseLongitude=" + promiseLongitude +
                ", promiseName='" + promiseName + '\'' +
                '}';
    }

}

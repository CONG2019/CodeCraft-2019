package com.huawei;

public class Road {
    // 类型直接定义为public
    public Road(String[] roadStr){
        id_ = Integer.parseInt(roadStr[0]);
        length_ = Integer.parseInt(roadStr[1]);
        speed_ = Integer.parseInt(roadStr[2]);
        channel_ = Integer.parseInt(roadStr[3]);
        from_ = Integer.parseInt(roadStr[4]);
        to_ = Integer.parseInt(roadStr[5]);
        isDuplex_ = Integer.parseInt(roadStr[6]);
    }
    public int id_;
    public int length_;
    public int speed_;
    public int channel_;
    public int from_;
    public int to_;
    public int isDuplex_;
}

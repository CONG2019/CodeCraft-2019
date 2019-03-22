package com.huawei;

public class Road implements Comparable<Road>{
    // 类型直接定义为public
    public Road(String[] roadStr){
        id_ = Integer.parseInt(roadStr[0]);
        length_ = Integer.parseInt(roadStr[1]);
        speed_ = Integer.parseInt(roadStr[2]);
        channel_ = Integer.parseInt(roadStr[3]);
        from_ = Integer.parseInt(roadStr[4]);
        to_ = Integer.parseInt(roadStr[5]);
        isDuplex_ = Integer.parseInt(roadStr[6]);
        roadLength_ = length_/(speed_+channel_) + 1;
    }
    public int id_;
    public int length_;
    public int speed_;
    public int channel_;
    public int from_;
    public int to_;
    public int isDuplex_;
    // 路的长度除以速度表示路径的长度更合理。
    public int roadLength_;

    // 重写比较算法
    @Override
    public int compareTo(Road road){
        return roadLength_ - road.roadLength_;
    }
}

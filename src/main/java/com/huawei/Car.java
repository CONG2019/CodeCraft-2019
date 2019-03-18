package com.huawei;

// car类实现Comparable接口，使得排序按照出发时间来进行。
public class Car implements Comparable<Car> {
    // 类型直接定义为public
    public Car(String[] carStr){
        id_ = Integer.parseInt(carStr[0]);
        from_ = Integer.parseInt(carStr[1]);
        to_ = Integer.parseInt(carStr[2]);
        speed_ = Integer.parseInt(carStr[3]);
        planTime_ = Integer.parseInt(carStr[4]);
    }
    public int id_;
    public int from_;
    public int to_;
    public int speed_;
    public int planTime_;

    // 排序函数
    @Override
    public int compareTo(Car car) {
        return planTime_-car.planTime_;
    }
}

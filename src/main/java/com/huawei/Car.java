package com.huawei;

import java.util.LinkedList;

// car类实现Comparable接口，使得排序按照出发时间来进行。
public class Car implements Comparable<Car> {
    // 类型直接定义为public
    public Car(String[] carStr){
        id_ = Integer.parseInt(carStr[0]);
        from_ = Integer.parseInt(carStr[1]);
        to_ = Integer.parseInt(carStr[2]);
        speed_ = Integer.parseInt(carStr[3]);
        planTime_ = Integer.parseInt(carStr[4]);
        priority_ = Integer.parseInt(carStr[5]);
        preset_ = Integer.parseInt(carStr[6]);
        state_ = -1;
        path_ = new LinkedList<>();
        FinishTime_ = 0;
//        row_ = new Integer(0);
//        col_ = new Integer(0);
    }
    public int id_;
    public int from_;
    public int to_;
    public int speed_;
    public int planTime_;
    // 新增两个属性，分别是优先车辆和预放置。
    public int priority_;
    public int preset_;
    /*
    @int state_: 状态标记属性,0:终止态,1：等待态,2：完成态，-1：等待上路
     */
    public int state_;

    public int StartTime_;
    public int FinishTime_;
    //增加一个队列，保存车的行驶路劲，队头就是车所在位置
    public LinkedList<Road> path_;
    //添加保存车位置信息的的两个变量
    public int row_;
    public int col_;
    // 排序函数
    @Override
    public int compareTo(Car car) {
        if(car.speed_ == speed_){
            if(planTime_ == car.planTime_){
                return car.id_ - id_;
            }
            return planTime_ - car.planTime_;
        }
        return car.speed_ - speed_;
        //return planTime_-car.planTime_;
        //return car.planTime_ - planTime_;
        //return from_-car.from_;
    }

}

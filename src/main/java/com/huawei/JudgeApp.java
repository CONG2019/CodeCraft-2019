package com.huawei;

import edu.princeton.cs.algs4.In;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class JudgeApp {
    private AllCar allCar;
    private AllRoad allRoad;
    private AllCross allCross;
    private ArrayList<ArrayList<Integer>> answer;
    //private Scheduler scheduler;
    private PresetAnswer presetAnswer;
    private Graph graph;
    private int time_;
    private boolean deakLock_;
    private int allcarStarPoint;
    private int allcarFinshPoint;
    private int PriorityStartPoint;
    private int PriorityFinshPoint;



    public JudgeApp(AllCar allCar, AllRoad allRoad, AllCross allCross, ArrayList<ArrayList<Integer>> allAnswer, PresetAnswer presetAnswer, Graph graph) {
        this.allCar = allCar;
        this.allRoad = allRoad;
        this.allCross = allCross;
        this.answer= new ArrayList<>();
        answer.addAll(allAnswer);
        this.presetAnswer = presetAnswer;
        this.graph = graph;
        time_ = 0;
        deakLock_ = false;
    }

    public void Init() {
        //将车辆路径加载到车辆对象
        //先将预置车路径和答案合并一下
        answer.addAll(presetAnswer.presetAnswer_);
        for (ArrayList<Integer> path_ : answer
        ) {
            Car car = allCar.carsMap_.get(path_.get(0));
            car.StartTime_ = path_.get(1);
            //对车的路径进行预处理
            if (car.from_ == allRoad.roadsMap_.get(path_.get(2)).from_) {
                car.path_.add(allRoad.roadsMap_.get(path_.get(2)));
            } else if (car.from_ == allRoad.roadsMap_.get(-path_.get(2)).from_) {
                car.path_.add(allRoad.roadsMap_.get(-path_.get(2)));
            } else {
                System.out.println("car:" + car.id_ + "路径不合法");
            }
            for (int i = 3; i < path_.size(); i++) {
                //如果是双向路，做特殊处理
                //路径的有效性检验先忽略
//                car.path_.add(allRoad.roadsMap_.get(path_.get(i)));
                if (car.path_.getLast().to_ == allRoad.roadsMap_.get(path_.get(i)).from_) {
                    car.path_.add(allRoad.roadsMap_.get(path_.get(i)));
                } else if (car.path_.getLast().to_ == allRoad.roadsMap_.get(-path_.get(i)).from_) {
                    //添加负id的路
                    car.path_.add(allRoad.roadsMap_.get(-path_.get(i)));
                } else {
                    System.out.println("car:" + car.id_ + "路径不合法");
                }
            }
        }
//        Car car9 = allCar.carsMap_.get(67787);
        //检验是否所有车都有路径，目前看来没有错误
//        for (Car car: allCar.cars_
//        ) {
//            if(car.path_.size() < 2){
//                System.out.println("添加路径出错");
//            }
//        }

        //将车辆添加到每条路的上路队列中
        for (Car car : allCar.cars_
        ) {
            if (car.priority_ == 1) {
                /*
                正常应该不会出现空指针问题
                 */
                if(car.path_.getFirst() != null){
                    car.path_.getFirst().PresetCarQueue_.add(car);
                }
            } else {
                if (car.path_.getFirst() != null){
                    car.path_.getFirst().OrdinaryQueue_.add(car);
                }
            }
        }
        //对上路车辆进行排序
        for (Road road : allRoad.roads_
        ) {
            //对普通车辆排序
            Collections.sort(road.OrdinaryQueue_, (car1, car2) ->
            {
                if (car1.StartTime_ != car2.StartTime_) {
                    return car1.StartTime_ - car2.StartTime_;
                } else {
                    return car1.id_ - car2.id_;
                }
            });
            //对优先车辆排序
            Collections.sort(road.PresetCarQueue_, (car1, car2) ->
            {
                if (car1.StartTime_ != car2.StartTime_) {
                    return car1.StartTime_ - car2.StartTime_;
                } else {
                    return car1.id_ - car2.id_;
                }
            });
        }
        //计算出发点和终点
        ArrayList<Integer> allcarStarPoint_ = new ArrayList<>();
        ArrayList<Integer> allcarFinshPoint_ = new ArrayList<>();
        ArrayList<Integer> PriorityStartPoint_ = new ArrayList<>();
        ArrayList<Integer> PriorityFinshPoint_ = new ArrayList<>();
        for (Car car: allCar.cars_
        ) {
            if (!allcarStarPoint_.contains(car.path_.getFirst().from_)){
                allcarStarPoint_.add(car.path_.getFirst().from_);
            }
            if (!allcarFinshPoint_.contains(car.path_.getLast().to_)){
                allcarFinshPoint_.add(car.path_.getLast().to_);
            }

            if(car.priority_ == 1){
                if (!PriorityStartPoint_.contains(car.path_.getFirst().from_)){
                    PriorityStartPoint_.add(car.path_.getFirst().from_);
                }
                if (!PriorityFinshPoint_.contains(car.path_.getLast().to_)){
                    PriorityFinshPoint_.add(car.path_.getLast().to_);
                }
            }
        }
        allcarStarPoint = allcarStarPoint_.size();
        allcarFinshPoint = allcarFinshPoint_.size();
        PriorityStartPoint = PriorityStartPoint_.size();
        PriorityFinshPoint = PriorityFinshPoint_.size();
//        Car car = allCar.carsMap_.get(88053);
    }

    //判题器主体
    public ArrayList<Integer> Judge() {
        //先对路口和graph的路按升序排序
        Collections.sort(allCross.cross_, (cross1, cross2) ->
                cross1.id_ - cross2.id_);
        //driveCarInWaitState需要对出路口的路遍历，所以这里排序Graph中的Road
        for (Cross cross : allCross.cross_
        ) {
            //有的路的ID是负的
            Collections.sort(cross.RoadArrays_, (road1, road2) ->
                    Math.abs(road1.id_) - Math.abs(road2.id_));
        }

        while (true) {
            //进行下一次调度，时间自增1
            time_++;
//            if(time_ == 182){
//                time_ = time_;
//            }
            System.out.println("time: " + time_);
            //道路内车辆标定与驱动
            driveJustCurrentRoad();
            //优先车辆上路
            drivePresetCarsInitList();
            //创建优先队列
            createCarSequeue();
            //驱动所有等待状态的车
            ArrayList<Integer> deakLock = driveCarInWaitState();
            //如果死锁，则返回死锁信息
            if(deakLock != null){
                return deakLock;
            }
            //没有死锁，所有未上路的车上路
            drivePresetCarsInitList();
            driveOrdinaryCarsInitList();
            //判断是否完成
            if(isFinsh()){
                break;
            }

           //WriteRoadsInfo();
        }
        //所有车到达目的地
        return null;
    }

    //驱动路上的车行驶，应该没有错误
    private void driveJustCurrentRoad() {
        //记录下一台车可行的最大距离
        int Distance;
        //创建一个Car对象，用来保存最后遍历到的车
        Car car;
        //每条路都调度一次
        for (Road road : allRoad.roads_
        ) {
            if(time_ == 16 && road.id_ == 5009)
                road = road;
            //车道号由小到大开始调度
            for (int i = 0; i < road.channel_; i++) {
                //初始化最大可行驶距离
                Distance = 0;
                car = null;
                //从车道的开头开始遍历一条车道
                for (int j = 0; j < road.length_; j++) {
                    //如果找到一台车，对其进行调度
                    if (road.CarsPosition_[i][j] != null) {
                        //如果遇到第一台车，车速比可行驶距离大，说明车可以过路口，将车标记为等待状态
                        if (car == null && Distance < Math.min(road.CarsPosition_[i][j].speed_, road.speed_)) {
                            road.CarsPosition_[i][j].state_ = 1;
                            //更改最后一台遍历到的车
                            car = road.CarsPosition_[i][j];
                            //更改最大可行驶距离
                            Distance = 0;
                            //第一台车，且车速小于等于最大可行驶距离
                        } else if (car == null && Distance >= Math.min(road.CarsPosition_[i][j].speed_, road.speed_)) {
                            //车前进车速个单位
                            if (road.CarsPosition_[i][j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_)] != null){
                                System.out.println("error codeline: 179 " );
                            }
                            road.CarsPosition_[i][j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_)] = road.CarsPosition_[i][j];
                            road.CarsPosition_[i][j].col_ = i;
                            road.CarsPosition_[i][j].row_ = j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_);
                            //将车标记为终止状态
                            road.CarsPosition_[i][j].state_ = 0;
                            //更改下一台车最大可行驶距离
                            Distance = Math.min(road.CarsPosition_[i][j].speed_, road.speed_);
                            // 更改最后遍历到的车
                            car = road.CarsPosition_[i][j];
                            //清空原来车所在的位置
                            road.CarsPosition_[i][j] = null;
                            //不是第一台车，且最大可行驶距离小于车速，且前面的车为终止状态
                        } else if (Distance < Math.min(road.CarsPosition_[i][j].speed_, road.speed_) && car.state_ == 0) {
                            //车向前移动最大可行使距离
                            if (road.CarsPosition_[i][j - Distance] != null){
                                System.out.println("error codeline: 196 " );
                            }
                            road.CarsPosition_[i][j - Distance] = road.CarsPosition_[i][j];
                            road.CarsPosition_[i][j].col_ = i;
                            road.CarsPosition_[i][j].row_ = j - Distance;
                            //车的状态标记为终止态
                            road.CarsPosition_[i][j].state_ = 0;
                            //下一台车的最大可行驶距离不变
                            // 更改最后遍历到的车
                            car = road.CarsPosition_[i][j];
                            //清空原来车所在的位置
                            road.CarsPosition_[i][j] = null;
                            //不是第一台车且最大可行驶距离小于车速，前面的车位等待状态
                        } else if (Distance < Math.min(road.CarsPosition_[i][j].speed_, road.speed_) && car.state_ == 1) {
                            //将车标记为等待状态
                            road.CarsPosition_[i][j].state_ = 1;
                            //更改最后遍历到的车
                            car = road.CarsPosition_[i][j];
                            //更改最大可行驶距离
                            Distance = 0;
                            //不是第一台车，且车速小于等于最大可行驶距离
                        } else if (Distance >= Math.min(road.CarsPosition_[i][j].speed_, road.speed_)) {
                            //车向前移动最大可行使距离
                            if(road.CarsPosition_[i][j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_)] != null){
                                System.out.println("error codeline: 220 " );
                            }
                            road.CarsPosition_[i][j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_)] = road.CarsPosition_[i][j];
                            road.CarsPosition_[i][j].col_ = i;
                            road.CarsPosition_[i][j].row_ = j - Math.min(road.CarsPosition_[i][j].speed_, road.speed_);
                            //车的状态标记为终止态
                            road.CarsPosition_[i][j].state_ = 0;
                            //更改下一台车最大可行驶距离
                            Distance = Math.min(road.CarsPosition_[i][j].speed_, road.speed_);
                            // 更改最后遍历到的车
                            car = road.CarsPosition_[i][j];
                            //清空原来车所在的位置
                            road.CarsPosition_[i][j] = null;
                        }
                    } else {
                        //有空位，则可行驶距离加1
                        Distance++;
                    }
                }
            }
        }
    }

    //优先车辆上路
    private void drivePresetCarsInitList() {
        //新建一个保存车可行驶距离的变量
        int Distance;
        boolean isFind;
        Car car;
//        boolean flag;
        //创建一个保存已经上路的车的队列
        ArrayList<Car> FinishCars = new ArrayList<>();
        for (Road road : allRoad.roads_
        ) {
            //有先车辆的出发时间小于等于当前时间时车就可上路
            for (int i = 0; i < road.PresetCarQueue_.size() && road.PresetCarQueue_.get(i).StartTime_ <= time_; i++) {
                car = road.PresetCarQueue_.get(i);
                isFind = false;
//                flag = false;
                //车道号从小到大遍历是否有上路条件
                for (int j = 0; j < road.channel_; j++) {
                    //每条车道最开始的可行驶距离为0
                    Distance = 0;
                    //从车道的尾部开始遍历是否有上路条件
                    for (int k = road.length_ - 1; k >= 0; k--) {
                        //如果前方没有车
                        if (road.CarsPosition_[j][k] == null) {
                            //可行驶距离加1
                            Distance++;
                            //如果车的可行驶距离大于等于车的速度，车就可以上路
                            if (Distance >= Math.min(car.speed_, road.speed_)) {
                                if(road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] != null){
                                    System.out.println("error codeline: 272 " );
                                }
                                road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] = car;
                                car.col_ = j;
                                car.row_ = road.length_ - Math.min(car.speed_, road.speed_);
                                //将车标记为终止状态
                                car.state_ = 0;
                                //将上路的车保存起来
                                FinishCars.add(car);
                                //没有必要继续找了
                                isFind = true;
                                break;
                            }
                            //如果遇到的车位终止状态，且可行驶距离不为0
                        } else if (road.CarsPosition_[j][k].state_ == 0 && Distance != 0) {
                            //车可上路, 这里不会出现可行驶距离大于车速的情况
                            if(road.CarsPosition_[j][road.length_ - Distance] != null){
                                System.out.println("error codeline: 289 " );
                            }
                            road.CarsPosition_[j][road.length_ - Distance] = car;
                            car.col_ = j;
                            car.row_ = road.length_ - Distance;
                            //将车标记为终止状态
                            car.state_ = 0;
                            //将上路的车保存起来
                            FinishCars.add(car);
                            //没有必要继续找了
                            isFind = true;
                            break;
                            //遇到终止状态车且可行驶距离为0，进入下一条车道搜索
                        } else if (road.CarsPosition_[j][k].state_ == 0 && Distance == 0) {
                            break;
                            //能来到这里说明可行驶距离小于车速，如果遇到的是车是等待状态的车不可以上路,搜索下一台车是否可以上路
                        } else if (road.CarsPosition_[j][k].state_ == 1) {
                            //不能结束，后面的车还是有可能可以上路的
                            isFind = true;
//                            flag = true;
                            break;
                        }
                    }
                    if (isFind) {
                        break;
                    }
                }
                //有等待车阻挡，这条了的优先车上不了路,下一条路的优先车上路
//                if (flag){
//                    break;
//                }
            }
            //将上路的车删除
            road.PresetCarQueue_.removeAll(FinishCars);
        }
    }

    //驱动某条路的优先车上路
    private void drivePresetCarsInitList(Road road){
        //新建一个保存车可行驶距离的变量
        int Distance;
        boolean isFind;
        Car car;
//        boolean flag;
        //创建一个保存已经上路的车的队列
        ArrayList<Car> FinishCars = new ArrayList<>();

        //有先车辆的出发时间小于等于当前时间时车就可上路
        for (int i = 0; i < road.PresetCarQueue_.size() && road.PresetCarQueue_.get(i).StartTime_ <= time_; i++) {
            car = road.PresetCarQueue_.get(i);
            isFind = false;
//                flag = false;
            //车道号从小到大遍历是否有上路条件
            for (int j = 0; j < road.channel_; j++) {
                //每条车道最开始的可行驶距离为0
                Distance = 0;
                //从车道的尾部开始遍历是否有上路条件
                for (int k = road.length_ - 1; k >= 0; k--) {
                    //如果前方没有车
                    if (road.CarsPosition_[j][k] == null) {
                        //可行驶距离加1
                        Distance++;
                        //如果车的可行驶距离大于等于车的速度，车就可以上路
                        if (Distance >= Math.min(car.speed_, road.speed_)) {
                            if(road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] != null){
                                System.out.println("error codeline: 272 " );
                            }
                            road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] = car;
                            car.col_ = j;
                            car.row_ = road.length_ - Math.min(car.speed_, road.speed_);
                            //将车标记为终止状态
                            car.state_ = 0;
                            //将上路的车保存起来
                            FinishCars.add(car);
                            //没有必要继续找了
                            isFind = true;
                            break;
                        }
                        //如果遇到的车位终止状态，且可行驶距离不为0
                    } else if (road.CarsPosition_[j][k].state_ == 0 && Distance != 0) {
                        //车可上路, 这里不会出现可行驶距离大于车速的情况
                        if(road.CarsPosition_[j][road.length_ - Distance] != null){
                            System.out.println("error codeline: 289 " );
                        }
                        road.CarsPosition_[j][road.length_ - Distance] = car;
                        car.col_ = j;
                        car.row_ = road.length_ - Distance;
                        //将车标记为终止状态
                        car.state_ = 0;
                        //将上路的车保存起来
                        FinishCars.add(car);
                        //没有必要继续找了
                        isFind = true;
                        break;
                        //遇到终止状态车且可行驶距离为0，进入下一条车道搜索
                    } else if (road.CarsPosition_[j][k].state_ == 0 && Distance == 0) {
                        break;
                        //能来到这里说明可行驶距离小于车速，如果遇到的是车是等待状态的车不可以上路,搜索下一台车是否可以上路
                    } else if (road.CarsPosition_[j][k].state_ == 1) {
                        //不能结束，后面的车还是有可能可以上路的
                        isFind = true;
//                            flag = true;
                        break;
                    }
                }
                if (isFind) {
                    break;
                }
            }
            //有等待车阻挡，这条了的优先车上不了路,下一条路的优先车上路
//                if (flag){
//                    break;
//                }
        }
        //将上路的车删除
        road.PresetCarQueue_.removeAll(FinishCars);
    }

    //创建道路优先队列
    private void createCarSequeue() {
        for (Road road : allRoad.roads_
        ) {
            //创建优先队列的方法放在Road中更合理，这里为了方便就直接放在JudgeApp中
            road.createSequeue();
        }
    }

    private ArrayList<Integer> driveCarInWaitState() {
        int channel;
        while (!isAllCarInEndState()) {
            //在一次循环开始认为是死锁的
            deakLock_ = false;
            for (Car car: allCar.cars_
            ) {
                //如果路上有等待的车，则假设死锁未真
                if(car.state_ == 1){
                    deakLock_ = true;
                    break;
                }
            }
            if(!deakLock_){
                //如果没有等待的车，直接退出
                return null;
            }


            //按路口ID升序遍历
            for (Cross cross : allCross.cross_
            ) {
                //按道路ID升序遍历
                for (Road road : cross.RoadArrays_
                ) {
                    //从路的优先队列中取出一辆车
//                    System.out.println("Road: " + road.id_);
                    while (road.Sequeue.peek() != null) {
                        Car car = road.Sequeue.peek();
                        //如果第一台车的转向有冲突，则调度下一条路的车转弯，这条路的车等待下一次遍历所有路口时在调度
//                        if(road.to_ != cross.id_){
//                           System.out.println("error: " + car.id_ + " " + road.id_);
//                        }
                        if (conflict(cross, car)) {
                            break;
                        }
                        //转向不存在冲突，则尝试转弯
                        //先保存一下车所在的车道
//                        if(car.id_ == 102529){
//                            car = car;
//                        }
                        channel = car.col_;
                        //只有在路口等待的车移动了，路上的车才会移动，路口等待的成没有移动，说明出现死锁
//                        System.out.println(car.id_ + "," + car.state_ + "," + cross.id_);
                        if (moveToNextRoad(car.id_)) {
                            //删除优先队列中的这台车,moveTo函数已经有这个操作了
//                            road.Sequeue.remove(car);
                            //能进入这里说明没有出现死锁
//                            System.out.println(car.id_ + "," + car.state_);
                            deakLock_ = false;
                            //如果转向成功，则调度一次该车道上的车
                            driveJustCurrentRoad(road, channel);
//                            createSequeue(road, dir);
                            //路出现空位，让优先车辆上路,这里应该调度这条路上的优先车俩就可以，这里先所有路的优先车俩都调度一次
                            drivePresetCarsInitList(road);
                        } else {
                            //转弯不成功，则调度下一条路的车转弯
                            break;
                        }
                    }

                }
            }
            ArrayList<Integer> deakLock = isdeakLock();
            if (deakLock != null) {
                return deakLock;
            }
        }
        return null;
    }

    //判断是否所有车都是终止状态，有车是等待状态就返回false
    private boolean isAllCarInEndState() {
        for (Car car : allCar.cars_
        ) {
            //有一台车是等待状态
            if (car.state_ == 1) {
                return false;
            }
        }
        return true;
    }

    //判断转向是否有冲突
    private boolean conflict(Cross cross, Car car) {
        //如果车没有下一条路径
        int Car1dir;
        int Car1NextRoad;
        int Car2dir;
        int Car2NextRoad;
        if(car.path_.size() == 1){
            if(cross.getOppositeRoad(Math.abs(car.path_.get(0).id_)) != -1){
                Car1NextRoad = cross.getOppositeRoad(Math.abs(car.path_.get(0).id_));
                Car1dir = 2;
            }else {
                //没有对面路，不会有冲突
                return false;
            }
        }else {
            Car1dir = cross.getDirection(Math.abs(car.path_.get(0).id_), Math.abs(car.path_.get(1).id_));
            Car1NextRoad = Math.abs(car.path_.get(1).id_);
        }
//        if(car.id_ == 88053 && time_ == 24){
//            car = car;
//        }
//        if(car.path_.peek().to_ != cross.id_){
//            System.out.println("car: " + car.id_ + " cross: "+ cross.id_ + " 路径存在问题");
//            System.out.println("state: " + car.state_ + " to_ " + car.path_.peek().to_ + " ");
//        }else {
//            System.out.println("car: " + car.id_ + " cross: "+ cross.id_ + " 路径正确");
//        }
        //正常的话，车的路径的第一条路是车目前所在的路
        if (car.priority_ == 0) {
            //如果是普通车辆，改路口有优先车和该普通车要进入同一条路，则有冲突返回true
            for (Road road : cross.RoadArrays_
            ) {
                //如果路是车所在的路，则跳过
                if (road == car.path_.peek()) {
                    continue;
                }
                Car car2 = road.Sequeue.peek();
//                System.out.println("car2: " + car2.id_ + " cross: "+ cross.id_ + " 路径正确");
                //car2不存在，则进入下一条路搜索
                if (car2 == null) {
                    continue;
                }
                //第二台车没有下一条路径
//                if (car2.path_.size() == 1){
//                    continue;
//                }
                if(car2.path_.size() == 1){
//                    Car2dir = 2;
                    if (cross.getOppositeRoad(Math.abs(car2.path_.get(0).id_)) != -1){
                        Car2NextRoad = cross.getOppositeRoad(Math.abs(car2.path_.get(0).id_));
                        Car2dir = 2;
                    }else {
                        //没有对面路，不产生冲突
                        continue;
                    }
                }else {
                    Car2dir = cross.getDirection(Math.abs(car2.path_.get(0).id_), Math.abs(car2.path_.get(1).id_));
                    Car2NextRoad =  Math.abs(car2.path_.get(1).id_);
                }
                //存在car2，且是优先车，且要进入同一条路
                if (car2.priority_ == 1) {
                    //冲突
                    if (Car2NextRoad == Car1NextRoad) {
                        return true;
                    }
                } else {
                    //car2不是优先车辆，如果car2的转向优先权大于car，则有冲突
                    if (Car2NextRoad == Car1NextRoad) {
                        if (Car2dir > Car1dir) {
                            return true;
                        }
                    }
                }
            }
        } else {
            //如果是优先车，则判断有没有优先车的优先权高于它就可以了
            for (Road road : cross.RoadArrays_
            ) {
                //如果路是车所在的路，则跳过
                if (road == car.path_.peek()) {
                    continue;
                }
                Car car2 = road.Sequeue.peek();
                //car2不存在，则进入下一条路搜索
                if (car2 == null) {
                    continue;
                }
                //下一台车没有下一条路径
//                if(car2.path_.size() <= 1){
//                    continue;
//                }
                if(car2.path_.size() == 1){
//                    Car2dir = 2;
                    if(cross.getOppositeRoad(Math.abs(car2.path_.get(0).id_)) != -1){
                        Car2NextRoad = cross.getOppositeRoad(Math.abs(car2.path_.get(0).id_));
                        Car2dir = 2;
                    }else {
                        //没有对面路，不用产生冲突
                        continue;
                    }
                }else {
                    Car2dir = cross.getDirection(Math.abs(car2.path_.get(0).id_), Math.abs(car2.path_.get(1).id_));
                    Car2NextRoad = Math.abs(car2.path_.get(1).id_);
                }
                //存在car2，且是优先车，且要进入同一条路
                if (car2.priority_ == 1 && Car1NextRoad == Car2NextRoad) {
                    //如果car2的转向优先权高于car，则冲突
                    if (Car2dir > Car1dir) {
                        return true;
                    }
                }
            }
        }
        //所有冲突情况都不存在，返回false
        return false;
    }

    //转弯
    private boolean moveToNextRoad(int carid){
        //如果到达终点，返回true
        /*
        路径不应该出现有0的情况
         */
//        if(car.path_.size() == 0){
////            car.path_.peek().CarsPosition_[car.col_][car.row_] = null;
//            car.state_ = 2;
//            car.FinishTime_ = time_;
//            return true;
//        }
        Car car = allCar.carsMap_.get(carid);

//        if(car.id_ == 73220)
//            car = car;
        //能调用moveTOnextRoad的car都是能移动到下一条路的车
        if(car.path_.size() == 1){
            //将车从其位置中移除，并从其所在路的优先队列中移除
            car.path_.peek().Sequeue.poll();
            car.path_.peek().CarsPosition_[car.col_][car.row_] = null;
            car.path_.poll();
            car.state_ = 2;
            car.FinishTime_ = time_;
            return true;
        }else {
            //正常不会出现没有路径的情况，这里将没有路径的情况忽略
            //从车道号小的开始扫描
            int Distance;
            Road ToRoad = car.path_.get(1);
            //先处理去不到下一条路的情况,调用此方法说明前面没有车阻挡
            //Math.min(car.speed_, ToRoad.speed_)
            if(Math.min(car.speed_, ToRoad.speed_) <= car.row_){
                //将车移动到到路的最前面，这里车是一定能走到最前面的
                if( car.path_.peek().CarsPosition_[car.col_][0] != null){
                    System.out.println("error codeline: 515");
                    System.out.println("carid: " + car.path_.peek().CarsPosition_[car.col_][0].id_ + ", " + car.path_.peek().CarsPosition_[car.col_][0].state_);
                    System.out.println("carid2: " + car.id_ + ", " + car.state_);
                }
                car.path_.peek().CarsPosition_[car.col_][0] = car;
                car.path_.peek().CarsPosition_[car.col_][car.row_] = null;
                car.row_ = 0;
                //车的状态改为终止态
                car.state_ = 0;
                //将车从该路的优先队列中删除
                car.path_.peek().Sequeue.poll();
                //车的路径占时不用改
                return true;
            }
            for (int i = 0; i < ToRoad.channel_; i++){
                Distance = 0;
                //从路的尾部搜索才对？
                for (int j = ToRoad.length_ - 1; j >= 0; j--){
                    //如果有空位，可行驶距离加1
                    if(ToRoad.CarsPosition_[i][j] == null){
                        Distance++;
                        //一定是等号成立时进入条件选择语句,有足够距离进入下一条路就不用继续搜索了
                        if(Distance + car.row_ >= Math.min(car.speed_, ToRoad.speed_)){
                            if(ToRoad.CarsPosition_[i][ToRoad.length_ - Distance] != null){
                                System.out.println("error codeline: 537");
                            }
                            //将车移动到下一条路
                            ToRoad.CarsPosition_[i][ToRoad.length_ - Distance] = car;
                            //将车重原来的位置删除
                            car.path_.peek().CarsPosition_[car.col_][car.row_] = null;
                            //改变车的位置标记
                            car.col_ = i;
                            car.row_ = ToRoad.length_ - Distance;
                            //车的状态改为终止状态
                            car.state_ = 0;
                            //将车从原路的优先队列中删除
                            car.path_.peek().Sequeue.poll();
                            //也删除车的路径的一条路
                            car.path_.poll();
                            //返回true
                            return true;
                        }
                    }else if(ToRoad.CarsPosition_[i][j].state_ == 0){
                        //如果遇到终止车辆，可行距离为0，扫描下一条车道
                        if(Distance == 0){
//                            continue;
                            break;
                        }else {
                            if(ToRoad.CarsPosition_[i][ToRoad.length_ - Distance] != null){
                                System.out.println("error codeline: 562");
                            }
                            //这里是车实际速度大于可行驶距离的情况
                            //将车移动到下一条路
                            ToRoad.CarsPosition_[i][ToRoad.length_ - Distance] = car;
                            //将车重原来的位置删除
                            car.path_.peek().CarsPosition_[car.col_][car.row_] = null;
                            //改变车的位置标记
                            car.col_ = i;
                            car.row_ = ToRoad.length_ - Distance;
                            //车的状态改为终止状态
                            car.state_ = 0;
                            //将车从原路的优先队列中删除
                            car.path_.peek().Sequeue.poll();
                            //也删除车的路径的一条路
                            car.path_.poll();
                            //返回true
                            return true;
                        }
                    }else if(ToRoad.CarsPosition_[i][j].state_ == 1){
                        //遇到等待车辆，不可以转弯
//                        System.out.println("car: " + carid + " --> " + "car: " + ToRoad.CarsPosition_[i][j].id_);
                        return false;
                    }
                }
            }
            //没有空位进入
            //走到最前方
            if(car.row_ == 0)
            {
//                System.out.println("car123: " + car.id_ + ", " + car.state_);
//                System.out.println("car2: " + car.path_.peek().CarsPosition_[car.col_][0].id_ + ", " + car.path_.peek().CarsPosition_[car.col_][0].state_);
                car.state_ = 0;
                //将车从原路的优先队列中删除
                car.path_.peek().Sequeue.poll();
            }else {
                car.path_.peek().CarsPosition_[car.col_][0] = car;
                //将车重原来的位置删除
                car.path_.get(0).CarsPosition_[car.col_][car.row_] = null;
                //改变车的位置标记
//            car.col_ = i;
                car.row_ = 0;
                //车的状态改为终止状态
                car.state_ = 0;
                //将车从原路的优先队列中删除
                car.path_.peek().Sequeue.poll();
            }
            return true;
        }
    }

    //驱动当前车道的车行驶
    private void driveJustCurrentRoad(Road road, Integer channel){
        int Distance = 0;
//        if(road.id_ == 5009 && time_ == 16)
//            road = road;
        Car car = null;
        //先看一下第一台车是不是要出路口,出不了路口后面的车都是终止态
        for (int i = 0; i < road.length_; i++) {
            if (road.CarsPosition_[channel][i] == null) {
                Distance++;
            } else {
                car = road.CarsPosition_[channel][i];
                break;
            }
        }
        if(car != null && car.state_ == 1 && Math.min(road.speed_, car.speed_) > Distance){
            return;
        }
//            }else {
//                //第一台车要出路口，直接结束
//                if(road.CarsPosition_[channel][i].state_ == 1 && Math.min(road.speed_, road.CarsPosition_[channel][i].speed_) > Distance){
//                    return;
//                }
//            }
        Distance = 0;
//        if(road.id_ == -6887){
//            car = null;
//        }
        //创建一个保存移动过的车的集合
        ArrayList<Car> Cars_ = new ArrayList<>();
        for (int i = 0; i < road.length_; i++){
            car = road.CarsPosition_[channel][i];
            if(road.CarsPosition_[channel][i] == null){
                Distance++;
            }else if(road.CarsPosition_[channel][i].state_ == 0){
                //遇到终止状态的车，可行驶距离变为0，继续向下搜索
                Distance = 0;
                continue;
            }else if(road.CarsPosition_[channel][i].state_ == 1){
                //遇到等待状态的车，等待车不会出路口，且前面的车一定是终止状态的
                car = road.CarsPosition_[channel][i];
                if(Math.min(car.speed_, road.speed_) <= Distance){
                    if( road.CarsPosition_[channel][i - Math.min(car.speed_, road.speed_)] != null){
                        System.out.println("error codeline: 641");
                    }
                    //如果车速小于等于可行驶距离
                    road.CarsPosition_[channel][i - Math.min(car.speed_, road.speed_)] = car;
                    //删除原来位置的车
                    road.CarsPosition_[car.col_][car.row_] = null;
                    //改变车的坐标
                    car.row_ = i - Math.min(car.speed_, road.speed_);
                    //car的状态改为终止状态
                    car.state_ = 0;
                    //可移动距离改变
                    Distance = Math.min(car.speed_, road.speed_);
                    //车进入队列，用于删除优先的车
                    Cars_.add(car);
                }else {
                    //速度大于可行驶距离
                    if (car.row_ == i - Distance){
                        car.state_ = 0;
                        Cars_.add(car);
                        continue;
                    }
                    if(road.CarsPosition_[channel][i - Distance] != null){
                        System.out.println("error codeline: 657");
                        System.out.println("car: " + car.id_ + ", " + car.state_);
                        System.out.println("car2: " + road.CarsPosition_[channel][i - Distance].id_ + ", " + road.CarsPosition_[channel][i - Distance].state_);
                    }
                    road.CarsPosition_[channel][i - Distance] = car;
                    //删除原来位置的车
                    road.CarsPosition_[car.col_][car.row_] = null;
                    //改变车的坐标
                    car.row_ = i - Distance;
                    //car的状态改为终止状态
                    car.state_ = 0;
                    //可移动距离不改变
                    //车进入队列，用于删除优先的车
                    Cars_.add(car);
                }
            }
        }
        //删除掉移动过的车
        road.Sequeue.removeAll(Cars_);
    }

    //判断是否出现死锁
    private ArrayList<Integer> isdeakLock(){
        if(deakLock_){
            //在某次调度，所有等待车辆都没有移动，说明出现死锁，
            //找出死锁车辆，死锁路和死锁路口
            ArrayList<Integer> deakLockCars = new ArrayList<>();
            ArrayList<Integer> deakLockCrosses = new ArrayList<>();
            ArrayList<Integer> deakLockRoads = new ArrayList<>();
            for (Car car: allCar.cars_
            ) {
                if(car.state_ == 1){
                    //如果车是在等待状态，说明是死锁车辆
                    deakLockCars.add(car.id_);
                    //路口和路有可能重复
//                    if(!deakLockRoads.contains(Math.abs(car.path_.peek().id_))){
//                        deakLockRoads.add(Math.abs(car.path_.peek().id_));
//                    }
//                    if(!deakLockCrosses.contains(car.path_.peek().to_)){
//                        deakLockCrosses.add(car.path_.peek().to_);
//                    }
                    if(!deakLockRoads.contains(car.path_.peek().id_)){
                        deakLockRoads.add(car.path_.peek().id_);
                    }
                    if(!deakLockCrosses.contains(allCross.crossMap_.get(car.path_.peek().to_).id_)){
                        deakLockCrosses.add(allCross.crossMap_.get(car.path_.peek().to_).id_);
                    }
                }
            }
            ArrayList<Integer> isdeakLock_ = new ArrayList<>();
            isdeakLock_.addAll(deakLockCrosses);
            isdeakLock_.add(0);
            isdeakLock_.addAll(deakLockRoads);
            isdeakLock_.add(0);
            isdeakLock_.addAll(deakLockCars);
            isdeakLock_.add(0);
            isdeakLock_.add(time_);
            System.out.println("deakLock!");
            System.out.println("Cross: " + deakLockCrosses.size() + " Road: " + deakLockRoads.size() + " cars: " +deakLockCars.size());
            return isdeakLock_;
        }else {
            return null;
        }
    }

    //普通车车上路
    private void driveOrdinaryCarsInitList(){
        //新建一个保存车可行驶距离的变量
        int Distance;
        boolean isFind;
        Car car;
        //创建一个保存已经上路的车的队列
        ArrayList<Car> FinishCars = new ArrayList<>();
        for (Road road : allRoad.roads_
        ) {
            //普通车辆的出发时间小于等于当前时间时车就可上路
            for (int i = 0; i < road.OrdinaryQueue_.size() && road.OrdinaryQueue_.get(i).StartTime_ <= time_; i++) {
                //取出一辆车
                car = road.OrdinaryQueue_.get(i);
                //默认上不了路
                isFind = false;
//                flag = false;
                //车道号从小到大遍历是否有上路条件
                for (int j = 0; j < road.channel_; j++) {
                    //每条车道最开始的可行驶距离为0
                    Distance = 0;
                    //从车道的尾部开始遍历是否有上路条件
                    for (int k = road.length_ - 1; k >= 0; k--) {
                        //如果前方没有车
                        if (road.CarsPosition_[j][k] == null) {
                            //可行驶距离加1
                            Distance++;
                            //如果车的可行驶距离大于等于车的速度，车就可以上路
                            if (Distance >= Math.min(car.speed_, road.speed_)) {
                                if(road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] != null){
                                    System.out.println("error codeline: 741");
                                }
                                road.CarsPosition_[j][road.length_ - Math.min(car.speed_, road.speed_)] = car;
                                car.col_ = j;
                                car.row_ = road.length_ - Math.min(car.speed_, road.speed_);
                                //将车标记为终止状态
                                car.state_ = 0;
                                //将上路的车保存起来
                                FinishCars.add(car);
                                //没有必要继续找了
                                isFind = true;
                                break;
                            }
                            //如果遇到的车位终止状态，且可行驶距离不为0
                        } else if (road.CarsPosition_[j][k].state_ == 0 && Distance != 0) {
                            if(road.CarsPosition_[j][road.length_ - Distance] != null){
                                System.out.println("error codeline: 757");
                            }
                            //车可上路, 这里不会出现可行驶距离大于车速的情况
                            road.CarsPosition_[j][road.length_ - Distance] = car;
                            car.col_ = j;
                            car.row_ = road.length_ - Distance;
                            //将车标记为终止状态
                            car.state_ = 0;
                            //将上路的车保存起来
                            FinishCars.add(car);
                            //没有必要继续找了
                            isFind = true;
                            break;
                            //遇到终止状态车且可行驶距离为0，进入下一条车道搜索
                        } else if (road.CarsPosition_[j][k].state_ == 0 && Distance == 0) {
                            break;
                            //能来到这里说明可行驶距离小于车速，如果遇到的是车是等待状态的车不可以上路,搜索下一台车是否可以上路
                        } else if (road.CarsPosition_[j][k].state_ == 1) {
                            //不能结束，后面的车还是有可能可以上路的
                            isFind = true;
//                            flag = true;
                            break;
                        }
                    }
                    if (isFind) {
                        break;
                    }
                }
                //有等待车阻挡，这条了的优先车上不了路,下一条路的优先车上路
//                if (flag){
//                    break;
//                }
            }
            //将上路的车删除
            road.OrdinaryQueue_.removeAll(FinishCars);
        }
    }

    //判断是否完成
    private boolean isFinsh(){
//        int i = 0;
//        Car ca1 = null;
//        if(time_ < 5400){
//            return false;
//        }
//        for (Car car: allCar.cars_
//             ) {
//            if(car.path_.size() != 0 && car.state_ == 0){
//                i++;
//                ca1 = car;
//            }
//        }
//        if(i >= 0){
//            System.out.println("剩余没有跑完的车的数量：" + i);
//            System.out.println("car: " + ca1.id_  + " state: " + ca1.state_);
//            String str = new String();
//            for (Road road: ca1.path_
//                 ) {
//                str = str + " " + road.id_;
//            }
//            System.out.println(str);
//            System.out.println(ca1.path_.peek().CarsPosition_);
//            return false;
//        }else {
//            return true;
//        }
//    }
        for (Car car: allCar.cars_
        ) {
            if (car.state_ != 2){
                return false;
            }
        }
        Score();
        return true;
    }

    //评分函数
    private void Score(){
        int T = time_;
        int Tsum, Tpri, Tsumpri, Te, Tesum;
        float a, b;
        int minStartTime, maxFinshTime, allCarNumber, priorityCarNumber, allCarMinStartTime, allCarMaxStartTime, priorityCarMaxStartTime;
        int allcarMaxSpeed, allcarMinSpeed, prioritycarMaxSpeed, prioritycarMinSpeed;
        //出发点和终点分部不能再最后计算， 放在初始化方法中
        Tsum = 0;
        Tsumpri = 0;
        allCarNumber = 0;
        priorityCarNumber = 0;
        minStartTime = Integer.MAX_VALUE;
        maxFinshTime = Integer.MIN_VALUE;
        allCarMinStartTime = Integer.MAX_VALUE;
        allCarMaxStartTime = Integer.MIN_VALUE;
        priorityCarMaxStartTime = Integer.MIN_VALUE;
        allcarMaxSpeed = Integer.MIN_VALUE;
        allcarMinSpeed = Integer.MAX_VALUE;
        prioritycarMaxSpeed = Integer.MIN_VALUE;
        prioritycarMinSpeed = Integer.MAX_VALUE;
        for (Car car: allCar.cars_
        ) {
            //所有车的总数自加1
            allCarNumber++;
            Tsum += (car.FinishTime_ - car.StartTime_);
            //找所有车的最小出发时间
            if(allCarMinStartTime > car.planTime_){
                allCarMinStartTime = car.planTime_;
            }
            //找所有车的最大出发时间
            if(allCarMaxStartTime < car.planTime_){
                allCarMaxStartTime = car.planTime_;
            }
            //所有车最大速度
            if(allcarMaxSpeed < car.speed_){
                allcarMaxSpeed = car.speed_;
            }
            //所有车最小速度
            if(allcarMinSpeed > car.speed_){
                allcarMinSpeed = car.speed_;
            }

            //优先车
            if(car.priority_ == 1){
                //优先车辆的总用时
                Tsumpri += (car.FinishTime_ - car.StartTime_);
                //优先车总数自加1
                priorityCarNumber++;
                //找最小出发时间
                if(car.planTime_ < minStartTime){
                    minStartTime = car.planTime_;
                }
                //优先车的最大出发时间
                if(priorityCarMaxStartTime < car.planTime_){
                    priorityCarMaxStartTime = car.planTime_;
                }
                //找最大到达时间
                if (car.FinishTime_ > maxFinshTime){
                    maxFinshTime = car.FinishTime_;
                }
                //优先车最大速度
                if(prioritycarMaxSpeed < car.speed_){
                    prioritycarMaxSpeed = car.speed_;
                }
                //优先车最小速度
                if(prioritycarMinSpeed > car.speed_){
                    prioritycarMinSpeed = car.speed_;
                }
            }
        }
        Tpri = maxFinshTime - minStartTime;

        a = ((float)0.05 * allCarNumber / priorityCarNumber + (float)0.2375 * allcarMaxSpeed * prioritycarMinSpeed
                / allcarMinSpeed / prioritycarMaxSpeed + (float)0.2375 * allCarMaxStartTime * minStartTime / priorityCarMaxStartTime
                / allCarMinStartTime + (float)0.2375 * allcarStarPoint /PriorityStartPoint + (float)0.2375 * allcarFinshPoint / PriorityFinshPoint);

        b = ((float)0.8 * allCarNumber / priorityCarNumber + (float)0.05 * allcarMaxSpeed * prioritycarMinSpeed
                / allcarMinSpeed / prioritycarMaxSpeed + (float)0.05 * allCarMaxStartTime * minStartTime / priorityCarMaxStartTime
                / allCarMinStartTime + (float)0.05 * allcarStarPoint /PriorityStartPoint + (float)0.05 * allcarFinshPoint / PriorityFinshPoint);

        Te = (int)(a * Tpri + T + 0.5);
        Tesum = (int) (b * Tsumpri + Tsum + 0.5);

        System.out.println("Tpri:" + Tpri + "  T:" + T + "  Tsumpri:" + Tsumpri + "  Tsum:" + Tsum + "  Tpri:" + Tpri);
        System.out.println("a: " + a + "  b: " + b + "  Te: " + Te + "  Tesum: " + Tesum);
    }



    // 保存每个时刻道路上车的信息，保存格式为
    //  forward指由 道路起点指向终点的方向
    //  backward指由 道路终点指向起点的方向
    //  time:n 注：time表示下一时刻到了
    //  (roadId,forward/backward,[[车道0],[车道1],[车道2]])注：车道内从左到右，按离道路出口升序排列
    //   exp:
    //   time:10
    //   (5001,forward,[[-1,-1,-1,10005],[-1,-1,10010,-1]])
    //   (5001,backward,[[-1,-1,-1,10007],[-1,-1,10210,-1]])
    //      backward    [-1][-1][10210][-1]    大车道
    //       进车口     [-1][-1][-1][10007]    小车道
    //                 ---------------------
    //        小车道    [10005][-1][-1][-1]    forward
    //        大车道    [-1][10010][-1][-1]    出车口
    private void WriteRoadsInfo(){
        // 遍历每条路
        if(time_ == 664){
            int test = 0;
        }
        ArrayList<String> allInfo = new ArrayList<>();
        for(Road road: allRoad.roads_){
            String info = new String("(");
            if(road.id_ > 0){
                info += Integer.toString(road.id_);
                info += ",forward,[";
            }
            else{
                info += Integer.toString(-road.id_);
                info += ",backward,[";
            }
            for(int channel = 0; channel < road.channel_; ++channel){
                String roadInfo = new String("[");
                for(int row = road.length_ - 1; row >= 0; --row){
                    if(road.CarsPosition_[channel][row] != null){
                        roadInfo += Integer.toString(road.CarsPosition_[channel][row].id_);
                        roadInfo += ",";
                    }
                    else{
                        roadInfo += "-1,";
                    }
                }
                // 去掉逗号
                roadInfo = roadInfo.substring(0, roadInfo.length()-1);
                roadInfo += "]";
                info += roadInfo;
                info += ",";
            }

            // 最后要去掉最后一个逗号，加上]
            info = info.substring(0, info.length()-1);
            info += "])\n";
            // 一条道路的信息，之后再写入一个文件中。
            allInfo.add(info);
        }

        // 写入到文件中。
        String fileName = new String("Data/visualData.txt");
        File file = new File(fileName);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(fileName, true);
            BufferedWriter bufWriter = new BufferedWriter(writer);
            String time = new String("time:"+Integer.toString(time_)+"\n");
            bufWriter.write(time);
            for(String line: allInfo){
                bufWriter.write(line);
            }
            bufWriter.close();
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

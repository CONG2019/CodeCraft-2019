package com.huawei;

import edu.princeton.cs.algs4.In;

import java.util.*;

// 调度器类，负责进行车辆的调度
public class Scheduler {
    // 结果要求的格式：　(车辆id，实际出发时间，行驶路线序列)格式的向量。例如(1001, 1, 501, 502, 503, 516, 506, 505, 518, 508, 509, 524)
    // 所以可以用一个二维的ArrayList进行保存
    public ArrayList<ArrayList<Integer>> answer;

    //
    private int NUMBER;
    private int PRENUMBER;
    //增加一个统计某时刻总发车数量的HashMap
    HashMap<Integer, Integer> CarNumber;
    public Scheduler(AllCross allCross, PresetAnswer presetAnswer, boolean isPriority){
        if(allCross.crossMap_.containsKey(22)){
//            System.out.print("It's map1!\n");
            if(isPriority){
                //PRENUMBER = 6;
                //NUMBER = 100;
                PRENUMBER = 6;
                NUMBER = 68;
            }
            else{
                //PRENUMBER = 4;
                //NUMBER = 42;
                PRENUMBER = 6;
                NUMBER =58;
            }
        }
        else{
//            System.out.print("It's map2!\n");
            if(isPriority){
                //PRENUMBER = 6;
                PRENUMBER = 7;
                NUMBER = 106;
                //NUMBER = 135;
            }
            else{
                //PRENUMBER = 4;
                //NUMBER = 63;
                PRENUMBER = 6;
                NUMBER = 75;
            }
        }
        //初始化预置车每时刻的发车数
        CarNumber = new HashMap<>();
        for (ArrayList<Integer> presetCar: presetAnswer.presetAnswer_
        ) {
            if(CarNumber.get(presetCar.get(1)) != null){
                CarNumber.replace(presetCar.get(1), CarNumber.get(presetCar.get(1))+1);
            }else {
                CarNumber.put(presetCar.get(1), 1);
            }
        }
    }

    /*
       第一个方法，每隔一秒放入一辆车，不考虑任何情况，在BFSSolution中寻找车辆起点到终点的方案。
     */
    public int SimpleSchedule(AllCar allCar, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> dijPath) {
        // 先对车辆按照触发时间进行排序
        Collections.sort(allCar.cars_);
        // 逐辆车进行调度，每个时间单位只走一个车
        int startTime = 1;
        int count = 1;
        answer = new ArrayList<>();
        for (Car car : allCar.cars_
        ) {
            // 先保存车辆id和实际安排的出发时间
            ArrayList<Integer> carSchedule = new ArrayList<>();
            carSchedule.add(car.id_);
            carSchedule.add(Math.max(startTime, car.planTime_));
            carSchedule.addAll(path.get(car.from_).get(car.to_));
            // 根据每台车的起始点和终点查找路径
//            if(car.id_ % 2 == 0){
//                carSchedule.addAll(path.get(car.from_).get(car.to_));
//            }
//            else{
//                carSchedule.addAll(dijPath.get(car.from_).get(car.to_));
//                //continue;
//            }
            // 下一台车在出发
            if (count == 18) {
                ++startTime;
                count = 1;
            }
            ++count;
            answer.add(carSchedule);
        }
        return startTime;
    }

    public int Schedule(AllCar allCar, MinPath bfsSolution, Graph graph, AllRoad allRoad, int startTime){
        ArrayList<ArrayList<Car>> carsArray = SplitCars(allCar);
        //保存还没有成功安排路径的车
        ArrayList<Car> queue = new ArrayList<>();
        // 足够的startTime让车辆先走。
        // int startTime = 900;
        int number = 0;
//        int j = 0;
//        bfsSolution.GetPaths(graph);
        answer = new ArrayList<>();
        //创建一个car集合，用于保存重新分配路径成功的车辆
        ArrayList<Car> Cars_ = new ArrayList<>();
        //每辆车重新根据路况计算一次最短路径
        for (ArrayList<Car> carlist: carsArray
        ) {
            for (Car car: carlist
            ) {
                //不再给预置车寻找路径
                if(car.preset_ == 1){
                    continue;
                }
                //根据车速对发车数进行调整
                // int NUMBER;     //记录同时发车的数量
                // NUMBER = 28;
                //每次发下一辆车试，优先发上一次没有发成功的车
                for (Car car_: queue
                ) {
                    ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car_, startTime);
                    //如果找到路径，则优先为这台车按排路径
                    if(tmp != null){
                        ArrayList<Integer> carSchedule_ = new ArrayList<>();
                        carSchedule_.add(car_.id_);
                        carSchedule_.add(Math.max(startTime, car_.planTime_));
                        carSchedule_.addAll(tmp);
                        answer.add(carSchedule_);
                        Cars_.add(car_);
                        number++;
                    }
                }
                //清除掉已经成功安排路径的车
                queue.removeAll(Cars_);
                Cars_.clear();

                // 先保存车辆id和实际安排的出发时间
                ArrayList<Integer> carSchedule = new ArrayList<>();
                carSchedule.add(car.id_);
                carSchedule.add(Math.max(startTime, car.planTime_));
                // 根据每台车的起始点和终点查找路径
                ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car, startTime);
                if(tmp != null){
                    carSchedule.addAll(tmp);
                    answer.add(carSchedule);
                    number++;
                }else {
                    queue.add(car);
                }
                // 下一台车在出发
//                for (Integer roadID: (bfsSolution.Path_.get(car.from_).get(car.to_)
//                ) ){
//                    HashMap<Integer, Road> Roads_ = allRoad.roadsMap_;
//                    Road road = Roads_.get(roadID);
//                    road.cars_++;
//                }
                // 目前先隔秒发车，试一试答案。
//                if(number >= (int)(NUMBER + 8 / car.speed_ + 0.5)){
//                    ++startTime;
//                    number = 0;
//                }
                if((startTime >=750 && number > NUMBER) || (startTime < 750 && number > PRENUMBER)){
//                    number = 0;
                    startTime += 2;
                    //强行将发车数近似变为线性发车
//                    if(CarNumber.get(startTime) != null){
//                        number = CarNumber.get(startTime) % NUMBER;
//                        startTime = startTime + CarNumber.get(startTime) / NUMBER;
//                    }else{
//                        number = 0;
//                    }
                    //在大量发车前后空出一定时间不发车
                    if(CarNumber.get(startTime + 2) != null && CarNumber.get(startTime + 2) > 2 * NUMBER){
                        number = 0;
                        startTime = startTime + CarNumber.get(startTime+2) / NUMBER + 8;
                    }else{
                        number = 0;
                    }
                }
            }

            // 车速降低一级，发车量减少。
           // NUMBER -= 10;
        }
        while (!queue.isEmpty()){
            startTime++;
            for (Car car_: queue
            ) {
                ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car_, startTime);
                if(tmp != null){
                    ArrayList<Integer> carSchedule_ = new ArrayList<>();
                    carSchedule_.add(car_.id_);
                    carSchedule_.add(Math.max(startTime, car_.planTime_));
                    carSchedule_.addAll(tmp);
                    answer.add(carSchedule_);
                    Cars_.add(car_);
                }
            }
            queue.removeAll(Cars_);
            Cars_.clear();
        }
        return startTime;
    }

    // 按照速度分割车
    public ArrayList<ArrayList<Car>> SplitCars(AllCar allCar){
        ArrayList<ArrayList<Car>> carsArray = new ArrayList<>();
        //将车按降序排序int carId = sameSpeedCars.get(j).id_;
        Collections.sort(allCar.cars_, (car1, car2)->
                car2.speed_ - car1.speed_);

        int speed = allCar.cars_.get(0).speed_;
        ArrayList<Car> carArrayList = new ArrayList<>();
        for (Car car: allCar.cars_
        ) {
            if(car.speed_ == speed){
                carArrayList.add(car);
            }else {
                carsArray.add(carArrayList);
                carArrayList = new ArrayList<>();
                speed = car.speed_;
                carArrayList.add(car);
            }
        }
        carsArray.add(carArrayList);
        //不同速度的车再按时间顺序排序
        for (ArrayList<Car> carArrayList1: carsArray
        ) {
            Collections.sort(carArrayList1, (car1, car2)->
                    car1.planTime_ - car2.planTime_);
        }

        // 按照出发点和速度分割车，尽可能使得每个点的发车均衡。
        // 还是按照速度分割，但是速度相同的车按照不同的出发点进行排列。然后轮询不同的路口进行顺序发车
        ArrayList<ArrayList<Car>> newCarsArray = new ArrayList<>();
        for(int i = 0; i < carsArray.size(); ++i){
            // 对于每一种相同速度的车，按照出发路口进行发车。
            HashMap<Integer, ArrayList<Car>> carsFrom = new HashMap<>();
            ArrayList<Car> sameSpeedCars = carsArray.get(i);
            for(int j = 0; j < sameSpeedCars.size(); ++j){
                Car car = sameSpeedCars.get(j);
                if(carsFrom.containsKey(car.from_)){
                    carsFrom.get(car.from_).add(sameSpeedCars.get(j));
                }
                else{
                    ArrayList<Car> carFrom = new ArrayList<>();
                    carFrom.add(sameSpeedCars.get(j));
                    carsFrom.put(car.from_, carFrom);
                }
            }
            // 整理一份新的发车顺序出来。
            ArrayList<Car> newCarsOrder = new ArrayList<>();
            while(!carsFrom.isEmpty()){
                // 遍历一个keyset
                ArrayList<Integer> removeIndex = new ArrayList<>();
                for(Integer key: carsFrom.keySet()){
                    newCarsOrder.add(carsFrom.get(key).get(0));
                    carsFrom.get(key).remove(0);
                    // 一次发两台车试试
                    if(carsFrom.get(key).isEmpty()){
                        removeIndex.add(key);
                    }
                    else{
                        newCarsOrder.add(carsFrom.get(key).get(0));
                        carsFrom.get(key).remove(0);
                        if(carsFrom.get(key).isEmpty()){
                            removeIndex.add(key);
                        }
                    }
                }
                for(int index: removeIndex){
                    carsFrom.remove(index);
                }
            }
            newCarsArray.add(newCarsOrder);
        }

        // 按照不同的顺序间隔发车
        // 改动一下发车的顺序，快慢交替发车，目前先随机
        // int speedTypes = newCarsArray.size();
        // for(int i = 1; i < speedTypes; ++i){
        //     newCarsArray.get(0).addAll(newCarsArray.get(i));
            
        // }
        // Collections.shuffle(newCarsArray.get(0));
        // // 删除原来的
        // for(int i = speedTypes-1; i >0; --i){
        //     newCarsArray.remove(i);
        // }
        return newCarsArray;
    }

 }


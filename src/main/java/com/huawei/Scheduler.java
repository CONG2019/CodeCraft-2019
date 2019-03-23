package com.huawei;

import java.util.*;

// 调度器类，负责进行车辆的调度
public class Scheduler {
    // 结果要求的格式：　(车辆id，实际出发时间，行驶路线序列)格式的向量。例如(1001, 1, 501, 502, 503, 516, 506, 505, 518, 508, 509, 524)
    // 所以可以用一个二维的ArrayList进行保存
    public ArrayList<ArrayList<Integer>> answer;

    //

    /*
       第一个方法，每隔一秒放入一辆车，不考虑任何情况，在BFSSolution中寻找车辆起点到终点的方案。
     */
    public void SimpleSchedule(AllCar allCar, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path){
        // 先对车辆按照触发时间进行排序
        Collections.sort(allCar.cars_);
        // 逐辆车进行调度，每个时间单位只走一个车
        int startTime = 1;
        int count = 1;
        answer = new ArrayList<>();
        for (Car car: allCar.cars_
             ) {
            // 先保存车辆id和实际安排的出发时间
            ArrayList<Integer> carSchedule = new ArrayList<>();
            carSchedule.add(car.id_);
            carSchedule.add(Math.max(startTime, car.planTime_));
            // 根据每台车的起始点和终点查找路径
            carSchedule.addAll(path.get(car.from_).get(car.to_));
            // 下一台车在出发
            if(count == 14){
                ++startTime;
                count = 1;
            }
            ++count;
            answer.add(carSchedule);
        }
    }

    /*
       第二个方法：按照车的出发点发车，没n个为一组。每一秒发n台。
     */
    public void SameSourceSchedule(AllCar allCar, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path){
        answer = new ArrayList<>();
        int maxNum = 15; // 每次发15台车
        int startTime = 1;
        int count = 1;
        // 以15个出发点为一组进行调度。
        LinkedList<Integer> sources = new LinkedList<>(allCar.carsFrom_.keySet());
        while(!sources.isEmpty()){
            // 取出15个出发点或者小于15个。
            LinkedList<Integer> partialSources = new LinkedList<>();
            while(count <= 15 && !sources.isEmpty()){
                partialSources.add(sources.get(0));
                sources.removeFirst();
                ++count;
            }
            count = 0;
            // 对这些点出发的车进行发车，每次发maxNum台直到没有车为止。
            boolean flag = true;

            while(flag){
                flag = false;
                for (int source: partialSources
                     ) {
                    // 如果还能找到车
                    if(allCar.carsFrom_.get(source).isEmpty()){
                        continue;
                    }
                    else{
                        flag = true;
                        ArrayList<Integer> carIds = allCar.carsFrom_.get(source);
                        int carId = carIds.get(carIds.size()-1);
                        // 删除一个carId
                        carIds.remove(carIds.size()-1);
                        // 先保存车辆id和实际安排的出发时间
                        ArrayList<Integer> carSchedule = new ArrayList<>();
                        carSchedule.add(carId);
                        Car car = allCar.carsMap_.get(carId);
                        carSchedule.add(Math.max(startTime, car.planTime_));
                        // 根据每台车的起始点和终点查找路径
                        carSchedule.addAll(path.get(car.from_).get(car.to_));
                        // 下一台车在出发
                        if(count == maxNum){
                            ++startTime;
                            count = 1;
                        }
                        ++count;
                        answer.add(carSchedule);
                    }
                }
            }
        }
    }
}


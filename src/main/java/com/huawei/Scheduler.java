package com.huawei;

import edu.princeton.cs.algs4.In;

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
    public void SimpleSchedule(AllCar allCar, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path) {
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
            // 根据每台车的起始点和终点查找路径
            carSchedule.addAll(path.get(car.from_).get(car.to_));
            // 下一台车在出发
            if (count == 20) {
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
    public void SameSourceSchedule(AllCar allCar, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path) {
        answer = new ArrayList<>();
        int maxNum = 15; // 每次发15台车
        int startTime = 1;
        int count = 1;
        // 以15个出发点为一组进行调度。
        LinkedList<Integer> sources = new LinkedList<>(allCar.carsFrom_.keySet());
        while (!sources.isEmpty()) {
            // 取出15个出发点或者小于15个。
            LinkedList<Integer> partialSources = new LinkedList<>();
            while (count <= 15 && !sources.isEmpty()) {
                partialSources.add(sources.get(0));
                sources.removeFirst();
                ++count;
            }
            count = 0;
            // 对这些点出发的车进行发车，每次发maxNum台直到没有车为止。
            boolean flag = true;

            while (flag) {
                flag = false;
                for (int source : partialSources
                ) {
                    // 如果还能找到车
                    if (allCar.carsFrom_.get(source).isEmpty()) {
                        continue;
                    } else {
                        flag = true;
                        ArrayList<Integer> carIds = allCar.carsFrom_.get(source);
                        int carId = carIds.get(carIds.size() - 1);
                        // 删除一个carId
                        carIds.remove(carIds.size() - 1);
                        // 先保存车辆id和实际安排的出发时间
                        ArrayList<Integer> carSchedule = new ArrayList<>();
                        carSchedule.add(carId);
                        Car car = allCar.carsMap_.get(carId);
                        carSchedule.add(Math.max(startTime, car.planTime_));
                        // 根据每台车的起始点和终点查找路径
                        carSchedule.addAll(path.get(car.from_).get(car.to_));
                        // 下一台车在出发
                        if (count == maxNum) {
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

    //第三个方法，利用单次BFS的无环性质，直接发车，多次BFS之间做一个间隔。
    public void SingleBFS(AllCar allCar, ArrayList<HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>> bfsPath) {
        // 遍历所有路径
        int startTime = 1;
        int maxNum = 20;
        int count = 1;
        answer = new ArrayList<>();
        for (int i = 0; i < bfsPath.size(); ++i) {
            Set<Integer> fromeSet = bfsPath.get(i).keySet();
            HashMap<Integer, ArrayList<Integer>> pathMap;
            // 对于每条路径
            for (Integer from : fromeSet) {
                // 找到以from为起点的pathMap
                pathMap = bfsPath.get(i).get(from);
                // 对每一条的起点和终点的路径都遍历一次
                for (Integer to : pathMap.keySet()) {
                    if (allCar.fromToCarsId.containsKey(from) && allCar.fromToCarsId.get(from).containsKey(to)) {
                        // 把arrayList里面的carId都拿出来规划路径，然后清空
                        ArrayList<Integer> carIds = allCar.fromToCarsId.get(from).get(to);
                        if (!carIds.isEmpty()) {
                            for (int index = 0; index < carIds.size(); ++index) {
                                ArrayList<Integer> carSchedule = new ArrayList<>();
                                carSchedule.add(carIds.get(index));
                                carSchedule.add(Math.max(startTime, allCar.carsMap_.get(carIds.get(index)).planTime_));
                                // 加入路径
                                carSchedule.addAll(pathMap.get(to));
                                if (count == maxNum) {
                                    ++startTime;
                                    count = 1;
                                }
                                ++count;
                                answer.add(carSchedule);
                            }
                            // 清空carIds数组
                            carIds.clear();
                        }
                    }
                }
            }
        }
    }


    // 一个简单的负载均衡算法，大致算出每条路上某个时刻的车的数量，如果超过了该数量，则改变发车时间，向前或者向后找到合适的发车时间。
    // 求一个宽松一点的解，计算车在路上的行驶时间，然后乘以一个系数，这段时间假设车都在路上。
    // 调整一次后可以从最晚发车时间开始往前移，通过多次迭代使得最晚的发车时间提前。
    public void LoadBalancing(AllCar allCar, AllRoad allRoad) {
        int godPara = 1; // 一个可以调节车在路上的时间的参数
        // 一个数组负责保存每个时刻路上的车的情况，还要实时更新allRoad里面的每条路的已发车情况。
        ArrayList<HashMap<Integer, Integer>> carsPlanedTime = new ArrayList<>();
        // 直接遍历现在已经确定了路径的初步方案，通过调整发车时间来完善。
        for (ArrayList<Integer> route : answer) {
            int carId = route.get(0);
            //int planTime = route.get(1);
            int startRoadId = route.get(2);
            // 先计算一下走过所有路口的长度。不考虑速度。
            int totalLength = 0;
            for (int i = 2; i < route.size(); ++i) {
                totalLength += allRoad.roadsMap_.get(route.get(i)).length_;
            }
            // 粗略除以车的速度计算车在路上的时间
            int timeInRoad = totalLength / allCar.carsMap_.get(carId).speed_;
            timeInRoad /= godPara;
            ++timeInRoad;
            // 车的开始发车时间和到达终点的时间。开始上路时间实际取决于规定最早时间和已排队发车所需时间的最大值。
            // 根据已有的发车路口的车的情况计算一个最早发车时间。
            int earliestTime = allRoad.plannedDeparture_.get(startRoadId) / allRoad.roadsMap_.get(startRoadId).channel_ + 1;
            int startTime = Math.max(allCar.carsMap_.get(carId).planTime_, earliestTime);
            int endTime = startTime + timeInRoad;
            // 如果目前的arraylist还没有记录到该时间片，则拓展它。
            if (endTime > carsPlanedTime.size()) {
                int add = endTime - carsPlanedTime.size();
                for (int i = 0; i < add; ++i) {
                    HashMap<Integer, Integer> newCarsPlanedTime = new HashMap<>();
                    newCarsPlanedTime.putAll(allRoad.carsInRoad_);
                    carsPlanedTime.add(newCarsPlanedTime);
                }
            }
            // 判断是否超出了路上车的最大量
            boolean legal = true;
            // 记下一个最迟发生冲突的情况，后面调整时间是可以直接从这个时间开始。
            int lastIllegal = -1;
            // 从第一条路开始
            for (int i = 2; i < route.size(); ++i) {
                // 从starttime到endtime
                for (int j = startTime - 1; j < endTime; ++j) {
                    // 判断是否超出路的车的容量
                    if (carsPlanedTime.get(j).get(route.get(i)) >= allRoad.roadsMap_.get(route.get(i)).maxCars_) {
                        legal = false;
                        if(lastIllegal < j){
                            lastIllegal = j;
                        }
                    }
                }
                //if (!legal) {
                    //break;
                //}
            }
            // 如果能够加入，则调整出发时间为starttime,调整其他数据结构
            if (legal) {
                route.set(1, startTime);
                // 更新已发车数据，路上的车数据
                allRoad.plannedDeparture_.put(startRoadId, allRoad.plannedDeparture_.get(startRoadId)+1);
                // 更新路上车的数据
                for(int i = startTime -1; i < endTime; ++i){
                    // 对应的roadId的车辆数增加１
                    for(int j = 2; j < route.size(); ++j){
                        int preNumber = carsPlanedTime.get(i).get(route.get(j));
                        carsPlanedTime.get(i).put(route.get(j), preNumber+1);
                    }
                }
            }
            // 如果不能在当前时间下发车，则向后搜索可以发车的方案
            else{
                // 从lastIllegal开始向后搜索。
                int newStartTime = lastIllegal + 1;
                while(!legal && newStartTime <= carsPlanedTime.size()){
                    legal = true;
                    // 判断从newstartTime开始是否能够找到一个合适的位置
                    int newEndTime = newStartTime + timeInRoad;
                    // for循环的最大值
                    int maxTime = Math.min(newEndTime, carsPlanedTime.size());
                    for(int roadIdIndex = 2; roadIdIndex < route.size(); ++roadIdIndex){
                        for(int time = newStartTime -1; time < maxTime; ++time){
                            if(carsPlanedTime.get(time).get(route.get(roadIdIndex)) >= allRoad.roadsMap_.get(route.get(roadIdIndex)).maxCars_){
                               legal = false;
                               break;
                            }
                        }
                        if(!legal){
                            break;
                        }
                    }
                    if(legal){
                        break;
                    }
                    else{
                        ++newStartTime;
                    }
                }

                // 判断是否找到了新的出发时间
                if(legal){
                    // 判断是否需要扩展时间片的记录
                    int newEndTime = newStartTime + timeInRoad;
                    if(newEndTime > carsPlanedTime.size()){
                        int add = newEndTime - carsPlanedTime.size();
                        for (int i = 0; i < add; ++i) {
                            HashMap<Integer, Integer> newPlanedTime = new HashMap<>(allRoad.carsInRoad_);
                            //newPlanedTime.putAll(allRoad.carsInRoad_);
                            carsPlanedTime.add(newPlanedTime);
                        }
                    }
                    // 开始更新车辆上路的时间以及数据
                    route.set(1, newStartTime);
                    // 更新已发车数据，路上的车数据
                    allRoad.plannedDeparture_.put(startRoadId, allRoad.plannedDeparture_.get(startRoadId)+1);
                    // 更新路上车的数据
                    for(int i = newStartTime -1; i < newEndTime; ++i){
                        // 对应的roadId的车辆数增加１
                        for(int j = 2; j < route.size(); ++j){
                            int preNumber = carsPlanedTime.get(i).get(route.get(j));
                            carsPlanedTime.get(i).put(route.get(j), preNumber+1);
                        }
                    }
                }
                else{
                    // 如果还没找到一个合适的位置，则直接从最新的已记录时间片开始
                    // 这时的newStartTime其实是原来的cars.PlanedTime.size()
                    int newEndTime = newStartTime + timeInRoad;
                    // 更新数组
                    for(int i = 0; i < timeInRoad; ++i){
                        HashMap<Integer, Integer> newPlanedTime = new HashMap<>(allRoad.carsInRoad_);
                        carsPlanedTime.add(newPlanedTime);
                    }
                    route.set(1, newStartTime);
                    allRoad.plannedDeparture_.put(startRoadId, allRoad.plannedDeparture_.get(startRoadId)+1);
                    for(int i = newStartTime -1; i < newEndTime; ++i){
                        // 对应的roadId的车辆数增加１
                        for(int j = 2; j < route.size(); ++j){
                            int preNumber = carsPlanedTime.get(i).get(route.get(j));
                            carsPlanedTime.get(i).put(route.get(j), preNumber+1);
                        }
                    }
                }
            }
        }

        // 最后做一次过滤，将超过了500的发车时间对500取模。
        for (ArrayList<Integer> route: answer
             ) {
            if(route.get(1) > 500){
                route.set(1, route.get(1)%500);
            }
        }
    }
}


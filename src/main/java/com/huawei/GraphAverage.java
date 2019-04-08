package com.huawei;

import edu.princeton.cs.algs4.In;

import java.util.*;

// 根据已知路线，挑选路上车比较稀疏的路线，然后改变一些车的出发时间和路线。
public class GraphAverage {
    Integer godPara = 2;
    // 用于判断路上车辆的系数
    double carsPara = 0.5;
    //保存某时刻路上车的数量，HashMap<RoadID, HashMap<Time, Number>>
    private HashMap<Integer, HashMap<Integer, Integer>> isCongestion_;

    // 一个用于搜索路径的广度优先搜索
    private BFSSolution bfsSolution;

    // 用于记录可以进行发车调整的车, 用hashmap记录起点和终点，方便bfs进行查找 格式<from, < to, Cars>>
    private HashMap<Integer, HashMap<Integer, LinkedList<Car>>> toChangePath;

    private AllCar allCar_;
    // 根据输出答案的情况，记录路上每个时刻车的数量。
    public void Init(ArrayList<ArrayList<Integer>> answer, AllCar allCar, AllRoad allRoad, AllCross allCross, Graph graph){
        allCar_ = allCar;

        toChangePath = new HashMap<>();
        // 初始化IsCongestion
        isCongestion_ = new HashMap<>();
        for(Road road: allRoad.roads_){
            isCongestion_.put(road.id_, new HashMap<>());
        }
        // 初始化bfs
        bfsSolution = new BFSSolution(allRoad, isCongestion_, graph);
        // 初始化可以进行调整的车
        for(Cross crossFrom : allCross.cross_){
            toChangePath.put(crossFrom.id_, new HashMap<>());
            for(Cross crossTo: allCross.cross_){
                if(crossFrom.id_ == crossTo.id_){
                    continue;
                }
                else{
                    toChangePath.get(crossFrom.id_).put(crossTo.id_, new LinkedList<>());
                }
            }
        }


        for(ArrayList<Integer> path: answer){
            Car car = allCar.carsMap_.get(path.get(0));
            Integer presetTime = path.get(1);
            Boolean exceed = false;
            for(int i = 2; i < path.size(); ++i){
                Integer roadId = path.get(i);
                Road road = allRoad.roadsMap_.get(roadId);
                HashMap<Integer, Integer> roadCondition = isCongestion_.get(roadId);
                // 这里计算存在时间的时候直接计算两倍的存在时间，增加对拥塞的判断
                for(int j = presetTime; j <= presetTime + (int)((float)road.length_ * godPara/ Math.min(car.speed_, road.speed_)+1); ++j){
                    if(roadCondition.get(j) != null){
                        roadCondition.replace(j, roadCondition.get(j)+1);
                        // 如果此时已经超出了路的限制
                        if(roadCondition.get(j) > (int)(road.length_*road.channel_*carsPara)){
                            exceed = true;
                        }
                    }
                    else{
                        roadCondition.put(j, 1);
                    }
                }
                presetTime = presetTime + (int)((float)road.length_ / Math.min(car.speed_, road.speed_) + 1);
                // 如果超过了范围，则可以考虑改变线路
                if(exceed){
                    toChangePath.get(car.from_).get(car.to_).add(car);
                }
            }
        }
    }

    // 调整答案，在前面找到较空发车位置，调整一些发车时间和路径。
    public ArrayList<ArrayList<Integer>> AdjustInsert(ArrayList<ArrayList<Integer>> answer){
        // 用一个hashmap保存新的发车路径和时间。 <carid, <plantime, >>
        HashMap<Integer, ArrayList<Integer>> newCarPaths = new HashMap<>();
        // 答案先排序一下
        Collections.sort(answer, new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> integers, ArrayList<Integer> t1) {
                return integers.get(1) - t1.get(1);
            }
        });
        // 最晚的出发时间
        int latestTime = answer.get(answer.size()-1).get(1);
        // 往前挪500
        latestTime -= 500;
        // 从预置发车之后的时间开始
        int interval = 50;
        double godPara = 0.3;
        for(int startTime = 800; startTime < latestTime; startTime += interval){
            // 搜索一次路径出来
            bfsSolution.GetPaths(startTime, interval, godPara);
            // 对于每一条路

            for(Integer from: bfsSolution.path_.keySet()){
                if(bfsSolution.path_.get(from).isEmpty()){
                    continue;
                }
                else{
                    // 说明有以from为起点的路径。
                    HashMap<Integer, ArrayList<Integer>> toPaths = bfsSolution.path_.get(from);
                    for(Integer to: toPaths.keySet()){
                        // 查看是否有需要更改路径的车
                        if(toChangePath.get(from).get(to).isEmpty()){
                            continue;
                        }
                        else{
                            // 取出carid
                            Car car = toChangePath.get(from).get(to).pollFirst();
                            ArrayList<Integer> newpath = new ArrayList<>();
                            newpath.add(car.id_);
                            newpath.add(startTime);
                            newpath.addAll(toPaths.get(to));
                            newCarPaths.put(car.id_, newpath);
                        }
                    }
                }
            }
        }

        // 整理answer,碰到有新的路径的车，就替换。
        ArrayList<ArrayList<Integer>> resultAnswer = new ArrayList<>();
        for(int i = 0; i < answer.size(); ++i){
            if(newCarPaths.containsKey(answer.get(i).get(0))){
                // 时间必须提前了才替换，否则没有意义
                if(answer.get(i).get(1) > newCarPaths.get(answer.get(i).get(0)).get(1)){
                    resultAnswer.add(newCarPaths.get(answer.get(i).get(0)));
                }
                else{
                    resultAnswer.add(answer.get(i));
                }
            }
            else{
                resultAnswer.add(answer.get(i));
            }
        }

        return resultAnswer;
    }
}


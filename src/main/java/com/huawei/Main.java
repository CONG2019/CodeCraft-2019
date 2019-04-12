package com.huawei;

import edu.princeton.cs.algs4.In;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args)
    {
        // 初始化日志
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
        if (args.length != 5) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        // 接口更改
        // 存放已有路径
        String presetAnswerPath = args[3];
        String answerPath = args[4];
        PresetAnswer presetAnswer = new PresetAnswer();
        presetAnswer.Init(presetAnswerPath);
        AllCar allPriorityCar = new AllCar();
        allPriorityCar.Init(carPath, presetAnswer, true, false);
        AllCar allCommonCar = new AllCar();
        allCommonCar.Init(carPath, presetAnswer, false, false);
        AllCar allCar = new AllCar();
        allCar.Init(carPath, presetAnswer, true, true);
        AllRoad allRoad = new AllRoad();
        allRoad.Init(roadPath);
        AllCross allCross = new AllCross();
        allCross.Init(crossPath, allRoad);
        // 判断一下是哪个图？
        // 图1的crossid包含id是11的
        if(allCross.crossMap_.containsKey(11)){
            System.out.print("It's map1!\n");
        }
        else{
            System.out.print("It's map2!\n");
        }
        //return ;
        // 测试Graph
        Graph graph = new Graph();
        graph.Init(allCross, allRoad);
        // 构造函数传入道路的信息
        /*
          初赛方案
         */
        MinPath bfsSolution = new MinPath(allRoad, allCross);
        bfsSolution.IsCongestion_Init(presetAnswer, allCar);
//        bfsSolution.GetPaths(graph);
        Scheduler schedulerPriority = new Scheduler(allCross, presetAnswer, true);
        ArrayList<ArrayList<Integer>> allAnswer;
        // 先求出优先车辆的路径。
        int startTime = schedulerPriority.Schedule(allPriorityCar, bfsSolution, graph, allRoad, 0);
        allAnswer = schedulerPriority.answer;

        // 再求普通车辆的路径
        Scheduler schedulerCommon = new Scheduler(allCross, presetAnswer, false);
        startTime = schedulerCommon.Schedule(allCommonCar, bfsSolution, graph, allRoad, startTime);
        allAnswer.addAll(schedulerCommon.answer);

        /*
            用一个hashmap保存carid和carid在allanswer中的位置的关系，方便修改answer
         */
        HashMap<Integer, Integer> CarIdToIndex = new HashMap<>();
        for(int i = 0; i < allAnswer.size(); ++i){
            CarIdToIndex.put(allAnswer.get(i).get(0), i);
        }


        // 初始化一个用于调整路径的bfs
        //BFSSolution adjustPath = new BFSSolution(allRoad, bfsSolution.GetIsCongestion(), graph);
        // 初始化一个用于调整路径的dijkstra
        Dijkstra adjustDij = new Dijkstra(allRoad, bfsSolution.GetIsCongestion());
        //迭代运行判题器
        while(true){
            JudgeApp judgeApp = new JudgeApp(allCar, allRoad, allCross, allAnswer, presetAnswer, graph);
            judgeApp.Init();
            // 如果死锁，返回死锁的crossid和roadid还有carid。
            ArrayList<Integer> deadlockIds = judgeApp.Judge();
            if(deadlockIds != null){
                // 发生死锁，取出死锁的路口，路和车
                HashSet<Integer> deadLockCrossId = new HashSet<>();
                HashSet<Integer> deadLockRoadId = new HashSet<>();
                ArrayList<Integer> deadLockCarId = new ArrayList<>();
                int deadLockTime = 0;
                int index = 0;
                for(int i = 0; i < deadlockIds.size(); i++){
                    if(deadlockIds.get(i) == 0){
                        ++index;
                    }
                    else{
                        if(index == 0){
                            deadLockCrossId.add(deadlockIds.get(i));
                        }
                        else if(index == 1){
                            deadLockRoadId.add(deadlockIds.get(i));
                        }
                        else if(index == 2){
                            deadLockCarId.add(deadlockIds.get(i));
                        }
                        else{
                            deadLockTime = deadlockIds.get(i);
                        }
                    }
                }

                // 调用一次bfs进行寻路。
                //adjustPath.GetPaths(deadLockTime, 1, deadLockRoadId);
                //HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> newPaths = adjustPath.path_;

                // 调用dij求最短路径。
                adjustDij.GetShortPath(graph, deadLockTime, 1, deadLockRoadId);
                HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> newPaths = adjustDij.path_;
                int proportion;
                int timePreset;
                int time;
                if(deadLockTime > 1000){
                    timePreset = 0;
                    proportion = 3;
                    time = 10;
                }
                else{
                    proportion = 1;
                    timePreset = 1;
                    time = -10;
                }

                int toChangeCars = deadLockCarId.size() / proportion;
                // 对每一辆车进行
                for(int i = 0; i < toChangeCars; ++i){
                    // 判断是否预置车
                    if(CarIdToIndex.containsKey(deadLockCarId.get(i))){
                        // 找到car
                        Car toChangeCar = allCar.carsMap_.get(deadLockCarId.get(i));
                        // 查看是否在路径中
                        if(newPaths.containsKey(toChangeCar.from_)){
                            // 是否能找到路
                            ArrayList<Integer> newPath = newPaths.get(toChangeCar.from_).get(toChangeCar.to_);
                            if( newPath != null){
                                // 新建路径并替换
                                ArrayList<Integer> toChangePath = new ArrayList<>();
                                toChangePath.add(toChangeCar.id_);
                                // 还是按照原来的出发时间
                                toChangePath.add(allAnswer.get(CarIdToIndex.get(toChangeCar.id_)).get(1)+timePreset);
                                toChangePath.addAll(newPath);
                                allAnswer.set(CarIdToIndex.get(toChangeCar.id_), toChangePath);
                            }
                            // 如果找不到路径则改变一下时间
                            else{
                                ArrayList<Integer> path = allAnswer.get(CarIdToIndex.get(toChangeCar.id_));
                                path.set(1, Math.max(path.get(1)-time, toChangeCar.planTime_));
                            }
                        }
                        // 这个种情况也是找不到
                        else{
                            ArrayList<Integer> path = allAnswer.get(CarIdToIndex.get(toChangeCar.id_));
                            path.set(1, Math.max(path.get(1)-time, toChangeCar.planTime_));
                        }
                    }
                }
            }
            else{
                break;
            }
            allCar = new AllCar();
            allCar.Init(carPath, presetAnswer, true, true);
            allRoad = new AllRoad();
            allRoad.Init(roadPath);
            allCross = new AllCross();
            allCross.Init(crossPath, allRoad);
        }



        OutPut.WriteAnswer(allAnswer, answerPath);
        logger.info("End...");
    }

}
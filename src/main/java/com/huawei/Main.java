package com.huawei;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;

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
        AllCar allCar = new AllCar();
        allCar.Init(carPath);
        AllRoad allRoad = new AllRoad();
        allRoad.Init(roadPath);
        AllCross allCross = new AllCross();
        allCross.Init(crossPath);
        PresetAnswer presetAnswer = new PresetAnswer();
        presetAnswer.Init(presetAnswerPath);
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
        //BFSSolution bfsSolution = new BFSSolution(allRoad);
        //bfsSolution.GetPaths(graph);
        //Dijkstra dijkstra = new Dijkstra(allRoad);
        //dijkstra.GetShortPath(graph);
        //Scheduler scheduler = new Scheduler();
        //scheduler.SimpleSchedule(allCar, bfsSolution.path_, dijkstra.path_ );
        //scheduler.LoadBalancing(allCar, allRoad);
        //scheduler.AverageBalance(allCar);
        //scheduler.SimpleSchedule(allCar, dijkstra.path_);
        //scheduler.SameSourceSchedule(allCar, dijkstra.path_);
        //scheduler.SameSourceSchedule(allCar, bfsSolution.path_);
        //scheduler.SingleBFS(allCar, bfsSolution.bfsPath_);

        /*
          初赛方案
         */
        MinPath bfsSolution = new MinPath(allRoad, allCross);
//        bfsSolution.GetPaths(graph);
        Scheduler scheduler = new Scheduler(allCross);
        scheduler.Schedule(allCar, bfsSolution, graph, allRoad);
        OutPut.WriteAnswer(scheduler, answerPath);
        // ArrayList<Road> adjCross1 = graph.Adj(1);
        // int a = graph.OutDegree(1);
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");

        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }
}
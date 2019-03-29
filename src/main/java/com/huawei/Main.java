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
        if (args.length != 4) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        AllCar allCar = new AllCar();
        allCar.Init(carPath);
        AllRoad allRoad = new AllRoad();
        allRoad.Init(roadPath);
        AllCross allCross = new AllCross();
        allCross.Init(crossPath);

        // 测试Graph
        Graph graph = new Graph();
        graph.Init(allCross, allRoad);
        // 构造函数传入道路的信息
        BFSSolution bfsSolution = new BFSSolution(allRoad);
        bfsSolution.GetPaths(graph);
        Dijkstra dijkstra = new Dijkstra(allRoad);
        dijkstra.GetShortPath(graph);
        Scheduler scheduler = new Scheduler();
        scheduler.SimpleSchedule(allCar, bfsSolution.path_, dijkstra.path_ );
        //scheduler.LoadBalancing(allCar, allRoad);
        //scheduler.AverageBalance(allCar);
        //scheduler.SimpleSchedule(allCar, dijkstra.path_);
        //scheduler.SameSourceSchedule(allCar, dijkstra.path_);
        //scheduler.SameSourceSchedule(allCar, bfsSolution.path_);
        //scheduler.SingleBFS(allCar, bfsSolution.bfsPath_);
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
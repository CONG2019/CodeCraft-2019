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
        allCross.Init(crossPath);
        // 判断一下是哪个图？
        // 图1的crossid包含id是11的
        if(allCross.crossMap_.containsKey(22)){
            System.out.print("It's map1!\n");
        }
        else{
            System.out.print("It's map2!\n");
        }
        //return ;
        // 测试Graph
        Graph graph = new Graph();
        graph.Init(allCross, allRoad);

        /*
         * just for test
         *
         */
        //DFSSolution dfsSolution = new DFSSolution();
        //dfsSolution.GetAllPaths(graph);
        /*
          初赛方案
         */
        MinPath bfsSolution = new MinPath(allRoad, allCross);
        bfsSolution.IsCongestion_Init(presetAnswer, allCar);
//        bfsSolution.GetPaths(graph);
        Scheduler schedulerPriority = new Scheduler(allCross, presetAnswer, true);
        ArrayList<ArrayList<Integer>> allAnswer;
        // 先求出优先车辆的路径。
        int startTime = schedulerPriority.Schedule(allPriorityCar, bfsSolution, graph, allRoad, 750);
        allAnswer = schedulerPriority.answer;

        // 再求普通车辆的路径
        Scheduler schedulerCommon = new Scheduler(allCross, presetAnswer, false);
        startTime = schedulerCommon.Schedule(allCommonCar, bfsSolution, graph, allRoad, startTime);
        allAnswer.addAll(schedulerCommon.answer);


        // 优化答案
        GraphAverage graphAverage = new GraphAverage();
        graphAverage.Init(allAnswer, allCar, allRoad, allCross, graph);
        allAnswer = graphAverage.AdjustInsert(allAnswer);
        OutPut.WriteAnswer(allAnswer, answerPath);
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
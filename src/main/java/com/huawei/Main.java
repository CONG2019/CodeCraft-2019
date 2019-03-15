package huawei;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");

        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }
}
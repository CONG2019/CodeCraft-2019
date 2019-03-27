package com.huawei;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AllRoad {
    public ArrayList<Road> roads_;
    // 根据路的id找到对应的路
    public HashMap<Integer, Road> roadsMap_;
    // 一个用于记录每条路上的已经发车的数量的hashmap，整体优化的时候需要用
    // <RoadId, plannedNumber>
    public HashMap<Integer, Integer> plannedDeparture_;
    // 一个map用于记录每个时刻每条路上的车的数量
    public HashMap<Integer, Integer> carsInRoad_;
    // 通过传入的文件名读入参数
    public void Init(String roadFileName){
        // 打开文件
        roads_ = new ArrayList<>();
        roadsMap_ = new HashMap<>();
        plannedDeparture_ = new HashMap<>();
        carsInRoad_ = new HashMap<>();
        // 算一下所有路每个时刻最多能够容纳的车辆数目。
        int carsInAllRoad = 0;
        try (FileInputStream inputStream = new FileInputStream(roadFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 存放每一行数据的 string
            String str = null;
            while((str = bufferedReader.readLine()) != null){
                // 如果是注释则忽略
                if(str.charAt(0) == '#'){
                    continue;
                }
                else{
                    str = str.substring(1, str.length()-1);
                    str = str.replace(" ", "");
                    // 使用string的分割函数
                    String[] road = str.split(",");
                    // 添加新的道路进去
                    Road oneRoad = new Road(road);
                    roads_.add(oneRoad);
                    roadsMap_.put(oneRoad.id_, oneRoad);
                    carsInAllRoad += oneRoad.maxCars_;
                    // 添加发车量为０
                    plannedDeparture_.put(oneRoad.id_, 0);
                    carsInRoad_.put(oneRoad.id_, 0);
                    // 如果存在双向道路的时候，新建一个-RoadId的道路，最后的时候还原即可。
                    if(oneRoad.isDuplex_ == 1){
                        Road anotherRoad = new Road(road);
                        // 交换from和to
                        int tmp = anotherRoad.from_;
                        anotherRoad.from_ = anotherRoad.to_;
                        anotherRoad.to_ = tmp;
                        // 如果roadid为０会出现问题，少了一条路。
                        anotherRoad.id_ = -anotherRoad.id_;
                        roads_.add(anotherRoad);
                        roadsMap_.put(anotherRoad.id_, anotherRoad);
                        plannedDeparture_.put(anotherRoad.id_, 0);
                        carsInRoad_.put(anotherRoad.id_, 0);
                        carsInAllRoad += oneRoad.maxCars_;
                    }
                }
            }
            bufferedReader.close();
            //inputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

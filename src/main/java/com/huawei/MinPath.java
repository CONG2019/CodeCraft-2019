package com.huawei;

import java.util.*;

public class MinPath {

    //采用hashmap保存路径信息
    //第一个Integer表示起始点， 第二个Integer表示目的地，ArrayList保存从起点到目的地的路径
    HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> Path_;

    public AllRoad allRoad_;
    //在构造器中将所有道路信息传入MinPath类中
    private double p;
    public MinPath(AllRoad allRoad, AllCross allCross){
        this.allRoad_ = allRoad;
        IsCongestion = new HashMap<>();
        for (Road road: allRoad.roads_
        ) {
            IsCongestion.put(road.id_, new HashMap<>());
        }

        if(allCross.crossMap_.containsKey(22)){
//            System.out.print("It's map1!\n");
            p = 1.0;
        }
        else{
//            System.out.print("It's map2!\n");
            p = 1.0;
        }
    }

    //保存某时刻路上车的数量，HashMap<RoadID, HashMap<Time, Number>>
    private HashMap<Integer, HashMap<Integer, Integer>> IsCongestion;
    public HashMap<Integer, HashMap<Integer, Integer>> GetIsCongestion(){
        return IsCongestion;
    }
    //创建一个车队列，优先为没有成功安排路线的车安排路线
    private ArrayList<Car> CarList = new ArrayList<>();
    /*
    根据道路车的数量为每一辆车搜索路劲，当路上车的数量大与设定值是，则认为这条路是不通的，车无法通过，如果经过一次最短路径搜索后车还是没有
    找到去目的地的路径，则返回null，下次优先为这台车搜索路径
     */
    public ArrayList<Integer> SuitablePath(Graph graph, Car car, int Time){

        // 找出最大的顶点的索引值,最大CrossID
        int maxIndex = Collections.max(graph.GetV());
        // 找出最大的顶点的索引值
        // 这里初始值是false吗？
        boolean[] marked = new boolean[maxIndex+1];
        boolean IsFinded = false;

        // 记录RoadId的hashmap， <CrossID, Road>这样保存
        HashMap<Integer, Road> edgeTo = new HashMap<>();
        // 一个队列用于保存遍历到的路径
        LinkedList<Road> queue = new LinkedList<>();

        //记录原点到各点的最短距离
       //int[] MinLength = new int[maxIndex+1];
        //记录原点到各定点的时间
        int[] MinTime = new int[maxIndex+1];
        for(int i = 0; i<MinTime.length; i++){
            //MinLength[i] = Integer.MAX_VALUE;
            MinTime[i] = Integer.MAX_VALUE;
        }
        //MinLength[car.from_] = 0;    //原点到自己的距离为0
        MinTime[car.from_] = 0;

        // 将起点的邻接边加入到队列
        edgeTo.put(car.from_, null);
        for (Road road: graph.Adj(car.from_)) {
            queue.add(road);
            //标记原点到第一层定点的距离
            //MinLength[road.to_] = road.getLength_();
            marked[road.to_] = true;
            MinTime[road.to_] = Time + (int)((float)road.length_ / Math.min(car.speed_, road.speed_) + 1);
            edgeTo.put(road.to_, road);
        }

        while (!queue.isEmpty()){
            // 弹出下一个Road,Road的to作为下一个顶点
            Road road = queue.poll();
            // 找出该路的终点，作为新的起点
            int to = road.to_;
            // 遍历以该点为起点的边
            for (Road outRoad: graph.Adj(to)){
                int tmp_to = outRoad.to_;
                HashMap<Integer, Integer> RoadCondition = IsCongestion.get(outRoad.id_);

                //先判断这条路是否适合走
                boolean flag = true;
                for(int i = MinTime[outRoad.from_]; i <= MinTime[outRoad.from_] + (int)((float)outRoad.length_ / Math.min(car.speed_, outRoad.speed_) + 1); i++){
//                    int roadCondition = RoadCondition.get(i);
                    //如果发现某个时刻车的数量超出预定值，则此路不通
                    if(RoadCondition.get(i) != null && RoadCondition.get(i) > (int)(outRoad.length_ * outRoad.channel_ * p)){
                        flag = false;
                        break;
                    }
                }
                //如果如不通，进入下一次循环
                if(!flag){
                    continue;
                }

                //如果发现更短的路径，则更改路径
                //第一次访问的定点的路劲是最长的
                //if(MinLength[tmp_to] > MinLength[outRoad.from_] + outRoad.getLength_()){
                    //如果顶点是第一次访问的话，就需要将顶点推入队列, 或顶点有新的更短的路径
                if(!marked[tmp_to]){
                    queue.add(outRoad);
                    //更改原点到改点的距离
                    //MinLength[tmp_to] = MinLength[outRoad.from_] + outRoad.getLength_();
                    MinTime[tmp_to] = MinTime[outRoad.from_] + (int)((float)outRoad.length_ / Math.min(car.speed_, outRoad.speed_) + 1);
                    // 更改路劲RoadId,移除旧路线
                    //不能用replace, 因为如果是新路径的话就不能添加了
                    //edgeTo.remove(outRoad.to_);
                    marked[tmp_to] = true;
                    edgeTo.put(outRoad.to_, outRoad);

                    if(tmp_to == car.to_){
                        IsFinded = true;
                    }
                //}
              }
            }
        }
        //如果找到路劲，就返回路径，否者，返回null
        if(IsFinded){
            ArrayList<Integer> path = new ArrayList<>();
            int tmp = car.to_;
            while (edgeTo.get(tmp) != null){
//                HashMap<Integer, HashMap<Integer, Integer>>;
                HashMap<Integer, Integer> roadcondition = IsCongestion.get(edgeTo.get(tmp).id_);
                for(int i = MinTime[edgeTo.get(tmp).from_]; i <= MinTime[edgeTo.get(tmp).from_] + (int)((float)edgeTo.get(tmp).length_ / car.speed_ + 1); i++){
                    //某时刻已经存在车，则车数量加1，否则增加该时刻的路况
                    if(roadcondition.get(i) != null){
                        roadcondition.replace(i, roadcondition.get(i)+1);
                    }else {
                        roadcondition.put(i, 1);
                    }
                }
                //添加路径
                path.add(edgeTo.get(tmp).id_);
                tmp = edgeTo.get(tmp).from_;
                if(tmp == car.from_){
                    break;
                }
            }
            //反向后返回路径
            Collections.reverse(path);
            return path;
        }else {
            return null;
        }
    }

    //道路状况初始化
    public void IsCongestion_Init(PresetAnswer presetAnswer, AllCar allCar){
        //保存某时刻路上车的数量，HashMap<RoadID, HashMap<Time, Number>>
        for (ArrayList<Integer> presetPath: presetAnswer.presetAnswer_
        ) {
            Integer carID = presetPath.get(0);
            Car car = allCar.carsMap_.get(carID);
            Integer presetTime = presetPath.get(1);
            //更改一台预置车上路后的路况
            for (int i = 2; i < presetPath.size(); i++){
                Integer roadID = presetPath.get(i);
                Road road = allRoad_.roadsMap_.get(roadID);
                HashMap<Integer, Integer> roadcondition = IsCongestion.get(roadID);
                for (int j = presetTime; j <= presetTime + (int)((float)road.length_*4 / Math.min(car.speed_, road.speed_) + 1); j++){
                    if(roadcondition.get(j) != null){
                        roadcondition.replace(j, roadcondition.get(j) + 1);
                    }else {
                        roadcondition.put(j, 1);
                    }
                }
                presetTime = presetTime + (int)((float)road.length_ / Math.min(car.speed_, road.speed_) + 1);
            }
        }
    }
}

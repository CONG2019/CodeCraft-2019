package com.huawei;

// 导入算法４的包
import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

// 每个点套用一次dijkstra算法
public class Dijkstra {
    // 保存路径
    // 由于路口的id不一定是按顺序的，所以这里不采用二维数组而是hashmap来保存
    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path_;

    private AllRoad allRoad_;

    //保存某时刻路上车的数量，HashMap<RoadID, HashMap<Time, Number>>
    private HashMap<Integer, HashMap<Integer, Integer>> isCongestion_;
    // 记录每次搜索的时候不能走的路
    private HashSet<Integer> forbidRoadIds_;
    // 寻路的时间起点和时间间隔
    private Integer startTime_ = 0;
    // private Integer interval_ = 0;
    private double para_ = 0.0;


    public Dijkstra(AllRoad allRoad, HashMap<Integer, HashMap<Integer, Integer>> isCongestion){
        allRoad_ = allRoad;
        isCongestion_ = isCongestion;
    }

    // 获得最短路径
    public void GetShortPath(Graph graph, Integer startTime,double godPara, HashSet<Integer> forbidRoadIds){
        startTime_ = startTime;
        para_ = godPara;
        forbidRoadIds_ = forbidRoadIds;
        path_ = new HashMap<>();
        // 对每个点都进行一次寻路
        for (Integer crossId: graph.GetV()
        ) {
            DijkstraSP(graph, crossId);
        }
    }
    private void DijkstraSP(Graph graph, int source){
        // 索引优先队列存放起点到某个点的距离，方便找出最短的路径, 大小为路口的总数
        IndexMinPQ<Integer> pq = new IndexMinPQ<>(Collections.max(graph.GetV())+1);
        // 保存路径长度
        HashMap<Integer, Integer> distTo = new HashMap<>();
        // 记录RoadId的hashmap,第一个值代表crossId，第二个值代表roadId
        HashMap<Integer, Integer> edgeTo = new HashMap<>();
        // 初始化最短路径的值, 都是最大值
        for(int v: graph.GetV()){
            distTo.put(v, Integer.MAX_VALUE);
        }
        // 起点到起点的最短路径是０
        distTo.put(source, 0);
        pq.insert(source, 0);
        while(!pq.isEmpty()){
            Relax(graph, pq.delMin(), distTo, edgeTo, pq);
        }
        GetPath(source, edgeTo);
    }

    // 放松最短的边
    private void Relax(Graph graph, int v, HashMap<Integer, Integer> distTo, HashMap<Integer, Integer> edgeTo, IndexMinPQ<Integer> pq){
        // 对该点出发的邻边，如果可以使其他路径变短，则更新
        for(Road road: graph.Adj(v)){
            if (forbidRoadIds_.contains(road.id_)) {
                continue;
            }
            boolean flag = true;
            HashMap<Integer, Integer> roadCondition = isCongestion_.get(road.id_);
            // 检查接下来的一段时间是否会堵塞,这里采用粗略计算的方式
            for(int i = startTime_; i < (startTime_+(int)((float)road.length_ / road.speed_) + 1); ++i){
                if(roadCondition.get(i) != null && roadCondition.get(i) > (int)(road.channel_*road.length_*para_)){
                    flag = false;
                    break;
                }
            }
            // 此路不能经过。
            if(!flag){
                continue;
            }
            int to = road.to_;
            if(distTo.get(to) > (distTo.get(v)+road.roadLength_)){
                // 更新最短路径的长度
                distTo.put(to, distTo.get(v)+road.roadLength_);
                // 记录一条边
                edgeTo.put(to, road.id_);
                // 更新一下优先队列记录的最短路径
                if(pq.contains(to)){
                    pq.changeKey(to, distTo.get(to));
                }
                else{
                    pq.insert(to, distTo.get(to));
                }
            }
        }
    }

    // 根据edgeTo查出对应的路径
    private void GetPath(int source, HashMap<Integer, Integer> edgeTo){
        // 记录从起点触发能够到达的点的路径。
        HashMap<Integer, ArrayList<Integer>> path = new HashMap<>();
        for(int to: edgeTo.keySet()){
            // 用一个数组保存路径的顺序，第一个RoadId的to作为终点。
            ArrayList<Integer> roadIds = new ArrayList<>();
            int tmp = to;
            while(edgeTo.containsKey(tmp)){
                roadIds.add(edgeTo.get(tmp));
                tmp = allRoad_.roadsMap_.get(edgeTo.get(tmp)).from_;
            }
            // 获得了整个路径的Roadid，反转后添加到path当中
            Collections.reverse(roadIds);
            path.put(to, roadIds);
        }
        // 最后添加到类的成员当中
        path_.put(source, path);
    }
}

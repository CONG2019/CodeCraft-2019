package com.huawei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

// 用邻接表表示的图
// 可以采用两种方案：第一种是采用hashmap，以路口即点的id作为key，value是该key出发的road。road由数组构成，分别是按照顺时针的方向的道路。
// 第二种是用ArrayList，但是要求cross是按顺序的，因为题目并不限制运行时间，所以直接用hashmap较好。
public class Graph {
    // hashmap表示的邻接表
    public HashMap<Integer, ArrayList<Road>> graph_;

    public void Init(AllCross crosses, AllRoad roads){
         graph_ = new HashMap<>();
         // 逐个路口读入，根据路口对应的roadId寻找road
        for (Cross cross: crosses.cross_) {
            // 根据cross的id和roadid来寻找路
            // 需要注意单双向的问题，如果是单向的
            ArrayList<Road> road = new ArrayList<>();
            if(cross.roadId1_ != -1){
                Road tmpRoad = roads.roadsMap_.get(cross.roadId1_);
                if(tmpRoad.from_ == cross.id_){
                    road.add(tmpRoad);
                }
                else{
                    if(tmpRoad.isDuplex_ == 1){
                        tmpRoad = roads.roadsMap_.get(-cross.roadId1_);
                        road.add(tmpRoad);
                    }
                }
            }
            if(cross.roadId2_ != -1){
                Road tmpRoad = roads.roadsMap_.get(cross.roadId2_);
                if(tmpRoad.from_ == cross.id_){
                    road.add(tmpRoad);
                }
                else{
                    if(tmpRoad.isDuplex_ == 1){
                        tmpRoad = roads.roadsMap_.get(-cross.roadId2_);
                        road.add(tmpRoad);
                    }
                }
            }
            if(cross.roadId3_ != -1){
                Road tmpRoad = roads.roadsMap_.get(cross.roadId3_);
                if(tmpRoad.from_ == cross.id_){
                    road.add(tmpRoad);
                }
                else{
                    if(tmpRoad.isDuplex_ == 1){
                        tmpRoad = roads.roadsMap_.get(-cross.roadId3_);
                        road.add(tmpRoad);
                    }
                }
            }
            if(cross.roadId4_ != -1){
                Road tmpRoad = roads.roadsMap_.get(cross.roadId4_);
                if(tmpRoad.from_ == cross.id_){
                    road.add(tmpRoad);
                }
                else{
                    if(tmpRoad.isDuplex_ == 1){
                        tmpRoad = roads.roadsMap_.get(-cross.roadId4_);
                        road.add(tmpRoad);
                    }
                }
            }
            graph_.put(cross.id_, road);
        }
    }
    // 提供操作图的方法，比如返回边集和点集合

    // 返回某个点的出度
    public int OutDegree(int v){
        return graph_.get(v).size();
    }

    // 返回某个点的邻接边
    public ArrayList<Road> Adj(int v){
        ArrayList<Road> result = graph_.get(v);
        Collections.sort(result);
        //Collections.shuffle(result);
        //Collections.reverse(result);
        return result;
    }

    // 返回所有key
    public Set<Integer> GetV(){
        return graph_.keySet();
    }

    public int GetVNumbers(){
        return graph_.size();
    }

}

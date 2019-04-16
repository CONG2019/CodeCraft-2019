package com.huawei;

import java.util.ArrayList;
import java.util.HashMap;

public class Tool {
    // 辅助函数，统计一下所有路径中某条路出现的总次数。

    public static void RoadCounts(ArrayList<ArrayList<Integer>> allAnswer, PresetAnswer answer, AllRoad allRoad){
        HashMap<Integer, Integer> counts = new HashMap<>();
        // 记录出发到结束点的数量。
        HashMap<Integer, HashMap<Integer, Integer>> fromToCars = new HashMap<>();
        for(ArrayList<Integer> path: answer.presetAnswer_){
            int from = allRoad.roadsMap_.get(path.get(2)).from_;
            int to = allRoad.roadsMap_.get(path.get(path.size()-1)).to_;
            if(fromToCars.containsKey(from)){
                if(fromToCars.get(from).get(to) != null){
                    int number = fromToCars.get(from).get(to);
                    fromToCars.get(from).put(to, number+1);
                }
                else{
                    fromToCars.get(from).put(to, 1);
                }
            }
            else{
                fromToCars.put(from, new HashMap<>());
                fromToCars.get(from).put(to, 1);
            }
            for(int i = 2; i < path.size(); ++i){
                if(counts.containsKey(path.get(i))){
                    counts.put(path.get(i), counts.get(path.get(i))+1);
                }
                else{
                    counts.put(path.get(i), 1);
                }
            }
        }
        // 打印出来
        for(Integer roadId: counts.keySet()){
            System.out.println("RoadId: " + Integer.toString(roadId) + "::" + Integer.toString(counts.get(roadId)));
        }
        System.out.println("Total Roads: " + Integer.toString(counts.size()));

        // 输出起点和终点的数量
        for(Integer from: fromToCars.keySet()){
            HashMap<Integer, Integer> toSet = fromToCars.get(from);
            for(Integer to: toSet.keySet()){
                System.out.println("From:" + Integer.toString(from) + " to: " + Integer.toString(to) + " counts: " + Integer.toString(toSet.get(to)));
            }
        }

        // 统计总的出发数
        System.out.println("All from: " + Integer.toString(fromToCars.size()));
    }
}

package com.huawei;

import java.util.ArrayList;
import java.util.HashMap;

public class Tool {
    // 辅助函数，统计一下所有路径中某条路出现的总次数。

    public static void RoadCounts(ArrayList<ArrayList<Integer>> allAnswer, PresetAnswer answer){
        HashMap<Integer, Integer> counts = new HashMap<>();
        for(ArrayList<Integer> path: allAnswer){
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
    }
}

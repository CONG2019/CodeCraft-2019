package com.huawei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

// 使用深度优先搜索找到两点之间的所有路径，然后按照路径的长度进行排序。
public class DFSSolution {
    // 一个多重嵌套hashmap负责保存找到的所有路径。所有路径应该按长度进行排序。 这样找路径肯定会爆内存，复杂网络的路径很多。
    public HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<Integer>>>> allPaths_;

    // 用于标记是否访问过某个点，hashmap的形式
    private HashMap<Integer, Boolean> marked_;

    private void Init(Graph graph){
        // 初始化需要用到的数据结构。
        allPaths_ = new HashMap<>();
        marked_ = new HashMap<>();
        Set<Integer> crossIds = graph.GetV();
        for (Integer from: crossIds) {
            marked_.put(from, false);
            allPaths_.put(from, new HashMap<>());
            for(Integer to: crossIds){
                if(from.equals(to)){
                    continue;
                }
                else{
                    allPaths_.get(from).put(to, new ArrayList<>());
                }
            }
        }
    }

    // 对每个点都进行一次深度优先遍历，找到所有的路径。
    public void GetAllPaths(Graph graph){
        Init(graph);
        Integer deepth = new Integer(0);
        ArrayList<Integer> path = new ArrayList<>();
        for(Integer crossId: graph.GetV()){
            DFS(graph, crossId, crossId, path, deepth);
        }
    }

    // 递归形式的深度优先遍历
    private void DFS(Graph graph, Integer from, Integer v, ArrayList<Integer> path, Integer deepth){
        // 标记已访问
        marked_.put(v, true);
        ++deepth;
        if(deepth > 30){
            --deepth;
            return;
        }
        // 如果不是起点，则找到一条路径。
        if(!from.equals(v)){
            allPaths_.get(from).get(v).add(new ArrayList<>(path));
        }
        // 继续遍历后面的路径。通过邻接边
        for(Road road: graph.Adj(v)){
            Integer to = road.to_;
            // 如果还没访问过
            if(!marked_.get(to)){
                // 添加一条路径。
                path.add(road.id_);
                DFS(graph, from, to, path, deepth);
                // 移除最后一个。
                path.remove(path.size()-1);
            }
        }
        marked_.put(v, false);
        --deepth;
    }
}

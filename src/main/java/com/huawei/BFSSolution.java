package com.huawei;

import java.sql.Array;
import java.util.*;

// 先简单的找一条路径出来。
// 类成员保存所有的路径，每台车都有不同的路径。
// 车的数量大，点的数量少，所以只需要找到每个点都所有点的路径即可。暂时先找出一条路径。

// 使用带记录路径的广度优先搜索。使用edgeTo来保存路径，比如edgeTo[w] = v表示从road_v到达road_w;edgeTo[w]=-1作为起始道路。
// 如果速度慢可以开多线程搜索
public class BFSSolution {
    // 由于路口的id不一定是按顺序的，所以这里不采用二维数组而是hashmap来保存
    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path_;

    // 一个复杂的结构，用于记录从某个点开始进行广度优先遍历能够找到所有的路径
    // 规划路线的时候按顺序遍历路径进行发车。
    public ArrayList<HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>> bfsPath_;
    // 存放road的信息
    public AllRoad allRoad_;

    public BFSSolution(AllRoad allRoad){
        allRoad_ = allRoad;
    }

    public void GetPaths(Graph graph){
        path_ = new HashMap<>();
        bfsPath_ = new ArrayList<>();
        // 对每个点都进行一次寻路
        for (Integer crossId: graph.GetV()
             ) {
            BFS(graph, crossId);
        }
    }

    /*
    @BFS: 搜索某一点作为起点的路径
    @Graph graph: 出入地图信息
    @int source: 出发点
     */
    private void BFS(Graph graph, int source){
        // 找出最大的顶点的索引值
        int maxIndex = Collections.max(graph.GetV());
        // 这里初始值是false吗？
        boolean[] marked = new boolean[maxIndex+1];
        // 记录RoadId的hashmap
        HashMap<Integer, Integer> edgeTo = new HashMap<>();
        // 一个队列用于保存遍历到的路径
        LinkedList<Road> queue = new LinkedList<>();
        // 标记一下起点
        marked[source] = true;
        // 将起点的邻接边加入到队列
        for (Road road: graph.Adj(source)) {
            queue.add(road);
            // 标记为已访问
            marked[road.to_] = true;
            // 这里可能有问题，如果roadId出现了-1的时候
            edgeTo.put(road.id_, -1);
        }

        while(!queue.isEmpty()){
            // 弹出下一个Road,Road的to作为下一个顶点
            Road road = queue.poll();
            // 找出该路的终点，作为新的起点
            int to = road.to_;
            // 遍历以该点为起点的边
            for (Road outRoad: graph.Adj(to)) {
                int tmp_to = outRoad.to_;
                if(!marked[tmp_to]){
                    // 标记一下父路径的RoadId
                    edgeTo.put(outRoad.id_, road.id_);
                    marked[tmp_to] = true;
                    // 推到queue中
                    queue.add(outRoad);
                }
            }
        }

        // 反向查找出从source出发可以到达的的点的路径。
        //FindPath(edgeTo, source);
        FindMostPath(edgeTo,graph);
    }

    // 一次广度优先搜索能够找到的一个点到其他点的路径，然后记录
    private void FindPath(HashMap<Integer, Integer> edgeTo, int source){
        HashMap<Integer, ArrayList<Integer>> path = new HashMap<>();
        for (Integer roadId: edgeTo.keySet()) {
            // 用一个数组保存路径的顺序，第一个RoadId的to作为终点。
            ArrayList<Integer> roadIds = new ArrayList<>();
            int to = allRoad_.roadsMap_.get(roadId).to_;
            int tempRoadId = roadId;
            while(tempRoadId != -1){
                roadIds.add(tempRoadId);
                tempRoadId = edgeTo.get(tempRoadId);
            }
            // 获得了整个路径的Roadid，反转后添加到path当中
            Collections.reverse(roadIds);
            path.put(to, roadIds);
        }
        // 最后添加到类的成员当中
        path_.put(source, path);
    }


    // 一次广度搜索产生的路径绝对不会死锁。所以应当记录所有的路径。后续如果找到已经存在路径的起点和终点，则不再更新。
    private void FindMostPath(HashMap<Integer, Integer> edgeTo,Graph graph){
        // 初始化一个新的path
        HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> path = new HashMap<>();
        for(Integer roadId: edgeTo.keySet()){
            // 用一个数组保存路径的顺序，第一个RoadId的to作为终点。
            ArrayList<Integer> roadIds = new ArrayList<>();
            //int to = allRoad_.roadsMap_.get(roadId).to_;
            int tempRoadId = roadId;
            // 一直找到一条最长的路径为止。
            while(tempRoadId != -1){
                roadIds.add(tempRoadId);
                tempRoadId = edgeTo.get(tempRoadId);
            }
            // 获得了整个路径的Roadid，反转后添加到path当中
            Collections.reverse(roadIds);
            // 开始遍历路径的组合
            for(int i = 0; i < roadIds.size(); ++i){
                for(int j = roadIds.size(); j >i; --j){
                    // 取出一条路径
                    ArrayList<Integer> onePath = new ArrayList<>(roadIds.subList(i,j));
                    int from = allRoad_.roadsMap_.get(onePath.get(0)).from_;
                    int to = allRoad_.roadsMap_.get(onePath.get(onePath.size()-1)).to_;
                    // 如果这条路的起点终点还没有路径，则添加
                    if(path.containsKey(from)){
                        if(path.get(from).containsKey(to)){
                            continue;
                        }
                        else{
                            path.get(from).put(to, onePath);
                        }
                    }
                    else{
                        // 新建一个hashmap
                        HashMap<Integer, ArrayList<Integer>> newFromPath = new HashMap<>();
                        // 新的路径需要加入
                        newFromPath.put(to, onePath);
                        path.put(from, newFromPath);
                    }

                    // 找到一条路之后也要加入到path_中，path_中放的是没那么容易死锁的路
                    if(path_.containsKey(from)){
                        if(path_.get(from).containsKey(to)){
                            continue;
                        }
                        else{
                            path_.get(from).put(to, onePath);
                        }
                    }
                    else{
                        HashMap<Integer, ArrayList<Integer>> newFromPath = new HashMap<>();
                        newFromPath.put(to, onePath);
                        path_.put(from, newFromPath);
                    }
                }
            }
        }
        bfsPath_.add(path);
    }
}

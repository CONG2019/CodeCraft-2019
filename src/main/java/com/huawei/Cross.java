package com.huawei;

import java.util.ArrayList;

public class Cross {
    // 类型直接定义为public
    public Cross(String[] crossStr, AllRoad allRoad){
        id_ = Integer.parseInt(crossStr[0]);
        roadId1_ = Integer.parseInt(crossStr[1]);
        roadId2_ = Integer.parseInt(crossStr[2]);
        roadId3_ = Integer.parseInt(crossStr[3]);
        roadId4_ = Integer.parseInt(crossStr[4]);

        RoadArrays_ = new ArrayList<>();
        if(allRoad.roadsMap_.get(roadId1_) != null){
            //如果是双向路则添加指向路口的
            if(allRoad.roadsMap_.get(roadId1_).isDuplex_ == 1){
                if(allRoad.roadsMap_.get(roadId1_).to_ == id_){
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId1_));
                }else {
                    RoadArrays_.add(allRoad.roadsMap_.get(-roadId1_));
                }
            }else {
                if (allRoad.roadsMap_.get(roadId1_).to_ == id_){
                    //指向路口的才添加
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId1_));
                }
            }
        }
        if(allRoad.roadsMap_.get(roadId2_) != null){
            //如果是双向路则添加指向路口的
            if(allRoad.roadsMap_.get(roadId2_).isDuplex_ == 1){
                if(allRoad.roadsMap_.get(roadId2_).to_ == id_){
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId2_));
                }else {
                    RoadArrays_.add(allRoad.roadsMap_.get(-roadId2_));
                }
            }else {
                if (allRoad.roadsMap_.get(roadId2_).to_ == id_){
                    //指向路口的才添加
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId2_));
                }
            }
        }
        if(allRoad.roadsMap_.get(roadId3_) != null){
            //如果是双向路则添加指向路口的
            if(allRoad.roadsMap_.get(roadId3_).isDuplex_ == 1){
                if(allRoad.roadsMap_.get(roadId3_).to_ == id_){
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId3_));
                }else {
                    RoadArrays_.add(allRoad.roadsMap_.get(-roadId3_));
                }
            }else {
                if (allRoad.roadsMap_.get(roadId3_).to_ == id_){
                    //指向路口的才添加
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId3_));
                }
            }
        }
        if(allRoad.roadsMap_.get(roadId4_) != null){
            //如果是双向路则添加指向路口的
            if(allRoad.roadsMap_.get(roadId4_).isDuplex_ == 1){
                if(allRoad.roadsMap_.get(roadId4_).to_ == id_){
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId4_));
                }else {
                    RoadArrays_.add(allRoad.roadsMap_.get(-roadId4_));
                }
            }else {
                if (allRoad.roadsMap_.get(roadId4_).to_ == id_){
                    //指向路口的才添加
                    RoadArrays_.add(allRoad.roadsMap_.get(roadId4_));
                }
            }
        }

//        PresetCarQueue_ = new ArrayList<>();
//        OrdinaryQueue_ = new ArrayList<>();
    }
    public int id_;
    public final int roadId1_;
    public final int roadId2_;
    public final int roadId3_;
    public final int roadId4_;
    //添加一个保存路口所连接道路的数组，这里的路有可能是双向的，如果是双向的需要把-ID的也添加进来
    public ArrayList<Road> RoadArrays_;
//    //添加两个队列，一个是优先车辆的上路队列，一个是普通车辆的上路队列
//    public ArrayList<Car> PresetCarQueue_;
//    public ArrayList<Car> OrdinaryQueue_;

    //车是在路口处进行转弯的，可以将判断转向的函数放在Cross对象中，roadID是不分正负的
    /*
    @int from_: 车所在的RoadID
    @int to_: 车将要进入的RoadID
    @return: 1表示左转，2表示直行，0表示右转，-1表示获取转向信息出错
    返回值越大，优先权越高
     */
    public int getDirection(int from_, int to_){
        if(from_ == roadId1_)
        {
            if(to_ == roadId2_){
                return 1;
            }else if(to_ == roadId3_){
                return 2;
            }else if(to_ == roadId4_){
                return 0;
            }else {
                System.out.println("获取转向失败: cross: " + id_ + " from_: " + from_ + " to_: " + to_);
                return -1;
            }
        }
        else if(from_ == roadId2_)
        {
            if(to_ == roadId3_){
                return 1;
            }else if(to_ == roadId4_){
                return 2;
            }else if(to_ == roadId1_){
                return 0;
            }else {
                System.out.println("获取转向失败: cross: " + id_ + " from_: " + from_ + " to_: " + to_);
                return -1;
            }
        }
        else if(from_ == roadId3_){
            if(to_ == roadId4_){
                return 1;
            }else if(to_ == roadId1_){
                return 2;
            }else if(to_ == roadId2_){
                return 0;
            }else {
                System.out.println("获取转向失败: cross: " + id_ + " from_: " + from_ + " to_: " + to_);
                return -1;
            }
        }
        else if(from_ == roadId4_){
            if(to_ == roadId1_){
                return 1;
            }else if(to_ == roadId2_){
                return 2;
            }else if(to_ == roadId3_){
                return 0;
            }else {
                System.out.println("获取转向失败: cross: " + id_ + " from_: " + from_ + " to_: " + to_);
                return -1;
            }
        }
        else {
            System.out.println("获取转向失败: cross: " + id_ + " from_: " + from_ + " to_: " + to_);
            return -1;
        }
    }
    //return: 对面路ID，没有则返回-1, 不能传入负ID的路
    public int getOppositeRoad(int from_){
        if(from_ == roadId1_){
            return roadId3_;
        }else if(from_ == roadId2_){
            return roadId4_;
        }else if (from_ == roadId3_){
            return roadId1_;
        }else if (from_ == roadId4_){
            return roadId2_;
        }else {
            System.out.println("传入的参数有误");
            return -1;
        }
    }
}

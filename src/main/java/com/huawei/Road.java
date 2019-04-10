package com.huawei;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
public class Road implements Comparable<Road>{
    // 类型直接定义为public
    public Road(String[] roadStr){
        id_ = Integer.parseInt(roadStr[0]);
        length_ = Integer.parseInt(roadStr[1]);
        speed_ = Integer.parseInt(roadStr[2]);
        channel_ = Integer.parseInt(roadStr[3]);
        from_ = Integer.parseInt(roadStr[4]);
        to_ = Integer.parseInt(roadStr[5]);
        isDuplex_ = Integer.parseInt(roadStr[6]);
        roadLength_ = length_/(speed_+channel_) + 1;
        //roadLength_ = length_/channel_+1;
        // 最大容纳的车辆数是长度乘上车道
        maxCars_ = length_*channel_;
        CarsPosition_ = new Car[channel_][length_];
        PresetCarQueue_ = new ArrayList<>();
        OrdinaryQueue_ = new ArrayList<>();
        Sequeue = new LinkedList<>();
    }
    public int id_;
    public int length_;
    public int speed_;
    public int channel_;
    public int from_;
    public int to_;
    public int isDuplex_;
    // 路的长度除以速度表示路径的长度更合理。
    public int roadLength_;
    // 每条路能够容纳的车辆数目。用于做一个简单的负载均匀算法，如果路上已经有大量的车，则选择往后推迟发车时间。
    public int maxCars_;
    // 标记是否访问过
    public boolean visited = false;
    // 记录路上的车
    public int cars_ = 0;
    //记录路上车的位置
    public Car[][] CarsPosition_;
    //添加两个队列，一个是优先车辆的上路队列，一个是普通车辆的上路队列
    //使用基于动态数据的集合好像不太合理，要删除上路的车有点费时
    public ArrayList<Car> PresetCarQueue_;
    public ArrayList<Car> OrdinaryQueue_;
    //添加一个出路优先队列
    public LinkedList<Car> Sequeue;
    // 重写比较算法
    @Override
    public int compareTo(Road road){
        return roadLength_ - road.roadLength_;
    }

    public int getLength_(){
        // 路上的车越多权重越大
        return (int)((float)(cars_ / length_ * channel_ * 3 + 1) * length_);
    }

    //这里创建优先队列把下一时刻不能出路口的车都添加都队列里了，这里可以通过后面的单独跑一条车道的车进行处理
    public void createSequeue(){
        //这里先用两个队列保存普通车的顺序和所在的车道
        LinkedList<Car> OrdinaryCarsOrder = new LinkedList<>();
        LinkedList<Integer> CarsChanel = new LinkedList<>();
        LinkedList<Integer>  CarsRow = new LinkedList<>();
        //一行一行地遍历
        for(int i = 0; i < length_; i++){
            for(int j = 0; j < channel_; j++){
                //车道号从小到大开始遍历
                //如果遇到的车是等待状态
                if(CarsPosition_[j][i] != null && CarsPosition_[j][i].state_ == 1){
                    //是优先车辆
                    if(CarsPosition_[j][i].priority_ == 1){
                        //判断前面是否有普通车辆阻挡
                        if(CarsChanel.contains(j)){
                            //如果有则不进入优先队列
                            continue;
                        }else{
                            //如果没有普通车阻挡就进入队列
                            if(Sequeue.contains(CarsPosition_[j][i])){
                                //如果已经在队列，则忽略本次操作
                                continue;
                            }else {
                                //如果不在队列，则添加到队列
                                Sequeue.add(CarsPosition_[j][i]);
                            }
                        }
                    }else {
                        //如果是普通车辆，先进入普通车辆队列，LinkedList可以添加相同的元素
                        if(OrdinaryCarsOrder.contains(CarsPosition_[j][i])){
                            continue;
                        }else {
                            //不在队列则添加
                            OrdinaryCarsOrder.add(CarsPosition_[j][i]);
                            CarsChanel.add(j);
                            CarsRow.add(i);
                        }
                    }
                }
            }
//            //遍历完一行，判断是不是每条车道都有普通车辆阻挡
//            int tmp;
//            for(tmp = 0; tmp < channel_; tmp++){
//                if (!CarsChanel.contains(tmp)) {
//                    //如果有一条车道没有普通车则退出
//                    break;
//                }
//            }
//            //如果都被普通车辆阻挡，则令第一台普通车进入队列
//            if(tmp == channel_){
//                Sequeue.add(OrdinaryCarsOrder.poll());
//                CarsChanel.poll();
//                //从该车所在行的下一行开始扫描
////                if (CarsRow.peek() != null){
////                    i = CarsRow.poll();
////                }else {
////                    System.out.println("error");
////                    System.exit(1);
////                }
//                i = CarsRow.poll();
////                i = Sequeue.getLast().row_;
//            }
        }
        //将剩余的普通车都添加到队列，普通车发车后要检查其后面有没有可发车的优先车
//        if(OrdinaryCarsOrder.peek() != null && OrdinaryCarsOrder.peek().id_ == 40324){
//            OrdinaryCarsOrder.peek();
//        }
        while (OrdinaryCarsOrder.peek() != null){
            Sequeue.add(OrdinaryCarsOrder.poll());
            int chanel = CarsChanel.poll();
            for(int i = CarsRow.poll() + 1; i < length_; i++ ){
                if(CarsPosition_[chanel][i] != null){
                    //遇到终止车或普通车则退出，这两种情况后面的优先车都发不出去
                    if (CarsPosition_[chanel][i].state_ == 0 || CarsPosition_[chanel][i].priority_ == 0){
                        break;
                    }else if(CarsPosition_[chanel][i].priority_ == 1){
                        //来到这里说明前面是没有车阻挡的，所以是优先车就可以发车
                        Sequeue.add(CarsPosition_[chanel][i]);
                    }
                }
            }
        }
    }
}

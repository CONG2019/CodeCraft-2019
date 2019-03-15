package huawei;

public class Cross {
    // 类型直接定义为public
    public Cross(String[] crossStr){
        id_ = Integer.parseInt(crossStr[0]);
        roadId1_ = Integer.parseInt(crossStr[1]);
        roadId2_ = Integer.parseInt(crossStr[2]);
        roadId3_ = Integer.parseInt(crossStr[3]);
        roadId4_ = Integer.parseInt(crossStr[4]);
    }
    public int id_;
    public int roadId1_;
    public int roadId2_;
    public int roadId3_;
    public int roadId4_;
}

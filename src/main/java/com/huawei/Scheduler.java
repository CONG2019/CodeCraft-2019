package com.huawei;
import java.lang.reflect.Array;
import java.util.*;

// 调度器类，负责进行车辆的调度
public class Scheduler {
    // 结果要求的格式：　(车辆id，实际出发时间，行驶路线序列)格式的向量。例如(1001, 1, 501, 502, 503, 516, 506, 505, 518, 508, 509, 524)
    // 所以可以用一个二维的ArrayList进行保存
    public ArrayList<ArrayList<Integer>> answer;

    //
    private int NUMBER;
    private int PRENUMBER;
    // 一个用于记录禁止进入路径的set
    private HashSet<Integer> forbidRoads_;
    public HashSet<Integer> saveForbidRoads_;
    //增加一个统计某时刻总发车数量的HashMap
    HashMap<Integer, Integer> CarNumber;
    public Scheduler(AllCross allCross, PresetAnswer presetAnswer, boolean isPriority){
        answer = new ArrayList<>();
            if(isPriority){
                PRENUMBER = 65;
                NUMBER = 120;
            }
        //初始化预置车每时刻的发车数
        CarNumber = new HashMap<>();
        for (ArrayList<Integer> presetCar: presetAnswer.presetAnswer_
        ) {
            if(CarNumber.get(presetCar.get(1)) != null){
                CarNumber.replace(presetCar.get(1), CarNumber.get(presetCar.get(1))+1);
            }else {
                CarNumber.put(presetCar.get(1), 1);
            }
        }
    }


    public int Schedule(ArrayList<Car> allCar, MinPath bfsSolution, Graph graph,int startTime){
        //ArrayList<ArrayList<Car>> carsArray = SplitCars(allCar);
        ArrayList<ArrayList<Car>> carsArray = SplitCarsOnCross(allCar);
        //保存还没有成功安排路径的车
        ArrayList<Car> queue = new ArrayList<>();
        // 足够的startTime让车辆先走。
        // int startTime = 900;
        int number = 0;


        //answer = new ArrayList<>();
        //创建一个car集合，用于保存重新分配路径成功的车辆
        ArrayList<Car> Cars_ = new ArrayList<>();
        //每辆车重新根据路况计算一次最短路径
        for (ArrayList<Car> carlist: carsArray
        ) {
            for (Car car: carlist
            ) {
                //不再给预置车寻找路径
                if(car.preset_ == 1){
                    continue;
                }
                //每次发下一辆车试，优先发上一次没有发成功的车
                for (Car car_: queue
                ) {
                    ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car_, startTime, forbidRoads_);
                    //如果找到路径，则优先为这台车按排路径
                    if(tmp != null){
                        ArrayList<Integer> carSchedule_ = new ArrayList<>();
                        carSchedule_.add(car_.id_);
                        carSchedule_.add(Math.max(startTime, car_.planTime_));
                        carSchedule_.addAll(tmp);
                        answer.add(carSchedule_);
                        Cars_.add(car_);
                        number++;
                    }
                }
                //清除掉已经成功安排路径的车
                queue.removeAll(Cars_);
                Cars_.clear();

                // 先保存车辆id和实际安排的出发时间
                ArrayList<Integer> carSchedule = new ArrayList<>();
                carSchedule.add(car.id_);
                carSchedule.add(Math.max(startTime, car.planTime_));
                // 根据每台车的起始点和终点查找路径
                ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car, startTime, forbidRoads_);
                if(tmp != null){
                    carSchedule.addAll(tmp);
                    answer.add(carSchedule);
                    number++;
                }else {
                    queue.add(car);
                }
                //if((startTime >=350 && number > NUMBER) || (startTime < 750 && number > PRENUMBER)){
                if(number > NUMBER){
                    startTime += 2;
//                    if(startTime < 750 && PRENUMBER > 10){
//                        PRENUMBER--;
//                    }
//                    if(startTime > 950){
//                        NUMBER = 130;
//                    }
                    number = 0;
                    //在大量发车前后空出一定时间不发车
//                    if(CarNumber.get(startTime + 2) != null && CarNumber.get(startTime + 2) > 2 * NUMBER){
//                        number = 0;
//                        startTime = startTime + CarNumber.get(startTime+2) / NUMBER + 8;
//                    }else{
//                        number = 0;
//                    }
                }
            }
        }
        while (!queue.isEmpty()){
            startTime++;
            for (Car car_: queue
            ) {
                ArrayList<Integer> tmp = bfsSolution.SuitablePath(graph, car_, startTime,forbidRoads_);
                if(tmp != null){
                    ArrayList<Integer> carSchedule_ = new ArrayList<>();
                    carSchedule_.add(car_.id_);
                    carSchedule_.add(Math.max(startTime, car_.planTime_));
                    carSchedule_.addAll(tmp);
                    answer.add(carSchedule_);
                    Cars_.add(car_);
                }
            }
            queue.removeAll(Cars_);
            Cars_.clear();
        }
        return startTime;
    }

    // 按照速度分割车
    public ArrayList<ArrayList<Car>> SplitCars(ArrayList<Car> allCar){
        ArrayList<ArrayList<Car>> carsArray = new ArrayList<>();
        //将车按降序排序int carId = sameSpeedCars.get(j).id_;
        Collections.sort(allCar, (car1, car2)->
                car2.speed_ - car1.speed_);

        int speed = allCar.get(0).speed_;
        ArrayList<Car> carArrayList = new ArrayList<>();
        for (Car car: allCar
        ) {
            if(car.speed_ == speed){
                carArrayList.add(car);
            }else {
                carsArray.add(carArrayList);
                carArrayList = new ArrayList<>();
                speed = car.speed_;
                carArrayList.add(car);
            }
        }
        carsArray.add(carArrayList);
        //不同速度的车再按时间顺序排序
        for (ArrayList<Car> carArrayList1: carsArray
        ) {
            Collections.sort(carArrayList1, (car1, car2)->
                    car1.planTime_ - car2.planTime_);
        }

        // 按照出发点和速度分割车，尽可能使得每个点的发车均衡。
        // 还是按照速度分割，但是速度相同的车按照不同的出发点进行排列。然后轮询不同的路口进行顺序发车
        ArrayList<ArrayList<Car>> newCarsArray = new ArrayList<>();
        for(int i = 0; i < carsArray.size(); ++i){
            // 对于每一种相同速度的车，按照出发路口进行发车。
            HashMap<Integer, ArrayList<Car>> carsFrom = new HashMap<>();
            ArrayList<Car> sameSpeedCars = carsArray.get(i);
            for(int j = 0; j < sameSpeedCars.size(); ++j){
                Car car = sameSpeedCars.get(j);
                if(carsFrom.containsKey(car.from_)){
                    carsFrom.get(car.from_).add(sameSpeedCars.get(j));
                }
                else{
                    ArrayList<Car> carFrom = new ArrayList<>();
                    carFrom.add(sameSpeedCars.get(j));
                    carsFrom.put(car.from_, carFrom);
                }
            }
            // 整理一份新的发车顺序出来。
            ArrayList<Car> newCarsOrder = new ArrayList<>();
            while(!carsFrom.isEmpty()){
                // 遍历一个keyset
                ArrayList<Integer> removeIndex = new ArrayList<>();
                for(Integer key: carsFrom.keySet()){
                    newCarsOrder.add(carsFrom.get(key).get(0));
                    carsFrom.get(key).remove(0);
                    // 一次发两台车试试
                    if(carsFrom.get(key).isEmpty()){
                        removeIndex.add(key);
                    }
                    else{
                        newCarsOrder.add(carsFrom.get(key).get(0));
                        carsFrom.get(key).remove(0);
                        if(carsFrom.get(key).isEmpty()){
                            removeIndex.add(key);
                        }
                    }
                }
                for(int index: removeIndex){
                    carsFrom.remove(index);
                }
            }
            newCarsArray.add(newCarsOrder);
        }
        return newCarsArray;
    }


    // 新的发车策略，分组进行发车，根据from和to进行分组
    public ArrayList<ArrayList<Car>> SplitCarsOnCross(ArrayList<Car> allCar) {
        ArrayList<ArrayList<Car>> carsArray = new ArrayList<>();
        //将车按降序排序int carId = sameSpeedCars.get(j).id_;
        Collections.sort(allCar, (car1, car2) ->
                car2.speed_ - car1.speed_);

        int speed = allCar.get(0).speed_;
        ArrayList<Car> carArrayList = new ArrayList<>();
        for (Car car : allCar
        ) {
            if (car.speed_ == speed) {
                carArrayList.add(car);
            } else {
                carsArray.add(carArrayList);
                carArrayList = new ArrayList<>();
                speed = car.speed_;
                carArrayList.add(car);
            }
        }
        carsArray.add(carArrayList);
        // 对于速度相同的每组车统计from到to 的车，然后分组
        ArrayList<ArrayList<Car>> newCarsArray = new ArrayList<>();
        // 记录车辆总数。
        int carNumbers = 0;
        for(ArrayList<Car> cars: carsArray){
            HashMap<Integer, HashMap<Integer, LinkedList<Car>>> fromToCars = new HashMap<>();
            for(Car car: cars){
                ++carNumbers;
                if(fromToCars.containsKey(car.from_)){
                    if(fromToCars.get(car.from_).containsKey(car.to_)){
                        fromToCars.get(car.from_).get(car.to_).add(car);
                    }
                    else{
                        fromToCars.get(car.from_).put(car.to_, new LinkedList<>());
                        fromToCars.get(car.from_).get(car.to_).add(car);
                    }
                }
                else{
                    fromToCars.put(car.from_, new HashMap<>());
                    fromToCars.get(car.from_).put(car.to_, new LinkedList<>());
                    fromToCars.get(car.from_).get(car.to_).add(car);
                }
            }

            // 开始进行分组
            ArrayList<Car> oneGroup = new ArrayList<>();
            int LEASTNUMBER = 40;
            int leastNumbers = 0;
            while(carNumbers > 0){
                // 记录要删除的index
                ArrayList<Integer> fromDelete = new ArrayList<>();
                for(Integer from: fromToCars.keySet()){
                    HashMap<Integer, LinkedList<Car>> fromCars = fromToCars.get(from);
                    ArrayList<Integer> toDelete = new ArrayList<>();
                    for(Integer to: fromCars.keySet()){
                        oneGroup.add(fromCars.get(to).pollLast());
                        ++leastNumbers;
                        --carNumbers;
                        if(fromCars.get(to).isEmpty()){
                            toDelete.add(to);
                        }
                    }
                    if(!toDelete.isEmpty()){
                        for(int i = 0; i < toDelete.size(); ++i){
                            fromCars.remove(toDelete.get(i));
                        }
                    }
                    if(fromCars.isEmpty()){
                        fromDelete.add(from);
                    }
                }
                // 删除空的hashmap
                if(!fromDelete.isEmpty()){
                    for(int i = 0; i < fromDelete.size(); ++i){
                        fromToCars.remove(fromDelete.get(i));
                    }
                }
                // 判断单组是否有足够的车
                if(leastNumbers >= LEASTNUMBER){
                    leastNumbers = 0;
                    newCarsArray.add(new ArrayList<Car>(oneGroup));
                    oneGroup.clear();
                }
                else{
                    if(fromToCars.isEmpty()){
                        newCarsArray.add(new ArrayList<Car>(oneGroup));
                        oneGroup.clear();
                    }
                }
            }
        }
        return newCarsArray;
    }

    // 先处理预置车中插入车辆的调度函数
    public Integer InsertPresetCars(PresetAnswer preAnswer, Graph graph, MinPath minPath, AllRoad allRoad, AllCar allCar){
        // 统计预置车每条路经过的次数, 忘记处理双向路了。
        HashMap<Integer, Integer> counts = new HashMap<>();
        // 记录出发到结束点的数量。
        HashMap<Integer, HashMap<Integer, Integer>> fromToCars = new HashMap<>();
        for(ArrayList<Integer> path: preAnswer.presetAnswer_){
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

        // 需要把hashmap根据值排序
        // 将EntrySet转化为list
        List<Map.Entry<Integer, Integer>> roadCountsList = new ArrayList<>(counts.entrySet());
        for(int i = 0; i < roadCountsList.size(); ++i){
            Road road = allRoad.roadsMap_.get(roadCountsList.get(i).getKey());
            int value = roadCountsList.get(i).getValue();
            roadCountsList.get(i).setValue(1000*value/road.length_/road.channel_);
        }
        // 转化一下数量的相对值。
        // 排序
        Collections.sort(roadCountsList, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> integerIntegerEntry, Map.Entry<Integer, Integer> t1) {
                return  t1.getValue() - integerIntegerEntry.getValue();
            }
        });
        // 一个禁止路的比例
        int proportion = 5;
        HashSet<Integer> forbiRoadIds = new HashSet<>();
        for(int i = 0; i < roadCountsList.size() / 5; ++i){
            forbiRoadIds.add(roadCountsList.get(i).getKey());
            forbiRoadIds.add(-roadCountsList.get(i).getKey());
        }
        saveForbidRoads_ = new HashSet<>(forbiRoadIds);
        // 开始进行预置车中间插入车的操作
        ArrayList<Car> cars = allCar.waitCars_;
        //将车按降序排序 忽略
        Collections.sort(cars);
        // 一个数组保存还没有安排路径的车，　顺便对车分一下组
        ArrayList<Car> noPathPriCars = new ArrayList<>();
        ArrayList<Car> noPathCommonCars = new ArrayList<>();
        ArrayList<Car> priCars = new ArrayList<>();
        int preSetEarlyTime = Integer.MAX_VALUE;
        // 分成两组车，只把优先慢车插到前面去。
        for(int i = 0; i < cars.size(); ++i){
            Car car = cars.get(i);
            if(car.preset_ == 1){
                if(car.planTime_ < preSetEarlyTime){
                    preSetEarlyTime = car.planTime_;
                }
                continue;
            }
            if(car.priority_ == 1){
                priCars.add(car);
            }
            else{
                noPathCommonCars.add(car);
            }
        }
        Collections.reverse(priCars);
        int startTime = 1;
        // 十个时间片作为缓冲
        int endTime = preAnswer.endTime ;
        // 每秒的发车量, 可以加大
        int carsPerSecond = 22;
        int number = 0;
        HashSet<Integer> timeSet = preAnswer.timeSet_;
        int index = 0;
        for(int i = 0; i < priCars.size(); ++i){
            Car car = priCars.get(i);
            // 根据每台车的起始点和终点查找路径
            ArrayList<Integer> path = minPath.SuitablePath(graph, car, startTime, forbiRoadIds);
            if(path != null){
                ArrayList<Integer> carSchedule = new ArrayList<>();
                carSchedule.add(car.id_);
                carSchedule.add(Math.max(startTime, car.planTime_));
                carSchedule.addAll(path);
                answer.add(carSchedule);
                number++;
            }
            // 找不到则加入到后面
            else {
                    noPathPriCars.add(car);
            }
            // 更改starTime和提前退出
            index = i;
            if(number > carsPerSecond){
                ++startTime;
                number = 0;
            }
            if(startTime > endTime){
                break;
            }
        }
        for(int i = index+1; i < priCars.size(); ++i){
            noPathPriCars.add(priCars.get(i));
        }
        saveForbidRoads_ = forbiRoadIds;
        // 接下来调用schdule方法处理剩下的优先车和普通车。
        forbidRoads_ = new HashSet<>();
        startTime = Math.max(endTime, startTime);
        if(!noPathPriCars.isEmpty()){
            startTime = Schedule(noPathPriCars, minPath, graph, startTime);
        }
        Schedule(noPathCommonCars, minPath, graph, startTime);
;        return startTime;
    }
 }


package com.huawei;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AllCar {
    public ArrayList<Car> cars_;
    // hashmap存储车
    public HashMap<Integer, Car> carsMap_;

    // 存储不同路口出发的车的id
    public HashMap<Integer, ArrayList<Integer>> carsFrom_;
    // 存储到达不同路口的车的id
    public HashMap<Integer, ArrayList<Integer>> carsTo_;

    // 存储from到to的车辆的id。和个数
    public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> fromToCarsId;
    // 存储个数
    public HashMap<Integer, HashMap<Integer, Integer>> fromToCarsNumber;
    // 通过传入的文件名读入参数
    public void Init(String carFileName, PresetAnswer presetAnswer){
        cars_ = new ArrayList<>();
        carsMap_ = new HashMap<>();
        carsFrom_ = new HashMap<>();
        carsTo_ = new HashMap<>();
        fromToCarsId = new HashMap<>();
        fromToCarsNumber = new HashMap<>();
        // 打开文件
        try (FileInputStream inputStream = new FileInputStream(carFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 存放每一行数据的 string
            String str = null;
            // 保存最大的出发时间
            int maxPlanTime = 0;
            while((str = bufferedReader.readLine()) != null){
                // 如果是注释则忽略
                if(str.charAt(0) == '#'){
                    continue;
                }
                else{
                    // 使用string的分割函数
                    str = str.substring(1, str.length()-1);
                    str = str.replace(" ", "");
                    String[] car = str.split(",");
                    // 添加新的道路进去
                    Car oneCar = new Car(car);
                    // 剔除掉预置车
                    if(presetAnswer.presetCarIds_.contains(oneCar.id_)){
                        continue;
                    }
                    if(oneCar.planTime_ > maxPlanTime){
                        maxPlanTime = oneCar.planTime_;
                    }
                    cars_.add(oneCar);
                    carsMap_.put(oneCar.id_, oneCar);
                    // 判断是否已经有同一个地方出发的
                    if(carsFrom_.containsKey(oneCar.from_)){
                        carsFrom_.get(oneCar.from_).add(oneCar.id_);
                    }
                    else{
                        carsFrom_.put(oneCar.from_, new ArrayList<>());
                        carsFrom_.get(oneCar.from_).add(oneCar.id_);
                    }
                    if(carsTo_.containsKey(oneCar.to_)){
                        carsTo_.get(oneCar.to_).add(oneCar.id_);
                    }
                    else{
                        carsTo_.put(oneCar.to_, new ArrayList<>());
                        carsTo_.get(oneCar.to_).add(oneCar.id_);
                    }

                    // 添加到统计fromToCarsNumber中去
                    if(fromToCarsNumber.containsKey(oneCar.from_)){
                        if(fromToCarsNumber.get(oneCar.from_).containsKey(oneCar.to_)){
                            fromToCarsNumber.get(oneCar.from_).put(oneCar.to_, fromToCarsNumber.get(oneCar.from_).get(oneCar.to_)+1);
                            fromToCarsId.get(oneCar.from_).get(oneCar.to_).add(oneCar.id_);
                        }
                        else{
                            fromToCarsNumber.get(oneCar.from_).put(oneCar.to_, 1);

                            fromToCarsId.get(oneCar.from_).put(oneCar.to_, new ArrayList<Integer>());
                            fromToCarsId.get(oneCar.from_).get(oneCar.to_).add(oneCar.id_);
                        }
                    }
                    else{
                        // 如果还没有统计from，则加上
                        fromToCarsNumber.put(oneCar.from_, new HashMap<>());
                        fromToCarsNumber.get(oneCar.from_).put(oneCar.to_, 1);

                        // 记录id的hashmap应该是同步的
                        fromToCarsId.put(oneCar.from_, new HashMap<>());
                        fromToCarsId.get(oneCar.from_).put(oneCar.to_, new ArrayList<Integer>());
                        fromToCarsId.get(oneCar.from_).get(oneCar.to_).add(oneCar.id_);
                    }
                }
            }
            bufferedReader.close();
            inputStream.close();
            // 对车辆按照出发时间进行排序
            //Collections.sort(cars_);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

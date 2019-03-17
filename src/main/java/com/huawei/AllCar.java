package huawei;

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
    // 通过传入的文件名读入参数
    public void Init(String carFileName){
        cars_ = new ArrayList<>();
        carsMap_ = new HashMap<>();
        // 打开文件
        try (FileInputStream inputStream = new FileInputStream(carFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 存放每一行数据的 string
            String str = null;
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
                    cars_.add(oneCar);
                    carsMap_.put(oneCar.id_, oneCar);
                }
            }
            // 对车辆按照出发时间进行排序
            //Collections.sort(cars_);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

package huawei;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AllCross {
    public ArrayList<Cross> cross_;
    public HashMap<Integer, Cross> crossMap_;
    // 通过传入的文件名读入参数
    public void Init(String roadFileName) {
        cross_ = new ArrayList<>();
        crossMap_ = new HashMap<>();
        // 打开文件
        try (FileInputStream inputStream = new FileInputStream(roadFileName)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 存放每一行数据的 string
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                // 如果是注释则忽略
                if (str.charAt(0) == '#') {
                    continue;
                } else {
                    // 使用string的分割函数
                    str = str.substring(1, str.length()-1);
                    str = str.replace(" ", "");
                    String[] cross = str.split(",");
                    // 添加新的道路进去
                    Cross oneCross = new Cross(cross);
                    cross_.add(oneCross);
                    crossMap_.put(oneCross.id_, oneCross);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

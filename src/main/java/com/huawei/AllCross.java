package huawei;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AllCross {
    public ArrayList<Cross> cross_;
    // 通过传入的文件名读入参数
    public void Init(String roadFileName) {
        cross_ = new ArrayList<>();
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
                    cross_.add(new Cross(cross));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

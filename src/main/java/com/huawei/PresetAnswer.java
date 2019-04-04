package com.huawei;

import edu.princeton.cs.algs4.In;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// 保存提前预规划好的路线
public class PresetAnswer {
    // 结果要求的格式：　(车辆id，实际出发时间，行驶路线序列)格式的向量。例如(1001, 1, 501, 502, 503, 516, 506, 505, 518, 508, 509, 524)
    // 所以可以用一个二维的ArrayList进行保存
    public ArrayList<ArrayList<Integer>> presetAnswer_;

    // 保存一个预置车的集合
    public Set<Integer> presetCarIds_;

    // 初始化函数
    public void Init(String presetAnswerFileName){
        presetAnswer_ = new ArrayList<>();
        presetCarIds_ = new HashSet<>();
        // 打开文件，每一行都分割成一个数组存放。
        try(FileInputStream inputStream = new FileInputStream(presetAnswerFileName)){
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            // 存放一行的string
            String str = null;
            while((str = bufferedReader.readLine()) != null){
                if(str.charAt(0) == '#'){
                    continue;
                }
                else{
                    // 使用string的分割函数
                    str = str.substring(1, str.length()-1);
                    str = str.replace(" ", "");
                    String[] cross = str.split(",");
                    // 转化成整型数组
                    ArrayList<Integer> answer = new ArrayList<Integer>();
                    for(int i = 0; i < cross.length; ++i){
                        answer.add(Integer.parseInt(cross[i]));
                    }
                    // 添加到集合
                    presetCarIds_.add(answer.get(0));
                }
            }
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

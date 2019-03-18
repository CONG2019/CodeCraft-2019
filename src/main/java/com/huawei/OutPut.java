package com.huawei;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// 根据输出文件的路径和调度答案输出文件
public class OutPut {
    public static void WriteAnswer(Scheduler scheduler, String fileName){
        // 打开文件
        try {
            FileWriter writer = new FileWriter(fileName);
            BufferedWriter bufWriter = new BufferedWriter(writer);
            // 循环写入答案
            for (ArrayList<Integer> answer: scheduler.answer
                 ) {
                // 用一个String保存内容
                String s = new String("(");
                for (Integer number: answer
                     ) {
                    s = s + Math.abs(number) + ",";
                }
                s = s.substring(0, s.length()-1);
                s = s + ")" + "\n";
                bufWriter.write(s);
            }
            // 关闭输出文件和缓冲流
            bufWriter.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

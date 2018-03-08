package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class transtxtToList {
	public static void main(String[] args) throws IOException {
		// System.out.println(TransFormJsToMap());
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

			/* 读入TXT文件 */
			String pathname = "C:\\Users\\Administrator\\Desktop\\bop.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
			File filename = new File(pathname); // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					String[] list = line.split("=");
					System.out
							.println(StringUtils.replace(list[1].split(";")[0], "\"", "").trim());

				}
				line = br.readLine(); // 一次读入一行数据

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

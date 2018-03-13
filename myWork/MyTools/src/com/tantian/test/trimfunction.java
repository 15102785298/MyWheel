package com.tantian.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.FileUtils;

public class trimfunction {

	public static void main(String[] args) {
		List<File> functionList = FileUtils.getAllFile("D:\\function.txt", "");
		List<File> uffunctionList = FileUtils.getAllFile("D:\\UFFunction.java", "");
		Map<String, String> res = new HashMap<>();

		try {
			getFunctionValue(uffunctionList.get(0), res);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(functionList.get(0)), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			String function = "";
			function = br.readLine();
			while (function != null) {
				if(res.get(function) == null){
					System.out.println(function);
					function = br.readLine();
					continue;
				}
				System.out.println(function + "-" + res.get(function));
				function = br.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getFunctionValue(File readFile, Map<String, String> function2zj) {
		List<String> res = new LinkedList<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"UTF-8"); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					System.out.println(line);
					String[] list = line.split("=");
					res.add(StringUtils.replace(list[1].split(";")[0], "\"", "").trim());
					if(list[1].split(";").length > 1){
						function2zj.put(StringUtils.replace(list[1].split(";")[0], "\"", "").trim(), StringUtils.replace(list[1].split(";")[1], "/", "").trim());

					}
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}

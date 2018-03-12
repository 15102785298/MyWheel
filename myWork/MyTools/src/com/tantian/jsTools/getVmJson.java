package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class getVmJson {

	static List<File> allJavaList = new LinkedList<>();

	public static void find(String pathName, int depth, String stuffix) throws IOException {
		// 获取pathName的File对象
		File dirFile = new File(pathName);
		// 判断该文件或目录是否存在，不存在时在控制台输出提醒
		if (!dirFile.exists()) {
			System.out.println("do not exit");
			return;
		}
		// 判断如果不是一个目录，就判断是不是一个文件，时文件则输出文件路径
		if (!dirFile.isDirectory()) {
			if (dirFile.isFile()) {
				allJavaList.add(dirFile);
				// System.out.println(dirFile.getCanonicalFile());
			}
			return;
		}
		// 获取此目录下的所有文件名与目录名
		String[] fileList = dirFile.list();
		int currentDepth = depth + 1;
		for (int i = 0; i < fileList.length; i++) {
			// 遍历文件目录
			String string = fileList[i];
			// File("documentName","fileName")是File的另一个构造器
			File file = new File(dirFile.getPath(), string);
			String name = file.getName();
			// 如果是一个目录，搜索深度depth++，输出目录名后，进行递归
			if (file.isDirectory()) {
				// 递归
				find(file.getCanonicalPath(), currentDepth, stuffix);
			} else {
				if (StringUtils.endsWith(name, stuffix)) {
					// System.out.println(file.getPath());
					allJavaList.add(file);
				}
			}
		}
	}

	public static Set<String> searchJson(File readFile) {
		Set<String> res = new HashSet<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (StringUtils.isNotBlank(line)
						&& (StringUtils.contains(line, ".json") || StringUtils.contains(line, ".htm"))
						&& !StringUtils.startsWith("//", line) && !StringUtils.startsWith("##", line)) {
					if (line.indexOf("/") < 0) {
						line = br.readLine(); // 一次读入一行数据
						continue;
					}
					String spliteFlag = ".json";
					if (StringUtils.contains(line, ".htm")) {
						spliteFlag = ".htm";
					}
					String[] list = line.split(spliteFlag);
					String finalString = list[0].split("\"")[list[0].split("\"").length - 1] + spliteFlag;
					if (StringUtils.indexOf(finalString, "/'") > -1) {
						finalString = finalString.split("\'")[finalString.split("\'").length - 1];
					}
					if (finalString.trim().startsWith("$contain.get")) {
						line = br.readLine(); // 一次读入一行数据
						continue;
					}
					res.add(finalString);
				}
				line = br.readLine(); // 一次读入一行数据
			}
			res.add(StringUtils.split(readFile.getName(), ".")[0] + ".htm");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static Set<String> searchUrl(File readFile, String url) {
		Set<String> res = new HashSet<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, url)
						&& !StringUtils.startsWith(line, "//")) {
					String finalString = line.split("=")[0].trim()
							.split(" ")[line.split("=")[0].trim().split(" ").length - 1].trim();
					res.add(finalString);
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static void main(String[] args) {
		List<File> allJavaList1 = new LinkedList<>();
		List<File> allJavaList2 = new LinkedList<>();

		try {
			find("D://zt//BOP//Sources//WebCodes//bop2.0", 100, ".vm");
			allJavaList1.addAll(allJavaList);
			find("D://zt//BOP//Sources//WebCodes//bop2.0", 100, ".java");
			allJavaList2.addAll(allJavaList);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (File temp : allJavaList1) {
			System.out.println("---------------------文件名" + temp.getName() + "---------------------");
			for (String url : searchJson(temp)) {
				Set<String> set = new HashSet<>();

				for (File temp2 : allJavaList2) {
					if (StringUtils.startsWith(temp2.getName(), "UrlValues")) {
						set.addAll(searchUrl(temp2, url));
					}

				}
				for (String url2 : set) {
					System.out.println(url);
					System.out.println(url2);
				}

			}
			System.out.println("---------------------文件名" + temp.getName() + "---------------------");
		}
	}
}

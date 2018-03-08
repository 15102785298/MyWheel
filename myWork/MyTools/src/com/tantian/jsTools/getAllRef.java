package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.StringUtils;

public class getAllRef {
	private static int depth = 100;
	private static boolean isShowEmptyFunction = false;
	static List<File> allJavaList = new LinkedList<>();

	public static void find(String pathName, int depth) throws IOException {
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
				find(file.getCanonicalPath(), currentDepth);
			} else {
				if (StringUtils.endsWith(name, ".java")) {
					// System.out.println(file.getPath());
					allJavaList.add(file);
				}
			}
		}
	}

	public static List<String> getFunctionValue(File readFile) {
		List<String> res = new LinkedList<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					String[] list = line.split("=");
					res.add(StringUtils.replace(list[1].split(";")[0], "\"", "").trim());
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<String> getFunctionNameValue(File readFile) {
		List<String> res = new LinkedList<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					String[] list = line.split("String");
					String functionName = list[1].split("=")[0].trim();
					res.add(functionName);
					// System.out.println(functionName);
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean isFindValueInFile(File readFile, String findValue, List<String> fileStuffix) {
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			// 根据后缀筛选
			if (fileStuffix != null) {
				for (String temp : fileStuffix) {
					if (StringUtils.endsWith(filename.getName(), temp)) {
						return false;
					}
				}
			}
			// System.out.println("查找文件中：" + filename.getName() + "目标字段：" +
			// findValue);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			int readTime = 1;
			line = br.readLine();
			while (line != null) {
				// System.out.println("读取" + readTime++ + line);
				if (StringUtils.startsWith(line.trim(), "//")) {
					line = br.readLine(); // 一次读入一行数据
					continue;
				}
				if (StringUtils.indexOf(line, findValue) > -1) {
					// System.out.println(new
					// StringBuffer().append("文件：").append(filename.getName()).append("找到目标字段：").append(findValue).toString());
					return true;
				}
				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<String> findMutiValueInFile(File readFile, List<String> findValueList,
			List<String> fileStuffix) {
		List<String> res = new LinkedList<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			// 根据后缀筛选
			if (fileStuffix != null) {
				for (String temp : fileStuffix) {
					if (StringUtils.equals(filename.getName(), temp)) {
						return new LinkedList<>();
					}
				}
			}
			// System.out.println("查找文件中：" + filename.getName() + "目标字段：" +
			// findValue);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			int readTime = 1;
			String nowMethod = "";
			line = br.readLine();
			while (line != null) {
				// System.out.println("读取" + readTime++ + line);
				if (StringUtils.startsWith(line.trim(), "//")) {
					line = br.readLine(); // 一次读入一行数据
					continue;
				}
				// 更新当前方法名
				// 方法声明不能有"."
				if (StringUtils.indexOf(line, ".") < 0) {

				}
				for (String temp : findValueList) {
					if (StringUtils.indexOf(line, temp) > -1) {
						System.out.println(new StringBuffer().append("文件：").append(filename.getName()).append("找到目标字段：")
								.append(temp).toString());
						res.add(temp);
					}
				}

				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private static void printRes(List<String> functionName, List<String> function,
			Map<String, List<File>> functionName2File, File file) throws FileNotFoundException, IOException {
		FileOutputStream outStream = new FileOutputStream(file); // 文件输出流用于将数据写入文件
		// 有被引用的功能号以及引用它的service
		System.out.println("----------生成文件预览----------");
		Set<String> res = new HashSet<>();
		for (Entry<String, List<File>> aimValue : functionName2File.entrySet()) {
			String functinId = function.get(functionName.indexOf(aimValue.getKey()));
			if (!isShowEmptyFunction) {
				if (aimValue.getValue().size() == 0) {
					continue;
				} else {
					res.add(functinId);
				}
			}
			if (!file.exists()) { // 文件不存在则创建文件，先创建目录
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}
			outStream.write(("-------------------功能号【" + functinId + "】----------------------\r\n").getBytes());
			System.out.println("-------------------功能号【" + functinId + "】----------------------");
			for (File temp : aimValue.getValue()) {
				System.out.println(temp.getName());
				outStream.write((temp.getName() + "\r\n").getBytes());
			}
			System.out.println("-------------------功能号【" + functinId + "】----------------------");
			outStream.write(("-------------------功能号【" + functinId + "】----------------------\r\n").getBytes());
		}
		outStream.write(("-------------------BOP调用的功能号----------------------\r\n").getBytes());
		for (String temp : res) {
			outStream.write((temp + "\r\n").getBytes());

		}
		outStream.write(("-------------------BOP调用的功能号----------------------\r\n").getBytes());
		outStream.close(); // 关闭文件输出流
		System.out.println("文件生成完毕！");
	}

	public static void main(String[] args) throws IOException {
		// 读取目录下所有文件
		find("D://zt//BOP//Sources//WebCodes//bop2.0", depth);
		// 存起来
		List<File> allFile = new LinkedList<>();
		allFile.addAll(allJavaList);
		// 清空
		allJavaList.clear();
		// 读取UrlValus
		find("D:/zt/BOP/Sources/WebCodes/bop2.0/bop-biz-platform/bop-pub/src/main/java/com/hundsun/bop/pub/constant/UFFunction.java",
				depth);
		List<File> uffunction = new LinkedList<>();
		uffunction.addAll(allJavaList);
		// 清空
		allJavaList.clear();
		// 获取functionName列表
		List<String> functionName = getFunctionNameValue(uffunction.get(0));
		// 获取function列表
		List<String> function = getFunctionValue(uffunction.get(0));
		Map<String, List<File>> functionName2File = new HashMap<>();
		Map<File, List<String>> File2functionName = new HashMap<>();
		for (File aimFile : allFile) {
			File2functionName.put(aimFile, findMutiValueInFile(aimFile, functionName, new LinkedList<String>() {
				{
					add("UFFunction.java");
					add("UFFunctionCounter.java");
					add("USERFunction.java");
					add("Functions.java");
					add("FunctionsUf2.java");
					add("UFFunctionExt.java");
					add("UFFunctionBus.java");
					add("Function.java");
					add("BOPFunction.java");
					add("FunctionsAcpt.java");
				}
			}));
			System.out.println("处理完成文件" + aimFile.getName());
		}
		System.out.println("转换中....");
		for (String temp : functionName) {
			List<File> res = new LinkedList<>();
			for (Entry<File, List<String>> aimValue : File2functionName.entrySet()) {
				if (aimValue.getValue().indexOf(temp) > -1) {
					res.add(aimValue.getKey());
				}
			}
			functionName2File.put(temp, res);
		}
		System.out.println("转换完毕，等待输出....");
		File file = new File("D://BOP被引用的功能号.txt"); // 文件路径（路径+文件名）
		// 输出结果
		printRes(functionName, function, functionName2File, file);

		// 查找这些service在那些action中被注入
		Map<String, List<File>> service2Action = new HashMap<>();

		System.out.println("本次处理常量【" + function.size() + "】条");
		System.out.println("本次处理文件【" + allFile.size() + "】个");
	}

}

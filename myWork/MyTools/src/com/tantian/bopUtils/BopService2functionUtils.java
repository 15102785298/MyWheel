package com.tantian.bopUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class BopService2functionUtils {
	private static int depth = 100;
	private static boolean isShowEmptyFunction = false;
	static List<File> allJavaList = new LinkedList<>();

	public void find(String pathName, int depth) throws IOException {
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
		List<String> res = new ArrayList<>();
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
		List<String> res = new ArrayList<>();
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

	public boolean isFindValueInFile(File readFile, String findValue, List<String> fileStuffix) {
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

	public static Map<String, Object> findMutiValueInFile(File readFile, List<String> findValueList,
			List<String> fileStuffix) {
		Map<String, Object> resFinal = new HashMap<>();
		Map<String, List<String>> function_fileName2Method = new HashMap<>();

		List<List<String>> res = new LinkedList<>();
		List<String> res1 = new LinkedList<>();
		List<String> res2 = new LinkedList<>();
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			File filename = readFile; // 要读取以上路径的input。txt文件
			// 根据后缀筛选
			if (fileStuffix != null) {
				for (String temp : fileStuffix) {
					if (StringUtils.equals(filename.getName(), temp)) {
						res.add(res1);
						res.add(res2);
						resFinal.put("list", res);
						resFinal.put("map", new HashMap<>());
						return resFinal;
					}
				}
			}
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			String nowMethod = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				// System.out.println("读取" + readTime++ + line);
				if (StringUtils.startsWith(line, "//")) {
					line = br.readLine(); // 一次读入一行数据
					continue;
				}
				// 更新当前方法名
				// 方法声明不能有"."
				if (StringUtils.indexOf(line, ".") < 0) {
					if (StringUtils.startsWith(line, "public") || StringUtils.startsWith(line, "private")
							|| StringUtils.startsWith(line, "protected")) {
						if (StringUtils.contains(line, "(")) {
							nowMethod = StringUtils.split(line, "(")[0].trim()
									.split(" ")[StringUtils.split(line, "(")[0].trim().split(" ").length - 1];
						}
					}
				}
				for (String temp : findValueList) {
					if (StringUtils.indexOf(line, temp) > -1) {
						System.out.println(new StringBuffer().append("文件：").append(filename.getName()).append("找到目标字段：")
								.append(temp).append("对应方法为：").append(nowMethod == "" ? "未找到方法" : nowMethod)
								.toString());
						res1.add(temp);
						res2.add(nowMethod);
						if (function_fileName2Method.get(temp + "-" + filename.getName()) == null) {
							List<String> tempList = new LinkedList<>();
							tempList.add(nowMethod);
							function_fileName2Method.put(temp + "-" + filename.getName(), tempList);
						} else {
							function_fileName2Method.get(temp + "-" + filename.getName()).add(nowMethod);
						}
					}
				}

				line = br.readLine(); // 一次读入一行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.add(res1);
		res.add(res2);
		resFinal.put("list", res);
		resFinal.put("map", function_fileName2Method);
		return resFinal;
	}

	private static void printRes(List<String> functionName, List<String> function,
			Map<String, List<File>> functionName2File, File file, Map<String, List<String>> functionName2Method,
			Map<String, List<String>> function_fileName2Method, Map<String, Set<String>> res2)
			throws FileNotFoundException, IOException {
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
			StringBuffer sb = new StringBuffer();
			for (File temp : aimValue.getValue()) {
				List<String> function_fileName2MethodList = function_fileName2Method
						.get(aimValue.getKey() + "-" + temp.getName());
				for (String MethodName : function_fileName2MethodList) {
					String tempkey = StringUtils.split(temp.getName(), ".")[0] + "." + MethodName;
					if (res2.get(tempkey) == null) {
						res2.put(tempkey, new HashSet<>());
					}
					res2.get(tempkey).add(functinId);
					sb.setLength(0);
					sb.append("类名【").append(temp.getName()).append("】方法名【").append(MethodName).append("】")
							.append("常量名【").append(aimValue.getKey()).append("】");
					System.out.println(sb.toString());
					outStream.write(sb.append("\r\n").toString().getBytes());
				}

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

	@SuppressWarnings("unchecked")
	public static Map<String, Set<String>> getService2FunctionMap(List<File> allFile, List<File> uffunction) {
		Map<String, Set<String>> res = new HashMap<>();
		uffunction.addAll(allJavaList);
		// 清空
		allJavaList.clear();
		// 获取functionName列表
		List<String> functionName = getFunctionNameValue(uffunction.get(0));
		// 获取function列表
		List<String> function = getFunctionValue(uffunction.get(0));
		Map<String, List<File>> functionName2File = new HashMap<>();
		Map<String, List<String>> functionName2Method = new HashMap<>();
		Map<String, List<String>> function_fileName2Method = new HashMap<>();
		Map<File, List<List<String>>> File2functionName = new HashMap<>();
		for (File aimFile : allFile) {
			Map<String, Object> valueInfileMap = findMutiValueInFile(aimFile, functionName, new LinkedList<String>() {
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
			});
			List<List<String>> temp = (List<List<String>>) valueInfileMap.get("list");
			function_fileName2Method.putAll((Map<String, List<String>>) valueInfileMap.get("map"));
			File2functionName.put(aimFile, temp);
			System.out.println("处理完成文件" + aimFile.getName());
		}
		System.out.println("转换中....");
		for (String temp : functionName) {
			List<File> res2 = new LinkedList<>();
			for (Entry<File, List<List<String>>> aimValue : File2functionName.entrySet()) {
				if (aimValue.getValue().get(0).indexOf(temp) > -1) {
					res2.add(aimValue.getKey());
				}
			}
			functionName2File.put(temp, res2);
		}

		for (String temp : functionName) {
			List<String> res2 = new LinkedList<>();
			for (Entry<File, List<List<String>>> aimValue : File2functionName.entrySet()) {
				if (aimValue.getValue().get(0).indexOf(temp) > -1) {
					res2.addAll(aimValue.getValue().get(1));
				}
			}
			functionName2Method.put(temp, res2);
		}
		String outputpath = "D://BOP被引用的功能号.txt";
		System.out.println("转换完毕，等待输出....输出路径：" + outputpath);
		File file = new File(outputpath); // 文件路径（路径+文件名）
		// 输出结果
		try {
			printRes(functionName, function, functionName2File, file, functionName2Method, function_fileName2Method,
					res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 查找这些service在那些action中被注入
		System.out.println("开始查找action...");
		System.out.println("本次处理常量【" + function.size() + "】条");
		System.out.println("本次处理文件【" + allFile.size() + "】个");
		return res;

	}

	public static Map<String, String> getStatic2Function(List<File> uffunctionFileList) {
		Map<String, String> res = new HashMap<>();
		// 获取functionName列表
		List<String> functionName = getFunctionNameValue(uffunctionFileList.get(0));
		// 获取function列表
		List<String> function = getFunctionValue(uffunctionFileList.get(0));
		return null;
	}

}

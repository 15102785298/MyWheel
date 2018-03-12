package com.tantian.bopUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FileUtils {
	static List<File> allJavaList = new LinkedList<>();

	private static void find(String pathName, int depth, String stuffix) throws IOException {
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
			}
			return;
		}
		// 获取此目录下的所有文件名与目录名
		String[] fileList = dirFile.list();
		int currentDepth = depth + 1;
		for (int i = 0; i < fileList.length; i++) {
			// 遍历文件目录
			String string = fileList[i];
			File file = new File(dirFile.getPath(), string);
			String name = file.getName();
			// 如果是一个目录，搜索深度depth++，输出目录名后，进行递归
			if (file.isDirectory()) {
				// 递归
				find(file.getCanonicalPath(), currentDepth, stuffix);
			} else {
				if (StringUtils.endsWith(name, stuffix)) {
					allJavaList.add(file);
				}
			}
		}
	}

	public static List<File> getAllFile(String pathName, int depth, String stuffix) {
		allJavaList.clear();
		try {
			find(pathName, depth, stuffix);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<File> res = new LinkedList<File>();
		res.addAll(allJavaList);
		return res;
	}

	public static List<File> getAllFile(String pathName, String stuffix) {
		allJavaList.clear();
		try {
			find(pathName, 999, stuffix);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<File> res = new LinkedList<File>();
		res.addAll(allJavaList);
		return res;
	}
}

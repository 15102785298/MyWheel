package com.tantian.bopUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class FileUtils {
	static List<File> allJavaList = new LinkedList<>();

	private static void find(String pathName, int depth, String stuffix) throws IOException {
		// ��ȡpathName��File����
		File dirFile = new File(pathName);
		// �жϸ��ļ���Ŀ¼�Ƿ���ڣ�������ʱ�ڿ���̨�������
		if (!dirFile.exists()) {
			System.out.println("do not exit");
			return;
		}
		// �ж��������һ��Ŀ¼�����ж��ǲ���һ���ļ���ʱ�ļ�������ļ�·��
		if (!dirFile.isDirectory()) {
			if (dirFile.isFile()) {
				allJavaList.add(dirFile);
			}
			return;
		}
		// ��ȡ��Ŀ¼�µ������ļ�����Ŀ¼��
		String[] fileList = dirFile.list();
		int currentDepth = depth + 1;
		for (int i = 0; i < fileList.length; i++) {
			// �����ļ�Ŀ¼
			String string = fileList[i];
			File file = new File(dirFile.getPath(), string);
			String name = file.getName();
			// �����һ��Ŀ¼���������depth++�����Ŀ¼���󣬽��еݹ�
			if (file.isDirectory()) {
				// �ݹ�
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

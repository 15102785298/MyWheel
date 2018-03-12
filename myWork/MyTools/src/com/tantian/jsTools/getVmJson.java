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
				// System.out.println(dirFile.getCanonicalFile());
			}
			return;
		}
		// ��ȡ��Ŀ¼�µ������ļ�����Ŀ¼��
		String[] fileList = dirFile.list();
		int currentDepth = depth + 1;
		for (int i = 0; i < fileList.length; i++) {
			// �����ļ�Ŀ¼
			String string = fileList[i];
			// File("documentName","fileName")��File����һ��������
			File file = new File(dirFile.getPath(), string);
			String name = file.getName();
			// �����һ��Ŀ¼���������depth++�����Ŀ¼���󣬽��еݹ�
			if (file.isDirectory()) {
				// �ݹ�
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
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (StringUtils.isNotBlank(line)
						&& (StringUtils.contains(line, ".json") || StringUtils.contains(line, ".htm"))
						&& !StringUtils.startsWith("//", line) && !StringUtils.startsWith("##", line)) {
					if (line.indexOf("/") < 0) {
						line = br.readLine(); // һ�ζ���һ������
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
						line = br.readLine(); // һ�ζ���һ������
						continue;
					}
					res.add(finalString);
				}
				line = br.readLine(); // һ�ζ���һ������
			}
			res.add(StringUtils.split(readFile.getName(), ".")[0] + ".htm");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static Set<String> searchUrl(File readFile, String url) {
		Set<String> res = new HashSet<>();
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
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
				line = br.readLine(); // һ�ζ���һ������
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
			System.out.println("---------------------�ļ���" + temp.getName() + "---------------------");
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
			System.out.println("---------------------�ļ���" + temp.getName() + "---------------------");
		}
	}
}

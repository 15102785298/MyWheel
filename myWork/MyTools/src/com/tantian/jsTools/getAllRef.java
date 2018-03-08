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

import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.StringUtils;

public class getAllRef {
	private static int depth = 100;
	private static boolean isShowEmptyFunction = false;
	static List<File> allJavaList = new LinkedList<>();

	public static void find(String pathName, int depth) throws IOException {
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
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					String[] list = line.split("=");
					res.add(StringUtils.replace(list[1].split(";")[0], "\"", "").trim());
				}
				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static List<String> getFunctionNameValue(File readFile) {
		List<String> res = new LinkedList<>();
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
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
				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public static boolean isFindValueInFile(File readFile, String findValue, List<String> fileStuffix) {
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			// ���ݺ�׺ɸѡ
			if (fileStuffix != null) {
				for (String temp : fileStuffix) {
					if (StringUtils.endsWith(filename.getName(), temp)) {
						return false;
					}
				}
			}
			// System.out.println("�����ļ��У�" + filename.getName() + "Ŀ���ֶΣ�" +
			// findValue);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			int readTime = 1;
			line = br.readLine();
			while (line != null) {
				// System.out.println("��ȡ" + readTime++ + line);
				if (StringUtils.startsWith(line.trim(), "//")) {
					line = br.readLine(); // һ�ζ���һ������
					continue;
				}
				if (StringUtils.indexOf(line, findValue) > -1) {
					// System.out.println(new
					// StringBuffer().append("�ļ���").append(filename.getName()).append("�ҵ�Ŀ���ֶΣ�").append(findValue).toString());
					return true;
				}
				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static List<String> findMutiValueInFile(File readFile, List<String> findValueList,
			List<String> fileStuffix) {
		List<String> res = new LinkedList<>();
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			// ���ݺ�׺ɸѡ
			if (fileStuffix != null) {
				for (String temp : fileStuffix) {
					if (StringUtils.equals(filename.getName(), temp)) {
						return new LinkedList<>();
					}
				}
			}
			// System.out.println("�����ļ��У�" + filename.getName() + "Ŀ���ֶΣ�" +
			// findValue);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			int readTime = 1;
			line = br.readLine();
			while (line != null) {
				// System.out.println("��ȡ" + readTime++ + line);
				if (StringUtils.startsWith(line.trim(), "//")) {
					line = br.readLine(); // һ�ζ���һ������
					continue;
				}
				for (String temp : findValueList) {
					if (StringUtils.indexOf(line, temp) > -1) {
						System.out.println(new StringBuffer().append("�ļ���").append(filename.getName()).append("�ҵ�Ŀ���ֶΣ�")
								.append(temp).toString());
						res.add(temp);
					}
				}

				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private static void printRes(List<String> functionName, List<String> function,
			Map<String, List<File>> functionName2File, File file) throws FileNotFoundException, IOException {
		FileOutputStream outStream = new FileOutputStream(file); // �ļ���������ڽ�����д���ļ�
		// �б����õĹ��ܺ��Լ���������service
		System.out.println("----------�����ļ�Ԥ��----------");
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
			if (!file.exists()) { // �ļ��������򴴽��ļ����ȴ���Ŀ¼
				File dir = new File(file.getParent());
				dir.mkdirs();
				file.createNewFile();
			}
			outStream.write(("-------------------���ܺš�" + functinId + "��----------------------\r\n").getBytes());
			System.out.println("-------------------���ܺš�" + functinId + "��----------------------");
			for (File temp : aimValue.getValue()) {
				System.out.println(temp.getName());
				outStream.write((temp.getName() + "\r\n").getBytes());
			}
			System.out.println("-------------------���ܺš�" + functinId + "��----------------------");
			outStream.write(("-------------------���ܺš�" + functinId + "��----------------------\r\n").getBytes());
		}
		outStream.write(("-------------------BOP���õĹ��ܺ�----------------------\r\n").getBytes());
		for (String temp : res) {
			outStream.write((temp + "\r\n").getBytes());

		}
		outStream.write(("-------------------BOP���õĹ��ܺ�----------------------\r\n").getBytes());

		outStream.close(); // �ر��ļ������
		System.out.println("�ļ�������ϣ�");
	}

	public static void main(String[] args) throws IOException {
		// ��ȡĿ¼�������ļ�
		find("D://zt//BOP//Sources//WebCodes//bop2.0", depth);
		// ������
		List<File> allFile = new LinkedList<>();
		allFile.addAll(allJavaList);
		// ���
		allJavaList.clear();
		// ��ȡUrlValus
		find("D:/zt/BOP/Sources/WebCodes/bop2.0/bop-biz-platform/bop-pub/src/main/java/com/hundsun/bop/pub/constant/UFFunction.java",
				depth);
		List<File> uffunction = new LinkedList<>();
		uffunction.addAll(allJavaList);
		// ���
		allJavaList.clear();
		// ��ȡfunctionName�б�
		List<String> functionName = getFunctionNameValue(uffunction.get(0));
		// ��ȡfunction�б�
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
				}
			}));
			System.out.println("��������ļ�" + aimFile.getName());
		}
		System.out.println("ת����....");
		for (String temp : functionName) {
			List<File> res = new LinkedList<>();
			for (Entry<File, List<String>> aimValue : File2functionName.entrySet()) {
				if (aimValue.getValue().indexOf(temp) > -1) {
					res.add(aimValue.getKey());
				}
			}
			functionName2File.put(temp, res);
		}
		System.out.println("ת����ϣ��ȴ����....");
		File file = new File("D://BOP�����õĹ��ܺ�"); // �ļ�·����·��+�ļ�����
		// ������
		printRes(functionName, function, functionName2File, file);

		// ������Щservice����Щaction�б�ע��
		Map<String, List<File>> service2Action = new HashMap<>();

		System.out.println(function.size());
		System.out.println(functionName.size());
		System.out.println(allFile.size());
	}

}

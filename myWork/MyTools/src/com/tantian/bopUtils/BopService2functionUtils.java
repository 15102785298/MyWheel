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
		List<String> res = new ArrayList<>();
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
		List<String> res = new ArrayList<>();
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

	public boolean isFindValueInFile(File readFile, String findValue, List<String> fileStuffix) {
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

	public static Map<String, Object> findMutiValueInFile(File readFile, List<String> findValueList,
			List<String> fileStuffix) {
		Map<String, Object> resFinal = new HashMap<>();
		Map<String, List<String>> function_fileName2Method = new HashMap<>();

		List<List<String>> res = new LinkedList<>();
		List<String> res1 = new LinkedList<>();
		List<String> res2 = new LinkedList<>();
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			// ���ݺ�׺ɸѡ
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
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			String nowMethod = "";
			line = br.readLine();
			while (line != null) {
				line = line.trim();
				// System.out.println("��ȡ" + readTime++ + line);
				if (StringUtils.startsWith(line, "//")) {
					line = br.readLine(); // һ�ζ���һ������
					continue;
				}
				// ���µ�ǰ������
				// ��������������"."
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
						System.out.println(new StringBuffer().append("�ļ���").append(filename.getName()).append("�ҵ�Ŀ���ֶΣ�")
								.append(temp).append("��Ӧ����Ϊ��").append(nowMethod == "" ? "δ�ҵ�����" : nowMethod)
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

				line = br.readLine(); // һ�ζ���һ������
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
					sb.append("������").append(temp.getName()).append("����������").append(MethodName).append("��")
							.append("��������").append(aimValue.getKey()).append("��");
					System.out.println(sb.toString());
					outStream.write(sb.append("\r\n").toString().getBytes());
				}

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

	@SuppressWarnings("unchecked")
	public static Map<String, Set<String>> getService2FunctionMap(List<File> allFile, List<File> uffunction) {
		Map<String, Set<String>> res = new HashMap<>();
		uffunction.addAll(allJavaList);
		// ���
		allJavaList.clear();
		// ��ȡfunctionName�б�
		List<String> functionName = getFunctionNameValue(uffunction.get(0));
		// ��ȡfunction�б�
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
			System.out.println("��������ļ�" + aimFile.getName());
		}
		System.out.println("ת����....");
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
		String outputpath = "D://BOP�����õĹ��ܺ�.txt";
		System.out.println("ת����ϣ��ȴ����....���·����" + outputpath);
		File file = new File(outputpath); // �ļ�·����·��+�ļ�����
		// ������
		try {
			printRes(functionName, function, functionName2File, file, functionName2Method, function_fileName2Method,
					res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ������Щservice����Щaction�б�ע��
		System.out.println("��ʼ����action...");
		System.out.println("���δ�������" + function.size() + "����");
		System.out.println("���δ����ļ���" + allFile.size() + "����");
		return res;

	}

	public static Map<String, String> getStatic2Function(List<File> uffunctionFileList) {
		Map<String, String> res = new HashMap<>();
		// ��ȡfunctionName�б�
		List<String> functionName = getFunctionNameValue(uffunctionFileList.get(0));
		// ��ȡfunction�б�
		List<String> function = getFunctionValue(uffunctionFileList.get(0));
		return null;
	}

}

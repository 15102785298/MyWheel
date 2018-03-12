package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.FileUtils;
import com.tantian.bopUtils.PatternUtils;

public class BopVm {
	// ��Ŀ·��
	private String projectPath;
	// ����
	private String vmsName;
	// ·��
	private String vmPath;
	// �ļ�
	private File vmFile;
	// vm�����е��õ�json
	private Set<BopJson> urlJson;
	// vm����ԭ������
	String urlSelfHtm;

	public BopVm(File vmFile, String projectPath, List<File> urlFileList) {
		this.projectPath = projectPath;
		this.vmFile = vmFile;
		this.vmsName = StringUtils.split(vmFile.getName(), ".")[0];
		this.vmPath = vmFile.getAbsolutePath();
		this.urlJson = searchJsonAndHtm(vmFile, urlFileList);
		this.urlSelfHtm = PatternUtils.patternString(vmPath, "\\\\bop\\\\(\\S*).vm", 0).size() == 0 ? ""
				: PatternUtils.patternString(vmPath, "\\\\bop\\\\(\\S*).vm", 0).get(0);
	}

	public void printfSelf() {
		System.out.println("��Ŀ·����" + projectPath);
		System.out.println("������" + vmsName);
		System.out.println("��·����" + vmPath);
		System.out.println("vm����ԭ������" + urlSelfHtm);
		System.out.println("--------vm����" + vmsName + "�е��õ�json------");
		for (BopJson temp : urlJson) {
			temp.printfSelf();
		}
		System.out.println("--------vm����" + vmsName + "�е��õ�json------");
	}

	/**
	 * �����ļ������е�Json����
	 *
	 * @param readFile
	 * @return
	 */
	private static Set<BopJson> searchJsonAndHtm(File readFile, List<File> urlFileList) {
		Set<BopJson> res = new HashSet<>();
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
					res.add(new BopJson(finalString, urlFileList));
				}
				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

}

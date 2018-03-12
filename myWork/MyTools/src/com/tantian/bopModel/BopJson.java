package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class BopJson {

	// ��������
	private String urlJson;
	// �����Ӧ��������
	private Set<String> url2constantNameList;
	// ��ȡ��UFFUNCTION�ļ�
	private static Set<String> ufFunctionlines = null;

	public String getUrlJson() {
		return urlJson;
	}

	public void setUrlJson(String urlJson) {
		this.urlJson = urlJson;
	}

	public Set<String> getUrl2constantNameList() {
		return url2constantNameList;
	}

	public void setUrl2constantNameList(Set<String> url2constantNameList) {
		this.url2constantNameList = url2constantNameList;
	}

	public BopJson(String urlJson, List<File> urlFileList) {
		this.urlJson = urlJson;
		this.url2constantNameList = new HashSet<>();
		for (File temp : urlFileList) {
			this.url2constantNameList.addAll(searchUrl(temp, this.urlJson));
		}
	}

	public void printfSelf() {
		System.out.println("�������ƣ�" + urlJson);
		int i = 1;
		for (String temp : url2constantNameList) {
			System.out.println("��" + i++ + "����������" + temp);
		}
	}

	/**
	 * ����url��Ӧ�ĳ�������
	 *
	 * @param readFile
	 * @param url
	 * @return
	 */
	private static Set<String> searchUrl(File readFile, String url) {
		Set<String> res = new HashSet<>();
		if (ufFunctionlines == null) {
			ufFunctionlines = new HashSet<String>();
			try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
				File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
				InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // ����һ������������reader
				BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
				String line = "";
				line = br.readLine();
				while (line != null) {
					line = line.trim();
					ufFunctionlines.add(line);
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
		} else {
			for (String line : ufFunctionlines) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, url)
						&& !StringUtils.startsWith(line, "//")) {
					String finalString = line.split("=")[0].trim()
							.split(" ")[line.split("=")[0].trim().split(" ").length - 1].trim();
					res.add(finalString);
				}

			}
			return res;
		}

	}

}

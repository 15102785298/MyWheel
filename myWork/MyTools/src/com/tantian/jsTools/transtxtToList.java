package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

public class transtxtToList {
	public static void main(String[] args) throws IOException {
		// System.out.println(TransFormJsToMap());
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw

			/* ����TXT�ļ� */
			String pathname = "C:\\Users\\Administrator\\Desktop\\bop.txt"; // ����·�������·�������ԣ������Ǿ���·����д���ļ�ʱ��ʾ���·��
			File filename = new File(pathname); // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			line = br.readLine();
			while (line != null) {
				if (StringUtils.isNotBlank(line) && StringUtils.contains(line, "static")
						&& !StringUtils.startsWith("//", line.trim())) {
					String[] list = line.split("=");
					System.out
							.println(StringUtils.replace(list[1].split(";")[0], "\"", "").trim());

				}
				line = br.readLine(); // һ�ζ���һ������

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

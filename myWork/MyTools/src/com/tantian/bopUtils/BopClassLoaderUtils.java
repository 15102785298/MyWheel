package com.tantian.bopUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

public class BopClassLoaderUtils extends ClassLoader {
	/**
	 * lib:��ʾ���ص��ļ���jar���� ����tomcat����{PROJECT}/WEB-INF/lib/
	 */
	/**
	 * classes:��ʾ���ص��ļ��ǵ�����class�ļ� ����tomcat����{PROJECT}/WEB-INF/classes/
	 */
	/**
	 * ��ȡ�����е�jar���е�class��ȡ���ڴ��� Ȼ�������Ҫ��ȡ��ʱ���ٴ�map�в���
	 */
	private Map<String, byte[]> map;

	/**
	 * ֻ��Ҫָ����Ŀ·���ͺ� Ĭ��jar����·����Ŀ¼��{PROJECT}/WEB-INF/lib/
	 * Ĭ��class����·����Ŀ¼��{PROJECT}/WEB-INF/classes/
	 *
	 * @param webPath
	 * @throws MalformedURLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public BopClassLoaderUtils(List<File> allJar) {
		map = new HashMap<String, byte[]>(64);
		preReadJarFile(allJar);
	}

	/**
	 * ���ո���Ļ��ƣ�����ڸ�����û���ҵ����� �Ż�������findClass������ ����ֻ����ط����Լ�Ŀ¼�µ��ļ�
	 * ��ϵͳ�Դ���Ҫ��class���������������
	 */
	@Override
	protected Class<?> findClass(String name) {
		try {
			byte[] result = getClassFromFileOrMap(name);
			if (result == null) {
				// throw new FileNotFoundException();
			} else {
				if (StringUtils.indexOf(name, "/") > -1) {
					return null;
				}
				Class<?> finedClass = defineClass(name, result, 0, result.length);
				return finedClass;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ��ָ����classes�ļ������ҵ��ļ�
	 *
	 * @param name
	 * @return
	 */
	private byte[] getClassFromFileOrMap(String name) {
		String classPath = name.replace('.', File.separatorChar) + ".class";
		File file = new File(classPath);
		if (file.exists()) {
			InputStream input = null;
			try {
				input = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int bufferSize = 4096;
				byte[] buffer = new byte[bufferSize];
				int bytesNumRead = 0;
				while ((bytesNumRead = input.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesNumRead);
				}
				return baos.toByteArray();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			if (map.containsKey(name)) {
				// ȥ��map�е����ã�����GC�޷��������õ�class�ļ�
				return map.remove(name);
			}
		}
		return null;
	}

	/**
	 * Ԥ��lib����İ�
	 */
	private void preReadJarFile(List<File> allJar) {
		List<File> list = allJar;
		for (File f : list) {
			JarFile jar;
			try {
				jar = new JarFile(f);
				readJAR(jar);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡһ��jar���ڵ�class�ļ��������ڵ�ǰ��������map��
	 *
	 * @param jar
	 * @throws IOException
	 */
	private void readJAR(JarFile jar) throws IOException {
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {
			JarEntry je = en.nextElement();
			String name = je.getName();
			if (name.endsWith(".class")) {
				String clss = StringUtils.replace(name.replace(".class", ""), "/", ".");

				if (this.findLoadedClass(clss) != null)
					continue;

				InputStream input = jar.getInputStream(je);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int bufferSize = 4096;
				byte[] buffer = new byte[bufferSize];
				int bytesNumRead = 0;
				while ((bytesNumRead = input.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesNumRead);
				}
				byte[] cc = baos.toByteArray();
				input.close();
				map.put(clss, cc);// ��ʱ��������
			}
		}
	}

	/**
	 * ���һ��jar������������ȥ��
	 *
	 * @param jarPath
	 * @throws IOException
	 */
	public void addJar(String jarPath) throws IOException {
		File file = new File(jarPath);
		if (file.exists()) {
			JarFile jar = new JarFile(file);
			readJAR(jar);
		}
	}

	public Map<String, Class<?>> getAllClass() {
		Set<String> classNameSet = new HashSet<>();
		classNameSet.addAll(map.keySet());
		Map<String, Class<?>> res = new HashMap<>();
		for (String temp : classNameSet) {
			try {
				if (!StringUtils.startsWith(temp, "com.hundsun")) {
					continue;
				}
				System.out.println(temp);
				res.put(temp, loadClass(temp));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoClassDefFoundError e) {
				System.out.println("����ʧ���ࣺ" + temp);
				// e.printStackTrace();
			}
		}
		return res;
	}
}

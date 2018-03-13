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
	 * lib:表示加载的文件在jar包中 类似tomcat就是{PROJECT}/WEB-INF/lib/
	 */
	/**
	 * classes:表示加载的文件是单纯的class文件 类似tomcat就是{PROJECT}/WEB-INF/classes/
	 */
	/**
	 * 采取将所有的jar包中的class读取到内存中 然后如果需要读取的时候，再从map中查找
	 */
	private Map<String, byte[]> map;

	/**
	 * 只需要指定项目路径就好 默认jar加载路径是目录下{PROJECT}/WEB-INF/lib/
	 * 默认class加载路径是目录下{PROJECT}/WEB-INF/classes/
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
	 * 按照父类的机制，如果在父类中没有找到的类 才会调用这个findClass来加载 这样只会加载放在自己目录下的文件
	 * 而系统自带需要的class并不是由这个加载
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
	 * 从指定的classes文件夹下找到文件
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
				// 去除map中的引用，避免GC无法回收无用的class文件
				return map.remove(name);
			}
		}
		return null;
	}

	/**
	 * 预读lib下面的包
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
	 * 读取一个jar包内的class文件，并存在当前加载器的map中
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
				map.put(clss, cc);// 暂时保存下来
			}
		}
	}

	/**
	 * 添加一个jar包到加载器中去。
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
				System.out.println("加载失败类：" + temp);
				// e.printStackTrace();
			}
		}
		return res;
	}
}

package com.tantian.bopModel;

import java.io.File;
import java.util.List;

/**
 * 类
 *
 * @author hspcadmin
 *
 */
public class BopClass {
	// 类名
	private String className;
	// 路径
	private String classPath;
	// 文件
	private File classFile;
	// 类自己的方法
	private List<BopMethod> bopSelfMethods;
	// 类实现的接口
	private List<BopInterface> bopImplementsInterface;
	// 类继承的类
	private List<BopClass> bopImplementsClass;
	// 类引用的Bean
	private List<BopClass> bopQuoteClass;
	// 类调用的方法
	private List<BopMethod> bopInvokeMethods;
}

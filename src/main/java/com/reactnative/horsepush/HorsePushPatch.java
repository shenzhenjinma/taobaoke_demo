package com.reactnative.horsepush;

import java.io.File;

/**
 * 类说明： 	APK Patch工具类
 * 68xg.com
 */
public class HorsePushPatch {

	/**
	 * native方法 使用路径为oldApkPath的apk与路径为patchPath的补丁包，合成新的apk，并存储于newApkPath
	 * 
	 * 返回：0，说明操作成功
	 * 
	 * @param oldApkPath 示例:/sdcard/old.apk
	 * @param newApkPath 示例:/sdcard/new.apk
	 * @param patchPath  示例:/sdcard/xx.patch
	 * @return
	 */
	static {
		System.loadLibrary("HorsePushPatch");
	}

	public static native int patch(String oldFilePath, String newFilePath, String patchPath);

	public static int horsePushPatch(String oldFilePath, String newFilePath, String patchPath){
		if(!(new File(oldFilePath).exists() && new File(patchPath).exists()))
			return -1;
		return patch(oldFilePath,newFilePath,patchPath);
	}


}
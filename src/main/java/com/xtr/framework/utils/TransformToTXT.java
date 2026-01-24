package com.xtr.framework.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class TransformToTXT {

	public static String creatTxtFile(String path,String name) throws IOException {
		return creatTempFile(path, name);
	}

	public static String creatChkFile(String path, String name) throws IOException {
		return creatTempFile(path, name);
	}

	private static String creatTempFile(String path,String name) throws IOException {
		Path filePath = Paths.get(path, name + ".tmp");
		if (!Files.exists(filePath)) {
			Files.createFile(filePath);
		}
		return filePath.toString();
	}

	public boolean writeTmpFile(String path,StringBuffer newbuf,String type, String encoding) throws IOException {
		boolean flag = false;
		String fileIn = newbuf.toString();
		String temp = "";

		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			File file = new File(path);
			// 将文件读入输入流
			isr = new InputStreamReader(Files.newInputStream(file.toPath()),encoding);
			br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			// 保存该文件原有的内容
			for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf.append(temp);
                // 行与行之间的分隔符 相当于“\n”
				buf.append("\n");
			}
			buf.append(fileIn);
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),encoding);
			BufferedWriter writer=new BufferedWriter(write);
			writer.write(buf.toString());
			writer.close();
			flag = true;
		} catch (IOException e1) {
			throw e1;
		} finally {
			if (br != null) {
				br.close();
			}
			if (isr != null) {
				isr.close();
			}
		}
		return flag;
	}

	public static void tmpTransToTxt(String path, String filename, String last, String changeLast) {
		File tmpFileName = new File(path + filename + last);
		if (tmpFileName.exists()) {
			File newFile = new File(path + filename + changeLast);
			if (!tmpFileName.renameTo(newFile)) {
				log.error("Failed to rename file: " + tmpFileName + " to " + newFile);
			}
		}
	}

	public void copyFile(String oldPath, String newPath) {
		try (InputStream inStream = new FileInputStream(oldPath);
			 FileOutputStream fs = new FileOutputStream(newPath)) {

			int bytesum = 0;
			int byteread;
			byte[] buffer = new byte[1444];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
		} catch (FileNotFoundException e) {
			log.error("File not found: " + e.getMessage());
		} catch (IOException e) {
			log.error("Error copying file: " + e.getMessage());
		}
	}

}

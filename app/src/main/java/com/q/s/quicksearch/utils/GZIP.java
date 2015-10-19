package com.q.s.quicksearch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import okio.Buffer;
import okio.GzipSink;
import okio.GzipSource;
import okio.Sink;
import okio.Source;
import okio.Timeout;

public class GZIP {

//	public static void writeGzip(File inf, File zipf) throws IOException {
//		FileInputStream fin = new FileInputStream(inf);
//
//		FileOutputStream fout = new FileOutputStream(zipf);
//		GZIPOutputStream zout = new GZIPOutputStream(fout);
//
//		byte[] buf = new byte[16 * 1024];
//		int len = 0;
//		while ((len = fin.read(buf)) != -1) {
//			zout.write(buf, 0, len);
//		}
//		zout.flush();
//		fin.close();
//		zout.close();
//		fout.close();
//	}

	public static void readGzip(InputStream inputStream, File outf) throws IOException {
		GZIPInputStream zin = new GZIPInputStream(inputStream);
		FileOutputStream fout = new FileOutputStream(outf);
		byte[] buf = new byte[16 * 1024];
		int len = 0;
		while ((len = zin.read(buf)) != -1) {
			fout.write(buf, 0, len);
		}
		fout.flush();
		fout.close();
		zin.close();
		inputStream.close();
	}

}

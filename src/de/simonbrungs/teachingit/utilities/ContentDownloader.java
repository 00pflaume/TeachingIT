package de.simonbrungs.teachingit.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class ContentDownloader {
	String toDownload;
	String path;

	public ContentDownloader(String pDownloadURL, String pPath) {
		toDownload = pDownloadURL;
		path = pPath;
	}

	public File download() throws IOException {
		File file = new File(path);
		URL website = new URL(toDownload);
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream("information.html");
		fos.close();
		return file;
	}

}

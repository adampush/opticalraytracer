package opticalraytracer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MyInitializationManager {
	OpticalRayTracer parent;
	ProgramValues programValues;
	String fileSep;
	String lineSep;
	String appName;
	String userDir;
	String userPath;
	String initPath;
	
	MyCustomFrame m;
	
	

	public MyInitializationManager(OpticalRayTracer p, ProgramValues pv, MyCustomFrame m) {
		this.parent = p;
		this.programValues = pv;
		
		this.m = m; // AJP
		
		lineSep = System.getProperty("line.separator");
		fileSep = System.getProperty("file.separator");
		appName = parent.getClass().getSimpleName();
		userDir = System.getProperty("user.home");
		
		userPath = userDir + fileSep + "." + appName;
		initPath = userPath + fileSep + appName + ".MyCustomFrame" + ".ini";
		
		testMakeDirs(userPath);
	}

	protected void setFullConfiguration(String data) {
		// TODO: rewrite this to set config of MyCustomFrame
		if (data != null) {
			String pv = data.replaceFirst(
					"(?is).*?program \\{\\s*(.*?)\\s*\\}.*", "$1");
			programValues.setValues(pv);
//			parent.componentList = new ArrayList<>();		// Don't update ORT components -- just MyCustomFrame stuff
			String s = "(?is)object \\{\\s*(.*?)\\s*\\}";
			Pattern pat = Pattern.compile(s);
			Matcher m = pat.matcher(data);
			while (m.find()) {
				String v = m.group(1);
//				parent.makeNewComponent(v, false, Common.OBJECT_REFRACTOR);		// Don't update ORT components -- just MyCustomFrame stuff
			}
			parent.writeProgramControls();
		}
	}

	protected void readConfig() {
		String data = readFile(initPath);
		setFullConfiguration(data);
	}

	protected void readStream(InputStream in) {
		int blockLen = 512;
		int len = 0;
		byte[] bdata = new byte[blockLen];
		StringBuilder data = new StringBuilder();
		do {
			try {
				len = in.read(bdata,0,blockLen);
				data.append(new String(bdata,0,len));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				len = 0;
			}
		} while (len == blockLen);
		if (data.length() > 0) {
			setFullConfiguration(data.toString());
		}
	}

	protected void writeConfig(boolean includeHeader) {
		writeFile(initPath, getFullConfiguration(includeHeader));
	}

	protected String getFullConfiguration(boolean includeHeader) {
		StringBuilder sb = new StringBuilder();
		if (includeHeader) {
			sb.append(String.format("# %s\n", parent.fullAppName));
			sb.append("# http://arachnoid.com/OpticalRayTracer\n");
			String date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z")
					.format(new Date());
			sb.append(String.format("# %s\n\n", date));
		}
		sb.append(String.format("program {\n"));
		sb.append(programValues.getValues());
		sb.append(String.format("}\n\n"));
		for (OpticalComponent oc : parent.componentList) {
			sb.append(oc.getValues());
			sb.append("\n");
		}
		return sb.toString();
	}

	private String readFile(String path) {
		String result = null;
		try {
			result = new String(Files.readAllBytes(Paths.get(path)),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// no initialization file, so first run
			parent.setDefaults(true);
		}
		return result;
	}

	private void writeFile(String path, String data) {
		try {
			PrintWriter out = new PrintWriter(path);
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean testMakeDirs(String path) {
		File fpath = new File(path);
		if (fpath.exists()) {
			return false;
		} else {
			fpath.mkdirs();
			return true;
		}
	}
}

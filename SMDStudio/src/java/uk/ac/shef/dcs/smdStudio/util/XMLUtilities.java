package uk.ac.shef.dcs.smdStudio.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>
 * Title: XML Utilities
 * </p>
 * 
 * <p>
 * Description: Manages operation on the XML files inside the program
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0
 */
public class XMLUtilities {

	/**
	 * Writes content of the object passed to the file at the specified path
	 * 
	 * @param path
	 *            String
	 * @param object
	 *            Object
	 * @throws IOException
	 */
	public static void write(String path, Object object) throws IOException {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream(path)));
		encoder.writeObject(object);
		encoder.close();
	}

	/**
	 * Writes content of the object passed to the file
	 * 
	 * @param file
	 *            File
	 * @param object
	 *            Object
	 * @throws IOException
	 */
	public static void write(File file, Object object) throws IOException {
		XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
				new FileOutputStream(file)));
		encoder.writeObject(object);
		encoder.close();
	}

	/**
	 * Reads content of the file located in the passed path, and save return as
	 * an object
	 * 
	 * @param path
	 *            String
	 * @throws IOException
	 * @return object Object
	 */
	public static Object read(String path) throws IOException {
		Object object;
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
				new FileInputStream(path)));
		object = decoder.readObject();
		decoder.close();
		return object;
	}

	/**
	 * Reads content of the file passed, and save return as an object
	 * 
	 * @param file
	 *            File
	 * @throws IOException
	 * @return object Object
	 */
	public static Object read(File file) throws IOException {
		Object object;
		XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
				new FileInputStream(file)));
		object = decoder.readObject();
		decoder.close();
		return object;
	}
}

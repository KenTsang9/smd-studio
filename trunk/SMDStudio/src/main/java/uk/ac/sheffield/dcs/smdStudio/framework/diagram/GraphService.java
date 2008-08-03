/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package uk.ac.sheffield.dcs.smdStudio.framework.diagram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Statement;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import uk.ac.sheffield.dcs.smdStudio.UMLEditor;
import uk.ac.sheffield.dcs.smdStudio.framework.file.CustomPersistenceDelegate;
import uk.ac.sheffield.dcs.smdStudio.framework.util.ClipboardPipe;
import uk.ac.sheffield.dcs.smdStudio.product.diagram.common.ImageNode;

public class GraphService {

	/**
	 * Reads a graph from a byte buffer
	 * 
	 * @param buffer
	 *            byte buffer that contains the serialized graph
	 * @return the graph
	 * @throws IOException
	 *             e
	 */
	public static Graph deserializeGraph(ByteBuffer buffer) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer.array());
		Graph g = GraphService.readGraph(bis);
		bis.close();
		return g;
	}

	/**
	 * Saves the graph to a byte buffer
	 * 
	 * @param g
	 *            the graph to save
	 * @return a byte buffer that contains the serialized graph
	 * @throws IOException
	 *             e
	 */
	public static ByteBuffer serializeGraph(Graph g) throws IOException {
		// Serialize to a byte array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GraphService.write(g, bos);
		// Get the bytes of the serialized object
		ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
		bos.close();
		return buffer;
	}

	/**
	 * Return the image correspondiojng to the graph
	 * 
	 * @param graph
	 * @author Alexandre de Pellegrin
	 * @return bufferedImage. To convert it into an image, use the syntax :
	 *         Toolkit
	 *         .getDefaultToolkit().createImage(bufferedImage.getSource());
	 */
	public static BufferedImage getImage(Graph graph) {
		BufferedImage dummy = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_RGB);
		// need a dummy image to get a Graphics to
		// measure the size
		Rectangle2D bounds = graph.getBounds((Graphics2D) dummy.getGraphics());
		BufferedImage image = new BufferedImage((int) bounds.getWidth() + 1,
				(int) bounds.getHeight() + 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.translate(-bounds.getX(), -bounds.getY());
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds
				.getWidth() + 1, bounds.getHeight() + 1));
		g2.setColor(Color.BLACK);
		g2.setBackground(Color.WHITE);
		graph.draw(g2, new Grid());

		return image;
	}

	/**
	 * Export graph to clipboard (Do not merge with exportToClipBoard(). Used in
	 * Eclipse plugin)
	 * 
	 * @author Alexandre de Pellegrin
	 * @param graph
	 */
	public static void exportToclipBoard(Graph graph) {
		BufferedImage bImage = getImage(graph);
		ClipboardPipe pipe = new ClipboardPipe(bImage);
		Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(pipe, null);
	}

	/**
	 * Writes the given graph in an outputstream. We use long-term bean
	 * persistence to save the program data. See
	 * http://java.sun.com/products/jfc/tsc/articles/persistence4/index.html for
	 * an overview.
	 * 
	 * @param out
	 *            the stream for saving
	 */
	public static void write(Graph graph, OutputStream out) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		ClassLoader pcl = UMLEditor.getInstance().getPluginClassLoader();
		if (pcl != null)
			Thread.currentThread().setContextClassLoader(pcl);
		XMLEncoder encoder = getXMLEncoder(out);
		encoder.writeObject(graph);
		encoder.close();
		if (pcl != null)
			Thread.currentThread().setContextClassLoader(cl); // TODO: Is this
		// necessary?
	}

	/**
	 * Reads a graph file
	 * 
	 * @param in
	 *            the input stream to read
	 * @return the graph that is read in
	 */
	public static Graph readGraph(InputStream in) throws IOException {
		XMLDecoder reader = new XMLDecoder(in, null, new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				e.printStackTrace();
			}
		}, UMLEditor.getInstance().getPluginClassLoader());
		Graph graph = (Graph) reader.readObject();
		in.close();
		return graph;
	}

	/**
	 * Creates a new instance of XML Encoder pre-configured for Violet beans
	 * serailization
	 * 
	 * @param out
	 * @return configured encoder
	 */
	public static XMLEncoder getXMLEncoder(OutputStream out) {
		XMLEncoder encoder = new XMLEncoder(out);

		encoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception ex) {
				ex.printStackTrace();
			}
		});
		setPersistenceDelegate(encoder);
		return encoder;
	}

	public static void setPersistenceDelegate(XMLEncoder encoder) {
		/*
		 * The following does not work due to bug #4741757
		 * 
		 * encoder.setPersistenceDelegate( Point2D.Double.class, new
		 * DefaultPersistenceDelegate( new String[]{ "x", "y" }) );
		 */
		encoder.setPersistenceDelegate(Point2D.Double.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Point2D p = (Point2D) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setLocation", new Object[] {
										new Double(p.getX()),
										new Double(p.getY()) }));
					}
				});
		encoder.setPersistenceDelegate(Line2D.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Line2D l = (Line2D) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setLine", new Object[] {
										new Double(l.getX1()),
										new Double(l.getY1()),
										new Double(l.getX2()),
										new Double(l.getY2()) }));
					}
				});
		encoder.setPersistenceDelegate(Rectangle2D.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Rectangle2D r = (Rectangle2D) oldInstance;
						out.writeStatement(new Statement(oldInstance,
								"setRect", new Object[] { new Double(r.getX()),
										new Double(r.getY()),
										new Double(r.getWidth()),
										new Double(r.getHeight()) }));
					}
				});

		encoder.setPersistenceDelegate(BentStyle.class,
				new CustomPersistenceDelegate());
		encoder.setPersistenceDelegate(LineStyle.class,
				new CustomPersistenceDelegate());
		encoder.setPersistenceDelegate(ArrowHead.class,
				new CustomPersistenceDelegate());
		encoder.setPersistenceDelegate(URL.class,
				new DefaultPersistenceDelegate(new String[] { "protocol",
						"host", "port", "file" }));
		encoder.setPersistenceDelegate(Map.class,
				new DefaultPersistenceDelegate() {
					protected void initialize(Class<?> type,
							Object oldInstance, Object newInstance, Encoder out) {
						super.initialize(type, oldInstance, newInstance, out);
						Map<?, ?> map = (Map<?, ?>) oldInstance;
						for (Iterator<?> it = map.keySet().iterator(); it
								.hasNext();) {
							Object key = it.next();
							Object value = map.get(key);
							out.writeStatement(new Statement(oldInstance,
									"put", new Object[] { key, value }));
						}
					}
				});

		AbstractGraph.setPersistenceDelegate(encoder);
		AbstractNode.setPersistenceDelegate(encoder, false);
		AbstractEdge.setPersistenceDelegate(encoder, false);
		ImageNode.setPersistenceDelegate(encoder);
	}

}

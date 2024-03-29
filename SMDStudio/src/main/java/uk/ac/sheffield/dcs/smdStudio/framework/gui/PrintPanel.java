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

package uk.ac.sheffield.dcs.smdStudio.framework.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import uk.ac.sheffield.dcs.smdStudio.framework.diagram.Graph;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.Theme;
import uk.ac.sheffield.dcs.smdStudio.framework.gui.theme.ThemeManager;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceBundleConstant;
import uk.ac.sheffield.dcs.smdStudio.framework.resources.ResourceFactory;
import uk.ac.sheffield.dcs.smdStudio.framework.swingextension.RolloverButtonUI;


/**
 * This class implements a dialog for previewing and printing a graph.
 */
@SuppressWarnings("serial")
public class PrintPanel extends JPanel {
	/**
	 * Constructs a print dialog.
	 * 
	 * @param gr
	 *            the graph to be printed
	 */
	public PrintPanel(Graph gr) {
		this.graph = gr;
		PrinterJob job = PrinterJob.getPrinterJob();
		pageFormat = job.defaultPage();
		attributes = new HashPrintRequestAttributeSet();
		layoutUI();
	}

	/**
	 * Lays out the UI of the dialog.
	 */
	public void layoutUI() {
		setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		Theme cLAF = ThemeManager.getInstance().getTheme();
		buttonPanel.setBackground(cLAF.getROLLOVERBUTTON_DEFAULT_COLOR());

		RolloverButtonUI buttonUI = new RolloverButtonUI(cLAF
				.getROLLOVERBUTTON_ROLLOVER_COLOR(), cLAF
				.getROLLOVERBUTTON_ROLLOVER_BORDER_COLOR(), cLAF
				.getROLLOVERBUTTON_DEFAULT_COLOR());

		ResourceBundle bundle = ResourceBundle.getBundle(
				ResourceBundleConstant.PRINT_STRINGS, Locale.getDefault());
		ResourceFactory factory = new ResourceFactory(bundle);

		JButton moreButton = factory.createButton("dialog.print.more");
		moreButton.setUI(buttonUI);
		buttonPanel.add(moreButton);
		moreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				scaleGraph *= Math.sqrt(2);
				canvas.repaint();
			}
		});

		JButton fewerButton = factory.createButton("dialog.print.fewer");
		fewerButton.setUI(buttonUI);
		buttonPanel.add(fewerButton);
		fewerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				scaleGraph /= Math.sqrt(2);
				canvas.repaint();
			}
		});

		JButton onePageButton = factory.createButton("dialog.print.one");
		onePageButton.setUI(buttonUI);
		buttonPanel.add(onePageButton);
		onePageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				while (getRows() * getCols() > 1)
					scaleGraph /= Math.sqrt(2);
				canvas.repaint();
			}
		});

		JButton pageSetupButton = factory.createButton("dialog.print.page");
		pageSetupButton.setUI(buttonUI);
		buttonPanel.add(pageSetupButton);
		pageSetupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				PrinterJob job = PrinterJob.getPrinterJob();
				PageFormat newPageFormat = job.pageDialog(attributes);
				if (newPageFormat != null)
					pageFormat = newPageFormat;
				canvas.repaint();
			}
		});

		JButton printButton = factory.createButton("dialog.print.print");
		printButton.setUI(buttonUI);
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PrinterJob job = PrinterJob.getPrinterJob();
					job.setPageable(makeBook());
					if (job.printDialog(attributes)) {
						pageFormat = job.validatePage(pageFormat);
						job.print(attributes);
					}
				} catch (PrinterException e2) {
					// TODO = display error
				}
			}
		});
		buttonPanel.add(printButton);

		add(buttonPanel, BorderLayout.NORTH);

		canvas = new PrintPreviewCanvas();
		JPanel canvasPanel = new JPanel();
		canvasPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
		canvasPanel.setOpaque(false);
		canvasPanel.setLayout(new BorderLayout());
		canvasPanel.add(canvas, BorderLayout.CENTER);

		add(canvasPanel, BorderLayout.CENTER);

	}

	/**
	 * Makes a book consisting of the pages to be printed.
	 * 
	 * @return the book to be printed
	 */
	private Book makeBook() {
		Book book = new Book();
		final int pageCount = getRows() * getCols();
		Printable printable = new Printable() {
			public int print(Graphics g, PageFormat pf, int page)
					throws PrinterException {
				Graphics2D g2 = (Graphics2D) g;
				if (page > pageCount)
					return Printable.NO_SUCH_PAGE;
				g2.translate(pf.getImageableX(), pf.getImageableY());
				drawPage(g2, pf, page);
				return Printable.PAGE_EXISTS;
			}

			public void drawPage(Graphics2D g2, PageFormat pf, int page) {
				int cols = getCols();
				int row = page / cols;
				int col = page % cols;
				double px = pageFormat.getImageableWidth();
				double py = pageFormat.getImageableHeight();
				g2.clip(new Rectangle2D.Double(0, 0, px, py));
				g2.translate(-col * px, -row * py);
				g2.scale((float) scaleGraph, (float) scaleGraph);
				g2.translate((float) -bounds.getX(), (float) -bounds.getY());
				g2.setColor(Color.BLACK);
				g2.setBackground(Color.WHITE);
				graph.draw(g2, null);
			}
		};

		book.append(printable, pageFormat, pageCount);
		return book;
	}

	/**
	 * Gets the number of columns currently required for the printout
	 * 
	 * @return the number of columns (>= 1)
	 */
	private int getCols() {
		return (int) Math.max(1, Math.ceil(bounds.getWidth() * scaleGraph
				/ pageFormat.getImageableWidth()));
	}

	/**
	 * Gets the number of rows currently required for the printout
	 * 
	 * @return the number of rows (>= 1)
	 */
	private int getRows() {
		return (int) Math.max(1, Math.ceil(bounds.getHeight() * scaleGraph
				/ pageFormat.getImageableHeight()));
	}

	/**
	 * The component for displaying the print preview.
	 */
	class PrintPreviewCanvas extends JComponent {
		public Dimension getPreferredSize() {
			return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		}

		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			bounds = graph.getBounds(g2);

			double xoff; // x offset of page start in window
			double yoff; // y offset of page start in window
			double scalePagesToCanvas; // scale factor to fit pages in canvas

			double px = pageFormat.getImageableWidth();
			double py = pageFormat.getImageableHeight();

			int cols = getCols();
			int rows = getRows();

			double dx = px * getCols();
			double dy = py * getRows();

			double sx = getWidth() - 1;
			double sy = getHeight() - 1;
			if (dx / dy < sx / sy) // center horizontally
			{
				scalePagesToCanvas = sy / dy;
				xoff = 0.5 * (sx - scalePagesToCanvas * dx);
				yoff = 0;
			} else
			// center vertically
			{
				scalePagesToCanvas = sx / dx;
				xoff = 0;
				yoff = 0.5 * (sy - scalePagesToCanvas * dy);
			}
			g2.translate((float) xoff, (float) yoff);
			g2.scale((float) scalePagesToCanvas, (float) scalePagesToCanvas);
			// draw page backgrounds
			Rectangle2D pages = new Rectangle2D.Double(0, 0, px * cols, py
					* rows);
			g2.setPaint(Color.WHITE);
			g2.fill(pages);
			g2.setPaint(Color.BLACK);

			AffineTransform oldTransform = g2.getTransform();

			g2.scale((float) scaleGraph, (float) scaleGraph);
			g2.translate((float) -bounds.getX(), (float) -bounds.getY());
			graph.draw(g2, null);

			g2.setTransform(oldTransform);
			// draw page outlines (ignoring margins)
			g2.setPaint(Color.LIGHT_GRAY);
			for (int i = 0; i < cols; i++)
				for (int j = 0; j < rows; j++) {
					Rectangle2D page = new Rectangle2D.Double(i * px, j * py,
							px, py);
					g2.draw(page);
				}
		}

		private static final int DEFAULT_WIDTH = 450;
		private static final int DEFAULT_HEIGHT = 300;
	}

	private PrintPreviewCanvas canvas;
	private PageFormat pageFormat;
	private PrintRequestAttributeSet attributes;
	private Graph graph;
	private Rectangle2D bounds;
	private double scaleGraph = 1;
}
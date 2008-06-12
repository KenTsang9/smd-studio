package uk.ac.shef.dcs.smdStudio.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;

/**
 * <p>
 * Title: Print Preview
 * </p>
 * 
 * <p>
 * Description: A Preview of the Quiz before printing
 * </p>
 * 
 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
 * @version 1.0.6
 */
@SuppressWarnings("serial")
public class PrintPreview extends JFrame {
	/**
	 * Page Width
	 */
	protected int m_wPage;

	/**
	 * Page Hight
	 */
	protected int m_hPage;

	/**
	 * Scale
	 */
	protected int m_scale;

	/**
	 * The Printable Object
	 */
	protected Printable m_target;

	/**
	 * A {@link JComboBox} for Scales
	 */
	protected JComboBox m_cbScale;

	/**
	 * An instance of {@link PreviewContainer}
	 */
	protected PreviewContainer m_preview;

	/**
	 * Initializes th{@link PrintPreview} frame
	 * 
	 * @param target
	 *            Printable
	 */
	public PrintPreview(Printable target) {
		this(target, "Print Preview");
	}

	/**
	 * Initializes the {@link PrintPreview} frame and shows the document passed
	 * as parameter
	 * 
	 * @param target
	 *            Printable
	 * @param title
	 *            String
	 */
	public PrintPreview(Printable target, String title) {
		super(title);
		setSize(700, 700);
		m_target = target;
		((Project) m_target).nextQuestion = 0;
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel mainPanel = new JPanel(new BorderLayout());
		setResizable(false);
		setTitle("Print Preview");
		topPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		mainPanel.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2),
				new SoftBevelBorder(SoftBevelBorder.LOWERED)));
		JToolBar tb = new JToolBar();
		tb.setBorder(BorderFactory.createEmptyBorder());
		JButton bt = new JButton("Print");
		bt.setMnemonic('p');
		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					((Project) m_target).nextQuestion = 0;
					PrinterJob printJob = PrinterJob.getPrinterJob();
					PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
					printJob.setPrintable(((Project) m_target));
					printJob.setJobName(((Project) m_target).smd
							.getCourseCode()
							+ " " + ((Project) m_target).smd.getName());
					if (printJob.printDialog(attr)) {
						printJob.print(attr);
					}

				} catch (PrinterException ex) {
					JOptionPane.showMessageDialog(PrintPreview.this,
							"Error: Unable to print the document!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		bt.addActionListener(lst);
		bt.setAlignmentY(0.5f);
		tb.add(bt);

		bt = new JButton("Close");
		bt.setMnemonic('c');
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		bt.addActionListener(lst);
		bt.setAlignmentY(0.5f);
		tb.add(bt);

		String[] scales = { "10 %", "25 %", "50 %", "100 %" };
		m_cbScale = new JComboBox(scales);
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						String str = m_cbScale.getSelectedItem().toString();
						if (str.endsWith("%"))
							str = str.substring(0, str.length() - 1);
						str = str.trim();
						try {
							m_scale = Integer.parseInt(str);
						} catch (NumberFormatException ex) {
							return;
						}
						int w = (int) (m_wPage * m_scale / 100);
						int h = (int) (m_hPage * m_scale / 100);

						Component[] comps = m_preview.getComponents();
						for (int k = 0; k < comps.length; k++) {
							if (!(comps[k] instanceof PagePreview))
								continue;
							PagePreview pp = (PagePreview) comps[k];
							pp.setScaledSize(w, h);
						}
						m_preview.doLayout();
						m_preview.getParent().getParent().validate();
					}
				};
				runner.start();
			}
		};
		m_cbScale.addActionListener(lst);
		m_cbScale.setMaximumSize(new Dimension(60, 23));
		m_cbScale.setEditable(true);
		tb.addSeparator();
		tb.add(m_cbScale);
		topPanel.add(tb, BorderLayout.SOUTH);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		m_preview = new PreviewContainer();
		createComponents();

		JScrollPane ps = new JScrollPane(m_preview);

		mainPanel.add(ps, BorderLayout.CENTER);

		getContentPane().add(mainPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Creates the print Pages for preview
	 */
	protected void createComponents() {
		try {
			PrinterJob prnJob = PrinterJob.getPrinterJob();
			PageFormat pageFormat = prnJob.defaultPage();
			if (pageFormat.getHeight() == 0 || pageFormat.getWidth() == 0) {
				JOptionPane.showMessageDialog(this,
						"Unable to determine default page size", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			m_wPage = (int) (pageFormat.getWidth());
			m_hPage = (int) (pageFormat.getHeight());
			m_scale = 10;

			int w = (int) (m_wPage * m_scale / 100);
			int h = (int) (m_hPage * m_scale / 100);
			int pageIndex = 0;

			while (true) {
				BufferedImage img = new BufferedImage(m_wPage, m_hPage,
						BufferedImage.TYPE_INT_RGB);
				Graphics g = img.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, m_wPage, m_hPage);
				if (m_target.print(g, pageFormat, pageIndex) != Printable.PAGE_EXISTS)
					break;
				PagePreview pp = new PagePreview(w, h, img);
				m_preview.add(pp);
				pageIndex++;
			}
			repaint();
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(this,
					"An ERROR occured while printing the document!", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * <p>
	 * Title: Preview Container
	 * </p>
	 * 
	 * <p>
	 * Description: Panel that contains preview pages
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2005
	 * </p>
	 * 
	 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
	 * @version 1.0
	 * 
	 * @see PrintPreview
	 */
	class PreviewContainer extends JPanel {
		/**
		 * Horizental Gap
		 */
		protected int H_GAP = 16;

		/**
		 * Vertical Gap
		 */
		protected int V_GAP = 10;

		/**
		 * Gets panel PreferredSize
		 * 
		 * @return Dimension
		 */
		public Dimension getPreferredSize() {
			int n = getComponentCount();
			if (n == 0)
				return new Dimension(H_GAP, V_GAP);
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n)
				nRow++;

			int ww = nCol * (w + H_GAP) + H_GAP;
			int hh = nRow * (h + V_GAP) + V_GAP;
			Insets ins = getInsets();
			return new Dimension(ww + ins.left + ins.right, hh + ins.top
					+ ins.bottom);
		}

		/**
		 * Gets panel MaximumSize
		 * 
		 * @return Dimension
		 */
		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		/**
		 * Gets panel MinimumSize
		 * 
		 * @return Dimension
		 */
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		/**
		 * Paints the print pages in the panel in order
		 */
		public void doLayout() {
			Insets ins = getInsets();
			int x = ins.left + H_GAP;
			int y = ins.top + V_GAP;

			int n = getComponentCount();
			if (n == 0)
				return;
			Component comp = getComponent(0);
			Dimension dc = comp.getPreferredSize();
			int w = dc.width;
			int h = dc.height;

			Dimension dp = getParent().getSize();
			int nCol = Math.max((dp.width - H_GAP) / (w + H_GAP), 1);
			int nRow = n / nCol;
			if (nRow * nCol < n)
				nRow++;

			int index = 0;
			for (int k = 0; k < nRow; k++) {
				for (int m = 0; m < nCol; m++) {
					if (index >= n)
						return;
					comp = getComponent(index++);
					comp.setBounds(x, y, w, h);
					x += w + H_GAP;
				}
				y += h + V_GAP;
				x = ins.left + H_GAP;
			}
		}
	}

	/**
	 * <p>
	 * Title: Page Preview
	 * </p>
	 * 
	 * <p>
	 * Description: Preview of individual pages
	 * </p>
	 * 
	 * <p>
	 * Copyright: Copyright (c) 2005
	 * </p>
	 * 
	 * @author <a href=mailto:a.dorri@genesys.shef.ac.uk>Aria Dorri</a>
	 * @version 1.0
	 * 
	 * @see PrintPreview
	 */
	class PagePreview extends JPanel {
		/**
		 * Width
		 */
		protected int m_w;

		/**
		 * Hight
		 */
		protected int m_h;

		/**
		 * Source
		 */
		protected Image m_source;

		/**
		 * Image
		 */
		protected Image m_img;

		/**
		 * Initializes the instance of {@link PagePreview}
		 * 
		 * @param w
		 *            int
		 * @param h
		 *            int
		 * @param source
		 *            Image
		 */
		public PagePreview(int w, int h, Image source) {
			m_w = w;
			m_h = h;
			m_source = source;
			m_img = m_source.getScaledInstance(m_w, m_h, Image.SCALE_SMOOTH);
			m_img.flush();
			setBackground(Color.white);
			setBorder(new MatteBorder(1, 1, 2, 2, Color.black));
		}

		public void setScaledSize(int w, int h) {
			m_w = w;
			m_h = h;
			m_img = m_source.getScaledInstance(m_w, m_h, Image.SCALE_SMOOTH);
			repaint();
		}

		public Dimension getPreferredSize() {
			Insets ins = getInsets();
			return new Dimension(m_w + ins.left + ins.right, m_h + ins.top
					+ ins.bottom);
		}

		public Dimension getMaximumSize() {
			return getPreferredSize();
		}

		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		public void paint(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
			g.drawImage(m_img, 0, 0, this);
			paintBorder(g);
		}
	}
}

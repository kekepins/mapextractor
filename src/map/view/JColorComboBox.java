package map.view;

import java.awt.*;

import javax.swing.*;

@SuppressWarnings("serial")
public final class JColorComboBox extends JComboBox<Color> {
	/** Show the color with a colored line. */
	// public static final int LINE = 0;
	/** Show the color with a colored rectangle. */
	// public static final int RECT = 1;

	/**
	 * Create a color combo box of the specified type, offering the specified
	 * colors and color names.
	 * 
	 * @param type
	 *            one of {@link #LINE}, {@link #RECT} or {@link #TEXT_ONLY}.
	 * @param colors
	 *            the colors used to fill the combo box.
	 * @param colorNames
	 *            the color names to use.
	 */
	public JColorComboBox(Color[] colors) {

		if (colors == null) {
			colors = DEFAULT_COLORS;
		}
		init(new DefaultComboBoxModel<Color>(colors));
	}


	/**
	 * Set this combo box of the specified type. This will reset any color
	 * thickness, color width and item height settings.
	 * 
	 * @param type
	 *            one of {@link #LINE}, {@link #RECT} or {@link #TEXT_ONLY}.
	 */
	public void setType(int type) {
		if (type == this.type) {
			return;
		}
		this.type = type;
		thickness = DEFAULT_LINE_THICKNESS;
		height = DEFAULT_HEIGHT;
		width = DEFAULT_WIDTH;
		setRenderer(new ComboBoxRenderer(this, thickness, DEFAULT_HEIGHT,
				DEFAULT_WIDTH));
	}

	/**
	 * Returns the type of this combo box.
	 * 
	 * @return one of {@link #LINE}, {@link #RECT} or {@link #TEXT_ONLY}.
	 * @see #setType
	 */
	public int getType() {
		return type;
	}

	/**
	 * Set the thickness of the color line (if type is {@link #LINE}) or the
	 * height of the color rectangle (if type is {@link #RECT}). The line
	 * thickness can't be less than one, and the retangle height also has an
	 * inferior limit. Calling this function will reset the item height.
	 * 
	 * @param thickness
	 *            the line thickness or the rectangle height.
	 * @see #setItemHeight
	 */
	public void setColorThickness(int thickness) {
		if (thickness == this.thickness) {
			return;
		}
		if (thickness < 1) {
			thickness = 1;
		}
		int def_thickness = DEFAULT_LINE_THICKNESS;
		if (thickness < def_thickness) {
			thickness = def_thickness;
		}

		this.thickness = thickness;
		height = thickness + DEFAULT_MARGINS;
		if (height < DEFAULT_HEIGHT) {
			height = DEFAULT_HEIGHT;
		}
		setRenderer(new ComboBoxRenderer(this, thickness, height, width));
	}

	/**
	 * Returns the current color line thickness/rectangle height.
	 * 
	 * @return the current color line thickness/rectangle height.
	 * @see #setColorThickness
	 */
	public int getColorThickness() {
		return thickness;
	}

	/**
	 * Sets the height of the combo box list item. This is to leave more space
	 * between color lines/rectangles. The height can't be less than the current
	 * thickness.
	 * 
	 * @param height
	 *            the list item height
	 */
	public void setItemHeight(int height) {
		if (height == this.height) {
			return;
		}
		if (height < thickness) {
			height = thickness;
		}
		setRenderer(new ComboBoxRenderer(this, thickness, height, width));
	}

	/**
	 * Returns the current height of the combo box list item.
	 * 
	 * @return the list item height
	 * @see #setItemHeight
	 */
	public int getItemHeight() {
		return height;
	}

	/**
	 * Sets the width of the color line/rectangle. It can't be less than one.
	 * 
	 * @param width
	 *            the color line/rectangle width.
	 */
	public void setColorWidth(int width) {
		if (width == this.width) {
			return;
		}
		if (width < 1) {
			width = 1;
		}
		this.width = width;
		setRenderer(new ComboBoxRenderer(this, thickness, height, width));
	}

	/**
	 * Returns the width of the color line/rectangle.
	 * 
	 * @return the color line/rectangle width.
	 * @see #setColorWidth
	 */
	public int getColorWidth() {
		return width;
	}

	/**
	 * Sets the font to use for this combo box. This will change something only
	 * if text is enabled.
	 * 
	 * @param font
	 *            the font to use to render text
	 * @see #setShowText
	 */
	public void setFont(Font font) {
		super.setFont(font);
		setRenderer(new ComboBoxRenderer(this, thickness, height, width));
	}

	/**
	 * Returns the selected color.
	 * 
	 * @return the currently selected color
	 */
	public Color getSelectedColor() {
		// return ((Pair) getSelectedItem()).color;
		return (Color) getSelectedItem();
	}

	/**
	 * Select a color.
	 * 
	 * @param color
	 *            the color to select. If the color is not present in the color
	 *            list, this function has no effect
	 */
	public void setSelectedColor(Color color) {
		DefaultComboBoxModel<Color> model = (DefaultComboBoxModel<Color>) getModel();
		for (int i = 0; i < model.getSize(); ++i) {
			/*
			 * Pair p = (Pair) model.getElementAt(i); if (p.color.equals(color))
			 * { setSelectedIndex(i); break; }
			 */
			Color colorIdx = model.getElementAt(i);
			if (colorIdx.equals(color)) {
				setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * Select a color.
	 * 
	 * @param color
	 *            the color to select. If the color is not present in the color
	 *            list, this function has no effect
	 * @see #setSelectedColor
	 */
	public void setSelectedItem(Object color) {
		if (color instanceof Color) {
			super.setSelectedItem(color);
			return;
		}
		/*
		 * if (color instanceof Pair) { super.setSelectedItem(color); return; }
		 */
		// setSelectedColor((Color) color);
	}

	private static final int DEFAULT_LINE_THICKNESS = 2;
	private static final int DEFAULT_RECT_THICKNESS = 10;
	private static final int DEFAULT_HEIGHT = 16;
	private static final int DEFAULT_MARGINS = DEFAULT_HEIGHT
			- DEFAULT_RECT_THICKNESS;
	private static final int DEFAULT_WIDTH = 80;

	private static final Color[] DEFAULT_COLORS = { Color.black, Color.blue,
			Color.cyan, Color.darkGray, Color.gray, Color.green,
			Color.lightGray, Color.magenta, Color.orange, Color.pink,
			Color.red, Color.white, Color.yellow, };

	private int type;
	private int thickness;
	private int height;
	private int width;

	private void init(ComboBoxModel<Color> model) {
		setModel(model);
		setEditable(false);
		thickness = DEFAULT_LINE_THICKNESS;
		height = DEFAULT_HEIGHT;
		width = DEFAULT_WIDTH;
		setRenderer(new ComboBoxRenderer(this, thickness, DEFAULT_HEIGHT,
				DEFAULT_WIDTH));
	}

	private final class ComboBoxRenderer extends JPanel implements
			ListCellRenderer <Color>{
		private Color color;
		private int width;
		private int thickness;
		private int height;
		private int disp;

		JPanel textPanel;
		JLabel text;

		public ComboBoxRenderer(JComboBox<Color> combo, int thickness,
				int height, int width) {
			this.thickness = thickness;
			this.width = width;
			this.height = height;
			disp = (height / 2) - (thickness / 2);
			textPanel = new JPanel();
			textPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
			textPanel.add(this);
			text = new JLabel();
			text.setOpaque(true);
			text.setFont(combo.getFont());
			textPanel.add(text);
		}

		/*public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
			} else {
				setBackground(JColorComboBox.super.getBackground());
			}

			// color = ((Pair) value).color;
			color = ((Color) value);

			return this;
		}*/

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(color);
			g.fillRect(2, disp, getWidth() - 4, thickness);
		}

		public Dimension getPreferredSize() {
			return new Dimension(width, height);
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends Color> list, Color value, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
			} else {
				setBackground(JColorComboBox.super.getBackground());
			}

			// color = ((Pair) value).color;
			color =  value;

			return this;
		}
	}
}

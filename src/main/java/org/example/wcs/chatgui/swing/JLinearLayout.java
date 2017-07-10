/*
 * Copyright 2016-2017 Tom Misawa, riversun.org@gmail.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use, 
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the 
 * Software, and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package org.example.wcs.chatgui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * LinearLayout like component for Pure java GUI component like AWT/Swing.
 *
 * You can put the components of a pure java as Android's LinearLayout-like
 * style.
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
@SuppressWarnings("serial")
public class JLinearLayout extends Component {

	public enum Orientation {
		HORIZONTAL, VERTICAL,
	}

	private JPanel mBasePanel = new JPanel();

	private Orientation mOrientation = Orientation.VERTICAL;
	private List<ComponentHolder> mChildViewList = new ArrayList<ComponentHolder>();

	private class ComponentHolder {
		public Component component;
		public double weight;
		public Insets insets;
	}

	public JLinearLayout() {

	}

	@Override
	public void setBackground(Color c) {
		super.setBackground(c);
		mBasePanel.setBackground(c);

	}

	public JLinearLayout setChildOrientation(Orientation orientation) {
		this.mOrientation = orientation;
		return JLinearLayout.this;
	}

	/**
	 * Add view(component) to this LayoutGroup
	 * 
	 * @param component
	 * @return
	 */
	public JLinearLayout addView(Component component) {
		addView(component, 1.0d);
		return JLinearLayout.this;
	}

	/**
	 * Add view(component) to this LayoutGroup
	 * 
	 * @param component
	 * @param weight
	 * @return
	 */
	public JLinearLayout addView(Component component, double weight) {

		final ComponentHolder compontentHolder = new ComponentHolder();
		compontentHolder.component = component;
		compontentHolder.weight = weight;
		compontentHolder.insets = new Insets(paddingTop, paddingLeft, paddingBottom, paddingRight);

		mChildViewList.add(compontentHolder);

		return JLinearLayout.this;
	}

	private int paddingLeft = 0;
	private int paddingTop = 0;
	private int paddingBottom = 0;
	private int paddingRight = 0;

	public JLinearLayout setPadding(int padding) {
		paddingLeft = padding;
		paddingTop = padding;
		paddingRight = padding;
		paddingBottom = padding;
		return JLinearLayout.this;
	}

	public JLinearLayout setPadding(int left, int top, int right, int bottom) {
		paddingLeft = left;
		paddingTop = top;
		paddingRight = right;
		paddingBottom = bottom;
		return JLinearLayout.this;
	}

	/**
	 * Set visible
	 * 
	 * @param visible
	 * @return
	 */
	public JLinearLayout setVisibility(boolean visible) {
		mBasePanel.setVisible(visible);
		return JLinearLayout.this;
	}

	public JLinearLayout insertToFrame(JFrame frame) {
		frame.add(getAsPanel());
		return JLinearLayout.this;
	}

	/**
	 * Get this layout group as a JPanel
	 * 
	 * @return
	 */
	public JPanel getAsPanel() {

		final int countOfChildObject = mChildViewList.size();

		final GridBagLayout layout = new GridBagLayout();

		if (mOrientation == Orientation.VERTICAL) {
			mBasePanel.setLayout(layout);
		} else {
			mBasePanel.setLayout(layout);
		}

		for (int i = 0; i < countOfChildObject; i++) {

			ComponentHolder childComponentHolder = mChildViewList.get(i);
			final Component childComponent = childComponentHolder.component;
			final double childComponentWeight = childComponentHolder.weight;
			final Insets childComponentInsets = childComponentHolder.insets;

			final GridBagConstraints gbc = new GridBagConstraints();

			if (mOrientation == Orientation.VERTICAL) {
				gbc.gridx = 0;
				gbc.gridy = i;
				gbc.weightx = 1.0d;
				gbc.weighty = childComponentWeight;
			} else {
				gbc.gridx = i;
				gbc.gridy = 0;
				gbc.weightx = childComponentWeight;
				gbc.weighty = 1.0d;
			}

			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			if (childComponentInsets != null) {
				gbc.insets = childComponentInsets;
			}
			gbc.fill = GridBagConstraints.BOTH;

			if (!(childComponent instanceof JLinearLayout)) {
				// If child component is Swing or AWT component
				layout.setConstraints(childComponent, gbc);
				mBasePanel.add(childComponent);

			} else {
				// If child component is JLayoutGroup

				final JLinearLayout childLayoutGroup = (JLinearLayout) childComponent;

				gbc.gridwidth = 1;
				gbc.gridheight = 1;
				gbc.weightx = 1.0d;
				gbc.weighty = 1.0d;

				if (mOrientation == Orientation.VERTICAL) {
					gbc.weighty = childComponentWeight;// childLayoutGroup.mWeight;
				} else {
					gbc.weightx = childComponentWeight;// childLayoutGroup.mWeight;
				}

				// Set weight to the panel that becomes base of the panel
				layout.setConstraints(childLayoutGroup.mBasePanel, gbc);

				JPanel childPanel = childLayoutGroup.getAsPanel();

				mBasePanel.add(childPanel);
			}
		}
		return mBasePanel;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

}
/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.single;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.*;

import org.fbreader.book.Book;
import org.fbreader.extras.info.InfoUtil;
import org.fbreader.extras.selection.SelectionCursorUtil;
import org.fbreader.extras.selection.SelectionPanelUtil;
import org.fbreader.text.extras.opener.ExternalHyperlinkOpener;
import org.fbreader.text.extras.opener.InternalHyperlinkOpener;
import org.fbreader.text.view.SelectionData;
import org.fbreader.text.widget.TextWidget;

public class TextWidgetExt extends TextWidget {
	{
		this.registerOpener(new InternalHyperlinkOpener(this));
		this.registerOpener(new ExternalHyperlinkOpener(this));
	}

	public TextWidgetExt(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextWidgetExt(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextWidgetExt(Context context) {
		super(context);
	}

	@Override
	protected GestureListenerExt createGestureListener() {
		return new GestureListenerExt(this);
	}

	@Override
	protected TextBookControllerImpl createController(Book book) {
		return new TextBookControllerImpl(getContext(), book);
	}

	@Override
	public void drawSelectionCursor(Canvas canvas, Point pt, boolean startNotEnd) {
		SelectionCursorUtil.drawCursor(this, canvas, pt, startNotEnd);
	}
	@Override
	protected void showSelectionPanel() {
		final SelectionData data = this.selectionData();
		if (data != null) {
			SelectionPanelUtil.showPanel(
				this, data.rects, new SelectionPanelListener(this)
			);
		}
	}
	@Override
	protected void hideSelectionPanel() {
		SelectionPanelUtil.hidePanel(this);
	}

	@Override
	protected void showInfo(String text) {
		InfoUtil.showInfo(this, text, this.colorLevel);
	}
}

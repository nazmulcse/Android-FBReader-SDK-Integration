/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.single;

import android.graphics.Point;

import org.fbreader.text.extras.gesture.GestureListenerImpl;
import org.fbreader.util.PageIndex;

class GestureListenerExt extends GestureListenerImpl {
	GestureListenerExt(TextWidgetExt widget) {
		super(widget);
	}

	@Override
	protected boolean onFingerSingleTap(Point pt) {
		if (super.onFingerSingleTap(pt)) {
			return true;
		}

		if (pt.x <= this.widget().getWidth() / 3) {
			this.widget().startAnimatedScrolling(PageIndex.previous);
		} else if (pt.x >= 2 * this.widget().getWidth() / 3) {
			this.widget().startAnimatedScrolling(PageIndex.next);
		}
		return true;
	}
	@Override
	public boolean isDoubleTapSupported() {
		return false;
	}
}

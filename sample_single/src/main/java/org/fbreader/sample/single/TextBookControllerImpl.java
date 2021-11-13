/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.single;

import android.content.*;
import android.net.Uri;

import org.fbreader.book.Book;
import org.fbreader.config.ConfigInterface;
import org.fbreader.config.StringOption;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.Position;
import org.fbreader.text.extras.controller.AbstractTextBookController;
import org.fbreader.text.widget.TextWidget;

public class TextBookControllerImpl extends AbstractTextBookController {
	public TextBookControllerImpl(Context context, Book book) {
		super(context, book);
	}

	/* +++++++++++ CURRENT POSITION ++++++++++ */
	private StringOption positionOption() {
		return ConfigInterface.instance(this.applicationContext).stringOption(
			"book-position", String.valueOf(book.getId()), ""
		);
	}
	@Override
	public Position position() {
		try {
			final String[] split = this.positionOption().getValue().split(";");
			return new FixedPosition(
				Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2])
			);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public void storePosition(Position p) {
		if (p != null) {
			this.positionOption().setValue(
				p.getParagraphIndex() + ";" + p.getElementIndex() + ";" + p.getCharIndex()
			);
		}
	}
	/* ----------- CURRENT POSITION ---------- */
}

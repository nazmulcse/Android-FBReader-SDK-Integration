/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.single;

import android.app.Activity;
import android.app.SearchManager;
import android.content.*;
import android.view.View;
import android.widget.Toast;

import org.fbreader.book.Bookmark;
import org.fbreader.util.ViewUtil;

final class SelectionPanelListener implements View.OnClickListener {
	private final TextWidgetExt widget;

	public SelectionPanelListener(TextWidgetExt widget) {
		this.widget = widget;
	}

	public void onClick(View view) {
		final Activity activity = ViewUtil.findContainingActivity(this.widget);
		if (activity == null) {
			return;
		}

		final String text = this.widget.getSelectedText();
		final int id = view.getId();
		if (id == R.id.selection_panel_copy) {
			final ClipboardManager clipboard =
				(ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setPrimaryClip(ClipData.newPlainText("Sample reader", text));
		} else if (id == R.id.selection_panel_share) {
			final String title = this.widget.controller().book.getTitle();
			final Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, title);
			intent.putExtra(Intent.EXTRA_TEXT, text);
			try {
				activity.startActivity(Intent.createChooser(intent, null));
			} catch (ActivityNotFoundException e) {
				// TODO: show error message
			}
		} else if (id == R.id.selection_panel_translate) {
			Toast.makeText(
				this.widget.getContext(),
				"Dictionary integration not implemented",
				Toast.LENGTH_LONG
			).show();
		} else if (id == R.id.selection_panel_browse) {
			final Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, text);
			try {
				activity.startActivity(Intent.createChooser(intent, null));
			} catch (ActivityNotFoundException e) {
				// TODO: show error message
			}
		} else if (id == R.id.selection_panel_bookmark) {
			final Bookmark bookmark = this.widget.addBookmarkFromSelection();
			this.widget.openBookmark(bookmark);
		}
		this.widget.clearSelection();
	}
}

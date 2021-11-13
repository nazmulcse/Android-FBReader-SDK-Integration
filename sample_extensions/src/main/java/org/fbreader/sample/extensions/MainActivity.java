/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.extensions;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import org.fbreader.book.Book;
import org.fbreader.book.BookLoader;
import org.fbreader.format.BookException;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.view.style.BaseStyle;
import org.fbreader.text.widget.TextWidget;

public class MainActivity extends AppCompatActivity {
	private final int REQUEST_TABLE_OF_CONTENT = 1;

	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.main);
		FullScreenUtil.hideSystemUI(this);

		final TextWidget widget = findViewById(R.id.text_widget);
		final View errorView = findViewById(R.id.error_message);
		final View progressView = findViewById(R.id.loading_progress);

		new AsyncTask<Void,Void,Book>() {
			@Override
			protected void onPreExecute() {
				widget.setVisibility(View.VISIBLE);
				errorView.setVisibility(View.GONE);
				progressView.setVisibility(View.VISIBLE);
			}

			@Override
			protected Book doInBackground(Void ... params) {
				try {
					widget.setBook(BookLoader.fromFile("book.epub", MainActivity.this, 1L));
					return widget.controller().book;
				} catch (BookException e) {
					e.printStackTrace();
					return null;
				}
			}

			@Override
			protected void onPostExecute(Book book) {
				progressView.setVisibility(View.GONE);
				if (book != null) {
					widget.invalidate();
					widget.post(() -> MainActivity.this.setTitle(book.getTitle()));
				} else {
					errorView.setVisibility(View.VISIBLE);
				}
			}
		}.execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_TABLE_OF_CONTENT:
				if (resultCode == RESULT_OK) {
					final TextWidget widget = findViewById(R.id.text_widget);
					final int ref = data.getIntExtra(String.valueOf(TableOfContentsUtil.Key.reference), -1);
					if (widget != null && ref != -1) {
						widget.jumpTo(new FixedPosition(ref, 0, 0));
					}
				}
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		final TextWidget widget = findViewById(R.id.text_widget);

		final SearchView searchView = (SearchView)menu.findItem(R.id.menu_search).getActionView();
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
				widget.searchInText(query);
				menu.findItem(R.id.menu_search).collapseActionView();
				return true;
			}
		});

		menu.findItem(R.id.menu_table_of_contents).setEnabled(TableOfContentsUtil.isAvailable(widget));
		final String name = widget.colorProfile().name;
		menu.findItem(R.id.menu_color_profile_light).setChecked("defaultLight".equals(name));
		menu.findItem(R.id.menu_color_profile_dark).setChecked("defaultDark".equals(name));
		menu.findItem(R.id.menu_color_profile_dark_with_bg).setChecked("darkWithBg".equals(name));
		menu.findItem(R.id.menu_color_profile_pink).setChecked("pink".equals(name));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final TextWidget widget = findViewById(R.id.text_widget);
		final BaseStyle baseStyle = widget.baseStyle();

		switch (item.getItemId()) {
			case R.id.menu_table_of_contents:
			{
				final Intent intent = TableOfContentsUtil.intent(widget);
				if (intent != null) {
					startActivityForResult(intent, REQUEST_TABLE_OF_CONTENT);
				}
				break;
			}
			case R.id.menu_zoom_in:
				baseStyle.fontSize.setValue(baseStyle.fontSize.getValue() + 2);
				break;
			case R.id.menu_zoom_out:
				baseStyle.fontSize.setValue(baseStyle.fontSize.getValue() - 2);
				break;
			case R.id.menu_color_profile_light:
				widget.setColorProfileName("defaultLight");
				break;
			case R.id.menu_color_profile_dark:
				widget.setColorProfileName("defaultDark");
				break;
			case R.id.menu_color_profile_dark_with_bg:
				widget.setColorProfileName("darkWithBg");
				break;
			case R.id.menu_color_profile_pink:
				widget.setColorProfileName("pink");
				break;
		}
		widget.clearTextCaches();
		widget.invalidate();
		return true;
	}
}

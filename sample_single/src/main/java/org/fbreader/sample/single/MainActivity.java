/*
 * Copyright (C) 2004-2021 FBReader.ORG Limited <contact@fbreader.org>
 */

package org.fbreader.sample.single;

import android.app.AlertDialog;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import org.fbreader.book.Book;
import org.fbreader.book.BookLoader;
import org.fbreader.format.BookException;
import org.fbreader.image.StreamImage;
import org.fbreader.text.info.CoverHelper;
import org.fbreader.text.view.style.BaseStyle;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.util.ColorUtil;
import org.fbreader.view.options.ColorProfile;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.main);

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
				MainActivity.this.invalidateOptionsMenu();
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.app, menu);

		final TextWidget widget = findViewById(R.id.text_widget);
		final String bg = widget.colorProfile().wallpaper.getValue();
		menu.findItem(R.id.menu_background_paper).setChecked("wallpapers/paper.jpg".equals(bg));
		menu.findItem(R.id.menu_background_sepia).setChecked("wallpapers/sepia.jpg".equals(bg));
		menu.findItem(R.id.menu_background_solid_white).setChecked("".equals(bg));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final TextWidget widget = findViewById(R.id.text_widget);
		final BaseStyle baseStyle = widget.baseStyle();
		final ColorProfile profile = widget.colorProfile();

		switch (item.getItemId()) {
			case R.id.menu_zoom_in:
				baseStyle.fontSize.setValue(baseStyle.fontSize.getValue() + 2);
				break;
			case R.id.menu_zoom_out:
				baseStyle.fontSize.setValue(baseStyle.fontSize.getValue() - 2);
				break;
			case R.id.menu_background_paper:
				profile.wallpaper.setValue("wallpapers/paper.jpg");
				profile.regularText.setValue(ColorUtil.fromRgb(0, 0, 0));
				this.invalidateOptionsMenu();
				break;
			case R.id.menu_background_sepia:
				profile.wallpaper.setValue("wallpapers/sepia.jpg");
				profile.regularText.setValue(ColorUtil.fromRgb(0x5D, 0x40, 0x37));
				this.invalidateOptionsMenu();
				break;
			case R.id.menu_background_solid_white:
				profile.wallpaper.setValue("");
				profile.background.setValue(ColorUtil.fromRgb(0xF4, 0x8F, 0xB1));
				profile.regularText.setValue(ColorUtil.fromRgb(0x0D, 0x47, 0xA1));
				this.invalidateOptionsMenu();
				break;
			case R.id.menu_cover:
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				Bitmap bitmap = null;
				final StreamImage image = CoverHelper.instance(this).getCover(widget.controller().book);
				if (image != null) {
					try {
						bitmap = BitmapFactory.decodeStream(image.inputStream());
					} catch (Exception e) {
						// ignore
					}
				}
				if (bitmap != null) {
					final View view = getLayoutInflater().inflate(R.layout.cover, null, false);
					view.<ImageView>findViewById(R.id.dialog_cover).setImageBitmap(bitmap);
					builder.setView(view);
				} else {
					builder.setMessage("No cover found");
				}
				builder
					.setPositiveButton(android.R.string.ok, null)
					.create().show();
				break;
		}
		widget.clearTextCaches();
		widget.invalidate();
		return true;
	}
}

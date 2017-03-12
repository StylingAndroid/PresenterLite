package com.stylingandroid.presenter.lite;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.stylingandroid.presenter.engine.AppState;
import com.stylingandroid.presenter.engine.display.StandaloneDisplayActivity;

public class PresentationChooserActivity extends ListActivity {
    private ArrayAdapter<CharSequence> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser);
        adapter = ArrayAdapter.createFromResource(this, R.array.presentations, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AppState.getInstance().setPresentationName(adapter.getItem(position).toString());
        AppState.getInstance().setEvent(getString(R.string.event_long), getString(R.string.event_short), getString(R.string.event_hashtag));
        Intent intent = new Intent(this, StandaloneDisplayActivity.class);
        intent.putExtra(StandaloneDisplayActivity.EXTRA_MIRROR_DISPLAY, false);
        startActivity(intent);
        finish();

    }
}

package com.dexonlive.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import okhttp3.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String PLAYLIST_URL = "https://aynaott-auto-update-playlist.pages.dev/AynaOTT.m3u";
    private RecyclerView rvChannels;
    private ChipGroup chipGroup;
    private ChannelAdapter adapter;
    private List<Channel> allChannels = new ArrayList<>();
    private List<Channel> filteredChannels = new ArrayList<>();
    private String currentCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvChannels = findViewById(R.id.rv_channels);
        chipGroup = findViewById(R.id.chip_group);
        EditText etSearch = findViewById(R.id.et_search);
        ImageButton btnInfo = findViewById(R.id.btn_info);

        rvChannels.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ChannelAdapter(this, filteredChannels);
        rvChannels.setAdapter(adapter);

        btnInfo.setOnClickListener(v -> showAboutDialog());

        etSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { filter(); }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        loadPlaylist();
    }

    private void loadPlaylist() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(PLAYLIST_URL).build();

        Executors.newSingleThreadExecutor().execute(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String content = response.body().string();
                    allChannels = M3UParser.parse(content, PLAYLIST_URL);
                    runOnUiThread(this::setupUI);
                }
            } catch (IOException e) {
                runOnUiThread(() -> new AlertDialog.Builder(this).setMessage("Failed to load playlist. Check internet.").show());
            }
        });
    }

    private void setupUI() {
        filteredChannels.clear();
        filteredChannels.addAll(allChannels);
        adapter.updateList(filteredChannels);

        // Categories
        chipGroup.removeAllViews();
        Set<String> groups = new LinkedHashSet<>();
        groups.add("All");
        for (Channel ch : allChannels) groups.add(ch.getGroup());

        for (String g : groups) {
            Chip chip = new Chip(this);
            chip.setText(g);
            chip.setCheckable(true);
            chip.setChecked(g.equals("All"));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    currentCategory = g;
                    filter();
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void filter() {
        String query = ((EditText) findViewById(R.id.et_search)).getText().toString().trim().toLowerCase();
        List<Channel> result = new ArrayList<>();

        for (Channel ch : allChannels) {
            boolean matchCategory = currentCategory.equals("All") || ch.getGroup().equals(currentCategory);
            boolean matchSearch = query.isEmpty() || ch.getName().toLowerCase().contains(query);
            if (matchCategory && matchSearch) result.add(ch);
        }

        filteredChannels.clear();
        filteredChannels.addAll(result);
        adapter.updateList(filteredChannels);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About dexonlive")
                .setMessage(getString(R.string.about_text))
                .setPositiveButton("OK", null)
                .show();
    }
}

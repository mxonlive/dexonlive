package com.dexonlive.app;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UnstableApi
public class PlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;
    private Channel currentChannel;
    private List<Channel> relatedChannels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playerView = findViewById(R.id.player_view);
        TextView tvCurrent = findViewById(R.id.tv_current_channel);

        currentChannel = (Channel) getIntent().getSerializableExtra("channel");
        if (currentChannel == null) finish();

        tvCurrent.setText(currentChannel.getName());

        // Related (same category)
        // In real app you would pass full list, here we just show current for demo (you can extend)
        relatedChannels.add(currentChannel);

        setupPlayer();
    }

    private void setupPlayer() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", currentChannel.getReferer());
        headers.put("Origin", currentChannel.getReferer());
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        DefaultHttpDataSource.Factory httpFactory = new DefaultHttpDataSource.Factory()
                .setDefaultRequestProperties(headers)
                .setAllowCrossProtocolRedirects(true);

        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(httpFactory);

        player = new ExoPlayer.Builder(this)
                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(currentChannel.getUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}

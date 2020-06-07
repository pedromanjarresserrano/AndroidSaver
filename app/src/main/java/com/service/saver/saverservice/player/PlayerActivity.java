package com.service.saver.saverservice.player;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;


import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.folder.FileModelFragment;
import com.service.saver.saverservice.folder.model.FileModel;

import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private TrackSelector trackSelector;
    private TrackSelection.Factory trackSelectionFactory;
    private PlayerView simpleExoPlayerView;
    private List<FileModel> list;
    private LoadControl loadControl;
    private SimpleExoPlayer player;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        simpleExoPlayerView = findViewById(R.id.player_view);
        list = FileModelFragment.Companion.getFILE_MODEL_LIST();
        initButtons();
        loadControl = new DefaultLoadControl();
        String abrAlgorithm = intent.getStringExtra("abr_algorithm");
        if (abrAlgorithm == null || "default".equals(abrAlgorithm)) {
            trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        } else if ("random".equals(abrAlgorithm)) {
            trackSelectionFactory = new RandomTrackSelection.Factory();
        } else {
            finish();
            return;
        }

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), trackSelector, loadControl);
        simpleExoPlayerView.setPlayer(player);
        addListenerToPlayer();
        play(position);
    }

    private void initButtons() {
        ImageButton nextbutton = simpleExoPlayerView.findViewById(R.id.next_button_player);
        nextbutton.setEnabled(true);
        nextbutton.setOnClickListener((e) -> playnext(position + 1));

        ImageButton backbutton = simpleExoPlayerView.findViewById(R.id.back_button_player);
        backbutton.setEnabled(true);
        backbutton.setOnClickListener((e) -> playback(position - 1));

    }

    private void playnext(int position) {
        int lastitem = list.size() - 1;
        if (position > lastitem) {
            position = 0;
        }
        String filepath = list.get(position).getFilepath();
        if (!filepath.endsWith(".mp4")) {
            while (!filepath.endsWith(".mp4")) {
                position++;
                if (position < lastitem)
                    filepath = list.get(position).getFilepath();
                else
                    position = 0;
            }
        }
        this.position = position;
        play(position);
    }

    private void playback(int position) {
        int lastitem = list.size() - 1;
        if (position < 0) {
            position = lastitem;
        }
        String filepath = list.get(position).getFilepath();
        if (!filepath.endsWith(".mp4")) {
            while (!filepath.endsWith(".mp4")) {
                position--;
                if (position > -1)
                    filepath = list.get(position).getFilepath();
                else
                    position = list.size() - 1;
            }
        }
        this.position = position;
        play(position);
    }

    private void play(int position) {
        String filepath = list.get(position).getFilepath();
        player.prepare(new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)))).setExtractorsFactory(new DefaultExtractorsFactory()).createMediaSource(getURI(filepath)));
        player.setPlayWhenReady(true);

    }

    private Uri getURI(String url) {
        return Uri.parse(url);
    }

    private void addListenerToPlayer() {
        player.addListener(new Player.EventListener() {


            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    playnext(position + 1);
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

        });
    }

    @Override
    public void onBackPressed() {
        this.player.stop();
        super.onBackPressed();
    }
}

package com.enix.hoken.custom.media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.enix.hoken.R;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.custom.mainview.UserMainView;
import com.enix.hoken.custom.notification.MyNotification;
import com.enix.hoken.util.CommonUtil;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MyPlayer {
	// 播放对象
	private MediaPlayer myMediaPlayer;
	// 播放列表
	private List<String> myMusicList = new ArrayList<String>();
	// 当前播放歌曲的索引
	private int currentListItem = 0;
	// 音乐的路径
	private String music_path;
	private Button mPre;
	private Button mNext;
	private ToggleButton mPlayPause;
	private View mPlayerView;
	private boolean isFirstPlay = true;// 用来判断是否第一次播放
	private TextView mTitle;
	private MyNotification mNotification;

	public MyPlayer(Activity mActivity, View playerView) {
		myMediaPlayer = new MediaPlayer();
		music_path = CommonUtil.getMediaPath(mActivity);
		mPlayerView = playerView;
		findViewById();
		musicList();
		setListener();
	//	mNotification = new MyNotification(mContext, DesktopActivity.class);

	}

	private void setListener() {
		mPlayPause.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (isFirstPlay) {
						playMusic(myMusicList.get(currentListItem));

					} else {
						myMediaPlayer.start();
					}

				} else {
					myMediaPlayer.pause();
					// mNotification.cancel(1); //--->取消通知
				}
			}
		});
		mNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				nextMusic();
			}
		});
		mPre.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				lastMusic();
			}
		});
	}

	// 播放音乐
	private void playMusic(String path) {
		try {
			myMediaPlayer.reset();
			myMediaPlayer.setDataSource(path);
			myMediaPlayer.prepare();
			myMediaPlayer.start();
			myMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					nextMusic();
				}
			});
			if (isFirstPlay) {
				isFirstPlay = false;
				//播放音乐时在通知栏显示通知
//				mNotification.setmContent("点击进入音乐列表");
//				mNotification.setmTicker("我的伊顿:"+getTitle());
//				mNotification.setmTitle("正在播放:"+getTitle());
//				mNotification.startNotification();
			}

			mPlayPause.setChecked(true);
			setTitle();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 下一首
	void nextMusic() {
		if (++currentListItem >= myMusicList.size()) {
			currentListItem = 0;
			playMusic(myMusicList.get(currentListItem));
		} else {
			playMusic(myMusicList.get(currentListItem));
		}
	}

	// 上一首
	void lastMusic() {
		if (currentListItem != 0) {
			if (--currentListItem >= 0) {
				currentListItem = myMusicList.size();
				playMusic(myMusicList.get(currentListItem));
			} else {
				playMusic(myMusicList.get(currentListItem));
			}
		} else {
			playMusic(myMusicList.get(currentListItem));
		}
	}

	private void setTitle() {
		if (myMusicList != null && myMusicList.size() > 0) {
			mTitle.setText(CommonUtil.getFilenameFromFullPath(myMusicList
					.get(currentListItem)));
		} else {
			mTitle.setText("当前列表没有音乐文件");
		}
	}

	private String getTitle() {
		if (myMusicList != null && myMusicList.size() > 0) {
			return CommonUtil.getFilenameFromFullPath(myMusicList
					.get(currentListItem));
		} else {
			return "当前列表没有音乐文件";
		}
	}

	private void musicList() {
		myMusicList = CommonUtil.getFilesByPath(music_path,
				CommonUtil.FILETYPE_MUSIC);
		setTitle();
	}

	private void findViewById() {
		mNext = (Button) mPlayerView
				.findViewById(R.id.btn_head_view_player_next);
		mPre = (Button) mPlayerView
				.findViewById(R.id.btn_head_view_player_prev);
		mPlayPause = (ToggleButton) mPlayerView
				.findViewById(R.id.btn_head_view_player_pause_play);
		mTitle = (TextView) mPlayerView
				.findViewById(R.id.tv_head_view_player_title);

	}
}

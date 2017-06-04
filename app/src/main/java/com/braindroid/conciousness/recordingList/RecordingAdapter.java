package com.braindroid.conciousness.recordingList;

import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.ViewGroup;

import com.braindroid.nervecenter.kotlinModels.data.OnDiskRecording;
import com.braindroid.nervecenter.kotlinModels.playbackTools.ManagedMediaPlayerPool;
import com.braindroid.nervecenter.playbackTools.SeekingAudioController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.NO_ID;
import static android.support.v7.widget.RecyclerView.NO_POSITION;


public class RecordingAdapter extends RecyclerView.Adapter<RecordingListViewHolder>
    implements RecordingListViewHolder.OnClick {

    public interface OnRecordingItemClicked {
        void onClick(OnDiskRecording recording, int position);
        void onLongClick(OnDiskRecording recording, int position);
    }

    private final List<OnDiskRecording> recordings;
    private final LruCache<String, RecordingListViewModel> viewModelLruCache;
    private final ManagedMediaPlayerPool playerPool;

    private OnRecordingItemClicked onRecordingItemClicked;

    public RecordingAdapter(OnRecordingItemClicked onClick, ManagedMediaPlayerPool playerPool) {
        setHasStableIds(true);

        this.playerPool = playerPool;
        this.onRecordingItemClicked = onClick;

        recordings = new ArrayList<>();
        viewModelLruCache = new LruCache<>(50);
    }

    //region Adapter Implementation
    @Override
    public RecordingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecordingListViewHolder.create(parent, this, playerPool.fromPool());
    }

    @Override
    public void onBindViewHolder(RecordingListViewHolder holder, int position) {
        if(!isValidRecordingPosition(position)) {
            Timber.e("position invalid; bind drop [%s", position);
            return;
        }

        OnDiskRecording toBind = recordings.get(position);
        String id = toBind.getRecordingId();
        RecordingListViewModel listViewModel = viewModelLruCache.get(id);
        if(listViewModel == null) {
            listViewModel = RecordingTransformer.toViewModel(toBind);
            viewModelLruCache.put(id, listViewModel);
        }
        holder.bind(listViewModel, toBind);
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @Override
    public long getItemId(int position) {
        if(!isValidRecordingPosition(position)) {
            return NO_ID;
        }
        return recordings.get(position).getSystemMeta().hashCode();
    }
    //endregion

    public void setNewList(List<OnDiskRecording> newList) {
        recordings.clear();
        viewModelLruCache.evictAll();
        recordings.addAll(newList);

        notifyDataSetChanged();
    }

    public List<OnDiskRecording> getRecordings() {
        return Collections.unmodifiableList(recordings);
    }

    @Override
    public void onRecordingClicked(RecordingListViewModel viewModel, int position, boolean longPress, SeekingAudioController seekingAudioController) {
        if(onRecordingItemClicked == null) {
            Timber.w("Dropping onRecordingClick... %s={%s}", viewModel, position);
            return;
        }

        if(!viewModel.isPlayable()) {
            Timber.v("PersistedRecording is not playable %s={%s}", viewModel, position);
            return;
        }

        OnDiskRecording recording = recordings.get(position);
        if(longPress) {
            onRecordingItemClicked.onLongClick(recording, position);
        } else {
            onRecordingItemClicked.onClick(recording, position);
        }

    }

    private boolean isValidRecordingPosition(int position) {
        return position != NO_POSITION && position < recordings.size() && position >= 0;
    }
}

package com.braindroid.conciousness.recordingList;

import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.ViewGroup;

import com.braindroid.nervecenter.recordingTools.Recording;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.support.v7.widget.RecyclerView.NO_ID;
import static android.support.v7.widget.RecyclerView.NO_POSITION;


public class RecordingAdapter extends RecyclerView.Adapter<RecordingListViewHolder>
    implements RecordingListViewHolder.OnClick {

    public interface OnRecordingItemClicked {
        void onClick(Recording recording, int position);
    }

    private final List<Recording> recordings;
    private final LruCache<String, RecordingListViewModel> viewModelLruCache;

    private OnRecordingItemClicked onRecordingItemClicked;

    public RecordingAdapter(OnRecordingItemClicked onClick) {
        setHasStableIds(true);

        this.onRecordingItemClicked = onClick;

        recordings = new ArrayList<>();
        viewModelLruCache = new LruCache<>(50);
    }

    //region Adapter Implementation
    @Override
    public RecordingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecordingListViewHolder.create(parent, this);
    }

    @Override
    public void onBindViewHolder(RecordingListViewHolder holder, int position) {
        if(!isValidRecordingPosition(position)) {
            Timber.e("position invalid; bind drop [%s", position);
            return;
        }

        Recording toBind = recordings.get(position);
        RecordingListViewModel listViewModel = viewModelLruCache.get(toBind.identifier());
        if(listViewModel == null) {
            listViewModel = RecordingTransformer.toViewModel(toBind);
            viewModelLruCache.put(toBind.identifier(), listViewModel);
        }
        holder.bind(listViewModel);
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
        return recordings.get(position).identifier().hashCode();
    }
    //endregion

    public void setNewList(List<Recording> newList) {
        recordings.clear();
        viewModelLruCache.evictAll();
        recordings.addAll(newList);

        notifyDataSetChanged();
    }

    @Override
    public void onRecordingClicked(RecordingListViewModel viewModel, int position) {
        if(onRecordingItemClicked == null) {
            Timber.w("Dropping onRecordingClick... %s={%s}", viewModel, position);
            return;
        }

        if(!viewModel.isPlayable()) {
            Timber.v("Recording is not playable %s={%s}", viewModel, position);
            return;
        }

        onRecordingItemClicked.onClick(recordings.get(position), position);
    }

    private boolean isValidRecordingPosition(int position) {
        return position != NO_POSITION && position < recordings.size() && position >= 0;
    }
}

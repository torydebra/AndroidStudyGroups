package tori.studygroups.channels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;

import java.util.List;

import tori.studygroups.R;


public class ChannelListFragment extends Fragment {

    public static final String EXTRA_OPEN_CHANNEL_URL = "OPEN_CHANNEL_URL";
    private static final String LOG_TAG = ChannelListFragment.class.getSimpleName();

    private RecyclerView channelListRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private OpenChannelListAdapter mChannelListAdapter;

    private OpenChannelListQuery channelListQuery;

    public static ChannelListFragment newInstance() {
        ChannelListFragment fragment = new ChannelListFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_channel_list, container, false);

        setRetainInstance(true);

        setHasOptionsMenu(true);

        channelListRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_channel_list);
        mChannelListAdapter = new OpenChannelListAdapter(getContext());

//        mCreateChannelFab = (FloatingActionButton) rootView.findViewById(R.id.fab_open_channel_list);
//        mCreateChannelFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), CreateOpenChannelActivity.class);
//                startActivity(intent);
//            }
//        });

        setUpAdapter();
        setUpRecyclerView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh once
        refreshChannelList(15);

    }

    void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        channelListRecyclerView.setLayoutManager(mLayoutManager);
        channelListRecyclerView.setAdapter(mChannelListAdapter);
        channelListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        // If user scrolls to bottom of the list, loads more channels.
        channelListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mLayoutManager.findLastVisibleItemPosition() == mChannelListAdapter.getItemCount() - 1) {
                    loadNextChannelList();
                }
            }
        });
    }

    // Set touch listeners to RecyclerView items
    private void setUpAdapter() {
        mChannelListAdapter.setOnItemClickListener(new OpenChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OpenChannel channel) {
                String channelUrl = channel.getUrl();
                OpenChatFragment fragment = OpenChatFragment.newInstance(channelUrl);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container_channels_list, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mChannelListAdapter.setOnItemLongClickListener(new OpenChannelListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(OpenChannel channel) {
            }
        });
    }

    /**
     * Creates a new query to get the list of the user's Open Channels,
     * then replaces the existing dataset.
     *
     * @param numChannels   The number of channels to load.
     */
    void refreshChannelList(int numChannels) {
        channelListQuery= OpenChannel.createOpenChannelListQuery();
        channelListQuery.setLimit(numChannels);
        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {

                }
                mChannelListAdapter.setOpenChannelList(list);

            }
        });
    }

    /**
     * Loads the next channels from the current query instance.
     */
    void loadNextChannelList() {
        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                for (OpenChannel channel : list) {
                    mChannelListAdapter.addLast(channel);
                }
            }
        });
    }


}

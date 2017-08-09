package tori.studygroups.channels;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sendbird.android.OpenChannel;
import com.sendbird.android.OpenChannelListQuery;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.exams.ExamListFragment;
import tori.studygroups.mainActivities.MainActivity;


public class ChannelListFragment extends Fragment {

    public static final String EXTRA_CHANNEL_URL = "CHANNEL_URL";
    public static final String EXTRA_CHANNEL_NAME = "CHANNEL_NAME";
    private static final String LOG_TAG = ChannelListFragment.class.getSimpleName();

    private RecyclerView channelListRecyclerView;
    private LinearLayout linearLayoutNoChannel;
    private Button createGroupButton;
    private LinearLayout loadBar;
    private LinearLayoutManager mLayoutManager;
    private ChannelListAdapter mChannelListAdapter;
    private TextView noChannelFind;

    private ArrayList<String> userPrefChannelList;

    private String groupNameSearched;


    private EditText searchChannelEditText;

    private OpenChannelListQuery channelListQuery;

    public static ChannelListFragment newInstance() {
        ChannelListFragment fragment = new ChannelListFragment();
        return fragment;
    }

    //chiamata se devo vedere solo i channel preferiti
    public static ChannelListFragment newInstance(ArrayList<String> userPrefChannelList) {
        ChannelListFragment fragment = new ChannelListFragment();

        Bundle args = new Bundle();
        args.putStringArrayList(MainActivity.USER_PREF_CHANNEL_LIST, userPrefChannelList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            userPrefChannelList = getArguments().getStringArrayList(MainActivity.USER_PREF_CHANNEL_LIST);
        } catch (Exception e){
            userPrefChannelList = null;
        }

        Log.d("MAHHHH", "channllistFrag created");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("MAHHHH", "channllistFrag createdView");
        View rootView = inflater.inflate(R.layout.fragment_channel_list, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        channelListRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_channel_list);
        mChannelListAdapter = new ChannelListAdapter(getContext());
        linearLayoutNoChannel = (LinearLayout) rootView.findViewById(R.id.no_channel_find_container);
        createGroupButton = (Button) rootView.findViewById(R.id.btn_create_group);
        loadBar = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);
        linearLayoutNoChannel.setVisibility(View.GONE);
        noChannelFind = (TextView) rootView.findViewById(R.id.text_view_no_channel_find);

        searchChannelEditText = (EditText) rootView.findViewById(R.id.search_channel_editText);

        setUpAdapter();
        setUpRecyclerView();

        if (userPrefChannelList != null){
            searchChannelEditText.setVisibility(View.GONE);
        } else {
            setUpSearchBar();
        }

        setUpCreateButton();


        // Refresh once
        refreshChannelList(15);

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();

    }


    //in ritorno dal create chan activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {

            if (resultCode == CreateChannelActivity.CHANNEL_CREATED) {

                groupNameSearched = data.getStringExtra("channelName");
                Log.d("BOHH", data.getStringExtra("channelName"));
                channelListQuery = OpenChannel.createOpenChannelListQuery();
                channelListQuery.setLimit(15);
                channelListQuery.setNameKeyword(data.getStringExtra("channelName"));
                loadBar.setVisibility(View.VISIBLE);
                channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                    @Override
                    public void onResult(List<OpenChannel> channels, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            return;
                        }
                        Log.d("BOHH", Integer.toString(channels.size()));


                        mChannelListAdapter.setChannelList(channels);
                        loadBar.setVisibility(View.GONE);

                        if(channels.size() == 0){
                            channelListRecyclerView.setVisibility(View.GONE);
                            linearLayoutNoChannel.setVisibility(View.VISIBLE);

                        } else {
                            linearLayoutNoChannel.setVisibility(View.GONE);
                            channelListRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
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
        mChannelListAdapter.setOnItemClickListener(new ChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OpenChannel channel) {

                //hide soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                // check if no view has focus:
                View v = ((Activity) getContext()).getCurrentFocus();
                if (v != null) {
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                // finish: hide keyboard

                String channelUrl = channel.getUrl();
                String channelName = channel.getName();
                ChatFragment fragment = ChatFragment.newInstance(channelUrl, channelName);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container_channels_list, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        mChannelListAdapter.setOnItemLongClickListener(new ChannelListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongPress(OpenChannel channel) {
            }
        });
    }

    private void setUpSearchBar() {
        searchChannelEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                groupNameSearched = s.toString();

                channelListQuery = OpenChannel.createOpenChannelListQuery();
                channelListQuery.setLimit(15);

                channelListQuery.setNameKeyword(s.toString());

                loadBar.setVisibility(View.VISIBLE);

                channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                    @Override
                    public void onResult(List<OpenChannel> channels, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            return;
                        }

                        mChannelListAdapter.setChannelList(channels);
                        loadBar.setVisibility(View.GONE);

                        if(channels.size() == 0){
                            channelListRecyclerView.setVisibility(View.GONE);
                            linearLayoutNoChannel.setVisibility(View.VISIBLE);

                        } else {
                            linearLayoutNoChannel.setVisibility(View.GONE);
                            channelListRecyclerView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void setUpCreateButton() {
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentCreateChannel = new Intent(getActivity(), CreateChannelActivity.class);
                intentCreateChannel.putExtra("groupName", groupNameSearched);
                startActivityForResult(intentCreateChannel, 0);
            }
        });

    }


    /**
     * Creates a new query to get the list of the user's Channels,
     * then replaces the existing dataset.
     *
     * @param numChannels   The number of channels to load.
     */
    void refreshChannelList(int numChannels) {
        channelListQuery = OpenChannel.createOpenChannelListQuery();
        channelListQuery.setLimit(numChannels);
        loadBar.setVisibility(View.VISIBLE);

        //richiesta arriva dal main dove ho cliccato su vedi gruppi preferiti
        if (userPrefChannelList != null) {
            createGroupButton.setVisibility(View.GONE);
            if (userPrefChannelList.size() == 0){
                noChannelFind.setText("Nessun gruppo tra i preferiti. Puoi aggiungerne cercando tra i gruppi e cliccando sulla stella in alto");
                channelListRecyclerView.setVisibility(View.GONE);
                linearLayoutNoChannel.setVisibility(View.VISIBLE);

            } else {
                for (String prefChannel : userPrefChannelList) {
                    channelListQuery.setUrlKeyword(prefChannel);
                    channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                        @Override
                        public void onResult(List<OpenChannel> channels, SendBirdException e) {
                            if (e != null) {
                                return;
                            }

                            try {
                                mChannelListAdapter.addLast(channels.get(0));

                            } catch (Exception err){

                            }
                        }
                    });
                }
                channelListRecyclerView.setVisibility(View.VISIBLE);
                linearLayoutNoChannel.setVisibility(View.GONE);

            }

            loadBar.setVisibility(View.GONE);

        } else {

            channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
                @Override
                public void onResult(List<OpenChannel> list, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }
                    loadBar.setVisibility(View.GONE);
                    mChannelListAdapter.setChannelList(list);

                }
            });
        }
    }


    /**
     * Loads the next channels from the current query instance.
     */
    void loadNextChannelList() {
        loadBar.setVisibility(View.VISIBLE);

        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
            @Override
            public void onResult(List<OpenChannel> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                loadBar.setVisibility(View.GONE);
                for (OpenChannel channel : list) {
                    mChannelListAdapter.addLast(channel);
                }
            }
        });
    }


}

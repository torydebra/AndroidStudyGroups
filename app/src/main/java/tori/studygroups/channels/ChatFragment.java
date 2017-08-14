package tori.studygroups.channels;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import tori.studygroups.R;
import tori.studygroups.exams.ActivityExamList;
import tori.studygroups.otherClass.MyEvent;
import tori.studygroups.utils.FileUtils;
import tori.studygroups.utils.MediaPlayerActivity;
import tori.studygroups.utils.PhotoViewerActivity;

import static tori.studygroups.channels.ChannelListFragment.EXTRA_CHANNEL_NAME;


public class ChatFragment extends Fragment {

    private static final String LOG_TAG = ChatFragment.class.getSimpleName();

    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_CHAT";
    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_CHAT";

    private static final int INTENT_REQUEST_CHOOSE_IMAGE = 300;
    private static final int INTENT_REQUEST_ADD_EVENT = 500;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    private static final String CUSTOM_TYPE_MESSAGE_TEXT_NORMAL = "normal";
    public static final String CUSTOM_TYPE_MESSAGE_TEXT_EVENT = "event";

    static final String EXTRA_CHANNEL_URL = "CHANNEL_URL";
    private static final String CHAT_OPEN = "chat_open";

    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private View mRootLayout;
    private EditText mMessageEditText;
    private Button mMessageSendButton;
    private ImageButton mUploadFileButton;
    private LinearLayout loadingBarContainer;

    private boolean favouriteGroup;

    private FirebaseUser user;
    DatabaseReference dbRefUserPrefChannels;
    DatabaseReference dbRefChannelPrefUser;
    DatabaseReference dbRefUser;
    DatabaseReference dbRefChannelToDevice;
    List <String> userDeviceList;

    private OpenChannel mChannel;
    private String mChannelUrl;
    private String mChannelName;
    private PreviousMessageListQuery mPrevMessageListQuery;

    /**
     * To create an instance of this fragment, a Channel URL should be passed.
     */
    public static ChatFragment newInstance(@NonNull String channelUrl, String channelName) {
        ChatFragment fragment = new ChatFragment();

        Bundle args = new Bundle();
        args.putString(ChannelListFragment.EXTRA_CHANNEL_URL, channelUrl);
        args.putString(EXTRA_CHANNEL_NAME, channelName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("MAHHH", "chatFrag created");

        mChannelUrl = getArguments().getString(ChannelListFragment.EXTRA_CHANNEL_URL);
        mChannelName = getArguments().getString(EXTRA_CHANNEL_NAME);


        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        dbRefUserPrefChannels = FirebaseDatabase.getInstance().getReference("userPrefChannels");
        dbRefChannelPrefUser = FirebaseDatabase.getInstance().getReference("channelPrefUser");
        dbRefUser = FirebaseDatabase.getInstance().getReference("users");
        dbRefChannelToDevice = FirebaseDatabase.getInstance().getReference("channelToDevice");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("MAHHH", "chatFrag oncreateView");
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        mRootLayout = rootView.findViewById(R.id.layout_chat_root);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_channel_chat);

        loadingBarContainer = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgressMessage);

        checkFavouriteGroup();
        setUpChatAdapter();
        setUpRecyclerView();

        // Set up chat box
        mMessageSendButton = (Button) rootView.findViewById(R.id.button_channel_chat_send);
        mMessageEditText = (EditText) rootView.findViewById(R.id.edittext_chat_message);

        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! mMessageEditText.getText().toString().isEmpty()){
                    sendUserMessage(mMessageEditText.getText().toString(), null, CUSTOM_TYPE_MESSAGE_TEXT_NORMAL);
                    mMessageEditText.setText("");
                }

            }
        });

        mUploadFileButton = (ImageButton) rootView.findViewById(R.id.button_channel_chat_upload);
        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImage();
            }
        });

        enterChannel(mChannelUrl);

        return rootView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d("MAHHH", "chatFrag onactivitycreated");

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("MAH", "chatFrag started");


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MAHHH", "chatFrag resumed");

        // Set this as true to restart auto-background detection.
        // This means that you will be automatically disconnected from SendBird when your
        // app enters the background.
        SendBird.setAutoBackgroundDetection(true);

        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                Log.d("MAHHH", "OpenChatFragment onReconnectStarted()");
            }

            @Override
            public void onReconnectSucceeded() {
                Log.d("MAHHH", "OpenChatFragment onReconnectSucceeded()");
            }

            @Override
            public void onReconnectFailed() {
                Log.d("MAHHH", "OpenChatFragment onReconnectFailed()");
            }
        });

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                // Add new message to view
                if (baseChannel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.addFirst(baseMessage);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted.
//                    Snackbar.make(mRootLayout, "Storage permissions granted. You can now upload or download files.",
//                            Snackbar.LENGTH_LONG)
//                            .show();
                } else {
                    // Permission denied.
                    Snackbar.make(mRootLayout, "Devi dare i permessi di scrittura all'app",
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            showUploadConfirmDialog(data.getData());

        } else if (requestCode == INTENT_REQUEST_ADD_EVENT){
            //Log.d("MAHBOH", "entrato");
            if (data != null && resultCode == Activity.RESULT_OK){

                MyEvent eventCreated = data.getParcelableExtra("eventAdded");
                Log.d("MAHBOH", eventCreated.toJsonString());
                sendUserMessage("event", eventCreated.toJsonString(), CUSTOM_TYPE_MESSAGE_TEXT_EVENT);

                if ( data.getBooleanExtra("calendar", false) ){

                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventCreated.timestampDateEvent)
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventCreated.timestampDateEvent + 3*60*60*1000)
                            .putExtra(CalendarContract.Events.TITLE, eventCreated.name)
                            .putExtra(CalendarContract.Events.DESCRIPTION, "evento creato con l'app StudyGroups")
                            .putExtra(CalendarContract.Events.ORGANIZER, eventCreated.userName)
                            .putExtra(CalendarContract.Attendees.EVENT_ID, eventCreated.timestampDateEvent)
                            .putExtra(CalendarContract.Events.EVENT_LOCATION, eventCreated.location);
                    startActivity(intent);

                }

            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("MAHHH", "chatFrag paused");
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);

        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CHAT_OPEN, "true");
        editor.putString(EXTRA_CHANNEL_URL, mChannelName);
        editor.commit();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("MAHHH", "chatFrag ondestroyedView");

        mChannel.exit(new OpenChannel.OpenChannelExitHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("MAH", "menu created");

        if (favouriteGroup) {
            inflater.inflate(R.menu.menu_chat_fav, menu);
            super.onCreateOptionsMenu(menu, inflater);

        } else {
            inflater.inflate(R.menu.menu_chat, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        // Set action bar title to name of channel
        String channelNameCapitalized = mChannelName.substring(0, 1).toUpperCase() + mChannelName.substring(1);
        ((ChannelsActivity) getActivity()).setActionBarTitle(channelNameCapitalized);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.action_chat_view_participants:
                intent = new Intent(getActivity(), ChatPartecipantListActivity.class);
                intent.putExtra(EXTRA_CHANNEL_URL, mChannel.getUrl());
                startActivity(intent);
                return true;

            case R.id.action_chat_add_event:
                intent = new Intent(getActivity(), CreateEventActivity.class);
                intent.putExtra(EXTRA_CHANNEL_URL, mChannel.getUrl());
                intent.putExtra("channelName", mChannel.getName());
                startActivityForResult(intent, INTENT_REQUEST_ADD_EVENT);
                return true;

            case R.id.favourite_star:

                if (favouriteGroup){
                    removeFromFavourite(item);
                } else {
                    addToFavourite(item);
                }
                return true;

            case R.id.action_chat_view_events :
                intent = new Intent(getActivity(), EventsChannelListActivity.class);
                intent.putExtra(EXTRA_CHANNEL_URL, mChannel.getUrl());
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void addToFavourite(final MenuItem item) {

        new AlertDialog.Builder(getContext())
            .setTitle("Aggiungi preferito")
            .setMessage("Aggiungere questo gruppo ai preferiti?")
            .setIcon(R.drawable.ic_star_full)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    dbRefUser.child(user.getUid()).child("devices").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                //Log.d("MAH", snapshot.getKey());
                                dbRefChannelToDevice.child(mChannelUrl).child(snapshot.getKey()).setValue("true");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    dbRefUserPrefChannels.child(user.getUid()).child(mChannelUrl).setValue("true");
                    dbRefChannelPrefUser.child(mChannelUrl).child(user.getUid()).setValue("true");
                    item.setIcon(R.drawable.ic_star_full);
                    favouriteGroup = true;
                    Toast t = Toast.makeText(getContext(), "Aggiunto ai preferiti", Toast.LENGTH_LONG);
                    t.show();
                }
            })

            .setNegativeButton(android.R.string.no, null).show();
    }


    private void removeFromFavourite(final MenuItem item) {

        new AlertDialog.Builder(getContext())
                .setTitle("Rimuovi preferito")
                .setMessage("Rimuovere questo gruppo dai preferiti?")
                .setIcon(R.drawable.ic_star_empty)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        dbRefUser.child(user.getUid()).child("devices").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    //Log.d("MAHcancellando", snapshot.getKey());
                                    dbRefChannelToDevice.child(mChannelUrl).child(snapshot.getKey()).getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        dbRefUserPrefChannels.child(user.getUid()).child(mChannelUrl).getRef().removeValue();
                        dbRefChannelPrefUser.child(mChannelUrl).child(user.getUid()).getRef().removeValue();
                        item.setIcon(R.drawable.ic_star_empty);
                        favouriteGroup = false;
                        Toast t = Toast.makeText(getContext(), "Rimosso dai preferiti", Toast.LENGTH_LONG);
                        t.show();
                    }
                })

                .setNegativeButton(android.R.string.no, null).show();
    }


    private void checkFavouriteGroup() {

        dbRefUserPrefChannels.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favouriteGroup = dataSnapshot.hasChild(mChannelUrl);

                if (getActivity() != null){
                    ActivityCompat.invalidateOptionsMenu(getActivity());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setUpChatAdapter() {
        mChatAdapter = new ChatAdapter(getActivity());
        mChatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {

            @Override
            public void onUserMessageItemClick(UserMessage message) {
                if (message.getCustomType().equals(CUSTOM_TYPE_MESSAGE_TEXT_EVENT)){
                    onEventMessageClicked(message);
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message) {
                onFileMessageClicked(message);
            }

            @Override
            public void onAdminMessageItemClick(AdminMessage message) {
            }
        });


        mChatAdapter.setOnItemLongClickListener(new ChatAdapter.OnItemLongClickListener() {

            @Override
            public void onUserMessageLongClick(final UserMessage message) {
                int arrayRes;
                if (user.getUid().equals(message.getSender().getUserId())){
                    arrayRes = R.array.chat_message_long_clic_options;
                } else{
                    arrayRes = R.array.chat_message_long_clic_options_not_delete;
                }
                new AlertDialog.Builder(getActivity())
                    .setTitle("Opzioni")
                    .setIcon(R.drawable.ic_settings_orange)
                    .setItems(arrayRes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: //vedi userpage
                                    Intent intent = new Intent(getActivity(), ActivityExamList.class);
                                    intent.putExtra(ActivityExamList.USERID, message.getSender().getUserId());
                                    startActivity(intent);
                                    break;

                                case 1: //cancella messaggio
                                    new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.delete_message_question)
                                        .setNegativeButton(R.string.delete_message_cancel, null)
                                        .setPositiveButton(R.string.delete_message_confirmation, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteMessage(message);
                                            }
                                        })
                                        .create()
                                        .show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .create().show();

            }

            @Override
            public void onFileMessageLongClick(final FileMessage message) {
                int arrayRes;
                if (user.getUid().equals(message.getSender().getUserId())){
                    arrayRes = R.array.chat_message_long_clic_options;
                } else{
                    arrayRes = R.array.chat_message_long_clic_options_not_delete;
                }
                new AlertDialog.Builder(getActivity())
                    .setTitle("Opzioni")
                    .setItems(arrayRes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: //vedi userpage
                                    Intent intent = new Intent(getActivity(), ActivityExamList.class);
                                    intent.putExtra(ActivityExamList.USERID, message.getSender().getUserId());
                                    startActivity(intent);
                                    break;

                                case 1: //cancella messaggio
                                    new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.delete_message_question)
                                        .setNegativeButton(R.string.delete_message_cancel, null)
                                        .setPositiveButton(R.string.delete_message_confirmation, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteMessage(message);
                                            }
                                        })
                                        .create()
                                        .show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                .create().show();
            }

        });
    }


    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);

        // Load more messages when user reaches the top of the current message list.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                    loadNextMessageList(30);
                }
                Log.v(LOG_TAG, "onScrollStateChanged");
            }
        });
    }

    private void onEventMessageClicked(UserMessage message) {

        EventFragment fragment = EventFragment.newInstance(message.getData());
        getFragmentManager().beginTransaction()
                .replace(R.id.container_channels_list, fragment)
                .addToBackStack(null)
                .commit();

    }


    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.startsWith("image")) {
            Intent i = new Intent(getActivity(), PhotoViewerActivity.class);
            i.putExtra("url", message.getUrl());
            i.putExtra("type", message.getType());
            startActivity(i);
        } else if (type.startsWith("video")) {
            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
            intent.putExtra("url", message.getUrl());
            startActivity(intent);
        } else {
            showDownloadConfirmDialog(message);
        }
    }


    private void showDownloadConfirmDialog(final FileMessage message) {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Conferma download")
                    .setMessage("Vuoi scaricare il file?")
                    .setIcon(R.drawable.ic_file_download)
                    .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                FileUtils.downloadFile(getActivity(), message.getUrl(), message.getName());
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null).show();
        }

    }


    private void showUploadConfirmDialog(final Uri uri) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Conferma upload")
                .setMessage("Vuoi caricare il file?")
                .setIcon(R.drawable.ic_file_upload)
                .setPositiveButton(R.string.upload, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {

                            // Specify two dimensions of thumbnails to generate
                            List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
                            thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
                            thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

                            sendImageWithThumbnail(uri, thumbnailSizes);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null).show();
    }


    private void requestImage() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Alwaays show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_IMAGE);

            // Set this as false to maintain connection
            // even when an external Activity is started.
            SendBird.setAutoBackgroundDetection(false);
        }
    }


    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(mRootLayout, R.string.info_permission_storage,
                    Snackbar.LENGTH_LONG)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * Enters an Channel.
     * <p>
     * A user must successfully enter a channel before being able to load or send messages
     * within the channel.
     *
     * @param channelUrl The URL of the channel to enter.
     */
    private void enterChannel(String channelUrl) {
        OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
            @Override
            public void onResult(final OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                // Enter the channel
                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Error!
                            e.printStackTrace();
                            return;
                        }

                        mChannel = openChannel;
                        loadInitialMessageList(30);


                    }
                });
            }
        });
    }

    private void sendUserMessage(String text, String data, String custom_type) {

        mChannel.sendUserMessage(text, data, custom_type, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
//                    Toast.makeText(getActivity(),
//                            "Send failed with error " + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT)
//                            .show();
                    Toast.makeText(
                            getActivity(),
                            R.string.error_message_not_send , Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Display sent message to RecyclerView
                mChatAdapter.addFirst(userMessage);
            }
        });
    }

    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendImageWithThumbnail(Uri uri, List<FileMessage.ThumbnailSize> thumbnailSizes) {
        loadingBarContainer.setVisibility(View.VISIBLE);

        Hashtable<String, Object> info = FileUtils.getFileInfo(getActivity(), uri);
        final String path = (String) info.get("path");
        final File file = new File(path);
        final String name = file.getName();
        final String mime = (String) info.get("mime");
        final int size = (Integer) info.get("size");

        if (path.equals("")) {
            Toast.makeText(getActivity(), R.string.error_file_not_storage, Toast.LENGTH_LONG).show();
        } else {
            // Send image with thumbnails in the specified dimensions
            mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    if (e != null) {
                        //Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), R.string.error_file_not_send, Toast.LENGTH_SHORT).show();

                        return;
                    }

                    mChatAdapter.addFirst(fileMessage);
                    loadingBarContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Replaces current message list with new list.
     * Should be used only on initial load.
     */
    private void loadInitialMessageList(int numMessages) {

        mPrevMessageListQuery = mChannel.createPreviousMessageListQuery();
        mPrevMessageListQuery.load(numMessages, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                mChatAdapter.setMessageList(list);
            }
        });

    }

    /**
     * Loads messages and adds them to current message list.
     * <p>
     * A PreviousMessageListQuery must have been already initialized through {@link #loadInitialMessageList(int)}
     */
    private void loadNextMessageList(int numMessages) throws NullPointerException {

        if (mChannel == null) {
            throw new NullPointerException("Current channel instance is null.");
        }

        if (mPrevMessageListQuery == null) {
            throw new NullPointerException("Current query instance is null.");
        }

        mPrevMessageListQuery.load(numMessages, true, new PreviousMessageListQuery.MessageListQueryResult() {
            @Override
            public void onResult(List<BaseMessage> list, SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    return;
                }

                for (BaseMessage message : list) {
                    mChatAdapter.addLast((message));
                }
            }
        });
    }

    /**
     * Deletes a message within the channel.
     * Note that users can only delete messages sent by oneself.
     *
     * @param message The message to delete.
     */
    private void deleteMessage(final BaseMessage message) {
        mChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!

                    Toast.makeText(getActivity(),
                            R.string.error_deleting_message, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                loadInitialMessageList(30);
            }
        });
    }
}

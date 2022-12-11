package algonquin.cst2335.myapplication.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.myapplication.R;

import algonquin.cst2335.myapplication.data.ChatMessage;
import algonquin.cst2335.myapplication.data.ChatMessageDAO;
import algonquin.cst2335.myapplication.data.ChatRoomViewModel;
import algonquin.cst2335.myapplication.data.MessageDatabase;
import algonquin.cst2335.myapplication.databinding.ActivityChatRoomBinding;

public class ChatRoom extends AppCompatActivity {
    private @NonNull
    ActivityChatRoomBinding binding;
    ArrayList<ChatMessage> messages;
    private RecyclerView.Adapter myAdapter;
    ChatRoomViewModel chatModel;
    ChatMessageDAO mDAO;
    int position;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())
        {
            case R.id.item_1:
                TextView messageText;
                messageText = findViewById(R.id.message);
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete this message: "+messageText);
                builder.setTitle("Question:");
                builder.setPositiveButton("Yes",(dialog, cl)->{
                    ChatMessage m=messages.get(position);
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() ->
                    {
                        mDAO.deleteMessage(m);
                    });

                    messages.remove(position);
                    myAdapter.notifyItemRemoved(position);

                    Snackbar.make(messageText, "You deleted message #"+position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", undo->{

                                thread.execute(() ->
                                {
                                    mDAO.insertMessage(m);
                                });
                                messages.add(position,m);
                                myAdapter.notifyItemInserted(position);

                            }).show();
                });
                builder.setNegativeButton("No",(dialog, cl)->{});
                builder.create().show();
            break;
            case R.id.version:
                Toast.makeText(this, "This is version 2.4.1", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        RecyclerView recycleView=binding.recycleView;
        Button sendButton=binding.sendButton;
        Button receiveButton=binding.receiveButton;
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        messages = chatModel.messages.getValue();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
        String currentDateandTime = sdf.format(new Date());

        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDAO = db.cmDAO();
        if(messages == null)
        {
            //chatModel.messages.postValue( messages = new ArrayList<ChatMessage>());
            chatModel.messages.setValue(messages = new ArrayList<>());

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll((Collection<? extends ChatMessage>) mDAO.getAllMessages()); //Once you get the data from database
                binding.recycleView.setAdapter( myAdapter ); //You can then load the RecyclerView
            });
        }




        binding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if(viewType==1) {
                    View root = getLayoutInflater().inflate(R.layout.receive_message, parent, false);
                    return new MyRowHolder(root);
                }
                else{
                    View root = getLayoutInflater().inflate(R.layout.sent_message, parent, false);
                    return new MyRowHolder(root);
                }
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                ChatMessage obj = messages.get(position);
                holder.messageText.setText(obj.getMessage());
                holder.timeText.setText(obj.getTimeSent());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

            public int getItemViewType(int position){
                if(messages.get(position).isSentButton==true)
                    return 0;
                else return 1;
            }
        });
        chatModel.selectedMessage.observe(this, (newMessageValue) -> {
            //FrameLayout fl = findViewById(R.id.fragmentLocation);
            //fl.removeAllViews();
            MessageDetailsFragment chatFragment = new MessageDetailsFragment( newMessageValue );
            FragmentManager fMgr = getSupportFragmentManager();
            FragmentTransaction tx = fMgr.beginTransaction();
            tx.replace(R.id.fragmentLocation, chatFragment);
            tx.commit();
            /*getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentLocation, chatFragment)
                    .commit();*/

        });
        sendButton.setOnClickListener(click->{
            //ChatMessage.id++;
            ChatMessage sm=new ChatMessage(binding.textInput.getText().toString(),currentDateandTime,true);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                mDAO.insertMessage(sm);
            });
            messages.add(sm);
            myAdapter.notifyItemChanged(messages.size()-1);
            binding.textInput.setText("");
        });
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));

        receiveButton.setOnClickListener(click->{
            //ChatMessage.id++;
            ChatMessage rm=new ChatMessage(binding.textInput.getText().toString(),currentDateandTime,false);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                mDAO.insertMessage(rm);
            });
            messages.add(rm);
            myAdapter.notifyItemChanged(messages.size()-1);
            binding.textInput.setText("");
        });
    }
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(clk ->{
               /*int position=getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete this message: "+messageText.getText());
                builder.setTitle("Question:");
                builder.setPositiveButton("Yes",(dialog, cl)->{
                    ChatMessage m=messages.get(position);
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() ->
                    {
                        mDAO.deleteMessage(m);
                    });

                    messages.remove(position);
                    myAdapter.notifyItemRemoved(position);
                    Snackbar.make(messageText, "You deleted message #"+position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", undo->{

                                thread.execute(() ->
                                {
                                    mDAO.insertMessage(m);
                                });
                                messages.add(position,m);
                                myAdapter.notifyItemInserted(position);

                            }).show();
                });
                builder.setNegativeButton("No",(dialog, cl)->{});
                builder.create().show();*/
                position = getAbsoluteAdapterPosition();
                ChatMessage selected = messages.get(position);

                chatModel.selectedMessage.postValue(selected);
            });

            messageText = itemView.findViewById (R.id.message) ;
            timeText= itemView.findViewById(R.id.time);

        }
    }


}

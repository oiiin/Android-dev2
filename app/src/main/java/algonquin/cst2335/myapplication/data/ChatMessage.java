package algonquin.cst2335.myapplication.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage{

    @ColumnInfo(name="message")
    public String message;
    @ColumnInfo(name="timeSent")
    public String timeSent;
    @ColumnInfo(name="isSentButton")
    public boolean isSentButton;
    @PrimaryKey(autoGenerate = true)
    public int id;

    public ChatMessage(){}
    public ChatMessage(String m, String t, boolean sent)
    {

        message = m;
        timeSent = t;
        isSentButton = sent;
    }
    public String getMessage(){
        return message;
    }
    public String getTimeSent(){
        return timeSent;
    }
    public boolean isSentButton(){
        return isSentButton;
    }
}

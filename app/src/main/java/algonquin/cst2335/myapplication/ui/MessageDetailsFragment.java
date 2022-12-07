package algonquin.cst2335.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import algonquin.cst2335.myapplication.data.ChatMessage;
import algonquin.cst2335.myapplication.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {
    ChatMessage selected;
    public MessageDetailsFragment(ChatMessage m){
        selected=m;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        DetailsLayoutBinding binding=DetailsLayoutBinding.inflate(inflater);
        binding.messageDetail.setText(selected.message);
        binding.timeDetail.setText(selected.timeSent);
        binding.dataID.setText("Id= "+selected.id);
        return binding.getRoot();
    }
}

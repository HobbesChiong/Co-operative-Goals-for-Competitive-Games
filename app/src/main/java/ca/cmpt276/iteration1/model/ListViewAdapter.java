package ca.cmpt276.iteration1.model;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.iteration1.R;
import ca.cmpt276.iteration1.activities.NewGameCreationScreen;

/*
* Code inspiration by website on 13 Nov, 2022 from https://velmurugan-murugesan.medium.com/edittext-in-listview-android-example-41064bae2841
* */

public class ListViewAdapter extends BaseAdapter {

    private final Context context;
    private List playerScore;
    private LayoutInflater layoutInflater;
    GameManager gm = GameManager.getInstance();
    NewGameCreationScreen temp;

    public ListViewAdapter(Context context, List playerScore) {
        this.context = context;
        this.playerScore = playerScore;
    }

    @Override
    public int getCount() {
        return playerScore.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewContainer container;
        if(convertView == null){
            container = new ViewContainer();
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.user_input_score, null);
            container.inputScore = convertView.findViewById(R.id.etUserScoreInput);
            container.playerNumberDisplay = convertView.findViewById(R.id.tvPlayerIScore);
            container.playerNumberDisplay.setText(context.getString(R.string.player_i_score, position+1));
            container.inputScore.setTag(position);
            convertView.setTag(container);
        }
        else{
            container = (ViewContainer) convertView.getTag();
        }
        int tag = (Integer) container.inputScore.getTag();
        container.inputScore.setId(tag);

        container.inputScore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final int playerNo = container.inputScore.getId();
                final EditText score = container.inputScore;
                if(!score.getText().toString().equals(context.getString(R.string.blank))){
                    playerScore.set(playerNo, Integer.parseInt(score.getText().toString()));
                }
            }
        });
        return convertView;
    }
}
class ViewContainer {
    TextView playerNumberDisplay;
    EditText inputScore;
}

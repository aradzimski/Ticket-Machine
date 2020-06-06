package com.example.ticket_machine.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ticket_machine.R;
import com.example.ticket_machine.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayAdapter class allows operation only on the string ArrayList,
 * so we extends the following class for use on list of user objects
 */
public class UserAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private List<User> usersList;

    /**
     * Class constructor in which we give basic information when creating an object
     * @param context
     * @param list
     */
    public UserAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<User> list) {
        super(context, 0 , list);
        mContext = context;
        usersList = list;
    }

    /**
     * Below method allow as to create an item of list view which will have Name, LastName and Email
     * Inside method we are getting TextView elements from list_item resources and fill them with data from specific users
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.accounts_list_item,parent,false);

        User currentUser = usersList.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.text_view_name_account);
        name.setText(currentUser.getName());

        TextView lastName = (TextView) listItem.findViewById(R.id.text_view_last_name_account);
        lastName.setText(currentUser.getLastName());

        TextView email = (TextView) listItem.findViewById(R.id.text_view_email_account);
        email.setText(currentUser.getEmail());

        return listItem;
    }
}

//package com.example.chatting2;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class CustomFirestoreRecyclerAdapter extends FirestoreRecyclerAdapter<MessageItem, MyViewHolder> {
//    private static final int VIEW_TYPE_MY_MESSAGE = 0;
//    private static final int VIEW_TYPE_OTHER_MESSAGE = 1;
//
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//    /**
//     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
//     * FirestoreRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
//    public CustomFirestoreRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MessageItem> options) {
//        super(options);
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View itemView;
//
//        if(viewType == VIEW_TYPE_MY_MESSAGE) {
//            itemView = inflater.inflate(R.layout.my_messagebox, parent, false);
//            return new MyViewHolder(itemView);
//        }
//        else if (viewType == VIEW_TYPE_OTHER_MESSAGE) {
//            itemView = inflater.inflate(R.layout.other_messagebox, parent, false);
//            return new MyViewHolder(itemView);
//        }
//
//        itemView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MessageItem model) {
////        holder.tvName.setText(model.getName());
////        holder.tvMsg.setText(model.getMessage());
////        holder.tvTime.setText(model.getTime().toString());
//        holder.bind(model);
//
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if(getItem(position).getUid().equals(user.getUid()))
//            return VIEW_TYPE_MY_MESSAGE;
//        else
//            return VIEW_TYPE_OTHER_MESSAGE;
//    }
//
//    @Override
//    public int getItemCount() {
//        return super.getItemCount();
//    }
//
//
//}

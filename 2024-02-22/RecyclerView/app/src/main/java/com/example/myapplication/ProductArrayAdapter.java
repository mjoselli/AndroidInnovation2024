package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductArrayAdapter extends
        RecyclerView.Adapter<ProductArrayAdapter.ViewHolder> {
    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }
    private ClickListener clickListener;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView1;
        TextView textView2;
        public ViewHolder(View itemView){
            super(itemView);
            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);
            itemView.setOnClickListener(view -> {
                if(clickListener != null){
                    clickListener.onItemClick(view,getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(view -> {
                if(clickListener != null){
                    clickListener.onItemLongClick(view,getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = Singleton.getInstance().products.get(position);
        holder.textView1.setText(p.name);
        holder.textView2.setText(String.valueOf(p.quantity));
    }

    @Override
    public int getItemCount() {
        return Singleton.getInstance().products.size();
    }
}

package com.example.contactproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactActionListener {
        void onContactClick(Contact contact);
        void onEdit(Contact contact);
        void onDelete(Contact contact);
    }

    private List<Contact> contactList;
    private Context context;
    private OnContactActionListener listener;

    public ContactAdapter(Context context, List<Contact> contactList, OnContactActionListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    public void updateList(List<Contact> newList) {
        contactList.clear();
        contactList.addAll(newList);
        notifyDataSetChanged();

    }


    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textNom.setText(contact.getName());
        holder.textTel.setText(contact.getPhone());
        holder.itemView.setOnClickListener(v -> listener.onContactClick(contact));

        holder.iconEdit.setOnClickListener(v -> listener.onEdit(contact));
        holder.iconDelete.setOnClickListener(v -> listener.onDelete(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textNom, textTel;
        ImageView iconEdit, iconDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textNom = itemView.findViewById(R.id.text_nom);
            textTel = itemView.findViewById(R.id.text_tel);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            iconDelete = itemView.findViewById(R.id.icon_delete);
        }
    }
}

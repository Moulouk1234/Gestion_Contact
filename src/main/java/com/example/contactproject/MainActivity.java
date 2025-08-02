package com.example.contactproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
      private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private DatabaseHelper dbHelper;
    private ImageView addContact;
    private EditText searchField;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupAddContactListener();
        setupSearchListener();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        addContact = findViewById(R.id.add_icon);
        searchField = findViewById(R.id.edit_search);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupRecyclerView() {
        contactList = dbHelper.listerContacts();
        adapter = new ContactAdapter(this, contactList, new ContactAdapter.OnContactActionListener() {
            @Override
            public void onContactClick(Contact contact) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + contact.getPhone()));
                startActivity(intent);
            }

            @Override
            public void onEdit(Contact contact) {
                showEditContactDialog(contact);
            }

            @Override
            public void onDelete(Contact contact) {
                View dialogView = getLayoutInflater().inflate(R.layout.activity_delete_contact, null);

              Button cancelButton = dialogView.findViewById(R.id.cancel_button);
                Button deleteButton = dialogView.findViewById(R.id.delete_button);



                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();

                cancelButton.setOnClickListener(v -> dialog.dismiss());

                deleteButton.setOnClickListener(v -> {
                    dbHelper.supprimerContact(contact.getId());
                    refresh();
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Contact supprimé avec succès", Toast.LENGTH_SHORT).show();
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                dialog.show();
            }

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupAddContactListener() {
        addContact.setOnClickListener(view -> showAddContactDialog());
    }

    private void setupSearchListener() {
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterContacts(String searchText) {
        List<Contact> filteredList;
        if (searchText.isEmpty()) {
            filteredList = dbHelper.listerContacts();
        } else {
            filteredList = dbHelper.filterContactsByName(searchText);
        }
        adapter.updateList(filteredList);
    }

    private void showAddContactDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_add_contact, null);

        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editPhone = dialogView.findViewById(R.id.edit_phone);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                if (phone.length() == 8) {
                    boolean nameExists = false;
                    List<Contact> allContacts = dbHelper.listerContacts();
                    for (Contact contact : allContacts) {
                        if (contact.getName().equalsIgnoreCase(name)) {
                            nameExists = true;
                            break;
                        }
                    }

                    if (!nameExists) {
                        dbHelper.ajouterContact(name, phone);
                        Toast.makeText(MainActivity.this, "Contact ajouté avec succès", Toast.LENGTH_SHORT).show();
                        refresh();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Ce nom existe déjà", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Numéro de téléphone invalide (8 chiffres requis)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    public void refresh() {
        searchField.setText("");
        contactList.clear();
        contactList.addAll(dbHelper.listerContacts());
        adapter.notifyDataSetChanged();
    }

    private void showEditContactDialog(Contact contact) {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_add_contact, null);
        TextView txtcontenu = dialogView.findViewById(R.id.contenu);
        txtcontenu.setText("Modifier Contact");
        EditText editName = dialogView.findViewById(R.id.edit_name);
        EditText editPhone = dialogView.findViewById(R.id.edit_phone);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        editName.setText(contact.getName());
        editPhone.setText(contact.getPhone());
        btnAdd.setText("Modifier");

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty()) {
                if (phone.length() == 8 && phone.matches("\\d+")) {
                    boolean updated = dbHelper.modifierContact(contact.getId(), name, phone);
                    if (updated) {
                        Toast.makeText(MainActivity.this, "Contact modifié avec succès", Toast.LENGTH_SHORT).show();
                        refresh();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, "Erreur de modification", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Numéro invalide (8 chiffres)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}
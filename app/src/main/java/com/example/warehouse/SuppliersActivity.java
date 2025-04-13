package com.example.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.UUID;

public class SuppliersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvSuppliers;
    private FloatingActionButton fabAdd;
    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etInn;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingSupplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_suppliers);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvSuppliers = findViewById(R.id.rv_suppliers);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Поставщики");

        adapter = new SimpleAdapter(Utils.TYPE_SUPPLIER, item -> {
            Intent intent = new Intent(SuppliersActivity.this, SuppliersActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Supplier) item).getId());
            startActivity(intent);
        });
        rvSuppliers.setLayoutManager(new LinearLayoutManager(this));
        rvSuppliers.setAdapter(adapter);
        adapter.setData(Storage.suppliers);
        tvNoData.setVisibility(Storage.suppliers.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.suppliers.remove(position);
                adapter.setData(Storage.suppliers);
                tvNoData.setVisibility(Storage.suppliers.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(SuppliersActivity.this, "Поставщик удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvSuppliers);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, SuppliersActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Supplier> filtered = new ArrayList<>();
                for (Supplier s : Storage.suppliers) {
                    if (s.getName().toLowerCase().contains(newText.toLowerCase()) ||
                            s.getInn().toLowerCase().contains(newText.toLowerCase()) ||
                            s.getPhone().toLowerCase().contains(newText.toLowerCase()) ||
                            s.getEmail().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(s);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String supplierId) {
        editingSupplierId = supplierId;
        setContentView(R.layout.activity_suppliers_add);

        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etInn = findViewById(R.id.et_inn);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(supplierId == null ? "Добавить поставщика" : "Редактировать поставщика");

        if (supplierId != null) {
            for (Supplier s : Storage.suppliers) {
                if (s.getId().equals(supplierId)) {
                    etName.setText(s.getName());
                    etPhone.setText(s.getPhone());
                    etEmail.setText(s.getEmail());
                    etInn.setText(s.getInn());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String inn = etInn.getText().toString().trim();

            if (name.isEmpty() || inn.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingSupplierId == null) {
                Supplier newSupplier = new Supplier(UUID.randomUUID().toString(), name, phone, email, inn);
                Storage.suppliers.add(newSupplier);
            } else {
                for (Supplier s : Storage.suppliers) {
                    if (s.getId().equals(editingSupplierId)) {
                        Storage.suppliers.remove(s);
                        Storage.suppliers.add(new Supplier(editingSupplierId, name, phone, email, inn));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Поставщик сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
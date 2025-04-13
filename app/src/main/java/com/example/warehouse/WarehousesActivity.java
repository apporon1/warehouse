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

public class WarehousesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvWarehouses;
    private FloatingActionButton fabAdd;
    private EditText etName;
    private EditText etAddress;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingWarehouseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_warehouses);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvWarehouses = findViewById(R.id.rv_warehouses);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Склады");

        adapter = new SimpleAdapter(Utils.TYPE_WAREHOUSE, item -> {
            Intent intent = new Intent(WarehousesActivity.this, WarehousesActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Warehouse) item).getId());
            startActivity(intent);
        });
        rvWarehouses.setLayoutManager(new LinearLayoutManager(this));
        rvWarehouses.setAdapter(adapter);
        adapter.setData(Storage.warehouses);
        tvNoData.setVisibility(Storage.warehouses.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.warehouses.remove(position);
                adapter.setData(Storage.warehouses);
                tvNoData.setVisibility(Storage.warehouses.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(WarehousesActivity.this, "Склад удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvWarehouses);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, WarehousesActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Warehouse> filtered = new ArrayList<>();
                for (Warehouse w : Storage.warehouses) {
                    if (w.getName().toLowerCase().contains(newText.toLowerCase()) ||
                            w.getAddress().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(w);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String warehouseId) {
        editingWarehouseId = warehouseId;
        setContentView(R.layout.activity_warehouses_add);

        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(warehouseId == null ? "Добавить склад" : "Редактировать склад");

        if (warehouseId != null) {
            for (Warehouse w : Storage.warehouses) {
                if (w.getId().equals(warehouseId)) {
                    etName.setText(w.getName());
                    etAddress.setText(w.getAddress());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingWarehouseId == null) {
                Warehouse newWarehouse = new Warehouse(UUID.randomUUID().toString(), name, address);
                Storage.warehouses.add(newWarehouse);
            } else {
                for (Warehouse w : Storage.warehouses) {
                    if (w.getId().equals(editingWarehouseId)) {
                        Storage.warehouses.remove(w);
                        Storage.warehouses.add(new Warehouse(editingWarehouseId, name, address));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Склад сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
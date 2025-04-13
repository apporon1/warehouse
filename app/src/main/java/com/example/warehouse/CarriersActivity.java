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

public class CarriersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvCarriers;
    private FloatingActionButton fabAdd;
    private EditText etName;
    private EditText etPhone;
    private EditText etLicense;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingCarrierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_carriers);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvCarriers = findViewById(R.id.rv_carriers);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Перевозчики");

        adapter = new SimpleAdapter(Utils.TYPE_CARRIER, item -> {
            Intent intent = new Intent(CarriersActivity.this, CarriersActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Carrier) item).getId());
            startActivity(intent);
        });
        rvCarriers.setLayoutManager(new LinearLayoutManager(this));
        rvCarriers.setAdapter(adapter);
        adapter.setData(Storage.carriers);
        tvNoData.setVisibility(Storage.carriers.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.carriers.remove(position);
                adapter.setData(Storage.carriers);
                tvNoData.setVisibility(Storage.carriers.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(CarriersActivity.this, "Перевозчик удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvCarriers);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CarriersActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Carrier> filtered = new ArrayList<>();
                for (Carrier c : Storage.carriers) {
                    if (c.getName().toLowerCase().contains(newText.toLowerCase()) ||
                            c.getLicense().toLowerCase().contains(newText.toLowerCase()) ||
                            c.getPhone().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(c);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String carrierId) {
        editingCarrierId = carrierId;
        setContentView(R.layout.activity_carriers_add);

        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etLicense = findViewById(R.id.et_license);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(carrierId == null ? "Добавить перевозчика" : "Редактировать перевозчика");

        if (carrierId != null) {
            for (Carrier c : Storage.carriers) {
                if (c.getId().equals(carrierId)) {
                    etName.setText(c.getName());
                    etPhone.setText(c.getPhone());
                    etLicense.setText(c.getLicense());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String license = etLicense.getText().toString().trim();

            if (name.isEmpty() || license.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingCarrierId == null) {
                Carrier newCarrier = new Carrier(UUID.randomUUID().toString(), name, phone, license);
                Storage.carriers.add(newCarrier);
            } else {
                for (Carrier c : Storage.carriers) {
                    if (c.getId().equals(editingCarrierId)) {
                        Storage.carriers.remove(c);
                        Storage.carriers.add(new Carrier(editingCarrierId, name, phone, license));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Перевозчик сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
package com.example.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DocumentsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvDocuments;
    private FloatingActionButton fabAdd;
    private EditText etType;
    private EditText etDate;
    private Spinner spinnerWarehouse;
    private EditText etStatus;
    private Spinner spinnerProducts;
    private Button btnAddProduct;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingDocumentId;
    private List<String> selectedProducts;
    private List<String> productIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_documents);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvDocuments = findViewById(R.id.rv_documents);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Документы");

        adapter = new SimpleAdapter(Utils.TYPE_DOCUMENT, item -> {
            Intent intent = new Intent(DocumentsActivity.this, DocumentsActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Document) item).getId());
            startActivity(intent);
        });
        rvDocuments.setLayoutManager(new LinearLayoutManager(this));
        rvDocuments.setAdapter(adapter);
        adapter.setData(Storage.documents);
        tvNoData.setVisibility(Storage.documents.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.documents.remove(position);
                adapter.setData(Storage.documents);
                tvNoData.setVisibility(Storage.documents.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(DocumentsActivity.this, "Документ удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvDocuments);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentsActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Document> filtered = new ArrayList<>();
                for (Document d : Storage.documents) {
                    if (d.getType().toLowerCase().contains(newText.toLowerCase()) ||
                            d.getDate().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(d);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String documentId) {
        editingDocumentId = documentId;
        setContentView(R.layout.activity_documents_add);

        toolbar = findViewById(R.id.toolbar);
        etType = findViewById(R.id.et_type);
        etDate = findViewById(R.id.et_date);
        spinnerWarehouse = findViewById(R.id.spinner_warehouse);
        etStatus = findViewById(R.id.et_status);
        spinnerProducts = findViewById(R.id.spinner_products);
        btnAddProduct = findViewById(R.id.btn_add_product);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(documentId == null ? "Добавить документ" : "Редактировать документ");

        selectedProducts = new ArrayList<>();
        List<String> warehouseNames = new ArrayList<>();
        List<String> warehouseIds = new ArrayList<>();
        for (Warehouse w : Storage.warehouses) {
            warehouseNames.add(w.getName());
            warehouseIds.add(w.getId());
        }

        if (warehouseNames.isEmpty()) {
            Toast.makeText(this, "Добавьте хотя бы один склад", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);
        } else {
            ArrayAdapter<String> warehouseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, warehouseNames);
            warehouseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWarehouse.setAdapter(warehouseAdapter);
        }

        List<String> productNames = new ArrayList<>();
        productIds = new ArrayList<>();
        for (Product p : Storage.products) {
            productNames.add(p.getName() + " (" + p.getBarcode() + ")");
            productIds.add(p.getId());
        }
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProducts.setAdapter(productAdapter);

        if (documentId != null) {
            for (Document d : Storage.documents) {
                if (d.getId().equals(documentId)) {
                    etType.setText(d.getType());
                    etDate.setText(d.getDate());
                    etStatus.setText(d.getStatus());
                    selectedProducts.addAll(d.getProducts());
                    for (int i = 0; i < warehouseIds.size(); i++) {
                        if (warehouseIds.get(i).equals(d.getWarehouseId())) {
                            spinnerWarehouse.setSelection(i);
                            break;
                        }
                    }
                    break;
                }
            }
        } else {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            etDate.setText(currentDate);
        }

        btnAddProduct.setOnClickListener(v -> {
            int productIndex = spinnerProducts.getSelectedItemPosition();
            if (productIndex >= 0 && productIndex < productIds.size()) {
                String productId = productIds.get(productIndex);
                if (!selectedProducts.contains(productId)) {
                    selectedProducts.add(productId);
                    Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Товар уже добавлен", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSave.setOnClickListener(v -> {
            String type = etType.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String status = etStatus.getText().toString().trim();
            int warehouseIndex = spinnerWarehouse.getSelectedItemPosition();
            String warehouseId = warehouseIndex >= 0 && warehouseIndex < warehouseIds.size() ? warehouseIds.get(warehouseIndex) : "";

            if (type.isEmpty() || date.isEmpty() || warehouseId.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date);
            } catch (Exception e) {
                Toast.makeText(this, "Введите дату в формате yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingDocumentId == null) {
                Document newDocument = new Document(UUID.randomUUID().toString(), type, date, warehouseId, status, new ArrayList<>(selectedProducts));
                Storage.documents.add(newDocument);
            } else {
                for (Document d : Storage.documents) {
                    if (d.getId().equals(editingDocumentId)) {
                        Storage.documents.remove(d);
                        Storage.documents.add(new Document(editingDocumentId, type, date, warehouseId, status, new ArrayList<>(selectedProducts)));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Документ сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import java.util.ArrayList;
import java.util.UUID;

public class ProductsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvProducts;
    private FloatingActionButton fabAdd;
    private EditText etName;
    private EditText etPrice;
    private EditText etQuantity;
    private EditText etBarcode;
    private Button btnScan;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingProductId;
    private androidx.activity.result.ActivityResultLauncher<ScanOptions> barcodeLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_products);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvProducts = findViewById(R.id.rv_products);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Товары");

        adapter = new SimpleAdapter(Utils.TYPE_PRODUCT, item -> {
            Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Product) item).getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);
        adapter.setData(Storage.products);
        tvNoData.setVisibility(Storage.products.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.products.remove(position);
                adapter.setData(Storage.products);
                tvNoData.setVisibility(Storage.products.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(ProductsActivity.this, "Товар удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvProducts);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductsActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Product> filtered = new ArrayList<>();
                for (Product p : Storage.products) {
                    if (p.getName().toLowerCase().contains(newText.toLowerCase()) ||
                            p.getBarcode().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(p);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String productId) {
        editingProductId = productId;
        setContentView(R.layout.activity_products_add);

        toolbar = findViewById(R.id.toolbar);
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etQuantity = findViewById(R.id.et_quantity);
        etBarcode = findViewById(R.id.et_barcode);
        btnScan = findViewById(R.id.btn_scan);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(productId == null ? "Добавить товар" : "Редактировать товар");

        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                etBarcode.setText(result.getContents());
                Toast.makeText(this, "Штрих-код отсканирован", Toast.LENGTH_SHORT).show();
            }
        });

        btnScan.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setPrompt("Наведите камеру на штрих-код");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            barcodeLauncher.launch(options);
        });

        if (productId != null) {
            for (Product p : Storage.products) {
                if (p.getId().equals(productId)) {
                    etName.setText(p.getName());
                    etPrice.setText(String.valueOf(p.getPrice()));
                    etQuantity.setText(String.valueOf(p.getQuantity()));
                    etBarcode.setText(p.getBarcode());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String barcode = etBarcode.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            int quantity;
            try {
                price = Double.parseDouble(priceStr);
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некорректные значения цены или количества", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingProductId == null) {
                Product newProduct = new Product(UUID.randomUUID().toString(), name, price, quantity, barcode);
                Storage.products.add(newProduct);
            } else {
                for (Product p : Storage.products) {
                    if (p.getId().equals(editingProductId)) {
                        Storage.products.remove(p);
                        Storage.products.add(new Product(editingProductId, name, price, quantity, barcode));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Товар сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
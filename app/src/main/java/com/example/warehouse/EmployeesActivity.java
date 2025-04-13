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

public class EmployeesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView tvNoData;
    private RecyclerView rvEmployees;
    private FloatingActionButton fabAdd;
    private EditText etFullName;
    private EditText etPosition;
    private EditText etPhone;
    private Button btnSave;
    private SimpleAdapter adapter;
    private String editingEmployeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String entityId = getIntent().getStringExtra(Utils.EXTRA_ENTITY_ID);
        if (entityId != null) {
            showAddEditForm(entityId);
            return;
        }

        setContentView(R.layout.activity_employees);

        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.search_view);
        tvNoData = findViewById(R.id.tv_no_data);
        rvEmployees = findViewById(R.id.rv_employees);
        fabAdd = findViewById(R.id.fab_add);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Сотрудники");

        adapter = new SimpleAdapter(Utils.TYPE_EMPLOYEE, item -> {
            Intent intent = new Intent(EmployeesActivity.this, EmployeesActivity.class);
            intent.putExtra(Utils.EXTRA_ENTITY_ID, ((Employee) item).getId());
            startActivity(intent);
        });
        rvEmployees.setLayoutManager(new LinearLayoutManager(this));
        rvEmployees.setAdapter(adapter);
        adapter.setData(Storage.employees);
        tvNoData.setVisibility(Storage.employees.isEmpty() ? View.VISIBLE : View.GONE);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Storage.employees.remove(position);
                adapter.setData(Storage.employees);
                tvNoData.setVisibility(Storage.employees.isEmpty() ? View.VISIBLE : View.GONE);
                Toast.makeText(EmployeesActivity.this, "Сотрудник удален", Toast.LENGTH_SHORT).show();
            }
        });
        itemTouchHelper.attachToRecyclerView(rvEmployees);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployeesActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Employee> filtered = new ArrayList<>();
                for (Employee e : Storage.employees) {
                    if (e.getFullName().toLowerCase().contains(newText.toLowerCase()) ||
                            e.getPosition().toLowerCase().contains(newText.toLowerCase()) ||
                            e.getPhone().toLowerCase().contains(newText.toLowerCase())) {
                        filtered.add(e);
                    }
                }
                adapter.setData(filtered);
                tvNoData.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
                return true;
            }
        });
    }

    private void showAddEditForm(String employeeId) {
        editingEmployeeId = employeeId;
        setContentView(R.layout.activity_employees_add);

        toolbar = findViewById(R.id.toolbar);
        etFullName = findViewById(R.id.et_full_name);
        etPosition = findViewById(R.id.et_position);
        etPhone = findViewById(R.id.et_phone);
        btnSave = findViewById(R.id.btn_save);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(employeeId == null ? "Добавить сотрудника" : "Редактировать сотрудника");

        if (employeeId != null) {
            for (Employee e : Storage.employees) {
                if (e.getId().equals(employeeId)) {
                    etFullName.setText(e.getFullName());
                    etPosition.setText(e.getPosition());
                    etPhone.setText(e.getPhone());
                    break;
                }
            }
        }

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String position = etPosition.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (fullName.isEmpty() || position.isEmpty()) {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingEmployeeId == null) {
                Employee newEmployee = new Employee(UUID.randomUUID().toString(), fullName, position, phone);
                Storage.employees.add(newEmployee);
            } else {
                for (Employee e : Storage.employees) {
                    if (e.getId().equals(editingEmployeeId)) {
                        Storage.employees.remove(e);
                        Storage.employees.add(new Employee(editingEmployeeId, fullName, position, phone));
                        break;
                    }
                }
            }
            Toast.makeText(this, "Сотрудник сохранен", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
package com.example.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class DashboardActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private MaterialCardView cardProducts;
    private MaterialCardView cardEmployees;
    private MaterialCardView cardSuppliers;
    private MaterialCardView cardCarriers;
    private MaterialCardView cardWarehouses;
    private MaterialCardView cardDocuments;
    private TextView navHeaderEmail;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navView = findViewById(R.id.nav_view);
        cardProducts = findViewById(R.id.card_products);
        cardEmployees = findViewById(R.id.card_employees);
        cardSuppliers = findViewById(R.id.card_suppliers);
        cardCarriers = findViewById(R.id.card_carriers);
        cardWarehouses = findViewById(R.id.card_warehouses);
        cardDocuments = findViewById(R.id.card_documents);
        utils = new Utils(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Складской учет");

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_products) {
                startActivity(new Intent(this, ProductsActivity.class));
            } else if (id == R.id.nav_employees) {
                startActivity(new Intent(this, EmployeesActivity.class));
            } else if (id == R.id.nav_suppliers) {
                startActivity(new Intent(this, SuppliersActivity.class));
            } else if (id == R.id.nav_carriers) {
                startActivity(new Intent(this, CarriersActivity.class));
            } else if (id == R.id.nav_warehouses) {
                startActivity(new Intent(this, WarehousesActivity.class));
            } else if (id == R.id.nav_documents) {
                startActivity(new Intent(this, DocumentsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                utils.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        View headerView = navView.getHeaderView(0);
        navHeaderEmail = headerView.findViewById(R.id.tv_email);
        navHeaderEmail.setText(utils.getUserEmail());

        cardProducts.setOnClickListener(v -> startActivity(new Intent(this, ProductsActivity.class)));
        cardEmployees.setOnClickListener(v -> startActivity(new Intent(this, EmployeesActivity.class)));
        cardSuppliers.setOnClickListener(v -> startActivity(new Intent(this, SuppliersActivity.class)));
        cardCarriers.setOnClickListener(v -> startActivity(new Intent(this, CarriersActivity.class)));
        cardWarehouses.setOnClickListener(v -> startActivity(new Intent(this, WarehousesActivity.class)));
        cardDocuments.setOnClickListener(v -> startActivity(new Intent(this, DocumentsActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
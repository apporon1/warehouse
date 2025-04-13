package com.example.warehouse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
    private final String type;
    private List<Object> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public SimpleAdapter(String type, OnItemClickListener listener) {
        this.type = type;
        this.listener = listener;
    }

    public void setData(List<?> data) {
        this.data = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        switch (type) {
            case Utils.TYPE_PRODUCT:
                layoutId = R.layout.item_product;
                break;
            case Utils.TYPE_EMPLOYEE:
                layoutId = R.layout.item_employee;
                break;
            case Utils.TYPE_SUPPLIER:
                layoutId = R.layout.item_supplier;
                break;
            case Utils.TYPE_CARRIER:
                layoutId = R.layout.item_carrier;
                break;
            case Utils.TYPE_WAREHOUSE:
                layoutId = R.layout.item_warehouse;
                break;
            case Utils.TYPE_DOCUMENT:
                layoutId = R.layout.item_document;
                break;
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object item = data.get(position);
        switch (type) {
            case Utils.TYPE_PRODUCT:
                Product p = (Product) item;
                holder.tvName.setText(p.getName());
                holder.tvDetails.setText("Цена: " + p.getPrice() + ", Кол-во: " + p.getQuantity() + ", Штрих-код: " + p.getBarcode());
                break;
            case Utils.TYPE_EMPLOYEE:
                Employee e = (Employee) item;
                holder.tvName.setText(e.getFullName());
                holder.tvDetails.setText("Должность: " + e.getPosition() + ", Телефон: " + e.getPhone());
                break;
            case Utils.TYPE_SUPPLIER:
                Supplier s = (Supplier) item;
                holder.tvName.setText(s.getName());
                holder.tvDetails.setText("ИНН: " + s.getInn() + ", Телефон: " + s.getPhone());
                break;
            case Utils.TYPE_CARRIER:
                Carrier c = (Carrier) item;
                holder.tvName.setText(c.getName());
                holder.tvDetails.setText("Лицензия: " + c.getLicense() + ", Телефон: " + c.getPhone());
                break;
            case Utils.TYPE_WAREHOUSE:
                Warehouse w = (Warehouse) item;
                holder.tvName.setText(w.getName());
                holder.tvDetails.setText("Адрес: " + w.getAddress());
                break;
            case Utils.TYPE_DOCUMENT:
                Document d = (Document) item;
                holder.tvName.setText(d.getType());
                holder.tvDetails.setText("Дата: " + d.getDate() + ", Статус: " + d.getStatus());
                break;
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDetails = itemView.findViewById(R.id.tv_details);
        }
    }
}
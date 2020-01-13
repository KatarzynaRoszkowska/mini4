package pl.roszkowska.geokr;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ShopAdapter  extends RecyclerView.Adapter<ShopViewHolder> {
    private Context context;
    private ArrayList<Shop> list;
    private ArrayList<Shop> mArrayList;

    private ShopDbHelper mDatabase;

    public ShopAdapter(Context context, ArrayList<Shop> list) {
        this.context = context;
        this.list = list;
        this.mArrayList=list;
        mDatabase = new ShopDbHelper(context);
    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopViewHolder holder, int position) {
        final Shop shop = list.get(position);

        holder.name.setText(shop.getName());
        holder.description.setText(String.valueOf(shop.getDescription()));
        holder.r.setText(String.valueOf(shop.getR()));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database

                mDatabase.delete(shop.getName());

                //refresh the activity page.
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}

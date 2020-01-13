package pl.roszkowska.geokr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class ShopActivity extends Activity {
    SharedPreferences sp;
    private ShopDbHelper mDatabase;
    private ArrayList<Shop> allShops = new ArrayList<>();
    private ShopAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_shop);

        RecyclerView contactView = findViewById(R.id.shops_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactView.setLayoutManager(linearLayoutManager);
        contactView.setHasFixedSize(true);

        mDatabase = new ShopDbHelper(this);
        allShops = mDatabase.getAllShops();

        if (allShops.size() > 0) {
            contactView.setVisibility(View.VISIBLE);
            mAdapter = new ShopAdapter(this, allShops);
            contactView.setAdapter(mAdapter);

        } else {
            contactView.setVisibility(View.GONE);
        }

    }



}
package pl.roszkowska.geokr;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopViewHolder  extends  RecyclerView.ViewHolder {
    public TextView name, description, r;
    public ImageView delete;


    public ShopViewHolder(View itemView) {

        super(itemView);

        name = (TextView)itemView.findViewById(R.id.name);
        description = (TextView)itemView.findViewById(R.id.description);
        r = (TextView)itemView.findViewById(R.id.r);

        delete = (ImageView)itemView.findViewById(R.id.delete);
    }
}

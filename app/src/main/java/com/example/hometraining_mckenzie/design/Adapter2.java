package com.example.hometraining_mckenzie.design;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hometraining_mckenzie.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.ViewHolder> {
    List<String> titles;
    List<Integer> images;
    LayoutInflater inflater;

    public Adapter2(Context ctx, List<String> titles, List<Integer> images) {
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.gridIcon.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView gridIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView2);
            gridIcon = itemView.findViewById(R.id.imageView2);
//            Intent intent = new Intent(itemView.getContext(),
//                    EduVideo.class);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    switch (title.getText().toString()) {
//                        case "Q-set":
//                            exer_setting(0);
//                            itemView.getContext().startActivity(intent);
//                            break;
//                        case "Q-Walk":
//                            exer_setting(1);
//                            itemView.getContext().startActivity(intent);
//                            break;
//                        case "Side-Walk":
//                            exer_setting(2);
//                            itemView.getContext().startActivity(intent);
//                            break;
//                        case "Squat":
//                            exer_setting(3);
//                            itemView.getContext().startActivity(intent);
//                            break;
//                        default:
//                            break;
//                    }
                }
            });
        }

        private void exer_setting(int type){
            SharedPreferences exer_type = itemView.getContext().getSharedPreferences("exer_type", MODE_PRIVATE);
            SharedPreferences.Editor editor = exer_type.edit();

            editor.putString("exer",String.valueOf(type));
            editor.apply();
        }
    }
}
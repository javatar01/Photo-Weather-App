package javatar.com.photoweatherapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javatar.com.photoweatherapp.R;
import javatar.com.photoweatherapp.databinding.ItemImageBinding;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<String> listImage = new ArrayList<>();

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String itemImage = listImage.get(position);

        if (position == listImage.size() - 1){
            holder.binding.addImage.setVisibility(View.VISIBLE);
            holder.binding.cardImg.setVisibility(View.GONE);
            holder.binding.addImage.setOnClickListener(v -> v.getContext().startActivity(new Intent(v.getContext(), SetPhotoActivity.class)));
        }else {
            holder.binding.addImage.setVisibility(View.GONE);
            holder.binding.cardImg.setVisibility(View.VISIBLE);

            Bitmap myBitmap = BitmapFactory.decodeFile(itemImage);
            holder.binding.image.setImageBitmap(myBitmap);

            holder.binding.image.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), SetPhotoActivity.class);
                intent.putExtra("uri_image",itemImage);
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public void setList(List<String> listImage) {
        this.listImage = listImage;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
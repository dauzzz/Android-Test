package tw.edu.ndhu.ee.tensorflowmodel.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tw.edu.ndhu.ee.tensorflowmodel.R;
import tw.edu.ndhu.ee.tensorflowmodel.item.Sticker;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerHolder>{

    Context context;
    List<Sticker> stickerList;
    int chosenOne = 0;
    boolean viewable;
    OnChosenListener onChosenListener;
    private boolean initialize;

    public StickerAdapter(Context context, List<Sticker> stickerList, OnChosenListener onChosenListener) {
        this.context = context;
        this.stickerList = stickerList;
        this.onChosenListener = onChosenListener;
        viewable = false;
        initialize = true;
    }

    @NonNull
    @Override
    public StickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StickerHolder(LayoutInflater.from(context).inflate(R.layout.sticker_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StickerHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return isViewable() ? stickerList.size() : 1;
    }

    public void setChosenOne(int chosenOne) {
        this.chosenOne = chosenOne;
    }

    public int getChosenOne() {
        return chosenOne;
    }

    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    public boolean isViewable() {
        return viewable;
    }

    class StickerHolder extends RecyclerView.ViewHolder{

        private static final String TAG = "stickerHolder";
        View view;
        ConstraintLayout stickerContainer;
        ImageButton imageButton;
        Sticker sticker;

        public StickerHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            stickerContainer = itemView.findViewById(R.id.sticker_container);
            imageButton = itemView.findViewById(R.id.sticker_button);
        }

        public void bind(int position){
            sticker = stickerList.get(position);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            if(sticker.getResId() != 0){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),sticker.getResId(),options);
                imageButton.setImageBitmap(bitmap);
                imageButton.setOnClickListener(v -> {
                    if(viewable){
                        setChosenOne(getAdapterPosition());
                        removeChildOtherThan(chosenOne);
                        onChosenListener.onChosen(sticker.getResId(),chosenOne);
                        setViewable(false);
                        //notifyDataSetChanged();
                    }
                });
            }else{
                imageButton.setImageBitmap(null);
                imageButton.setOnClickListener(null);
            }
            if(initialize){
                initialize = false;
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),stickerList.get(position+2).getResId(),options);
                imageButton.setImageBitmap(bitmap);
            }
            /*if(position != chosenOne){
                if(viewable){
                    stickerContainer.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = stickerContainer.getLayoutParams();
                    params.height = params.width;
                    stickerContainer.setLayoutParams(params);
                } else {
                    stickerContainer.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams params = stickerContainer.getLayoutParams();
                    params.height = 0;
                    stickerContainer.setLayoutParams(params);
                }
            }*/
        }
    }

    private void removeChildOtherThan(int position){
        int middleStart = 1;
        int countFromMiddle =stickerList.size() - position - 1;
        int start = 0;
        int countFromStart = position;
        notifyItemRangeRemoved(start,countFromStart);
        notifyItemRangeRemoved(middleStart,countFromMiddle);
    }

    public void addChildOtherThan(int position){
        int middleStart = position+1;
        int countFromMiddle =stickerList.size() - middleStart;
        int start = 0;
        int countFromStart = position;
        notifyItemRangeInserted(start,countFromStart);
        notifyItemRangeInserted(middleStart,countFromMiddle);
    }

    public interface OnChosenListener{
        void onChosen(int resId,int position);
    }
}

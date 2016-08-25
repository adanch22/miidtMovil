package ciex.edu.mx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ciex.edu.mx.R;
import ciex.edu.mx.model.Unit;

/**
 * Created by azulyoro on 12/08/16.
 */
public class unitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<Unit> unitsArrayList;
    private final Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, grammar, vocabulary;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) itemView.findViewById(R.id.title);
            grammar = (TextView) itemView.findViewById(R.id.grammar);
            vocabulary = (TextView) itemView.findViewById(R.id.vocabulary);
        }
    }

    public unitAdapter(Context mContext, ArrayList<Unit> unitsArrayList) {
        this.mContext = mContext;
        this.unitsArrayList = unitsArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_item_card, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Unit unit = unitsArrayList.get(position);
        ((ViewHolder) holder).title.setText(unit.getTitle());
        ((ViewHolder) holder).grammar.setText(unit.getGrammar());
        ((ViewHolder) holder).vocabulary.setText(unit.getVocabulary());
    }

    @Override
    public int getItemCount() {
        return unitsArrayList.size();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private unitAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                     final unitAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                            if (child != null && clickListener != null) {
                                clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}

package com.physicomtech.kit.physis_ledring.customize;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.physicomtech.kit.physis_ledring.R;

import java.util.List;

public class LightOptionView extends RelativeLayout {

    private static final String TAG = "LightOptionView";

    public interface OnSelectedItemListener {
        void onSelectedIndex(int groupTag, int itemPosition);
    }

    private OnSelectedItemListener onSelectedItemListener = null;

    public void setOnSelectedItemListener(OnSelectedItemListener listener){
        onSelectedItemListener = listener;
    }

    ImageView btnShowOptions;
    TextView tvOptionTitle, tvSelectedOption;
    RadioGroup rgOptions;

    private Context context;
    private String title, tag = null;

    private List<String> items;
    private int selectedPosition = 0;

    public LightOptionView(Context context) {
        super(context);
    }

    @SuppressLint({"Recycle", "CustomViewStyleable"})
    public LightOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        title = (String) context.obtainStyledAttributes(attrs, R.styleable.LightOption)
                .getText(R.styleable.LightOption_setOptionTitle);
        tag = (String) context.obtainStyledAttributes(attrs, R.styleable.LightOption)
                .getText(R.styleable.LightOption_setOptionTag);
        initVIew();
    }

    public LightOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initVIew() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final View view = layoutInflater.inflate(R.layout.view_light_option, this, false);
        addView(view);

        btnShowOptions = view.findViewById(R.id.btn_show_light_option);
        tvOptionTitle = view.findViewById(R.id.subject_light_option);
        tvSelectedOption = view.findViewById(R.id.tv_light_option);
        rgOptions = view.findViewById(R.id.rg_light_option);

        setOptionTitle(title);
        setOptionTag(tag);

        rgOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (onSelectedItemListener != null && group.getTag() != null) {
                    selectedPosition = group.indexOfChild(view.findViewById(checkedId));
                    onSelectedItemListener.onSelectedIndex(Integer.valueOf(group.getTag().toString()), selectedPosition);
                }
            }
        });

        btnShowOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgOptions.getVisibility() == View.VISIBLE){
                    rgOptions.setVisibility(View.GONE);
                    btnShowOptions.setImageResource(R.drawable.ic_drop_down);
                }else{
                    if(rgOptions.getChildCount() != 0 && !((RadioButton)rgOptions.getChildAt(selectedPosition)).isChecked())
                        ((RadioButton)rgOptions.getChildAt(selectedPosition)).setChecked(true);
                    rgOptions.setVisibility(View.VISIBLE);
                    btnShowOptions.setImageResource(R.drawable.ic_drop_up);
                }
            }
        });
    }

    public void setOptionItems(List<String> items){
        this.items = items;
        for(String item : items){
            RadioButton rbtn = new RadioButton(context);
            rbtn.setText(item);
            rgOptions.addView(rbtn);
        }
    }

    public void setOptionTitle(String title){
        if(title != null)
            tvOptionTitle.setText(title);
    }

    public void setOptionTag(String tag){
        if(tag != null) {
            btnShowOptions.setTag(tag);
            rgOptions.setTag(tag);
        }
    }

    public int getOptionTag(){
        return Integer.valueOf(btnShowOptions.getTag().toString());
    }

    public void setSelectedText(String option){
        Log.e(TAG, "@ Selected Option Text : " + option);
        tvSelectedOption.setText(option);
    }

    public void setEnable(boolean enable){
        btnShowOptions.setEnabled(enable);
        tvOptionTitle.setEnabled(enable);
        tvSelectedOption.setEnabled(enable);
    }
}

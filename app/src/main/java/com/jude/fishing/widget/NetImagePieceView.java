package com.jude.fishing.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jude.exgridview.PieceView;

/**
 * Created by Mr.Jude on 2015/3/14.
 */
public class NetImagePieceView extends PieceView {

    DraweeHolder mDraweeHolder;

    public NetImagePieceView(Context context) {
        super(context);
    }

    public NetImagePieceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NetImagePieceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initView() {
        super.initView();
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
//                .setProgressBarImage(getContext().getResources().getDrawable(R.drawable.default_loading))
//                .setFailureImage(getContext().getResources().getDrawable(R.drawable.job_error))
                .build();
        mDraweeHolder = DraweeHolder.create(hierarchy, getContext());
        setWillNotDraw(false);
    }

    public void setImage(Uri uri){
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(156, 156))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(mDraweeHolder.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>(){
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        super.onFinalImageSet(id, imageInfo, animatable);
                        postInvalidate();
                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        super.onFailure(id, throwable);
                        postInvalidate();
                    }
                })
                .build();
        mDraweeHolder.setController(controller);
        setBackgroundDrawable(mDraweeHolder.getHierarchy().getTopLevelDrawable());
    }

    public void setImage(Bitmap bitmap){
        BitmapDrawable drawable = new BitmapDrawable(getResources(),bitmap);
        setBackgroundDrawable(drawable);
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        if (who == mDraweeHolder.getHierarchy().getTopLevelDrawable()) {
            return true;
        }
        return super.verifyDrawable(who);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }
}

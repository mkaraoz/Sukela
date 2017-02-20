package org.bok.mk.sukela.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mk on 02.09.2016.
 */
public class AnimationListener implements View.OnClickListener {

    private View current;
    private static View previous = null;

    public AnimationListener(View v) {
        this.current = v;
        if (v.getVisibility() == View.VISIBLE)
            previous = current;
    }

    @Override
    public void onClick(View view) {
        if (previous == null) {
            previous = current;
        } else if (current != previous) {
            collapse(previous);
            previous = current;
        }

        if (current.getVisibility() == View.GONE) {
            expand(current);
        } else {
            collapse(current);
        }
    }

    private void collapse(final View view) {
        int finalHeight = view.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, view);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    private void expand(final View view) {
        view.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, view.getMeasuredHeight(), view);
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, final View view) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}

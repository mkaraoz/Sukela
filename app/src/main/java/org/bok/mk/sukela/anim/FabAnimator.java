package org.bok.mk.sukela.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionMenu;

public class FabAnimator
{
    public static AnimatorSet createAnimatorSet(final FloatingActionMenu menu, final int openImageRes, final int closeImageRes) {
        AnimatorSet set = new AnimatorSet();

        final ImageView target = menu.getMenuIconView();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(target, "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(target, "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation) {
                target.setImageResource(menu.isOpened() ? openImageRes : closeImageRes);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        return set;
    }
}

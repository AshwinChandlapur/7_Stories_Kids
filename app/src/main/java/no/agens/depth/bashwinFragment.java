package no.agens.depth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import no.agens.depth.lib.MaterialMenuDrawable;

public class bashwinFragment extends Fragment implements MenuAnimation {

    public static final int TRANSFORM_DURATION = 900;
    private boolean introAnimate;
    InterstitialAd mInterstitialAd;
    private InterstitialAd interstitial;

    public bashwinFragment() {
    }

    public void setIntroAnimate(boolean introAnimate) {
        this.introAnimate = introAnimate;
    }

    View root;
    MaterialMenuDrawable menuIcon;
    WaterSceneView waterScene;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_bashwin, container, false);
        waterScene = (WaterSceneView) root.findViewById(R.id.water_scene);
        setupFab();
        introAnimate();
       // setupSeekbars();
        setupMenuButton();
        ((RootActivity) getActivity()).setCurretMenuIndex(3);

        AdRequest adRequest = new AdRequest.Builder().build();
        // Prepare the Interstitial Ad
        interstitial = new InterstitialAd(root.getContext());
// Insert the Ad Unit ID
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        interstitial.loadAd(adRequest);
// Prepare an Interstitial Ad Listener
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
// Call displayInterstitial() function
                displayInterstitial();
            }
        });

        return root;
    }

   /* private void setupSeekbars() {
        //SeekBar waveSeekBar = (SeekBar) root.findViewById(R.id.wave_seekbar);
        //SeekBar noiseSeekBar = (SeekBar) root.findViewById(R.id.noise_seekbar);

        //WindFragment.setProgressBarColor(waveSeekBar, getResources().getColor(R.color.fab));
      //  WindFragment.setProgressBarColor(noiseSeekBar, getResources().getColor(R.color.fab));

       // noiseSeekBar.setProgress(50);
       // waveSeekBar.setProgress(50);

        //waveSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterScene.setWaveHeight(progress / 4f * getResources().getDisplayMetrics().density);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        noiseSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waterScene.setNoiseIntensity((float) progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }*/

    private void setupMenuButton() {
        ImageView menu = (ImageView) root.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((RootActivity) getActivity()).isMenuVisible)
                    ((RootActivity) getActivity()).showMenu();
                else
                    getActivity().onBackPressed();
            }
        });
        menuIcon = new MaterialMenuDrawable(getActivity(), Color.WHITE, MaterialMenuDrawable.Stroke.THIN, TRANSFORM_DURATION);
        menu.setImageDrawable(menuIcon);
    }
    public void displayInterstitial() {
// If Ads are loaded, show Interstitial else show nothing.
        if (interstitial.isLoaded() && Math.random()>0.7) {
            interstitial.show();
        }
    }

    private void introAnimate() {
        if (introAnimate)
            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    TransitionHelper.startIntroAnim(root, showShadowListener);
                    hideShadow();
                    waterScene.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            waterScene.setPause(true);
                        }
                    }, 10);
                }
            });
    }

    private void setupFab() {
        root.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        TransitionHelper.startExitAnim(root);
                    }
                });
                cashwinFragment cashwinFragment = new cashwinFragment();
                cashwinFragment.setIntroAnimate(true);
                ((RootActivity) getActivity()).goToFragment(cashwinFragment);
                if (((RootActivity) getActivity()).isMenuVisible)
                    ((RootActivity) getActivity()).hideMenu();
                hideShadow();
                waterScene.setPause(true);
            }
        });
    }

    AnimatorListenerAdapter showShadowListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            showShadow();
            waterScene.setPause(false);
        }
    };

    private void hideShadow() {
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.GONE);
    }

    private void showShadow() {
        View actionbarShadow = root.findViewById(R.id.actionbar_shadow);
        actionbarShadow.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(actionbarShadow, View.ALPHA, 0, 0.8f).setDuration(400).start();
    }

    @Override
    public void animateTOMenu() {
        TransitionHelper.animateToMenuState(root, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                waterScene.setPause(false);
            }
        });
        menuIcon.animateIconState(MaterialMenuDrawable.IconState.ARROW);
        hideShadow();
        waterScene.setPause(true);
    }

    @Override
    public void revertFromMenu() {
        TransitionHelper.startRevertFromMenu(root, showShadowListener);
        menuIcon.animateIconState(MaterialMenuDrawable.IconState.BURGER);
        waterScene.setPause(true);
    }

    @Override
    public void exitFromMenu() {
        TransitionHelper.animateMenuOut(root);
        waterScene.setPause(true);
    }
}

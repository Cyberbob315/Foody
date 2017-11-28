package nhannt.foody.screen.placedetail;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import nhannt.foody.R;
import nhannt.foody.data.model.Branch;
import nhannt.foody.data.model.Place;
import nhannt.foody.data.model.PlaceWifi;
import nhannt.foody.screen.BaseActivity;
import nhannt.foody.screen.wifi.WifiActivity;
import nhannt.foody.utils.Constants;
import nhannt.foody.utils.Utils;

public class PlaceDetailActivity extends BaseActivity
    implements PlaceDetailContract.View, OnMapReadyCallback {
    private PlaceDetailContract.Presenter mPresenter;
    private TextView mTvPlaceName, mTvPlaceAddress, mTvOpenTime, mTvStatus, mTvTotalImage,
        mTvTotalCheckin, mTvTotalBookmark, mTvTotalComment, mTvPlaceNameToolbar,
        mTvPriceRange, mTvWifiName, mTvWifiPassword, mTvWifiDate;
    private LinearLayout mLLUtils, mLLWifiContainer;
    private ImageView mImgPlaceImage;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerViewComment;
    private NestedScrollView mNestedScrollView;
    private CommentRecyclerViewAdapter mCommentAdapter;
    private Place mPlace;
    private GoogleMap mGoogleMap;
    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        mPresenter = new PlaceDetailPresenter();
        mPresenter.setView(this);
        initViews();
        initEvents();
        mPlace = getIntent().getParcelableExtra("place");
        setToolbar();
        setViews();
        mPresenter.getPlaceImage(mPlace.getHinhanhquanan().get(0));
        mPresenter.downloadUtilImage(mPlace.getTienich());

    }

    private void initEvents() {
        mLLWifiContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlace != null) {
                    Intent intent = new Intent(PlaceDetailActivity.this, WifiActivity.class);
                    intent.putExtra(Constants.PLACE_WIFI_KEY, mPlace.getMaquanan());
                    intent.putExtra(Constants.PLACE_WIFI_NAME_KEY, mPlace.getTenquanan());
                    startActivity(intent);
                }
            }
        });
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getWifiList(mPlace.getMaquanan());
    }

    private void setViews() {
        mTvPlaceNameToolbar.setText(mPlace.getTenquanan());
        mTvPlaceName.setText(mPlace.getTenquanan());
        if (mPlace.getLstBranch().size() > 0) {
            mTvPlaceAddress.setText(mPlace.getLstBranch().get(0).getDiachi());
        }
        mTvOpenTime.setText(mPlace.getGiomocua() + " - " + mPlace.getGiodongcua());
        mTvTotalImage.setText(mPlace.getHinhanhquanan().size() + "");
        mTvTotalComment.setText(mPlace.getBinhluanList().size() + "");
        mTvStatus.setText(Utils.getPlaceStatus(mPlace.getGiomocua(), mPlace.getGiodongcua()));
        if (mPlace.getGiatoida() != 0 && mPlace.getGiatoithieu() != 0) {
            NumberFormat numberFormat = new DecimalFormat("###,###");
            String minPrice = numberFormat.format(mPlace.getGiatoithieu()) + " VND";
            String maxPrice = numberFormat.format(mPlace.getGiatoida()) + "VND";
            mTvPriceRange.setText(minPrice + " - " + maxPrice);
        } else {
            mTvPriceRange.setVisibility(View.GONE);
        }
        // Setup recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewComment.setLayoutManager(layoutManager);
        mRecyclerViewComment.setNestedScrollingEnabled(false);
        mCommentAdapter = new CommentRecyclerViewAdapter(this, mPlace.getBinhluanList());
        mRecyclerViewComment.setAdapter(mCommentAdapter);
        // Map
        mMapFragment.getMapAsync(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mTvPlaceName = findViewById(R.id.tv_place_name);
        mTvPlaceAddress = findViewById(R.id.tv_place_address);
        mTvOpenTime = findViewById(R.id.tv_open_time);
        mTvStatus = findViewById(R.id.tv_status);
        mTvTotalImage = findViewById(R.id.tv_total_image);
        mTvTotalCheckin = findViewById(R.id.tv_total_checkin);
        mTvTotalBookmark = findViewById(R.id.tv_total_bookmark);
        mTvTotalComment = findViewById(R.id.tv_total_comment);
        mImgPlaceImage = findViewById(R.id.img_place);
        mTvPlaceNameToolbar = findViewById(R.id.tv_place_name_toolbar);
        mRecyclerViewComment = findViewById(R.id.rv_comment_detail);
        mNestedScrollView = findViewById(R.id.nested_scroll_view_place_detail);
        mTvPriceRange = findViewById(R.id.tv_price_range);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mLLUtils = findViewById(R.id.ll_utils_place_detail);
        mTvWifiName = findViewById(R.id.tv_wifi_name);
        mTvWifiPassword = findViewById(R.id.tv_password_wifi);
        mTvWifiDate = findViewById(R.id.tv_wifi_date);
        mLLWifiContainer = findViewById(R.id.ll_wifi);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void setPlaceImage(String url) {
        Glide.with(this).load(url).into(mImgPlaceImage);
    }

    @Override
    public void appendUtilImage(String url) {
        final ImageView imageUtil = new ImageView(PlaceDetailActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        layoutParams.setMargins(10, 10, 10, 10);
        imageUtil.setLayoutParams(layoutParams);
        imageUtil.setScaleType(ImageView.ScaleType.FIT_XY);
        imageUtil.setPadding(5, 5, 5, 5);
        mLLUtils.addView(imageUtil);
        Glide.with(this).load(url).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                        Target<Drawable> target,
                                        boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                           DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(imageUtil);
    }

    @Override
    public void showListWifi(ArrayList<PlaceWifi> lstWifi) {
        if (lstWifi.size() > 0) {
            mTvWifiName.setText(lstWifi.get(0).getTen());
            mTvWifiPassword.setText(lstWifi.get(0).getMatkhau());
            mTvWifiDate.setText(lstWifi.get(0).getNgaydang());
        } else {
            mTvWifiName.setText(getResources().getString(R.string.click_to_add_wifi));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        Branch branch = mPlace.getLstBranch().get(0);
        LatLng latLng = new LatLng(branch.getLatitude(), branch.getLongitude());
        markerOptions.position(latLng);
        markerOptions.title(mPlace.getTenquanan());
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.moveCamera(cameraUpdate);
    }
}

package com.luck.picture.lib;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.luck.picture.lib.basic.PictureCommonFragment;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectLimitType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.config.SelectModeConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.manager.SelectedManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PictureSelectorPickFragment extends PictureCommonFragment {
    public static final String TAG = PictureSelectorPickFragment.class.getSimpleName();

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull PickVisualMediaRequest input) {
            Intent intent = super.createIntent(context, input);
            if (isCustomMimeType()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, getCustomMimeTypes());
            }
            return intent;
        }
    }, uri -> {
        backResult(uri);
    });
    private ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull PickVisualMediaRequest input) {
            Intent intent = super.createIntent(context, input);
            if (isCustomMimeType()) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, getCustomMimeTypes());
            }
            return intent;
        }
    }, uris -> {
        backResult(uris);
    });


    public static PictureSelectorPickFragment newInstance() {
        return new PictureSelectorPickFragment();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public int getResourceId() {
        return R.layout.ps_empty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            ActivityOptionsCompat animation =
                    ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.ps_anim_up_in, R.anim.ps_anim_down_out);
            if (selectorConfig.selectionMode == SelectModeConfig.MULTIPLE && selectorConfig.maxSelectNum > 1) {
                pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(getVisualMediaType())
                        .setMaxItems(selectorConfig.maxSelectNum)
                        .build(), animation);
            } else {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(getVisualMediaType())
                        .build(), animation);
            }
        }
    }

    private void backResult(List<Uri> uris) {
        List<LocalMedia> list = doFilterResultByQuery(uris);
        list = doFilterResultBySelect(list);
        ArrayList<LocalMedia> selectedResult = selectorConfig.getSelectedResult();
        if (uris != null && !uris.isEmpty() && !selectedResult.isEmpty()) {
            list.removeAll(selectedResult);// 去重
        }
        selectedResult.addAll(list);
        if (selectorConfig.isEmptyResultReturn && list.isEmpty()) {
            onExitPictureSelector();
        } else {
            dispatchTransformResult();
        }
    }

    private void backResult(Uri uri) {
        List<Uri> uris = new ArrayList<>();
        if (uri != null) {
            uris.add(uri);
        }
        backResult(uris);
    }

    /**
     * 过滤查询结果 - 查询文件大小，视频时长，自定义查询过滤等（同步到 PictureSelectorFragment 在加载数据时就过滤了的）
     * filterMinFileSize， filterMaxFileSize，filterVideoMinSecond，filterVideoMaxSecond
     *
     * @param uris 选择结果
     * @return
     * @see com.luck.picture.lib.loader.IBridgeMediaLoader
     */
    private List<LocalMedia> doFilterResultByQuery(List<Uri> uris) {
        List<LocalMedia> result = new ArrayList<>();
        if (uris == null || uris.isEmpty()) {
            return result;
        }
        for (Uri uri : uris) {
            result.add(buildLocalMedia(uri.toString()));
        }
        Iterator<LocalMedia> iterator = result.iterator();
        while (iterator.hasNext()) {
            LocalMedia item = iterator.next();
            if (item == null || isFilterFileSize(item) || isFilterVideoDuration(item) || isFilterQuery(item)) {
                iterator.remove();
            }
        }
        return result;
    }

    /**
     * 过滤选择结果 - 选择文件大小，视频时长，自定义选择过滤等
     *
     * @param selected 选择结果
     * @return
     * @see com.luck.picture.lib.basic.PictureCommonFragment#confirmSelect
     */
    private List<LocalMedia> doFilterResultBySelect(List<LocalMedia> selected) {
        List<LocalMedia> result = new ArrayList<>();
        if (selected == null || selected.isEmpty()) {
            return result;
        }
        for (LocalMedia media : selected) {
            if (!isFilterResultBySelect(media)) {
                result.add(media);
            }
        }
        return result;
    }

    /**
     * 过滤选择结果 - 选择文件大小，视频时长，自定义选择过滤等
     * selectMaxFileSize， selectMinFileSize，maxVideoSelectNum，selectMaxDurationSecond，selectMinDurationSecond等
     *
     * @param localMedia 选择结果
     * @return
     * @see com.luck.picture.lib.basic.PictureCommonFragment#confirmSelect
     */
    private boolean isFilterResultBySelect(LocalMedia localMedia) {
        if (localMedia == null) {
            return true;
        }
        if (selectorConfig.onSelectFilterListener != null) {
            if (selectorConfig.onSelectFilterListener.onSelectFilter(localMedia)) {
                boolean isSelectLimit = false;
                if (selectorConfig.onSelectLimitTipsListener != null) {
                    isSelectLimit = selectorConfig.onSelectLimitTipsListener
                            .onSelectLimitTips(getAppContext(), localMedia, selectorConfig, SelectLimitType.SELECT_NOT_SUPPORT_SELECT_LIMIT);
                }
                if (isSelectLimit) {
                    return true;
                }
            }
        }
        int checkSelectValidity = isCheckSelectValidity(localMedia, false);
        if (checkSelectValidity != SelectedManager.SUCCESS) {
            return true;
        }
        return false;
    }

    protected boolean isFilterFileSize(LocalMedia localMedia) {
        if (localMedia == null) {
            return true;
        }
        if (selectorConfig.filterMinFileSize > 0 && localMedia.getSize() < selectorConfig.filterMinFileSize) {
            return true;
        }
        if (selectorConfig.filterMaxFileSize > 0 && localMedia.getSize() > selectorConfig.filterMaxFileSize) {
            return true;
        }
        return false;
    }

    protected boolean isFilterVideoDuration(LocalMedia localMedia) {
        if (localMedia == null) {
            return true;
        }
        if (PictureMimeType.isHasVideo(localMedia.getMimeType())) {
            if (selectorConfig.filterVideoMinSecond > 0 && localMedia.getDuration() < selectorConfig.filterVideoMinSecond) {
                return true;
            }
            if (selectorConfig.filterVideoMaxSecond > 0 && localMedia.getDuration() > selectorConfig.filterVideoMaxSecond) {
                return true;
            }
        }
        return false;
    }

    protected boolean isFilterQuery(LocalMedia localMedia) {
        if (localMedia == null) {
            return true;
        }
        if (selectorConfig.onQueryFilterListener != null && selectorConfig.onQueryFilterListener.onFilter(localMedia)) {
            return true;
        }
        return false;
    }

    private ActivityResultContracts.PickVisualMedia.VisualMediaType getVisualMediaType() {
        if (isCustomMimeType()) {//这里设置全部，重新实现 createIntent 方法传递自定义的 mimeType
            return new ActivityResultContracts.PickVisualMedia.SingleMimeType("*/*");
        } else if (selectorConfig.chooseMode == SelectMimeType.ofImage()) {
            return ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        } else if (selectorConfig.chooseMode == SelectMimeType.ofVideo()) {
            return ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE;
        } else {
            return ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE;
        }
    }

    private String[] getCustomMimeTypes() {
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add(PictureMimeType.ofJPEG());
        mimeTypes.add(PictureMimeType.ofPNG());
        if (selectorConfig.isGif) {
            mimeTypes.add(PictureMimeType.ofGIF());
        }
        if (selectorConfig.isBmp) {
            mimeTypes.add(PictureMimeType.ofBMP());
            mimeTypes.add(PictureMimeType.ofXmsBMP());
            mimeTypes.add(PictureMimeType.ofWapBMP());
        }
        if (selectorConfig.isWebp) {
            mimeTypes.add(PictureMimeType.ofWEBP());
        }
        if (selectorConfig.isHeic) {
            mimeTypes.add(PictureMimeType.ofHeic());
        }
        if (selectorConfig.chooseMode == SelectMimeType.ofAll()) {
            mimeTypes.add(SelectMimeType.SYSTEM_VIDEO);
        }
        return mimeTypes.toArray(new String[0]);
    }

    private boolean isCustomMimeType() {
        boolean isImage = selectorConfig.chooseMode == SelectMimeType.ofImage() || selectorConfig.chooseMode == SelectMimeType.ofAll();
        boolean isFilter = !selectorConfig.isGif || !selectorConfig.isBmp || !selectorConfig.isWebp || !selectorConfig.isHeic;
        return isImage && isFilter;
    }

}

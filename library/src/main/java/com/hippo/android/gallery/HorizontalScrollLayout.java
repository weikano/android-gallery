/*
 * Copyright 2017 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.android.gallery;

/*
 * Created by Hippo on 2017/11/10.
 */

import android.view.View;
import java.util.List;

// offset = target - base
public class HorizontalScrollLayout extends BaseScrollLayout {

  private int totalLeft;
  private int totalRight;

  protected void measurePage(View view) {
    float scale = isScalable(view) ? this.scale : 1.0f;
    int widthMeasureSpec = getPageMeasureSpec((int) (width * scale), view.getLayoutParams().width);
    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (height * scale), View.MeasureSpec.EXACTLY);
    view.measure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  public void layoutAnchor(View page, int offset) {
    measurePage(page);

    int deviate = isScalable(page) ? this.deviate : 0;
    int bottom = deviate + page.getMeasuredHeight();
    int right = offset + page.getMeasuredWidth();
    layout(page, offset, deviate, right, bottom);

    totalLeft = offset;
    totalRight = right;
  }

  @Override
  public boolean canLayoutNext(View last, int offset) {
    return last.getLeft() < width + offset;
  }

  @Override
  public void layoutNext(View page) {
    measurePage(page);

    int deviate = isScalable(page) ? this.deviate : 0;
    int bottom = deviate + page.getMeasuredHeight();
    int left = totalRight + interval;
    int right = left + page.getMeasuredWidth();
    layout(page, left, deviate, right, bottom);

    totalRight = right;
  }

  @Override
  public int getNextBlank(View last) {
    return Math.min(0, last.getRight() - width);
  }

  @Override
  public boolean canLayoutPrevious(View first, int offset) {
    return first.getRight() > offset;
  }

  @Override
  public void layoutPrevious(View page) {
    measurePage(page);

    int deviate = isScalable(page) ? this.deviate : 0;
    int bottom = deviate + page.getMeasuredHeight();
    int right = totalLeft - interval;
    int left = right - page.getMeasuredWidth();
    layout(page, left, deviate, right, bottom);

    totalLeft = left;
  }

  @Override
  public int getPreviousOffset(View first) {
    return first.getLeft();
  }

  @Override
  public void offsetPages(List<GalleryView.Page> pages, int offset) {
    for (GalleryView.Page page : pages) {
      page.view.offsetLeftAndRight(offset);
    }

    totalLeft += offset;
    totalRight += offset;
  }

  @Override
  public boolean canBeAnchor(View page) {
    return Math.max(0, page.getLeft()) < Math.min(width, page.getRight());
  }

  @Override
  public int getAnchorOffset(View anchor) {
    return anchor.getLeft();
  }

  @Override
  public void resetLayoutState(View view) {
    totalLeft = view.getLeft();
    totalRight = view.getRight();
  }

  @Override
  public void scrollBy(int dx, int dy, int[] result) {
    result[0] = -dx;
    result[1] = -dy;
  }

  @Override
  public void scaleBy(int anchorOffset, int pageDeviate, int x, int y, float factor, int[] result) {
    result[0] = x - (int) ((x - anchorOffset) * factor);
    result[1] = y - (int) ((y - pageDeviate) * factor);
  }
}

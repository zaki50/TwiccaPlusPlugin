/*
 * Copyright 2011 YAMAZAKI Makoto<makoto1975@gmail.com>
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

package org.zakky.twicca.plus;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class TwiccaPlusPluginActivity extends Activity {
    private static final int REQ_PLUS = 1;

    private static final int REQ_INSTALL = 2;

    private static final String PACKAGE_NAME = "com.google.android.apps.plus";

    // 投稿に使う Activity の名前。古い順。 ACTION_SEND で text/plain を受け取るものを手がかりに探した

    private static final String OLD_ACTIVITY_CLASS_NAME = "com.google.android.apps.plusone.app.ComposeUpdateActivity";

    private static final String OLD_ACTIVITY_CLASS_NAME2 = PACKAGE_NAME + ".phone.PostActivity";

    // Post するための Activity の名前。今のところタブレットでも phone らしい
    private static final String ACTIVITY_CLASS_NAME_FOR_PHONE = PACKAGE_NAME + ".phone.SignOnActivity";

    // Google+ 4.5.0.72928916 以降での投稿の入り口
    private static final String ACTIVITY_CLASS_NAME_FOR_GATEWAY = "com.google.android.libraries.social.gateway.GatewayActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Intent initiatorIntent = getIntent();
        if (initiatorIntent == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final String text = initiatorIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (text == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        // まずは、新しい G+ アクティビティが持っている Activity から順にためしていく

        intent.setClassName(PACKAGE_NAME, ACTIVITY_CLASS_NAME_FOR_GATEWAY);
        if (startActivityForResult(this, intent, REQ_PLUS)) {
            return;
        }

        intent.setClassName(PACKAGE_NAME, ACTIVITY_CLASS_NAME_FOR_PHONE);
        if (startActivityForResult(this, intent, REQ_PLUS)) {
            return;
        }

        // ダメだった場合は 以前のバージョンの Activity を試す。
        intent.setClassName(PACKAGE_NAME, OLD_ACTIVITY_CLASS_NAME2);
        if (startActivityForResult(this, intent, REQ_PLUS)) {
            return;
        }
        intent.setClassName(PACKAGE_NAME, OLD_ACTIVITY_CLASS_NAME);
        if (startActivityForResult(this, intent, REQ_PLUS)) {
            return;
        }

        // 見つからなかったのでマーケットへ飛ばす
        Toast.makeText(this, R.string.gplus_app_not_found, Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse("market://search?q=" + PACKAGE_NAME);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
        marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(marketIntent, REQ_INSTALL);
    }

    /**
     * {@link Activity#startActivityForResult(Intent, int)} を実行し、
     * インテントをうけとる Activity が存在したかどうかを返り値として返します。
     *
     * @param activity startActivityForResult を実行するアクティビティ。
     * @param intent 投げるインテント。
     * @param requestCode resultを受け取る際のリクエストコード。
     * @return 受け取るアクティビティが∃した場合は {@code true} を、存在せずに
     * {@link ActivityNotFoundException} がスローされた場合は {@code false}を返します。
     */
    private static boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
            return true;
        } catch (SecurityException | ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}

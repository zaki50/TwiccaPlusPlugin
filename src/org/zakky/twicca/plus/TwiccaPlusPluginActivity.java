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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class TwiccaPlusPluginActivity extends Activity {
    private static final int REQ_PLUS = 1;

    private static final int REQ_INSTALL = 2;

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

        try {
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, text);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plusone.app.ComposeUpdateActivity");
            startActivityForResult(intent, REQ_PLUS);
        } catch (Exception e) {
            Uri uri = Uri.parse("market://search?q=com.google.android.apps.plus");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
            startActivityForResult(marketIntent, REQ_INSTALL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}

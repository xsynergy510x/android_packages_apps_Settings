/*
 * Copyright (C) 2015 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.ListPreference;
import android.provider.Settings;

import android.provider.SearchIndexableResource;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cyanogenmod.qs.QSTiles;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import java.util.ArrayList;
import java.util.List;

public class NotificationDrawerSettings extends SettingsPreferenceFragment 
    implements Indexable, OnPreferenceChangeListener {
    private Preference mQSTiles;

    private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
    ListPreference mSmartPulldown;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.notification_drawer_settings);

        mQSTiles = findPreference("qs_order");

        // Smart Pulldown   
        mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
        mSmartPulldown.setOnPreferenceChangeListener(this); 
        int smartPulldown = Settings.System.getInt(getContentResolver(),    
                Settings.System.QS_SMART_PULLDOWN, 0);  
        mSmartPulldown.setValue(String.valueOf(smartPulldown)); 
        updateSmartPulldownSummary(smartPulldown);
    }

    @Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSmartPulldown) {  
            int smartPulldown = Integer.valueOf((String) newValue); 
            Settings.System.putInt(getContentResolver(), Settings.System.QS_SMART_PULLDOWN, 
                    smartPulldown); 
            updateSmartPulldownSummary(smartPulldown);  
            return true;
         }
         return false;
    }

    private void updateSmartPulldownSummary(int value) {    
        Resources res = getResources(); 

        if (value == 0) {   
            // Smart pulldown deactivated   
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));  
        } else {    
            String type = null; 
            switch (value) {    
                case 1: 
                    type = res.getString(R.string.smart_pulldown_dismissable);  
                    break;  
                case 2: 
                    type = res.getString(R.string.smart_pulldown_persistent);   
                    break;  
                default:    
                    type = res.getString(R.string.smart_pulldown_all);  
                    break; 
            }   
            // Remove title capitalized formatting  
            type = type.toLowerCase();  
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));    
        }   
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.notification_drawer_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    return new ArrayList<String>();
                }
            };
}

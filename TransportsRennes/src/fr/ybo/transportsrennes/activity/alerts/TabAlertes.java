/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.ybo.transportsrennes.activity.alerts;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import com.ubikod.capptain.android.sdk.activity.CapptainTabActivity;
import fr.ybo.transportsrennes.R;

public class TabAlertes extends CapptainTabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabalertes);

        Resources res = getResources();
        TabHost tabHost = getTabHost();

        // Create an Intent to launch an Activity for the tab (to be reused)
        Intent intent = new Intent().setClass(this, ListAlerts.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        TabHost.TabSpec spec =
                tabHost.newTabSpec("alertes").setIndicator(getString(R.string.alertes), res.getDrawable(android.R.drawable.ic_dialog_alert))
                        .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        Intent intentTwitter = new Intent().setClass(this, ListTwitter.class);
        TabHost.TabSpec twitterSpec =
                tabHost.newTabSpec("twitter").setIndicator(getString(R.string.twitter), res.getDrawable(R.drawable.ic_menu_twitter))
                        .setContent(intentTwitter);
        tabHost.addTab(twitterSpec);

        tabHost.setCurrentTab(0);
    }
}
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
package fr.ybo.transportsrennes.application;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import com.google.code.geocoder.model.LatLng;
import com.google.code.geocoder.model.LatLngBounds;
import com.ubikod.capptain.android.sdk.CapptainAgentUtils;
import fr.ybo.opentripplanner.client.OpenTripPlannerException;
import fr.ybo.opentripplanner.client.modele.GraphMetadata;
import fr.ybo.transportsrennes.database.TransportsRennesDatabase;
import fr.ybo.transportsrennes.database.modele.AlertBdd;
import fr.ybo.transportsrennes.database.modele.Bounds;
import fr.ybo.transportsrennes.keolis.Keolis;
import fr.ybo.transportsrennes.keolis.modele.bus.Alert;
import fr.ybo.transportsrennes.services.UpdateTimeService;
import fr.ybo.transportsrennes.util.AlarmReceiver;
import fr.ybo.transportsrennes.util.CalculItineraires;
import fr.ybo.transportsrennes.util.ErreurReseau;
import fr.ybo.transportsrennes.util.GeocodeUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de l'application permettant de stocker les attributs globaux à
 * l'application.
 */
public class TransportsRennesApplication extends Application {

    private static GeocodeUtil geocodeUtil;

    public static GeocodeUtil getGeocodeUtil() {
        return geocodeUtil;
    }

    private static TransportsRennesDatabase databaseHelper;

    public static TransportsRennesDatabase getDataBaseHelper() {
        return databaseHelper;
    }

    private boolean isInPrincipalProcess() {
        PackageInfo packageinfo;
        try {
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SERVICES);
        } catch (android.content.pm.PackageManager.NameNotFoundException ex) {
            return false;
        }
        String processName = packageinfo.applicationInfo.processName;

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == android.os.Process.myPid()) {
                return runningAppProcessInfo.processName.equals(processName);
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        if (CapptainAgentUtils.isInDedicatedCapptainProcess(this))
            return;
        super.onCreate();

        databaseHelper = new TransportsRennesDatabase(this);
        if (!isInPrincipalProcess()) {
            return;
        }
        geocodeUtil = new GeocodeUtil(this);
        startService(new Intent(UpdateTimeService.ACTION_UPDATE));
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName("fr.ybo.transportsrennes", ".services.UpdateTimeService"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        final String dateCourante = new SimpleDateFormat("ddMMyyyy").format(new Date());
        Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
        if (boundsBdd != null) {
            if (!dateCourante.equals(boundsBdd.getDate())) {
                getDataBaseHelper().delete(boundsBdd);
            }
        }

        AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
        if (alertBdd != null) {
            if (!dateCourante.equals(alertBdd.getDate())) {
                getDataBaseHelper().delete(alertBdd);
            }
        }

        // Récupération des alertes
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    AlertBdd alertBdd = getDataBaseHelper().selectSingle(new AlertBdd());
                    if (alertBdd == null) {
                        alertBdd = new AlertBdd();
                        alertBdd.setDate(dateCourante);
                        Set<String> lignes = new HashSet<String>();
                        for (Alert alert : Keolis.getInstance().getAlerts()) {
                            lignes.addAll(alert.lines);
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String ligne : lignes) {
                            stringBuilder.append(ligne);
                            stringBuilder.append(',');
                        }
                        alertBdd.setLignes(stringBuilder.toString());
                        getDataBaseHelper().insert(alertBdd);
                    }

                    Collections.addAll(lignesWithAlerts, alertBdd.getLignes().split(","));
                } catch (ErreurReseau ignore) {
                }
                try {
                    Bounds boundsBdd = getDataBaseHelper().selectSingle(new Bounds());
                    if (boundsBdd == null) {
                        GraphMetadata metadata = CalculItineraires.getInstance().getMetadata();
                        if (metadata != null) {
                            boundsBdd = new Bounds();
                            boundsBdd.setDate(dateCourante);
                            boundsBdd.setMinLatitude(metadata.getMinLatitude());
                            boundsBdd.setMaxLatitude(metadata.getMaxLatitude());
                            boundsBdd.setMinLongitude(metadata.getMinLongitude());
                            boundsBdd.setMaxLongitude(metadata.getMaxLongitude());
                            getDataBaseHelper().insert(boundsBdd);
                        }
                    }
                    if (boundsBdd != null) {
                        bounds = new LatLngBounds(new LatLng(new BigDecimal(boundsBdd.getMinLatitude()), new BigDecimal(
                                boundsBdd.getMinLongitude())), new LatLng(new BigDecimal(boundsBdd.getMaxLatitude()),
                                new BigDecimal(boundsBdd.getMaxLongitude())));
                    }
                } catch (OpenTripPlannerException ignore) {
                }
                return null;
            }
        }.execute((Void) null);

        setRecurringAlarm(this);
    }

    private static Set<String> lignesWithAlerts = new HashSet<String>();

    public static boolean hasAlert(String ligneNomCourt) {
        return lignesWithAlerts.contains(ligneNomCourt);
    }

    private static LatLngBounds bounds;

    public static LatLngBounds getBounds() {
        return bounds;
    }

    private static final long INTERVAL_ALARM = AlarmManager.INTERVAL_HALF_DAY;

    private void setRecurringAlarm(Context context) {
        Intent alarm = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringCheck = PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, INTERVAL_ALARM, recurringCheck);
    }

}
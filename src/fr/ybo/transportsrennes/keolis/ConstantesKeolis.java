package fr.ybo.transportsrennes.keolis;

import java.util.ArrayList;
import java.util.List;

import fr.ybo.transportsrennes.keolis.gtfs.modele.Agence;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Arret;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretFavori;
import fr.ybo.transportsrennes.keolis.gtfs.modele.ArretRoute;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Calendrier;
import fr.ybo.transportsrennes.keolis.gtfs.modele.DernierMiseAJour;
import fr.ybo.transportsrennes.keolis.gtfs.modele.HeuresArrets;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Route;
import fr.ybo.transportsrennes.keolis.gtfs.modele.Trip;

public class ConstantesKeolis {

	public static final List<Class<?>> LIST_CLASSES_DATABASE = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_DATABASE.add(Agence.class);
		LIST_CLASSES_DATABASE.add(Arret.class);
		LIST_CLASSES_DATABASE.add(Calendrier.class);
		LIST_CLASSES_DATABASE.add(DernierMiseAJour.class);
		LIST_CLASSES_DATABASE.add(Route.class);
		LIST_CLASSES_DATABASE.add(Trip.class);
		LIST_CLASSES_DATABASE.add(HeuresArrets.class);
		LIST_CLASSES_DATABASE.add(ArretFavori.class);
		LIST_CLASSES_DATABASE.add(ArretRoute.class);
	}

	public static final List<Class<?>> LIST_CLASSES_GTFS = new ArrayList<Class<?>>();

	static {
		LIST_CLASSES_GTFS.add(Agence.class);
		LIST_CLASSES_GTFS.add(Arret.class);
		LIST_CLASSES_GTFS.add(Calendrier.class);
		LIST_CLASSES_GTFS.add(HeuresArrets.class);
		LIST_CLASSES_GTFS.add(Route.class);
		LIST_CLASSES_GTFS.add(Trip.class);
	}

}
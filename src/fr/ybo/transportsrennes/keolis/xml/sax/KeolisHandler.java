package fr.ybo.transportsrennes.keolis.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ybo.transportsrennes.keolis.modele.Answer;
import fr.ybo.transportsrennes.keolis.modele.StatusKeolis;

/**
 * Handler pour les appels � Keolis.
 * 
 * @author ybonnel
 * 
 * @param <ObjetKeolis>
 */
public abstract class KeolisHandler<ObjetKeolis> extends DefaultHandler {

	/**
	 * Nom de la balise answer.
	 */
	private static final String ANSWER = "answer";

	/**
	 * Nom de la balise status.
	 */
	private static final String STATUS = "status";
	/**
	 * Nom de la balise code.
	 */
	private static final String CODE = "code";
	/**
	 * Nom de la balise message.
	 */
	private static final String MESSAGE = "message";

	/**
	 * R�ponse de l'API getdistrict.
	 */
	private Answer<ObjetKeolis> answer = null;

	/**
	 * Objet Keolis courant.
	 */
	private ObjetKeolis currentObjetKeolis;

	/**
	 * StringBuilder servant au parsing xml.
	 */
	private StringBuilder contenu = null;

	@Override
	public final void characters(final char[] ch, final int start, final int length) throws SAXException {
		super.characters(ch, start, length);
		this.contenu.append(ch, start, length);
	}

	@Override
	public final void endElement(final String uri, final String localName, final String name) throws SAXException {
		super.endElement(uri, localName, name);
		if (this.answer != null) {
			if (localName.equals(getBaliseData())) {
				this.answer.getData().add(this.currentObjetKeolis);
			} else {
				remplirObjectKeolis(this.currentObjetKeolis, localName, this.contenu.toString());
			}
			surchargeEndElement(localName);
			this.contenu.setLength(0);
		}
	}

	/**
	 * Getter.
	 * 
	 * @return r�ponse de l'API getdistrict.
	 */
	public final Answer<ObjetKeolis> getAnswer() {
		return this.answer;
	}

	/**
	 * M�thode � impl�menter donnant le nom de la balise englobante.
	 * 
	 * @return le nom de la balise englobante.
	 */
	protected abstract String getBaliseData();

	/**
	 * @return le contenu de la balise courante.
	 */
	protected final StringBuilder getContenu() {
		return this.contenu;
	}

	/**
	 * M�thode � impl�menter cr�ant un nouvel objet Keolis.
	 * 
	 * @return nouvel objet Keolis.
	 */
	protected abstract ObjetKeolis getNewObjetKeolis();

	/**
	 * M�thode � impl�menter remplissant le contenu d'un objet Keolis.
	 * 
	 * @param currentObjectKeolis
	 *            objet Keolis courant.
	 * @param baliseName
	 *            nom de la balise.
	 * @param contenuOfBalise
	 *            contenu de la balise.
	 */
	protected abstract void remplirObjectKeolis(ObjetKeolis currentObjectKeolis, String baliseName, String contenuOfBalise);

	@Override
	public final void startDocument() throws SAXException {
		super.startDocument();
		this.contenu = new StringBuilder();
	}

	@Override
	public final void startElement(final String uri, final String localName, final String name, final Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals(ANSWER)) {
			this.answer = new Answer<ObjetKeolis>();
		} else if (localName.equals(STATUS)) {
			this.answer.setStatus(new StatusKeolis());
			this.answer.getStatus().setCode(attributes.getValue(attributes.getIndex(CODE)));
			this.answer.getStatus().setMessage(attributes.getValue(attributes.getIndex(MESSAGE)));
		} else if (localName.equals(getBaliseData())) {
			this.currentObjetKeolis = getNewObjetKeolis();
		}
		this.contenu.setLength(0);
	}

	/**
	 * M�thode permettant de surcharger le comportement sur endElement.
	 * 
	 * @param localName
	 *            nom de la balise.
	 */
	public void surchargeEndElement(final String localName) {
	}

}
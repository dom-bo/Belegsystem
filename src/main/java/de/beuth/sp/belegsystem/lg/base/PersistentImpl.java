package de.beuth.sp.belegsystem.lg.base;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.tapestry5.beaneditor.NonVisual;
import org.hibernate.annotations.Type;

/**
 * Abstrakte Superklasse für zu persistierende Objekte (entitites).
 * 
 * @id Wir verwenden random generierte UUIDs anstatt von per generierte Ids
 *     (z.b. per Hibernate/DB - Sequence). <br/>
 *     Dadurch können wir schon vor dem erstmaligen Speichern hashCode() und
 *     equals() korrekt ausführen da die UUID bereits bei Instantiierung und
 *     nicht erst beim Speichern vergeben wird.<br/>
 *     Wodurch wir wiederum die neuen Objekte gleich z.B. in einem Set speichern
 *     können. <br/>
 *     Negativ daran ist dass UUIDs etwas mehr Speicher benötigen und JOINs
 *     etwas langsamer sind (da über VARCHAR-values gejoint wird und nciht über
 *     einen Zahlenwert), aber das sollte verschmerzbar sein. <br/>
 *     Siehe auch {@link http
 *     ://de.wikipedia.org/wiki/Universally_unique_identifier}<br/>
 *     PS: Die Wahrscheinlichkeit für ein Duplikat liegt bei 50% wenn jede
 *     Person auf dieser Welt schon 600 Millionen UUIDs besitzen würde.
 * @version Da Hibernate sich nicht mehr um die Id-Generierung kümmert müssen
 *          wir {@link #version} implementieren damit Hibernate festellen kann
 *          ob es sich um ein noch nicht gespeichertes (transient) Objekt
 *          handelt.
 * 
 * 
 */
@MappedSuperclass
public abstract class PersistentImpl implements Persistent {

	private static final long serialVersionUID = -6781498776713390810L;

	@Id
	@Type(type = "org.hibernate.type.UUIDCharType")
	private final UUID id = randomUUID();

	@Version
	private Integer version;

	@Override
	@NonVisual
	public UUID getId() {
		return id;
	}

	@Override
	@NonVisual
	public Integer getVersion() {
		return version;
	}

	/**
	 * @return True if o.id = this.id
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof PersistentImpl) {
			final PersistentImpl other = (PersistentImpl) obj;
			return (id == other.id) || (id != null && id.equals(other.id));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	/**
	 * A meaningful name for entities.
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + id + "]";
	}
}
package de.beuth.sp.belegsystem.lg.base;

import java.io.Serializable;
import java.util.UUID;

/**
 * Interface for persistent entities. Every persistent element should implement
 * this interface.
 * 
 * 
 */
public interface Persistent extends Serializable {

	public UUID getId();

	public Integer getVersion();

}

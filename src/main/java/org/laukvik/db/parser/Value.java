package org.laukvik.db.parser;

public abstract class Value {
	
	public Value( String value ){
	}
	
	public abstract boolean equals( Value value );
	public abstract boolean greater( Value value );
	public abstract boolean less( Value value );

	public abstract String toString();
	
}